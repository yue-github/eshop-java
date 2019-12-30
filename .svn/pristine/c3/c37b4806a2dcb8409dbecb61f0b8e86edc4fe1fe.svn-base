package com.eshop.order.test;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.ext.test.ControllerTestCase;

public class ConfirmOrderTest extends ControllerTestCase<TestProjectConfig> {
	
	/**
	 * 加入购物车
	 */
	@Test
	public void testAddCart() {
//		String url = "/pc/order/addCart?id=82&priceId=1&amount=11";
//		String msg = use(url).invoke();
//		System.out.println(msg);
		
		int productId = 82;
    	int priceId = 13673;
    	int amount = 11;
    	
    	int customerId = 12;
    	ServiceCode code = Member.addShoppingCart(customerId, productId, priceId, amount);
    	
    	if(code != ServiceCode.Success) {
    		System.out.println("aaa");
    	}
	}
	
	/**
	 * 提交订单 - 购物车
	 */
	@Test
	public void testSubmitOrderByCart() {
		String url = "/pc/comfirmOrder/saveOrderByCart?address_id=1&source=2&payType=4&couponId=1";
		String msg = use(url).invoke();
		System.out.println(msg);
	}
	
	/**
     * 提交订单 - 直接购物
	 */
	@Test
	public void testSubmitOrder() {
		
		String url = "/pc/comfirmOrder/saveOrder?address_id=1&source=2&payType=4&couponId=1&priceId=1&productId=1&amount=1000";
		String msg = use(url).invoke(); 
		System.out.println(msg);
		
	}
	
	/**
	 * 
	 */
	/*@Test
	public void testBeforeSumbit() {
    	int customerId = 12;
    	int isSelected = 1;
    	
    	List<Record> list = Member.deliveryOrdersByShopCart(customerId);
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, isSelected);
    	
    	//每个待提交的订单添加运费
    	List<Record> shopList = Member.addAllPriceAndFreight(list);
	}*/
	
}
