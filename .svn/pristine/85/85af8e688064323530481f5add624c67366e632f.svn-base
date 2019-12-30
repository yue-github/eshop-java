package com.eshop.event.listenner;

import com.eshop.event.EventEnum;
import com.eshop.event.param.CustomerParam;
import com.eshop.event.param.OrderParam;
import com.eshop.membership.MemberShip;
import com.eshop.model.Customer;
import com.eshop.model.Order;

public class PointListenner implements EventListenner {

	@Override
	public void Process(EventEnum event, Object param1) {
		switch (event) {
		case EVENT_REGISTER:
			if (param1 instanceof CustomerParam) {
				Customer customer = ((CustomerParam) param1).getCustomer();
				MemberShip.gainPoint(customer.getId(), MemberShip.REGISTER_POINT, customer.getId(), 1);
			}
			break;
		case EVENT_UPDATE_INFO:
			if (param1 instanceof CustomerParam) {
				Customer customer = ((CustomerParam) param1).getCustomer();
				MemberShip.gainPoint(customer.getId(), MemberShip.UPDATE_INFO_POINT, customer.getId(), 1);
			}
			break;
		case EVENT_INVITE_REGISTER:
			if (param1 instanceof CustomerParam) {
				Customer customer = ((CustomerParam) param1).getCustomer();
				MemberShip.gainPoint(customer.getId(), MemberShip.INVITE_REGISTER_POINT, customer.getId(), 1);
			}
			break;
		case EVENT_SIGN:
			if (param1 instanceof CustomerParam) {
				Customer customer = ((CustomerParam) param1).getCustomer();
				MemberShip.gainPoint(customer.getId(), MemberShip.SIGN_IN_POINT, customer.getId(), 1);
			}
			break;
		case EVENT_SUBMIT_COMMENT:
			if (param1 instanceof CustomerParam) {
				Customer customer = ((CustomerParam) param1).getCustomer();
				MemberShip.gainPoint(customer.getId(), MemberShip.SUBMIT_COMMENT_POINT, customer.getId(), 1);
			}
			break;
		case EVENT_CLICK_ADV:
			if (param1 instanceof CustomerParam) {
				Customer customer = ((CustomerParam) param1).getCustomer();
				MemberShip.gainPoint(customer.getId(), MemberShip.CLICK_ADV_POINT, customer.getId(), 1);
			}
			break;
		case EVENT_SHARE_LINK:
			if (param1 instanceof CustomerParam) {
				Customer customer = ((CustomerParam) param1).getCustomer();
				MemberShip.gainPoint(customer.getId(), MemberShip.SHARE_LINK_POINT, customer.getId(), 1);
			}
			break;
		case EVENT_BUY_SEPECILATY:
			if (param1 instanceof OrderParam) {
				Order order = ((OrderParam) param1).getOrder();
				if (order.getPayType() == 4) {
					MemberShip.gainPoint(order.getCustomerId(), MemberShip.BUY_SPECIALTY_WALLETPAY_POINT, order.getId(), order.getTotalPayable().doubleValue());
				} else {
					MemberShip.gainPoint(order.getCustomerId(), MemberShip.BUY_SPECIALTY_THIRDPAY_POINT, order.getId(), order.getTotalPayable().doubleValue());
				}
			}
			break;
		case EVENT_BUY_TRAVEL:
			if (param1 instanceof OrderParam) {
				Order order = ((OrderParam) param1).getOrder();
				if (order.getPayType() == 4) {
					MemberShip.gainPoint(order.getCustomerId(), MemberShip.BUY_TRAVEL_WALLETPAY_POINT, order.getId(), order.getTotalPayable().doubleValue());
				} else {
					MemberShip.gainPoint(order.getCustomerId(), MemberShip.BUY_TRAVEL_THIRDPAY_POINT, order.getId(), order.getTotalPayable().doubleValue());
				}
			}
			break;
		}
	}
	
}
