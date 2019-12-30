package com.eshop.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.eshop.groupcard.GroupActivityCardService;
import com.eshop.helper.ExcelHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购活动管理
 * @author TangYiFeng
 */
public class GroupActivityCardController extends AdminBaseController {
	
	private GroupActivityCardService service = new GroupActivityCardService();
	
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"group_activity_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int groupActivityId = this.getParaToInt("group_activity_id");
		
		int code = service.generateCards(groupActivityId);
		if (code != 0) {
			this.returnError(code, "创建失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void list() {
		String[] params = {"offset", "length", "group_activity_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String code = getPara("code"); 
		String isUsed = getPara("isUsed");
		Integer groupActivityId = getParaToIntegerDefault("group_activity_id");
		
		List<Record> list = service.list(offset, length, code, isUsed, groupActivityId);
		int total = service.count(code, isUsed, groupActivityId);
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void export() {
		String[] params = {"group_activity_id"};
		if (!this.validate(params)) {
			return;
		}
		
		String code = getPara("code"); 
		String isUsed = getPara("isUsed");
		Integer groupActivityId = getParaToIntegerDefault("group_activity_id");
		
		List<Record> list = service.all(code, isUsed, groupActivityId);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "orderList_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportActivityCard(fileName, list);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
}