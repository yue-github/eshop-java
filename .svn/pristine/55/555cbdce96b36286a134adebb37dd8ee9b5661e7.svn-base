package com.eshop.config;

import com.eshop.interceptor.RequestInterceptor;
import com.eshop.model._MappingKit;
import com.eshop.routes.AdminRoutes;
import com.eshop.routes.PcRoutes;
import com.eshop.routes.WebAppRoutes;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;

public class ProjectConfig extends JFinalConfig {
	
	/**
	 * 配置常量
	 */
	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setEncoding("UTF-8");
		me.setError404View("/404.html");
	}
	
	/**
	 * 配置路由
	 */
	@Override
	public void configRoute(Routes me) {
		me.add(new PcRoutes());
		me.add(new AdminRoutes());
		me.add(new WebAppRoutes());
	}
	
	public static C3p0Plugin createC3p0Plugin() {
		PropKit.use("config.txt");
		return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
	}
	
	/**
	 * 配置插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		EhCachePlugin cache = new EhCachePlugin(PathKit.getWebRootPath() + "/WEB-INF/ehcache.xml");
		me.add(cache);
		
		C3p0Plugin cp = createC3p0Plugin();
		me.add(cp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		me.add(arp);
		_MappingKit.mapping(arp);
		
		//定时调度插件      
//		me.add(new Cron4jPlugin(PropKit.use("task.properties")));
		
	}
	
	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new RequestInterceptor());
	}
	
	/**
	 * 配置处理器
	 */
	@Override
	public void configHandler(Handlers me) {
	}
	
}

