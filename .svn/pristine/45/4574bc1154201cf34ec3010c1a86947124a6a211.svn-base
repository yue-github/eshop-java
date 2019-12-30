package com.eshop.controller.admin;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.User;
import com.eshop.permission.NavigationService;
import com.eshop.service.HomeTotalService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 全局控制器
 * @author TangYiFeng
 */
public class IndexController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public IndexController() {
    }
    
    /**
     * 获取权限导航
     * @param adminToken 用户登录口令
     * @return 成功：{error: 0, data: [{id: id, name: 英文名称, displayName: 中文名称, icon: 图标, url: 链接, sortNumber: 排序, children: [下级导航, ...]  },...}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void getRoleNavs() {
    	String token = getPara("adminToken");
    	User user = (User)CacheHelper.get(token);
    	
    	List<Record> navs = NavigationService.getRoleNavs(user.getId());
    	
    	jsonObject.put("data", navs);
    	
    	renderJson(jsonObject);
    }
    
	/**
	 * 获取月成交量，昨日成交量，今日成交量,最近7天成交量
	 * @return
	 */
    @Before(AdminAuthInterceptor.class)
	public void getMakeABargain() {
    	//
    	Record makeABargain = HomeTotalService.getMakeABargain();
    	//最近7天成交量
    	Record sevenDays = HomeTotalService.getWeekMakeABargain();
    	//最近7天成交金额
    	Record sevenDaysMoney = HomeTotalService.getWeekMakeAMoney();
    	jsonObject.put("monthTotal", makeABargain.get("monthTotal"));
    	jsonObject.put("todayTotal", makeABargain.get("todayTotal"));
    	jsonObject.put("yesterdayTotal", makeABargain.get("yesterdayTotal"));
    	jsonObject.put("sevenDaysBargain", sevenDays);
    	jsonObject.put("sevenDaysMoney", sevenDaysMoney);
    	renderJson(jsonObject);
    }

}