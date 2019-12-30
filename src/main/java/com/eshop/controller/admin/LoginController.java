package com.eshop.controller.admin;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.IPHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.helper.TokenHelper;
import com.eshop.helper.ValidateCode;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.User;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.permission.UserService;
import com.eshop.service.Member;
import com.eshop.service.OperationLogService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 权限管理
 *   @author TangYiFeng
 */
public class LoginController extends AdminBaseController {

    /**
     * Default constructor
     */
    public LoginController() {
    }
    
    /**
     * 管理员登录
     * @param userName 用户名 
     * @param password 密码
     * @return 成功：{error: 0, adminToken:token值, userName:用户名}
     */
    public void token(){
    	String[] params = {"userName", "password", "vcode", "vcodeToken"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String vcode = getPara("vcode");
    	String vcodeToken = getPara("vcodeToken");
//    	if (!ValidateCode.checkVcode(vcode, vcodeToken)) {
//    		returnError(-3, "图形验证码错误");
//    		return;
//    	}
    	
    	String userName = getPara("userName"); 
    	String password = getPara("password");
    	User user = UserService.login(userName, password);
    	if(user == null){
    		returnError(ErrorCode.Exception, "登陆失败");
    		return;
    	}
    	
    	String token = TokenHelper.create();
		CacheHelper.put(token, user);
		CacheHelper.remove(user.getId().toString());
		CacheHelper.put(user.getId().toString(), token);
		
		jsonObject.put("adminToken", token);
		jsonObject.put("userName", user.getUserName());
		setAttr("adminToken", token);
		
		OperationLogService service = new OperationLogService();
		service.create(user, IPHelper.getIpAddr(this.getRequest()), "登录", "", "登录");
		 
    	renderJson(jsonObject);
    }
    
    /**
     * 退出登录
     * @param adminToken 必填
     * @return 成功：{error: 0}
     */
    @Before(AdminAuthInterceptor.class)
    public void logout() {
    	String token = getPara("adminToken");
    	CacheHelper.remove(token);
    	
    	User user = (User) CacheHelper.get(token);
    	OperationLogService service = new OperationLogService();
		service.create(user, IPHelper.getIpAddr(this.getRequest()), "退出", "", "登录");
		
    	renderJson(jsonObject);
    }
    
    /**
     * 手机号登录
     */
    public void phoneLogin() {
    	String[] params = {"userName", "code"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String phone = getPara("userName"); 
    	
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
    	
    	User user = UserService.login(phone);
    	
    	if(user == null){
    		returnError(ErrorCode.Exception, "登陆失败");
    		return;
    	}
    	
    	String token = TokenHelper.create();
		CacheHelper.put(token, user);
		CacheHelper.remove(user.getId().toString());
		CacheHelper.put(user.getId().toString(), token);
		
		jsonObject.put("adminToken", token);
		jsonObject.put("userName", user.getUserName());
		setAttr("adminToken", token);
		
		OperationLogService service = new OperationLogService();
		service.create(user, IPHelper.getIpAddr(this.getRequest()), "登录", "", "登录");
		 
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
    	jsonObject.put("error", result.get("error"));
    	renderJson(jsonObject);
    }

}