package com.eshop.finance;

import java.math.BigDecimal;
import java.util.List;

import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SaleCostService {
	
	public List<Record> findSaleCostItems(int offset, int length, Integer supplierId, String invoiceType,
			String startTime, String endTime) {
		
		String sql = findSaleCostItemsSql(supplierId, invoiceType, startTime, endTime);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		
		List<Record> list = Db.find(sql);
		list = setItems(list);
		
		return list;
	}
	
	public List<Record> findSaleCostItems(Integer supplierId, String invoiceType, String startTime, 
			String endTime) {
		
		String sql = findSaleCostItemsSql(supplierId, invoiceType, startTime, endTime);
		
		List<Record> list = Db.find(sql);
		list = setItems(list);
		
		return list;
	}
	
	private List<Record> setItems(List<Record> list) {
		for (Record item : list) {
			String invoice = "增值税普通发票";
			if (item.getStr("invoiceType").equals("value_add")) {
				invoice = "增值税专用发票";
			}
			item.set("invoiceType", invoice);
		}
		return list;
	}
	
	public Record calculateSaleCostItems(List<Record> list) {
		BigDecimal totalWeixinpay = new BigDecimal(0);
		BigDecimal totalAlipay = new BigDecimal(0);
		BigDecimal totalUnionpay = new BigDecimal(0);
		BigDecimal totalCardpay = new BigDecimal(0);
		BigDecimal totalPointpay = new BigDecimal(0);
		BigDecimal totalPayable = new BigDecimal(0);
		BigDecimal totalUnitCost = new BigDecimal(0);
		BigDecimal totalUnitCostNoTax = new BigDecimal(0);
		
		for (Record item : list) {
			totalWeixinpay = totalWeixinpay.add(item.getBigDecimal("weixinpay"));
			totalAlipay = totalAlipay.add(item.getBigDecimal("alipay"));
			totalUnionpay = totalUnionpay.add(item.getBigDecimal("unionpay"));
			totalCardpay = totalCardpay.add(item.getBigDecimal("cardpay"));
			totalPointpay = totalPointpay.add(item.getBigDecimal("pointpay"));
			totalPayable = totalPayable.add(item.getBigDecimal("payable"));
			totalUnitCost = totalUnitCost.add(item.getBigDecimal("totalUnitCost"));
			totalUnitCostNoTax = totalUnitCostNoTax.add(item.getBigDecimal("totalUnitCostNoTax"));
		}
		
		Record result = new Record();
		result.set("totalWeixinpay", totalWeixinpay.doubleValue());
		result.set("totalAlipay", totalAlipay.doubleValue());
		result.set("totalUnionpay", totalUnionpay.doubleValue());
		result.set("totalCardpay", totalCardpay.doubleValue());
		result.set("totalPointpay", totalPointpay.doubleValue());
		result.set("totalPayable", totalPayable.doubleValue());
		result.set("totalUnitCost", totalUnitCost.doubleValue());
		result.set("totalUnitCostNoTax", totalUnitCostNoTax.doubleValue());
		
		return result;
	}
	
	private String findSaleCostItemsSql(Integer supplierId, String invoiceType, String startTime, 
			String endTime) {
		
		String selectOrder = "select '订单' as tradeType, b.supplier_id, b.supplier_name, b.taxRate," + 
				" (b.unitCost*b.unitOrdered+b.totalActualDeliveryCharge) as totalUnitCost," + 
				" (b.unitCostNoTax*b.unitOrdered) as totalUnitCostNoTax," +
				" b.invoiceType, (b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount) as payable," +
				" case when a.payType = 1 then b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount else 0 end as weixinpay," +
				" case when a.payType = 2 then b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount else 0 end as alipay," +
				" case when a.payType = 3 then b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount else 0 end as unionpay," +
				" case when a.payType = 5 then b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount else 0 end as cardpay," +
				" case when a.payType = 6 then b.totalActualProductPrice + b.totalActualDeliveryCharge - b.couponDiscount else 0 end as pointpay," +
				" a.orderTime, a.payTime, a.sendOutTime, a.receiveTime";
		String sqlExceptSelectOrder = " from `order` as a" +
				" left join product_order as b on a.id = b.order_id" +
				" where a.status not in (1, 6)";
		
		String selectRefund = "select '退款' as tradeType, b.supplier_id, b.supplier_name, b.taxRate," + 
				" (b.unitCost*b.unitOrdered+b.totalActualDeliveryCharge)*-1 as totalUnitCost," + 
				" (b.unitCostNoTax*b.unitOrdered*-1) as totalUnitCostNoTax," +
				" b.invoiceType, a.refundCash * -1 as payable," +
				" case when c.payType = 1 then a.refundCash * -1 else 0 end as weixinpay," +
				" case when c.payType = 2 then a.refundCash * -1 else 0 end as alipay," +
				" case when c.payType = 3 then a.refundCash * -1 else 0 end as unionpay," +
				" case when c.payType = 5 then a.refundCash * -1 else 0 end as cardpay," +
				" case when c.payType = 6 then a.refundCash * -1 else 0 end as pointpay," +
				" a.successTime as orderTime, a.successTime as payTime," + 
				" a.successTime as sendOutTime, a.successTime as receiveTime";
		String sqlExceptSelectRefund = " from `refund` as a" +
				" left join product_order as b on a.product_order_id = b.id" +
				" left join `order` as c on b.order_id = c.id" +
				" where a.status = 3";
		
		String selectBack = "select '退货' as tradeType, b.supplier_id, b.supplier_name, b.taxRate," + 
				" (b.unitCost*b.unitOrdered+b.totalActualDeliveryCharge)*-1 as totalUnitCost," + 
				" (b.unitCostNoTax*b.unitOrdered*-1) as totalUnitCostNoTax," +
				" b.invoiceType, a.refundCash * -1 as payable," +
				" case when c.payType = 1 then a.refundCash * -1 else 0 end as weixinpay," +
				" case when c.payType = 2 then a.refundCash * -1 else 0 end as alipay," +
				" case when c.payType = 3 then a.refundCash * -1 else 0 end as unionpay," +
				" case when c.payType = 5 then a.refundCash * -1 else 0 end as cardpay," +
				" case when c.payType = 6 then a.refundCash * -1 else 0 end as pointpay," +
				" a.successTime as orderTime, a.successTime as payTime," + 
				" a.successTime as sendOutTime, a.successTime as receiveTime";
		String sqlExceptSelectBack = " from `back` as a" +
				" left join product_order as b on a.product_order_id = b.id" +
				" left join `order` as c on b.order_id = c.id" +
				" where a.status = 4" +
				" and c.receiveTime != ''";
		
		String sql = selectOrder + sqlExceptSelectOrder +
				" union all " + 
				selectRefund + sqlExceptSelectRefund + 
				" union all " +
				selectBack + sqlExceptSelectBack;
		
		String select = "select tradeType, supplier_id, supplier_name, taxRate," + 
				" sum(totalUnitCost) as totalUnitCost," + 
				" sum(totalUnitCostNoTax) as totalUnitCostNoTax," + 
				" invoiceType, sum(payable) as payable, sum(weixinpay) as weixinpay," +
				" sum(alipay) alipay, sum(unionpay) as unionpay, sum(cardpay) as cardpay," +
				" orderTime, payTime, sendOutTime, receiveTime, sum(pointpay) as pointpay";
		String sqlExceptSelect = " from (" + sql + ") as t" +
				" where supplier_id != 0";
		
		if (supplierId != null) {
			sqlExceptSelect += " and supplier_id = " + supplierId;
		}
		if (invoiceType != null) {
			sqlExceptSelect += " and invoiceType = '" + invoiceType + "'";
		}
		if (startTime != null && !startTime.equals("")) {
			sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sqlExceptSelect += " and DATE_FORMAT(receiveTime,'%Y-%m-%d') <= '" + endTime + "'";
		}
		
		sqlExceptSelect += " and (tradeType = '订单' or tradeType = '退货')";
		sqlExceptSelect += " group by supplier_id, taxRate, invoiceType";
		
		String lastSql = select + sqlExceptSelect;
		lastSql = "select * from (" + lastSql + ") as t where payable != 0";
		
		return lastSql;
	}
		
}
