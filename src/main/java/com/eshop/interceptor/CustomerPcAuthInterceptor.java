package com.eshop.interceptor;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.eshop.controller.admin.BaseController;
import com.eshop.controller.admin.BaseController.ErrorCode;
import com.eshop.helper.CacheHelper;
import com.eshop.model.Customer;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 后台登录验证拦截器
 * @author TangYiFeng
 */
public class CustomerPcAuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		 BaseController ctl = (BaseController)inv.getController();
		 String token = ctl.getPara("token");
		 
		 if (token == null || token.equals("")) {
			 ctl.returnError(ErrorCode.Authority, "登陆失败");
			 return;
		 }
		 Customer customer = (Customer) CacheHelper.get(token);
		 
		 if(customer == null || customer.getDisable() == 1) {
			 ctl.returnError(ErrorCode.Authority, "登陆失败");
			 return;
		 } else {
			 Object ObjToken = CacheHelper.get(customer.getId().toString());
			 if(ObjToken == null || !token.equals(ObjToken.toString())) {
				 CacheHelper.remove(token);   //清除过期的缓存token
				 ctl.returnError(ErrorCode.Authority, "账号已经在别的地方登录");
				 return;
			 }
		 }
		inv.invoke();
	}
}
