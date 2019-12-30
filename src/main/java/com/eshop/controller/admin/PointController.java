package com.eshop.controller.admin;

import java.util.Date;
import java.util.List;
import com.eshop.helper.DateHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.PointProduct;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.point.PointService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购活动管理
 * @author TangYiFeng
 */
public class PointController extends AdminBaseController {
	
	private PointService service = new PointService();
	
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"product_id", "need_point", "is_show", "sort", "start_time", "end_time"};
		if (!this.validate(params)) {
			return;
		}
		
		PointProduct model = new PointProduct();
		model.setProductId(getParaToInt("product_id"));
		model.setNeedPoint(getParaToInt("need_point"));
		model.setIsShow(getPara("is_show"));
		model.setSort(getParaToInt("sort"));
		model.setStartTime(DateHelper.strToDateTime(getPara("start_time")));
		model.setEndTime(DateHelper.strToDateTime(getPara("end_time")));
		model.setCreatedAt(new Date());
		
		ServiceCode code = service.create(model);
		if (code != ServiceCode.Success) {
			this.returnError(ErrorCode.Exception, "创建失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "product_id", "need_point", "is_show", "sort", "start_time", "end_time"};
		if (!this.validate(params)) {
			return;
		}
		
		PointProduct model = service.get(getParaToInt("id"));
		model.setProductId(getParaToInt("product_id"));
		model.setNeedPoint(getParaToInt("need_point"));
		model.setIsShow(getPara("is_show"));
		model.setSort(getParaToInt("sort"));
		model.setStartTime(DateHelper.strToDateTime(getPara("start_time")));
		model.setEndTime(DateHelper.strToDateTime(getPara("end_time")));
		
		ServiceCode code = service.update(model);
		if (code != ServiceCode.Success) {
			this.returnError(ErrorCode.Exception, "修改失败");
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
		
		ServiceCode code = service.delete(getParaToInt("id"));
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
		
		PointProduct model = service.get(getParaToInt("id"));
		
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
		Integer productId = null;
		String isShow = getPara("is_show");
		Boolean isValid = null;
		List<Record> list = service.list(offset, length, productId, isShow, isValid);
		int total = service.count(productId, isShow, isValid);
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void allProduct() {
		List<Product> list = Product.dao.find("select * from product where is_sale = 1 and isDelete = 0");
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
}