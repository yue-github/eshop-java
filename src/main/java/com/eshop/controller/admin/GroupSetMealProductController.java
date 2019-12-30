package com.eshop.controller.admin;

import java.util.List;

import com.eshop.groupcard.GroupSetMealProductService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购活动管理
 * @author TangYiFeng
 */
public class GroupSetMealProductController extends AdminBaseController {
	
	private GroupSetMealProductService service = new GroupSetMealProductService();
	
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"product_id", "group_activity_id", "group_set_meal_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int productId = this.getParaToInt("product_id");
		int groupActivityId = this.getParaToInt("group_activity_id");
		int groupSetMealId = this.getParaToInt("group_set_meal_id");
		int amount = this.getParaToInt("amount");
		ServiceCode code = service.create(productId, groupActivityId, groupSetMealId, amount);
		
		if (code != ServiceCode.Success) {
			this.returnError(ErrorCode.Exception, "创建失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "product_id", "amount"};
		if (!this.validate(params)) {
			return;
		}
		
		int id = this.getParaToInt("id");
		int productId = this.getParaToInt("product_id");
		int amount = this.getParaToInt("amount");
		ServiceCode code = service.update(id, productId, amount);
		
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
		
		Record model = service.get(this.getParaToInt("id"));
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void list() {
		String[] params = {"offset", "length", "group_set_meal_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer groupSetMealId = this.getParaToIntegerDefault("group_set_meal_id");
		List<Record> list = service.list(offset, length, groupSetMealId);
		int total = service.count(groupSetMealId);
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
}