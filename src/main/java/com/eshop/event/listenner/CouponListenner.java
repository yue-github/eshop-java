package com.eshop.event.listenner;

import java.sql.SQLException;
import java.util.List;

import com.eshop.coupon.CashCouponService;
import com.eshop.coupon.CouponService;
import com.eshop.coupon.CustomerCouponService;
import com.eshop.coupon.DiscountCouponService;
import com.eshop.event.EventEnum;
import com.eshop.event.param.CouponParam;
import com.eshop.log.Log;
import com.eshop.model.Coupon;
import com.eshop.model.CustomerCoupon;
import com.eshop.model.Order;
import com.eshop.service.Member;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class CouponListenner implements EventListenner {

	@Override
	public void Process(EventEnum event, Object param1) {
		switch (event) {
		case EVENT_SUBMIT_ORDER:
			if (param1 instanceof CouponParam) {
				CouponParam couponParam = (CouponParam) param1;
				int customerId = couponParam.getCustomerId();
				int couponId = couponParam.getCouponId();
				String theSameOrderNum = couponParam.getTheSameOrderNum();
				List<Record> products = couponParam.getProducts();
				
				// 处理优惠券
				handleCoupon(customerId, couponId, theSameOrderNum, products);
			}
			break;
		}
	}
	
	/**
	 * 处理优惠券
	 * @param couponId
	 * @param theSameOrderNum
	 * @param products
	 */
	private void handleCoupon(int customerId, int couponId, String theSameOrderNum, List<Record> products) {
		Coupon coupon = Coupon.dao.findById(couponId);
		
		// 优惠券为空不做处理
		if (coupon == null) {
			return;
		}
		
		// 判断该优惠券是否合理
		if (!isLegal(customerId, coupon, products)) {
			return;
		}
		
		// 判断该优惠券是全场优惠券还是店铺优惠券
		int scope = coupon.getScope();
		
		switch (scope) {
		case CouponService.SCOPE_ALL:
			handleScopeAll(customerId, theSameOrderNum, coupon, products);
			break;
		case CouponService.SCOPE_SHOP:
			handleScopeShop(customerId, theSameOrderNum, coupon, products);
			break;
		}
	}
	
	private void handleScopeShop(final int customerId, final String theSameOrderNum, final Coupon coupon, final List<Record> products) {
		final double discount = calculateDiscount(coupon, products);
		
		final List<Order> orders = Order.dao.find("select * from `order` where theSameOrderNum = ? and shop_id = ?", theSameOrderNum, coupon.getShopId());
		
		handleOrder(customerId, coupon, orders, discount);
	}
	
	private void handleScopeAll(final int customerId, final String theSameOrderNum, final Coupon coupon, final List<Record> products) {
		final double discount = calculateDiscount(coupon, products);
		
		final List<Order> orders = Order.dao.find("select * from `order` where theSameOrderNum = ?", theSameOrderNum);
		
		handleOrder(customerId, coupon, orders, discount);
	}
	
	private void handleOrder(final int customerId, final Coupon coupon, final List<Order> orders, final double discount) {
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					int couponId = coupon.getId();
					double total = Member.calculateTotalActualProductPrice(orders);
					for (Order order : orders) {
						double totalActualProductPrice = order.getTotalActualProductPrice().doubleValue();
						double couponDiscount = totalActualProductPrice / total * discount;
						double deliveryPrice = order.getDeliveryPrice().doubleValue() - couponDiscount;
						order.set("couponDiscount", couponDiscount);
						order.set("couponId", couponId);
						order.set("deliveryPrice", deliveryPrice);
						order.update();
					}
					
					CustomerCoupon customerCoupon = CustomerCoupon.dao.findFirst("select * from customer_coupon where cusotmerId = ? and couponId = ?", customerId, couponId);
					customerCoupon.setIsUsed(CustomerCouponService.USED);
					customerCoupon.update();
				} catch (Exception e) {
					Log.error(e.getMessage() + ",提交订单时处理优惠券错误");
					return false;
				}
				return true;
			}
		});
	}
	
	/**
	 * 计算优惠金额
	 * @param coupon
	 * @param products
	 * @return
	 */
	private double calculateDiscount(Coupon coupon, List<Record> products) {
		int couponId = coupon.getId();
		int type = coupon.getType();
		
		CouponService couponService;
		
		if (CouponService.COUPON_CASH == type) {
			couponService = new CashCouponService();
			return couponService.calculateDiscount(couponId, products);
		} else {
			couponService = new DiscountCouponService();
			return couponService.calculateDiscount(couponId, products);
		}
		
	}
	
	/**
	 * 判断优惠券是否合法
	 * @param customerId
	 * @param coupon
	 * @param products
	 * @return
	 */
	private boolean isLegal(int customerId, Coupon coupon, List<Record> products) {
		List<Coupon> coupons = CouponService.coupons(customerId, products);
		
		boolean isContain = false;
		
		for (Coupon item : coupons) {
			int id = item.getId();
			if (id == coupon.getId()) {
				isContain = true;
				break;
			}
		}
		
		return isContain;
	}
	
}
