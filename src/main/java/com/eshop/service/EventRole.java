package com.eshop.service;

import com.eshop.event.EventEnum;
import com.eshop.event.EventManager;
import com.eshop.event.listenner.CouponListenner;
import com.eshop.event.listenner.GoldListenner;
import com.eshop.event.listenner.GrowthListenner;
import com.eshop.event.listenner.InvoiceListenner;
import com.eshop.event.listenner.LogisticsListenner;
import com.eshop.event.listenner.PointListenner;
import com.eshop.event.listenner.PromotionListenner;
import com.eshop.event.listenner.TaxListenner;

public class EventRole {
	
	private static EventManager eventManager = new EventManager();
	private static EventRole instance = null;
	
	public static EventRole instance() {
		if (instance == null) {
			instance = new EventRole();
		}
		return instance;
	}

	private EventRole() {
		System.out.println("eventRole构造函数");
		LogisticsListenner logisticsListenner = new LogisticsListenner();
		TaxListenner taxListenner = new TaxListenner();
		InvoiceListenner invoiceListenner = new InvoiceListenner();
		CouponListenner couponListenner = new CouponListenner();
		PromotionListenner promotionListenner = new PromotionListenner();
		GoldListenner goldListenner = new GoldListenner();
		GrowthListenner growthListenner = new GrowthListenner();
		PointListenner pointListenner = new PointListenner();
		
		eventManager.AddListenner(EventEnum.EVENT_SUBMIT_ORDER, logisticsListenner);
		eventManager.AddListenner(EventEnum.EVENT_DISPATCH, logisticsListenner);
		eventManager.AddListenner(EventEnum.EVENT_CALCULATE_DELIVERY, logisticsListenner);
		eventManager.AddListenner(EventEnum.EVENT_SUBMIT_ORDER, taxListenner);
		eventManager.AddListenner(EventEnum.EVENT_SUBMIT_ORDER, invoiceListenner);
		eventManager.AddListenner(EventEnum.EVENT_SUBMIT_ORDER, promotionListenner);
		eventManager.AddListenner(EventEnum.EVENT_SUBMIT_ORDER, couponListenner);
		eventManager.AddListenner(EventEnum.EVENT_SHOP_CART, promotionListenner);
		

		eventManager.AddListenner(EventEnum.EVENT_SUBMIT_ORDER, goldListenner);
		eventManager.AddListenner(EventEnum.EVENT_BUY_TRAVEL, goldListenner);
		eventManager.AddListenner(EventEnum.EVENT_BUY_SEPECILATY, goldListenner);
		
		eventManager.AddListenner(EventEnum.EVENT_BUY_TRAVEL, growthListenner);
		eventManager.AddListenner(EventEnum.EVENT_BUY_SEPECILATY, growthListenner);
		
		eventManager.AddListenner(EventEnum.EVENT_BUY_TRAVEL, pointListenner);
		eventManager.AddListenner(EventEnum.EVENT_BUY_SEPECILATY, pointListenner);
	}
	
	/**
	 * 触发事件
	 * @param event
	 * @param p1
	 */
	public void dispatch(EventEnum event, Object p1) {
		eventManager.RaiseEvent(event, p1);
	}
	
}
