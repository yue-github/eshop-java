package com.eshop.event.param;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;

public class CouponParam extends EventParam {
	
	private int customerId;
	private int couponId;
	private String theSameOrderNum;
	private List<Record> products;
	
	public int getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
	public int getCouponId() {
		return couponId;
	}
	
	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}
	
	public String getTheSameOrderNum() {
		return theSameOrderNum;
	}
	
	public void setTheSameOrderNum(String theSameOrderNum) {
		this.theSameOrderNum = theSameOrderNum;
	}
	
	public List<Record> getProducts() {
		return products;
	}
	
	public void setProducts(List<Record> products) {
		this.products = products;
	}

}
