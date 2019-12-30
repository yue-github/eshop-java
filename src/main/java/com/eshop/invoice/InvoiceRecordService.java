package com.eshop.invoice;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eshop.model.InvoiceRecord;
import com.eshop.model.Order;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class InvoiceRecordService {
	
	/**
	 * 开发票
	 * @param invoiceCode
	 * @param money
	 * @param orderId
	 * @return
	 */
	public static ServiceCode create(String invoiceCode, BigDecimal money, int orderId) {
		Order order = Order.dao.findById(orderId);
		InvoiceRecord model = new InvoiceRecord();
		model.setInvoiceCode(invoiceCode);
		model.setMoney(money);
		model.setTheSameOrderNum(order.getTheSameOrderNum());
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		model.setOrderId(orderId);
		model.setOrderCode(order.getOrderCode());
		model.setTotalPayable(order.getTotalPayable());
		
		if (!model.save()) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
	}
	
	/**
	 * 开发票
	 * @param orderId
	 * @param invoices
	 * @return
	 */
	public static ServiceCode create(final int orderId, final String invoices) {
		System.out.println("orderId="+orderId);
		System.out.println("invoices="+invoices);
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from invoice_record where orderId = ?", orderId);
					
					JSONArray arr = JSON.parseArray(invoices);
					System.out.println(arr);
					for (int i = 0; i < arr.size(); i++) {
			    		JSONObject obj = arr.getJSONObject(i);
			    		String invoiceCode = obj.getString("invoiceCode");
			    		BigDecimal money = obj.getBigDecimal("money");
			    		
			    		Order order = Order.dao.findById(orderId);
						InvoiceRecord model = new InvoiceRecord();
						model.setInvoiceCode(invoiceCode);
						model.setMoney(money);
						model.setTheSameOrderNum(order.getTheSameOrderNum());
						model.setCreatedAt(new Date());
						model.setUpdatedAt(new Date());
						model.setOrderId(orderId);
						model.setOrderCode(order.getOrderCode());
						model.setTotalPayable(order.getTotalPayable());
						model.save();
			    	}
					
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 修改发票
	 * @param id
	 * @param invoiceCode
	 * @param money
	 * @param orderId
	 * @return
	 */
	public static ServiceCode update(int id, String invoiceCode, BigDecimal money, int orderId) {
		Order order = Order.dao.findById(orderId);
		InvoiceRecord model = InvoiceRecord.dao.findById(id);
		model.setInvoiceCode(invoiceCode);
		model.setMoney(money);
		model.setTheSameOrderNum(order.getTheSameOrderNum());
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		model.setOrderId(orderId);
		model.setOrderCode(order.getOrderCode());
		model.setTotalPayable(order.getTotalPayable());
		
		if (!model.save()) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
	}
	
	/**
	 * 删除发票
	 * @param id
	 * @return
	 */
	public static ServiceCode delete(int id) {
		boolean flag = InvoiceRecord.dao.deleteById(id);
		if (!flag) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
	}
	
	/**
	 * 查看详细
	 * @param id
	 * @return
	 */
	public static Record get(int id) {
		String sql = "select * from invoice_record as a" + 
				" left join order_invoice as b on a.order_id = b.order_id" +
				" where id = " + id;
		return Db.findFirst(sql);
	}
	
	/**
	 * 批量查询已开发票记录
	 * @param offset
	 * @param count
	 * @param type
	 * @param invoiceHead
	 * @param invoiceContent
	 * @param name
	 * @param phone
	 * @param email
	 * @param companyName
	 * @param companyCode
	 * @param companyAddress
	 * @param companyPhone
	 * @param bankName
	 * @param bankAccount
	 * @param minTotalPayable
	 * @param maxTotalPayable
	 * @param orderCode
	 * @param startTime
	 * @param endTime
	 * @param invoiceCode
	 * @return
	 */
	public static List<Record> findInvoiceRecordItems(int offset, int count, Integer orderId, Integer type, 
			String invoiceHead, String invoiceContent, String name, String phone, String email, 
			String companyName, String companyCode, String companyAddress, String companyPhone, 
			String bankName, String bankAccount, Double minTotalPayable, 
			Double maxTotalPayable, String orderCode, String startTime, String endTime, 
			String invoiceCode, String theSameOrderNum,Double moneyLessThan, Double moneyMoreThan, Map<String, String> orderByMap) {
		
		String sql = findInvoiceRecordItemsSql(orderId, type, invoiceHead, invoiceContent, name, phone, 
				email, companyName, companyCode, companyAddress, companyPhone, bankName, bankAccount, 
				minTotalPayable, maxTotalPayable, orderCode, startTime, endTime, invoiceCode, 
				theSameOrderNum,moneyLessThan,moneyMoreThan, orderByMap);
		
		sql = BaseDao.appendLimitSql(sql, offset, count);
		
		return Db.find(sql);
	}
	
	public static List<Record> findInvoiceRecordItems(Integer orderId, Integer type, String invoiceHead, 
			String invoiceContent, String name, String phone, String email, 
			String companyName, String companyCode, String companyAddress, String companyPhone, 
			String bankName, String bankAccount, Double minTotalPayable, 
			Double maxTotalPayable, String orderCode, String startTime, String endTime, 
			String invoiceCode, String theSameOrderNum,Double moneyLessThan,Double moneyMoreThan, Map<String, String> orderByMap) {
		
		String sql = findInvoiceRecordItemsSql(orderId, type, invoiceHead, invoiceContent, name, phone, 
				email, companyName, companyCode, companyAddress, companyPhone, bankName, bankAccount, 
				minTotalPayable, maxTotalPayable, orderCode, startTime, endTime, invoiceCode, 
				theSameOrderNum,moneyLessThan,moneyMoreThan, orderByMap);
		
		return Db.find(sql);
	}
	
	public static List<Record> findInvoiceRecordItems(int offset, int count, Integer orderId, Integer type, 
			String invoiceHead, String orderCode, String startTime, String endTime, String invoiceCode,String invoiceContent, 
			String theSameOrderNum,Double moneyLessThan,Double moneyMoreThan, Map<String, String> orderByMap) {
		
		return findInvoiceRecordItems(offset, count, orderId, type, invoiceHead, invoiceContent, null, null, null, 
				null, null, null, null, null, null, null, null, orderCode, startTime, endTime, 
				invoiceCode, theSameOrderNum,moneyLessThan,moneyMoreThan, orderByMap);
	}
	
	public static List<Record> findInvoiceRecordItems(Integer orderId, Map<String, String> orderByMap) {
		
		String sql = findInvoiceRecordItemsSql(orderId, null, null, null, null, null, null, null, null, 
				null, null, null, null, null, null, null, null, null, null, null,null ,null, orderByMap);
		
		return Db.find(sql);
	}
	
	public static int countInvoiceRecordItems(Integer orderId, Integer type, String invoiceHead, 
			String orderCode, String startTime, String endTime, String invoiceCode,String invoiceContent, String theSameOrderNum,Double moneyLessThan,Double moneyMoreThan) {
		
		return countInvoiceRecordItems(orderId, type, invoiceHead, null, null, null, null, null, null, 
				null, null, null, null, null, null, orderCode, startTime, endTime, invoiceCode, 
				theSameOrderNum,moneyLessThan,moneyMoreThan);
	}
	
	/**
	 * 批量查询已开发票记录的总数量
	 * @param type
	 * @param invoiceHead
	 * @param invoiceContent
	 * @param name
	 * @param phone
	 * @param email
	 * @param companyName
	 * @param companyCode
	 * @param companyAddress
	 * @param companyPhone
	 * @param bankName
	 * @param bankAccount
	 * @param minTotalPayable
	 * @param maxTotalPayable
	 * @param orderCode
	 * @param startTime
	 * @param endTime
	 * @param invoiceCode
	 * @return
	 */
	public static int countInvoiceRecordItems(Integer orderId, Integer type, String invoiceHead, String invoiceContent, 
			String name, String phone, String email, String companyName, String companyCode, 
			String companyAddress, String companyPhone, String bankName, String bankAccount, 
			Double minTotalPayable, Double maxTotalPayable, String orderCode, String startTime, 
			String endTime, String invoiceCode, String theSameOrderNum, Double moneyLessThan,Double moneyMoreThan) {
		
		String sql = findInvoiceRecordItemsSql(orderId, type, invoiceHead, invoiceContent, name, phone, email, 
				companyName, companyCode, companyAddress, companyPhone, bankName, bankAccount, 
				minTotalPayable, maxTotalPayable, orderCode, startTime, endTime, invoiceCode,
				theSameOrderNum, moneyLessThan,moneyMoreThan, null);
		
		return Db.find(sql).size();
	}
	
	/**
	 * 组装sql语句
	 * @param type
	 * @param invoiceHead
	 * @param invoiceContent
	 * @param name
	 * @param phone
	 * @param email
	 * @param companyName
	 * @param companyCode
	 * @param companyAddress
	 * @param companyPhone
	 * @param bankName
	 * @param bankAccount
	 * @param minTotalPayable
	 * @param maxTotalPayable
	 * @param orderCode
	 * @param startTime
	 * @param endTime
	 * @param invoiceCode
	 * @return
	 */
	private static String findInvoiceRecordItemsSql(Integer orderId, Integer type, String invoiceHead, 
			String invoiceContent, String name, String phone, String email, String companyName, 
			String companyCode, String companyAddress, String companyPhone, String bankName, 
			String bankAccount, Double minTotalPayable, Double maxTotalPayable, String orderCode, 
			String startTime, String endTime, String invoiceCode, String theSameOrderNum, Double moneyLessThan,Double moneyMoreThan, 
			Map<String, String> orderByMap) {
		
		String sql = "select a.invoiceCode, a.money, a.created_at, b.* from invoice_record as a" +
				" left join order_invoice as b on a.orderId = b.orderId" +
				" where a.id != 0";
		
		if (orderId != null) {
			sql += " and a.orderId = " + orderId;
		}
		if (type != null) {
			sql += " and b.type = " + type;
		}
		if (invoiceHead != null && !invoiceHead.equals("")) {
			sql += " and b.invoiceHead like '%" + invoiceHead + "%'";
		}
		if (invoiceContent != null && !invoiceContent.equals("")) {
			sql += " and b.invoiceContent like '%" + invoiceContent + "%'";
		}
		if (name != null && !name.equals("")) {
			sql += " and b.name like '%" + name + "%'";
		}
		if (phone != null && !phone.equals("")) {
			sql += " and b.phone like '%" + phone + "%'";
		}
		if (email != null && !email.equals("")) {
			sql += " and b.email like '%" + email + "%'";
		}
		if (companyName != null && !companyName.equals("")) {
			sql += " and b.companyName like '%" + companyName + "%'";
		}
		if (companyCode != null && !companyCode.equals("")) {
			sql += " and b.companyName like '%" + companyName + "%'";
		}
		if (companyAddress != null && !companyAddress.equals("")) {
			sql += " and b.companyAddress like '%" + companyAddress + "%'";
		}
		if (companyPhone != null && !companyPhone.equals("")) {
			sql += " and b.companyPhone like '%" + companyPhone + "%'";
		}
		if (bankName != null && !bankName.equals("")) {
			sql += " and b.bankName like '%" + bankName + "%'";
		}
		if (bankAccount != null && !bankAccount.equals("")) {
			sql += " and b.bankAccount like '%" + bankAccount + "%'";
		}
		if (minTotalPayable != null) {
			sql += " and b.totalPayable >= " + minTotalPayable;
		}
		if (maxTotalPayable != null) {
			sql += " and b.totalPayable <= " + maxTotalPayable;
		}
		if (moneyLessThan != null) {
			sql += " and a.money > " + moneyLessThan;
		}
		if (moneyMoreThan != null) {
			sql += " and a.money < " + moneyMoreThan;
		}
		if (orderCode != null && !orderCode.equals("")) {
			sql += " and b.orderCode like '%" + orderCode + "%'";
		}
		if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
		if (invoiceCode != null && !invoiceCode.equals("")) {
			sql += " and a.invoiceCode like '%" + invoiceCode + "%'";
		}
		if (theSameOrderNum != null && !theSameOrderNum.equals("")) {
			sql += " and a.theSameOrderNum like '%" + theSameOrderNum + "%'";
		}
		
		sql += BaseDao.getOrderSql(orderByMap);
		
		return sql;
	}
	
}
