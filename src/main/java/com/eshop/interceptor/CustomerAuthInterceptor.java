package com.eshop.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.eshop.controller.admin.BaseController;
import com.eshop.controller.admin.BaseController.ErrorCode;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.TokenHelper;
import com.eshop.helper.WxLoginHelper;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * 登录验证拦截器
 * @author TangYiFeng
 */
public class CustomerAuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		 BaseController ctl = (BaseController)inv.getController();
		 String token = ctl.getPara("token");
		 
		 if (token == null || token.equals("")) {
			 ctl.returnError(ErrorCode.Authority, "登陆失败");
			 return;
		 }
		 Customer ct = (Customer) CacheHelper.get(token);
		 
		 if(ct == null || ct.getDisable() == 1) {
			 String code = ctl.getPara("code");
			 String redirectURI = ctl.getPara("redirectURI");
			 
			 if(code == null || code == "") {
				 String url = WxLoginHelper.getUrl(redirectURI);
				 
				 ctl.jsonObject.put("authUrl", url);
				 ctl.returnError(ErrorCode.Authority, "没有登录");
				 
				 return;
			 } 
			 else  {
				 try {
					 WxMpUser user = WxLoginHelper.getUserInfo(code);
					 //通过openId查询用户是否已注册
					 Customer customer = Member.getByOpenId(user.getOpenId());
					 
					 if(customer == null) {   //未注册，自动注册
						 customer = new Customer();
						 customer.setWeiXinOpenId(user.getOpenId());
						 customer.setHeadImg(user.getHeadImgUrl());
						 customer.setNickName(user.getNickname());
						 customer.setCreatedAt(new Date());
						 customer.setUpdatedAt(new Date());
						 
						 if(Member.register(customer) != ServiceCode.Success) {
							 ctl.returnError(ErrorCode.Exception, "服务器异常");
							 
							 return;
						 }
					 } else if (customer.getDisable() == 1) {
						 return;
					 }
					 
					 token = TokenHelper.create();
					 CacheHelper.put(token, customer);
					 ctl.jsonObject.put("token", token);
					 ctl.setAttr("token", token);
				
					 ctl.returnError(ErrorCode.Success, "success");
					 inv.invoke();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ctl.returnError(ErrorCode.Exception, e.getMessage() + "**");
					
					return;
				}
			 }
		 } else {
			 inv.invoke();
		 }
	}
}
