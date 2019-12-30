package com.eshop.coupon.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.coupon.CouponService;
import com.eshop.coupon.DiscountCouponService;
import com.eshop.model.Coupon;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class CouponServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private CouponService couponService = new DiscountCouponService();
	
    /**
     * 计算优惠金额
     * @param couponId 
     * @param totalPayable
     */
	@Test
	public void testCalculateDiscount() {
		double count = couponService.calculateDiscount(1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 查看该产品参与了哪些优惠券活动
     * 算法：1、先找出所有优惠券；2、再逐个筛选出符合条件的优惠券
     * @param productId
     */
	@Test
	public void testGetCouponSlogans() {
		List<Coupon> count = CouponService.getCouponSlogans(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 下单时满足优惠条件的优惠券
     * 算法：1、找出用户已领取的优惠券（未使用并在有效期之内）；2、遍历集合，找出符合条件的优惠券
     * @param products [{product_id:1,amount:3,price:8},...], 整个订单的产品列表
     */
	@Test
	public void testCoupons() {
		java.util.List<Coupon> count = CouponService.coupons(1, new ArrayList<Record>(2));
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 批量查询优惠券列表
     */
	@Test
	public void testFindCouponItemsIntIntStringStringStringStringStringIntegerIntegerIntegerDoubleDoubleDoubleDoubleIntegerStringIntegerIntegerString() {
		java.util.List<com.jfinal.plugin.activerecord.Record> count = CouponService.findCouponItems(10, 5, "1", "1", "1", "1", "1", 1, 1, 1, 0.5, 0.4, 0.3, 0.2, 1, "1", 1, 1, "1");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	 /**
     * 批量查询优惠券列表
     */
	@Test
	public void testFindCouponItemsStringStringStringStringStringIntegerIntegerIntegerDoubleDoubleDoubleDoubleIntegerStringIntegerIntegerString() {
		java.util.List<com.jfinal.plugin.activerecord.Record> count = CouponService.findCouponItems("1", "1", "1", "1", "1", 1, 1, 1, 0.1, 0.1, 0.1, 0.1, 1, "1", 1, 1, "1");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 给优惠券添加扩展字段
     * @param coupons
     */
	@Test
	public void testFindCouponItemsListOfRecord() {
		java.util.List<com.jfinal.plugin.activerecord.Record> count = CouponService.findCouponItems(new ArrayList<Record>(2));
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 批量查询优惠券列表的总条数
     */
	@Test
	public void testCountCouponItems() {
		int count = CouponService.countCouponItems("", "", "", "", "", 1, 1, 1, 0.1, 0.1, 0.1, 0.1, 1, "", 1, 1, "");
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
   /**
     * 组装sql语句
     */
	@Test
	public void testFindCouponItemsSql() {
		String count = CouponService.findCouponItemsSql("", "", "", "", "", 1, 1, 1, 0.1, 0.1, 0.1, 0.1, 1, "", 1, 1, "");
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 查看优惠券详情
     * @param id 
     */
	@Test
	public void testGetCoupon() {
		Coupon count = CouponService.getCoupon(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 创建优惠券
     * @param model 
     * @return
     */
	@Test
	public void testCreateCoupon() {
		Coupon model = new Coupon();
		model.set("2", "5");
		ServiceCode count = CouponService.createCoupon(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 修改优惠券
     * @param model 
     * @return
     */
	@Test
	public void testUpdateCoupon() {
		Coupon model = new Coupon();
		model.set("2", "5");
		ServiceCode count = CouponService.updateCoupon(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除优惠券
     * 算法：如果该优惠券已被领取或使用，则不允许删除；如果还没被领取或使用，则允许删除
     * @param id 
     */
	@Test
	public void testDeleteCoupon() {
		ServiceCode count = CouponService.deleteCoupon(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
