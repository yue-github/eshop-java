package com.eshop.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eshop.finance.ShouZhiService;
import com.eshop.helper.DateHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.model.Supplier;
import com.eshop.service.Manager;
import com.jfinal.plugin.activerecord.Record;

public class ShouZhiController extends AdminBaseController {

	public void many() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer supplierId = getParaToIntegerDefault("supplierId");
		String supplierName = getPara("supplierName");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		
		if (startTime == null) {
			startTime = DateHelper.firstDay();
		}
		if (endTime == null) {
			endTime = DateHelper.lastDay();
		}
		
		List<Record> data = ShouZhiService.findShouZhiItems(offset, length, startTime, endTime, supplierId, supplierName);
		List<Record> list = ShouZhiService.findShouZhiItems(startTime, endTime, supplierId, supplierName);
		Record statistics = ShouZhiService.calculateShouZhiItems(list);
		int total = list.size();
		
		Supplier supplier = null;
		if (supplierId != null) {
			supplier = Manager.getSupplier(supplierId);
		}
		String strSupplierName = (supplier != null) ? supplier.getName() : "";
		
		jsonObject.put("data", data);
		jsonObject.put("list", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("startTime", startTime);
		jsonObject.put("endTime", endTime);
		jsonObject.put("strSupplierName", strSupplierName);
		jsonObject.put("totalNoSend", statistics.getBigDecimal("totalNoSend"));
		jsonObject.put("totalSending", statistics.getBigDecimal("totalSending"));
		jsonObject.put("totalConfirmed", statistics.getBigDecimal("totalConfirmed"));
		jsonObject.put("totalRefund", statistics.getBigDecimal("totalRefund"));
		jsonObject.put("totalBack", statistics.getBigDecimal("totalBack"));
		jsonObject.put("totalBalance", statistics.getBigDecimal("totalBalance"));
		renderJson(jsonObject);
	}
	
	public void export() {
		Integer supplierId = getParaToIntegerDefault("supplierId");
		String supplierName = getPara("supplierName");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		
		if (startTime == null) {
			startTime = DateHelper.firstDay();
		}
		if (endTime == null) {
			endTime = DateHelper.lastDay();
		}
		
		List<Record> list = ShouZhiService.findShouZhiItems(startTime, endTime, supplierId, supplierName);
		Record statistics = ShouZhiService.calculateShouZhiItems(list);
		
		Supplier supplier = null;
		if (supplierId != null) {
			supplier = Manager.getSupplier(supplierId);
		}
		String strSupplierName = (supplier != null) ? supplier.getName() : "";

		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("startTime", startTime);
		searchMap.put("endTime", endTime);
		searchMap.put("strSupplierName", strSupplierName);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "shouzhiList_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportShouZhi(fileName, list, statistics, searchMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
}
