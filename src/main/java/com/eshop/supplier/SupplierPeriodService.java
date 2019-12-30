package com.eshop.supplier;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eshop.model.SupplierPeriod;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class SupplierPeriodService {

	/**
	 * 获取所有账期
	 * @return
	 */
	public static List<SupplierPeriod> all() {
		return SupplierPeriod.dao.find("select * from supplier_period order by type asc, days asc");
	}
	
	/**
	 * 创建账期
	 * @param model
	 * @return
	 */
	public static ServiceCode create(SupplierPeriod model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		if (model.save()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 修改账期
	 * @param model
	 * @return
	 */
	public static ServiceCode update(SupplierPeriod model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		if (model.update()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 删除账期
	 * @param id
	 * @return
	 */
	public static ServiceCode delete(int id) {
		if (SupplierPeriod.dao.deleteById(id)) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 批量删除账期
	 * @param model [1,2,...]
	 * @return
	 */
	public static ServiceCode batchDelete(String ids) {
		final JSONArray jsarr = JSON.parseArray(ids);
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (int i = 0; i < jsarr.size(); i++) {
						int id = jsarr.getIntValue(i);
						SupplierPeriod.dao.deleteById(id);
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 查看账期详情
	 * @param id
	 * @return
	 */
	public static SupplierPeriod get(int id) {
		return SupplierPeriod.dao.findById(id);
	}
	
	/**
	 * 批量查询账期
	 * @param offset
	 * @param length
	 * @param type
	 * @return
	 */
	public static List<Record> findSupplierPeriodItems(int offset, int length, String type, 
			Map<String, String> orderByMap) {
		
		return findSupplierPeriodItems(offset, length, type, null, null, null, null, orderByMap);
	}
	
	/**
	 * 批量查询账期
	 * @param offset
	 * @param length
	 * @param type
	 * @param minDays
	 * @param maxDays
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findSupplierPeriodItems(int offset, int length, String type, Integer minDays, 
			Integer maxDays, String startTime, String endTime, Map<String, String> orderByMap) {
		
		String sql = findSupplierPeriodSql(type, minDays, maxDays, startTime, endTime, orderByMap);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		return Db.find(sql);
	}
	
	/**
	 * 批量查询账期的总条数
	 * @param type
	 * @param minDays
	 * @param maxDays
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int countSupplierPeriodItems(String type) {
		return countSupplierPeriodItems(type, null, null, null, null);
	}
	
	/**
	 * 批量查询账期的总条数
	 * @param type
	 * @param minDays
	 * @param maxDays
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int countSupplierPeriodItems(String type, Integer minDays, Integer maxDays, 
			String startTime, String endTime) {
		
		String sql = findSupplierPeriodSql(type, minDays, maxDays, startTime, endTime, null);
		return Db.find(sql).size();
	}
	
	private static String findSupplierPeriodSql(String type, Integer minDays, Integer maxDays, 
			String startTime, String endTime, Map<String, String> orderByMap) {
		
		String sql = "select * from supplier_period where id != 0";
		
		if (type != null && !type.equals("")) {
			sql += " and type = '" + type + "'";
		}
		if (minDays != null) {
			sql += " and days >= " + minDays;
		}
		if (maxDays != null) {
			sql += " and days <= " + maxDays;
		}
		if (startTime != null && !startTime.equals("")) {
			sql += " and created_at >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sql += " and created_at <= '" + endTime + "'";
		}
		
		sql += BaseDao.getOrderSql(orderByMap);
		
		return sql;
	}
	
}
