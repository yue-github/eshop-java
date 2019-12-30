package com.eshop.emain.test;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Shop;
import com.eshop.service.Member;
import com.jfinal.ext.test.ControllerTestCase;

public class TestProductComment extends ControllerTestCase<TestProjectConfig> {

	@Test
	public void run() {
		Integer productOrderId = 1732;
		Shop shop = Member.getShopByproductOrderId(productOrderId);
    	System.out.println(shop.getPhone()+shop.getEmail());
		Member.informShop(shop, "【乐驿商城】尊敬的商家您好，您有一条订单产品评价信息，请前往查看", "【乐驿商城】买家评价提醒", "【乐驿商城】尊敬的商家您好，您有一条订单产品评价信息，请前往查看");
    	
	}
}
