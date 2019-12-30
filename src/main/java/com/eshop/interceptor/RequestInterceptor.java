package com.eshop.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.eshop.controller.admin.BaseController;
import com.eshop.controller.admin.BaseController.ErrorCode;
import com.eshop.helper.CacheHelper;
import com.eshop.model.Customer;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class RequestInterceptor implements Interceptor { 
	
	@Override
	public void intercept(Invocation inv) {
		BaseController ctl = (BaseController)inv.getController();
		System.out.println("requeestURI: " + ctl.getRequest().getRequestURI());
		System.out.println("servelPath: " + ctl.getRequest().getRemoteAddr());
		String request = ctl.getRequest().getRequestURI();
		String route = ctl.getRequest().getServletPath();
//		String userIP = ctl.getRequest().getRemoteAddr();//获取客户端IP地址
		String token = ctl.getPara("token");
		System.out.println("token:"+ token);
		Date nowTime = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

		if (token != null && token.trim().length() != 0) {
			Customer customer = (Customer) CacheHelper.get(token);
			if (customer != null) {
				String user = customer.getName();
				if(user == null) {
					user = customer.getMobilePhone();
				}
				if(user == null) {
					user = customer.getEmail();
				}
				if(user == null) {
					user = "";
				}
				Record log = new Record().set("username", user).set("request_url", request).set("route_url", route).set("create_time", ft.format(nowTime));
				Db.save("request_log", log);
				System.out.println("用户："+customer.getName());
			}			
		}
		inv.invoke();
    }
} 
