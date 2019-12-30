package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.aop.Before;

/**
 *   修改个人信息控制器
 *   @author TangYiFeng
 */
public class ModifyController extends WebappBaseController {

    /**
     * 构造方法
     */
    public ModifyController() {
    }

    /**
     * 修改用户信息
     *  @param token 帐户访问口令（必填）
     *  @param id 必填
     *  @param headImg 头像
     *  @param nickName 昵称
     *  @param name 真实姓名
     *  @param gender 性别
     *  @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void updateInfo() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	String name = getPara("name");
    	String nickName = getPara("nickName");
    	String headImg = getPara("headImg");
    	int gender = getParaToInt("gender");
    	
    	Customer model = new Customer();
    	model.setId(customerId);
    	model.setName(name);
    	model.setNickName(nickName);
    	model.setHeadImg(headImg);
    	model.setGender(gender);
    	
    	ServiceCode code = Member.updateInfo(model);
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改用户信息失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取用户信息
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0,data:{id:id,headImg:头像,nickName:昵称,name:姓名,mobilePhone:手机号码}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void getInfo() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Customer model = Member.getCustomer(customerId);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }

}