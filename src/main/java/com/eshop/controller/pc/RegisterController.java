package com.eshop.controller.pc;

import com.eshop.event.EventEnum;
import com.eshop.event.param.CustomerParam;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.MD5Helper;
import com.eshop.helper.SMSHelper;
import com.eshop.helper.ValidateCode;
import com.eshop.membership.CustomerGrowthService;
import com.eshop.membership.CustomerPointService;
import com.eshop.membership.MemberShip;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.EventRole;
import com.eshop.service.Member;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Record;

/**
 * 用户注册控制器
 *   @author TangYiFeng
 */
public class RegisterController extends PcBaseController {
	protected static EventRole eventRole = EventRole.instance();
	
    /**
     * Default constructor
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
    	if(!this.validateRequiredString("mobilePhone")) {
    		return;
    	}
    	if(!this.validatePhone("mobilePhone")){
    		return;
    	}
    	String mobilePhone = getPara("mobilePhone");
    	
    	if(!this.validateRequiredString("password")) {
    		return;
    	}
    	if(!this.validateStringLength("password", 8, 50)){
    		return;
    	}
    	if(!this.validatePassword("password")) {
    		return;
    	}
    	String password = getPara("password");
    	
    	if (!this.validateRequiredString("code")) {
    		return;
    	}
    	if(!this.validateStringLength("code", 4, 10)){
    		return;
    	}
    	String code = getPara("code");
    	
    	if(!this.validateRequiredString("passworded")) {
    		return;
    	}
    	if(!this.validateStringLength("passworded", 8, 50)){
    		return;
    	}
    	String passworded = getPara("passworded");
    	
    	if (!this.validateRequiredString("codeToken")) {
    		return;
    	}
    	String codeToken = getPara("codeToken");
    	String phone = getPara("phone");
    	
    	if(!phone.equals(mobilePhone)){
    		returnError(-5, "手机号码已篡改");
    		return;
    	}
    	
    	if (Member.hasCode(codeToken, code) != ServiceCode.Success) {
    		returnError(-3, "验证码不正确");
    		return;
    	}
    	
    	int result = Member.registerWithPhone(mobilePhone, password, passworded);
    	
    	if(result == 2){    
    		returnError(-1, "密码不一致");
    		return;
    	}
    	
    	if(result == 3){    
    		returnError(-2, "该手机号已被注册");
    		return;
    	}
    	
    	if(result == 4){    
    		returnError(-4, "注册失败");
    		return;
    	}
    	
    	Customer cs = Customer.dao.findFirst("select * from customer where mobilePhone = ?", mobilePhone);
    	
    	//注册成功 +积分
    	CustomerParam customerParam = new CustomerParam();
    	customerParam.setCustomer(cs);
    	eventRole.dispatch(EventEnum.EVENT_REGISTER, customerParam);
//    	CustomerPointService.updatePoint(cs.getId(), 1, MemberShip.REGISTER_POINT, "注册");
    	//注册成功 +成长值
//    	CustomerGrowthService.updateGrowth(cs.getId(), 1, MemberShip.REGISTER_GROWTH, "注册");
    	
    	renderJson(jsonObject);
    }

    /**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    public void getCode() {
    	String[] params = {"phone", "vcode", "vcodeToken"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String vcode = getPara("vcode");
    	String vcodeToken = getPara("vcodeToken");
    	if (!ValidateCode.checkVcode(vcode, vcodeToken)) {
    		returnError(-3, "图形验证码错误");
    		return;
    	}
    	
    	String phone = getPara("phone");
    	int phoneresult = Member.registerWithPhone(phone, null, null);
    	if(phoneresult == 3){    
    		returnError(-2, "该手机号已被注册");
    		return;
    	}
    	Record result = SMSHelper.sendCode(phone);
    	CacheHelper.remove(vcodeToken);
  
    	
    	jsonObject.put("phone", result.get("phone"));
    	jsonObject.put("codeToken", result.get("codeToken"));
//    	jsonObject.put("code", result.get("code"));
    	jsonObject.put("error", result.get("error"));
    	renderJson(jsonObject);
    }

   /**
    * 邮箱注册
    * @param email
    * @param password
    * @param passworded
    * @return 成功：{error:0,error:-1(密码不一致),error:-2(该邮箱已被注册),error:-3(还没被激活),error:-4(已被禁用)} 失败：{error:>0,errmsg:错误信息}
    */
    public void verifedEmail() {
    	if(!this.validateRequiredString("email")) {
    		return;
    	}
    	String email = getPara("email");
    	
    	if(!this.validateRequiredString("password")) {
    		return;
    	}
    	String password = getPara("password");
    	
    	if(!this.validateRequiredString("passworded")) {
    		return;
    	}
    	String passworded = getPara("passworded");
    	
    	if(!password.equals(passworded)){    
    		returnError(-1, "密码不一致");
    		return;
    	}
    	
    	Customer customer = Member.getCustomerByEmail(email);
    	
    	if(customer != null) {
    		int disable = customer.getDisable();
    		
    		if (disable == 0) {
    			returnError(-2, "该账户已被注册，无需再注册");
        		return;
    		}
    		
    		if (disable == 1) {
    			returnError(-4, "该账户已被禁用");
        		return;
    		}
    		
    		if (disable == 2) {
    			Member.sendEmailWithActiveAccount(email, customer.getPassword());
    			returnError(-3, "该账户还没激活，请到邮箱激活");
    			return;
    		}
    	}
    	
    	ServiceCode code = Member.registerWithEmail(email, password, passworded);
    	
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "注册失败");
			return;
		}
		
		Customer cs = Customer.dao.findFirst("select * from customer where email = ?", email);
		
		//注册成功 +积分
		CustomerParam customerParam = new CustomerParam();
    	customerParam.setCustomer(cs);
    	eventRole.dispatch(EventEnum.EVENT_REGISTER, customerParam);
//    	CustomerPointService.updatePoint(cs.getId(), 1, MemberShip.REGISTER_POINT, "注册");
//    	//注册成功 +成长值
//    	CustomerGrowthService.updateGrowth(cs.getId(), 1, MemberShip.REGISTER_GROWTH, "注册");
    	
		Member.sendEmailWithActiveAccount(email, MD5Helper.Encode(password));
    	renderJson(jsonObject);
    }
    
    /**
     * 激活账号
     * @email
     * @activityCode 激活码
     * @return 成功：{error:0} 失败:{error:>0,errmsg:错误信息}
     */
    public void activite() {
    	String failUrl = PropKit.get("frontHostName") + PropKit.get("emailRegisterFailUrl");
    	String successUrl = PropKit.get("frontHostName") + PropKit.get("emailRegisterSuccessUrl");

    	if (!this.validateRequiredString("email")) {
    		redirect(failUrl);
    		return;
    	}
    	
    	if (!this.validateRequiredString("activityCode")) {
    		redirect(failUrl);
    		return;
    	}
    	
    	String email = getPara("email");
    	String activityCode = getPara("activityCode");
    	
    	ServiceCode code = Member.activate(email, activityCode);
    	if (code != ServiceCode.Success) {
    		redirect(failUrl);
    		return;
    	}
    	
    	redirect(successUrl); 
    }
    
    /**
     * 检查手机验证码
     * @param code 手机验证码(必填)
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    public void validateCode() {
    	if (!this.validateRequiredString("code")) {
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
}