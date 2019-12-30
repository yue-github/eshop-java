package com.eshop.controller.webapp;


/**
 *   首页控制器
 *   @author TangYiFeng
 */
public class AppVersionController extends WebappBaseController {
	private String content = "发现新版本，请更新";
	
	public void androidInfo() {
		jsonObject.put("versionCode", 3);
		jsonObject.put("versionName", "3.0");
		jsonObject.put("content", this.content);
		jsonObject.put("updateUrl", "");
		renderJson(jsonObject);
	}
	
	public void iosInfo() {
		jsonObject.put("versionCode", 3);
		jsonObject.put("versionName", "3.0");
		jsonObject.put("content", this.content);
		jsonObject.put("updateUrl", "");
		renderJson(jsonObject);
	}

}