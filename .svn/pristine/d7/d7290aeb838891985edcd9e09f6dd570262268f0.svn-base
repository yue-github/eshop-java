package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 个人中心控制器
 * @author TangYiFeng
 */
public class CenterController extends WebappBaseController {

    /**
     * 构造方法
     */
    public CenterController() {
    }

    /**
     * 获取会员信息
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0, info: {nickName: 昵称, headImg: 头像, note: 简介,status1: 待付款数量, status2: 待发货数量, status3: 待收货数量, status4: 待评价数量, status5: 售后数量  }}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void info() {
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	Record result = User.countOrderStatusAmount(customer.getId());
    	int status5 = result.getInt("refunds") + result.getInt("backs");
    	
    	customer.put("status1", result.getInt("status1"));
    	customer.put("status2", result.getInt("status2"));
    	customer.put("status3", result.getInt("status3"));
    	customer.put("status4", result.getInt("status4"));
    	customer.put("status5", status5);
        
        jsonObject.put("info", customer);
        renderJson(jsonObject);
    }
    
    /**
     * 退出登录
     * @param token
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void logout() {
    	String token = getPara("token");
    	CacheHelper.remove(token);
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值,isRegister:{true:该手机号码已注册，false:该手机号码还没注册}} 失败：{error:>0, errmsg:错误信息}
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
    	
    	Customer customer = Member.getCustomerByMobilePhone(phone);
    	boolean isRegister = (customer != null) ? true : false;
    	
    	jsonObject.put("isRegister", isRegister);
    	jsonObject.put("codeToken", result.get("codeToken"));
    	renderJson(jsonObject);
    }
    
    /**
     * 绑定手机
     * @token
     * @phone 手机号码
     * @code 手机验证码
     * @codeToken 验证码token
     * @return 成功:{error:0,error:-1(验证码不正确),error:-2(手机号码已存在，不允许绑定)} 失败:{error>0,errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void bindAccount() {
    	String[] params = {"phone", "code", "codeToken"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String phone = getPara("phone");
    	String codeToken = getPara("codeToken");
    	String code = getPara("code");
    	
    	//判断验证码是否正确
    	if (Member.hasCode(codeToken, code) != ServiceCode.Success) {
    		returnError(-1, "验证码不正确");
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode rcode = Member.bindPhone(customerId, phone);
    	
    	if (rcode == ServiceCode.Function) {
    		setError(-2, "手机号码已存在，不允许绑定");
    	} else if (rcode != ServiceCode.Failed) {
    		setError(ErrorCode.Exception, "绑定手机号码失败");
    	}
    	
    	renderJson(jsonObject);
    }

}