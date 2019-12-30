package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.helper.TokenHelper;
import com.eshop.login.LoginService;
import com.eshop.model.Customer;
import com.jfinal.plugin.activerecord.Record;

/**
 * 用户登录控制器
 * @author TangYiFeng
 */
public class LoginController extends WebappBaseController {
	
	private LoginService loginService;
	
	public LoginController() {
		loginService = new LoginService();
	}

    /**
     * 会员登录
     * @param phone:电话号码  
     * @param password:密码
     * @return 成功：{error: 0, customer:{会员信息}}  失败：{error: >0, errmsg: 错误信息}
     */
    public void submit(){
    	String[] params = {"phone", "password"};
    	if (!this.validate(params)) {
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
    	
    	if (result.getInt("error") != 0 ) {
    		returnError(ErrorCode.Exception, "发送短信失败");
    		return;
    	}
    	
    	jsonObject.put("codeToken", result.get("codeToken"));
    	renderJson(jsonObject);
    }

}