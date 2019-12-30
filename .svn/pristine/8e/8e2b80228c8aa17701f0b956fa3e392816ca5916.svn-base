package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.invoice.InvoiceRecordService;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 全局控制器
 * @author TangYiFeng
 */
public class InvoiceController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public InvoiceController() {
    }
    
    /**
     * 发票列表
     * @param offset
     * @param length
     * @param orderId
     * @param type
     * @param invoiceHead
     * @param orderCode
     * @param invoiceCode
     * @param theSameOrderNum
     * @param startTime
     * @param endTime
     * @return 成功：{error:0, offset:页码, totalRow:总数, data:[...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	Integer orderId = getParaToInt("orderId");
    	Integer type = getParaToInt("type");
    	String invoiceHead = getPara("invoiceHead");
    	String invoiceContent = getPara("invoiceContent");
    	Double moneyMoreThan = getParaToDoubleDefault("moneyMoreThan");
    	Double moneyLessThan = getParaToDoubleDefault("moneyLessThan");
    	String orderCode = getPara("orderCode");
    	String invoiceCode = getPara("invoiceCode");
    	String theSameOrderNum = getPara("theSameOrderNum");
    	String startTime = getParaToDateTimeDefault("startTime");
    	String endTime = getParaToDateTimeDefault("endTime");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> list = InvoiceRecordService.findInvoiceRecordItems(offset, length, orderId, type, invoiceHead, orderCode, startTime, endTime, invoiceCode,invoiceContent, theSameOrderNum,moneyLessThan,moneyMoreThan, orderByMap);    				
    	int total = InvoiceRecordService.countInvoiceRecordItems(orderId, type, invoiceHead, orderCode, startTime, endTime, invoiceCode,invoiceContent, theSameOrderNum,moneyLessThan,moneyMoreThan);    	
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取发票详情
     * @param id
     * @return 成功：{error:0, data:{invoiceCode:发票编码,money:发票金额,created_at:开票时间,type:发票类型(1普通发票,2电子发票,3增值税发票),invoiceHead:发票抬头,invoiceContent:发票内容,name:收票人姓名,phone:收票人手机,email:电话,proviceName:省,cityName:市,districtId:区,addressDetail:详细地址,companyName:公司名称,companyCode:纳税人编码,companyAddress:公司地址,companyPhone:公司电话,bankName:开户银行,bankAccount:银行账号,orderCode:订单号,totalPayable:订单总金额}}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void getInvoiceRecord() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record record = InvoiceRecordService.get(id);
    	
    	jsonObject.put("data", record);
    	renderJson(jsonObject);
    }
    
    /**
     * 后台  获取发票基本信息
     * @param id
     * @return 成功：{error:0, data:{type:发票类型(1普通发票,2电子发票,3增值税发票),invoiceHead:发票抬头,invoiceContent:发票内容,name:收票人姓名,phone:收票人手机,email:电话,proviceName:省,cityName:市,districtId:区,addressDetail:详细地址,companyName:公司名称,companyCode:纳税人编码,companyAddress:公司地址,companyPhone:公司电话,bankName:开户银行,bankAccount:银行账号,orderCode:订单号,totalPayable:订单总金额}}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void getOrderInvoice() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record record = InvoiceRecordService.get(id);
    	
    	jsonObject.put("data", record);
    	renderJson(jsonObject);
    }
    
    /**
     * 开发票
     * @param orderId 订单id
     * @param invoiceCode 发票编码
     * @param money 发票金额
     * @return 成功：{error:0}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void createInvoiceRecord() {
    	String[] params = {"orderId", "invoiceCode", "money"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int orderId = getParaToInt("orderId");
    	String invoiceCode = getPara("invoiceCode");
    	BigDecimal money = getParaToDecimal("money");
    	
    	ServiceCode code = InvoiceRecordService.create(invoiceCode, money, orderId);
    	
    	if (code != ServiceCode.Success) {
    		jsonObject.put("errmsg", "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 某个订单的发票列表
     * @param orderId 订单id
     * @return 成功：{error:0,data:[{id:id,invoiceCode:发票编码,money:发票金额},...]}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void getOrderInvoiceRecords() {
    	String[] params = {"orderId"};
    	
    	if (!validate(params)) {
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
    @Before(AdminAuthInterceptor.class)
    public void deleteOrderInvoiceRecord() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = InvoiceRecordService.delete(id);
    	
    	if (code != ServiceCode.Success) {
    		jsonObject.put("errmsg", "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}