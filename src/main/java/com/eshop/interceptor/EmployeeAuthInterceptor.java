package com.eshop.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.eshop.controller.admin.BaseController;
import com.eshop.controller.admin.BaseController.ErrorCode;
import com.eshop.helper.CacheHelper;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * 登录验证拦截器
 * @author TangYiFeng
 */
public class EmployeeAuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		 BaseController ctl = (BaseController)inv.getController();
		 String token = ctl.getPara("token");

		 if(token == null || token.equals("") || CacheHelper.get(token) == null) {
			 ctl.returnError(ErrorCode.Authority, "没有登录");
			 
			 return;
		 } else {
			 inv.invoke();
		 }
	}
}
