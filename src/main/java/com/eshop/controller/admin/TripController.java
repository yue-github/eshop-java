package com.eshop.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eshop.excel.ExportSupplierOrderList;
import com.eshop.finance.HotelAndTripService;
import com.eshop.finance.SupplierFinanceService;
import com.eshop.helper.DateHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.dao.BaseDao;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 退货管理-控制器
 * @author TangYiFeng
 */
public class TripController extends AdminBaseController {
	
	public void export() {
		String[] params = {"orderType", "headerTitle"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String headerTitle = getPara("headerTitle");
    	Integer supplierId = getParaToIntegerDefault("supplierId");
    	String supplierName = "";
    	Integer status = getParaToIntegerDefault("status");
    	String orderCode = getPara("orderCode");
    	String tradeCode = "";
    	String tradeNo = getPara("tradeNo");
    	String expressCode = "";
    	String logisticsName = "";
    	Integer payType = null;
    	Integer source = null;
    	String productName = "";
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	Integer orderType = getParaToInt("orderType");
    	
    	if (getPara("startTime") != null && !getPara("startTime").equals("")) {
    		startTime = getPara("startTime");
    	}
    	if (getPara("endTime") != null && !getPara("endTime").equals("")) {
    		endTime = getPara("endTime");
    	}
    	
    	String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
    	
    	List<Record> list = HotelAndTripService.findDetailItems(supplierId, status, supplierName, orderCode, tradeCode, tradeNo, productName, startTime, endTime, payType, source, expressCode, logisticsName, whereInPayType, orderType);
    	Record statistics = HotelAndTripService.calculateDetailItems(list);
    	
    	Record supplier = SupplierFinanceService.getSupplier(supplierId);
    	String strSupplierName = (supplier != null) ? supplier.getStr("name") : "";
    	String strStatus = getOrderStatus(status);
    	String strPayType = getPayTypes(getPara("payType"));
    	Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("strSupplierName", strSupplierName);
		searchMap.put("strStartTime", startTime);
		searchMap.put("strEndTime", endTime);
		searchMap.put("strStatus", strStatus);
		searchMap.put("strPayType", strPayType);
    	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = headerTitle + dateFormat.format(new Date());
		String path = ExportSupplierOrderList.exportTripList(fileName, list, statistics, startTime, endTime, searchMap, headerTitle);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	} 
	
	@Before(AdminAuthInterceptor.class)
    public void list() {
    	String[] params = {"offset", "length", "orderType"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	Integer supplierId = getParaToIntegerDefault("supplierId");
    	String supplierName = "";
    	Integer status = getParaToIntegerDefault("status");
    	String orderCode = getPara("orderCode");
    	String tradeCode = "";
    	String tradeNo = getPara("tradeNo");
    	String expressCode = "";
    	String logisticsName = "";
    	Integer payType = null;
    	Integer source = null;
    	String productName = "";
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	Integer orderType = getParaToInt("orderType");
    	
    	if (getPara("startTime") != null && !getPara("startTime").equals("")) {
    		startTime = getPara("startTime");
    	}
    	if (getPara("endTime") != null && !getPara("endTime").equals("")) {
    		endTime = getPara("endTime");
    	}
    	
    	String whereInPayType = null;
    	
		if (getPara("payType") != null 
			&& !getPara("payType").equals("") 
			&& !getPara("payType").equals("[]")) {
			
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
    	List<Record> data = HotelAndTripService.findDetailItems(offset, length, supplierId, status, supplierName, orderCode, tradeCode, tradeNo, productName, startTime, endTime, payType, source, expressCode, logisticsName, whereInPayType, orderType);
    	List<Record> list = HotelAndTripService.findDetailItems(supplierId, status, supplierName, orderCode, tradeCode, tradeNo, productName, startTime, endTime, payType, source, expressCode, logisticsName, whereInPayType, orderType);
    	Record statistics = HotelAndTripService.calculateDetailItems(list);
    	int total = list.size();
    	
    	Record supplier = SupplierFinanceService.getSupplier(supplierId);
    	String strSupplierName = (supplier != null) ? supplier.getStr("name") : "";
    	String strStatus = getOrderStatus(status);
    	String strPayType = getPayTypes(getPara("payType"));
    	Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("strSupplierName", strSupplierName);
		searchMap.put("strStartTime", startTime);
		searchMap.put("strEndTime", endTime);
		searchMap.put("strStatus", strStatus);
		searchMap.put("strPayType", strPayType);
    	
		jsonObject.putAll(searchMap);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", data);
    	jsonObject.put("list", list);
    	jsonObject.put("totalRefundAmount", statistics.getInt("totalRefundAmount"));
    	jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
    	jsonObject.put("totalUnitOrdered", statistics.getInt("totalUnitOrdered"));
    	jsonObject.put("totalSaleCost", statistics.getDouble("totalSaleCost"));
    	jsonObject.put("totalSale", statistics.getDouble("totalSale"));
    	jsonObject.put("totalCommissionPayable", statistics.getDouble("totalCommissionPayable"));
    	jsonObject.put("weixin", statistics.getDouble("weixin"));
    	jsonObject.put("weixinPc", statistics.getDouble("weixinPc"));
    	jsonObject.put("weixinApp", statistics.getDouble("weixinApp"));
    	jsonObject.put("alipay", statistics.getDouble("alipay"));
    	jsonObject.put("unionpay", statistics.getDouble("unionpay"));
    	jsonObject.put("balancepay", statistics.getDouble("balancepay"));
    	renderJson(jsonObject);
    }
	
	private String getPayTypes(String payTypes) {
		String str = "";
		if (payTypes == null || payTypes.equals("")) {
			return str;
		}
		JSONArray array = JSON.parseArray(payTypes);
		for (int i = 0; i < array.size(); i++) {
			int payType = array.getIntValue(i);
			if (i == array.size() - 1) {
				str += getPayType(payType);
				break;
			} else {
				str += getPayType(payType) + ",";
			}
		}
		return str;
	}
	
	private String getPayType(int payType) {
		String strPayType = "";
		switch (payType) {
		case 1:
			strPayType = "微信支付";
			break;
		case 2:
			strPayType = "支付宝支付";
			break;
		case 3:
			strPayType = "银联支付";
			break;
		case 5:
			strPayType = "团购卡支付";
			break;
		case 6:
			strPayType = "积分兑换支付";
			break;	
		}
		return strPayType;
	}
	
	private String getOrderStatus(Integer status) {
		if (status == null) {
			return "";
		}
		String strStatus = "";
		switch (status) {
		case 1:
			strStatus = "已付";
			break;
		case 2:
			strStatus = "发货";
			break;
		case 3:
			strStatus = "收货";
			break;
		}
		return strStatus;
	}
    
}