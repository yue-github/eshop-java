package com.eshop.invoice;

import java.util.List;

import com.eshop.model.PlainInvoice;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class PlainInvoiceService {
	
	public final static int TYPE_PERSONAL = 1;
	public final static int TYPE_COMPANY = 2;
	
	/**
	 * 创建普通发票
	 * @param model
	 * @return
	 */
	public static ServiceCode create(PlainInvoice model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		if (!model.save()) {
			return ServiceCode.Failed;
		}
		
		return ServiceCode.Success;
	}
	
	/**
	 * 修改普通发票
	 * @param model
	 * @return
	 */
	public static ServiceCode update(PlainInvoice model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		if (!model.update()) {
			return ServiceCode.Failed;
		}
		
		return ServiceCode.Success;
	}
	
	/**
	 * 查看普通发票详情
	 * @param id
	 * @return
	 */
	public static PlainInvoice get(int id) {
		return PlainInvoice.dao.findById(id);
	}
	
	/**
	 * 批量查询普通发票记录
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 * @return
	 */
	public static List<Record> findPlainInvoiceItems(Integer customerId, String invoiceHead, Integer type) {
		String sql = findPlainInvoiceItemsSql(customerId, invoiceHead, type);
		return Db.find(sql);
	}
	
	/**
	 * 批量查询普通发票记录
	 * @param offset
	 * @param count
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 * @return
	 */
	public static List<Record> findPlainInvoiceItems(int offset, int count, Integer customerId, String invoiceHead, Integer type) {
		String sql = findPlainInvoiceItemsSql(customerId, invoiceHead, type);
		sql = BaseDao.appendLimitSql(sql, offset, count);
		return Db.find(sql);
	}
	
	/**
	 * 批量查询普通发票的总数量
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 * @return
	 */
	public static int countPlainInvoiceItems(Integer customerId, String invoiceHead, Integer type) {
		String sql = findPlainInvoiceItemsSql(customerId, invoiceHead, type);
		return Db.find(sql).size();
	}
	
	/**
	 * 组装sql语句
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 * @return
	 */
	public static String findPlainInvoiceItemsSql(Integer customerId, String invoiceHead, Integer type) {
		String sql = "select * from plain_invoice where id != 0";
		if (customerId != null) {
			sql += " and customerId = " + customerId;
		}
		if (invoiceHead != null && !invoiceHead.equals("")) {
			sql += " and invoiceHead like '%" + invoiceHead + "%'";
		}
		if (type != null) {
			sql += " and type = " + type;
		}
		return sql;
	}
	
	/**
	 * 删除一条普通发票记录
	 * @param id
	 * @return
	 */
	public static ServiceCode delete(int id) {
		if (PlainInvoice.dao.deleteById(id)) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
}
