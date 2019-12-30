package com.eshop.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.eshop.controller.admin.BaseController;
import com.eshop.controller.admin.BaseController.ErrorCode;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Logger;

public class ExceptionInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		 BaseController ctl = (BaseController)inv.getController();
		 try {
			 inv.invoke();
		 } catch(Exception e) {
			 Logger log = Logger.getLogger(ctl.getClass());
			 log.error(e.getMessage());
			 ctl.returnError(ErrorCode.Exception, "服务器错误");
		 }
	}
}
