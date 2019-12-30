package com.eshop.controller.pc;

import java.security.PrivateKey;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.Service;
import javax.servlet.http.HttpSession;

import com.eshop.helper.*;
import com.eshop.log.Log;
import com.eshop.model.Customer;
import com.eshop.model.Step;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

/**
 * 我的银行卡控制器
 *   @author TangYiFeng
 */
public class FindPasswordController extends PcBaseController {

    /**
     * Default constructor
     */
    public FindPasswordController() {
    }
//	private final  static  	Step step = new Step();
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
    	Record result = SMSHelper.sendCode(phone);
    	CacheHelper.remove(vcodeToken);
    	
    	jsonObject.put("codeToken", result.get("codeToken"));
//    	jsonObject.put("code", result.get("code"));
    	jsonObject.put("error", result.get("error"));
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
    	int phone_code_count = getSessionAttr("phone_code_count") != null ? (Integer) getSessionAttr("phone_code_count") : 0;
    	System.out.println("phone_code_count="+phone_code_count);
    	if (phone_code_count > 5) {
    		long now = new Date().getTime();
    		long last_time = getSessionAttr("phone_code_time");
    		int five_minutes = 5 * 60 * 1000;
    		//System.out.println("time="+(now-last_time));
    		if ((now - last_time) < five_minutes) {
    			returnError(-3, "验证码尝试次数不能超过5次");
    			return;
    		} else {
    			removeSessionAttr("phone_code_count");
    			removeSessionAttr("phone_code_time");
    		}
    	}
    	
    	if (!this.validateRequiredString("phone")) {
    		return;
    	}
    	String phone = getPara("phone");
    	if (!this.validateRequiredString("code")) {
    		return;
    	}
    	String code = getPara("code");
    	
    	if (!this.validateRequiredString("codeToken")) {
    		return;
    	}
    	String codeToken = getPara("codeToken");
    	
    	ServiceCode sCode = Member.hasCode(codeToken, code);
    	
    	//判断验证码是否正确
    	if (sCode != ServiceCode.Success) {

    		Integer phone_code_count2 = getSessionAttr("phone_code_count") != null
					? (Integer) getSessionAttr("phone_code_count") : 0;
    		setSessionAttr("phone_code_count", phone_code_count2 + 1);
    		setSessionAttr("phone_code_time", new Date().getTime());
    		jsonObject.put("error", -1);
    		renderJson(jsonObject);
    		return;
    	}
    	
    	//判断账号是否存在
    	Customer customer = Member.getCustomerByMobilePhone(phone);
    	if (customer == null) {
    		jsonObject.put("error", -2);
    		renderJson(jsonObject);
    		return;
    	}
    	
    	// 删除session
    	if (getSessionAttr("phone_code_count") != null) {
    		removeSessionAttr("phone_code_count");
    		removeSessionAttr("phone_code_time");
    	}

    	CacheHelper.put("phone",phone);
		CacheHelper.put("Flag",2);
    	jsonObject.put("data", customer);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改密码
     * @param customerId
     * @param password
     * @param repassword
     * @return 成功：{error: 0,error: -1 密码不一致 }；失败：{error: >0, errmsg: 错误信息}
     */
    public void updatePassword() {
    	if (!this.validateRequiredString("password")) {
    		return;
    	}
    	String password = getPara("password");
    	
    	if (!this.validateRequiredString("repassword")) {
    		return;
    	}
    	String repassword = getPara("repassword");

		//手机号修改
		if(CacheHelper.get("phone")!=null || CacheHelper.get("Flag")!=null){

			String phone = CacheHelper.get("phone").toString();
			Integer flag = Integer.valueOf(CacheHelper.get("Flag").toString());

			Record customer = Db.findFirst("select * from customer where mobilePhone = ?",phone );
			if(customer!=null){

				ServiceCode code = Member.setPassword(customer.getInt("id"), password, repassword);
				if (code != ServiceCode.Success) {
					returnError(ErrorCode.Exception, "修改密码失败");
					return;
				}
				CacheHelper.remove("phone");
				CacheHelper.remove("Flag");
			}
		}
		//邮箱修改
		if(CacheHelper.get("getId")!=null){
			Integer getId = Integer.valueOf(CacheHelper.get("getId").toString());

			ServiceCode serviceCode = Member.setPassword(getId, password, repassword);
			if(serviceCode!=ServiceCode.Success){

				returnError(ErrorCode.Exception, "修改密码失败");
				return;
			}
			CacheHelper.remove("getId");
		}
    	renderJson(jsonObject);
    }

    /**
     * 是否允许发邮件
     * @param email
     * @return
     */
    private boolean allowSend(String email) {
    	int maxNum = 10;
		Object v = CacheHelper.get(email);
		if(v != null) {
			int num = (int)v;
			if(CacheHelper.isExpire(email, 60)) {
				CacheHelper.remove(email);
				CacheHelper.put(email, 1);
			} else if(num >= maxNum) {
				return false;
			} else {
				CacheHelper.reset(email, num+1);
			}
		} else {
			CacheHelper.put(email, 1);
		}
		
		return true;
    }
    
    /**
     * 发送链接到用户邮箱
     * @param email 邮箱
     * @return 成功：{error:0,error:-1(该邮箱账号不存在),error:-2(发送邮件失败)} 失败：{error:>0,errmsg:错误信息}
     */
    public void sendEmail() {
    	if (!this.validateRequiredString("email")) {
    		return;
    	}
    	
    	if(!this.validateEmail("email")) {
    		return;
    	}
    	
    	String email = getPara("email");
    	
    	Customer customer = Member.getCustomerByEmail(email);
    	
    	if (customer == null) {
    		returnError(-1, "该账号不存在");
    		return;
    	}
    	
//    	String url = PropKit.use("callBackUrl.txt").get("frontHostName") + "eshop-pc/index.html#/setNewPwd?id=" + customer.getId();
		StringBuffer url =new StringBuffer();
    	url.append("http://www.eebin.com/#/setNewPwd?id="+ customer.getId());
		CacheHelper.put("getId",customer.getId());
    	String subject = "【乐驿商城】找回密码";
    	String content = "<p>您好，" + email + "</p><p style='margin-top:30px;'>请点击以下链接找回您的密码</p>" + 
    						"<p><a href=" + url + ">" + url + "</a></p>" + 
    					 "<p style='margin-top:50px;'>乐驿商城</p>";
    	
    	try {
			if(!allowSend(email)) {
				returnError(-3, "该邮箱超过发送条数");
				return;
			}
			MailHelper.sendEmail(email, subject, content);
			renderJson(jsonObject);
		} catch (MessagingException e) {
			e.printStackTrace();
			Log.error(e.getMessage() + ",发送邮件失败");
			returnError(-2, "发送邮件失败");
		}
    }
}