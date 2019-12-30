package com.eshop.event.listenner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.eshop.event.EventEnum;
import com.eshop.event.param.OrderParam;
import com.eshop.helper.LogisticsHelper;
import com.eshop.logistics.Logistics;
import com.eshop.model.Address;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.promotion.BaoYou;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class LogisticsListenner implements EventListenner {

	@Override
	public void Process(EventEnum event, Object param1) {
		switch (event) {
		case EVENT_DISPATCH:
			if (param1 instanceof OrderParam) {
				Order order = ((OrderParam) param1).getOrder();
				handleDispatch(order);
			}
			break;
		case EVENT_CALCULATE_DELIVERY:
			if (param1 instanceof OrderParam) {
				Order order = ((OrderParam) param1).getOrder();
				List<Record> products = ((OrderParam) param1).getProducts();
				calculateFreight(order, products);
			}
			break;
		}
	}
	
	/**
	 * 处理发货事件
	 * @param order
	 */
	private void handleDispatch(Order order) {
		int expressSubscribeStatus = order.getExpressSubscribeStatus();
    	//发送快递订阅请求
    	if (expressSubscribeStatus == 0) {
    		int status = LogisticsHelper.postOrder(order.getLogisticsNum(), order.getExpressCode());
    		Db.update("update `order` set expressSubscribeStatus = ? where id = ?", status, order.getId());
    	}
	}
	
	/**
	 * 计算订单运费
	 * @param order
	 */
	private void calculateFreight(final Order order, List<Record> products) {
		Logistics logistics = new Logistics();
		List<Record> orderProducts = getProds(order);
		
		Address address = Address.dao.findById(order.getAddressId());
		List<Record> templatesFreight = logistics.getTemplatesFreight(orderProducts, address.getProvinceId(), address.getCityId());
		
		double deliveryPrice = logistics.getTotalTemplateFreight(templatesFreight);
		boolean isAllFree = BaoYou.isMeetAllFree(orderProducts);
		
		if (isAllFree) {
			deliveryPrice = 0;
		} else {
			List<Record> promotions = BaoYou.meetShopFree(products);
			for (Record item : promotions) {
				if (item.getInt("shop_id") == order.getShopId()) {
					deliveryPrice = 0;
					break;
				}
			}
		}
		
		BigDecimal bigDeliveryPrice = BigDecimal.valueOf(deliveryPrice);
		
		Db.update("update `order` set totalPayable = totalPayable + ?, deliveryPrice = ? where id = ?", bigDeliveryPrice, bigDeliveryPrice, order.getId());
	}
	
	private List<Record> getProds(Order order) {
		List<ProductOrder> productOrders = ProductOrder.dao.find("select a.*, b.shop_id from product_order as a left join product as b on a.product_id = b.id where order_id = ?", order.getId());
		List<Record> list = new ArrayList<Record>();
		for (ProductOrder item : productOrders) {
			Product product = Product.dao.findById(item.getProductId());
			Record record = new Record();
			record.set("product_id", item.getProductId());
			record.set("amount", item.getUnitOrdered());
			record.set("template_id", product.getLogisticsTemplateId());
			record.set("price", item.getActualUnitPrice().doubleValue());
			record.set("shop_id", item.getInt("shop_id"));
			list.add(record);
		}
		return list;
	}

}
