package com.eshop.controller.admin;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.excel.ExportSupplierOrderList;
import com.eshop.excel.ExportSupplierOrderSummary;
import com.eshop.finance.ExcelService;
import com.eshop.finance.SupplierFinanceService;
import com.eshop.helper.DateHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.logistics.CustomerAddress;
import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;
import com.eshop.model.Supplier;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 供应商管理
 * @author TangYiFeng
 */
public class SupplierController extends AdminBaseController {
	
    /**
     * Default constructor
     */
    public SupplierController() {
    }
    
    /**
     * 获取供应商
     * @param token 帐户访问口令（必填）
     * @param offset 页码
     * @param length 每页显示条数
     * @param name
     * @param contactPerson
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, recordsFiltered: 过滤后总数, data: [{id:id, name: 名称, contactPerson:联系人姓名, phone1:联系电话,type:供应商类型(1公司，2个人), created_at:创建时间,updated_at:修改时间}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String name = getPara("name");
    	String contactPerson = getPara("contactPerson");
    	String phone1 = getPara("phone1");
    	Integer type = getParaToIntegerDefault("type");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> data = Manager.findSupplierItems(offset, length, name, phone1, null, type, contactPerson, orderByMap);
    	List<Record> list = Manager.findSupplierItems(name, phone1, null, type, contactPerson, orderByMap);
    	int total = list.size();
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", data);
    	jsonObject.put("list", list);
    	renderJson(jsonObject);
    }
    
    public void exportSupplierList() {
    	String name = getPara("name");
    	String contactPerson = getPara("contactPerson");
    	String phone1 = getPara("phone1");
    	Integer type = getParaToIntegerDefault("type");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	String strType = getSupplierType(type);
    	Map<String, String> searchMap = new HashMap<String, String>();
    	searchMap.put("name", name);
    	searchMap.put("contactPerson", contactPerson);
    	searchMap.put("phone1", phone1);
    	searchMap.put("type", strType);
    	
    	List<Record> list = Manager.findSupplierItems(name, phone1, null, type, contactPerson, orderByMap);
    	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "供应商列表" + dateFormat.format(new Date());
		String path = ExcelHelper.exportSupplierList(fileName, list, searchMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
    }
    
    private String getSupplierType(Integer type) {
    	String strType = "";
    	if (type != null && type == 1) {
    		strType = "个人性质";
    	} else if (type != null && type == 2) {
    		strType = "公司性质";
    	}
    	return strType;
    }
    
    /**
     * 导出供应商
     */
    public void exportSupplier() {
    	String name = getPara("name");
    	String contactPerson = getPara("contactPerson");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Manager.findSupplierItems(name, null, null, null, contactPerson, orderByMap);
    	
    	Map<String, String> headers = ExcelService.suppliers();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "supplierOrderList_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportSuppliers(fileName, headers, list);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
    }
    
    /**
     * 创建供应商
     * @param token 帐户访问口令（必填）
     * @param name 供应商名称(必填)
     * @param contactPerson 联系人姓名
     * @param province_id
     * @param city_id
     * @param district_id
     * @param street
     * @param zipcode
     * @param type 公司类型(1公司，2个人)(必填)
     * @param phone1
     * @param phone1Type
     * @param phone2
     * @param phone2Type
     * @param phone3
     * @param phone3Type
     * @param note
     * @param bankName
     * @param createProvince
     * @param createCtiy
     * @param createBank
     * @param createName
     * @param bankAccount
     * @param legalPerson
     * @param fixedTelephone
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"name", "contactPerson", "province_id", "city_id", "district_id", "street", "type", 
    				"legalPerson", "bankName", "bankAccount", "createBank", "createName", "createProvince", "createCity"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String name = getPara("name");
    	int type = getParaToInt("type");
    	String contactPerson = getPara("contactPerson");
    	int provinceId = (getPara("province_id") != null) ? getParaToInt("province_id") : 0;
    	int cityId = (getPara("city_id") != null) ? getParaToInt("city_id") : 0;
    	int districtId = (getPara("district_id") != null) ? getParaToInt("district_id") : 0;
    	String street = getPara("street");
    	String zipcode = getPara("zipcode");
    	String phone1 = getPara("phone1");
    	String phone1Type = getPara("phone1Type");
    	String phone2 = getPara("phone2");
    	String phone2Type = getPara("phone2Type");
    	String phone3 = getPara("phone3");
    	String phone3Type = getPara("phone3Type");
    	String note = getPara("note");
    	String bankName = getPara("bankName");
    	String createProvince = getPara("createProvince");
    	String createCity = getPara("createCity");
    	String createBank = getPara("createBank");
    	String createName = getPara("createName");
    	String bankAccount = getPara("bankAccount");
    	String legalPerson = getPara("legalPerson");
    	String fixedTelephone = getPara("fixedTelephone");
    	
    	Supplier model = new Supplier();
    	model.setName(name);
    	model.setContactPerson(contactPerson);
    	model.setProvinceId(provinceId);
    	model.setCityId(cityId);
    	model.setDistrictId(districtId);
    	model.setStreet(street);
    	model.setZipcode(zipcode);
    	model.setType(type);
    	model.setPhone1(phone1);
    	model.setPhone1Type(phone1Type);
    	model.setPhone2(phone2);
    	model.setPhone2Type(phone2Type);
    	model.setPhone3(phone3);
    	model.setPhone3Type(phone3Type);
    	model.setNote(note);
    	model.setBankName(bankName);
    	model.setCreateProvince(createProvince);
    	model.setCreateCity(createCity);
    	model.setCreateBank(createBank);
    	model.setCreateName(createName);
    	model.setBankAccount(bankAccount);
    	model.setLegalPerson(legalPerson);
    	model.setFixedTelephone(fixedTelephone);
    	
    	ServiceCode result = Manager.isExetSupplierName(name);
    	if (result == ServiceCode.Validation) {
    		returnError(ErrorCode.Validation, "该供应商名称已存在");
    		return;
    	}
    	
    	ServiceCode code = Manager.createSupplier(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改供应商
     * @param token 帐户访问口令（必填）
     * @param id
     * @param name 供应商名称
     * @param contactPerson 联系人姓名
     * @param province_id
     * @param city_id
     * @param street
     * @param zipcode
     * @param type 公司类型(1公司，2个人)
     * @param phone1
     * @param phone1Type
     * @param phone2
     * @param phone2Type
     * @param phone3
     * @param phone3Type
     * @param note
     * @param bankName
     * @param createProvince
     * @param createCtiy
     * @param createBank
     * @param createName
     * @param bankAccount
     * @param legalPerson
     * @param fixedTelephone
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "name", "type"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String name = getPara("name");
    	int type = getParaToInt("type");
    	String contactPerson = getPara("contactPerson");
    	int provinceId = (getPara("province_id") != null) ? getParaToInt("province_id") : 0;
    	int cityId = (getPara("city_id") != null) ? getParaToInt("city_id") : 0;
    	int districtId = (getPara("district_id") != null) ? getParaToInt("district_id") : 0;
    	String street = getPara("street");
    	String zipcode = getPara("zipcode");
    	String phone1 = getPara("phone1");
    	String phone1Type = getPara("phone1Type");
    	String phone2 = getPara("phone2");
    	String phone2Type = getPara("phone2Type");
    	String phone3 = getPara("phone3");
    	String phone3Type = getPara("phone3Type");
    	String note = getPara("note");
    	String bankName = getPara("bankName");
    	String createProvince = getPara("createProvince");
    	String createCity = getPara("createCity");
    	String createBank = getPara("createBank");
    	String createName = getPara("createName");
    	String bankAccount = getPara("bankAccount");
    	String legalPerson = getPara("legalPerson");
    	String fixedTelephone = getPara("fixedTelephone");
    	
    	Supplier model = Manager.getSupplier(id);
    	model.setName(name);
    	model.setContactPerson(contactPerson);
    	model.setProvinceId(provinceId);
    	model.setCityId(cityId);
    	model.setDistrictId(districtId);
    	model.setStreet(street);
    	model.setZipcode(zipcode);
    	model.setType(type);
    	model.setPhone1(phone1);
    	model.setPhone1Type(phone1Type);
    	model.setPhone2(phone2);
    	model.setPhone2Type(phone2Type);
    	model.setPhone3(phone3);
    	model.setPhone3Type(phone3Type);
    	model.setNote(note);
    	model.setBankName(bankName);
    	model.setCreateProvince(createProvince);
    	model.setCreateCity(createCity);
    	model.setCreateBank(createBank);
    	model.setCreateName(createName);
    	model.setBankAccount(bankAccount);
    	model.setLegalPerson(legalPerson);
    	model.setFixedTelephone(fixedTelephone);
    	
    	ServiceCode code = Manager.updateSupplier(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个供应商
     * @param token 帐户访问口令（必填）
     * @param id
     * @return 成功：{error: 0,data:{shops:[{name:店铺名称},...],id:id, name: 名称, contactPerson:联系人姓名, phone1:联系电话,type:供应商类型(1公司，2个人), created_at:创建时间,updated_at:修改时间}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Supplier model = Manager.getSupplier(id);
    	
    	if (model != null) {
    		List<Record> list = Manager.getShopsBySupplierId(id, null);
    		model.put("shops", list);
		}
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 删除供应商
     * @param token 帐户访问口令（必填）
     * @param ids [1,2,...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void delete() {
    	String[] params = {"ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
		
    	ServiceCode code = Manager.batchDeleteSupplier(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 判断供应商是否存在
     * @param supplierName
     * @return 
     */
    public void checkSupplier() {
    	String[] params = {"supplierName"};
    	
    	if (!validate(params)) {
			return;
		}
    	
    	String supplierName;
		supplierName = getPara("supplierName");
		ServiceCode result = Manager.isExetSupplierName(supplierName);
		if (result == ServiceCode.Validation) {
			returnError(ErrorCode.Validation, "供应商已存在");
			return;
		}

    	renderJson(jsonObject);
    }
    
    /**
     * 判断账号是否存在
     * @param bankAccount
     * @return 
     */
    public void checkBankAccount() {
    	String[] params = {"bankAccount"};
    	
    	if (!validate(params)) {
			return;
		}
    	String bankAccount = getPara("bankAccount");
    	ServiceCode result = Manager.isExetBankAccount(bankAccount);
    	if (result == ServiceCode.Validation) {
			returnError(ErrorCode.Validation, "账号已存在");
			return;
		}
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有省
     * @return 成功：{error: 0, provinces: [{id: 省id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void provinces() {
    	List<SProvince> list = CustomerAddress.getAllProvince();
    	
    	jsonObject.put("provinces", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 根据省获取所有市
     * @param provinceId 省id（必填）
     * @return 成功：{error: 0, cities: [{id: 市id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void cities() {
    	String[] params = {"provinceId"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int provinceId = getParaToInt("provinceId");
    	List<SCity> citys = CustomerAddress.getCityByProvinceId(provinceId);
    	
    	jsonObject.put("cities", citys);
    	renderJson(jsonObject);
    }
    
    /**
     * 根据市获取所有区
     * @param cityId 市id(必填)
     */
    public void districts() {
    	String[] params = {"cityId"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int cityId = getParaToInt("cityId");
    	List<SDistrict> list = CustomerAddress.getDistrictByCityId(cityId);
    	
    	jsonObject.put("districts", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有供应商
     * @return @return 成功：{error: 0, data:[{id:1,name:供应商名称}]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void allSuppliers() {
    	List<Record> list = Manager.findSupplierItems(null, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 供应商对账明细表
     * @param token
     * @param offset 页码
     * @param length 每页显示条数
     * @param supplierId 供应商id 选填
     * @param supplierName
     * @param status
     * @param orderCode
     * @param tradeCode
     * @param tradeNo
     * @param expressCode
     * @param logisticsName
     * @param payType
     * @param source
     * @param productName
     * @param startTime 开始时间 选填
     * @param endTime 结束时间 选填
     * @return 成功：{error: 0,totalRefundAmount:总退款数量,totalSaleCost:总产品成本,totalSale:总销售金额,totalRefundCash:退款总金额,totalUnitOrdered:产品总数量, offset: 页码, totalRow: 总数, data: [{sendOutTime:发货时间, order_code: 订单号, expressCode:快递单号, logisticsName:快递名称,selectProterties:选择的属性, product_name:产品名称, pricingUnit:单位,unitOrdered:产品数量,totalProductCost:产品成本,totalPrice:销售金额,totalDeliveryCost:运费成本,totalActualDeliveryCharge:运费,note:备注,backAmount:退款数量,totalRefund:退款金额}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void supplierOrderList() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	Integer supplierId = getParaToIntegerDefault("supplierId");
    	String supplierName = getPara("supplierName");
    	Integer status = getParaToIntegerDefault("status");
    	String orderCode = getPara("orderCode");
    	String tradeCode = getPara("tradeCode");
    	String tradeNo = getPara("tradeNo");
    	String expressCode = getPara("expressCode");
    	String logisticsName = getPara("logisticsName");
    	Integer payType = null;
    	Integer source = getParaToInt("source");
    	String productName = getPara("productName");
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	
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
    	
    	List<Record> data = SupplierFinanceService.findSupplierDetailItems(offset, length, supplierId, status, supplierName, orderCode, tradeCode, tradeNo, productName, startTime, endTime, payType, source, expressCode, logisticsName, whereInPayType);
    	List<Record> list = SupplierFinanceService.findSupplierDetailItems(supplierId, status, supplierName, orderCode, tradeCode, tradeNo, productName, startTime, endTime, payType, source, expressCode, logisticsName, whereInPayType);
    	Record statistics = SupplierFinanceService.calculateSupplierDetailItems(list);
    	int total = list.size();
    	
    	Record supplier = SupplierFinanceService.getSupplier(supplierId);
    	String spName = (supplier != null) ? supplier.getStr("name") : "";
    	String spContactPerson = (supplier != null) ? supplier.getStr("contactPerson") : "";
    	String spPhone = (supplier != null) ? supplier.getStr("phone1") : "";
    	String spAccountPeriod = (supplier != null) ? supplier.getStr("account_period") : "";
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", data);
    	jsonObject.put("list", list);
    	jsonObject.put("spName", spName);
    	jsonObject.put("spContactPerson", spContactPerson);
    	jsonObject.put("spPhone", spPhone);
    	jsonObject.put("spAccountPeriod", spAccountPeriod);
    	jsonObject.put("totalRefundAmount", statistics.getInt("totalRefundAmount"));
    	jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
    	jsonObject.put("totalUnitOrdered", statistics.getInt("totalUnitOrdered"));
    	jsonObject.put("totalSaleCost", statistics.getDouble("totalSaleCost"));
    	jsonObject.put("totalSale", statistics.getDouble("totalSale"));
    	jsonObject.put("weixin", statistics.getDouble("weixin"));
    	jsonObject.put("weixinPc", statistics.getDouble("weixinPc"));
    	jsonObject.put("weixinApp", statistics.getDouble("weixinApp"));
    	jsonObject.put("alipay", statistics.getDouble("alipay"));
    	jsonObject.put("unionpay", statistics.getDouble("unionpay"));
    	jsonObject.put("balancepay", statistics.getDouble("balancepay"));
    	renderJson(jsonObject);
    }
    
    /**
     * 供应商对账明细表
     * @param token
     * @param offset 页码
     * @param length 每页显示条数
     * @param supplierId 供应商id 选填
     * @param supplierName
     * @param status
     * @param orderCode
     * @param tradeCode
     * @param tradeNo
     * @param expressCode
     * @param logisticsName
     * @param payType
     * @param source
     * @param productName
     * @param startTime 开始时间 选填
     * @param endTime 结束时间 选填
     * @return 成功：{error: 0,totalRefundAmount:总退款数量,totalSaleCost:总产品成本,totalSale:总销售金额,totalRefundCash:退款总金额,totalUnitOrdered:产品总数量, offset: 页码, totalRow: 总数, data: [{sendOutTime:发货时间, order_code: 订单号, expressCode:快递单号, logisticsName:快递名称,selectProterties:选择的属性, product_name:产品名称, pricingUnit:单位,unitOrdered:产品数量,totalProductCost:产品成本,totalPrice:销售金额,totalDeliveryCost:运费成本,totalActualDeliveryCharge:运费,note:备注,backAmount:退款数量,totalRefund:退款金额}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void allSupplierOrderList() {
    	Integer supplierId = getParaToIntegerDefault("supplierId");
    	String supplierName = getPara("supplierName");
    	Integer status = getParaToIntegerDefault("status");
    	String orderCode = getPara("orderCode");
    	String tradeCode = getPara("tradeCode");
    	String tradeNo = getPara("tradeNo");
    	String expressCode = getPara("expressCode");
    	String logisticsName = getPara("logisticsName");
    	Integer payType = null;
    	Integer source = getParaToInt("source");
    	String productName = getPara("productName");
    	
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	
    	if (!this.validateRequiredString("startTime")) {
			startTime = getParaToDateTimeDefault("startTime");
		}
    	if (!this.validateRequiredString("endTime")) {
			endTime = getParaToDateTimeDefault("endTime");
		}
    	
    	String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
    	
    	List<Record> list = SupplierFinanceService.findSupplierDetailItems(supplierId, status, supplierName, orderCode, tradeCode, tradeNo, productName, startTime, endTime, payType, source, expressCode, logisticsName, whereInPayType);
    	Record statistics = SupplierFinanceService.calculateSupplierDetailItems(list);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("totalRefundAmount", statistics.getInt("totalRefundAmount"));
    	jsonObject.put("totalUnitOrdered", statistics.getInt("totalUnitOrdered"));
    	jsonObject.put("totalSaleCost", statistics.getDouble("totalSaleCost"));
    	jsonObject.put("totalSale", statistics.getDouble("totalSale"));
    	jsonObject.put("totalDeliveryCost", statistics.getDouble("totalDeliveryCost"));
    	jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
    	renderJson(jsonObject);
    }
    
    /**
     * 导出供应商对账明细表
	 * @param token
     * @param supplierId 供应商id 选填
     * @param supplierName
     * @param status
     * @param orderCode
     * @param tradeCode
     * @param tradeNo
     * @param expressCode
     * @param logisticsName
     * @param payType
     * @param source
     * @param productName
     * @param startTime 开始时间 选填
     * @param endTime 结束时间 选填
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0,errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportSupplierOrderList() {
		Integer supplierId = getParaToIntegerDefault("supplierId");
    	String supplierName = getPara("supplierName");
    	Integer status = getParaToIntegerDefault("status");
    	String orderCode = getPara("orderCode");
    	String tradeCode = getPara("tradeCode");
    	String tradeNo = getPara("tradeNo");
    	String expressCode = getPara("expressCode");
    	String logisticsName = getPara("logisticsName");
    	Integer payType = null;
    	Integer source = getParaToInt("source");
    	String productName = getPara("productName");
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	
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
    	
    	Record supplier = SupplierFinanceService.getSupplier(supplierId);
    	String spName = (supplier != null) ? supplier.getStr("name") : "";
    	String spContactPerson = (supplier != null) ? supplier.getStr("contactPerson") : "";
    	String spPhone = (supplier != null) ? supplier.getStr("phone1") : "";
    	String spAccountPeriod = (supplier != null) ? supplier.getStr("account_period") : "";
    	spAccountPeriod = (spAccountPeriod != null) ? spAccountPeriod : "";
    	Map<String, String> supplierMap = new HashMap<String, String>();
    	supplierMap.put("spName", spName);
    	supplierMap.put("spContactPerson", spContactPerson);
    	supplierMap.put("spPhone", spPhone);
    	supplierMap.put("spAccountPeriod", spAccountPeriod);
    	
    	List<Record> list = SupplierFinanceService.findSupplierDetailItems(supplierId, status, supplierName, 
    			orderCode, tradeCode, tradeNo, productName, startTime, endTime, payType, source, 
    			expressCode, logisticsName, whereInPayType);
    	Record statistics = SupplierFinanceService.calculateSupplierDetailItems(list);
    	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "supplierOrderList_" + dateFormat.format(new Date());
		String path = ExportSupplierOrderList.exportSupplierOrderList(fileName, list, statistics, 
				startTime, endTime, supplierMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
    /**
     * 供应商对账汇总表
     * @param token
     * @param offset 页码
     * @param length 每页显示条数
     * @param startTime 开始时间  选填
     * @param endTime 结束时间 选填
     * @param supplier_id 供应商id 选填
     * @param supplierName 供应商名称 选填
     * @param status
     * @return 成功：{error: 0,totalBackAmount:退款总数量,totalSendAmount:发货总数量,totalRefund:退款总金额,totalProductCost:产品总成本,totalPrice:产品总金额,totalDeliveryCost:运费总成本,totalActualDeliveryCharge:总运费, offset: 页码, totalRow: 总数, data: [{sendOutTime：发货时间,supplier_name:供应商名称,unitOrdered:发货总数量,totalProductCost:产品总成本,totalPrice:产品总金额,totalDeliveryCost:运费总成本,totalActualDeliveryCharge:总运费,note:备注,backAmount:退款总数量,totalRefund:总退款金额}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void supplierOrderSummary() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length"); 
    	Integer supplierId = getParaToIntegerDefault("supplier_id");
    	Integer status = getParaToIntegerDefault("status");
    	String supplierName = getPara("supplierName");
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	if(getPara("startTime") != null && !getPara("startTime").equals("")) {
    		startTime = getPara("startTime");
    	}
    	if(getPara("endTime") != null && !getPara("endTime").equals("")) {
    		endTime = getPara("endTime");
    	}
    	
    	List<Record> data = SupplierFinanceService.findSupplierSummaryItems(offset, length, supplierId, supplierName, status, startTime, endTime);
    	List<Record> list = SupplierFinanceService.findSupplierSummaryItems(supplierId, supplierName, status, startTime, endTime);
    	int totalRow = list.size();
    	Record statistics = SupplierFinanceService.calculateSupplierSummaryItems(supplierId, supplierName, status, startTime, endTime);
    	
    	Record supplier = SupplierFinanceService.getSupplier(supplierId);
    	String spName = (supplier != null) ? supplier.getStr("name") : "";
    	String spContactPerson = (supplier != null) ? supplier.getStr("contactPerson") : "";
    	String spPhone = (supplier != null) ? supplier.getStr("phone1") : "";
    	String spAccountPeriod = (supplier != null) ? supplier.getStr("account_period") : "";
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("data", data);
    	jsonObject.put("list", list);
    	jsonObject.put("spName", spName);
    	jsonObject.put("spContactPerson", spContactPerson);
    	jsonObject.put("spPhone", spPhone);
    	jsonObject.put("spAccountPeriod", spAccountPeriod);
    	jsonObject.put("totalBackAmount", statistics.getInt("totalBackAmount"));
    	jsonObject.put("totalSendAmount", statistics.getInt("totalSendAmount"));
    	jsonObject.put("totalRefund", statistics.getDouble("totalRefund"));
    	jsonObject.put("totalProductCost", statistics.getDouble("totalProductCost"));
    	jsonObject.put("totalCost", statistics.getDouble("totalCost"));
    	jsonObject.put("totalPrice", statistics.getDouble("totalPrice"));
    	jsonObject.put("totalDeliveryCost", statistics.getDouble("totalDeliveryCost"));
    	jsonObject.put("totalActualDeliveryCharge", statistics.getDouble("totalActualDeliveryCharge"));
    	jsonObject.put("totalSalable", statistics.getDouble("totalSalable"));
    	jsonObject.put("totalActualDeliveryCharge", statistics.getDouble("totalActualDeliveryCharge"));
    	jsonObject.put("totalWeiXin", statistics.getDouble("totalWeiXin"));
    	jsonObject.put("totalWxApp", statistics.getDouble("totalWxApp"));
    	jsonObject.put("totalWxPc", statistics.getDouble("totalWxPc"));
    	jsonObject.put("totalAlipay", statistics.getDouble("totalAlipay"));
    	jsonObject.put("totalUnionPay", statistics.getDouble("totalUnionPay"));
    	jsonObject.put("totalWallet", statistics.getDouble("totalWallet"));
    	jsonObject.put("startTime", startTime);
    	jsonObject.put("endTime", endTime);
    	renderJson(jsonObject);
    }
    
    /**
     * 导出供应商对账汇总表
	 * @param token
     * @param supplierId 供应商id 选填
     * @param startTime 开始时间 选填
     * @param endTime 结束时间 选填
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0,errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportSupplierOrderSummary() {
		Integer supplierId = getParaToIntegerDefault("supplier_id");
    	Integer status = getParaToIntegerDefault("status");
    	status = status != null ? status : 2;
    	String supplierName = getPara("supplierName");
    	String startTime = DateHelper.firstDay();
    	String endTime = DateHelper.lastDay();
    	if(getPara("startTime") != null && !getPara("startTime").equals("")) {
    		startTime = getPara("startTime");
    	}
    	if(getPara("endTime") != null && !getPara("endTime").equals("")) {
    		endTime = getPara("endTime");
    	}
    	
    	List<Record> list = SupplierFinanceService.findSupplierSummaryItems(supplierId, supplierName, status, startTime, endTime);
    	Record statistics = SupplierFinanceService.calculateSupplierSummaryItems(supplierId, supplierName, status, startTime, endTime);
    	
    	Record supplier = SupplierFinanceService.getSupplier(supplierId);
    	String spName = (supplier != null) ? supplier.getStr("name") : "";
    	String spContactPerson = (supplier != null) ? supplier.getStr("contactPerson") : "";
    	String spPhone = (supplier != null) ? supplier.getStr("phone1") : "";
    	String spAccountPeriod = (supplier != null) ? supplier.getStr("account_period") : "";
    	spAccountPeriod = (spAccountPeriod != null) ? spAccountPeriod : "";
    	String spStatus = "";
    	if (status == 1) {
    		spStatus = "已支付";
    	} else if (status == 2) {
    		spStatus = "已发货";
    	} else if (status == 3) {
    		spStatus = "已收货";
    	}
    	Map<String, String> searchMap = new HashMap<String, String>();
    	searchMap.put("spName", spName);
    	searchMap.put("spContactPerson", spContactPerson);
    	searchMap.put("spPhone", spPhone);
    	searchMap.put("spAccountPeriod", spAccountPeriod);
    	searchMap.put("spStatus", spStatus);
    	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "supplierOrderSummary_" + dateFormat.format(new Date());
		String path = ExportSupplierOrderSummary.exportSupplierOrderSummary(fileName, list, startTime, endTime, statistics, searchMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
}