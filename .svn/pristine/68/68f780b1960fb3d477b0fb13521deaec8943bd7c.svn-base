package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.invoice.OrderInvoiceService;
import com.eshop.model.Customer;
import com.eshop.model.Order;
import com.eshop.model.ProductOrder;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 我的订单控制器
 * @author TangYiFeng
 */
public class MyOrdersController extends PcBaseController {

    /**
     * Default constructor
     */
    public MyOrdersController() {
    }

    /**
     * 我的订单列表
     * @param token 帐户访问口令（必填）
     * @param offset 当前页，用于分页
     * @param length
     * @param status 订单状态（-1为全部）
     * @param order_code 订单号
     * @return 成功：{error: 0, totalPages:总页数， orders: [{id: 订单id, order_code: 订单号, created_at: 下单时间,details: [{name产品名, mainPic主图,size规格，package包装，num个数,price价钱,totalPayable总价}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @SuppressWarnings("unlikely-arg-type")
	@Before(CustomerPcAuthInterceptor.class)
    public void myOrders() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	Integer status = getParaToIntegerDefault("status");
    	status = status != null ? status : -1;
    	String orderCode = getPara("order_code");
    	String startTime = getPara("startTime");
    	String endTime = getPara("endTime");
    	String token = getPara("token");
    	
    	HashSet<String> set = new HashSet();
    	set.add(orderCode);
    	set.add(token);
    	set.add(startTime);
    	set.add(endTime);
    	String matche_str = "'|\"|>|..|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|script|frame|;|or|-|+|,|)|etc|style|expression";
    	String[] strArr = matche_str.split("\\|");
    	for (int i=0 ; i < strArr.length ; i++ ){
    		if (set.add(strArr[i])) {
    			System.out.println(true + "不存在");
    		} else {
    			System.out.println(false + "存在");
    			return;
    		}
		}
    	
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("o.created_at", "desc");
    	
    	List<Record> list = User.findOrderItems(offset, length, customerId, null, orderCode, null, null, status, startTime, endTime, null, null, null, null, null, null, null, orderByMap);
    	int total = User.countOrderItems(customerId, null, orderCode, null, null, status, startTime, endTime, null, null, null, null, null, null);
    	
    	for (Record item : list) {
    		List<Record> details = User.findProductOrderItems(item.getInt("id"), null, null);
    		item.set("productOrders", details);
    	}
    	
    	jsonObject.put("orders", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 订单详情
     * @param token 帐户访问口令（必填）
     * @param id 订单ID（必填）
     * @return 成功：{error: 0, order: {id: 订单id, order_code: 订单号, created_at: 下单时间, details: [{name产品名, mainPic主图,size规格，package包装，num个数,price价钱}。。。。}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void get() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Order order = User.getOrder(id);
    	Record orderInvoice = OrderInvoiceService.getOrderInvoice(order.getId());
    	String invoiceContent = (orderInvoice != null) ? orderInvoice.getStr("invoiceContent") : "";
    	String invoiceType = "";
    	if (orderInvoice != null) {
    		int type = orderInvoice.getInt("type");
    		if (type == 1) {
    			invoiceType = "普通发票";
    		} else if (type == 2) {
    			invoiceType = "电子发票";
    		} else if (type == 3) {
    			invoiceType = "增值税发票";
    		}
    	}
    	order.put("invoiceContent", invoiceContent);
    	order.put("invoiceType", invoiceType);
    	
    	Customer customer = Member.getCustomer(order.getCustomerId());
    	List<ProductOrder> details = Member.getProductOrder(order.getId());
    	order.put("details", details);
    	
    	jsonObject.put("customer", customer);
    	jsonObject.put("order", order);
    	renderJson(jsonObject);
    }
    
    /**
     * 确认收货
     *  @param token 帐户访问口令（必填）
     *  @param id 订单ID（必填）
     *   @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void comfirmGet() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int orderId = getParaToInt("id");
    	
    	ServiceCode code = Member.confirm(orderId);
    	
    	if(code != ServiceCode.Success) {
    		jsonObject.put("errmsg", "确认收货失败");
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 取消订单
     * @param token 帐户访问口令（必填）
     * @param orderId 订单ID（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void cancel() {
    	if(!this.validateRequiredString("orderId")) {
    		return;
    	}
    	int orderId = getParaToInt("orderId");
    	
    	if(Member.cancel(orderId, "", "") != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "cancel failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改订单支付方式
     * @param token 必填
     * @param orderId 订单id 必填
     * @param payType 新的支付方式 必填
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void editPaytype() {
    	String[] params = {"orderId", "payType"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int orderId = getParaToInt("orderId");
    	int payType = getParaToInt("payType");
    	
    	ServiceCode code = Member.updatePayType(orderId, payType);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改支付方式失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}