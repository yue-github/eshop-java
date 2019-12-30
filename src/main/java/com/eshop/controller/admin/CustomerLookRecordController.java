package com.eshop.controller.admin;

import java.util.Date;
import java.util.List;

import com.eshop.model.CustomerLookRecord;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.visit.CustomerLookRecordService;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class CustomerLookRecordController extends AdminBaseController {
	
	/**
	*会员购买习惯-- customer_look_record(表名)
	*会员购买习惯添加
    *@param customerId 用户id
    *@param customerName 会员姓名
    *@param supplierId 供应商Id
    *@param supplierName 供应商名称
    *@param productId 商品Id
    *@param productName 商品名称
    *@param categoryId 商品类型Id
    *@param categoryName 商品类型名称 
    *@return  成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	*/
	public void customerLook() {
		ServiceCode code = null;
		try {
			Integer customerId = getParaToInt("customer_id");
			String customerName = getPara("customer_name");
			Integer supplierId = getParaToInt("supplier_id");
			String supplierName = getPara("supplier_name");
			Integer productId = getParaToInt("product_id");
			String productName = getPara("product_name");
			String categoryName = getPara("category_name");
			Integer categoryId = getParaToInt("category_id");
			Date createAt = new Date();
			CustomerLookRecord model = new CustomerLookRecord();
			model.setCustomerId(customerId);
			model.setCustomerName(customerName);
			model.setSupplierId(supplierId);
			model.setSupplierName(supplierName);
			model.setProductId(productId);
			model.setProductName(productName);
			model.setCategoryId(categoryId);
			model.setCategoryName(categoryName);
			model.setCreateAt(createAt);
			code = CustomerLookRecordService.customerLook(model);
		} catch (Exception e) {
			e.printStackTrace();
			setError(ErrorCode.Exception, "失败");
			
		}
		if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
		renderJson(jsonObject);
	}
	
	/**
	*会员访问 -- customer_look_record(表名)
	*会员购买习惯列表
	*@param pageIndex 开始页数 1
	*@param length 
	*@param startTime开始时间
	*@param endTime 结束时间
	*@return  成功：{error: 0, customer_visit: [{""}]}；失败：{error: >0, errmsg: 错误信息}
	*/
	public void customerLookRecordList() {
		String[] params = {"pageIndex", "length"};
		if (!validate(params)) {
			return;
		}
		int pageIndex = getParaToInt("pageIndex");
		int length = getParaToInt("length");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		Page<CustomerLookRecord> list = CustomerLookRecordService.findCustomerLooks(pageIndex, length, startTime, endTime);
		//将数据封装
		jsonObject.put("pageIndex", pageIndex);
		jsonObject.put("totalRow", list.getTotalRow());
		jsonObject.put("data", list.getList());
		//返回
		renderJson(jsonObject);
	}
	
	public void many() {
    	String[] params = {"offset", "length"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	String customerName = getPara("customerName");
    	String productName = getPara("productName");
    	String startTime = getPara("startTime");
    	String endTime = getPara("endTime");
    	
    	Record result = CustomerLookRecordService.findItems(offset, length, customerName, productName, startTime, endTime);
    	
    	jsonObject.put("data", result.get("data"));
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", result.getInt("total"));
    	renderJson(jsonObject);
    }

}
