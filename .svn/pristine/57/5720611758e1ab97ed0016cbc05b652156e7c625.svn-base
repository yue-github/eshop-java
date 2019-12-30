package com.eshop.coupon;

import java.util.*;

import com.eshop.model.Coupon;
import com.jfinal.plugin.activerecord.Record;

/**
 * 现金券
 */
public class CashCouponService extends CouponService {

    /**
     * 计算优惠金额
     * @param id
     * @param totalPayable
     * @return
     */
    @Override
	public double calculateDiscount(int id, double totalPayable) {
        Coupon coupon = Coupon.dao.findById(id);
        double full = coupon.getFull().doubleValue();
        double value = coupon.getValue().doubleValue();
        
        if (totalPayable >= full) {
			return value;
		} else {
			return 0;
		}
    }
    
    /**
     * 计算优惠金额
     * @param couponId
     * @param products
     * @return
     */
	@Override
	public double calculateDiscount(int couponId, List<Record> products) {
		double totalPayable = getCouponTotalPayable(couponId, products);
		
		return calculateDiscount(couponId, totalPayable);
	}

}