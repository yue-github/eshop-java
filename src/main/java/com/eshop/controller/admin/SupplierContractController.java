package com.eshop.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.SupplierContract;
import com.eshop.model.SupplierPeriod;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.supplier.SupplierPeriodService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 供应商合同管理
 * @author LiuJiaFu
 *
 */
public class SupplierContractController extends AdminBaseController {
	/**
	 * Default constructor
	 */
	public SupplierContractController() {}
	
	
	/**
	 * 获取所有供应商合同
	 * @param adminToken
	 * @param offset
	 * @param length
	 */
	@Before(AdminAuthInterceptor.class)
	public void many() {
		
		String[] params = {"offset", "length"};
		
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	Integer contractId = getParaToIntegerDefault("contractId");
    	Integer supplierId = getParaToIntegerDefault("supplier_id");
    	Integer accountPeriod = getParaToIntegerDefault("account_period");
    	
    	Map<String, String> constractByMap = new HashMap<String, String>();
    	constractByMap.put("start_at", "desc");
    	
    	List<Record> list = Manager.findContractItems(offset, length, contractId, supplierId, accountPeriod);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
	}
	
	/**
     * 获取某个供应商合同
     * @param token
     * @param id
     * @return
     */
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		SupplierContract model = Manager.getSupplierContract(id);
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	
	/**
	 * 根据供应商id获取合同
	 * @param supplierId
	 */
	@Before(AdminAuthInterceptor.class)
	public void getContract() {
		String[] params = {"supplierId"};
		
		if (!validate(params)) {
			return;
		}
		
		int supplierId = getParaToInt("supplierId");
		
		List<Record> list = Manager.getContractBySupplierId(supplierId);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	/**
	 * 创建供应商合同
	 * @param supplier_id
	 * @param account_period
	 * @param start_at
	 * @param end_at
	 * @param filePath 合同文件路径
	 */
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"supplier_id", "account_period", "contract_file", "start_at", "end_at"};
    	
    	if (!validate(params)) {
    		return;
    	}
		
		int supplierId = getParaToInt("supplier_id");
		String accountPeriod = getPara("account_period");
		Date startAt = getParaToDate("start_at");
		Date endAt = getParaToDate("end_at");
		String filePath = getPara("contract_file");
		
		SupplierContract model = new SupplierContract();
		model.setSupplierId(supplierId);
		model.setAccountPeriod(accountPeriod);
		model.setContractFile(filePath);
		model.setStartAt(startAt);
		model.setEndAt(endAt);
		
		ServiceCode code = Manager.createSupplierContract(model);
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "创建失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 修改供应商合同
	 * @param id
	 * @param supplier_id
	 * @param account_period
	 * @param start_at
	 * @param end_at
	 * @param contract_file 合同文件路径
	 */
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "supplier_id", "account_period", "contract_file", "start_at", "end_at"};
    	
    	if (!validate(params)) {
    		return;
    	}
		
    	int id = getParaToInt("id");
		int supplierId = getParaToInt("supplier_id");
		String accountPeriod = getPara("account_period");
		Date startAt = getParaToDate("start_at");
		Date endAt = getParaToDate("end_at");
		String filePath = getPara("contract_file");
		
		SupplierContract model = Manager.getSupplierContract(id);
		model.setSupplierId(supplierId);
		model.setAccountPeriod(accountPeriod);
		model.setContractFile(filePath);
		model.setStartAt(startAt);
		model.setEndAt(endAt);
		
		ServiceCode code = Manager.updateSupplierContract(model);
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "创建失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 获取所有账期
	 * @param token 必填
	 */
	@Before(AdminAuthInterceptor.class)
	public void allSupplierPeriod() {
		List<SupplierPeriod> list = SupplierPeriodService.all();
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		for (SupplierPeriod item : list) {
			String value = item.getType() + "," + item.getDays();
			String label = "";
			if (item.getType().equals("day")) {
				label = "日结";
			} else if (item.getType().equals("month")) {
				label = "月结";
			}
			label += item.getDays() + "天";
			
			if (item.getType().equals("month") && item.getDays() == 0) {
				label = "当月结";
			}
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("label", label);
			map.put("value", value);
			data.add(map);
		}
		
		jsonObject.put("data", data);
		renderJson(jsonObject);
	}
	
}
