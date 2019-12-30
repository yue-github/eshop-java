package com.eshop.volume;

import java.math.BigDecimal;
import java.util.List;

import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 销量统计类
 */
public class ProductSalesVolume {
	
	/**
     * 统计商品的销量
     * @param offset
     * @param count
     * @param name 产品名称
     * @return
     */
    public static List<Record> productSalesNum(int offset, int count, String name, String startOrderTime, String endOrderTime) {
    	String sql = productSalesNumSql(name, startOrderTime, endOrderTime);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    public static List<Record> productSalesNum(String name, String startOrderTime, String endOrderTime) {
    	String sql = productSalesNumSql(name, startOrderTime, endOrderTime);
    	return Db.find(sql);
    }
    
    public static Record statisticProductSale(List<Record> list) {
    	Record result = new Record();
    	BigDecimal totalSalesPrice = new BigDecimal(0);
    	int totalSalesVolume = 0;
    	
    	for (Record item : list) {
    		totalSalesPrice = totalSalesPrice.add(item.getBigDecimal("salesPrice"));
    		totalSalesVolume += item.getNumber("salesVolume").intValue();
    	}
    	
    	result.set("totalSalesPrice", totalSalesPrice);
    	result.set("totalSalesVolume", totalSalesVolume);
    	return result;
    }
    
    /**
     * 商品的销量总条数
     * @return
     */
    public static int countProductSalesNum(String name, String startOrderTime, String endOrderTime) {
    	String sql = productSalesNumSql(name, startOrderTime, endOrderTime);
    	return Db.find(sql).size();
    }
    
    /**
     * 客户购买商品种类统计
     * @param offset
     * @param count
     * @param cname 分类名称
     * @return
     */
    public static List<Record> cetegoryByCustomerBuy(int offset, int count, String cname, String startOrderTime, String endOrderTime) {
    	String sql = cetegoryByCustomerBuySql(cname, startOrderTime, endOrderTime);
    	
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    public static List<Record> cetegoryByCustomerBuy(String cname, String startOrderTime, String endOrderTime) {
    	String sql = cetegoryByCustomerBuySql(cname, startOrderTime, endOrderTime);
    	return Db.find(sql);
    }
    
    public static Record statisticsCategoryByCustomerBuy(List<Record> list) {
    	Record result = new Record();
    	BigDecimal totalSalesPrice = new BigDecimal(0);
    	int totalSalesVolume = 0;
    	
    	for (Record item : list) {
    		totalSalesPrice = totalSalesPrice.add(item.getBigDecimal("salesPrice"));
    		totalSalesVolume += item.getNumber("salesVolume").intValue();
    	}
    	
    	result.set("totalSalesPrice", totalSalesPrice);
    	result.set("totalSalesVolume", totalSalesVolume);
    	return result;
    }
    
    /**
     * 商品种类销量总条数
     * @return
     */
    public static int countCetegoryByCustomerBuy(String cname, String startOrderTime, String endOrderTime) {
    	String sql = cetegoryByCustomerBuySql(cname, startOrderTime, endOrderTime);
    	return Db.find(sql).size();
    }
    
    
    /**
     * 总销售金额
     * @return
     */
    public static int salesTotalPrice() {
    	String sql = "SELECT SUM(o.totalActualProductPrice) AS totalPrice"
    			+ " FROM `order` AS o JOIN product_order AS po"
    			+ " ON o.id = po.order_id"
    			+ " WHERE o.status NOT IN (1,6)";
    	BigDecimal totalPrice = Db.findFirst(sql).get("totalPrice");
    	
    	return totalPrice.intValue();
    }
    
    /**
     * 总销售数量
     * @return
     */
    public static int countSalesVolume() {
    	String sql = "SELECT SUM(po.unitOrdered) AS totalSalesVolume"
    			+ " FROM `order` AS o JOIN product_order AS po"
    			+ " ON o.id = po.order_id"
    			+ " WHERE o.status NOT IN (1,6)";
    	BigDecimal totalSalesVolume =  Db.findFirst(sql).get("totalSalesVolume");
    	
    	return totalSalesVolume.intValue();
    }
    
    /**
     * 统计商品的销量sql语句
     */
    public static String productSalesNumSql(String name, String startOrderTime, String endOrderTime) {
    	String sql = "SELECT po.product_id AS pid, po.product_name AS pname, SUM(po.totalActualProductPrice) AS salesPrice, SUM(po.unitOrdered) AS salesVolume, DATE(o.orderTime) AS orderTime"
    			+ " FROM `order` AS o"
    			+ " JOIN product_order AS po"
    			+ " ON o.id = po.order_id"
    			+ " where o.status not in (1,6)";
    	
    	if (name != null && !name.equals("")) {
    		sql += " and po.product_name like '%" + name + "%'";
    	}
    	if (startOrderTime != null && !startOrderTime.equals("")) {
    		sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') >= '" + startOrderTime + "'";
    	}
    	if (endOrderTime != null && !endOrderTime.equals("")) {
    		sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') <= '" + endOrderTime + "'";
    	}
    	
    	sql += " GROUP BY po.product_id, DATE(o.orderTime)"
    			+ " ORDER BY o.orderTime desc";
    	
    	return sql;
    }
    
    /**
     * 客户购买商品种类销量统计sql语句
     */
    public static String cetegoryByCustomerBuySql(String cname, String startOrderTime, String endOrderTime) {
    	String sql = "SELECT po.category_name AS cname,"
    			+ " SUM(o.totalActualProductPrice) AS salesPrice, SUM(po.unitOrdered) AS salesVolume, DATE(o.orderTime) AS orderTime"
    			+ " FROM new_eshop_db.order AS o"
    			+ " JOIN product_order AS po"
    			+ " ON o.id = po.order_id"
    			+ " where o.status not in (1,6)";
    	if (cname != null && !cname.equals("")) {
			sql += " and po.category_name like '%" + cname + "%'";
		}
    	if (startOrderTime != null && !startOrderTime.equals("")) {
    		sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') >= '" + startOrderTime + "'";
    	}
    	
    	if (endOrderTime != null && !endOrderTime.equals("")) {
    		sql += " and DATE_FORMAT(o.orderTime, '%Y-%m-%d') <= '" + endOrderTime + "'";
    	}
    	sql += " GROUP BY po.category_name, DATE(o.orderTime)"
    			+ " ORDER BY o.orderTime DESC";
    	return sql;
    }
}
