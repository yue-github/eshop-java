package com.eshop.tax;

import java.sql.SQLException;
import java.util.List;

import com.eshop.log.Log;
import com.eshop.model.PayRate;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class PayTax {

	/**
     * 获取某一条
     * @param id
     * @return
     */
    public static PayRate get(int id) {
    	return PayRate.dao.findById(id);
    }
    
    /**
     * 批量查询支付手续费记录
     * @param offset
     * @param count
     * @param payType
     * @param enable
     * @return
     */
    public static List<Record> findPayRateItems(int offset, int count, Integer payType, Integer enable) {
    	String sql = findPayRateItemsSql(payType, enable);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询支付手续费记录的总数量
     * @param payType
     * @param enable
     * @return
     */
    public static int countFindPayRateItems(Integer payType, Integer enable) {
    	String sql = findPayRateItemsSql(payType, enable);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param payType
     * @param enable
     * @return
     */
    private static String findPayRateItemsSql(Integer payType, Integer enable) {
    	String sql = "select * from pay_rate where id != 0";
    	if (payType != null)
    		sql += " and payType = " + payType;
    	if (enable != null)
    		sql += " and enable = " + enable;
    	return sql;
    }
    
    /**
     * 创建
     * @param model
     * @return
     */
    public static ServiceCode create(PayRate model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	if (!model.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 修改
     * @param model
     * @return
     */
    public static ServiceCode update(PayRate model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	if (!model.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 删除
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
    	if (PayRate.dao.deleteById(id)) 
    		return ServiceCode.Success;
    	else
    		return ServiceCode.Failed;
    }
    
    
    /**
     * 批量删除
     * @param ids
     * @return
     */
    public static ServiceCode batchDelete(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						PayRate.dao.deleteById(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",批量删除导航菜单失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
	
}
