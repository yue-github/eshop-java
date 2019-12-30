package com.eshop.event.listenner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eshop.event.EventEnum;
import com.eshop.event.param.PromotionParam;
import com.eshop.event.param.ShopCartParam;
import com.eshop.model.Order;
import com.eshop.model.ProductOrder;
import com.eshop.model.PromotionSku;
import com.eshop.model.Tax;
import com.eshop.model.dao.BaseDao;
import com.eshop.promotion.BasePromotion;
import com.eshop.service.Member;
import com.eshop.tax.ProductTax;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class PromotionListenner implements EventListenner {

	@Override
	public void Process(EventEnum event, Object param1) {
		switch (event) {
		case EVENT_SHOP_CART:	
			if (param1 instanceof ShopCartParam) {
				ShopCartParam p = (ShopCartParam) param1;
				List<Record> shops = p.getShopCarts();
				
				for (Record item : shops) {
					int shopId = item.getInt("id");
					List<Record> products = item.get("products");
					
					List<Record> promotions = BasePromotion.getShopCartPromotion(shopId, products);
					
					item.set("promotions", promotions);
				}
				
				p.setShopCarts(shops);
			}
			break;
		case EVENT_SUBMIT_ORDER:
			if (param1 instanceof PromotionParam) {
				String theSameOrderNum = ((PromotionParam) param1).getTheSameOrderNum();
				List<Record> products = ((PromotionParam) param1).getProducts();
				
				List<Order> orders = Order.dao.find("select * from `order` where theSameOrderNum = ?", theSameOrderNum);
				List<Record> promotions = BasePromotion.getMeetPromotion(products);
				
				for (Record promotion : promotions) {
					int type = promotion.getInt("type");
					boolean isMeet = promotion.getBoolean("isMeet");
					if (BasePromotion.MANSONG == type) {
						handleMansong(promotion, orders);
					} else if (isMeet && (BasePromotion.MANJIAN == type || BasePromotion.DAZHE == type)) {
						handleManjianOrDazhe(promotion, orders);
					}
				}
			}
			break;
		}
	}
	
	private void handleMansong(Record promotion, List<Order> orders) {
		int promotionId = promotion.getInt("id");
		int shopId = promotion.getInt("shop_id");
		
		List<PromotionSku> promotionSkus = PromotionSku.dao.find("select * from promotion_sku where promotion_id = ?", promotionId);
		List<Record> products = Db.find("select * from product");
		List<Record> suppliers = Db.find("select * from supplier");
		List<Record> categories = Db.find("select * from category");
		
		for (PromotionSku promotionSku : promotionSkus) {
			String selectProterties = promotionSku.getSelectProterties();
			
			int productId = promotionSku.getProductId();
			Record product = BaseDao.findItem(productId, products, "id");
			int supplierId = product.getInt("supplier_id");
			Record supplier = BaseDao.findItem(supplierId, suppliers, "id");
			int categoryId = product.getInt("category_id");
			Record category = BaseDao.findItem(categoryId, categories, "id");
			
			Tax tax = ProductTax.get(product.getInt("taxId"));
			String taxName = (tax != null) ? tax.getName() : "";
			BigDecimal taxRate = (tax != null) ? tax.getRate() : new BigDecimal(0);
			
			for (Order order : orders) {
				if (shopId == order.getShopId() && supplierId == order.getSupplierId()) {
					int orderId = order.getId();
					BigDecimal zero = new BigDecimal(0);
					ProductOrder productOrder = new ProductOrder();
					productOrder.setOrderId(orderId);
					productOrder.setProductId(productId);
					productOrder.setUnitOrdered(1);
					productOrder.setUnitCost(zero);
					productOrder.setSuggestedRetailUnitPrice(zero);
					productOrder.setActualUnitPrice(zero);
					productOrder.setTotalProductCost(zero);
					productOrder.setTotalDeliveryCost(zero);
					productOrder.setTotalCost(zero);
					productOrder.setTotalActualProductPrice(zero);
					productOrder.setTotalActualDeliveryCharge(zero);
					productOrder.setTotalPrice(zero);
					productOrder.setSelectProterties(selectProterties);
					productOrder.setStatus(1);
					productOrder.setCreatedAt(new Date());
					productOrder.setUpdatedAt(new Date());
					productOrder.setProductName(product.getStr("name"));
					productOrder.setSupplierName(supplier.getStr("name"));
					productOrder.setUpc(product.getStr("upc"));
					productOrder.setCategoryName(category.getStr("name"));
					productOrder.setProductSummary(product.getStr("summary"));
					productOrder.setPriceId(promotionSku.getPriceId());
					productOrder.setSupplierId(supplierId);
					productOrder.setTaxName(taxName);
					productOrder.setTaxRate(taxRate);
					productOrder.setTaxRateSum(zero);
					productOrder.setIsGift(1);
					productOrder.setPromotionId(promotionId);
					productOrder.save();
				}
			}
		}
	}
	
	private void handleManjianOrDazhe(Record promotion, List<Order> orders) {
		int promotionId = promotion.getInt("id");
		int shopId = promotion.getInt("shop_id");
		
		orders = filterOrder(shopId, orders);
		
		double discount = promotion.getDouble("discount");
		double totalProductPay = Member.calculateTotalActualProductPrice(orders);
		
		for (Order order : orders) {
			double totalActualProductPrice = order.getTotalActualProductPrice().doubleValue();
			double promotionDiscount = totalActualProductPrice / totalProductPay * discount;
			
			BigDecimal bigPromotionDiscount = BigDecimal.valueOf(promotionDiscount);
			
			
			Db.update("update `order` set totalPayable = ?, promotion_id = ?, promotionDiscount = ?", bigPromotionDiscount, promotionId, bigPromotionDiscount);
		}
	}
	
	private List<Order> filterOrder(int shopId, List<Order> orders) {
		List<Order> list = new ArrayList<Order>();
		
		for (Order order : orders) {
			if (order.getShopId() == shopId) {
				list.add(order);
			}
		}
		
		return list;
	}

}
