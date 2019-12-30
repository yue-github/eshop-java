package com.eshop.controller.pc;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.PointProduct;
import com.eshop.model.Product;
import com.eshop.point.PointService;
import com.eshop.service.Member;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

public class PointController extends PcBaseController {
	
	private PointService service = new PointService();
	private Member member = new Member();
	
	/**
	 * 积分产品
	 */
	public void product_list() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer productId = null;
		String isShow = "是";
		Boolean isValid = true;
		
		List<Record> list = service.list(offset, length, productId, isShow, isValid);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	public void pointProduct() {
		String[] params = {"productId"};
		if (!this.validate(params)) {
			return;
		}
		int productId = getParaToInt("productId");
		
		PointProduct data = PointProduct.dao.findFirst("select * from point_product where product_id = " + productId);
		jsonObject.put("data", data);
		renderJson(jsonObject);
	}
	
	/**
	 * 提交订单
	 */
	@Before(CustomerPcAuthInterceptor.class)
    public void saveOrder() {
    	String[] params = {"address_id", "source", "payType", "id", "priceId", "amount", "pointProductId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int addressId = getParaToInt("address_id");
    	int source = getParaToInt("source");
    	int payType = getParaToInt("payType");
    	int productId = getParaToInt("id");
    	int priceId = getParaToInt("priceId");
    	int amount = getParaToInt("amount");
    	int pointProductId = getParaToInt("pointProductId");
    	String startAt = null;
    	String endAt = null;
    	int couponId = 0;
    	if (getPara("couponId") != null && !getPara("couponId").equals("")) {
    		couponId = getParaToInt("couponId");
    	}
    	
    	Product product = Product.dao.findById(productId);
    	int address_type = getPara("address_type") != null && !getPara("address_type").equals("") ? getParaToInt("address_type") : 1;
    	Record other = new Record();
    	other.set("address_id", addressId);
    	other.set("source", source);
    	other.set("payType", payType);
    	other.set("couponId", couponId);
    	other.set("order_type", product.getProdType());
    	other.set("note", getPara("note"));
    	other.set("address_type", address_type);
    	other.set("contacts", getPara("contacts"));
    	other.set("phone", getPara("phone"));
    	
    	if (getPara("invoice") != null) {
    		String invoiceStr = getPara("invoice");
    		JSONObject object = JSON.parseObject(invoiceStr);
    		Record invoice = new Record();
    		invoice.set("type", object.getInteger("type"));
    		invoice.set("invoiceHead", object.getString("invoiceHead"));
    		invoice.set("invoiceContent", object.getInteger("invoiceContent"));
    		other.set("invoice", invoice);
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Record result = member.submitOrderWithPoint(pointProductId, customerId, productId, priceId, amount, other, startAt, endAt);
    	int error = result.getInt("error");
    	
    	if (error == -1) {
    		returnError(error, "积分不足");
    		return;
    	} else if (error == 1) {
    		returnError(error, "提交订单失败");
    		return;
    	}
    	
    	jsonObject.put("theSameOrderNum", result.getStr("theSameOrderNum"));
    	jsonObject.put("orderCcode", "");
    	jsonObject.put("orderId", 0);
    	jsonObject.put("payType", 4);
    	jsonObject.put("payInfo", "");
    	renderJson(jsonObject);
    }
	
	@Before(CustomerPcAuthInterceptor.class)
	public void checkPoint(){
		String[] params = {"pointProductId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int pointProductId = getParaToInt("pointProductId");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	boolean success = member.checkPointAmount(customerId, pointProductId);
    	
    	if (!success) {
    		returnError(ErrorCode.Exception, "积分不足");
    		return;
    	}
    	
    	renderJson(jsonObject);
	}
	
}
