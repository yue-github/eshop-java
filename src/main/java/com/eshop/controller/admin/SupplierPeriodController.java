package com.eshop.controller.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.SupplierPeriod;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.supplier.SupplierPeriodService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

public class SupplierPeriodController extends AdminBaseController {

	/**
	 * 批量查询
	 * @param token 必填
	 * @param offset 必填
	 * @param length 必填
	 * @param type 账期类型 选填
	 */
	@Before(AdminAuthInterceptor.class)
	public void many() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String type = getPara("type");
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("created_at", "desc");
		
		List<Record> list = SupplierPeriodService.findSupplierPeriodItems(offset, length, type, orderByMap);
		int totalRow = SupplierPeriodService.countSupplierPeriodItems(type);
		
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", totalRow);
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	/**
	 * 查看账期详情
	 * @param token 必填
	 * @param id 必填
	 */
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		SupplierPeriod model = SupplierPeriodService.get(id);
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	/**
	 * 创建账期
	 * @param token 必填
	 * @param type 账期类型 必填
	 * @param days 天数 必填
	 */
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"type", "days"};
		if (!this.validate(params)) {
			return;
		}
		
		String type = getPara("type");
		int days = getParaToInt("days");
		
		SupplierPeriod model = new SupplierPeriod();
		model.setType(type);
		model.setDays(days);
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		
		ServiceCode code = SupplierPeriodService.create(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "创建失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 修改账期
	 * @param token 必填
	 * @param id 必填
	 * @param type 账期类型 必填
	 * @param days 天数 必填
	 */
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "type", "days"};
		if (!this.validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		String type = getPara("type");
		int days = getParaToInt("days");
		
		SupplierPeriod model = SupplierPeriodService.get(id);
		model.setType(type);
		model.setDays(days);
		model.setUpdatedAt(new Date());
		
		ServiceCode code = SupplierPeriodService.update(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "修改失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 删除某条账期
	 * @param token 必填
	 * @param id 必填
	 */
	@Before(AdminAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		ServiceCode code = SupplierPeriodService.delete(id);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 批量删除失败
	 * @param token 必填
	 * @param ids [1,2,...] 必填
	 */
	@Before(AdminAuthInterceptor.class)
	public void batchDelete() {
		String[] params = {"ids"};
		if (!this.validate(params)) {
			return;
		}
		
		String ids = getPara("ids");
		ServiceCode code = SupplierPeriodService.batchDelete(ids);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "批量删除失败");
		}
		
		renderJson(jsonObject);
	}
	
}
