package com.eshop.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import  java.util.*;
import java.text.*;
import com.eshop.controller.admin.BaseController;
import com.eshop.controller.admin.BaseController.ErrorCode;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.IPHelper;
import com.eshop.model.Customer;
import com.eshop.model.Navigation;
import com.eshop.model.OperationLog;
import com.eshop.model.User;
import com.eshop.service.OperationLogService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 后台登录验证拦截器
 * @author TangYiFeng
 */
public class AdminAuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		 BaseController ctl = (BaseController)inv.getController();
		 String token = ctl.getPara("adminToken");
		 
		 if (token == null || token.equals("")) {
			 ctl.returnError(ErrorCode.Authority, "登陆失败");
			 return;
		 }
		 User user = (User) CacheHelper.get(token);
		 
		 
		 if(token == null || token.equals("") || CacheHelper.get(token) == null) {
			 ctl.returnError(ErrorCode.Authority, "没有登录");
			 return;
		 } else {
			 Object ObjToken = CacheHelper.get(user.getId().toString());
			 if(ObjToken == null || !token.equals(ObjToken.toString())) {
				 CacheHelper.remove(token);   //清除过期的缓存token
				 ctl.returnError(ErrorCode.Authority, "账号已经在别的地方登录");
				 return;
			 }
			 
			 OperationLogService service = new OperationLogService();
			 String[] names = inv.getController().getClass().getName().split("\\.");
			 if(names.length > 0) {
				 String controllerName = names[names.length - 1];
				 
				 String navStr = controllerName.replace("Controller", "");
				 Navigation nav = Navigation.dao.findFirst("select * from navigation where name = ?", navStr);
				 if(nav != null) {
					 navStr = nav.getDisplayName();
				 } 
				 
				 String actionName = getActionName(inv.getMethodName());
				 if(actionName != null) {
					 service.create(user, IPHelper.getIpAddr(ctl.getRequest()), navStr + "=>" +actionName, "", "操作");
				 }
			 }
			 
			 inv.invoke();
		 }
	}
	
	private String getActionName(String name) {
		String actionName = name;
		if(name.equals("many")) {
			actionName = "查询";
		} else if(name.equals("create")) {
			actionName = "创建";
		} else if(name.equals("update")) {
			actionName = "修改";
		} else if(name.equals("delete")) {
			actionName = "删除";
		}  else if(name.equals("get")) {
			actionName = "查看详情";
		} 
		
		return actionName;
	}
}
