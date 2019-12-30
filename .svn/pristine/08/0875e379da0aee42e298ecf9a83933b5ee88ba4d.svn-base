package com.eshop.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eshop.finance.SaleCostService;
import com.eshop.helper.DateHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Supplier;
import com.eshop.service.Manager;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 收入成本管理
 * @author TangYiFeng
 */
public class SaleCostController extends AdminBaseController {
	
	SaleCostService saleCostService;

	public SaleCostController() {
		saleCostService = new SaleCostService();
    }
    
	/**
	 * 查询
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
    	String invoiceType = getPara("invoiceType");
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	
    	if (getPara("startTime") != null && !getPara("startTime").equals("")) {
    		startTime = getPara("startTime");
    	}
    	if (getPara("endTime") != null && !getPara("endTime").equals("")) {
    		endTime = getPara("endTime");
    	}
    	
    	Map<String, String> searchMap = getSearchMap(supplierId, invoiceType, startTime, endTime);
    	List<Record> data = saleCostService.findSaleCostItems(offset, length, supplierId, invoiceType, 
    			startTime, endTime);
    	List<Record> list = saleCostService.findSaleCostItems(supplierId, invoiceType, startTime, endTime);
    	Record statistics = saleCostService.calculateSaleCostItems(list);
    	int totalRow = list.size();
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("data", data);
    	jsonObject.put("list", list);
    	jsonObject.put("totalWeixinpay", statistics.getDouble("totalWeixinpay"));
    	jsonObject.put("totalAlipay", statistics.getDouble("totalAlipay"));
    	jsonObject.put("totalUnionpay", statistics.getDouble("totalUnionpay"));
    	jsonObject.put("totalCardpay", statistics.getDouble("totalCardpay"));
    	jsonObject.put("totalPointpay", statistics.getDouble("totalPointpay"));
    	jsonObject.put("totalPayable", statistics.getDouble("totalPayable"));
    	jsonObject.put("totalUnitCost", statistics.getDouble("totalUnitCost"));
    	jsonObject.put("totalUnitCostNoTax", statistics.getDouble("totalUnitCostNoTax"));
    	jsonObject.put("supplierName", searchMap.get("supplierName"));
    	jsonObject.put("invoiceType", searchMap.get("invoiceType"));
    	jsonObject.put("startTime", searchMap.get("startTime"));
    	jsonObject.put("endTime", searchMap.get("endTime"));
    	renderJson(jsonObject);
    }
    
    /**
     * 导出
     */
    public void export() {
    	Integer supplierId = getParaToIntegerDefault("supplierId");
    	String invoiceType = getPara("invoiceType");
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	
    	if (getPara("startTime") != null && !getPara("startTime").equals("")) {
    		startTime = getPara("startTime");
    	}
    	if (getPara("endTime") != null && !getPara("endTime").equals("")) {
    		endTime = getPara("endTime");
    	}
    	
    	List<Record> list = saleCostService.findSaleCostItems(supplierId, invoiceType, startTime, endTime);
    	Record statistics = saleCostService.calculateSaleCostItems(list);
    	Map<String, String> searchMap = getSearchMap(supplierId, invoiceType, startTime, endTime);
    	
		String path = ExcelHelper.exportSaleCost("shouruchengbeng", list, statistics, searchMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
    }
    
    private Map<String, String> getSearchMap(Integer supplierId, String invoiceType, String startTime, 
    		String endTime) {
    	
    	Supplier supplier = null;
    	if (supplierId != null) {
    		supplier = Manager.getSupplier(supplierId);
    	}
    	String supplierName = (supplier != null) ? supplier.getName() : "";	
    	
    	String invoice = "";
    	if (invoiceType != null && invoiceType.equals("value_add")) {
    		invoice = "增值税专用发票";
    	} else if (invoiceType != null && invoiceType.equals("general")) {
    		invoice = "增值税普通发票";
    	}
    	
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("supplierName", supplierName);
    	map.put("invoiceType", invoice);
    	map.put("startTime", startTime);
    	map.put("endTime", endTime);
    	return map;
    }
	
}