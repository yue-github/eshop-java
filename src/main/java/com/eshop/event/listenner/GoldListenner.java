package com.eshop.event.listenner;

import com.eshop.event.EventEnum;
import com.eshop.event.param.OrderParam;
import com.eshop.membership.MemberShip;
import com.eshop.model.Order;

public class GoldListenner implements EventListenner {

	@Override
	public void Process(EventEnum event, Object param1) {
		switch (event) {
		case EVENT_REGISTER:
			break;
		case EVENT_UPDATE_INFO:
			break;
		case EVENT_INVITE_REGISTER:
			break;
		case EVENT_SIGN:
			break;
		case EVENT_SUBMIT_COMMENT:
			break;
		case EVENT_CLICK_ADV:
			break;
		case EVENT_SHARE_LINK:
			break;
		case EVENT_SUBMIT_ORDER:
			//消费金币
			if (param1 instanceof OrderParam) {
				Order order = ((OrderParam) param1).getOrder();
				MemberShip.expanseGold(order.getCustomerId(), order.getGold(), MemberShip.GOLD_EXCHNAGE, order.getId(), "消费抵扣");
			}
			break;
		case EVENT_BUY_SEPECILATY:
			//获取金币
			if (param1 instanceof OrderParam) {
				Order order = ((OrderParam) param1).getOrder();
				if (order.getPayType() == 4) {
					MemberShip.gainGold(order.getCustomerId(), MemberShip.BUY_SPECIALTY_WALLETPAY_GROWTH, order.getId(), order.getTotalPayable().doubleValue());
				} else {
					MemberShip.gainGold(order.getCustomerId(), MemberShip.BUY_SPECIALTY_THIRDPAY_GROWTH, order.getId(), order.getTotalPayable().doubleValue());
				}
			}
			break;
		case EVENT_BUY_TRAVEL:
			//获取金币
			if (param1 instanceof OrderParam) {
				Order order = ((OrderParam) param1).getOrder();
				if (order.getPayType() == 4) {
					MemberShip.gainGold(order.getCustomerId(), MemberShip.BUY_TRAVEL_WALLETPAY_GROWTH, order.getId(), order.getTotalPayable().doubleValue());
				} else {
					MemberShip.gainGold(order.getCustomerId(), MemberShip.BUY_TRAVEL_THIRDPAY_GROWTH, order.getId(), order.getTotalPayable().doubleValue());
				}
			}
			break;
		}
	}

}
