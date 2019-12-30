package com.eshop.finance;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.helper.DateHelper;
import com.eshop.helper.MathHelper;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 导出excel
 * @author TangYiFeng
 */
public class ExcelService {
	
    public static Map<String, String> suppliers() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("name", "供应商名称");
    	headers.put("type", "类型");
    	headers.put("phone1", "联系方式");
    	headers.put("contactPerson", "联系人");
    	headers.put("province", "所在省");
    	headers.put("city", "所在市");
    	headers.put("district", "所在区");
    	headers.put("street", "所在街道");
    	headers.put("zipcode", "邮编");
    	
    	return headers;
    }
    
    /**
     * 导出对账列表
     * @param sql
     */
    public static Map<String, String> exportOrderPayList() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("payType", "支付方式");
    	headers.put("totalPayable", "销售金额");
    	headers.put("totalCost", "销售成本");
    	headers.put("totalRefundCash", "退款金额");
    	headers.put("payRateSum", "手续费");
    	headers.put("totalTaxCost", "税额 ");
    	headers.put("balance", "划账金额");
    	
    	return headers;
    }
    
    /**
     * 导出销售明细汇总表
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public static Map<String, String> exportByOrderList() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("order_code", "订单号");
    	headers.put("tradeCode", "商户单号");
    	headers.put("tradeNo", "流水号");
    	headers.put("source", "平台类型");
    	headers.put("payType", "付款方式");
    	headers.put("tradeType", "类型");
    	headers.put("payTime", "支付日期");
    	headers.put("totalPayable", "销售额");
    	headers.put("deliveryPrice", "运费");
    	headers.put("totalActualProductPrice", "产品价格");
    	headers.put("totalProductCost", "产品成本");
    	headers.put("totalRefundCash", "退款金额");
    	headers.put("totalPayRateSum", "手续费");
    	headers.put("totalTaxCost", "税额");
    	//headers.put("balance", "划账金额");
    	
    	return headers;
    }
    
    /**
     * 导出对账列表
     * @param sql
     */
    public static Map<String, String> exportAuditRecord() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("tradeCode", "商户订单号");
    	headers.put("tradeNo", "流水号");
    	headers.put("tradeType", "交易类型");
    	headers.put("payable", "商户交易金额");
    	headers.put("thirdPayable", "平台交易金额");
    	headers.put("tradeTime", "交易时间");
    	headers.put("payType", "支付方式");
    	headers.put("differ", "对比结果");
    	headers.put("status", "数据状态");
    	
    	return headers;
    }
    
    /**
     * 按税率标识导出汇总表
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public static Map<String, String> exportInvoice() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("email", "邮箱");
    	headers.put("invoiceCode", "发票编号");
    	headers.put("orderCode", "订单号");
    	headers.put("invoiceHead", "发票抬头");
    	headers.put("totalPayable", "订单金额");
    	headers.put("money", "开票金额");
    	headers.put("amount", "开票数量");
    	
    	return headers;
    }
    
    /**
     * 按税率标识导出汇总表
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public static Map<String, String> exportByOrderTax() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("payDate", "日期");
    	headers.put("taxName", "税率标识");
    	headers.put("source", "交易平台类型");
    	headers.put("sumTotalPayable", "销售总额");
    	headers.put("totalRefundCash", "退款总额");
    	headers.put("totalTaxCost", "税额");
    	headers.put("balance", "划账金额");
    	
    	return headers;
    }
    
    /**
     * 按收款方式导出汇总表
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public static Map<String, String> exportByOrderSource() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("payTime", "支付日期");
    	headers.put("payType", "支付方式");
    	headers.put("source", "平台类型");
    	headers.put("tradeType", "消费类型");
    	headers.put("totalPayable", "销售总额");
    	headers.put("refundCash", "退款总额");
    	headers.put("payRateSum", "手续费");
    	headers.put("totalTaxCost", "税额");
    	headers.put("balance", "划账金额");
    	
    	return headers;
    }
    
    /**
     * 导出退货单
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public static Map<String, String> exportBack() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("orderCode", "订单号");
    	headers.put("tradeCode", "商户单号");
    	headers.put("source", "平台下单类型");
    	headers.put("payType", "支付方式");
    	headers.put("refundCash", "退货金额");
    	headers.put("status", "退款状态");
    	headers.put("refundAmount", "数量");
    	headers.put("productName", "商品名");
    	headers.put("taxRate", "税率");
    	headers.put("totalPayable", "订单总额");
    	headers.put("orderTime", "下单时间");
    	headers.put("payTime", "支付时间");
    	headers.put("operator", "审核人");
    	headers.put("created_at", "申请时间");
    	
    	return headers;
    }
    
    /**
     * 导出退款单
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public static Map<String, String> exportRefund() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("order_code", "订单号");
    	headers.put("tradeCode", "商户单号");
    	headers.put("source", "平台下单类型");
    	headers.put("payType", "支付方式");
    	headers.put("refundCash", "退货金额");
    	headers.put("status", "退款状态");
    	headers.put("refundAmount", "数量");
    	headers.put("productName", "商品名");
    	headers.put("taxRate", "税率");
    	headers.put("totalPayable", "订单总额");
    	headers.put("orderTime", "下单时间");
    	headers.put("payTime", "支付时间");
    	headers.put("operator", "审核人");
    	headers.put("created_at", "申请时间");
    	
    	return headers;
    }
    
    
    /**
     * 根据供应商导出订单
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public Record exportOrderBySupplier(String ids) {
    	Record result = new Record();
    	
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序列");
    	headers.put("supplier", "供应商");
    	headers.put("orderCode", "订单号");
    	headers.put("product_name", "产品");
    	headers.put("selectProterties", "规格");
    	headers.put("unitOrdered", "购买数量");
    	headers.put("totalPrice", "金额");
    	headers.put("receiveName", "收件人");
    	headers.put("preferredContactPhone", "联系方式");
    	headers.put("receiveDetailAddress", "收件详细地址");
    	headers.put("orderTime", "下单时间");
    	headers.put("invoiceHead", "发票抬头");
    	headers.put("status", "订单状态");
    	headers.put("pStatus", "状态");
    	headers.put("zipCode", "邮编");
    	result.set("headers", headers);
    	
    	String orderIdWhereIn = getOrderIdWhereIn(ids);
    	
    	String sql = "select c.supplier_id as id from `order` as a " +
    			"LEFT JOIN product_order as b on a.id = b.order_id " + 
    			"LEFT JOIN product as c on b.product_id = c.id " + 
    			"where a.id in " + orderIdWhereIn + " " +
    			"and c.supplier_id is not null " +
    			"group by c.supplier_id";
    	
    	List<Record> list = Db.find(sql);
    	
    	String supplierIdWhereIn = BaseDao.getWhereIn(list, "id");
    	List<Record> suppliers = Db.find("select * from supplier where id in " + supplierIdWhereIn);
    	
    	for (Record item : suppliers) {
    		int supplierId = item.getInt("id");
    		String sql1 = "select a.* from `order` as a " +
        			"LEFT JOIN product_order as b on a.id = b.order_id " + 
        			"LEFT JOIN product as c on b.product_id = c.id " + 
        			"where a.id in " + orderIdWhereIn + " " +
        			"and c.supplier_id = " + supplierId + " " +
        			"group by b.order_id";
    		
    		List<Record> orders = Db.find(sql1);
    		item.set("orders", orders);
    		
    		for (Record order : orders) {
    			int orderId = order.getInt("id");
    			String sql2 = "select a.* from `product_order` as a " +
    					"left join product as b on a.product_id = b.id " +
    					"where a.order_id = " + orderId + " " +
    					"and b.supplier_id = " + supplierId;
    			
    			List<Record> productOrders = Db.find(sql2);
    			order.set("productOrders", productOrders);
    		}
    	}
    	
    	result.set("data", suppliers);
    	return result;
    }
    
    public static List<Record> findExportOrderItems(Integer customerId, Integer shopId, 
    		String code, String tradeCode, String tradeNo, Integer status, String startTime, 
    		String endTime, String receiverName, String receiverPhone, Integer source, 
    		Integer payType, String logisticsName, String expressCode, Integer timeType, 
    		Map<String, String> orderByMap) {
    	
    	String select = "select a.*, '' as zipCode," + 
    			" case codeType when 1 then theSameOrderNum when 2 then order_code end as tradeCode," +
    			" b.product_id, b.supplier_name, b.order_id, b.product_name,b.totalActualDeliveryCharge," +
    			" b.selectProterties, b.unitOrdered, b.actualUnitPrice," + 
    			" (b.unitOrdered * b.actualUnitPrice) as totalActualUnitPrice, b.unitCost," + 
    			" (b.unitOrdered * b.unitCost) as totalUnitCost, b.totalPrice, b.status as pStatus";
    	String sqlExceptSelect = " from `order` as a" +
    			" left join product_order as b on a.id = b.order_id" + 
    			" left join shop as c on a.shop_id = c.id" +
    			" where a.id > 0";
    	
    	if (code != null) {
    		sqlExceptSelect += " and order_code like '%" + code + "%'";
    	}
    	
    	if (tradeCode != null) {
    		sqlExceptSelect += " and tradeCode like '%" + tradeCode + "%'";
    	}
    	
    	if (tradeNo != null) {
    		sqlExceptSelect += " and tradeNo like '%" + tradeNo + "%'";
    	}
    	
    	if (status != -1) {
    		if (status == 4) {  //已收货
    			sqlExceptSelect += " and a.status in (4, 5, 7)";
    		} else if (status == 5) {  //关闭订单
    			sqlExceptSelect += " and a.status = " + 6;
    		} else {
    			sqlExceptSelect += " and a.status = " + status;
    		}
    	}
    	
    	if (startTime != null) {
    		if (status == -1 || status == 1) {
    			sqlExceptSelect += " and orderTime >='" + startTime+"'";
    		} else if (status == 2) {
    			sqlExceptSelect += " and payTime >= '" + startTime+"'";
    		} else if (status == 3) {
    			sqlExceptSelect += " and sendOutTime >='" + startTime+"'";
    		} else if (status == 4) {
    			sqlExceptSelect += " and receiveTime >= '" + startTime+"'";
    		} else if (status == 5) {
    			sqlExceptSelect += " and cancelTime >= '" + startTime+"'";
    		}
    	}
    	
    	if (endTime != null) {
    		if (status == -1 || status == 1) {
    			sqlExceptSelect += " and orderTime <='" + endTime+"'";
    		} else if (status == 2) {
    			sqlExceptSelect += " and payTime <= '" + endTime+"'";
    		} else if (status == 3) {
    			sqlExceptSelect += " and sendOutTime <='" + endTime+"'";
    		} else if (status == 4) {
    			sqlExceptSelect += " and receiveTime <='" + endTime+"'";
    		} else if (status == 5) {
    			sqlExceptSelect += " and cancelTime <= '" + endTime+"'";
    		} 
    	}
    	
    	//如果是待发货则过滤掉申请退货退款的订单
    	if (status == 2) {
    		sqlExceptSelect += " and b.status = 1";
    	}
    	
    	//3已发货,4已收货，如果是已发货或已收货则过滤掉申请退货退款的订单
    	if (status == 3 || status == 4) {
    		sqlExceptSelect += " and b.status in (1, 7)";
    	}
    	
    	sqlExceptSelect += " and order_id is not null order by b.supplier_id desc, order_code desc";
    	List<Record> list = Db.find(select + sqlExceptSelect);
    	
    	return list;
    }
    
    /**
     * 根据供应商导出订单
     * @param ids [1,2,...] 订单id
     * @return {headers:{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址}, data:[{orderCode:订单号,receiveName:收件人姓名,receivePhone:收件人电话,receiveProvince:收件省,receiveCity:收件市,receiveDistrict:收件区,receiveDetailAddress:收件详细地址,name:寄件人姓名,phone:寄件人电话,province:寄件省,city:寄件市,district:寄件区,detailAddress:寄件详细地址},...]}
     */
    public static Map<String, String> exportOrders() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序列");
    	headers.put("supplierName", "供应商");
    	headers.put("order_code", "订单号");
    	headers.put("product_name", "产品");
    	headers.put("unitOrdered", "购买数量");
    	headers.put("pricingUnit", "单位");
    	headers.put("receiveName", "收件人");
    	headers.put("preferredContactPhone", "联系方式");
    	headers.put("receiveProvince", "省");
    	headers.put("receiveCity", "市");
    	headers.put("receiveDistrict", "区");
    	headers.put("receiveDetailAddress", "收件详细地址");
    	headers.put("created_at", "下单时间");
    	headers.put("pStatus", "状态");
    	return headers;
    }
    
    /**
     * 根据供应商导出订单
     */
    public static Map<String, String> exportRecharge() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序列");
    	headers.put("tradeNo", "商户单号");
    	headers.put("customerName", "用户名称");
    	headers.put("customerPhone", "手机号码");
    	headers.put("event", "充值类型");
    	headers.put("money", "金额");
    	headers.put("created_at", "充值时间");
    	
    	return headers;
    }
    
    public List<Record> getOrderSourceData(List<Record> data, String startTime, String endTime, int state) {
		//计算退款金额、税额、账号余额，账号余额=交易金额-退款金额-手续费-税额
    	for (Record item : data) {
    		double sumTotalPayable = item.getBigDecimal("sumTotalPayable").doubleValue();  //总销售额
    		double totalRefundCash = 0.0;  //退款金额
    		double totalTaxCost = 0.0;     //税额
    		double totalPayRateSum = item.getBigDecimal("totalPayRateSum").doubleValue();  //手续费
    		double balance = 0.0;          //账号余额
    		
    		if (item.getStr("tradeType").equals("充值")) {
    			balance = sumTotalPayable - totalRefundCash - totalPayRateSum;
    			item.set("balance", MathHelper.cutDecimal(balance));
    			item.set("totalRefundCash", 0.0);
    			item.set("totalTaxCost", 0.0);
    			continue;
    		}
    		
    		//计算税额
    		String sql2 = "select b.taxRate, b.totalPrice from `order` as a" +
    				" left join product_order as b on a.id = b.order_id" +
    				" where a.id > 0";
    		
    		if (state == 0) {	//销售汇总
    			sql2 += " and DATE_FORMAT(a.created_at,'%Y-%m-%d') = '"  + item.getStr("createdAt") + "' and source = " + item.get("source") + " and payType = " + item.get("payType");
    		} else if (state == 1) {	//支付方式汇总
    			sql2 += " and DATE_FORMAT(a.created_at,'%Y-%m-%d') >= '" + startTime + "' and DATE_FORMAT(a.created_at,'%Y-%m-%d') <= '" + endTime + "" + "' and payType = " + item.get("payType");
    		} else if (state == 2) {	//平台类型汇总
    			sql2 += " and DATE_FORMAT(a.created_at,'%Y-%m-%d') >= '" + startTime + "' and DATE_FORMAT(a.created_at,'%Y-%m-%d') <= '" + endTime + "" + "' and source = " + item.get("source");
    		}
    		
    		List<Record> productOrderList = Db.find(sql2);
    		for (Record record : productOrderList) {
        		if (item.get("taxRate") != null) {
        			totalTaxCost += record.getBigDecimal("taxRate").doubleValue() * record.getBigDecimal("totalPrice").doubleValue() * 0.01;
        		}
    		}
    		
    		//计算退款金额
    		String sql3 = "select c.refundCash, c.remitTime, a.source, a.payType from `order` as a" +
    				" left join product_order as b on a.id = b.order_id" +
    				" left join back as c on b.id = c.product_order_id" +
    				" where c.status = 4" +
    				" union " +
    				" select c.refundCash, c.remitTime, a.source, a.payType from `order` as a" +
    				" left join product_order as b on a.id = b.order_id" +
    				" left join refund as c on b.id = c.product_order_id" +
    				" where c.status = 3";
    		String sql = "select * from (" + sql3 + ") as t1";
    		
    		if (state == 0) {	//销售汇总
    			sql += " where DATE_FORMAT(remitTime,'%Y-%m-%d') = '"  + item.getStr("createdAt") + "' and source = " + item.get("source") + " and payType = " + item.get("payType");
    		} else if (state == 1) {	//支付方式汇总
    			sql += " where DATE_FORMAT(remitTime,'%Y-%m-%d') >= '" + startTime + "' and DATE_FORMAT(remitTime,'%Y-%m-%d') <= '" + endTime + "' and payType = " + item.get("payType");
    		} else if (state == 2) {	//平台类型汇总
    			sql += " where DATE_FORMAT(remitTime,'%Y-%m-%d') >= '" + startTime + "' and DATE_FORMAT(remitTime,'%Y-%m-%d') <= '" + endTime + "' and source = " + item.get("source");
    		}
    		
    		List<Record> refunds = Db.find(sql);
    		for (Record refund : refunds) {
    			totalRefundCash += refund.getBigDecimal("refundCash").doubleValue();
    		}
    		
    		balance = sumTotalPayable - totalPayRateSum - totalRefundCash;
    		item.set("totalRefundCash", MathHelper.cutDecimal(totalRefundCash));
    		item.set("totalTaxCost", MathHelper.cutDecimal(totalTaxCost));
    		item.set("balance", MathHelper.cutDecimal(balance));
    	}
    	
    	return data;
	}
    
    public List<Record> getOrderSummaryData(List<Record> data, String startTime, String endTime, int state, int psource, int ppayType) {
    	//退款金额数据
    	String refundSql = "select c.refundCash, DATE_FORMAT(c.remitTime,'%Y-%m-%d') as remitTime, c.created_at, c.successTime, a.source, a.payType from `order` as a" +
				" left join product_order as b on a.id = b.order_id" +
				" left join back as c on b.id = c.product_order_id" +
				" where c.status = 4" +
				" union " +
				" select c.refundCash, DATE_FORMAT(c.remitTime,'%Y-%m-%d') as remitTime,  c.created_at, c.successTime, a.source, a.payType from `order` as a" +
				" left join product_order as b on a.id = b.order_id" +
				" left join refund as c on b.id = c.product_order_id" +
				" where c.status = 3";
		String sql = "select sum(refundCash) as refundCash, remitTime, created_at, successTime, source, payType from (" + refundSql + ") as t1" +
				" where DATE_FORMAT(remitTime, '%Y-%m-%d') >= '" + startTime + "'" +
				" and DATE_FORMAT(remitTime, '%Y-%m-%d') <= '" + endTime + "'";
		
		if (psource != 0) {
			sql += " and source = " + psource;
		}
			
		if (ppayType != 0) {
			sql += " and payType = " + ppayType;
		}
		
		sql += " group by DATE_FORMAT(remitTime, '%Y-%m-%d'), source, payType";
		List<Record> refunds = Db.find(sql);
		
		//税额数据
		String taxSql = "select DATE_FORMAT(a.payTime,'%Y-%m-%d') createdAt, a.source, a.payType, b.taxRate, b.totalPrice from `order` as a" +
				" left join product_order as b on a.id = b.order_id" +
				" where a.id > 0";
		List<Record> taxs = Db.find(taxSql);
		
		//计算退款金额、税额、账号余额，账号余额=交易金额-退款金额-手续费-税额
    	for (Record item : data) {
    		double sumTotalPayable = item.getBigDecimal("sumTotalPayable").doubleValue();  //总销售额
    		double totalRefundCash = 0.0;  //退款金额
    		double totalTaxCost = 0.0;
    		double totalPayRateSum = item.getBigDecimal("totalPayRateSum").doubleValue();  //手续费
    		double balance = 0.0;          //账号余额
    		
    		if (item.getStr("tradeType").equals("充值")) {
    			balance = sumTotalPayable - totalRefundCash - totalPayRateSum;
    			item.set("balance", MathHelper.cutDecimal(balance));
    			item.set("totalRefundCash", 0.0);
    			item.set("totalTaxCost", 0.0);
    			continue;
    		}
    		
    		String createdAt = item.getStr("createdAt");
    		int source = item.getNumber("source").intValue();
    		int payType = item.getNumber("payType").intValue();
    		
    		//计算退款金额
    		for (Record refund : refunds) {
    			String remitTime = refund.getStr("remitTime");
    			int rsource = refund.getInt("source");
    			int rpayType = refund.getInt("payType");
    			if (createdAt.equals(remitTime) && source == rsource && payType == rpayType) {
    				totalRefundCash += refund.getBigDecimal("refundCash").doubleValue();
    			}
    		}
    		item.set("totalRefundCash", MathHelper.cutDecimal(totalRefundCash));
    		
    		//计算税额
    		for (Record tax : taxs) {
    			String tcreatedAt = tax.getStr("createdAt");
    			int tsource = tax.getInt("source");
    			int tpayType = tax.getInt("payType");
    			if (createdAt.equals(tcreatedAt) && source == tsource && payType == tpayType && tax.getBigDecimal("taxRate") != null) {
        			totalTaxCost += tax.getBigDecimal("taxRate").doubleValue() * tax.getBigDecimal("totalPrice").doubleValue() * 0.01;
    			}
    		}
    		item.set("totalTaxCost", MathHelper.cutDecimal(totalTaxCost));
    		
    		balance = sumTotalPayable - totalRefundCash - totalPayRateSum;
    		item.set("balance", MathHelper.cutDecimal(balance));
    	}
    	
		//找出遗漏的退款记录
    	for (Record refund : refunds) {
    		refund.set("isUsed", false);
    		String remitTime = refund.getStr("remitTime");
			int rsource = refund.getInt("source");
			int rpayType = refund.getInt("payType");
			for (Record item : data) {
				String createdAt = item.getStr("createdAt");
				int source = item.getNumber("source").intValue();
	    		int payType = item.getNumber("payType").intValue();
	    		if (createdAt.equals(remitTime) && source == rsource && payType == rpayType) {
	    			refund.set("isUsed", true);
	    			break;
	    		}
			}
			
			if (!refund.getBoolean("isUsed")) {
				double refundCash = refund.getBigDecimal("refundCash").doubleValue();
				Record tmp = new Record();
				tmp.set("id", 0);
				tmp.set("startTime", startTime);
				tmp.set("endTime", endTime);
				tmp.set("created_at", DateHelper.formatDate(refund.getDate("created_at"), "yyyy-MM-dd-HH-mm:ss:ms", 0));
				tmp.set("createdAt", refund.getStr("remitTime"));
				tmp.set("payType", refund.getInt("payType"));
				tmp.set("source", refund.getInt("source"));
				tmp.set("sumTotalPayable", new BigDecimal(0));
				tmp.set("totalPayRateSum", new BigDecimal(0));
				tmp.set("totalRefundCash", MathHelper.cutDecimal(refundCash));
				tmp.set("tradeType", "退款");
				tmp.set("totalTaxCost", 0.0);
				tmp.set("balance", MathHelper.cutDecimal(refundCash * -1));
				data.add(tmp);
			}
    	}
    	
    	return data;
	}
    
    public List<Record> getOrderListData(List<Record> data) {
    	String sql = "select * from product_order";
    	List<Record> list = Db.find(sql);
    	
		//计算产品总价格，产品总成本、税额、划账金额=销售额-退款金额-手续费
    	for (Record item : data) {
    		double sumTotalPayable = item.getBigDecimal("sumTotalPayable").doubleValue();
    		double totalRefundCash = item.getBigDecimal("totalRefundCash").doubleValue();
    		double totalPayRateSum = item.getBigDecimal("totalPayRateSum").doubleValue();
    		double balance = sumTotalPayable - totalRefundCash - totalPayRateSum;
    		double totalTaxCost = 0;     //税额
    		double totalProductCost = 0; //产品总成本
    		double totalActualProductPrice = 0; //产品总价格
    		
    		//获取商户单号
    		String tradeNum = "";  //商户单号
    		int codeType = item.getNumber("codeType").intValue();
    		if (codeType == 1) {
				tradeNum = item.getNumber("theSameOrderNum").toString();
			} else {
				tradeNum = item.getStr("order_code");
			}
    		item.set("tradeNum", tradeNum);
    		
    		int flag = 1, flag2 = 1;
    		List<Record> productOrders = new ArrayList<Record>();
    		if (item.getStr("tradeType").equals("订单")) {
				productOrders = BaseDao.findItems(list, "order_id", item.getNumber("order_id").intValue());
			} else if (item.getStr("tradeType").equals("退款") || item.getStr("tradeType").equals("退货")) {
    			productOrders = BaseDao.findItems(list, "id", item.getNumber("product_order_id").intValue());
    			flag = -1;
    			flag2 = 0;
    		}
    		
    		//计算订单产品总成本和产品总价格
    		for (Record po : productOrders) {
    			totalTaxCost += po.getBigDecimal("taxRate").doubleValue() * po.getBigDecimal("actualUnitPrice").doubleValue() * po.getInt("unitOrdered").doubleValue() * 0.01 * flag2;
    			totalProductCost += po.getBigDecimal("unitCost").doubleValue() * po.getInt("unitOrdered") * flag;
    			totalActualProductPrice += po.getBigDecimal("actualUnitPrice").doubleValue() * po.getInt("unitOrdered") * flag2;
    		}
    		
    		item.set("totalTaxCost", MathHelper.cutDecimal(totalTaxCost));
    		item.set("totalProductCost", MathHelper.cutDecimal(totalProductCost));
    		item.set("totalActualProductPrice", MathHelper.cutDecimal(totalActualProductPrice));
    		item.set("balance", MathHelper.cutDecimal(balance));
    	}
    	
    	return data;
	}
    
    private String getOrderIdWhereIn(String ids) {
    	int size = JSON.parseArray(ids).size();
    	String orderIdWhereIn = "";
    	
    	if (size == 0) {
    		String select = "select a.* from `order` as a left join product_order as b on a.id = b.order_id where b.status = 1 and a.status = 2 group by a.id order by a.created_at desc";
    		List<Record> list = Db.find(select);
    		
    		int length = list.size();
    		for (int i = 0; i < length; i++) {
    			int id = list.get(i).getInt("id");
    			if (i == length - 1) {
    				orderIdWhereIn += id;
    				break;
    			}
    			orderIdWhereIn += id + ",";
    		}
    		
    		if (orderIdWhereIn.equals("")) {
    			orderIdWhereIn = "(null)";
    		} else {
    			orderIdWhereIn = "(" + orderIdWhereIn + ")";
    		}
    	} else {
    		orderIdWhereIn = BaseDao.getWhereIn(ids);
    	}
    	
    	return orderIdWhereIn;
    }
    
    /**
     * 导出供应商对账汇总表
     */
    public static Map<String, String> supplierOrderSummary() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("sendOutTime", "发货日期");
    	headers.put("supplier_name", "供应商名称");
    	headers.put("tradeType", "交易类型");
    	headers.put("unitOrdered", "发货总数量");
    	headers.put("totalProductCost", "产品总成本");
    	headers.put("totalPrice", "产品总金额");
    	headers.put("backAmount", "退货总数量");
    	headers.put("totalRefund", "退货总金额");
    	headers.put("totalActualDeliveryCharge", "快递总费用");
    	return headers;
    }
    
    /**
     * 导出供应商对账明细表
     */
    public static Map<String, String> supplierOrderList() {
    	Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序号");
    	headers.put("supplier_name", "供应商");
    	headers.put("payTime", "支付日期");
    	headers.put("sendOutTime", "发货日期");
    	headers.put("order_code", "订单号");
    	headers.put("tradeType", "交易类型");
    	headers.put("expressCode", "快递单号");
    	headers.put("logisticsName", "快递名称");
    	headers.put("selectProterties", "产品名称及规格");
    	headers.put("pricingUnit", "单位");
    	headers.put("unitOrdered", "发货数量");
    	headers.put("totalProductCost", "产品成本小计");
    	headers.put("totalActualDeliveryCharge", "快递费用");
    	headers.put("totalCost", "总成本");
    	headers.put("totalPrice", "产品销售金额小计");
    	headers.put("totalRefund", "退货总金额");
    	headers.put("totalSalable", "销售总额");
    	headers.put("payType", "支付方式");
    	headers.put("couponDiscount", "优惠券优惠金额");
    	headers.put("note", "备注");
    	
    	return headers;
    }
    
}