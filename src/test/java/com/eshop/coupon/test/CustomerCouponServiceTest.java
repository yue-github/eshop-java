package com.eshop.coupon.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.coupon.CustomerCouponService;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;

public class CustomerCouponServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private CustomerCouponService customerCouponService = new CustomerCouponService();

    /**
     * 删除用户优惠券
     * 算法：如果该优惠券已被使用，则不允许删除
     * @param id 
     * @return
     */
	@Test
	public void testDeleteCustomerCoupon() {
		ServiceCode count = CustomerCouponService.deleteCustomerCoupon(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 判断优惠券是否已过期
     * @param customerId
     * @param couponId
     * @return
     */
	@Test
	public void testIsExpired() {
		boolean count = CustomerCouponService.isExpired(1);

		assertEquals(false, count);
	}
    
    /**
     * 优惠券是否已被领完
     * @param couponId
     * @return
     */
	@Test
	public void testIsUseUp() {
		boolean count = CustomerCouponService.isUseUp(1);

		assertEquals(false, count);
	}
    
    /**
     * 用户是否已领取过该优惠券
     * @param customerId
     * @param couponId
     * @return
     */
	@Test
	public void testIsReceivedIntInt() {
		boolean count = CustomerCouponService.isReceived(1, 1);

		assertEquals(false, count);
	}
	   
    /**
     * 用户是否已领取过该优惠券
     * @param phone
     * @param couponId
     * @return
     */
	@Test
	public void testIsReceivedStringInt() {
		boolean count = CustomerCouponService.isReceived("123434565", 1);

		assertEquals(false, count);
	}

    /**
     * 点击领取优惠券
     * @param customerId
     * @return -1失败，0成功，1已过期，2已被领完，3已领取
     */
	@Test
	public void testReceiveCouponIntInt() {
		int count = CustomerCouponService.receiveCoupon(1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

    /**
     * 手机领取优惠券
     * @param phone 
     * @return -1失败，0成功，1已过期，2已被领完，3已领取
     */
	@Test
	public void testReceiveCouponStringInt() {
		int count = CustomerCouponService.receiveCoupon("", 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
