package com.eshop.controller.webapp;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eshop.groupcard.GroupSetMealService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.service.Member;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

public class GroupActivityController extends WebappBaseController {
	
	private GroupSetMealService groupSetMealService = new GroupSetMealService();
	
	/**
	 * 套餐列表
	 */
	public void set_meal_list() {
		String[] params = {"group_activity_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int groupActivityId = getParaToInt("group_activity_id");
		List<Record> data = groupSetMealService.all(groupActivityId, "", "是");
		
		jsonObject.put("data", data);
		renderJson(jsonObject);
	}
	
	/**
	 * 套餐详情
	 */
	public void set_meal_detail() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		Record data = groupSetMealService.detail(getParaToInt("id"));
		
		jsonObject.put("data", data);
		renderJson(jsonObject);
	}
	
	/**
	 * 订单列表
	 */
	public void orderList() {
		String[] params = {"group_set_meal_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int groupSetMealId = getParaToInt("group_set_meal_id");
		List<Record> list = groupSetMealService.orderList(groupSetMealId, 0);
		List<Record> products = groupSetMealService.productList(groupSetMealId);
		
		double totalProdPrice = Member.calculateProductTotalPayable(products);
    	double totalAmount = Member.calculateProductTotalAmount(products);
    	double totalPromDiscount = 0;
    	double totalCouponDiscount = 0;
    	double deliveryPrice = 0;
    	double totalPayable = totalProdPrice + deliveryPrice - totalPromDiscount - totalCouponDiscount;

    	jsonObject.put("data", list);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("totalPromDiscount", totalPromDiscount);
    	jsonObject.put("totalCouponDiscount", totalCouponDiscount);
    	jsonObject.put("deliveryPrice", deliveryPrice);
    	jsonObject.put("totalPayable", totalPayable);
    	renderJson(jsonObject);
	}
	
	/**
	 * 提交订单
	 */
	@Before(CustomerWebAppAuthInterceptor.class)
	public void submitOrder() {
		String[] params = {"group_set_meal_id", "address_id", "source", "payType", "card_code", "card_password"};
		if (!this.validate(params)) {
			return;
		}
		
		String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
		int groupSetMealId = getParaToInt("group_set_meal_id");
		String cardCode = getPara("card_code");
		String cardPassword = getPara("card_password");
		
		Record other = new Record();
		other.set("address_id", getParaToInt("address_id"));
    	other.set("source", getParaToInt("source"));
    	other.set("payType", getParaToInt("payType"));
    	other.set("couponId", 0);
    	other.set("order_type", 0);
    	other.set("note", getPara("note"));
    	
    	if (getPara("invoice") != null) {
    		String invoiceStr = getPara("invoice");
    		JSONObject object = JSON.parseObject(invoiceStr);
    		
    		Record invoice = new Record();
    		invoice.set("type", object.getInteger("type"));
    		invoice.set("invoiceHead", object.getString("invoiceHead"));
    		invoice.set("invoiceContent", object.getInteger("invoiceContent"));
    		
    		other.set("invoice", invoice);
    	}
    	
    	int code = groupSetMealService.submitOrder(groupSetMealId, customerId, cardCode, cardPassword, other);
    	if (code != 0) {
    		returnError(code, "提交订单失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
	}
}
