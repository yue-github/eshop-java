package com.eshop.finance;

import java.math.BigDecimal;
import java.util.List;

import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ShouZhiService {
	
	public static List<Record> findShouZhiItems(int offset, int length, String startTime, String endTime, 
			Integer supplierId, String supplierName) {
		
		String sql = findShouZhiItemsSql(startTime, endTime, supplierId, supplierName);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		list = appendItems(list, startTime, endTime);
		return list;
	}
	
	public static List<Record> findShouZhiItems(String startTime, String endTime, Integer supplierId, 
			String supplierName) {
		
		String sql = findShouZhiItemsSql(startTime, endTime, supplierId, supplierName);
		List<Record> list = Db.find(sql);
		list = appendItems(list, startTime, endTime);
		return list;
	}
	
	public static Record calculateShouZhiItems(List<Record> list) {
		BigDecimal totalNoSend = new BigDecimal(0);
		BigDecimal totalSending = new BigDecimal(0);
		BigDecimal totalConfirmed = new BigDecimal(0);
		BigDecimal totalRefund = new BigDecimal(0);
		BigDecimal totalBack = new BigDecimal(0);
		BigDecimal totalBalance = new BigDecimal(0);
		
		for (Record item : list) {
			totalNoSend = totalNoSend.add(item.getBigDecimal("nosend"));
			totalSending = totalSending.add(item.getBigDecimal("sending"));
			totalConfirmed = totalConfirmed.add(item.getBigDecimal("confirmed"));
			totalRefund = totalRefund.add(item.getBigDecimal("refund"));
			totalBack = totalBack.add(item.getBigDecimal("back"));
			totalBalance = totalBalance.add(item.getBigDecimal("balance"));
		}
		
		totalNoSend = totalNoSend.add(totalRefund);
		BigDecimal zero = new BigDecimal(0);
		if (totalNoSend.compareTo(zero) < 0) {
			totalNoSend = zero;
		}
		
		Record result = new Record();
		result.set("totalNoSend", totalNoSend);
		result.set("totalSending", totalSending);
		result.set("totalConfirmed", totalConfirmed);
		result.set("totalRefund", totalRefund);
		result.set("totalBack", totalBack);
		result.set("totalBalance", totalBalance);
		return result;
	}
	
	private static List<Record> appendItems(List<Record> list, String startTime, String endTime) {
		for (Record item : list) {
			String datetime = startTime + "至" + endTime;
			item.set("datetime", datetime);
		}
		return list;
	}
	
	private static String findShouZhiItemsSql(String startTime, String endTime, Integer supplierId, String supplierName) {
		// 未发货
		String nosendSql = "select (b.totalActualProductPrice+b.totalActualDeliveryCharge) as nosend, 0 as sending, 0 as confirmed, 0 as refund," +
				" 0 as back, (b.totalActualProductPrice+b.totalActualDeliveryCharge) as balance, b.supplier_id, b.supplier_name" +
				" from `order` as a" +
				" left join product_order as b on a.id = b.order_id" +
				" where a.status = 2" +
				" and DATE_FORMAT(payTime, '%Y-%m-%d') >= '" + startTime + "'" +
				" and DATE_FORMAT(payTime, '%Y-%m-%d') <= '" + endTime + "'";
		
		// 已发货
		String sendingSql = "select 0 as nosend, (b.totalActualProductPrice+b.totalActualDeliveryCharge) as sending, 0 as confirmed, 0 as refund," +
				" 0 as back, (b.totalActualProductPrice+b.totalActualDeliveryCharge) as balance, b.supplier_id, b.supplier_name" +
				" from `order` as a" +
				" left join product_order as b on a.id = b.order_id" +
				" where a.status = 3" +
				" and DATE_FORMAT(sendOutTime, '%Y-%m-%d') >= '" + startTime + "'" +
				" and DATE_FORMAT(sendOutTime, '%Y-%m-%d') <= '" + endTime + "'";
		
		// 已收货
		String confirmedSql = "select 0 as nosend, 0 as sending, (b.totalActualProductPrice+b.totalActualDeliveryCharge) as confirmed, 0 as refund," +
				" 0 as back, (b.totalActualProductPrice+b.totalActualDeliveryCharge) as balance, b.supplier_id, b.supplier_name" +
				" from `order` as a" +
				" left join product_order as b on a.id = b.order_id" +
				" where a.status in (4,5,7)" +
				" and DATE_FORMAT(receiveTime, '%Y-%m-%d') >= '" + startTime + "'" +
				" and DATE_FORMAT(receiveTime, '%Y-%m-%d') <= '" + endTime + "'";
		
		// 退款
		String refundSql = "select 0 as nosend, 0 as sending, 0 as confirmed, (refundCash*-1) as refund," +
				" 0 as back, (refundCash*-1) as balance, supplier_id, supplier_name" +
				" from `refund` as a" +
				" left join product_order as b on a.product_order_id = b.id" +
				" where a.status = 3" +
				" and DATE_FORMAT(successTime, '%Y-%m-%d') >= '" + startTime + "'" +
				" and DATE_FORMAT(successTime, '%Y-%m-%d') <= '" + endTime + "'";
		
		// 退货
		String backSql = "select 0 as nosend, 0 as sending, 0 as confirmed, 0 as refund," +
				" (refundCash*-1) as back, (refundCash*-1) as balance, supplier_id, supplier_name" +
				" from `back` as a" +
				" left join product_order as b on a.product_order_id = b.id" +
				" where a.status = 4" +
				" and DATE_FORMAT(successTime, '%Y-%m-%d') >= '" + startTime + "'" +
				" and DATE_FORMAT(successTime, '%Y-%m-%d') <= '" + endTime + "'";
		
		String sql = nosendSql +
				" union all " +
				sendingSql +
				" union all " +
				confirmedSql +
				" union all " +
				refundSql +
				" union all " +
				backSql;
		
		sql = "select sum(nosend) as nosend, sum(sending) as sending, sum(confirmed) as confirmed," + 
				" sum(refund) as refund, sum(back) as back, sum(balance) as balance, supplier_id," + 
				" supplier_name" +
				" from (" + sql + ") as t" +
				" where supplier_id != 0";
		
		if (supplierId != null) {
			sql += " and supplier_id = " + supplierId;
		}
		if (supplierName != null && supplierName.equals("")) {
			sql += " and supplier_name like '%" + supplierName + "%'";
		}
		
		sql += " group by supplier_id";
		
		return sql;
	}
	
}
