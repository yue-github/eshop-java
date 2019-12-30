package com.eshop.finance;

import java.math.BigDecimal;
import java.util.List;

import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class HotelAndTripService {

	public static List<Record> findDetailItems(int offset, int count, Integer supplierId, 
			Integer status, String supplierName, String orderCode, String tradeCode, String tradeNo,
			String productName, String startTime, String endTime, Integer payType,
			Integer source, String expressCode, String logisticsName, String whereInPayType,
			Integer orderType) {
		
		String sql = findDetailItemsSql(supplierId, status, supplierName, orderCode, 
				tradeCode, tradeNo, productName, startTime, endTime, payType, source, 
				expressCode, logisticsName, whereInPayType, orderType);
		sql = BaseDao.appendLimitSql(sql, offset, count);
		
		System.out.println(sql);
		
		List<Record> list = Db.find(sql);
		list = appendDetailItems(list);
		
		return list;
	}
	
	public static List<Record> findDetailItems(Integer supplierId, Integer status, 
			String supplierName, String orderCode, String tradeCode, String tradeNo,
			String productName, String startTime, String endTime, Integer payType,
			Integer source, String expressCode, String logisticsName, String whereInPayType,
			Integer orderType) {
		
		String sql = findDetailItemsSql(supplierId, status, supplierName, orderCode, 
				tradeCode, tradeNo, productName, startTime, endTime, payType, source, 
				expressCode, logisticsName, whereInPayType, orderType);
		
		List<Record> list = Db.find(sql);
		list = appendDetailItems(list);
		
		return list;
	}
	
	public static Record calculateDetailItems(List<Record> list) {
		BigDecimal totalSale = new BigDecimal(0);
		BigDecimal totalSaleCost = new BigDecimal(0);
		BigDecimal totalRefundCash = new BigDecimal(0);
		BigDecimal totalDeliveryPrice = new BigDecimal(0);
		BigDecimal totalCommissionPayable = new BigDecimal(0);
		
		BigDecimal weixin = new BigDecimal(0);
		BigDecimal weixinPc = new BigDecimal(0);
		BigDecimal weixinApp = new BigDecimal(0);
		BigDecimal alipay = new BigDecimal(0);
		BigDecimal unionpay = new BigDecimal(0);
		BigDecimal balancepay = new BigDecimal(0);  // 余额支付
		
		int totalRefundAmount = 0;
		int totalUnitOrdered = 0;
		
		for (Record item : list) {
			totalRefundAmount = totalRefundAmount + item.getNumber("refundAmount").intValue();
			totalUnitOrdered = totalUnitOrdered + item.getNumber("pUnitOrdered").intValue();
			totalSale = totalSale.add(item.getBigDecimal("totalSale"));
			totalSaleCost = totalSaleCost.add(item.getBigDecimal("totalCost"));
			totalRefundCash = totalRefundCash.add(item.getBigDecimal("refundCash"));
			totalDeliveryPrice = totalDeliveryPrice.add(item.getBigDecimal("deliveryPrice"));
			totalCommissionPayable = totalCommissionPayable.add(item.getBigDecimal("commissionPayable"));
			
			// 计算微信app、微信公众号、支付宝、银联和余额的支付金额
			int payType = item.getInt("payType");
    		int source = item.getInt("source");
			
    		if (payType == 1 && (source == 1 || source == 2)) {
    			weixinPc = weixinPc.add(item.getBigDecimal("totalSale"));
    		} else if (payType == 1 && source == 3) {
    			weixinApp = weixinApp.add(item.getBigDecimal("totalSale"));
    		} else if (payType == 2) {
    			alipay = alipay.add(item.getBigDecimal("totalSale"));
    		} else if (payType == 3) {
    			unionpay = unionpay.add(item.getBigDecimal("totalSale"));
    		} else if (payType == 4) {
    			balancepay = balancepay.add(item.getBigDecimal("totalSale"));
    		}
		}
		
		weixin = weixinApp.add(weixinPc);
		
		Record result = new Record();
		result.set("totalSale", totalSale.doubleValue());
		result.set("totalSaleCost", totalSaleCost.doubleValue());
		result.set("totalRefundCash", totalRefundCash.doubleValue());
		result.set("weixinPc", weixinPc.doubleValue());
		result.set("weixinApp", weixinApp.doubleValue());
		result.set("weixin", weixin.doubleValue());
		result.set("alipay", alipay.doubleValue());
		result.set("unionpay", unionpay.doubleValue());
		result.set("balancepay", balancepay.doubleValue());
		result.set("totalRefundAmount", totalRefundAmount);
		result.set("totalUnitOrdered", totalUnitOrdered);
		result.set("totalDeliveryPrice", totalDeliveryPrice);
		result.set("totalCommissionPayable", totalCommissionPayable.doubleValue());
		return result;
	}
	
	private static List<Record> appendDetailItems(List<Record> list) {
		return list;
	}
	
	public static String findDetailItemsSql(Integer supplierId, Integer status, 
			String supplierName, String orderCode, String tradeCode, String tradeNo,
			String productName, String startTime, String endTime, Integer payType,
			Integer source, String expressCode, String logisticsName, String whereInPayType,
			Integer orderType) {
		
		if (status == null) {
			status = 1;
		}
		
		String selectOrder = "select '订单' as tradeType, payTime, sendOutTime, receiveTime," + 
				" order_code, expressCode, logisticsName, a.note, b.supplier_name," + 
				" selectProterties, product_name, pricingUnit, unitOrdered," + 
				" b.totalProductCost, b.totalActualProductPrice, 0 as refundCash," + 
				" 0 as refundAmount, payType, source, theSameOrderNum as tradeCode," +
				" tradeNo, b.supplier_id, b.totalActualDeliveryCharge as deliveryPrice," +
				" b.unitCost, b.taxRate, b.unitCostNoTax, b.invoiceType, b.actualUnitPrice," +
				" b.totalPrice, b.couponDiscount," + 
				" (b.totalProductCost + b.totalActualDeliveryCharge) as totalCost," +
				" (b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount) as totalSale," +
				" unitOrdered as pUnitOrdered, b.order_type, " + 
				" (b.commission * b.totalPrice * 0.01) as commissionPayable, b.commission";
    	String sqlExceptSelectOrder = " from `order` as a" +
    			" left join product_order as b on a.id = b.order_id" +
    			" left join refund as c on b.id = c.product_order_id" +
    			" where a.status not in (1, 6)";
    	
    	// 不显示退款成功的订单
    	switch (status) {
    	case 2:
    		sqlExceptSelectOrder += " and (c.status != 3 or c.status is null)";
    		break;
    	case 3:
    		sqlExceptSelectOrder += " and (c.status != 3 or c.status is null)";
    		break;
    	}
    	
    	String selectRefund = "select '退款' as tradeType, a.successTime as payTime," +
    			" a.successTime as sendOutTime, a.successTime as receiveTime, order_code," +
    			" expressCode, logisticsName, c.note, supplier_name, selectProterties," +
    			" product_name, pricingUnit, 0 as unitOrdered, b.totalProductCost * -1 as totalProductCost," +
    			" b.totalActualProductPrice * -1 as totalActualProductPrice, refundCash, refundAmount," +
    			" payType, source, theSameOrderNum as tradeCode, tradeNo, b.supplier_id," + 
    			" a.deliveryPrice * -1 as deliveryPrice," + 
    			" b.unitCost, b.taxRate, b.unitCostNoTax, b.invoiceType, b.actualUnitPrice," +
    			" b.totalPrice, 0 as couponDiscount," +
    			" (b.totalProductCost*-1 + a.deliveryPrice*-1) as totalCost," +
    			" a.refundCash*-1 as totalSale, 0 as pUnitOrdered, b.order_type, " + 
    			" (b.commission * b.totalPrice * 0.01 * -1) as commissionPayable, b.commission";
    	String sqlExceptSelectRefund = " from `refund` as a" +
    			" left join product_order as b on a.product_order_id = b.id" +
    			" left join `order` as c on b.order_id = c.id" +
    			" where a.status = 3" +
    			" and (c.id != '' or c.id != null)";
    	
    	String selectBack = "select '退货' as tradeType, a.successTime as payTime," +
    			" a.successTime as sendOutTime, a.successTime as receiveTime, order_code," + 
    			" expressCode, logisticsName, c.note, supplier_name, selectProterties," +
    			" product_name, pricingUnit, 0 as unitOrdered, b.totalProductCost * -1 as totalProductCost," +
    			" b.totalActualProductPrice * -1 as totalActualProductPrice, refundCash, refundAmount," +
    			" payType, source, theSameOrderNum as tradeCode, tradeNo, b.supplier_id," + 
    			" a.deliveryPrice * -1 as deliveryPrice," +
    			" b.unitCost, b.taxRate, b.unitCostNoTax, b.invoiceType, b.actualUnitPrice," +
    			" b.totalPrice, 0 as couponDiscount," +
    			" (b.totalProductCost*-1 + a.deliveryPrice*-1) as totalCost," +
    			" a.refundCash*-1 as totalSale, 0 as pUnitOrdered, b.order_type," +
    			" (b.commission * b.totalPrice * 0.01 * -1) as commissionPayable, b.commission";
    	String sqlExceptSelectBack = " from `back` as a" +
    			" left join product_order as b on a.product_order_id = b.id" +
    			" left join `order` as c on b.order_id = c.id" +
    			" where a.status = 4" +
    			" and (c.id != '' or c.id != null)";
    	
    	switch (status) {
    	case 3:
    		sqlExceptSelectBack += " and c.receiveTime != ''";
    		break;
    	}
    	
    	String sql = selectOrder + sqlExceptSelectOrder +
    			" union " +
    			selectRefund + sqlExceptSelectRefund +
    			" union " +
    			selectBack + sqlExceptSelectBack;
    	
    	String select = "select *";
    	String sqlExceptSelect = " from (" + sql + ") as t" +
    			" where tradeType in ('订单', '退款', '退货')";
    	
    	if (orderType != null) {
    		sqlExceptSelect += " and order_type = " + orderType;
    	}
    	if (supplierId != null) {
			sqlExceptSelect += " and supplier_id = " + supplierId;
		}
    	if (supplierName != null && !supplierName.equals("")) {
			sqlExceptSelect += " and supplier_name like '%" + supplierName + "%'";
		}
    	if (orderCode != null && !orderCode.equals("")) {
			sqlExceptSelect += " and order_code like '%" + orderCode + "%'";
		}
    	if (tradeCode != null && !tradeCode.equals("")) {
			sqlExceptSelect += " and tradeCode like '%" + tradeCode + "%'";
		}
    	if (tradeNo != null && !tradeNo.equals("")) {
			sqlExceptSelect += " and tradeNo like '%" + tradeNo + "%'";
		}
    	if (productName != null && !productName.equals("")) {
			sqlExceptSelect += " and product_name like '%" + productName + "%'";
		}
    	if (payType != null) {
			sqlExceptSelect += " and payType = " + payType;
		}
    	if (whereInPayType != null) {
			sqlExceptSelect += " and payType in " + whereInPayType;
		}
    	if (source != null) {
			sqlExceptSelect += " and source = " + source;
		}
    	if (expressCode != null && !expressCode.equals("")) {
			sqlExceptSelect += " and expressCode like '%" + expressCode + "%'";
		}
    	if (logisticsName != null && !logisticsName.equals("")) {
			sqlExceptSelect += " and logisticsName like '%" + logisticsName + "%'";
		}
    	
    	switch (status) {
    	case 1:	
    		if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
    		if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			sqlExceptSelect += " order by payTime desc";
    		break;
    	case 2:
    		if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
    		if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
    		sqlExceptSelect += " and (tradeType = '订单' or tradeType = '退货')";
			sqlExceptSelect += " order by sendOutTime desc";
    		break;
    	case 3:
    		if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
    		if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
    		sqlExceptSelect += " and (tradeType = '订单' or tradeType = '退货')";
			sqlExceptSelect += " order by receiveTime desc";
    		break;
    	}
    	
    	return select + sqlExceptSelect;
	}
	
}
