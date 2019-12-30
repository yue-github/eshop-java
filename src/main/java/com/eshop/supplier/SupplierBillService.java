package com.eshop.supplier;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eshop.helper.DateHelper;
import com.eshop.model.SupplierBill;
import com.eshop.model.SupplierBillItem;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class SupplierBillService {
	
	/**
	 * 查询供应商账单明细列表
	 * @param offset
	 * @param length
	 * @param supplierId
	 * @param supplierName
	 * @param supplierBillStatus
	 * @return
	 */
	public static List<Record> findSupplierBillItems(int offset, int length, Integer supplierId, 
			String supplierName, String status, String startSendOutTime, String endSendOutTime, 
			String code, String tradeType, String startCreatedAt, String endCreatedAt,
			String productName, String startAuditedAt, String endAuditedAt,
			String operator, Integer supplierBillId, String orderCode, Map<String, String> orderByMap) {
		
		String sql = findSupplierBillItemsSql(supplierId, supplierName, status, startSendOutTime, 
				endSendOutTime, code, tradeType, startCreatedAt, endCreatedAt, productName, 
				startAuditedAt, endAuditedAt, operator, supplierBillId, orderCode, orderByMap);
		
		sql = BaseDao.appendLimitSql(sql, offset, length);
		
		return Db.find(sql);
	}
	
	/**
	 * 查询供应商账单明细列表
	 * @param supplierId
	 * @param supplierName
	 * @param supplierBillStatus
	 * @param startTime
	 * @param endTime
	 * @param orderByMap
	 * @return
	 */
	public static List<Record> findSupplierBillItems(Integer supplierId, String supplierName, 
			String status, String startSendOutTime, String endSendOutTime, String code, 
			String tradeType, String startCreatedAt, String endCreatedAt, 
			String productName, String startAuditedAt, String endAuditedAt,
			String operator, Integer supplierBillId, String orderCode, Map<String, String> orderByMap) {
		
		String sql = findSupplierBillItemsSql(supplierId, supplierName, status, startSendOutTime, 
				endSendOutTime, code, tradeType, startCreatedAt, endCreatedAt, productName, 
				startAuditedAt, endAuditedAt, operator, supplierBillId, orderCode, orderByMap);
		
		return Db.find(sql);
	}
	
	public static List<Record> findSupplierBillItems(Integer supplierBillId) {
		
		return findSupplierBillItems(null, null, null, null, null, null, null, 
				null, null, null, null, null, null, supplierBillId, null, null);
	}
	
	/**
	 * 查询供应商账单明细列表的总条数
	 * @param supplierId
	 * @param supplierName
	 * @param supplierBillStatus
	 * @return
	 */
	public static int countSupplierBillItems(Integer supplierId, String supplierName, String status, 
			String startSendOutTime, String endSendOutTime, String code, String tradeType, 
			String startCreatedAt, String endCreatedAt, String productName, 
			String startAuditedAt, String endAuditedAt, String operator,
			Integer supplierBillId, String orderCode) {
		
		String sql = findSupplierBillItemsSql(supplierId, supplierName, status, startSendOutTime, 
				endSendOutTime, code, tradeType, startCreatedAt, endCreatedAt, productName, 
				startAuditedAt, endAuditedAt, operator, supplierBillId, orderCode, null);
		
		return Db.find(sql).size();
	}
	
	/**
	 * 组装sql语句
	 * @param offset
	 * @param length
	 * @param supplierId
	 * @param supplierName
	 * @param supplierBillStatus
	 * @return
	 */
	private static String findSupplierBillItemsSql(Integer supplierId, String supplierName, 
			String status, String startSendOutTime, String endSendOutTime, 
			String code, String tradeType, String startCreatedAt, String endCreatedAt,
			String productName, String startAuditedAt, String endAuditedAt, String operator,
			Integer supplierBillId, String orderCode, Map<String, String> orderByMap) {
		
		String sql = "select * from supplier_bill_item" +
				" where id != 0";
		
		if (supplierId != null) {
			sql += " and supplier_id = " + supplierId;
		}
		if (supplierName != null && !supplierName.equals("")) {
			sql += " and supplier_name like '%" + supplierName + "%'";
		}
		if (status != null && !status.equals("")) {
			sql += " and status = '" + status + "'";
		}
		if (startSendOutTime != null && !startSendOutTime.equals("")) {
			sql += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') >= '" + startSendOutTime + "'";
		}
		if (endSendOutTime != null && !endSendOutTime.equals("")) {
			sql += " and DATE_FORMAT(sendOutTime,'%Y-%m-%d') <= '" + endSendOutTime + "'";
		}
		if (code != null && !code.equals("")) {
			sql += " and code like '%" + code + "%'";
		}
		if (tradeType != null && !tradeType.equals("")) {
			sql += " and tradeType = '" + tradeType + "'";
		}
		if (startCreatedAt != null && !startCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(created_at,'%Y-%m-%d') >= '" + startCreatedAt + "'";
		}
		if (endCreatedAt != null && !endCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(created_at,'%Y-%m-%d') <= '" + endCreatedAt + "'";
		}
		if (productName != null && !productName.equals("")) {
			sql += " and product_name like '%" + productName + "%'";
		}
		if (startAuditedAt != null && !startAuditedAt.equals("")) {
			sql += " and DATE_FORMAT(audited_at,'%Y-%m-%d') >= '" + startAuditedAt + "'";
		}
		if (endAuditedAt != null && !endAuditedAt.equals("")) {
			sql += " and DATE_FORMAT(audited_at,'%Y-%m-%d') <= '" + endAuditedAt + "'";
		}
		if (operator != null && !operator.equals("")) {
			sql += " and operator like '%" + operator + "%'";
		}
		if (supplierBillId != null) {
			sql += " and supplier_bill_id = " + supplierBillId;
		}
		if (orderCode != null && !orderCode.equals("")) {
			sql += " and order_code like '%" + orderCode + "%'";
		}
		
		String orderBy = BaseDao.getOrderSql(orderByMap);
		sql += orderBy;
		
		return sql;
	}
	
	/**
	 * 统计
	 * @param list
	 * @return
	 */
	public static Record calculateSupplierBillItems(List<Record> list) {
		BigDecimal totalActualProductPrice = new BigDecimal(0);
		Record result = new Record();
		
		for (Record item : list) {
			totalActualProductPrice = totalActualProductPrice.add(item.getBigDecimal("totalActualProductPrice"));
		}
		
		result.set("totalActualProductPrice", totalActualProductPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
		return result;
	}
	
	/**
	 * 批量查询账单
	 * @param offset
	 * @param length
	 * @param supplierId
	 * @param supplierName
	 * @param billCode
	 * @param status
	 * @param auditOperator
	 * @param payOperator
	 * @param startCreatedAt
	 * @param endCreatedAt
	 * @param startPayedAt
	 * @param endPayedAt
	 * @return
	 */
	public static List<Record> findSupplierBill(int offset, int length, Integer supplierId, String supplierName,
			String billCode, String status, String auditOperator, String payOperator, String startCreatedAt,
			String endCreatedAt, String startPayedAt, String endPayedAt, Map<String, String> orderByMap) {
		
		String sql = findSupplierBillSql(supplierId, supplierName, billCode, status, auditOperator, payOperator, 
				startCreatedAt, endCreatedAt, startPayedAt, endPayedAt, orderByMap);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		list = appendSupplierBill(list);
		
		return list;
	}
	
	public static List<Record> findSupplierBill(Integer supplierId, String supplierName,
			String billCode, String status, String auditOperator, String payOperator, String startCreatedAt,
			String endCreatedAt, String startPayedAt, String endPayedAt, Map<String, String> orderByMap) {
		
		String sql = findSupplierBillSql(supplierId, supplierName, billCode, status, auditOperator, payOperator, 
				startCreatedAt, endCreatedAt, startPayedAt, endPayedAt, orderByMap);
		
		return Db.find(sql);
	}
	
	private static List<Record> appendSupplierBill(List<Record> list) {
		for (Record item : list) {
			List<Record> supplierBillItems = findSupplierBillItems(item.getInt("id"));
			item.set("supplierBillItems", supplierBillItems);
		}
		return list;
	}
	
	/**
	 * 批量查询账单的总条数
	 * @param supplierId
	 * @param supplierName
	 * @param billCode
	 * @param status
	 * @param auditOperator
	 * @param payOperator
	 * @param startCreatedAt
	 * @param endCreatedAt
	 * @param startPayedAt
	 * @param endPayedAt
	 * @return
	 */
	public static int countSupplierBill(Integer supplierId, String supplierName, String billCode, 
			String status, String auditOperator, String payOperator, String startCreatedAt,
			String endCreatedAt, String startPayedAt, String endPayedAt) {
		
		String sql = findSupplierBillSql(supplierId, supplierName, billCode, status, auditOperator, payOperator,
				startCreatedAt, endCreatedAt, startPayedAt, endPayedAt, null);
		return Db.find(sql).size();
	}
	
	/**
	 * 统计账单
	 * @param list
	 * @return
	 */
	public static Record calculateSupplierBill(List<Record> list) {
		BigDecimal totalPayable = new BigDecimal(0);
		
		for (Record item : list) {
			totalPayable = totalPayable.add(item.getBigDecimal("totalPayable"));
		}
		
		Record result = new Record();
		result.set("totalPayable", totalPayable);
		return result;
	}
	
	/**
	 * 组装sql语句
	 * @param supplierId
	 * @param supplierName
	 * @param billCode
	 * @param status
	 * @param auditOperator
	 * @param payOperator
	 * @param startCreatedAt
	 * @param endCreatedAt
	 * @param startPayedAt
	 * @param endPayedAt
	 * @return
	 */
	private static String findSupplierBillSql(Integer supplierId, String supplierName, String billCode, 
			String status, String auditOperator, String payOperator, String startCreatedAt,
			String endCreatedAt, String startPayedAt, String endPayedAt, Map<String, String> orderByMap) {
		
		String sql = "select * from supplier_bill" +
				" where id != 0";
		
		if (supplierId != null) {
			sql += " and supplier_id = " + supplierId;
		}
		if (supplierName != null && !supplierName.equals("")) {
			sql += " and supplier_name like '%" + supplierName + "%'";
		}
		if (billCode != null && !billCode.equals("")) {
			sql += " and bill_code like '%" + billCode + "%'";
		}
		if (status != null && !status.equals("")) {
			sql += " and status = '" + status + "'";
		}
		if (auditOperator != null && !auditOperator.equals("")) {
			sql += " and audit_operator like '%" + auditOperator + "%'";
		}
		if (payOperator != null && !payOperator.equals("")) {
			sql += " and pay_operator like '%" + payOperator + "%'";
		}
		if (startCreatedAt != null && !startCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(created_at,'%Y-%m-%d') >= '" + startCreatedAt + "'";
		}
		if (endCreatedAt != null && !endCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(created_at,'%Y-%m-%d') <= '" + endCreatedAt + "'";
		}
		if (startPayedAt != null && !startPayedAt.equals("")) {
			sql += " and DATE_FORMAT(payed_at,'%Y-%m-%d') >= '" + startPayedAt + "'";
		}
		if (endPayedAt != null && !endPayedAt.equals("")) {
			sql += " and DATE_FORMAT(payed_at,'%Y-%m-%d') <= '" + endPayedAt + "'";
		}
		
		sql += BaseDao.getOrderSql(orderByMap);
		
		return sql;
	}
	
	/**
	 * 生成供应商账单
	 * 算法：1、生成供应商账单记录；2、修改账单明细的账单编号字段
	 * @param ids
	 * @param status
	 * @return
	 */
	public static ServiceCode generateSupplierBill(final List<Integer> ids, final String operator) {
		if (ids.size() <= 0) {
			return ServiceCode.Failed;
		}
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					SupplierBillItem billItem = SupplierBillItem.dao.findById(ids.get(0));
					int supplierId = billItem.getSupplierId();
					String supplierName = billItem.getSupplierName();
					String billCode = generateBillCode(supplierId);
					BigDecimal totalPayable = calculateTotalPayable(ids);
					
					SupplierBill model = new SupplierBill();
					model.setSupplierId(supplierId);
					model.setSupplierName(supplierName);
					model.setBillCode(billCode);
					model.setTotalPayable(totalPayable);
					model.setCreatedAt(new Date());
					model.setAuditOperator(operator);
					model.setStatus("unpay");
					model.save();
					
					String whereIn = BaseDao.getWhereIn(ids);
					String sql = "update supplier_bill_item " + 
							" set status = 'applying'" + "," + 
							" supplier_bill_id = " + model.getId() + "," +
							" operator = '" + operator + "'" +
							" where id in " + whereIn;
					Db.update(sql);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 供应商账单打款
	 * @param id
	 * @return
	 */
	public static ServiceCode paySupplierBill(final int id, final String operator) {
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					SupplierBill bill = SupplierBill.dao.findById(id);
					bill.setStatus("payed");
					bill.setPayedAt(new Date());
					bill.setPayOperator(operator);
					bill.update();
					
					Db.update("update supplier_bill_item set status = ? where supplier_bill_id = ?", "applied", id);
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 查看账单详情
	 * @param id
	 * @return
	 */
	public static SupplierBill getSupplierBill(int id) {
		return SupplierBill.dao.findById(id);
	}
	
	private static BigDecimal calculateTotalPayable(List<Integer> ids) {
		String whereIn = BaseDao.getWhereIn(ids);
		List<Record> list = Db.find("select * from supplier_bill_item where id in " + whereIn);
		Record statistics = calculateSupplierBillItems(list);
		return statistics.getBigDecimal("totalActualProductPrice");
	}
	
	/**
	 * 生成账单编号
	 * 生成规则：供应商id+年月日+流水号
	 * @param supplierId
	 * @return
	 */
	private static String generateBillCode(int supplierId) {
		String date = DateHelper.formatDate(new Date(), "yyyyMMdd");
		int count = SupplierBill.dao.find("select * from supplier_bill").size() + 1;
		return supplierId + date + count;
	}
	
}
