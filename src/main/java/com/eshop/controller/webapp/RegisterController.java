package com.eshop.controller.webapp;

import com.eshop.helper.SMSHelper;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.plugin.activerecord.Record;

/**
 * 注册控制器
 * @author TangYiFeng
 */
public class RegisterController extends WebappBaseController {

    /**
     * 构造方法
     */
    public RegisterController() {
    }

    /**
     * 手机号码注册
     * @param mobilePhone 手机号
     * @param password 密码
     * @param passworded 确认密码
     * @param code 验证码
     * @param codeToken 验证码token值
     * @return 成功：{error: 0,error: -1 两次密码不一致 ,error: -2 该账户已存在,error:-3 验证码不存在}；失败：{error: >0, errmsg: 错误信息}
     */
    public void register() {
    	String[] params = {"mobilePhone", "password", "code", "passworded", "codeToken"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String mobilePhone = getPara("mobilePhone");
    	String password = getPara("password");
    	String code = getPara("code");
    	String passworded = getPara("passworded");
    	String codeToken = getPara("codeToken");
    	
    	//判断验证码是否正确
    	if (Member.hasCode(codeToken, code) != ServiceCode.Success) {
    		setError(-3, "验证码不正确");
    		return;
    	}
    	
    	//判断密码是否一致
    	if (!password.equals(passworded)) {    
    		setError(-1, "密码不一致");
    		return;
    	}
    	
    	Customer model = new Customer();
    	model.setMobilePhone(mobilePhone);
    	model.setPassword(password);
    	
    	int result = Member.registerWithPhone(mobilePhone, password, passworded);
    	
    	//是否已经注册
    	if (result == 3) {    
    		setError(-2, "该手机号码已注册");
    		return;
    	}
    	
    	if (result == 4) {
    		setError(ErrorCode.Exception, "注册失败");
    	}
    	
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
    	
    	if (result.getInt("error") != 0) {
    		returnError(ErrorCode.Exception, result.getStr("errmsg"));
    		return;
    	}
    	
    	jsonObject.put("codeToken", result.get("codeToken"));
    	renderJson(jsonObject);
    }

}