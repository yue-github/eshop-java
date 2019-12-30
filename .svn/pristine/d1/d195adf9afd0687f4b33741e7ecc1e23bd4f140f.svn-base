package com.eshop.finance;

import java.util.List;
import java.util.Map;

import com.eshop.helper.MathHelper;
import com.eshop.model.Back;
import com.eshop.model.Refund;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ExportDataService {
	
	/**
	 * 批量查询订单明细
	 * @param customerId
	 * @param shopId
	 * @param orderCode
	 * @param tradeCode
	 * @param tradeNo
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @param receiverName
	 * @param receiverPhone
	 * @param source
	 * @param payType
	 * @param logisticsName
	 * @param expressCode
	 * @param orderByMap
	 * @return
	 */
	public static List<Record> findExportProductOrderItems(Integer customerId, Integer shopId, 
			String orderCode, String tradeCode, String tradeNo, Integer status, String startTime, 
			String endTime, String receiverName, String receiverPhone, Integer source, Integer payType, 
			String logisticsName, String expressCode, Map<String, String> orderByMap) {
		
		String sql = findExportProductOrderItemsSql(customerId, shopId, orderCode, tradeCode, tradeNo, 
				status, startTime, endTime, receiverName, receiverPhone, source, payType, logisticsName, 
				expressCode, orderByMap);
		
		return Db.find(sql);
	}
	
	/**
	 * 组装sql语句
	 * @param status 订单状态（-1为全部,1待付款,2待发货,3已发货,4已收货,5取消订单） 必填
	 * @param order_code 订单号 选填
	 * @param startTime 开始时间 选填
	 * @param endTime 结束时间 选填
	 * @param tradeCode 商户单号 选填
	 * @param tradeNo 交易流水号 选填
	 * @param payType 支付方式
	 * @param source 平台
	 * @param preferredContactPhone 收货人姓名
	 * @param receiveName 收获人姓名
	 * @param expressCode 物流号
	 * @param logisticsName 快递公司名称
	 * @return
	 */
	public static String findExportProductOrderItemsSql(Integer customerId, Integer shopId, 
			String orderCode, String tradeCode, String tradeNo, Integer status, String startTime, 
			String endTime, String receiverName, String receiverPhone, Integer source, Integer payType, 
			String logisticsName, String expressCode, Map<String, String> orderByMap) {
		
		String sql = "select a.*, b.product_name, b.selectProterties, b.unitOrdered, b.actualUnitPrice," +
				" b.totalActualProductPrice, b.unitCost, b.totalProductCost, b.status as pStatus" + 
				" from `order` as a" +
				" left join product_order as b on a.id = b.order_id";
		
		if (customerId != null) {
			sql += " and a.customer_id = " + customerId;
		}
		if (shopId != null) {
			sql += " and a.shop_id = " + shopId;
		}
		if (orderCode != null && !orderCode.equals("")) {
			sql += " and a.order_code like '%" + orderCode + "%'";
		}
		if (tradeCode != null && !tradeCode.equals("")) {
			sql += " and a.theSameOrderNum like '%" + tradeCode + "%'";
		}
    	if (tradeNo != null && !tradeNo.equals("")) {
			sql += " and a.tradeNo like '%" + tradeNo + "%'";
		}
    	if (status != null) {
			sql += " and a.status = " + status;
		}
    	if (status == null) {
    		if (startTime != null && !startTime.equals("")) {
				sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startTime + "'";
			}
    		if (endTime != null && !endTime.equals("")) {
				sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endTime + "'";
			}
    	} else {
    		switch (status) {
    		case BaseDao.UNPAY:
    			if (startTime != null && !startTime.equals("")) {
					sql += " and DATE_FORMAT(a.orderTime, '%Y-%m-%d') >= '" + startTime + "'";
				}
        		if (endTime != null && !endTime.equals("")) {
					sql += " and DATE_FORMAT(a.orderTime, '%Y-%m-%d') <= '" + endTime + "'";
				}
        		break;
    		case BaseDao.PAYED:
    			if (startTime != null && !startTime.equals("")) {
					sql += " and DATE_FORMAT(a.payTime, '%Y-%m-%d') >= '" + startTime + "'";
				}
        		if (endTime != null && !endTime.equals("")) {
					sql += " and DATE_FORMAT(a.payTime, '%Y-%m-%d') <= '" + endTime + "'";
				}
        		break;
    		case BaseDao.DISPATCHED:
    			if (startTime != null && !startTime.equals("")) {
					sql += " and DATE_FORMAT(a.sendOutTime, '%Y-%m-%d') >= '" + startTime + "'";
				}
        		if (endTime != null && !endTime.equals("")) {
					sql += " and DATE_FORMAT(a.sendOutTime, '%Y-%m-%d') <= '" + endTime + "'";
				}
        		break;
    		case BaseDao.RECEIVED:
    			if (startTime != null && !startTime.equals("")) {
					sql += " and DATE_FORMAT(a.receiveTime, '%Y-%m-%d') >= '" + startTime + "'";
				}
        		if (endTime != null && !endTime.equals("")) {
					sql += " and DATE_FORMAT(a.receiveTime, '%Y-%m-%d') <= '" + endTime + "'";
				}
        		break;
    		case BaseDao.CANCELED:
    			if (startTime != null && !startTime.equals("")) {
					sql += " and DATE_FORMAT(a.cancelTime, '%Y-%m-%d') >= '" + startTime + "'";
				}
        		if (endTime != null && !endTime.equals("")) {
					sql += " and DATE_FORMAT(a.cancelTime, '%Y-%m-%d') <= '" + endTime + "'";
				}
        		break;
    		}
    	}
    	if (receiverName != null && !receiverName.equals("")) {
			sql += " and a.receiveName like '%" + receiverName + "%'";
		}
    	if (receiverPhone != null && !receiverPhone.equals("")) {
			sql += " and a.preferredContactPhone like '%" + receiverPhone + "%'";
		}
    	if (source != null) {
			sql += " and a.source = " + source;
		}
    	if (payType != null) {
			sql += " and a.payType = " + payType;
		}
    	if (logisticsName != null && !logisticsName.equals("")) {
			sql += " and a.logisticsName like '%" + logisticsName + "%'";
		}
    	if (expressCode != null && !expressCode.equals("")) {
			sql += " and a.expressCode like '%" + expressCode + "%'";
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
	}
	
	public static Record calculateExportOrderTaxItems(List<Record> list) {
		double allPayable = 0;		//总销售额
    	double allRefundCash = 0;	//总退款金额
    	double allTaxCost = 0;		//总税额
    	double allBalance = 0;		//总划账金额
    	
    	for (Record item : list) {
    		allPayable += item.getDouble("sumTotalPayable");
    		allRefundCash += item.getDouble("totalRefundCash");
    		allTaxCost += item.getDouble("totalTaxCost");
    		allBalance += item.getDouble("balance");
    	}
    	
    	Record statistics = new Record();
    	statistics.set("totalPayable", MathHelper.cutDecimal(allPayable));
    	statistics.set("tatalRefundCash", MathHelper.cutDecimal(allRefundCash));
    	statistics.set("totalTaxCost", MathHelper.cutDecimal(allTaxCost));
    	statistics.set("totalBalance", MathHelper.cutDecimal(allBalance));
    	return statistics;
	}
	
	/**
	 * 批量查询产品税率
	 * @param payType
	 * @param source
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findExportOrderTaxItems(Integer payType, Integer source, String startTime, 
			String endTime) {
		
		String sql = findExportOrderTaxItemsSql(payType, source, startTime, endTime);
		List<Record> list = Db.find(sql);
		
    	//计算退款金额和账号余额，账号余额=交易金额-退款金额
    	for (Record item : list) {
    		double sumTotalPayable = item.getBigDecimal("sumTotalPayable").doubleValue();   //销售额
    		double totalTaxCost = item.getBigDecimal("totalTaxCost").doubleValue();     //税额
    		double totalRefundCash = 0;  //退款金额
    		
    		String paytime = item.getStr("payDate");
    		int taxId = item.getInt("taxId");
    		int source1 = item.getInt("source");
    		
    		String select = "select a.*, b.taxId, b.id as productOrderId" +
    				" from `order` as a left join product_order as b on a.id = b.order_id" +
    				" where a.status not in(1, 6)" + 
    				" and taxId = " + taxId + 
    				" and source = " + source1 +
    				" and DATE_FORMAT(payTime,'%Y-%m-%d') = '" + paytime + "'";
    		
    		List<Record> prodOrders = Db.find(select);
    	
    		for (Record prodOrder : prodOrders) {
    			int productOrderId = prodOrder.getInt("productOrderId");
    			
    			List<Refund> refunds = Refund.dao.find("select * from refund where product_order_id = ?", productOrderId);
        		for (Refund refund : refunds) {
        			int status = refund.getStatus();
        			if (status == 3) {
        				totalRefundCash += refund.getRefundCash().doubleValue();
        			}
        		}
        		
        		List<Back> backs = Back.dao.find("select * from back where product_order_id = ?", productOrderId);
        		for (Back back : backs) {
        			int status = back.getStatus();
        			if (status == 4) {
        				totalRefundCash += back.getRefundCash().doubleValue();
        			}
        		}
    		}
    		
    		double balance = sumTotalPayable - totalRefundCash - totalTaxCost;
    		
    		item.set("sumTotalPayable", MathHelper.cutDecimal(sumTotalPayable));
    		item.set("totalTaxCost", MathHelper.cutDecimal(totalTaxCost));
    		item.set("totalRefundCash", MathHelper.cutDecimal(totalRefundCash));
    		item.set("balance", MathHelper.cutDecimal(balance));
    	}
    	
    	return list;
	}
	
	/**
	 * 组装sql语句
	 * @param payType
	 * @param source
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String findExportOrderTaxItemsSql(Integer payType, Integer source, String startTime, 
			String endTime) {
		
		String sql = "select a.*, DATE_FORMAT(a.payTime,'%Y-%m-%d') as payDate, b.taxName," +
				" b.taxRate, sum(b.totalPrice) as sumTotalPayable, sum(taxRateSum) as totalTaxCost" +
				" sum(b.totalPrice*b.taxRate*0.01) as totalTaxCost" +
				" from `order` as a left join product_order as b on a.id = b.order_id" +
				" where a.status not in(1, 6)";
		
		if (startTime != null && !startTime.equals("")) {
			sql += " and a.payTime >= " + "'" + startTime + "'";
		}
		
		if (endTime != null && !endTime.equals("")) {
			sql += " and a.payTime <= " + "'" + endTime + "'";
		}
		
		sql += "GROUP BY DATE_FORMAT(a.payTime,'%Y-%m-%d'), b.taxName, a.source";
		
		return sql;
	}
	
}
