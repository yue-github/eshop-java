package com.eshop.finance;

import java.math.BigDecimal;
import java.util.List;

import com.eshop.helper.QueryHelper;
import com.eshop.model.ProductOrder;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class SaleFinanceService {
	
	/**
	 * 批量查询销售汇总记录
	 * @param payType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findSaleSummaryItems(int offset, int count, Integer payType, Integer source, 
			String startTime, String endTime, Integer summaryType, String whereInPayType) {
		
		String sql = findSaleSummaryItemsSql(payType, source, startTime, endTime, summaryType, whereInPayType);
		sql = BaseDao.appendLimitSql(sql, offset, count);
		List<Record> list = Db.find(sql);
		return appendSaleSummaryItems(list);
	}
	
	/**
	 * 批量查询销售汇总记录
	 * @param payType
	 * @param source
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findSaleSummaryItems(Integer payType, Integer source, String startTime, 
			String endTime, Integer summaryType, String whereInPayType) {
		
		String sql = findSaleSummaryItemsSql(payType, source, startTime, endTime, summaryType, whereInPayType);
		List<Record> list = Db.find(sql);
		return appendSaleSummaryItems(list);
	}
	
	private static List<Record> appendSaleSummaryItems(List<Record> list) {
		for (Record item : list) {
			BigDecimal balance = item.getBigDecimal("totalPayable").subtract(item.getBigDecimal("payRateSum"));
			item.set("balance", balance);
		}
		return list;
	}
	
	/**
	 * 批量查询销售汇总记录的总条数
	 * @param payType
	 * @param source
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int countSaleSummaryItems(Integer payType, Integer source, String startTime, 
			String endTime, Integer summaryType, String whereInPayType) {
		
		String sql = findSaleSummaryItemsSql(payType, source, startTime, endTime, summaryType, whereInPayType);
		return Db.find(sql).size();
	}
	
	/**
	 * 组装sql语句
	 * @param payType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String findSaleSummaryItemsSql(Integer payType, Integer source, String startTime, 
			String endTime, Integer summaryType, String whereInPayType) {
		
		if (summaryType == null) {
			summaryType = 0;
		}
		
		String selectOrder = "select '订单' as tradeType, source, payType," +
				" DATE_FORMAT(payTime,'%Y-%m-%d') as payTime, totalPayable, payRateSum, taxRateSum," +
				" deliveryPrice, totalProductCost, totalActualProductPrice," + 
				" 0 as refundCash";
		String sqlExceptSelectOrder = " from `order`" + 
				" where status not in(1, 6)";

		String selectBack = "select '退货' as tradeType, c.source, c.payType," + 
				" DATE_FORMAT(a.successTime,'%Y-%m-%d') as payTime, refundCash * -1 as totalPayable," + 
				" 0 as payRateSum, 0 as taxRateSum, a.deliveryPrice * -1 as deliveryPrice," + 
				" b.totalProductCost * -1 as totalProductCost," + 
				" b.totalActualProductPrice * -1 as totalActualProductPrice," +
				" a.refundCash";
		String sqlExceptSelectBack = " from back as a" + 
				" left join product_order as b on a.product_order_id = b.id" + 
				" left join `order` as c on b.order_id = c.id"+ 
				" where a.status = 4";
		
		String selectRefund = "select '退款' as tradeType, c.source, c.payType," + 
				" DATE_FORMAT(a.successTime,'%Y-%m-%d') as payTime, refundCash * -1 as totalPayable," + 
				" 0 as payRateSum, 0 as taxRateSum, a.deliveryPrice * -1 as deliveryPrice," + 
				" b.totalProductCost * -1 as totalProductCost," + 
				" b.totalActualProductPrice * -1 as totalActualProductPrice," +
				" a.refundCash";
		String sqlExceptSelectRefund = " from refund as a" + 
				" left join product_order as b on a.product_order_id = b.id" + 
				" left join `order` as c on b.order_id = c.id" + 
				" where a.status = 3";
		
		String selectRg = "select '充值' as tradeType, 1 as source," + 
				" case event when 3 then 2 when 4 then 1 when 6 then 3 end as payType," +
				" DATE_FORMAT(created_at,'%Y-%m-%d') as payTime, money as totalPayable," + 
				" 0 as payRateSum, 0 as taxRateSum, 0 as deliveryPrice, 0 as totalProductCost," + 
				" 0 as totalActualProductPrice," +
				" 0 as refundCash";
		String sqlExceptSelectRg = " from wallet" +
				" where event in (3, 4, 6)" + 
				" and isPaySuccess = 1" + 
				" and transactionId is not null";
		
		String sql = selectOrder + sqlExceptSelectOrder +
				" union all " +
				selectRg + sqlExceptSelectRg +
				" union all " +
				selectBack + sqlExceptSelectBack +
				" union all " +
				selectRefund + sqlExceptSelectRefund;
		
		String select = "select tradeType, source, payType, payTime, sum(totalPayable) as totalPayable," + 
				" sum(payRateSum) as payRateSum, sum(taxRateSum) as totalTaxCost," + 
				" sum(deliveryPrice) as deliveryPrice, sum(totalProductCost) as totalProductCost," + 
				" sum(totalActualProductPrice) as totalActualProductPrice," +
				" sum(refundCash) as refundCash" +
				" from (" + sql + ") as t" +
				" where tradeType in ('订单', '退款', '退货', '充值')";
		
		if (payType != null) {
			select += " and payType = " + payType;
		}
		if (whereInPayType != null) {
			select += " and payType in " + whereInPayType;
		}
		if (source != null) {
			select += " and source = " + source;
		}
		if (startTime != null) {
			select += " and payTime >= '" + startTime + "'";
		}
		if (endTime != null) {
			select += " and payTime <= '" + endTime + "'";
		}
		if (summaryType == 0) {
			select += " GROUP BY payTime, source, payType";
		} else if (summaryType == 1) {
			select += " GROUP BY payType";
		} else if (summaryType == 2) {
			select += " GROUP BY source";
		}

		return select;
	}
	
	/**
	 * 计算销售汇总金额
	 * @param list
	 * @return
	 */
	public static Record calculateSaleSummaryItems(List<Record> list) {
		BigDecimal totalActualProductPrice = new BigDecimal(0);
		BigDecimal totalPayRateSum = new BigDecimal(0);
		BigDecimal totalPayable = new BigDecimal(0);
		BigDecimal totalProductCost = new BigDecimal(0);
		BigDecimal totalRefundCash = new BigDecimal(0);
		BigDecimal totalTaxCost = new BigDecimal(0);
		BigDecimal totalBalance = new BigDecimal(0);
		
		for (Record item : list) {
			totalActualProductPrice = totalActualProductPrice.add(item.getBigDecimal("totalActualProductPrice"));
			totalPayRateSum = totalPayRateSum.add(item.getBigDecimal("payRateSum"));
			totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
			totalRefundCash = totalRefundCash.add(item.getBigDecimal("refundCash"));
			totalTaxCost = totalTaxCost.add(item.getBigDecimal("totalTaxCost"));
			totalProductCost = totalProductCost.add(item.getBigDecimal("totalProductCost"));
			totalBalance = totalBalance.add(item.getBigDecimal("balance"));
		}
		
		Record result = new Record();
		result.set("totalProductCost", totalProductCost.doubleValue());
		result.set("totalActualProductPrice", totalActualProductPrice.doubleValue());
		result.set("totalPayRateSum", totalPayRateSum.doubleValue());
		result.set("totalPayable", totalPayable.doubleValue());
		result.set("totalRefundCash", totalRefundCash.doubleValue());
		result.set("totalTaxCost", totalTaxCost.doubleValue());
		result.set("totalBalance", totalBalance.doubleValue());
		return result;
	}
	
	/**
	 * 批量查询销售明细记录
	 * @param offset
	 * @param count
	 * @param payType
	 * @param source
	 * @param status
	 * @param orderCode
	 * @param tradeCode
	 * @param tradeNo
	 * @param shopName
	 * @param supplierName
	 * @param receiveName
	 * @param receivePhone
	 * @param startTime
	 * @param endTime
	 * @param orderByMap
	 * @return
	 */
	public static List<Record> findSaleDetailItems(int offset, int count, Integer payType, Integer source, 
			Integer status, String orderCode, String tradeCode, String tradeNo, String shopName, 
			String supplierName, String startTime, String endTime, Integer supplierId, String whereInPayType) {
		
		String sql = findSaleDetailItemsSql(payType, source, status, orderCode, tradeCode, tradeNo,
				shopName, supplierName, startTime, endTime, supplierId, whereInPayType);
		
		sql = BaseDao.appendLimitSql(sql, offset, count);
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	/**
	 * 批量查询销售明细
	 * @param payType
	 * @param source
	 * @param status
	 * @param orderCode
	 * @param tradeCode
	 * @param tradeNo
	 * @param shopName
	 * @param supplierName
	 * @param receiveName
	 * @param receivePhone
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findSaleDetailItems(Integer payType, Integer source, 
			Integer status, String orderCode, String tradeCode, String tradeNo, String shopName, 
			String supplierName, String startTime, String endTime, Integer supplierId, String whereInPayType) {
		
		String sql = findSaleDetailItemsSql(payType, source, status, orderCode, tradeCode, tradeNo, 
				shopName, supplierName, startTime, endTime, supplierId, whereInPayType);
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	/**
	 * 批量查询销售明细总条数
	 * @param payType
	 * @param source
	 * @param status
	 * @param orderCode
	 * @param tradeCode
	 * @param tradeNo
	 * @param shopName
	 * @param supplierName
	 * @param receiveName
	 * @param receivePhone
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int countSaleDetailItems(Integer payType, Integer source, 
			Integer status, String orderCode, String tradeCode, String tradeNo, String shopName, 
			String supplierName, String startTime, String endTime, Integer supplierId, String whereInPayType) {
		
		String sql = findSaleDetailItemsSql(payType, source, status, orderCode, tradeCode, tradeNo, 
				shopName, supplierName, startTime, endTime, supplierId, whereInPayType);
		
		return Db.find(sql).size();
	}
	
	/**
	 * 组装sql语句
	 * @param payType
	 * @param source
	 * @param status 1待发货,2待收货,3已收货
	 * @param orderCode
	 * @param tradeCode
	 * @param tradeNo
	 * @param shopName
	 * @param supplierName
	 * @param receiveName
	 * @param receivePhone
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String findSaleDetailItemsSql(Integer payType, Integer source, 
			Integer status, String orderCode, String tradeCode, String tradeNo, String shopName, 
			String supplierName, String startTime, String endTime, Integer supplierId, String whereInPayType) {
		
		if (status == null) {
			status = 1;
		}
		
		// 订单
		String selectOrder = "select '订单' as tradeType, a.id," +
				" a.codeType, 0 as refundCash, a.payTime, a.sendOutTime, a.receiveTime," +
				" a.theSameOrderNum as tradeCode, a.source, a.payType, a.order_code, a.tradeNo," +
				" b.taxRateSum, b.totalProductCost, (b.actualUnitPrice * b.unitOrdered) as totalActualProductPrice," +
				" a.shopName, b.supplier_name as supplierName, b.product_name, b.unitOrdered, a.status," +
				" b.totalActualDeliveryCharge as deliveryPrice," + 
				" (b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount) as totalPayable," +
				" b.actualUnitPrice, b.taxRate," +
				" a.id as order_id, a.totalPayable as orderPayable," +
				" b.supplier_id";
		String sqlExceptSelectOrder = " from `order` as a" + 
				" left join product_order as b on a.id = b.order_id" +
				" where a.status not in(1, 6)";

		// 退款
		String selectRefund = "select '退款' as tradeType, a.id, c.codeType," + 
				" a.refundCash * -1 refundCash, a.successTime as payTime," + 
				" a.successTime as sendOutTime, a.successTime as receiveTime," + 
				" c.theSameOrderNum as tradeCode, c.source, c.payType, c.order_code, c.tradeNo," + 
				" 0 as taxRateSum, b.totalProductCost * -1 as totalProductCost," + 
				" 0 as totalActualProductPrice," +
				" c.shopName, b.supplier_name as supplierName, b.product_name, b.unitOrdered * -1 as unitOrdered, c.status," +
				" a.deliveryPrice * -1 as deliveryPrice, a.refundCash * -1 as totalPayable," +
				" b.actualUnitPrice * -1 as actualUnitPrice, b.taxRate," +
				" 0 as order_id, 0 as orderPayable," +
				" b.supplier_id";
		String sqlExceptSelectRefund = " from refund as a" + 
				" left join product_order as b on a.product_order_id = b.id" + 
				" left join `order` as c on b.order_id = c.id" + 
				" where a.status = 3";
		
		// 退货
		String selectBack = "select '退货' as tradeType, a.id, c.codeType," + 
				" a.refundCash * -1 as refundCash, a.successTime as payTime," + 
				" a.successTime as sendOutTime, a.successTime as receiveTime," + 
				" c.theSameOrderNum as tradeCode, c.source, c.payType, c.order_code, c.tradeNo," + 
				" 0 as taxRateSum, b.totalProductCost * -1 as totalProductCost," + 
				" 0 as totalActualProductPrice," +
				" c.shopName, b.supplier_name as supplierName, b.product_name, b.unitOrdered * -1 as unitOrdered, c.status," +
				" a.deliveryPrice * -1 as deliveryPrice, a.refundCash * -1 as totalPayable," +
				" b.actualUnitPrice * -1 as actualUnitPrice, b.taxRate," +
				" 0 as order_id, 0 as orderPayable," +
				" b.supplier_id";
		String sqlExceptSelectBack = " from back as a" + 
				" left join product_order as b on a.product_order_id = b.id" + 
				" left join `order` as c on b.order_id = c.id" + 
				" where a.status = 4";
		
		// 充值
		String selectRg = "select '充值' as tradeType, id, 2 as codeType," + 
				" 0 as refundCash, finishTime as payTime, finishTime as sendOutTime," + 
				" finishTime as receiveTime, CONVERT(tradeNo,SIGNED) as tradeCode," + 
				" source, case event when 3 then 2 when 4 then 1 when 6 then 3 end as payType," +
				" tradeNo as order_code, transactionId as tradeNo," + 
				" 0 as taxRateSum, 0 as totalProductCost, 0 as totalActualProductPrice," +
				" '' as shopName, '' as supplierName, '' as product_name, 0 as unitOrdered," + 
				" 1 as status, 0 as deliveryPrice, money as totalPayable," +
				" 0 as actualUnitPrice, 0 as taxRate," +
				" 0 as order_id, money as orderPayable," +
				" 0 as supplier_id";
		String sqlExceptSelectRg = " from wallet" + 
				" where event in (3, 4, 6)" + 
				" and isPaySuccess = 1" + 
				" and transactionId is not null";
		
		switch (status) {
		case 3:
			sqlExceptSelectBack += " and c.receiveTime != ''";
			break;
		}
		
		String sql = selectOrder + sqlExceptSelectOrder + 
				" union all " + 
				selectRefund + sqlExceptSelectRefund + 
				" union all " +
				selectBack + sqlExceptSelectBack + 
				" union all " + 
				selectRg + sqlExceptSelectRg;

		String select = "select *";
		String sqlExceptSelect = " from (" + sql + ") as t" + " where id != 0";
		
		if (payType != null) {
			sqlExceptSelect += " and payType = " + payType;
		}
		
		if (whereInPayType != null) {
			sqlExceptSelect += " and payType in " + whereInPayType;
		}
		
		if (orderCode != null && !orderCode.equals("")) {
			sqlExceptSelect += " and order_code like '%" + orderCode + "%'";
		}
			
		if (source != null) {
			sqlExceptSelect += " and source = " + source;
		}
		
		if (tradeCode != null && !tradeCode.equals("")) {
			sqlExceptSelect += " and theSameOrderNum like '%" + tradeCode + "%'";
		}
		
		if (tradeNo != null && !tradeNo.equals("")) {
			sqlExceptSelect += " and tradeNo like '%" + tradeNo + "%'";
		}
		
		if (shopName != null && !shopName.equals("")) {
			sqlExceptSelect += " and shopName like '%" + shopName + "%'";
		}
		
		if (supplierName != null && !supplierName.equals("")) {
			sqlExceptSelect += " and supplierName like '%" + supplierName + "%'";
		}
		
		if (supplierId != null) {
			sqlExceptSelect += " and supplier_id = " + supplierId;
		}
		
		if (status == 1) { // 待发货
			if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			
		} else if (status == 2) { // 待收货
			if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			
			sqlExceptSelect += " and (tradeType = '订单' or tradeType = '退货')";
			
		} else if (status == 3) { // 已收货
			if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			
			sqlExceptSelect += " and (tradeType = '订单' or tradeType = '退货')";
			
		}
		
		sqlExceptSelect += " order by payTime desc";
		
		return select + sqlExceptSelect;
	}
	
	/**
	 * 计算销售明细总金额
	 * @param list
	 * @return
	 */
	public static Record calculateSaleDetailItems(List<Record> list) {
		BigDecimal totalActualProductPrice = new BigDecimal(0);
		BigDecimal totalPayable = new BigDecimal(0);
		BigDecimal totalProductCost = new BigDecimal(0);
		BigDecimal totalRefundCash = new BigDecimal(0);
		BigDecimal totalTaxCost = new BigDecimal(0);
		BigDecimal totalDeliveryPrice = new BigDecimal(0);
		BigDecimal totalCost = new BigDecimal(0);
		
		for (Record item : list) {
			totalActualProductPrice = totalActualProductPrice.add(item.getBigDecimal("totalActualProductPrice"));
			totalRefundCash = totalRefundCash.add(item.getBigDecimal("refundCash"));
			totalTaxCost = totalTaxCost.add(item.getBigDecimal("taxRateSum"));
			totalProductCost = totalProductCost.add(item.getBigDecimal("totalProductCost"));
			totalDeliveryPrice = totalDeliveryPrice.add(item.getBigDecimal("deliveryPrice"));
			totalCost = totalProductCost.add(totalDeliveryPrice);
			totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
		}
		
		Record result = new Record();
		result.set("totalPayable", totalPayable.doubleValue());
		result.set("totalProductCost", totalProductCost.doubleValue());
		result.set("totalActualProductPrice", totalActualProductPrice.doubleValue());
		result.set("totalRefundCash", totalRefundCash.doubleValue());
		result.set("totalTaxCost", totalTaxCost.doubleValue());
		result.set("totalDeliveryPrice", totalDeliveryPrice.doubleValue());
		result.set("totalCost", totalCost.doubleValue());
		return result;
	}
	
	/**
	 * 查询销售汇总记录
	 * @param offset
	 * @param length
	 * @param supplierId
	 * @param supplierName
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findOrderSummaryItems(int offset, int length, Integer supplierId, 
			String supplierName, Integer status, String startTime, String endTime) {
		
		String sql = findOrderSummaryItemsSql(supplierId, supplierName, status, startTime, endTime);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	public static List<Record> findOrderSummaryItems(Integer supplierId, String supplierName, 
			Integer status, String startTime, String endTime) {
		
		String sql = findOrderSummaryItemsSql(supplierId, supplierName, status, startTime, endTime);
		
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	public static Record calculateOrderSummaryItems(List<Record> list) {
		BigDecimal totalPayable = new BigDecimal(0);
		BigDecimal totalRefundCash = new BigDecimal(0);
		BigDecimal totalCouponDiscount = new BigDecimal(0);
		BigDecimal totalWeixinpay = new BigDecimal(0);
		BigDecimal totalAlipay = new BigDecimal(0);
		BigDecimal totalUnionpay = new BigDecimal(0);
		BigDecimal totalWalletpay = new BigDecimal(0);
		BigDecimal totalCardpay = new BigDecimal(0);
		BigDecimal totalPointpay = new BigDecimal(0);
		int totalUnitOrdered = 0;
		int totalRefundAmount = 0;
		
		for (Record item : list) {
			totalPayable = totalPayable.add(item.getBigDecimal("payable"));
			totalRefundCash = totalRefundCash.add(item.getBigDecimal("refundCash"));
			totalCouponDiscount = totalCouponDiscount.add(item.getBigDecimal("couponDiscount"));
			totalWeixinpay = totalWeixinpay.add(item.getBigDecimal("weixinpay"));
			totalAlipay = totalAlipay.add(item.getBigDecimal("alipay"));
			totalUnionpay = totalUnionpay.add(item.getBigDecimal("unionpay"));
			totalWalletpay = totalWalletpay.add(item.getBigDecimal("walletpay"));
			totalCardpay = totalCardpay.add(item.getBigDecimal("cardpay"));
			totalPointpay = totalPointpay.add(item.getBigDecimal("pointpay"));
			totalUnitOrdered += item.getNumber("unitOrdered").intValue();
			totalRefundAmount += item.getNumber("refundAmount").intValue();
		}
		
		Record result = new Record();
		result.set("totalPayable", totalPayable.doubleValue());
		result.set("totalRefundCash", totalRefundCash.doubleValue());
		result.set("totalCouponDiscount", totalCouponDiscount.doubleValue());
		result.set("totalWeixinpay", totalWeixinpay.doubleValue());
		result.set("totalAlipay", totalAlipay.doubleValue());
		result.set("totalUnionpay", totalUnionpay.doubleValue());
		result.set("totalWalletpay", totalWalletpay.doubleValue());
		result.set("totalCardpay", totalCardpay.doubleValue());
		result.set("totalPointpay", totalPointpay.doubleValue());
		result.set("totalUnitOrdered", totalUnitOrdered);
		result.set("totalRefundAmount", totalRefundAmount);
		
		return result;
	}
	
	private static String findOrderSummaryItemsSql(Integer supplierId, String supplierName, 
			Integer status, String startTime, String endTime) {
		
		String selectOrder = "select '订单' as tradeType, b.supplier_id, b.supplier_name, b.taxRate," + 
				" (b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount) as payable," + 
				" b.couponDiscount, b.unitOrdered, 0 refundCash, 0 as refundAmount," +
				" CASE WHEN a.payType = 1 THEN b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount ELSE 0 END as weixinpay," +
				" CASE WHEN a.payType = 2 THEN b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount ELSE 0 END as alipay," +
				" CASE WHEN a.payType = 3 THEN b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount ELSE 0 END as unionpay," +
				" CASE WHEN a.payType = 4 THEN b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount ELSE 0 END as walletpay," +
				" CASE WHEN a.payType = 5 THEN b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount ELSE 0 END as cardpay," +
				" CASE WHEN a.payType = 6 THEN b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount ELSE 0 END as pointpay," +
				" a.orderTime, a.payTime, a.sendOutTime, a.receiveTime";
		String sqlExceptSelectOrder = " from `order` as a" + 
				" left join product_order as b on a.id = b.order_id" +
				" where a.status not in (1,6)";
		
		String selectRefund = "select '退款' as tradeType, b.supplier_id, b.supplier_name, b.taxRate," + 
				" a.refundCash * -1 as payable," + 
				" 0 as couponDiscount, b.unitOrdered*-1 as unitOrdered, a.refundCash, a.refundAmount," +
				" CASE WHEN c.payType = 1 THEN a.refundCash*-1 ELSE 0 END as weixinpay," +
				" CASE WHEN c.payType = 2 THEN a.refundCash*-1 ELSE 0 END as alipay," +
				" CASE WHEN c.payType = 3 THEN a.refundCash*-1 ELSE 0 END as unionpay," +
				" CASE WHEN c.payType = 4 THEN a.refundCash*-1 ELSE 0 END as walletpay," +
				" CASE WHEN c.payType = 5 THEN a.refundCash*-1 ELSE 0 END as cardpay," +
				" CASE WHEN c.payType = 6 THEN a.refundCash*-1 ELSE 0 END as pointpay," +
				" a.successTime as orderTime, a.successTime as payTime," + 
				" a.successTime as sendOutTime, a.successTime as receiveTime";
		String sqlExceptSelectRefund = " from refund as a " +
				" left join product_order as b on a.product_order_id = b.id" + 
				" left join `order` as c on b.order_id = c.id" +
				" where a.status = 3";
		
		String selectBack = "select '退货' as tradeType, b.supplier_id, b.supplier_name, b.taxRate," + 
				" a.refundCash * -1 as payable," + 
				" 0 as couponDiscount, b.unitOrdered*-1 as unitOrdered, a.refundCash, a.refundAmount," +
				" CASE WHEN c.payType = 1 THEN a.refundCash*-1 ELSE 0 END as weixinpay," +
				" CASE WHEN c.payType = 2 THEN a.refundCash*-1 ELSE 0 END as alipay," +
				" CASE WHEN c.payType = 3 THEN a.refundCash*-1 ELSE 0 END as unionpay," +
				" CASE WHEN c.payType = 4 THEN a.refundCash*-1 ELSE 0 END as walletpay," +
				" CASE WHEN c.payType = 5 THEN a.refundCash*-1 ELSE 0 END as cardpay," +
				" CASE WHEN c.payType = 6 THEN a.refundCash*-1 ELSE 0 END as pointpay," +
				" a.successTime as orderTime, a.successTime as payTime," + 
				" a.successTime as sendOutTime, a.successTime as receiveTime";
		String sqlExceptSelectBack = " from back as a " +
				" left join product_order as b on a.product_order_id = b.id" + 
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
		
		String select = "select tradeType, supplier_id, supplier_name, taxRate, sum(payable) as payable," + 
				" sum(couponDiscount) as couponDiscount, sum(unitOrdered) as unitOrdered," +
				" sum(refundCash) refundCash, sum(refundAmount) as refundAmount," +
				" sum(weixinpay) as weixinpay, sum(alipay) as alipay, sum(unionpay) as unionpay," + 
				" sum(walletpay) as walletpay, sum(cardpay) as cardpay, sum(pointpay) as pointpay";
		String sqlExceptSelect = " from (" + sql + ") as t" +
				" where supplier_id != 0";
		
		String orderBy = "";
		if (supplierId != null) {
			sqlExceptSelect += " and supplier_id = " + supplierId;
		}
		if (supplierName != null && !supplierName.equals("")) {
			sqlExceptSelect += " and supplier_name like '%" + supplierName + "%'";
		}
		if (status == 1) {  // 已支付
			if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			orderBy = " order by payTime desc";
			
		} else if (status == 2) {  // 已发货
			if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			
			sqlExceptSelect += " and (tradeType = '订单' or tradeType = '退货')";
			orderBy = " order by sendOutTime desc";
			
		} else if (status == 3) {  // 已收货
			if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			
			sqlExceptSelect += " and (tradeType = '订单' or tradeType = '退货')";
			orderBy = " order by receiveTime desc";
		}
		
		sqlExceptSelect += " group by supplier_id, taxRate";
		sqlExceptSelect += orderBy;
		
		return select + sqlExceptSelect;
	}
	
	/**
	 * 财务管理-销售汇总
	 * @param length 
	 * @param pageIndex 
	 */
	public static Page<ProductOrder> getOrderSummaryList(int pageIndex, int length, Integer supplierId, String supplierName, Integer status, String startDate, String endDate) {
		QueryHelper helper = appendWhereCaluse(supplierId, supplierName, status, startDate, endDate);
		 StringBuffer buffer = new StringBuffer(2000);
		 StringBuffer backSql = new StringBuffer(200);
		 backSql.append(" SELECT b.created_at, c.status, b.supplier_id, b.supplier_name AS supplierName, b.taxRate , SUM(b.totalActualProductPrice) * -1 AS totalActualProductPrice, SUM(b.couponDiscount) * -1 AS couponDiscount, SUM(b.unitOrdered) * -1 AS unitOrdered, SUM(a.refundAmount) * -1 AS refundAmount, SUM(a.refundCash) * -1 AS refundCash, ");
		 backSql.append("SUM(CASE WHEN c.payType = 2 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS zhifubaoPrice, SUM(CASE WHEN c.payType = 1 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS weixinPrice, SUM(CASE WHEN c.payType = 3 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS yinlianPrice ");
		 backSql.append(" FROM back AS a LEFT JOIN `product_order` AS b ");
		 backSql.append(" ON a.product_order_id=b.id ");
		 backSql.append(" LEFT JOIN `order` AS c ON b.order_id=c.id ");
		 backSql.append(" WHERE DATE_FORMAT(c.created_at, '%Y-%m-%d')>= "+ "'"+startDate+"'");
		 backSql.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')<= "+ "'"+endDate+"'");
		 backSql.append(" GROUP BY b.supplier_name, b.taxRate ");
		 buffer.append(" FROM (");
		 if(status == 4) {
			 buffer.append(backSql);
		 }else {
			 buffer.append(" SELECT a.created_at, a.status, a.supplier_id, a.supplier_name AS supplierName, a.taxRate, SUM(a.totalActualProductPrice) AS totalActualProductPrice , SUM(a.couponDiscount) AS couponDiscount, SUM(a.unitOrdered) AS unitOrdered, 0 AS refundAmount, ");
			 buffer.append(" 0 AS refundCash,SUM(CASE WHEN b.payType = 2 THEN a.totalActualProductPrice ELSE 0 END) AS zhifubaoPrice, SUM(CASE WHEN b.payType = 1 THEN a.totalActualProductPrice ELSE 0 END) AS weixinPrice, SUM(CASE WHEN b.payType = 3 THEN a.totalActualProductPrice ELSE 0 END) AS yinlianPrice ");
			 buffer.append(" FROM product_order AS a LEFT JOIN `order` AS b ");
			 buffer.append(" ON a.order_id=b.id ");
			 buffer.append(appendStatus(status));
			 buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')>= "+ "'"+startDate+"'");
			 buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')<= "+ "'"+endDate+"'");
			 buffer.append(" GROUP BY a.supplier_name, a.taxRate ");
			 buffer.append(" UNION ");
			 buffer.append(backSql);
			 buffer.append(" UNION ");
			 buffer.append(" SELECT b.created_at, c.status, b.supplier_id, b.supplier_name AS supplierName, b.taxRate, SUM(b.totalActualProductPrice) * -1 AS totalActualProductPrice, SUM(b.couponDiscount) * -1 AS couponDiscount, SUM(b.unitOrdered) * -1 AS unitOrdered, SUM(a.refundAmount) * -1 AS refundAmount, SUM(a.refundCash) * -1 AS refundCash, ");
			 buffer.append(" SUM(CASE WHEN c.payType = 2 THEN b.totalActualProductPrice ELSE 0.00 END)*-1 AS zhifubaoPrice, SUM(CASE WHEN c.payType = 1 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS weixinPrice, SUM(CASE WHEN c.payType = 3 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS yinlianPrice");
			 buffer.append(" FROM refund AS a LEFT JOIN `product_order` AS b ");
			 buffer.append(" ON a.product_order_id=b.id ");
			 buffer.append(" LEFT JOIN `order` AS c ON b.order_id=c.id ");
			 buffer.append(" WHERE DATE_FORMAT(c.created_at, '%Y-%m-%d')>= "+ "'"+startDate+"'");
			 buffer.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')<= "+ "'"+endDate+"'");
			 buffer.append(" GROUP BY b.supplier_name, b.taxRate ");
		 }
		 buffer.append(" ) as o");
		 buffer.append(helper.getWhereClause());
		 
		 Page<ProductOrder> paginate = ProductOrder.dao.paginate(pageIndex, length, "select o.*", buffer.toString(), helper.getParams().toArray());
		 return paginate;

	}
	/**
	 * 财务管理-销售汇总
	 */
	public static  List<ProductOrder> getAllOrderSummaryList(Integer supplierId, String supplierName, Integer status, String startDate, String endDate) {
		 QueryHelper helper = appendWhereCaluse(supplierId, supplierName, status, startDate, endDate);
		 StringBuffer buffer = new StringBuffer(2000);
		 StringBuffer backSql = new StringBuffer(200);
		 backSql.append(" SELECT b.created_at, c.status, b.supplier_id, b.supplier_name AS supplierName, b.taxRate , SUM(b.totalActualProductPrice) * -1 AS totalActualProductPrice, SUM(b.couponDiscount) * -1 AS couponDiscount, SUM(b.unitOrdered) * -1 AS unitOrdered, SUM(a.refundAmount) * -1 AS refundAmount, SUM(a.refundCash) * -1 AS refundCash, ");
		 backSql.append(" SUM(CASE WHEN c.payType = 2 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS zhifubaoPrice, SUM(CASE WHEN c.payType = 1 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS weixinPrice, SUM(CASE WHEN c.payType = 3 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS yinlianPrice ");
		 backSql.append(" FROM back AS a LEFT JOIN `product_order` AS b ");
		 backSql.append(" ON a.product_order_id=b.id ");
		 backSql.append(" LEFT JOIN `order` AS c ON b.order_id=c.id ");
		 backSql.append(" WHERE DATE_FORMAT(c.created_at, '%Y-%m-%d')>= "+ "'"+startDate+"'");
		 backSql.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')<= "+ "'"+endDate+"'");
		 backSql.append(" GROUP BY b.supplier_name, b.taxRate ");
		 buffer.append(" SELECT o.* FROM ( ");
		 if(status == 4) {
			 buffer.append(backSql);
		 }else {
			 buffer.append(" SELECT a.created_at, a.status, a.supplier_id, a.supplier_name AS supplierName, a.taxRate, SUM(a.totalActualProductPrice) AS totalActualProductPrice , SUM(a.couponDiscount) AS couponDiscount, SUM(a.unitOrdered) AS unitOrdered, 0 AS refundAmount, ");
			 buffer.append(" 0 AS refundCash,SUM(CASE WHEN b.payType = 2 THEN a.totalActualProductPrice ELSE 0 END) AS zhifubaoPrice, SUM(CASE WHEN b.payType = 1 THEN a.totalActualProductPrice ELSE 0 END) AS weixinPrice, SUM(CASE WHEN b.payType = 3 THEN a.totalActualProductPrice ELSE 0 END) AS yinlianPrice ");
			 buffer.append(" FROM product_order AS a LEFT JOIN `order` AS b ");
			 buffer.append(" ON a.order_id=b.id ");
			 buffer.append(appendStatus(status));
		     buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')>= "+ "'"+startDate+"'");
			 buffer.append(" AND DATE_FORMAT(b.created_at, '%Y-%m-%d')<= "+ "'"+endDate+"'");
			 buffer.append(" GROUP BY a.supplier_name, a.taxRate ");
			 buffer.append(" UNION ");
			 buffer.append(backSql);
			 buffer.append(" UNION ");
			 buffer.append(" SELECT b.created_at, c.status, b.supplier_id, b.supplier_name AS supplierName, b.taxRate, SUM(b.totalActualProductPrice) * -1 AS totalActualProductPrice, SUM(b.couponDiscount) * -1 AS couponDiscount, SUM(b.unitOrdered) * -1 AS unitOrdered, SUM(a.refundAmount) * -1 AS refundAmount, SUM(a.refundCash) * -1 AS refundCash, ");
			 buffer.append(" SUM(CASE WHEN c.payType = 2 THEN b.totalActualProductPrice ELSE 0.00 END)*-1 AS zhifubaoPrice, SUM(CASE WHEN c.payType = 1 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS weixinPrice, SUM(CASE WHEN c.payType = 3 THEN b.totalActualProductPrice ELSE 0 END)*-1 AS yinlianPrice");
			 buffer.append(" FROM refund AS a LEFT JOIN `product_order` AS b ");
			 buffer.append(" ON a.product_order_id=b.id ");
			 buffer.append(" LEFT JOIN `order` AS c ON b.order_id=c.id ");
			 buffer.append(" WHERE DATE_FORMAT(c.created_at, '%Y-%m-%d')>= "+ "'"+startDate+"'");
			 buffer.append(" AND DATE_FORMAT(c.created_at, '%Y-%m-%d')<= "+ "'"+endDate+"'");
			 buffer.append(" GROUP BY b.supplier_name, b.taxRate ");
		 }
		
		 buffer.append(" ) AS o ");
		 buffer.append(helper.getWhereClause());
		 
		List<ProductOrder> list = ProductOrder.dao.find(buffer.toString(), helper.getParams().toArray());
		 return list;
	
	}
	public static QueryHelper appendWhereCaluse(Integer supplierId, String supplierName, Integer status, String startDate, String endDate) {
		QueryHelper helper = new QueryHelper();
		if(supplierId != null) {
			helper.addCondition("o.supplier_id=?", supplierId);
		}
		//1未发货，2在途，3已收货，4退货
		//1: 待付款,2: 待发货,3: 待收货,4: 待评价,5: 已评价,6: 已取消, 7订单完成）
		if(status != null ) {
			switch (status) {
			case 1:
				helper.addCondition("o.status=?", 2);
				break;
			case 2:
				helper.addCondition("o.status=?", 3);
				break;
			case 3:
				helper.addCondition("o.status IN(4,5,7)", null);
				break;
			default:
				helper.addCondition("o.status NOT IN(1,6)", null);
				break;
			}
			
		}else {
			helper.addCondition("o.status NOT IN(1,6)", null);
		}
	
		if(supplierName != null && supplierName.length() > 0) {
			helper.addCondition("o.supplierName like ?", "%"+supplierName+"%");
		}
		return helper;
		
	}
	/**
	 * 
	 * @param status
	 * @return
	 */
	public static String appendStatus(Integer status) {
		//1未发货，2在途，3已收货，4退货
		//1: 待付款,2: 待发货,3: 待收货,4: 待评价,5: 已评价,6: 已取消, 7订单完成）
		QueryHelper helper = new QueryHelper();
		if(status != null ) {
			switch (status) {
			case 1:
				helper.addCondition("b.status=2", null);
				break;
			case 2:
				helper.addCondition("b.status=3", null);
				break;
			case 3:
				helper.addCondition("b.status IN(4,5,7)", null);
				break;
			default:
				helper.addCondition("b.status NOT IN(1,6)", null);
				break;
			}
			
		}else {
			helper.addCondition("b.status NOT IN(1,6)", null);
		}
		return helper.getQuerySql();
	}
	
	/**
	 * 计算销售汇总每列数据的和
	 * @param list
	 * @return
	 */
	public static Record totalAllOrderSummaryList(List<ProductOrder> list){
		//总销售金额
		BigDecimal totalActualProductPrice = new BigDecimal(0);
		//总支付宝支付金额
		BigDecimal totalZhifubaoPrice = new BigDecimal(0);
		//总退款次数 
		BigDecimal totalRefundAmount = new BigDecimal(0);
		//总银联支付金额
		BigDecimal totalYinlianPrice = new BigDecimal(0);
		//总退款金额
		BigDecimal totalRefundCash = new BigDecimal(0);
		//总交易次数
		BigDecimal totalUnitOrdered = new BigDecimal(0);
		//总微信支付金额
		BigDecimal totalWeixinPrice = new BigDecimal(0);
		//总优惠金额
		BigDecimal totalCouponDiscount = new BigDecimal(0);
		//将列表中对应的行相加
		if(list != null) {
			for(ProductOrder order: list) {
				totalActualProductPrice = totalActualProductPrice.add(order.getBigDecimal("totalActualProductPrice"));
				totalZhifubaoPrice = totalZhifubaoPrice.add(order.getBigDecimal("zhifubaoPrice"));
				totalRefundAmount = totalRefundAmount.add(order.getBigDecimal("refundAmount"));
				totalCouponDiscount = totalCouponDiscount.add(order.getBigDecimal("couponDiscount"));
				totalUnitOrdered = totalUnitOrdered.add(order.getBigDecimal("unitOrdered"));
				totalRefundCash = totalRefundCash.add(order.getBigDecimal("refundCash"));
				totalWeixinPrice =totalWeixinPrice.add(order.getBigDecimal("weixinPrice"));
				totalYinlianPrice = totalYinlianPrice.add(order.getBigDecimal("yinlianPrice"));
			}
		}
		
		Record total = new Record();
		total.set("totalActualProductPrice", totalActualProductPrice.doubleValue());
		total.set("totalZhifubaoPrice", totalZhifubaoPrice.doubleValue());
		total.set("totalRefundAmount", totalRefundAmount.intValue());
		total.set("totalCouponDiscount", totalCouponDiscount.doubleValue());
		total.set("totalUnitOrdered", totalUnitOrdered.intValue());
		total.set("totalRefundCash", totalRefundCash.doubleValue());
		total.set("totalWeixinPrice", totalWeixinPrice.doubleValue());
		total.set("totalYinlianPrice", totalYinlianPrice.doubleValue());
		return total;
	}
		
}
