package com.eshop.controller.pc;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.LogisticsHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.invoice.PlainInvoiceService;
import com.eshop.membership.CustomerGoldService;
import com.eshop.model.Customer;
import com.eshop.model.PlainInvoice;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 购买流程控制器
 *   @author TangYiFeng
 */
public class OrderController extends PcBaseController {

    /**
     * Default constructor
     */
    public OrderController() {
    }

    /**
     * 加入购物车
     * @param id 产品id（必填）
     * @param priceId 价格id（必填）
     * @param amount 数量（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void addCart() {
    	String[] params = {"id", "priceId", "amount"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	if (getParaToInt("amount") < 1) {
    		returnError(-1, "购物车数量不能小于1");
    		return;
    	}
    	
    	int productId = getParaToInt("id");
    	int priceId = getParaToInt("priceId");
    	int amount = getParaToInt("amount");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	if(Member.addShoppingCart(customerId, productId, priceId, amount) != ServiceCode.Success) {
    		jsonObject.put("error", 1);
        	renderJson(jsonObject);
    	}
    	
    	jsonObject.put("error", 0);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取我的金币
     * @param token（必填）
     * @return 成功：{error:0, golds: 金币数量, goldDiscount: 可抵扣金额} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getMyGold() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	
    	int golds = customer.getGolds();
    	double goldDiscount = CustomerGoldService.getGoldDiscount(golds);
    	
    	jsonObject.put("golds", golds);
    	jsonObject.put("goldDiscount", goldDiscount);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取金币的抵扣金额
     * @param token
     * @param golds 金币数量(必填)
     * @return 成功：{error:0, goldDiscount: 金币优惠金额} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getGoldDiscount() {
    	if (!this.validateRequiredString("golds")) {
    		return;
    	}
    	int golds = getParaToInt("golds");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int remainGolds = customer.getGolds();
    	
    	if (golds > remainGolds) {
    		this.returnError(ErrorCode.Exception, "剩余金币不足");
    		return;
    	}
    	
    	double goldDiscount = CustomerGoldService.getGoldDiscount(golds);
    	jsonObject.put("goldDiscount", goldDiscount);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取发票抬头
     * @param token
     * @return 成功：{error:0, data:[id:id, invoiceName:发票抬头]} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getPlainInvoice() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = PlainInvoiceService.findPlainInvoiceItems(customerId, null, 2);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 保存发票抬头，创建或修改
     * @param id 选填
     * @param invoiceHead 发票抬头 必填
     * @param companyCode 纳税编号 选填
     */
    public void savePlainInvoice() {
    	String[] params = {"invoiceHead"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = 0;
    	if (getPara("id") != null && !getPara("id").equals("")) {
    		id = getParaToInt("id");
    	}
    	int type = 2;
    	if (getPara("type") != null && !getPara("type").equals("")) {
    		type = getParaToInt("type");
    	}
    	int invoiceContent = 1;
    	if (getPara("invoiceContent") != null && !getPara("invoiceContent").equals("")) {
    		invoiceContent = getParaToInt("invoiceContent");
    	}
    	
    	String invoiceHead = getPara("invoiceHead");
    	String companyCode = getPara("companyCode");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	PlainInvoice model = new PlainInvoice();
    	model.setId(id);
    	model.setInvoiceHead(invoiceHead);
    	model.setInvoiceContent(invoiceContent);
    	model.setCompanyCode(companyCode);
    	model.setType(type);
    	model.setCustomerId(customerId);
    	
    	ServiceCode code;
    	if (id == 0) {
    		code = PlainInvoiceService.create(model);
    	} else {
    		code = PlainInvoiceService.update(model);
    	}
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 添加发票抬头
     * @param token
     * @param invoiceHead 发票抬头
     * @param type
     * @param invoiceContent
     * @param companyCode 税号
     * @return 成功：{error:0} 失败：{error: >0, errmsg:错误信息}
     */
	@Before(CustomerPcAuthInterceptor.class)
    public void createInvoice() {
    	if (!this.validateRequiredString("invoiceHead")) {
    		return;
    	}
    	String invoiceHead = getPara("invoiceHead");
    	
    	String companyCode = getPara("companyCode");
    	
    	if (!this.validateRequiredString("type")) {
    		return;
    	}
    	int type = getParaToInt("type");
    	
    	if (!this.validateRequiredString("invoiceContent")) {
    		return;
    	}
    	int invoiceContent = getParaToInt("invoiceContent");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	PlainInvoice model = new PlainInvoice();
    	model.setCustomerId(customerId);
    	model.setInvoiceHead(invoiceHead);
    	model.setCompanyCode(companyCode);
    	model.setType(type);
    	model.setInvoiceContent(invoiceContent);
    	
    	ServiceCode code = PlainInvoiceService.create(model);
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "添加失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改发票抬头
     * @param token
     * @param id 发票抬头id
     * @param invoiceHead 发票抬头
     * @param companyCode 税号
     * @return 成功：{error:0} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void updateInvoice() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if (!this.validateRequiredString("invoiceHead")) {
    		return;
    	}
    	String invoiceHead = getPara("invoiceHead");
    	
    	if (!this.validateRequiredString("companyCode")) {
    		return;
    	}
    	String companyCode = getPara("companyCode");
    	
    	PlainInvoice model = PlainInvoiceService.get(id);
    	model.setInvoiceHead(invoiceHead);
    	model.setCompanyCode(companyCode);
    	
    	ServiceCode code = PlainInvoiceService.update(model);
    	
    	if (code != ServiceCode.Success) {
    		jsonObject.put("errmsg", "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除普通发票
     * @param token
     * @param id 发票抬头id
     * @return 成功：{error:0} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void deleteInvoice() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	ServiceCode code = PlainInvoiceService.delete(id);
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "删除失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 快递回调请求
     * @throws IOException 
     * 
     */
    public void logisticsCallback() throws IOException {
    	HttpServletRequest request = this.getRequest();
    	LogisticsHelper.notifyHandle(request.getParameter("param"));
		
		jsonObject.put("result", true);
		jsonObject.put("returnCode", 200);
		jsonObject.put("message", "success成功");
		renderJson(jsonObject);
    }
    
}