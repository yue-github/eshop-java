package com.eshop.controller.pc;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.eshop.controller.admin.BaseController.ErrorCode;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.IPHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.helper.TokenHelper;
import com.eshop.helper.ValidateCode;
import com.eshop.helper.WxLoginPCHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.login.LoginService;
import com.eshop.model.Customer;
import com.eshop.model.User;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.permission.UserService;
import com.eshop.service.Member;
import com.eshop.service.OperationLogService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.oauth.Oauth;

/**
 * 用户登录控制器
 *   @author TangYiFeng
 */
public class LoginController extends PcBaseController {
	
	private LoginService loginService;

    /**
     * Default constructor
     */
    public LoginController() {
    	loginService = new LoginService();
    }

    /**
     * 会员登录
     * @param phone:电话号码  password:密码
     * @return 成功：{error: 0, error:-1(该账户被禁用), error:-2(该账户未激活), customer:{id:id,name:会员姓名,mobilePhone:手机号码,nickName:昵称}, token:token值}  失败：{error: >0, errmsg: 错误信息}
     */
    public void submit(){
    	String[] params = {"phone", "password", "vcode", "vcodeToken"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String vcode = getPara("vcode");
    	String vcodeToken = getPara("vcodeToken");
//    	if (!ValidateCode.checkVcode(vcode, vcodeToken)) {
//    		returnError(-3, "图形验证码错误");
//    		return;
//    	}
    	
    	if(!this.validateStringLength("phone", 8, 50)){
    		return;
    	}
    	
    	if(!this.validateStringLength("password", 8, 50)){
    		return;
    	}
    	
    	String phone = getPara("phone");
    	String password = getPara("password");
    	
    	Record result = loginService.login(phone, password);
    	int error = result.getInt("error");
    	String errmsg = result.getStr("errmsg");
    	
    	if (error != 0) {
    		returnError(error, errmsg);
    		return;
    	}
    	
    	Customer customer = (Customer) result.get("customer");
    	String token = TokenHelper.create();
		CacheHelper.put(token, customer);
		CacheHelper.remove(customer.getId().toString());
		CacheHelper.put(customer.getId().toString(), token);
		
		jsonObject.put("customer", customer);
		jsonObject.put("token", token);
		setAttr("token", token);
    	renderJson(jsonObject);
    }
    

    /**
     * 手机号登录
     */
    public void phoneLogin() {
    	String[] params = {"phone", "code"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String phone = getPara("phone"); 
    	
    	if(!this.validateStringLength("code", 4, 10)){
    		return;
    	}
    	String code = getPara("code");
    	
    	if (!this.validateRequiredString("codeToken")) {
    		return;
    	}
    	String codeToken = getPara("codeToken");
    	
    	if (Member.hasCode(codeToken, code) != ServiceCode.Success) {
    		returnError(-3, "验证码不正确");
    		return;
    	}
    	
    	Record result = loginService.login(phone);
    	int error = result.getInt("error");
    	String errmsg = result.getStr("errmsg");
    	
    	if (error != 0) {
    		returnError(error, errmsg);
    		return;
    	}
    	
    	Customer customer = (Customer) result.get("customer");
    	String token = TokenHelper.create();
		CacheHelper.put(token, customer);
		CacheHelper.remove(customer.getId().toString());
		CacheHelper.put(customer.getId().toString(), token);
		
		jsonObject.put("customer", customer);
		jsonObject.put("token", token);
		setAttr("token", token);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    public void getCode() {
    	String[] params = {"phone"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String phone = getPara("phone");
    	Record result = SMSHelper.sendCode(phone);
    	
    	jsonObject.put("phone", result.get("phone"));
    	jsonObject.put("codeToken", result.get("codeToken"));
//    	jsonObject.put("code", result.get("code"));
    	jsonObject.put("error", result.get("error"));
    	renderJson(jsonObject);
    }
    
    /**
     * 自动登陆
     * @return 成功：{error: 0, token:token值}  失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void autoLogin(){
    	Customer customer = Member.getFirstCustomer();
    	String token = TokenHelper.create();
		CacheHelper.put(token, customer);
		
		jsonObject.put("customer", customer);
		jsonObject.put("token", token);
		setAttr("token", token);
    	renderJson(jsonObject);
    }
    
    /**
     * 转向微信登录 
     */
    public void weixinLogin() {
    	String redirectUrl = "http://service.eebin.com/" + "pc/login/wxToken";
    	
    	String url = WxLoginPCHelper.getRequestCodeUrl(redirectUrl);
    	jsonObject.put("url", url);
		renderJson(jsonObject);
    }
    
    /**
     * 转向QQ登录
     * @throws QQConnectException
     */
    public void qqLogin() {
    	try {
    		String url = new Oauth().getAuthorizeURL(this.getRequest());
    		jsonObject.put("url", url);
    		renderJson(jsonObject);
    		
    		return;
        } catch (QQConnectException e) {
            e.printStackTrace();
            returnError(ErrorCode.Exception, "get url of qqlogin failed");
            return;
        }
    }

    
    /**
     * 获取qq登录token
     */
    public void qqToken() {
    	try {
    		HttpServletRequest request = this.getRequest();
    		AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(this.getRequest());
    		
    		String accessToken   = null,
                    openID        = null;
             long tokenExpireIn = 0L;

             if (accessTokenObj.getAccessToken().equals("")) {
            	 returnError(ErrorCode.Exception, "没有获取到响应参数");
            	 return;
             } else {
                 accessToken = accessTokenObj.getAccessToken();
                 tokenExpireIn = accessTokenObj.getExpireIn();

                 request.getSession().setAttribute("demo_access_token", accessToken);
                 request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                 OpenID openIDObj =  new OpenID(accessToken);
                 openID = openIDObj.getUserOpenID();            //openid 

                 request.getSession().setAttribute("demo_openid", openID);
                 //UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                 //UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                 //String nickname = userInfoBean.getNickname();  //昵称 
                 //String gender = userInfoBean.getGender();      //姓别 
             }
             
             renderJson(jsonObject);
    		
    		return;
        } catch (QQConnectException e) {
            e.printStackTrace();
            returnError(ErrorCode.Exception, "qqlogin failed");
            return;
        }
    	
    }

    /**
     * 获取微信登录token
     * @param code
     */
    public void wxToken() {
    	String code = getPara("code");
    	
    	WxLoginPCHelper wxLoginPCHelper = new WxLoginPCHelper();
    	Map<String, String> map = wxLoginPCHelper.getUserInfoAccessToken(code);
    	
    	String accessToken = map.get("access_token");
    	String openId = map.get("openid");
    	
    	Map<String, String> userInfo = wxLoginPCHelper.getUserInfo(accessToken, openId);
    	if (userInfo == null) {
    		returnError(1, "登录失败");
    		return;
    	}
    	
    	String nickName = userInfo.get("nickname");
    	String headImg = userInfo.get("headimgurl");
    	String unionid = userInfo.get("unionid");
    	
    	Customer model = Member.loginByUnionid(unionid, nickName, headImg);
    	String token = TokenHelper.create();
    	CacheHelper.put(token, model);
    	String customerStr = JSON.toJSONString(model);
    	
    	String redirectUrl = this.getHostName() + "/loyee-eshop-pc/index.html#/home" + "?customer=" + customerStr + "&token=" + token;
    	redirect(redirectUrl);
    }

}