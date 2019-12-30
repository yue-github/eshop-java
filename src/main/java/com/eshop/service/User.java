package com.eshop.service;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eshop.content.ResourceService;
import com.eshop.model.Back;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.model.Refund;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 
 */
public class User {
	
	protected static EventRole eventRole = EventRole.instance();
	
	/**
	 * 合并订单明细
	 * @param orders
	 * @return
	 */
	public static List<Record> getOrderDetails(List<Record> orders) {
		List<Record> list = new ArrayList<Record>();
		
		for (Record order : orders) {
			List<Record> details = order.get("details");
			list.addAll(details);
		}
		
		return list;
	}

    /**
     * 批量查询订单
     * @param offset
     * @param count
     * @param customerId
     * @param shopId
     * @param code
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
     * @return [{id:订单id, order_code:订单号, created_at:下单时间, shopName:店铺名称, shopLogo:店铺logo, details: [{id:订单明细id,product_id:产品id,product_name:产品名称,mainPic:主图,unitOrdered:数量,actualUnitPrice:价格,totalActualProductPrice:总价,selectProterties:属性}, ...]
     */
    public static List<Record> findOrderItems(int offset, int count, Integer customerId, Integer shopId, 
    		String code, String tradeCode, String tradeNo, Integer status, String startTime, 
    		String endTime, String receiverName, String receiverPhone, Integer source, 
    		Integer payType, String logisticsName, String expressCode, Integer timeType, 
    		Map<String, String> orderByMap) {
    	
        String sql = findOrderItemsSql(customerId, shopId, code, tradeCode, tradeNo, status, startTime, 
        		endTime, receiverName, receiverPhone, source, payType, logisticsName, expressCode, timeType,
        		orderByMap);
        
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        List<Record> list = Db.find(sql);
        
        return list;
    }
    
    /**
     * 批量查询订单
     * @param customerId
     * @param shopId
     * @param code
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
    public static List<Record> findOrderItems(Integer customerId, Integer shopId, 
    		String code, String tradeCode, String tradeNo, Integer status, String startTime, 
    		String endTime, String receiverName, String receiverPhone, Integer source, 
    		Integer payType, String logisticsName, String expressCode, Integer timeType, Map<String, String> orderByMap) {
    	
    	String sql = findOrderItemsSql(customerId, shopId, code, tradeCode, tradeNo, status, startTime, 
    			endTime, receiverName, receiverPhone, source, payType, logisticsName, expressCode,
    			timeType, null);
    	
    	List<Record> list = Db.find(sql);
    	
    	return list;
    }
    
    public static List<Record> findOrderItems(String tradeCode, Map<String, String> orderByMap) {
    	return findOrderItems(null, null, null, tradeCode, null, null, null, null, null, null, 
    			null, null, null, null, null, orderByMap);
    }
    
    /**
     * 计算订单统计金额
     * @param list
     * @return
     */
    public static Record calculateOrderItems(List<Record> list) {
    	BigDecimal totalCost = new BigDecimal(0);
    	BigDecimal totalPayable = new BigDecimal(0);
    	
    	for (Record item : list) {
    		totalCost = totalCost.add(item.getBigDecimal("totalProductCost"));
    		totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
    	}
    	
    	Record result = new Record();
    	result.set("totalProductCost", totalCost.doubleValue());
    	result.set("totalPayable", totalPayable.doubleValue());
    	return result;
    }

    /**
     * 批量查询订单的总数量
     * @param customerId
     * @param shopId
     * @param code
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
     * @return
     */
    public static int countOrderItems(Integer customerId, Integer shopId, String code, String tradeCode, 
    		String tradeNo, Integer status, String startTime, String endTime, String receiverName, 
    		String receiverPhone, Integer source, Integer payType, String logisticsName, 
    		String expressCode) {
    	
        String sql = findOrderItemsSql(customerId, shopId, code, tradeCode, tradeNo, status, 
        		startTime, endTime, receiverName, receiverPhone, source, payType, logisticsName, 
        		expressCode,null, null);
        
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param shopId
     * @param code
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
     * @return
     */
    public static String findOrderItemsSql(Integer customerId, Integer shopId, String code, String tradeCode, 
    		String tradeNo, Integer status, String startTime, String endTime, String receiverName, 
    		String receiverPhone, Integer source, Integer payType, String logisticsName, 
    		String expressCode, Integer timeType, Map<String, String> orderByMap) {
    	
    	String sql = "select o.id, o.order_code, o.shopName, o.theSameOrderNum, o.tradeNo," + 
    			" o.totalPayable, o.totalProductCost, o.orderTime, o.payTime, o.sendOutTime," + 
    			" o.receiveTime, o.cancelTime, o.source, o.`status`, o.preferredContactPhone," + 
    			" o.created_at, o.deliveryPrice, o.payType" +
    			" from `order` as o" +
    			" left join product_order as po on o.id = po.order_id" +
    			" where o.id != 0";
    	
    	if (customerId != null) {
			sql += " and o.customer_id = " + customerId;
		}
    	if (shopId != null) {
			sql += " and o.shop_id = " + shopId;
		}
    	if (code != null && !code.equals("")) {
			sql += " and o.order_code like concat('%'," + '"' + code + '"' + ",'%')";
		}
    	if (tradeCode != null && !tradeCode.equals("")) {
			sql += " and o.theSameOrderNum like '%" + tradeCode + "%'";
		}
    	if (tradeNo != null && !tradeNo.equals("")) {
			sql += " and o.tradeNo like '%" + tradeNo + "%'";
		}
    	if (status != null && status != -1) {
    		if (status == 4) { // 已收货
    			sql += " and o.status in (4, 5, 7)";
    		} else if (status == 8) { // 关闭订单
    			sql += " and po.status not in (1, 7) ";
    		} else {
    			sql += " and o.status = " + status;
    		}
		}
    	if (startTime != null && !startTime.equals("")) {
    		if (status == 1) {
    			sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') >= '" + startTime + "'";
    		} else if (status == 2) {
    			sql += " and DATE_FORMAT(o.payTime, '%Y-%m-%d') >= '" + startTime + "'";
    		} else if (status == 3) {
    			sql += " and DATE_FORMAT(o.sendOutTime, '%Y-%m-%d') >= '" + startTime + "'";
    		} else if (status == 4) {
    			sql += " and DATE_FORMAT(o.receiveTime, '%Y-%m-%d') >= '" + startTime + "'";
    		} else if (status == 5) {
    			sql += " and DATE_FORMAT(o.cancelTime, '%Y-%m-%d') >= '" + startTime + "'";
    		} else {
    			sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') >= '" + startTime + "'";
    		}
    	}
    	if (endTime != null && !endTime.equals("")) {
    		if (status == 1) {
    			sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') <= '" + endTime + "'";
    		} else if (status == 2) {
    			sql += " and DATE_FORMAT(o.payTime, '%Y-%m-%d') <= '" + endTime + "'";
    		} else if (status == 3) {
    			sql += " and DATE_FORMAT(o.sendOutTime, '%Y-%m-%d') <= '" + endTime + "'";
    		} else if (status == 4) {
    			sql += " and DATE_FORMAT(o.receiveTime, '%Y-%m-%d') <= '" + endTime + "'";
    		} else if (status == 5) {
    			sql += " and DATE_FORMAT(o.cancelTime, '%Y-%m-%d') <= '" + endTime + "'";
    		} else {
    			sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') <= '" + endTime + "'";
    		}
    	}
    	if (receiverName != null && !receiverName.equals("")) {
			sql += " and o.receiveName like '%" + receiverName + "%'";
		}
    	if (receiverPhone != null && !receiverPhone.equals("")) {
			sql += " and o.preferredContactPhone like '%" + receiverPhone + "%'";
		}
    	if (source != null) {
			sql += " and o.source = " + source;
		}
    	if (payType != null) {
			sql += " and o.payType = " + payType;
		}
    	if (logisticsName != null && !logisticsName.equals("")) {
			sql += " and o.logisticsName like '%" + logisticsName + "%'";
		}
    	if (expressCode != null && !expressCode.equals("")) {
			sql += " and o.expressCode like '%" + expressCode + "%'";
		}
    	
    	// 如果是待发货则过滤掉申请退货退款的订单
    	if (status == 2) {
    		sql += " and po.status = 1";
    	}

    	// 3已发货,4已收货，如果是已发货或已收货则过滤掉申请退货退款的订单
    	if (status == 3 || status == 4) {
    		sql += " and po.status in (1, 7)";
    	}
    	
    	sql += " group by o.id";
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	System.out.println(sql);
    	
    	return sql;
    }

    /**
     * 查看订单
     * @param id 
     * @return
     */
    public static Order getOrder(int id) {
        return Order.dao.findById(id);
    }

    /**
     * 批量查询退款单
     * @param offset 
     * @param count 
     * @param customerId 
     * @param shopId 
     * @param status 
     * @param startTime 
     * @param endTime 
     * @param productName 
     * @param shopName 
     * @param orderCode 
     * @param tradeCode 
     * @param tradeNo 
     * @return
     */
    public static List<Record> findRefundItems(int offset, int count, Integer customerId, Integer shopId, 
    		Integer status, String startTime, String endTime, String productName, String shopName, 
    		String orderCode, String tradeCode, String tradeNo, String operator, 
    		Map<String, String> orderByMap) {
        
    	String sql = findRefundItemsSql(customerId, shopId, status, startTime, endTime, productName, 
    			shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
    	
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        List<Record> list = Db.find(sql);
        list = appendRefundItems(list);
        
        return list;
    }
    
    public static List<Record> findRefundItems(Integer customerId, Integer shopId, 
    		Integer status, String startTime, String endTime, String productName, String shopName, 
    		String orderCode, String tradeCode, String tradeNo, String operator, 
    		Map<String, String> orderByMap) {
        
    	String sql = findRefundItemsSql(customerId, shopId, status, startTime, endTime, productName, 
    			shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
    	
        List<Record> list = Db.find(sql);
        list = appendRefundItems(list);
        
        return list;
    }
    
    private static List<Record> appendRefundItems(List<Record> list) {
    	List<Record> customers = Db.find("select * from customer");
        List<Record> products = Db.find("select * from product");
        List<Record> resources = Db.find("select * from resource");
        
        for (Record item : list) {
        	int productId = item.getInt("productId");
        	Record product = BaseDao.findItem(productId, products, "id");
        	int mainPic = product != null ? product.getInt("mainPic") : 0;
        	Record resource = BaseDao.findItem(mainPic, resources, "id");
        	String path = resource != null ? resource.getStr("path") : "";
        	item.set("mainPic", path);
        	int customerId = item.getInt("customer_id");
        	Record customer = BaseDao.findItem(customerId, customers, "id");
        	String customerName = BaseDao.getCustomerName(customer);
        	String customerPhone = customer != null ? customer.getStr("mobilePhone") : "";
        	item.set("customerName", customerName);
        	item.set("customerPhone", customerPhone);
        }
        
        return list;
    }
    
    public static List<Record> findRefundItems(int offset, int count, Integer customerId, Integer shopId, 
    		Integer status, String startTime, String endTime, String orderCode, String tradeCode, 
    		String tradeNo, Map<String, String> orderByMap) {
    	
    	return findRefundItems(offset, count, customerId, shopId, status, startTime, endTime, null, null, orderCode, tradeCode, tradeNo, null, orderByMap);
    }
    
    public static int countRefundItems(Integer customerId, Integer shopId, 
    		Integer status, String startTime, String endTime, String orderCode, String tradeCode, 
    		String tradeNo) {
    	
    	return countRefundItems(customerId, shopId, status, startTime, endTime, null, null, orderCode, tradeCode, tradeNo, null);
    }

    /**
     * @param offset 
     * @param count 
     * @param customerId 
     * @param shopId 
     * @param status 
     * @param startTime 
     * @param endTime 
     * @param productName 
     * @param shopName 
     * @param orderCode 
     * @param tradeCode 
     * @param tradeNo 
     * @return
     */
    public static int countRefundItems(Integer customerId, Integer shopId, Integer status, 
    		String startTime, String endTime, String productName, String shopName, String orderCode, 
    		String tradeCode, String tradeNo, String operator) {
    	
    	String sql = findRefundItemsSql(customerId, shopId, status, startTime, endTime, productName, 
    			shopName, orderCode, tradeCode, tradeNo, operator, null);
    	
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param shopId
     * @param status
     * @param startTime
     * @param endTime
     * @param productName
     * @param shopName
     * @param orderCode
     * @param tradeCode
     * @param tradeNo
     * @param operator
     * @return
     */
    public static String findRefundItemsSql(Integer customerId, Integer shopId, Integer status, 
    		String startTime, String endTime, String productName, String shopName, String orderCode, 
    		String tradeCode, String tradeNo, String operator, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, c.orderTime, c.order_code as orderCode, c.order_code, c.source, c.payType," + 
    			" c.payTime, b.order_id, b.totalPrice as tradeCash, b.product_id as productId," + 
    			" b.product_name as productName, b.actualUnitPrice, b.taxRate, d.name" +
    			" from refund as a" +
    			" left join product_order as b on a.product_order_id = b.id" +
    			" left join `order` as c on b.order_id = c.id" +
    			" left join shop as d on c.shop_id = d.id" +
    			" where a.id != 0";
    	
    	if (customerId != null) {
			sql += " and a.customer_id = " + customerId;
		}
    	if (shopId != null) {
			sql += " and d.id = " + shopId;
		}
    	if (status != null) {
			sql += " and a.status = " + status;
		}
    	if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
    	if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
    	if (productName != null && !productName.equals("")) {
			sql += " and b.product_name like '%" + productName + "%'";
		}
    	if (shopName != null && !shopName.equals("")) {
			sql += " and d.name like '%" + shopName + "%'";
		}
    	if (orderCode != null && !orderCode.equals("")) {
			sql += " and c.order_code like concat('%'," + '"' + orderCode + '"' + ",'%')";
		}
    	if (tradeCode != null && !tradeCode.equals("")) {
			sql += " and c.theSameOrderNum like '%" + tradeCode + "%'";
		}
    	if (tradeNo != null && !tradeNo.equals("")) {
			sql += " and c.tradeNo like '%" + tradeNo + "%'";
		}
    	if (operator != null && !operator.equals("")) {
			sql += " and a.operator like '%" + operator + "%'";
		}
    	
    	// 排序
    	String orderBySql = BaseDao.getOrderSql(orderByMap);
    	sql = sql + orderBySql;
    	
    	return sql;
    }

    /**
     * 查看退款单
     * @param id 
     * @return
     */
    public static Refund getRefund(int id) {
        return Refund.dao.findById(id);
    }
    
    public static double getAllowDeliveryPriceWithRefund(int refundId) {
    	String orderSql = "select o.* from refund as r" +
    			" left join product_order as po on r.product_order_id = po.id" +
    			" left join `order` as o on po.order_id = o.id" +
    			" where r.id = ?";
    	Record order = Db.findFirst(orderSql, refundId);
    	double orderDeliveryPrice = order.getBigDecimal("deliveryPrice").doubleValue();
    	String refundSql = "select r.*, sum(r.deliveryPrice) as totalRefundDeliveryPrice from `order` as o" +
    			" left join product_order as po on o.id = po.order_id" +
    			" left join refund as r on po.id = r.product_order_id" +
    			" where o.id = ?" +
    			" and r.id != ?";
    	Record refund = Db.findFirst(refundSql, order.getInt("id"), refundId);
    	double totalRefundDeliveryPrice = refund.get("totalRefundDeliveryPrice") != null ? refund.getBigDecimal("totalRefundDeliveryPrice").doubleValue() : 0;
    	double allDeliveryPrice = orderDeliveryPrice > totalRefundDeliveryPrice ? orderDeliveryPrice - totalRefundDeliveryPrice : 0;
    	return allDeliveryPrice;
    }
    
    public static double getAllowDeliveryPriceWithBack(int refundId) {
    	String orderSql = "select o.* from back as r" +
    			" left join product_order as po on r.product_order_id = po.id" +
    			" left join `order` as o on po.order_id = o.id" +
    			" where r.id = ?";
    	Record order = Db.findFirst(orderSql, refundId);
    	double orderDeliveryPrice = order.getBigDecimal("deliveryPrice").doubleValue();
    	String refundSql = "select r.*, sum(r.deliveryPrice) as totalRefundDeliveryPrice from `order` as o" +
    			" left join product_order as po on o.id = po.order_id" +
    			" left join back as r on po.id = r.product_order_id" +
    			" where o.id = ?" +
    			" and r.id != ?";
    	Record refund = Db.findFirst(refundSql, order.getInt("id"), refundId);
    	double totalRefundDeliveryPrice = refund.get("totalRefundDeliveryPrice") != null ? refund.getBigDecimal("totalRefundDeliveryPrice").doubleValue() : 0;
    	double allDeliveryPrice = orderDeliveryPrice > totalRefundDeliveryPrice ? orderDeliveryPrice - totalRefundDeliveryPrice : 0;
    	return allDeliveryPrice;
    }
    
    /**
     * 修改退款金额
     * @param model
     * @return
     */
    public static ServiceCode updateRefundCash(Refund model) {
    	int productOrderId = model.getProductOrderId();
    	ProductOrder productOrder = getProductOrder(productOrderId);
    	int orderId = productOrder.getOrderId();
    	Order order = Order.dao.findById(orderId);
    	
    	BigDecimal totalPrice = productOrder.getTotalPrice();
    	BigDecimal deliveryPrice = order.getDeliveryPrice();
    	BigDecimal maxCash = totalPrice.add(deliveryPrice);
    	BigDecimal refundCash = model.getRefundCash();
    	
    	if (refundCash.compareTo(maxCash) > 0) {
			return ServiceCode.Failed;
		}
    	
    	model.update();
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 计算退货总金额
     * @param list
     * @return
     */
    public static Record calculateReturnedItems(List<Record> list) {
    	Record result = new Record();
    	double totalRefundCash = 0;
    	double totalTradeCash = 0;
    	
    	for (Record item : list) {
    		totalRefundCash += item.getBigDecimal("refundCash").doubleValue();
    		totalTradeCash += item.getBigDecimal("tradeCash").doubleValue();
    	}
    	
    	result.set("totalRefundCash", totalRefundCash);
    	result.set("totalTradeCash", totalTradeCash);
    	
    	return result;
    }

    /**
     * 批量查询退货单
     * @param offset 
     * @param count 
     * @param customerId 
     * @param shopId 
     * @param status 
     * @param startTime 
     * @param endTime 
     * @param productName 
     * @param shopName 
     * @param orderCode 
     * @param tradeCode 
     * @param tradeNo 
     * @return
     */
    public static List<Record> findReturnedItems(int offset, int count, Integer customerId, Integer shopId, 
    		Integer status, String startTime, String endTime, String productName, String shopName, 
    		String orderCode, String tradeCode, String tradeNo, String operator, 
    		Map<String, String> orderByMap) {
    	
        String sql = findReturnedItemsSql(customerId, shopId, status, startTime, endTime, productName, 
        		shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
        
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        List<Record> list = Db.find(sql);
        list = appendReturnedItems(list);
        
        return list;
    }
    
    public static List<Record> findReturnedItems(Integer customerId, Integer shopId, 
    		Integer status, String startTime, String endTime, String productName, String shopName, 
    		String orderCode, String tradeCode, String tradeNo, String operator, 
    		Map<String, String> orderByMap) {
    	
        String sql = findReturnedItemsSql(customerId, shopId, status, startTime, endTime, productName, 
        		shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
        
        List<Record> list = Db.find(sql);
        list = appendReturnedItems(list);
        
        return list;
    }
    
    private static List<Record> appendReturnedItems(List<Record> list) {
        List<Record> products = Db.find("select * from product");
        List<Record> customers = Db.find("select * from customer");
        
        for (Record item : list) {
        	int productId = item.getInt("product_id");
        	Record product = BaseDao.findItem(productId, products, "id");
        	int intPic = product != null ? product.getInt("mainPic") : 0;
        	String mainPic = ResourceService.getPath(intPic);
        	item.set("mainPic", mainPic);
        	
        	int customerId = item.getInt("customer_id");
        	Record customer = BaseDao.findItem(customerId, customers, "id");
        	String customerName = BaseDao.getCustomerName(customer);
        	item.set("customerName", customerName);
        }
        
        return list;
    }
    
    /**
     * 批量查询退货的总数量
     * @param customerId 
     * @param shopId 
     * @param status 
     * @param startTime 
     * @param endTime 
     * @param productName 
     * @param shopName 
     * @param orderCode 
     * @param tradeCode 
     * @param tradeNo 
     * @return
     */
    public static int countReturnedItems(Integer customerId, Integer shopId, Integer status, 
    		String startTime, String endTime, String productName, String shopName, String orderCode, 
    		String tradeCode, String tradeNo, String operator) {
    	
        String sql = findReturnedItemsSql(customerId, shopId, status, startTime, endTime, productName, 
        		shopName, orderCode, tradeCode, tradeNo, operator, null);
        
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param shopId
     * @param status
     * @param startTime
     * @param endTime
     * @param productName
     * @param shopName
     * @param orderCode
     * @param tradeCode
     * @param tradeNo
     * @param operator
     * @return
     */
    public static String findReturnedItemsSql(Integer customerId, Integer shopId, Integer status, 
    		String startTime, String endTime, String productName, String shopName, String orderCode, 
    		String tradeCode, String tradeNo, String operator, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, c.orderTime, c.order_code as orderCode, c.source, c.payType, b.product_id," + 
    			" b.product_name as productName, b.product_summary, b.actualUnitPrice," + 
    			" b.totalPrice as tradeCash, d.name" +
    			" from back as a" +
    			" left join product_order as b on a.product_order_id = b.id" +
    			" left join `order` as c on b.order_id = c.id" +
    			" left join shop as d on c.shop_id = d.id" +
    			" where a.id != 0" +
    			" and b.product_id is not null";
    	
    	if (customerId != null) {
			sql += " and a.customer_id = " + customerId;
		}
    	if (shopId != null) {
			sql += " and d.id = " + shopId;
		}
    	if (status != null) {
			sql += " and a.status = " + status;
		}
    	if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
    	if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
    	if (productName != null && !productName.equals("")) {
			sql += " and b.product_name like '%" + productName + "%'";
		}
    	if (shopName != null && !shopName.equals("")) {
			sql += " and d.name like '%" + shopName + "%'";
		}
    	if (orderCode != null && !orderCode.equals("")) {
			sql += " and c.order_code like '%" + orderCode + "%'";
		}
    	if (tradeCode != null && !tradeCode.equals("")) {
			sql += " and c.theSameOrderNum like '%" + tradeCode + "%'";
		}
    	if (tradeNo != null && !tradeNo.equals("")) {
			sql += " and c.tradeNo like '%" + tradeNo + "%'";
		}
    	if (operator != null && !operator.equals("")) {
			sql += " and a.operator like '%" + operator + "%'";
		}
    	
    	String orderBySql = BaseDao.getOrderSql(orderByMap);
    	sql += orderBySql;
    	
    	System.out.println(sql);
    	
    	return sql;
    }

    /**
     * 查看退货单
     * @param id 
     * @return
     */
    public static Back getReturned(int id) {
        return Back.dao.findById(id);
    }
    
    /**
     * 修改退货金额
     * @param model
     * @return
     */
    public static ServiceCode updateRefundCash(Back model) {
    	int productOrderId = model.getProductOrderId();
    	ProductOrder productOrder = getProductOrder(productOrderId);
    	int orderId = productOrder.getOrderId();
    	Order order = Order.dao.findById(orderId);
    	
    	BigDecimal totalPrice = productOrder.getTotalPrice();
    	BigDecimal deliveryPrice = order.getDeliveryPrice();
    	BigDecimal maxCash = totalPrice.add(deliveryPrice);
    	BigDecimal refundCash = model.getRefundCash();
    	
    	if (refundCash.compareTo(maxCash) > 0) {
			return ServiceCode.Failed;
		}
    	
    	model.update();
    	
    	return ServiceCode.Success;
    }

    /**
     * 查看订单明细
     * @param id
     * @return
     */
    public static ProductOrder getProductOrder(int id) {
    	ProductOrder model = ProductOrder.dao.findById(id);
    	if (model != null) {
    		Product product = Product.dao.findById(model.getProductId());
    		int mainPic = (product != null) ? product.getMainPic() : 0;
    		model.put("mainPic", ResourceService.getPath(mainPic));
    	}
    	return model;
    }
    
    /**
     * 批量查询订单明细
     * @param offset
     * @param count
     * @param orderId
     * @param productName
     * @param supplierId
     * @return
     */
    public static List<Record> findProductOrderItems(int offset, int count, Integer orderId, 
    		String productName, Integer supplierId) {
    	
    	String sql = findProductOrderItemsSql(orderId, productName, supplierId);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	return findProductOrderItemsModel(sql);
    }
    
    /**
     * 批量查询订单明细
     * @param orderId
     * @param productName
     * @param supplierId
     * @return
     */
    public static List<Record> findProductOrderItems(Integer orderId, String productName, 
    		Integer supplierId) {
    	
    	String sql = findProductOrderItemsSql(orderId, productName, supplierId);
    	return findProductOrderItemsModel(sql);
    }
    
    private static List<Record> findProductOrderItemsModel(String sql) {
    	List<Record> list = Db.find(sql);
    	for (Record item : list) {
    		String mainPic = ResourceService.getPath(item.getInt("mainPic"));
    		item.set("mainPic", mainPic);
    	}
    	return list;
    }
    
    /**
     * 批量查询订单明细的总数量
     * @param orderId
     * @param productName
     * @param supplierId
     */
    public static int getProductOrderItemsCount(Integer orderId, String productName, Integer supplierId) {
    	String sql = findProductOrderItemsSql(orderId, productName, supplierId);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param orderId
     * @param productName
     * @param supplierId
     */
    public static String findProductOrderItemsSql(Integer orderId, String productName, Integer supplierId) {
    	String sql = "select a.*, b.mainPic from product_order as a" +
    			" inner join product as b on a.product_id = b.id";
    	
    	if (orderId != null) {
			sql += " and a.order_id = " + orderId;
		}
    	if (productName != null && productName.equals("")) {
			sql += " and a.product_name like '%" + productName + "%'";
		}
    	if (supplierId != null) {
			sql += " and a.supplier_id = " + supplierId;
		}
    	
    	return sql;
    }
    
    /**
     * 获取物流信息
     * @param orderId 订单id
     * @return {logisticsName:物流公司名称,expressCode:快递单号,list:[{title:neir,time:时间},...]}
     */
    public static Record getExpressDetail(int orderId) {
    	Record result = Db.findById("order", orderId);
    	
    	if (result == null) {
    		return null;
    	}
    	
    	List<Record> list = new ArrayList<Record>();
    	String expressDetail = result.get("expressDetail");
    	JSONObject expressDetailJson = JSON.parseObject(expressDetail);
    	
    	if (expressDetailJson != null) {
    		JSONObject lastResult = expressDetailJson.getJSONObject("lastResult");
    		JSONArray arr = lastResult.getJSONArray("data");
    		for (int i = 0; i < arr.size(); i++) {
    			JSONObject arrItem = arr.getJSONObject(i);
    			String context = arrItem.getString("context");
    			String time = arrItem.getString("time");
    			
    			Record item = new Record();
    			item.set("title", context);
    			item.set("time", time);
    			
    			list.add(item);
    		}
    	}
    	
    	result.set("list", list);
    	return result;
    }
    
    /**
     * 统计用户每个状态的订单数量
     * @param customerId
     * @return
     */
    public static Record countOrderStatusAmount(int customerId) {
    	//status1: 待付款数量, status2: 待发货数量, status3: 待收货数量, status4: 待评价数量, status5: 申请中售后数量
    	List<Record> orders = Db.find("select * from `order` where customer_id = ?", customerId);
    	int status1 = BaseDao.findItems(orders, "status", 1).size();
    	int status2 = BaseDao.findItems(orders, "status", 2).size();
    	int status3 = BaseDao.findItems(orders, "status", 3).size();
    	int status4 = BaseDao.findItems(orders, "status", 4).size();
    	int refunds = Refund.dao.find("select * from refund where status = ? and customer_id = ?", 0, customerId).size();
    	int backs = Back.dao.find("select * from back where status = ? and customer_id = ?", 0, customerId).size();
    	
    	Record result = new Record();
    	result.set("status1", status1);
    	result.set("status2", status2);
    	result.set("status3", status3);
    	result.set("status4", status4);
    	result.set("refunds", refunds);
    	result.set("backs", backs);
    	return result;
    }
    
}