package com.eshop.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.eshop.auditprice.AuditPriceService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.AuditPrice;
import com.eshop.model.User;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

public class AuditPriceController extends AdminBaseController {

	@Before(AdminAuthInterceptor.class)
	public void many() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String supplierName = getPara("supplier_name");
		String productName = getPara("product_name");
		String publishStatus = getPara("publish_status");
		String status = getPara("status");
		
		List<Record> list = AuditPriceService.findAuditPriceItems(offset, length, productName, supplierName, null, status, publishStatus, null, null);
		int total = AuditPriceService.countAuditPriceItems(productName, supplierName, null, status, publishStatus, null, null);
	
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = {"theSameNum"};
		if (!this.validate(params)) {
			return;
		}
		
		int theSameNum = getParaToInt("theSameNum");
		List<Record> list = AuditPriceService.getItems(theSameNum);
		AuditPrice model = AuditPriceService.get(theSameNum);
		
		jsonObject.put("data", list);
		jsonObject.put("status", model.getStatus());
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void audit() {
		String[] params = {"theSameNum", "status"};
		if (!this.validate(params)) {
			return;
		}
		
		String token = getPara("adminToken");
    	User user = (User) CacheHelper.get(token);
    	String operator = (user != null) ? user.getNickName() : "";
		int theSameNum = getParaToInt("theSameNum");
		String status = getPara("status");
		ServiceCode code = AuditPriceService.audit(theSameNum, status, operator);
		
		if (code == ServiceCode.Validation) {
			setError(ErrorCode.Validation, "审核状态不能为未审核");
		} else if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "审核失败");
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void details() {
		String[] params = {"offset", "length", "theSameNum"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer theSameNum = getParaToIntegerDefault("theSameNum");
		List<Record> list = AuditPriceService.findAuditItems(offset, length, theSameNum, null);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	
	@Before(AdminAuthInterceptor.class)
	public void exportAuditPriceInfo() {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "chanPinDingJia_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportPriceInfo(fileName, null, null, null);
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	} 
	
}
