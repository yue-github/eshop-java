package com.eshop.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.SupplierBill;
import com.eshop.model.User;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.supplier.SupplierBillService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

public class SupplierBillController extends AdminBaseController {

	/**
	 * 批量查询
	 * @param token 必填
	 * @param offset 必填
	 * @param length 必填
	 * @param supplierId 供应商id 选填
	 * @param supplierName 供应商名称 选填
	 * @param productName 产品名称 选填
	 * @param status 对账单状态 选填
	 * @param startSendOutTime 选填
	 * @param endSendOutTime 选填
	 * @param code 账单编号 选填
	 * @param tradeType 账单类型 选填
	 * @param startCreatedAt 选填
	 * @param endCreatedAt 选填
	 * @param startAuditedAt 选填
	 * @param endAuditAt 选填
	 * @param operator 选填
	 * @param supplierBillId 选填
	 * @param orderCode 选填
	 */
	@Before(AdminAuthInterceptor.class)
	public void many() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer supplierId = getParaToIntegerDefault("supplierId");
		String supplierName = getPara("supplierName");
		String productName = getPara("productName");
		String status = getPara("status");
		String startSendOutTime = getPara("startSendOutTime");
		String endSendOutTime = getPara("endSendOutTime");
		String code = getPara("code");
		String tradeType = getPara("tradeType");
		String startCreatedAt = getPara("startCreatedAt");
		String endCreatedAt = getPara("endCreatedAt");
		String startAuditedAt = getPara("startAuditedAt");
		String endAuditedAt = getPara("startAuditedAt");
		String operator = getPara("operator");
		String orderCode = getPara("orderCode");
		Integer supplierBillId = getParaToIntegerDefault("supplierBillId");
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("sendOutTime", "asc");
		
		List<Record> data = SupplierBillService.findSupplierBillItems(offset, length, supplierId, supplierName, 
				status, startSendOutTime, endSendOutTime, code, tradeType, startCreatedAt, endCreatedAt, 
				productName, startAuditedAt, endAuditedAt, operator, supplierBillId, orderCode, orderByMap);
		
		int totalRow = SupplierBillService.countSupplierBillItems(supplierId, supplierName, status, 
				startSendOutTime, endSendOutTime, code, tradeType, startCreatedAt, endCreatedAt, 
				productName, startAuditedAt, endAuditedAt, operator, supplierBillId, orderCode);
		
		List<Record> list = SupplierBillService.findSupplierBillItems(supplierId, supplierName, status, 
				startSendOutTime, endSendOutTime, code, tradeType, startCreatedAt, endCreatedAt,
				productName, startAuditedAt, endAuditedAt, operator, supplierBillId, orderCode, orderByMap);
		
		Record statistics = SupplierBillService.calculateSupplierBillItems(list);
		
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", totalRow);
		jsonObject.put("data", data);
		jsonObject.put("list", list);
		jsonObject.put("totalActualProductPrice", statistics.getBigDecimal("totalActualProductPrice"));
		renderJson(jsonObject);
	}
	
	/**
	 * 审核供应商对账单明细
	 */
	@Before(AdminAuthInterceptor.class)
	public void submit() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		String adminToken = getPara("adminToken");
		User user = (User) CacheHelper.get(adminToken);
		String operator = user.getNickName() != null ? user.getNickName() : user.getUserName();
		Integer id = getParaToInt("id");
		
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		
		ServiceCode code = SupplierBillService.generateSupplierBill(ids, operator);
		if (ServiceCode.Success != code) {
			setError(ErrorCode.Exception, "提交失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 批量提交供应商对账单
	 * @param token 必填
	 * @param ids 格式：[1,2,3,...] 必填
	 * @param status 目标状态 必填
	 */
	@Before(AdminAuthInterceptor.class)
	public void batchSubmit() {
		String[] params = {"ids"};
		if (!this.validate(params)) {
			return;
		}
		
		String adminToken = getPara("adminToken");
		User user = (User) CacheHelper.get(adminToken);
		String operator = user.getNickName() != null ? user.getNickName() : user.getUserName();
		
		String idsStr = getPara("ids");
		JSONArray jsarr = JSON.parseArray(idsStr);
		
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < jsarr.size(); i++) {
			int id = jsarr.getIntValue(i);
			ids.add(id);
		}
		
		ServiceCode code = SupplierBillService.generateSupplierBill(ids, operator);
		if (ServiceCode.Success != code) {
			setError(ErrorCode.Exception, "提交失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 批量查询账单
	 * @param offset 必填
	 * @param length 必填
	 * @param supplierId 选填
	 * @param supplierName 选填
	 * @param billCode 选填
	 * @param status 选填
	 * @param auditOperator 选填
	 * @param payOperator 选填
	 * @param startCreatedAt 选填
	 * @param endCreatedAt 选填
	 * @param startPayedAt 选填
	 * @param endPayedAt 选填
	 */
	@Before(AdminAuthInterceptor.class)
	public void supplierBills() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer supplierId = getParaToIntegerDefault("supplierId");
		String supplierName = getPara("supplierName");
		String billCode = getPara("billCode");
		String status = getPara("status");
		String auditOperator = getPara("auditOperator");
		String payOperator = getPara("payOperator");
		String startCreatedAt = getPara("startCreatedAt");
		String endCreatedAt = getPara("endCreatedAt");
		String startPayedAt = getPara("startPayedAt");
		String endPayedAt = getPara("endPayedAt");
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("created_at", "desc");
		
		List<Record> data = SupplierBillService.findSupplierBill(offset, length, supplierId, supplierName,
				billCode, status, auditOperator, payOperator, startCreatedAt, endCreatedAt, startPayedAt,
				endPayedAt, orderByMap);
		int totalRow = SupplierBillService.countSupplierBill(supplierId, supplierName, billCode, status,
				auditOperator, payOperator, startCreatedAt, endCreatedAt, startPayedAt, endPayedAt);
		List<Record> list = SupplierBillService.findSupplierBill(supplierId, supplierName, billCode, status,
				auditOperator, payOperator, startCreatedAt, endCreatedAt, startPayedAt, endPayedAt,
				orderByMap);
		Record statistics = SupplierBillService.calculateSupplierBill(list);
		
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", totalRow);
		jsonObject.put("data", data);
		jsonObject.put("list", list);
		jsonObject.put("totalPayable", statistics.getBigDecimal("totalPayable"));
		renderJson(jsonObject);
	}
	
	/**
	 * 账单打款
	 * @param id 账单id 必填
	 */
	public void paySupplierBill() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		String adminToken = getPara("adminToken");
		User user = (User) CacheHelper.get(adminToken);
		String operator = user.getNickName() != null ? user.getNickName() : user.getUserName();
		
		ServiceCode code = SupplierBillService.paySupplierBill(id, operator);
		if (ServiceCode.Success != code) {
			setError(ErrorCode.Exception, "打款失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 查看账单详情和账单明细
	 */
	public void supplierBillAndDetails() {
		String[] params = {"supplierBillId"};
		if (!this.validate(params)) {
			return;
		}
		
		int supplierBillId = getParaToInt("supplierBillId");
		SupplierBill supplierBill = SupplierBillService.getSupplierBill(supplierBillId);
		List<Record> list = SupplierBillService.findSupplierBillItems(supplierBillId);
		
		jsonObject.put("supplierBill", supplierBill);
		jsonObject.put("supplierBillDetails", list);
		renderJson(jsonObject);
	}
	
}
