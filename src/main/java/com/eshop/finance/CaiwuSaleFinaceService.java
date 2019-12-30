package com.eshop.finance;

import java.math.BigDecimal;
import java.util.List;

import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class CaiwuSaleFinaceService {
	
	/**
	 * 批量查询财务销售汇总记录
	 * @param payType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findSaleSummaryItems(Integer payType, String startTime, String endTime,
			String whereInPayType) {
		
		String sql = findSaleSummaryItemsSql(payType, startTime, endTime, whereInPayType);
		List<Record> list = Db.find(sql);
		
		for (Record item : list) {
			BigDecimal totalCost = item.getBigDecimal("totalProductCost").add(item.getBigDecimal("deliveryPrice"));
			BigDecimal balance = item.getBigDecimal("totalPayable").subtract(item.getBigDecimal("payRateSum"));
			item.set("totalCost", totalCost);
			item.set("balance", balance);
			item.set("totalRefundDelivery", item.getBigDecimal("deliveryPrice"));
		}
		
		return list;
	}
	
	/**
	 * 批量查询财务销售汇总记录的总条数
	 * @param list
	 * @return
	 */
	public static int countSaleSummaryItems(List<Record> list) {
		return list.size();
	}
	
	/**
	 * 组装sql语句
	 * @param payType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String findSaleSummaryItemsSql(Integer payType, String startTime, String endTime,
			String whereInPayType) {
		
		//订单
		String selectOrder = "select '订单' as tradeType, payType, totalPayable, 0 as refundCash," + 
				" payRateSum, taxRateSum, payTime, deliveryPrice, totalProductCost," +
				" totalActualProductPrice";
		String sqlExpectSelectOrder = " from `order`" +
				" where status not in (1, 6)";
		
		//充值
		String selectRg = "select '充值' as tradeType," +
				" case event when 3 then 2 when 4 then 1 when 6 then 3 end as payType," +
				" money as totalPayable, 0 as refundCash, 0 as payRateSum, 0 as taxRateSum," + 
				" finishTime as payTime, 0 as deliveryPrice, 0 as totalProductCost," + 
				" 0 as totalActualProductPrice";
		String sqlExpectSelectRg = " from wallet" +
				" where event in (3, 4, 6)" +
				" and isPaySuccess = 1" +
				" and transactionId is not null";
		
		//退款
		String selectRefund = "select '退款' as tradeType, payType," +
				" (refundCash * -1) as totalPayable, refundCash, 0 as payRateSum," + 
				" 0 as taxRateSum, successTime as payTime, a.deliveryPrice * -1 as deliveryPrice," + 
				" b.totalProductCost * -1 as totalProductCost," + 
				" b.totalActualProductPrice * -1 as totalActualProductPrice";
		String sqlExpectSelectRefund = " from refund as a" +
				" left join product_order as b on a.product_order_id = b.id" +
				" left join `order` as c on b.order_id = c.id" +
				" where a.status = 3";
		
		//退货
		String selectBack = "select '退货' as tradeType, payType," + 
				" (refundCash * -1) as totalPayable, refundCash, 0 as payRateSum," + 
				" 0 as taxRateSum, successTime as payTime, a.deliveryPrice * -1 as deliveryPrice," + 
				" b.totalProductCost * -1 as totalProductCost," + 
				" b.totalActualProductPrice * -1 as totalActualProductPrice";
		String sqlExpectSelectBack = " from back as a" +
				" left join product_order as b on a.product_order_id = b.id" +
				" left join `order` as c on b.order_id = c.id" +
				" where a.status = 4";
		
		String sql = selectOrder + sqlExpectSelectOrder +
				" union all " +
				selectRg + sqlExpectSelectRg +
				" union all " +
				selectRefund + sqlExpectSelectRefund +
				" union all " +
				selectBack + sqlExpectSelectBack;
		
		String select = "select tradeType, payType, sum(totalPayable) as totalPayable," + 
				" sum(refundCash) as totalRefundCash, sum(payRateSum) as payRateSum," + 
				" sum(taxRateSum) as totalTaxCost, sum(deliveryPrice) as deliveryPrice," + 
				" sum(totalProductCost) as totalProductCost," + 
				" sum(totalActualProductPrice) as totalActualProductPrice";
		String sqlExpectSelect = " from (" + sql + ") as t" +
				" where payType != 4";
		
		//1:微信支付,2:支付宝,3:银联支付
		if (payType != null) {
			sqlExpectSelect += " and payType = " + payType;
		}
		if (startTime != null && !startTime.equals("")) {
			sqlExpectSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') >=" + "'" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sqlExpectSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') <=" + "'" + endTime + "'";
		}
		if (whereInPayType != null && !whereInPayType.equals("")) {
			sqlExpectSelect += " and payType in " + whereInPayType;
		}
		
		sqlExpectSelect += " group by payType";
		
		return select + sqlExpectSelect;
	}
	
	/**
	 * 计算财务销售汇总金额
	 * @param list
	 * @return
	 */
	public static Record calculateSaleSummaryItems(List<Record> list) {
		BigDecimal totalActualProductPrice = new BigDecimal(0);
		BigDecimal totalCost = new BigDecimal(0);
		BigDecimal totalPayRateSum = new BigDecimal(0);
		BigDecimal totalPayable = new BigDecimal(0);
		BigDecimal totalRefundCash = new BigDecimal(0);
		BigDecimal totalTaxCost = new BigDecimal(0);
		BigDecimal totalProductCost = new BigDecimal(0);
		
		for (Record item : list) {
			totalActualProductPrice = totalActualProductPrice.add(item.getBigDecimal("totalActualProductPrice"));
			totalCost = totalCost.add(item.getBigDecimal("totalCost"));
			totalPayRateSum = totalPayRateSum.add(item.getBigDecimal("payRateSum"));
			totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
			totalRefundCash = totalRefundCash.add(item.getBigDecimal("totalRefundCash"));
			totalTaxCost = totalTaxCost.add(item.getBigDecimal("totalTaxCost"));
			totalProductCost = totalProductCost.add(item.getBigDecimal("totalProductCost"));
		}
		
		Record result = new Record();
		result.set("totalActualProductPrice", totalActualProductPrice.doubleValue());
		result.set("totalCost", totalCost.doubleValue());
		result.set("totalPayRateSum", totalPayRateSum.doubleValue());
		result.set("totalPayable", totalPayable.doubleValue());
		result.set("totalRefundCash", totalRefundCash.doubleValue());
		result.set("totalTaxCost", totalTaxCost.doubleValue());
		result.set("totalProductCost", totalTaxCost.doubleValue());
		return result;
	}
	
	/**
	 * 批量查询财务销售明细记录
	 * @param offset
	 * @param length
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
			String supplierName, String startTime, String endTime, Integer orderType, Integer supplierId,
			String whereInPayType) {
		
		String sql = findSaleDetailItemsSql(payType, source, status, orderCode, tradeCode, tradeNo, 
				shopName, supplierName, startTime, endTime, orderType, supplierId, whereInPayType);
		
		sql = BaseDao.appendLimitSql(sql, offset, count);
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	/**
	 * 批量查询财务销售明细
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
			String supplierName, String startTime, String endTime, Integer orderType, Integer supplierId,
			String whereInPayType) {
		
		String sql = findSaleDetailItemsSql(payType, source, status, orderCode, tradeCode, tradeNo, 
				shopName, supplierName, startTime, endTime, orderType, supplierId, whereInPayType);
		
		List<Record> list = Db.find(sql);
		
		return list;
	}
	
	/**
	 * 批量查询财务销售明细总条数
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
			String supplierName, String startTime, String endTime, Integer orderType, Integer supplierId,
			String whereInPayType) {
		
		String sql = findSaleDetailItemsSql(payType, source, status, orderCode, tradeCode, tradeNo, 
				shopName, supplierName, startTime, endTime, orderType, supplierId, whereInPayType);
		
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
	public static String findSaleDetailItemsSql(Integer payType, Integer source, Integer status, 
			String orderCode, String tradeCode, String tradeNo, String shopName, String supplierName, 
			String startTime, String endTime, Integer orderType, Integer supplierId, String whereInPayType) {
		
		if (status == null) {
			status = 0;
		}
		
		// 订单
		String selectOrder = "select '订单' as tradeType, 0 as refundCash, a.payTime, a.sendOutTime," + 
				" a.receiveTime, a.theSameOrderNum as tradeCode, a.source, a.payType, a.order_code, a.tradeNo," +
				" b.totalProductCost, (b.actualUnitPrice * b.unitOrdered) as totalActualProductPrice, b.taxRateSum, a.shopName," +
				" b.supplier_name as supplierName, b.product_name, b.unitOrdered, a.status," +
				" b.totalActualDeliveryCharge as deliveryPrice," +
				" (b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount) as totalPayable," +
				" ROUND((b.totalActualProductPrice + b.totalActualDeliveryCharge) * a.payRate * 0.01, 2) as payRateSum," + 
				" b.actualUnitPrice, b.taxRate, b.order_type," + 
				" a.id as order_id, a.totalPayable as orderPayable," +
				" b.supplier_id";
		String sqlExceptSelectOrder = " from `order` as a" + 
				" left join product_order as b on a.id = b.order_id" +
				" where a.status not in(1, 6)";

		// 退款
		String selectRefund = "select '退款' as tradeType, a.refundCash * -1 as refundCash," + 
				" a.successTime as payTime, a.successTime as sendOutTime," +
				" a.successTime as receiveTime, c.theSameOrderNum as tradeCode, c.source," +
				" c.payType, c.order_code, c.tradeNo, " + 
				" b.totalProductCost * -1 as totalProductCost," + 
				" 0 as totalActualProductPrice," +
				" 0 as taxRateSum, c.shopName, b.supplier_name as supplierName," + 
				" b.product_name, b.unitOrdered * -1 as unitOrdered, c.status," +
				" a.deliveryPrice * -1 as deliveryPrice," + 
				" a.refundCash * -1 as totalPayable, 0 as payRateSum," +
				" b.actualUnitPrice * -1 as actualUnitPrice, b.taxRate, b.order_type," +
				" 0 as order_id, 0 as orderPayable," +
				" b.supplier_id";
		String sqlExceptSelectRefund = " from refund as a" + 
				" left join product_order as b on a.product_order_id = b.id" + 
				" left join `order` as c on b.order_id = c.id" + 
				" where a.status = 3";
		
		// 退货
		String selectBack = "select '退货' as tradeType, a.refundCash * -1 as refundCash," + 
				" a.successTime as payTime, a.successTime as sendOutTime," +
				" a.successTime as receiveTime, c.theSameOrderNum as tradeCode, c.source," +
				" c.payType, c.order_code, c.tradeNo," + 
				" b.totalProductCost * -1 as totalProductCost," + 
				" 0 as totalActualProductPrice," +
				" 0 as taxRateSum, c.shopName, b.supplier_name as supplierName," + 
				" b.product_name, b.unitOrdered * -1 as unitOrdered, c.status," +
				" a.deliveryPrice * -1 as deliveryPrice," +
				" a.refundCash * -1 as totalPayable, 0 as payRateSum," +
				" b.actualUnitPrice * -1 as actualUnitPrice, b.taxRate, b.order_type," +
				" 0 as order_id, 0 as orderPayable," +
				" b.supplier_id";
		String sqlExceptSelectBack = " from back as a" + 
				" left join product_order as b on a.product_order_id = b.id" + 
				" left join `order` as c on b.order_id = c.id"+ 
				" where a.status = 4";
		
		// 充值
		String selectRg = "select '充值' as tradeType, 0 as refundCash," +
				" finishTime as payTime, finishTime as sendOutTime," +
				" finishTime as receiveTime, tradeNo as tradeCode, source," + 
				" case event when 3 then 2 when 4 then 1 when 6 then 3 end as payType," + 
				" tradeNo as order_code, transactionId as tradeNo," + 
				" 0 as totalProductCost, 0 as totalActualProductPrice," +
				" 0 as taxRateSum, '' as shopName, '' as supplierName," +
				" '' as product_name, 0 as unitOrdered, 1 as status," + 
				" 0 as deliveryPrice, money as totalPayable, 0 as payRateSum," +
				" 0 as actualUnitPrice, 0 as taxRate, 0 as order_type," +
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
				" union all "
				+ selectBack + sqlExceptSelectBack + 
				" union all " + 
				selectRg + sqlExceptSelectRg;

		String select = "select *";
		String sqlExceptSelect = " from (" + sql + ") as t" + 
				" where tradeType in ('订单', '退款', '退货', '充值')";

		if (payType != null) {
			sqlExceptSelect += " and payType = " + payType;
		}
		
		if (whereInPayType != null) {
			sqlExceptSelect += " and payType in " + whereInPayType;
		}

		if (orderType != null) {
			sqlExceptSelect += " and order_type = " + orderType;
		}
		
		if (orderCode != null && !orderCode.equals("")) {
			sqlExceptSelect += " and order_code like '%" + orderCode + "%'";
		}
			
		if (source != null) {
			sqlExceptSelect += " and source = " + source;
		}
		
		if (tradeCode != null && !tradeCode.equals("")) {
			sqlExceptSelect += " and tradeCode like '%" + tradeCode + "%'";
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
		
	    if (status == 0) {
			sqlExceptSelect += " and payType != 4";
			if (startTime != null && !startTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') >= '" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				sqlExceptSelect += " and DATE_FORMAT(payTime,'%Y-%m-%d') <= '" + endTime + "'";
			}
			
		} else if (status == 1) { // 待发货
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
	 * 计算财务销售明细总金额
	 * @param list
	 * @return
	 */
	public static Record calculateSaleDetailItems(List<Record> list) {
		BigDecimal totalActualProductPrice = new BigDecimal(0);
		BigDecimal totalProductCost = new BigDecimal(0);
		BigDecimal totalRefundCash = new BigDecimal(0);
		BigDecimal totalTaxCost = new BigDecimal(0);
		BigDecimal totalDeliveryPrice = new BigDecimal(0);
		BigDecimal totalPayable = new BigDecimal(0);
		BigDecimal totalPayRateSum = new BigDecimal(0);
		BigDecimal totalCost = new BigDecimal(0);
		int totalUnitOrdered = 0;
		
		for (Record item : list) {
			totalActualProductPrice = totalActualProductPrice.add(item.getBigDecimal("totalActualProductPrice"));
			totalRefundCash = totalRefundCash.add(item.getBigDecimal("refundCash"));
			totalTaxCost = totalTaxCost.add(item.getBigDecimal("taxRateSum"));
			totalProductCost = totalProductCost.add(item.getBigDecimal("totalProductCost"));
			totalDeliveryPrice = totalDeliveryPrice.add(item.getBigDecimal("deliveryPrice"));
			totalPayRateSum = totalPayRateSum.add(item.getBigDecimal("payRateSum"));
			totalUnitOrdered += item.getNumber("unitOrdered").intValue();
			totalCost = totalProductCost.add(totalDeliveryPrice);
			totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
			
			// 计算总销售额
			/*if (item.getStr("tradeType").equals("订单")) {
				int orderId = item.getNumber("order_id").intValue();
				if (!orderIds.contains(orderId)) {
					totalPayable = totalPayable.add(item.getBigDecimal("orderPayable"));
					orderIds.add(orderId);
				}
			} else if (item.getStr("tradeType").equals("退款")) {
				totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
			} else if (item.getStr("tradeType").equals("退货")) {
				totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
			} else if (item.getStr("tradeType").equals("充值")) {
				totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
			}*/
		}
		
		Record result = new Record();
		result.set("totalProductCost", totalProductCost.doubleValue());
		result.set("totalActualProductPrice", totalActualProductPrice.doubleValue());
		result.set("totalRefundCash", totalRefundCash.doubleValue());
		result.set("totalTaxCost", totalTaxCost.doubleValue());
		result.set("totalDeliveryPrice", totalDeliveryPrice);
		result.set("totalUnitOrdered", totalUnitOrdered);
		result.set("totalPayable", totalPayable.doubleValue());
		result.set("totalPayRateSum", totalPayRateSum.doubleValue());
		result.set("totalCost", totalCost.doubleValue());
		return result;
	}
	
}
