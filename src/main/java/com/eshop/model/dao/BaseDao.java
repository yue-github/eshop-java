package com.eshop.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eshop.model.Customer;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.model.ProductPrice;
import com.eshop.service.Merchant;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class BaseDao {
	
	public static final int UNPAY = 1;
	public static final int PAYED = 2;
	public static final int DISPATCHED = 3;
	public static final int RECEIVED = 4;
	public static final int CANCELED = 6;
	
	/**
	 * 错误代码，分别为成功、异常、验证错误及功能错误
	 * @author Yang
	 *
	 */
	public enum ServiceCode {
		Success, Failed, Exception, Validation, Function
	}
	
	/**
	 * 分页子句
	 * @param sql
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String appendLimitSql(String sql, int offset, int count) {
		return sql + " limit " + offset + "," + count;
	}
	
	/**
	 * 获取排序子句
	 * @param orderByMap
	 * @return
	 */
	public static String getOrderSql(Map<String, String> orderByMap) {
		String orderBySql = "";
		if (orderByMap != null) {
        	for (Map.Entry<String, String> entry : orderByMap.entrySet()) {
        		orderBySql += entry.getKey() + " " + entry.getValue() + ",";
    	    }
    	}
		if (!orderBySql.equals("")) {
			orderBySql = orderBySql.substring(0, orderBySql.length() - 1);
			orderBySql = " order by " + orderBySql;
    	}
	
		return orderBySql;
	}
	
	/**
     * 构造whereIn子句
     * @param ids [1,2,3,...]
     * @return (2,3)
     */
    public static String getWhereIn(String ids) {
    	String whereIn = "";
    	JSONArray list = JSON.parseArray(ids);
    	int size = list.size();
    	
    	for (int i =0; i < size; i++) {
    		int id = list.getIntValue(i);
    		
    		if (i == (size - 1)) {
    			whereIn += id;
    			break;
    		}
    		
    		whereIn += id + ",";
    	}
    	
    	String result = "";
    	
    	if (whereIn.equals("")) {
    		result = "(null)";
    	} else {
    		result = "(" + whereIn + ")";
    	}
    	
    	return result;
    }
    
    /**
     * 构造wehereIn子句
     * @param arr
     * @return
     */
    public static String getWhereIn(int[] arr) {
    	List<Integer> ls = new ArrayList<Integer>();
    	for (int item : arr) {
    		ls.add(item);
    	}
    	return getWhereIn(ls);
    }
	
	/**
	 * 构造wherein子句
	 * @param list
	 * @return (1,2,...)
	 */
	public static String getWhereIn(List<Integer> ls) {
		List<Integer> list = new ArrayList<Integer>();
		for (Integer item : ls) {
			if (!list.contains(item)) {
				list.add(item);
			}
		}
		
		String whereIn = "";
    	int size = list.size();
    	
    	for (int i =0; i < size; i++) {
    		int id = list.get(i);
    		
    		if (i == (size - 1)) {
    			whereIn += id;
    			break;
    		}
    		
    		whereIn += id + ",";
    	}
    	
    	if (whereIn.equals("")) {
			return "(null)";
		} else {
			return "(" + whereIn + ")";
		}
	}
	
	/**
     * 构造whereIn子句
     * @param list
     * @return (2,3)
     */
    public static String getWhereIn(List<Record> list, String key) {
    	String whereIn = "";
    	int size = list.size();
    	
    	for (int i =0; i < size; i++) {
    		int id = list.get(i).getInt(key);
    		
    		if (i == (size - 1)) {
    			whereIn += id;
    			break;
    		}
    		
    		whereIn += id + ",";
    	}
    	
    	String result = "";
    	
    	if (whereIn.equals("")) {
    		result = "(null)";
    	} else {
    		result = "(" + whereIn + ")";
    	}
    	
    	return result;
    }
	
	/**
     * 获取产品实际价格
     * @param productId
     * @param priceId
     * @return
     */
	public static Record getSku(int productId, int priceId) {
    	Record sku = new Record();
    	
    	ProductPrice productPrice = ProductPrice.dao.findById(priceId);
    	if (productPrice != null) {
    		double price = productPrice.getPrice().doubleValue();
    		
    		double originPrice = 0;
    		if (productPrice.getOriginUnitPrice() == null) {
				originPrice = price;
			} else {
				originPrice = productPrice.getOriginUnitPrice().doubleValue();
			}
    		
    		double unitCost = 0;
    		if (productPrice.getUnitCost() == null) {
    			unitCost = price;
			} else {
				unitCost = productPrice.getUnitCost().doubleValue();
			}
    		
    		sku.set("price", price);
    		sku.set("originPrice", originPrice);
    		sku.set("cost", unitCost);
    		return sku;
    	}
    	
    	Product product = Product.dao.findById(productId);
    	if (product != null) {
    		double suggestedRetailUnitPrice = product.getSuggestedRetailUnitPrice().doubleValue();
    		int isSeckill = Merchant.isSeckill(product);
    		if (isSeckill == 1) {
    			suggestedRetailUnitPrice = product.getSecPrice().doubleValue();
    		}
    		
    		double originUnitPrice = 0;
    		if (product.getOriginUnitPrice() == null) {
				originUnitPrice = suggestedRetailUnitPrice;
			} else {
				originUnitPrice = product.getOriginUnitPrice().doubleValue();
			}
    		
    		double unitcost = 0;
    		if (product.getUnitCost() == null) {
				unitcost = suggestedRetailUnitPrice;
			} else {
				unitcost = product.getUnitCost().doubleValue();
			}
    		
    		sku.set("price", suggestedRetailUnitPrice);
    		sku.set("originPrice", originUnitPrice);
    		sku.set("cost", unitcost);
    		return sku;
    	}
    	
    	return null;
    }
    
    /**
     * 获取产品实际价格
     * @param record
     * @return {"price":2, "originPrice":3, "cost":4}
     */
	public static Record getSku(Record record) {
    	int priceId = record.getNumber("price_id").intValue();
    	int productId = record.getInt("product_id");
    	Product product = Product.dao.findById(productId);
    	Record result = getSku(productId, priceId);
    	if (product.getProdType() == 1) {
    		result.set("price", record.getDouble("price"));
    		result.set("originPrice", record.getDouble("price"));
    		result.set("cost", record.getDouble("unitCost"));
    	}
    	return result;
    }
	
	/**
	 * 获取选中的销售属性
	 * @param priceId
	 * @return
	 */
	public static String getselectProterties(int priceId) {
    	ProductPrice price = ProductPrice.dao.findById(priceId);
    	if (price == null) {
			return "";
		}
    	
    	String typeAttr = price.getTypeAttr();
    	String[] attrs = typeAttr.split(",");
    	String selectProterties = "";
    	
    	for(String s : attrs) {
    		int aid = Integer.parseInt(s);
    		
    	    List<Record> ps = Db.find("select property_value.*, property.name as property_name from property_value left join property on property.id = property_value.property_id where property_value.id = ?", aid);
    	    
    	    if(ps.size() > 0) {
    	    	Record p = ps.get(0);
    	    	
    	    	selectProterties += p.getStr("property_name") + "：" + p.getStr("name") + " ";
    	    }
    	}

    	return selectProterties;
    }
	
	/**
     * 获取单品
     * @param productId 产品id
     * @param typeAttr 销售属性
     * @return
     */
    public static ProductPrice getProductPrice(int productId, String typeAttr) {
    	ProductPrice productPrice =  ProductPrice.dao.findFirst("select * from product_price where product_id = ? and type_attr = ?", productId, typeAttr);
    	return productPrice;
    }
    
    /**
     * 获取订单
     * @param productOrderId
     * @return
     */
    public static Order getOrderByProductOrderId(int productOrderId) {
    	ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    	if (productOrder == null) {
    		return null;
    	}
    	Order order = Order.dao.findById(productOrder.getOrderId());
    	return order;
    }
    
    /**
	 * 筛选出符合条件的集合
	 * @param list
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<Record> findItems(List<Record> list, String key, int value) {
		List<Record> lst = new ArrayList<Record>();
		for (Record item : list) {
			if (item.getInt(key) == value) {
				lst.add(item);
			}
		}
		return lst;
	}
	
	public static List<Record> findItems(List<Record> list, String key, String value) {
		List<Record> lst = new ArrayList<Record>();
		for (Record item : list) {
			if (value.equals(item.getStr(key))) {
				lst.add(item);
			}
		}
		return lst;
	}
    
    public static Record findItem(int value, List<Record> list, String key) {
    	if(list != null && key != null){
    		for (Record item : list) {
        		if (item.getInt(key) == value) {
    				return item;
    			}
        	}
    	}
    	
    	return null;
    }
    
    /**
     * 获取库存
     * @param productId
     * @param priceId
     * @return
     */
    public static int getStoreAmount(int productId, int priceId) {
    	int amount = 0;
    	
    	Product product = Product.dao.findById(productId);
    	ProductPrice productPrice = ProductPrice.dao.findById(priceId);
    	
    	if (productPrice != null) {
    		amount = productPrice.getProductNumber();
    		return amount;
    	}
    	
    	if (product != null) {
    		amount = product.getStoreAmount();
    		return amount;
    	}
    	
    	return amount;
    }
    
    public static String getCustomerName(Record customer) {
    	String customerName = "";
    	if (customer != null) {
    		if (customer.getStr("name") != null && !customer.getStr("name").equals("")) {
    			customerName = customer.getStr("name");
    		} else if (customer.getStr("nickName") != null && !customer.getStr("nickName").equals("")) {
    			customerName = customer.getStr("nickName");
    		} else if (customer.getStr("mobilePhone") != null && !customer.getStr("mobilePhone").equals("")) {
    			customerName = customer.getStr("mobilePhone");
    		} else if (customer.getStr("email") != null && !customer.getStr("email").equals("")) {
    			customerName = customer.getStr("email");
    		}
    	}
    	return customerName;
    }
    
    public static String getCustomerName(Customer customer) {
    	String customerName = "";
    	if (customer != null) {
    		if (customer.getStr("name") != null && !customer.getStr("name").equals("")) {
    			customerName = customer.getStr("name");
    		} else if (customer.getStr("nickName") != null && !customer.getStr("nickName").equals("")) {
    			customerName = customer.getStr("nickName");
    		} else if (customer.getStr("mobilePhone") != null && !customer.getStr("mobilePhone").equals("")) {
    			customerName = customer.getStr("mobilePhone");
    		} else if (customer.getStr("email") != null && !customer.getStr("email").equals("")) {
    			customerName = customer.getStr("email");
    		}
    	}
    	return customerName;
    }
    
}
