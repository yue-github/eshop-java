package com.eshop.controller.admin;

import java.util.Date;
import java.util.List;
import com.eshop.groupcard.GroupActivityService;
import com.eshop.helper.DateHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.GroupActivities;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购活动管理
 * @author TangYiFeng
 */
public class GroupActivityController extends AdminBaseController {
	
	private GroupActivityService service = new GroupActivityService();
	
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"title", "total_money", "discount", "start_time", "end_time"};
		if (!this.validate(params)) {
			return;
		}
		
		GroupActivities model = new GroupActivities();
		model.setTitle(getPara("title"));
		model.setTotalMoney(getParaToDecimal("total_money"));
		model.setDiscount(getParaToDecimal("discount"));
		model.setStartTime(DateHelper.strToDateTime(getPara("start_time")));
		model.setEndTime(DateHelper.strToDateTime(getPara("end_time")));
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		
		int code = service.create(model);
		if (code != 0) {
			this.returnError(code, "创建失败");
			return;
		}
		
		jsonObject.put("id", model.getId());
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "title", "total_money", "discount", "start_time", "end_time"};
		if (!this.validate(params)) {
			return;
		}
		
		GroupActivities model = service.get(getParaToInt("id"));
		model.setTitle(getPara("title"));
		model.setTotalMoney(getParaToDecimal("total_money"));
		model.setDiscount(getParaToDecimal("discount"));
		model.setStartTime(DateHelper.strToDateTime(getPara("start_time")));
		model.setEndTime(DateHelper.strToDateTime(getPara("end_time")));
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		
		int code = service.update(model);
		if (code != 0) {
			this.returnError(code, "修改失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		int code = service.delete(getParaToInt("id"));
		if (code != 0) {
			returnError(code, "删除失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		GroupActivities model = service.get(getParaToInt("id"));
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void list() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String title = getPara("title");
		List<Record> list = service.list(offset, length, title);
		int total = service.count(title);
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
	
}