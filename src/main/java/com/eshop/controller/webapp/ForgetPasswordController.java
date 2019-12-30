package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.plugin.activerecord.Record;

/**
 * 找回密码控制器
 * @author TangYiFeng
 */
public class ForgetPasswordController extends WebappBaseController {

    /**
     * 构造方法
     */
    public ForgetPasswordController() {
    }

    /**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    public void getCode() {
    	if (!this.validateRequiredString("phone")) {
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

    /**
     * 根据手机号码和验证码找回密码
     * @param phone
     * @param code
     * @param codeToken
     * @return 成功：{error: 0,error: -1 验证码不正确 ,error: -2 该账户不存在,data:{id:会员id}}；失败：{error: >0, errmsg: 错误信息}
     */
    public void nextByPhone() {
    	String[] params = {"phone", "code", "codeToken"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String phone = getPara("phone");
    	String code = getPara("code");
    	String codeToken = getPara("codeToken");
    	
    	ServiceCode sCode = Member.hasCode(codeToken, code);
    	
    	//判断验证码是否正确
    	if (sCode != ServiceCode.Success) {
    		returnError(-1, "手机验证码不正确");
    		return;
    	}
    	
    	//判断账号是否存在
    	Customer customer = Member.getCustomerByMobilePhone(phone);
    	if (customer == null) {
    		returnError(-2, "该用户不存在");
    		return;
    	}
    	
    	jsonObject.put("data", customer);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改密码
     * @param token 登录口令
     * @param customerId
     * @param password
     * @param repassword
     * @return 成功：{error: 0,error: -1 密码不一致,error:-2(验证码不正确)}；失败：{error: >0, errmsg: 错误信息}
     */
    public void updatePassword() {
    	String[] params = {"token", "password", "repassword"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String password = getPara("password");
    	String repassword = getPara("repassword");
    	
    	if (!password.equals(repassword)) {
    		setError(-1, "密码不一致");
    		return;
    	}
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	Customer cus = Member.getCustomer(customer.getId());
    	if (cus == null) {
    		returnError(-3, "该用户不存在");
    		return;
    	}
    	
    	ServiceCode rcode = Member.setPassword(customer.getId(), password, repassword);
    	
    	if (rcode != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "修改密码失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }

}