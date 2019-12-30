package com.eshop.helper;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eshop.model.Order;
import com.eshop.model.ProductOrder;
import com.eshop.model.ProductPrice;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class MigrateHelper {
	
	public static ServiceCode migrateOrderProductPayable() {
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					List<Order> orders = Order.dao.find("select * from `order`");
					for (Order order : orders) {
						int orderId = order.getId();
						BigDecimal payRateSum = order.getPayRate().multiply(order.getTotalPayable()).multiply(new BigDecimal(0.01));
						List<ProductOrder> pds = ProductOrder.dao.find("select * from product_order where order_id = ?", orderId);
						Map<String, BigDecimal> map = calculateProductOrders(pds);
						order.setTotalProductCost(map.get("totalProductCost"));
						order.setTotalActualProductPrice(map.get("totalActualProductPrice"));
						order.setTaxRateSum(map.get("taxRateSum"));
						order.update();
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	public static Map<String, BigDecimal> calculateProductOrders(List<ProductOrder> list) {
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		BigDecimal deliveryPrice = new BigDecimal(0);
		BigDecimal totalProductCost = new BigDecimal(0);
		BigDecimal totalActualProductPrice = new BigDecimal(0);
		BigDecimal totalPayable = new BigDecimal(0);
		BigDecimal couponDiscount = new BigDecimal(0);
		BigDecimal taxRateSum = new BigDecimal(0);
		
		for (ProductOrder item : list) {
			deliveryPrice = deliveryPrice.add(item.getTotalActualDeliveryCharge());
			totalProductCost = totalProductCost.add(item.getTotalProductCost());
			totalActualProductPrice = totalActualProductPrice.add(item.getTotalActualProductPrice());
			couponDiscount = couponDiscount.add(item.getCouponDiscount());
			taxRateSum = taxRateSum.add(item.getTaxRateSum());
		}
		
		totalPayable = deliveryPrice.add(totalProductCost).add(totalActualProductPrice).subtract(couponDiscount);
		
		map.put("deliveryPrice", deliveryPrice);
		map.put("totalProductCost", totalProductCost);
		map.put("totalActualProductPrice", totalActualProductPrice);
		map.put("totalPayable", totalPayable);
		map.put("couponDiscount", couponDiscount);
		map.put("taxRateSum", taxRateSum);
		return map;
	}
	
	private static List<Integer> getSupplierIds(List<ProductOrder> list) {
		List<Integer> ids = new ArrayList<Integer>();
		for (ProductOrder item : list) {
			int supplierId = item.getSupplierId();
			if (!ids.contains(supplierId)) {
				ids.add(supplierId);
			}
		}
		return ids;
	}
	
	public static ServiceCode migrateProductPrice() {
		String sql = "select pp.*, p.taxRate, p.invoiceType from product_price as pp" + 
				" left join product as p on p.id = pp.product_id" + 
				" where p.unitCostNoTax > 0;";
		
		final List<Record> list = Db.find(sql);
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (Record item : list) {
						if (item.get("unitCost") != null) {
							BigDecimal rate = new BigDecimal(1);
							BigDecimal taxRate = item.getBigDecimal("taxRate");
							BigDecimal unitCost = item.getBigDecimal("unitCost");
							String invoiceType = item.getStr("invoiceType");
							
							if (invoiceType.equals("value_add")) {
								rate = rate.add(taxRate.multiply(new BigDecimal(0.01)));
							}
							
							BigDecimal unitCostNoTax = unitCost.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
							
							ProductPrice model = ProductPrice.dao.findById(item.getNumber("id").intValue());
							model.setUnitCostNoTax(unitCostNoTax);
							model.update();
						}
					}
					
					return true;
					
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
}
