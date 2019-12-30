package com.eshop.coupon.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.coupon.DiscountCouponService;
import com.jfinal.ext.test.ControllerTestCase;

public class DiscountCouponServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private DiscountCouponService discountCouponService = new DiscountCouponService();

    /**
     * 计算优惠金额
     * @param id 
     * @param totalPayable 
     * @return
     */
	@Test
	public void testCalculateDiscount() {
		double count = discountCouponService.calculateDiscount(1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
