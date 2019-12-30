package com.eshop.controller.pc;

import java.util.*;


import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.invoice.EletronicInvoiceService;
import com.eshop.invoice.InvoiceRecordService;
import com.eshop.invoice.OrderInvoiceService;
import com.eshop.invoice.VatInvoiceService;
import com.eshop.model.Customer;
import com.eshop.model.EletronicInvoice;
import com.eshop.model.VatInvoice;
import com.eshop.model.VatInvoiceItem;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 发票控制器
 * @author TangYiFeng
 */
public class InvoiceController extends PcBaseController {
	
    /**
     * Default constructor
     */
    public InvoiceController() {
    }

    /**
     * 保存增值发票公司信息
     * @param companyName 公司名称
     * @param companyCode 纳税人识别码
     * @param companyAddress 公司注册地址
     * @param companyPhone 注册电话
     * @param bankName 开户银行
     * @param bankAccount 银行账户
     * @return 成功：{error:0}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void saveInvoiceInfo() {
    	String[] params = {"companyName", "companyCode", "companyAddress", "companyPhone", 
    			"bankName", "bankAccount", "invoiceContent"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String companyName = getPara("companyName");
    	String companyCode = getPara("companyCode");
    	String companyAddress = getPara("companyAddress");
    	String companyPhone = getPara("companyPhone");
    	String bankName = getPara("bankName");
    	String bankAccount = getPara("bankAccount");
    	Integer invoiceContent = getParaToInt("invoiceContent");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	VatInvoice model = new VatInvoice();
    	model.setCustomerId(customerId);
    	model.setCompanyName(companyName);
    	model.setCompanyCode(companyCode);
    	model.setCompanyAddress(companyAddress);
    	model.setCompanyPhone(companyPhone);
    	model.setBankName(bankName);
    	model.setBankAccount(bankAccount);
    	model.setInvoiceContent(invoiceContent);
    	
    	ServiceCode code = VatInvoiceService.saveVatInvoice(model);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取增值发票公司信息
     * @param token
     * @return 成功：{error:0,data:{companyName:公司名称,companyCode:纳税人编码,companyAddress:公司地址,companyPhone:注册电话,bankName:开户银行,bankAccount:银行账号}}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getInvoiceInfo() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Record model = VatInvoiceService.get(customerId);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }

    /**
     * 保存增值发票个人信息
     * @param name 姓名
     * @param phone 手机
     * @param provinceId 省id
     * @param cityId 市id
     * @param districtId 区id
     * @param addressDetail 详细地址
     * @return 成功：{error:0}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void saveInvoicePerson() {
    	String[] params = {"name", "phone", "provinceId", "cityId", "districtId", "addressDetail"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String name = getPara("name");
    	String phone = getPara("phone");
    	int provinceId = getParaToInt("provinceId");
    	int cityId = getParaToInt("cityId");
    	int districtId = getParaToInt("districtId");
    	String addressDetail = getPara("addressDetail");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	VatInvoiceItem model = new VatInvoiceItem();
    	model.setName(name);
    	model.setPhone(phone);
    	model.setProvinceId(provinceId);
    	model.setCityId(cityId);
    	model.setDistrictId(districtId);
    	model.setAddressDetail(addressDetail);
    	model.setCustomerId(customerId);
    	
    	ServiceCode code = VatInvoiceService.saveVatInvoice(model);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取电子发票信息
     * @param type 发票类型
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getEletronicInvoice() {
    	String[] params = {"type"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int type = getParaToInt("type");
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	EletronicInvoice model = EletronicInvoiceService.getEletronicInvoice(type, customerId);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 保存电子发票
     * @param invoiceHead
     * @param type
     * @param companyCode 税号（选填）
     * @param email
     * @param phone
     * @return 成功：{error:0}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void saveInvoiceEmail() {
    	String[] params = {"invoiceHead", "type", "email", "phone", "invoiceContent"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String invoiceHead = getPara("invoiceHead");
    	int type = getParaToInt("type");
    	String email = getPara("email");
    	String phone = getPara("phone");
    	int invoiceContent = getParaToInt("invoiceContent");
    	String companyCode = getPara("companyCode");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = EletronicInvoiceService.save(invoiceHead, customerId, type, companyCode, phone, 
    			email, invoiceContent);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "创建电子发票失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 开发票
     * @param orderId 订单id
     * @param invoiceCode 发票编码
     * @param money 发票金额
     * @return 成功：{error:0}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void createInvoiceRecord() {
    	String[] params = {"orderId", "invoices"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int orderId = getParaToInt("orderId");
    	String invoices = getPara("invoices");
    	ServiceCode code = InvoiceRecordService.create(orderId, invoices);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 某个订单的发票列表
     * @param orderId 订单id
     * @return 成功：{error:0,data:[{id:id,invoiceCode:发票编码,money:发票金额},...]}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getOrderInvoiceRecords() {
    	if (!this.validateRequiredString("orderId")) {
    		return;
    	}
    	int orderId = getParaToInt("orderId");
    	
    	List<Record> list = InvoiceRecordService.findInvoiceRecordItems(orderId, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 删除某个发票
     * @param id
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void deleteOrderInvoiceRecord() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	ServiceCode code = InvoiceRecordService.delete(id);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取发票详情
     * @param id
     * @return 成功：{error:0, data:{invoiceCode:发票编码,money:发票金额,created_at:开票时间,type:发票类型(1普通发票,2电子发票,3增值税发票),invoiceHead:发票抬头,invoiceContent:发票内容,name:收票人姓名,phone:收票人手机,email:电话,proviceName:省,cityName:市,districtId:区,addressDetail:详细地址,companyName:公司名称,companyCode:纳税人编码,companyAddress:公司地址,companyPhone:公司电话,bankName:开户银行,bankAccount:银行账号,orderCode:订单号,totalPayable:订单总金额}}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getInvoiceRecord() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Record record = InvoiceRecordService.get(id);
    	
    	jsonObject.put("data", record);
    	renderJson(jsonObject);
    }
    
    /**
     * 商家获取发票基本信息
     * @param id
     * @return 成功：{error:0, data:{type:发票类型(1普通发票,2电子发票,3增值税发票),invoiceHead:发票抬头,invoiceContent:发票内容,name:收票人姓名,phone:收票人手机,email:电话,proviceName:省,cityName:市,districtId:区,addressDetail:详细地址,companyName:公司名称,companyCode:纳税人编码,companyAddress:公司地址,companyPhone:公司电话,bankName:开户银行,bankAccount:银行账号,orderCode:订单号,totalPayable:订单总金额}}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getOrderInvoice() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Record record = OrderInvoiceService.getOrderInvoice(id);
    	
    	jsonObject.put("data", record);
    	renderJson(jsonObject);
    }
    
}