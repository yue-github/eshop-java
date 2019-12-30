package com.eshop.controller.admin;

import java.util.List;

import com.eshop.groupcard.GroupSetMealService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.GroupSetMeals;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购活动管理
 * @author TangYiFeng
 */
public class GroupSetMealController extends AdminBaseController {
	
	private GroupSetMealService service = new GroupSetMealService();
	
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"title", "group_activity_id", "store_amount", "is_sale"};
		if (!this.validate(params)) {
			return;
		}
		
		GroupSetMeals model = new GroupSetMeals();
		model.setTitle(this.getPara("title"));
		model.setGroupActivityId(this.getParaToInt("group_activity_id"));
		model.setStoreAmount(this.getParaToInt("store_amount"));
		model.setIsSale(this.getPara("is_sale"));
		model.setDesc(this.getPara("desc"));
		model.setImage(this.getPara("image"));
		
		ServiceCode code = service.create(model);
		
		if (code != ServiceCode.Success) {
			this.returnError(ErrorCode.Exception, "创建失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "title", "group_activity_id", "store_amount", "is_sale"};
		if (!this.validate(params)) {
			return;
		}
		
		GroupSetMeals model = service.get(this.getParaToInt("id"));
		model.setTitle(this.getPara("title"));
		model.setGroupActivityId(this.getParaToInt("group_activity_id"));
		model.setStoreAmount(this.getParaToInt("store_amount"));
		model.setIsSale(this.getPara("is_sale"));
		model.setDesc(this.getPara("desc"));
		model.setImage(this.getPara("image"));
		
		ServiceCode code = service.update(model);
		
		if (code != ServiceCode.Success) {
			this.returnError(ErrorCode.Exception, "创建失败");
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
		
		ServiceCode code = service.delete(this.getParaToInt("id"));
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "删除失败");
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
		
		GroupSetMeals model = service.get(this.getParaToInt("id"));
		
		jsonObject.put("data", model);
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
		Integer groupActivityId = getParaToIntegerDefault("group_activity_id");
		List<Record> list = service.list(offset, length, groupActivityId, "", "");
		int total = service.count(groupActivityId, "", "");
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
	
}