package com.eshop.visit;

import java.util.Date;
import java.util.List;

import com.eshop.helper.QueryHelper;
import com.eshop.model.Customer;
import com.eshop.model.CustomerLookRecord;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class CustomerLookRecordService  {

	/**
	 * 创建会员购买/查看商品习惯
	 * @param model
	 */
	public static BaseDao.ServiceCode customerLook(CustomerLookRecord model) {
		
		if(!model.save()) {
	        return BaseDao.ServiceCode.Failed;
		}
		return BaseDao.ServiceCode.Success;
		
	}
	
	/**
	 * 会员购买习惯列表
	 * @param pageIndex 开始页数 1
	 * @param length
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Page<CustomerLookRecord> findCustomerLooks(int pageIndex, int length, String startTime, String endTime) {
		QueryHelper helper = new QueryHelper("customer_look_record", "c");
		if(startTime != null && startTime.length() > 0) {
			helper.addCondition("DATE_FORMAT(c.create_at, '%Y-%m-%d %H:%i:%S')>=?", startTime);
		}
		if(endTime != null && startTime.length() > 0) {
			helper.addCondition("DATE_FORMAT(c.create_at, '%Y-%m-%d %H:%i:%S')<=?", endTime);
		}
		Page<CustomerLookRecord> list = CustomerLookRecord.dao.paginate(pageIndex, length, "select * ", helper.getQuerySql(), helper.getParams().toArray());
		
		return list;
	}
	
	public static Record findItems(int offset, int length, String customerName, String productName, String startTime,
			String endTime) {
		
		String sql = findItemsSql(customerName, productName, startTime, endTime);
		int total = Db.find(sql).size();
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> data = Db.find(sql);
		
		Record result = new Record();
		result.set("total", total);
		result.set("data", data);
		
		return result;
	}
	
	public static String findItemsSql(String customerName, String productName, String startTime,
			String endTime) {
		
		String sql = "select * from customer_look_record where id != 0";
		if (customerName != null && !customerName.equals("")) {
			sql += " and customer_name like '%" + customerName + "%'";
		}
		if (productName != null && !productName.equals("")) {
			sql += " and product_name like '%" + productName + "%'";
		}
		if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(create_at,'%Y-%m-%d') >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(create_at,'%Y-%m-%d') <= '" + endTime + "'";
		}
		return sql;
	}
	
	/**
	 * 根据商品id判断商品是否存在
	 * @param productId
	 * @return
	 */
	public static Boolean isExistByProductId(Integer productId) {
		return Db.findFirst("select * from customer_look_record where product_id=?", productId) != null ? true : false;
	}
	
	public static Boolean isExistByProductId(Integer customerId, Integer productId) {
		CustomerLookRecord model = CustomerLookRecord.dao.findFirst("select * from customer_look_record where customer_id = ? and product_id = ? order by create_at desc", customerId, productId);
		if (model == null) {
			return false;
		}
		long start = model.getCreateAt().getTime();
		long now = new Date().getTime();
		long differ = 30 * 60 * 1000;
		if ((now - start) > differ) {
			return false;
		} else {
			return true;
		}
	}
	
	public static String getCustomerName(Customer customer) {
		if (customer.getName() != null && !customer.getName().equals("")) {
			return customer.getName();
		} else if (customer.getMobilePhone() != null && !customer.getMobilePhone().equals("")) {
			return customer.getMobilePhone();
		} else if (customer.getEmail() != null && !customer.getEmail().equals("")) {
			return customer.getEmail();
		} else if (customer.getNickName() != null && !customer.getNickName().equals("")) {
			return customer.getNickName();
		} else {
			return "";
		}
	}
	
	/**
	 * 随机获取 randomNum 条 某个/所有 客户的查看商品、加入购物车习惯列表
	 * @param randomNum
	 * @param customerId
	 * @return
	 */
	public static List<Record> customerLookProducts(Integer customerId, Integer randomNum) {
		String sql = "SELECT * FROM customer_look_record c";
		
		//customerId ！= null 获取某个客户 
		if (customerId != null) {
			sql += " where c.customer_id = " + customerId;
		}
		//随机抽取
		sql += " ORDER BY rand() limit " + randomNum;
		
		return Db.find(sql);
	}
}
