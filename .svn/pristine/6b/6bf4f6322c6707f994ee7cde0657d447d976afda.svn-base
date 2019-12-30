package com.eshop.order.test;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.jfinal.ext.test.ControllerTestCase;

public class CartControllerTest extends ControllerTestCase<TestProjectConfig> {
	
	@Test
	public void testGetMyOrderListBeforeSubmit() {
		String url = "/pc/cart/getMyOrderListBeforeSubmit";
		String msg = use(url).invoke();
		System.out.println(msg);
	}

}
