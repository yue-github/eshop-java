package com.eshop.coupon.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.coupon.CashCouponService;
import com.eshop.service.Member;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class CashCouponServiceTest  extends ControllerTestCase<TestProjectConfig> {
	
	private CashCouponService cashCouponService = new CashCouponService();

    /**
     * 计算优惠金额
     * @param id 
     * @param totalPayable
     */
	@Test
	public void testCalculateDiscountIntDouble() {
		double count = cashCouponService.calculateDiscount(49,2000);
		boolean flag = count > 0;
		System.out.println(flag);
		assertEquals(true, flag);
	}

	/**
     * 计算优惠金额
     * @param couponId
     * @param products
     * @return
     */
	@Test
	public void testCalculateDiscount() {
		List<Record> products = new ArrayList<>();
		Record record = Member.getProduct(1).toRecord();
		products.add(record);
		double count = cashCouponService.calculateDiscount(49, products);
	}
}