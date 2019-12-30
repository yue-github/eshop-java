package com.eshop.finance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eshop.helper.MathHelper;
import com.eshop.helper.QueryHelper;
import com.eshop.model.ProductOrder;
import com.eshop.model.Supplier;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class SupplierFinanceService {

	/**
	 * 批量查询供应商对账汇总记录
	 * @param offset
	 * @param count
	 * @param supplierId
	 * @param supplierName
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findSupplierSummaryItems(int offset, int count, Integer supplierId, 
			String supplierName, Integer status, String startTime, String endTime) {
		
		Map<String, String> map = findSupplierSummaryItemsSqlMap(supplierId, supplierName, status, 
				startTime, endTime);
		
		String sql = map.get("sql2");
		sql = BaseDao.appendLimitSql(sql, offset, count);
		
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	/**
	 * 批量查询供应商对账汇总记录
	 * @param supplierId
	 * @param supplierName
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findSupplierSummaryItems(Integer supplierId, String supplierName, 
			Integer status, String startTime, String endTime) {
		
		Map<String, String> map = findSupplierSummaryItemsSqlMap(supplierId, supplierName, status, 
				startTime, endTime);
		
		String sql = map.get("sql2");
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	/**
	 * 组装sql语句
	 * @param supplierId
	 * @param supplierName
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Map<String, String> findSupplierSummaryItemsSqlMap(Integer supplierId, String supplierName, 
			Integer status, String startTime, String endTime) {
		
		if (status == null) {
			status = 2;
		}
		
		String selectOrder = "select distinct b.id as order_id, b.payType, b.source," + 
				" '订单' as tradeType, payTime, sendOutTime, receiveTime, a.id, a.supplier_id," + 
				" a.supplier_name, unitOrdered, (unitCost * unitOrdered) as totalProductCost," + 
				" totalPrice, (a.totalActualDeliveryCharge + a.totalActualProductPrice) as totalSalable," + 
				" totalDeliveryCost, totalActualDeliveryCharge, '' as note, 0 as refundAmount," + 
				" 0 as refundCash, a.couponDiscount";
		String sqlExceptSelectOrder = " from product_order as a" +
				" left join `order` as b on a.order_id = b.id" +
				" left join refund as c on a.id = c.product_order_id" +
				" where b.status not in (1, 6)";
		
		switch (status) {
		case 2:
			sqlExceptSelectOrder += " and (c.status != 3 or c.status is null)";
			break;
		case 3:
			sqlExceptSelectOrder += " and (c.status != 3 or c.status is null)";
			break;
		}
		
		String selectRefund = "select c.id as order_id, c.payType, c.source," + 
				" '退款' as tradeType, a.successTime as payTime, a.successTime as sendOutTime," + 
				" a.successTime as receiveTime, b.id, b.supplier_id, b.supplier_name, 0 as unitOrdered," + 
				" (unitCost * unitOrdered * -1) as totalProductCost, 0 as totalPrice, refundCash * -1 as totalSalable," + 
				" 0 as totalDeliveryCost, (a.deliveryPrice * -1) as totalActualDeliveryCharge, '' as note," + 
				" refundAmount, refundCash, 0 as couponDiscount";
		String sqlExceptSelectRefund = " from refund as a" +
				" left join `product_order` as b on a.product_order_id = b.id" +
				" left join `order` as c on b.order_id = c.id" +
				" where a.status = 3";
		
		String selectBack = "select c.id as order_id, c.payType, c.source," + 
				" '退货' as tradeType, a.successTime as payTime, a.successTime as sendOutTime," + 
				" a.successTime as receiveTime, b.id, b.supplier_id, b.supplier_name, 0 as unitOrdered," + 
				" (unitCost * unitOrdered * -1) as totalProductCost, 0 as totalPrice, refundCash * -1 as totalSalable," + 
				" 0 as totalDeliveryCost, (a.deliveryPrice * -1) as totalActualDeliveryCharge, '' as note," + 
				" refundAmount, refundCash, 0 as couponDiscount";
		String sqlExceptSelectBack = " from back as a" +
				" left join `product_order` as b on a.product_order_id = b.id" +
				" left join `order` as c on b.order_id = c.id" +
				" where a.status = 4";
		
		switch (status) {
		case 3:
			sqlExceptSelectBack += " and c.receiveTime != ''";
			break;
		}
		
		String sql = selectOrder + sqlExceptSelectOrder +
				" union all " +
				selectRefund + sqlExceptSelectRefund +
				" union all " +
				selectBack + sqlExceptSelectBack;
		
		String select = "select order_id, tradeType, DATE_FORMAT(sendOutTime,'%Y-%m-%d') as sendOutTime," + 
				" id, supplier_id, supplier_name," + 
				" sum(unitOrdered) as unitOrdered, sum(totalProductCost) as totalProductCost," + 
				" sum(totalPrice) as totalPrice, sum(totalSalable) as totalSalable," + 
				" sum(totalDeliveryCost) as totalDeliveryCost," + 
				" sum(totalActualDeliveryCharge) as totalActualDeliveryCharge, '' as note," + 
				" sum(refundAmount) as backAmount, sum(refundCash) as totalRefund," + 
				" sum(couponDiscount) as couponDiscount";
		String sqlExceptSelect = " from (" + sql + ") as t" +
				" where (tradeType = '订单' or tradeType = '退货' or tradeType = '退款')";
		
		if (supplierId != null) {
			sqlExceptSelect += " and supplier_id = " + supplierId;
		}
		
		String orderBy = "";
		switch (status) {
		case 1:	
			sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') >= '" + startTime + "'" +
					" and DATE_FORMAT(payTime,'%Y-%m-%d') <= '" + endTime + "'";
					orderBy = " order by payTime desc";
			break;
		case 2:
			sqlExceptSelect += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') >= '" + startTime + "'" +
					" and DATE_FORMAT(sendOutTime,'%Y-%m-%d') <= '" + endTime + "'" +
					" and (tradeType = '订单' or tradeType = '退货')";
					orderBy = " order by sendOutTime desc";
			break;
		case 3:
			sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') >= '" + startTime + "'" +
					" and DATE_FORMAT(receiveTime,'%Y-%m-%d') <= '" + endTime + "'" +
					" and (tradeType = '订单' or tradeType = '退货')";
					orderBy = " order by receiveTime desc";
			break;
		}
		
		String sql1 = "select *" + sqlExceptSelect;
		sqlExceptSelect += " group by DATE_FORMAT(sendOutTime,'%Y-%m-%d'), supplier_id, tradeType" + orderBy;
		String sql2 = select + sqlExceptSelect;
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("sql1", sql1);
		map.put("sql2", sql2);
		System.out.println("sql1="+sql1);
		System.out.println("sql2="+sql2);
		return map;
	}
	
	/**
	 * 计算供应商对账汇总金额
	 * @param list
	 * @return
	 */
	public static Record calculateSupplierSummaryItems(Integer supplierId, String supplierName, 
			Integer status, String startTime, String endTime) {
		
		Map<String, String> map = findSupplierSummaryItemsSqlMap(supplierId, supplierName, status, 
				startTime, endTime);
		String sql = map.get("sql2");
		String sql2 = map.get("sql1");
		List<Record> data = Db.find(sql);
		List<Record> data2 = Db.find(sql2);
		
		int totalBackAmount = 0;
		int totalSendAmount = 0;
		double totalRefund = 0;
		double totalProductCost = 0;
		double totalCost = 0;
		double totalPrice = 0;
		double totalDeliveryCost = 0;
		double totalActualDeliveryCharge = 0;
		double totalSalable = 0;
		double totalWeiXin = 0;
		double totalWxApp = 0;
		double totalWxPc = 0;
		double totalUnionPay = 0;
		double totalAlipay = 0;
		double totalWallet = 0;
		double couponDiscount = 0;
		
		for (Record item : data2) {
			int payType = item.getInt("payType");
			int source = item.getInt("source");
			double salable = item.getBigDecimal("totalSalable").doubleValue();
			double refund = item.getBigDecimal("refundCash").doubleValue();
			couponDiscount = item.getBigDecimal("couponDiscount").doubleValue();
			String tradeType = item.getStr("tradeType");
			//1: 微信支付, 2: 支付宝, 3钱包支付，4钱包支付
			//订单来源，1pc、2微信端、3android、4苹果
			if (tradeType.equals("订单")) {
				if (payType == 1 && (source == 3 || source == 4)) {
					totalWxApp += salable - couponDiscount;
				} else if (payType == 1 && (source == 1 || source == 2)) {
					totalWxPc += salable - couponDiscount;
				} else if (payType == 2) {
					totalAlipay += salable - couponDiscount;
				} else if (payType == 3) {
					totalUnionPay += salable - couponDiscount;
				} else if (payType == 4) {
					totalUnionPay += salable - couponDiscount;
				}
			}
			if (tradeType.equals("退款") || tradeType.equals("退货")) {
				if (payType == 1 && (source == 1 || source == 2)) {
					totalWxPc -= refund;
				} else if (payType == 1 && (source == 3 || source == 4)) {
					totalWxApp -= refund;
				} else if (payType == 2) {
					totalAlipay -= refund;
				} else if (payType == 3) {
					totalUnionPay -= refund;
				} else if (payType == 4) {
					totalUnionPay -= refund;
				}
			}
		}
		
		totalWeiXin = totalWxPc + totalWxApp;
		
		for (Record item : data) {
			couponDiscount = item.getBigDecimal("couponDiscount").doubleValue();
			totalBackAmount += item.getBigDecimal("backAmount").intValue();
			totalSendAmount += item.getBigDecimal("unitOrdered").intValue();
			totalRefund += item.getBigDecimal("totalRefund").doubleValue();
			totalProductCost += item.getBigDecimal("totalProductCost").doubleValue();
			totalCost += item.getBigDecimal("totalProductCost").doubleValue() + item.getBigDecimal("totalActualDeliveryCharge").doubleValue();
			totalPrice += item.getBigDecimal("totalPrice").doubleValue();
			totalDeliveryCost += item.getBigDecimal("totalDeliveryCost").doubleValue();
			totalActualDeliveryCharge += item.getBigDecimal("totalDeliveryCost").doubleValue();
			totalSalable += item.getBigDecimal("totalSalable").doubleValue() - couponDiscount;
		}
		
		Record r = new Record();
		r.set("totalBackAmount", totalBackAmount);
		r.set("totalSendAmount", totalSendAmount);
		r.set("totalRefund", MathHelper.cutDecimal(totalRefund));
		r.set("totalProductCost", MathHelper.cutDecimal(totalProductCost));
		r.set("totalCost", MathHelper.cutDecimal(totalCost));
		r.set("totalPrice", MathHelper.cutDecimal(totalPrice));
		r.set("totalDeliveryCost", MathHelper.cutDecimal(totalDeliveryCost));
		r.set("totalActualDeliveryCharge", MathHelper.cutDecimal(totalActualDeliveryCharge));
		r.set("totalSalable", MathHelper.cutDecimal(totalSalable));
		r.set("totalWeiXin", MathHelper.cutDecimal(totalWeiXin));
		r.set("totalWxApp", MathHelper.cutDecimal(totalWxApp));
		r.set("totalWxPc", MathHelper.cutDecimal(totalWxPc));
		r.set("totalAlipay", MathHelper.cutDecimal(totalAlipay));
		r.set("totalUnionPay", MathHelper.cutDecimal(totalUnionPay));
		r.set("totalWallet", MathHelper.cutDecimal(totalWallet));
		return r;
	}
	
	public static List<Record> findSupplierDetailItems(int offset, int count, Integer supplierId, 
			Integer status, String supplierName, String orderCode, String tradeCode, String tradeNo,
			String productName, String startTime, String endTime, Integer payType,
			Integer source, String expressCode, String logisticsName, String whereInPayType) {
		
		String sql = findSupplierDetailItemsSql(supplierId, status, supplierName, orderCode, 
				tradeCode, tradeNo, productName, startTime, endTime, payType, source, 
				expressCode, logisticsName, whereInPayType);
		sql = BaseDao.appendLimitSql(sql, offset, count);
		
		List<Record> list = Db.find(sql);
		list = appendSupplierDetailItems(list);
		
		return list;
	}
	
	public static List<Record> findSupplierDetailItems(Integer supplierId, Integer status, 
			String supplierName, String orderCode, String tradeCode, String tradeNo,
			String productName, String startTime, String endTime, Integer payType,
			Integer source, String expressCode, String logisticsName, String whereInPayType) {
		
		String sql = findSupplierDetailItemsSql(supplierId, status, supplierName, orderCode, 
				tradeCode, tradeNo, productName, startTime, endTime, payType, source, 
				expressCode, logisticsName, whereInPayType);
		
		List<Record> list = Db.find(sql);
		list = appendSupplierDetailItems(list);
		
		return list;
	}
	
	private static List<Record> appendSupplierDetailItems(List<Record> list) {
		return list;
	}
	
	public static int countSupplierDetailItems(Integer supplierId, Integer status, 
			String supplierName, String orderCode, String tradeCode, String tradeNo,
			String productName, String startTime, String endTime, Integer payType,
			Integer source, String expressCode, String logisticsName, String whereInPayType) {
		
		String sql = findSupplierDetailItemsSql(supplierId, status, supplierName, orderCode, 
				tradeCode, tradeNo, productName, startTime, endTime, payType, source, 
				expressCode, logisticsName, whereInPayType);
		return Db.find(sql).size();
	}
	
	public static String findSupplierDetailItemsSql(Integer supplierId, Integer status, 
			String supplierName, String orderCode, String tradeCode, String tradeNo,
			String productName, String startTime, String endTime, Integer payType,
			Integer source, String expressCode, String logisticsName, String whereInPayType) {
		
		if (status == null) {
			status = 2;
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
				" unitOrdered as pUnitOrdered";
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
    			" a.refundCash*-1 as totalSale, 0 as pUnitOrdered";
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
    			" a.refundCash*-1 as totalSale, 0 as pUnitOrdered";
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
	
	public static Record calculateSupplierDetailItems(List<Record> list) {
		BigDecimal totalSale = new BigDecimal(0);
		BigDecimal totalSaleCost = new BigDecimal(0);
		BigDecimal totalRefundCash = new BigDecimal(0);
		BigDecimal totalDeliveryPrice = new BigDecimal(0);
		
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
		return result;
	}
	
	/**
	 * 批量查询供应商对账汇总记录
	 * @param pageIndex
	 * @param length
	 * @param supplierId
	 * @param supplierName
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Page<ProductOrder> getSupplierSummaryItems(int pageIndex, int length, Integer supplierId,
			String supplierName, Integer status, String startTime, String endTime) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" FROM ( ");
		buffer.append(" SELECT a.supplier_id,a.supplier_name, b.order_code, a.product_name,a.selectProterties, a.pricingUnit, a.unitCost, a.`taxRate`, a.unitCostNoTax, SUM(a.unitOrdered) AS unitOrdered,  ");
		buffer.append(" SUM(b.deliveryPrice) AS deliveryPrice, SUM(b.totalProductCost) AS totalProductCost, a.invoiceType FROM product_order AS a ");
		buffer.append(" LEFT JOIN `order` AS b ");
		buffer.append(" ON a.order_id=b.id ");
		buffer.append(" WHERE b.status IN(3,4,5,7) ");
		buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')>= "+ "'"+startTime+"'");
		buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')<="+ "'"+endTime+"'");
		buffer.append(" GROUP BY a.supplier_name, a.product_name, a.selectProterties ");
		buffer.append(" UNION ");
		buffer.append(" SELECT a.supplier_id,a.supplier_name, c.order_code, a.product_name,a.selectProterties, a.pricingUnit, a.unitCost, a.taxRate, a.unitCostNoTax, SUM(a.unitOrdered)*-1 AS unitOrdered,  ");
		buffer.append(" SUM(c.deliveryPrice)*-1 AS deliveryPrice, SUM(c.totalProductCost)*-1 AS totalProductCost, a.invoiceType  ");
		buffer.append(" FROM back AS b ");
		buffer.append(" LEFT JOIN product_order AS a ");
		buffer.append(" ON b.product_order_id=a.id ");
		buffer.append(" LEFT JOIN `order` AS c ");
		buffer.append(" ON a.order_id=c.`id` ");
		buffer.append(" WHERE c.status IN(3,4,5,7) ");
		buffer.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')>= "+ "'"+startTime+"'");
		buffer.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')<= "+ "'"+endTime+"'");
		buffer.append(" GROUP BY a.supplier_name, a.product_name, a.selectProterties ");
		buffer.append(" ) AS o ");
		QueryHelper helper = appendWhereCaluse(supplierId, supplierName);
		/*if(supplierId != null) {
			helper.addCondition("o.supplier_id=?", supplierId);
			buffer.append(helper.getWhereClause());
		}*/
		buffer.append(helper.getWhereClause());
		Page<ProductOrder> page = ProductOrder.dao.paginate(pageIndex, length, " SELECT o.* ", buffer.toString(), helper.getParams().toArray());
		return page;
	}
	
	/**
	 * 查询供应商对账汇总记录
	 * @param supplierId
	 * @param supplierName
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<ProductOrder> getAllSupplierSummaryItems(Integer supplierId, String supplierName, Integer status,
			String startTime, String endTime) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" SELECT * FROM ( ");
		buffer.append(" SELECT a.supplier_id,a.supplier_name, b.order_code, a.product_name,a.selectProterties, a.pricingUnit, a.unitCost, a.taxRate, a.unitCostNoTax, SUM(a.unitOrdered) AS unitOrdered,  ");
		buffer.append(" SUM(b.deliveryPrice) AS deliveryPrice, SUM(b.totalProductCost) AS totalProductCost, a.invoiceType,SUM(CASE WHEN b.payType = 2 THEN b.totalActualProductPrice ELSE 0 END) AS zhifubaoPrice,  ");
		buffer.append(" SUM(CASE WHEN b.payType = 1 THEN b.totalActualProductPrice ELSE 0 END) AS weixinPrice, SUM(CASE WHEN b.payType = 3 THEN b.totalActualProductPrice ELSE 0 END) AS yinlianPrice, ");
		buffer.append(" SUM( a.totalPrice) AS  totalPrice, SUM(a.totalCost) AS totalCost,  ");
		buffer.append(" SUM(CASE WHEN b.payType = 1 AND b.source IN(1,2) THEN b.totalActualProductPrice ELSE 0 END) AS weixinPCPrice,SUM(CASE WHEN b.payType = 1 AND b.source IN(3,4) THEN b.totalActualProductPrice ELSE 0 END) AS weixinAppPrice ");
		buffer.append(" FROM product_order AS a ");
		buffer.append(" LEFT JOIN `order` AS b ");
		buffer.append(" ON a.order_id=b.id ");
		buffer.append(" WHERE b.status IN(3,4,5,7) ");
		buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')>= "+ "'"+startTime+"'");
		buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')<="+ "'"+endTime+"'");
		buffer.append(" GROUP BY a.supplier_name, a.product_name, a.selectProterties ");
		buffer.append(" UNION ");
		buffer.append(" SELECT b.supplier_id,b.supplier_name, c.order_code, b.product_name,b.selectProterties, b.pricingUnit, b.unitCost, b.taxRate, b.unitCostNoTax, SUM(b.unitOrdered)*-1 AS unitOrdered,  ");
		buffer.append(" SUM(c.deliveryPrice)*-1 AS deliveryPrice, SUM(c.totalProductCost)*-1 AS totalProductCost, b.invoiceType, SUM(CASE WHEN c.payType = 2 THEN c.totalActualProductPrice ELSE 0 END)*-1 AS zhifubaoPrice, ");
		buffer.append(" SUM(CASE WHEN c.payType = 1 THEN c.totalActualProductPrice ELSE 0 END)*-1 AS weixinPrice, SUM(CASE WHEN c.payType = 3 THEN c.totalActualProductPrice ELSE 0 END)*-1 AS yinlianPrice, ");
		buffer.append(" SUM( b.totalPrice) AS  totalPrice, SUM(b.totalCost) AS totalCost, ");
		buffer.append(" SUM(CASE WHEN c.payType = 1 AND c.source IN (1,2) THEN c.totalActualProductPrice ELSE 0 END)*-1 AS weixinPCPrice,SUM(CASE WHEN c.payType = 1 AND c.source IN(3,4) THEN c.totalActualProductPrice ELSE 0 END)*-1 AS weixinAppPrice ");
		buffer.append(" FROM back AS a ");
		buffer.append(" LEFT JOIN product_order AS b ");
		buffer.append(" ON a.product_order_id=b.id ");
		buffer.append(" LEFT JOIN `order` AS c ");
		buffer.append(" ON b.order_id=c.id ");
		buffer.append(" WHERE c.status IN(3,4,5,7) ");
		buffer.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')>= "+ "'"+startTime+"'");
		buffer.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')<= "+ "'"+endTime+"'");
		buffer.append(" GROUP BY b.supplier_name, b.product_name, b.selectProterties ");
		buffer.append(" ) AS o ");
		QueryHelper helper = appendWhereCaluse(supplierId, supplierName);
		/*if(supplierId != null) {
			helper.addCondition("o.supplier_id=?", supplierId);
			buffer.append(helper.getWhereClause());
		}*/
		buffer.append(helper.getWhereClause());
		List<ProductOrder> list = ProductOrder.dao.find(buffer.toString(), helper.getParams().toArray());
		return list;
	}
	/**
	 * 拼接where子句
	 * @param supplierId
	 * @param supplierName
	 * @return
	 */
	public static QueryHelper appendWhereCaluse(Integer supplierId, String supplierName) {
		QueryHelper helper = new QueryHelper();
		if(supplierId != null) {
			helper.addCondition("o.supplier_id=?", supplierId);
		}
	
		if(supplierName != null && supplierName.length() > 0) {
			helper.addCondition("o.supplier_name like ?", "%"+supplierName+"%");
		}
		return helper;
		
	}
	
	public static Supplier findSupplierContractById(Integer id) {
		String sql = "SELECT a.*,b.account_period FROM supplier AS a LEFT JOIN supplier_contract AS b ON a.id=b.supplier_id WHERE a.id=?";
		Supplier supplier = Supplier.dao.findFirst(sql, new Object[] {id});
		return supplier;
	}
	
	public static Record getSupplier(Integer id) {
		if (id == null)
			return null;
		
		String sql = "select s.*, sc.account_period from supplier as s" + 
				" left join supplier_contract as sc on s.id = sc.supplier_id" + 
				" where s.id = ?";
		Record supplier = Db.findFirst(sql, id);
		
		if (supplier != null) {
			String accountPeriod = supplier.getStr("account_period");
			if (accountPeriod != null && !accountPeriod.equals("")) {
				accountPeriod = getSupplierPeriod(accountPeriod);
				supplier.set("account_period", accountPeriod);
			} else {
				accountPeriod = "";
			}
		}
		
		return supplier;
	}
	
	private static String getSupplierPeriod(String supplierPeriod) {
		String[] array = supplierPeriod.split(",");
		if (array.length == 2) {
			String type = array[0];
			String days = array[1];
			
			String label = "";
			if (type.equals("day")) {
				label = "日结";
			} else if (type.equals("month")) {
				label = "月结";
			}
			label += days + "天";
			
			if (type.equals("month") && days.equals("0")) {
				label = "当月结";
			}
			
			return label;
		}
		
		return "";
	}
	
}
