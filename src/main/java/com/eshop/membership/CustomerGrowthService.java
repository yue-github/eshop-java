package com.eshop.membership;

import java.sql.SQLException;
import java.util.*;

import com.eshop.helper.DateHelper;
import com.eshop.log.Log;
import com.eshop.model.CustomerGrowth;
import com.eshop.model.CustomerPointRule;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 会员成长值类
 */
public class CustomerGrowthService extends MemberShip {

    /**
     * 批量查询成长值
     * @param offset
     * @param count
     * @param customerId
     * @param amount
     * @param type
     * @param source
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<Record> findCustomerGrowthItems(int offset, int count, Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
    	String sql = findCustomerGrowthItemsSql(customerId, amount, type, source, startTime, endTime, name);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        return Db.find(sql);
    }

    /**
     * 批量查询成长值的总数量
     * @param customerId
     * @param amount
     * @param type
     * @param source
     * @param startTime
     * @param endTime
     * @return
     */
    public static int countCustomerGrowthItems(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
    	String sql = findCustomerGrowthItemsSql(customerId, amount, type, source, startTime, endTime, name);
    	return Db.find(sql).size();
    }
    
    public static String findCustomerGrowthItemsSql(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime) {
    	return findCustomerGrowthItemsSql(customerId, amount, type, source, startTime, endTime, null);
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param amount
     * @param type
     * @param source
     * @param startTime
     * @param endTime
     * @return
     */
    public static String findCustomerGrowthItemsSql(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
    	String sql = "select a.*, b.name as customerName from customer_growth as a" +
    			" left join customer as b on a.customer_id = b.id" + 
    			" where a.id != 0";
        if (customerId != null) {
			sql += " and customer_id = " + customerId;
		}
        if (amount != null) {
			sql += " and amount = " + amount;
		}
        if (type != null) {
			sql += " and type = " + type;
		}
        if (source != null) {
			sql += " and source = " + source;
		}
        if (name != null) {
			sql += " and b.name like '%" + name + "%'";
		}
        if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
        if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(updated_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
        sql += " order by created_at desc";
        return sql;
    }

    /**
     * 签到
     * @param customerId
     * @return 0成功，1今天已签到，2失败
     */
    public static int signIn(final int customerId) {
    	String today = DateHelper.today();
		String yesterday = DateHelper.yesterday();
		CustomerGrowth ys = CustomerGrowth.dao.findFirst("select * from customer_growth where customer_id=? and DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", customerId, yesterday, SIGN_IN_GROWTH);
		CustomerGrowth cr = CustomerGrowth.dao.findFirst("select * from customer_growth where customer_id=? and DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", customerId, today, SIGN_IN_GROWTH);
		
		if (cr != null) {
			return 1;
		}
		
		final int DAYS = 10;
		final int cyclenum = (ys == null) ? 1 : (ys.getCycleNum() % DAYS + 1);
		
		CustomerPointRule rule = CustomerPointRule.dao.findFirst("select * from customer_point_rule where code = ? and original = ?", SIGN_IN_GROWTH, cyclenum);
		if (rule == null) {
			return 2;
		}
		
		final int amount = rule.getTarget().intValue();
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("insert into customer_growth(customer_id,amount,type,note,created_at,updated_at,source,cycleNum) values(?,?,?,?,?,?,?,?)", customerId, amount, 1, "签到", new Date(), new Date(), SIGN_IN_GROWTH, cyclenum);
					changeCustomerGrowth(customerId, amount);
					changeCustomerGrade(customerId);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",签到失败");
					return false;
				}
				return true;
			}
		});
		
		return success ? 0 : 2;
    }
    
    /**
     * +-成长值
     * @param customerId
     * @param type 	1:获取, 2:扣除
     * @param ruleCode 会员体系基类MemberShip
     * @param note 行为备注
     * @return 0成功，1今天获得限制，2失败
     */
    public static int updateGrowth(final int customerId, final int type, final int ruleCode, final String note) {
    	
    	String today = DateHelper.today();
    	CustomerGrowth todayGrowth = CustomerGrowth.dao.findFirst("SELECT SUM(amount) AS todayAmount FROM customer_growth WHERE DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", today, ruleCode);
    	Integer todayAmount = todayGrowth.getBigDecimal("todayAmount") == null ? 0 : todayGrowth.getBigDecimal("todayAmount").intValue();
    	if (ruleCode == MemberShip.CLICK_ADV_GROWTH) {
			if (todayAmount == 20) {
				return 1;
			}
		} else if (ruleCode == MemberShip.SHARE_LINK_GROWTH) {
			if (todayAmount == 20) {
				return 1;
			}
		}
    	
    	String yesterday = DateHelper.yesterday();
		CustomerGrowth ys = CustomerGrowth.dao.findFirst("select * from customer_growth where customer_id=? and DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", customerId, yesterday, ruleCode);
		
		final int DAYS = 10;
		final int cyclenum = (ys == null) ? 1 : (ys.getCycleNum() % DAYS + 1);
		
		CustomerPointRule rule = CustomerPointRule.dao.findFirst("select * from customer_point_rule where code = ? and original = ?", ruleCode, cyclenum);
		
		int target = rule.getTarget().intValue();
		if (type == 2) {
			target = -target;
		}
		
		final int amount = target;
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("insert into customer_growth(customer_id,amount,type,note,created_at,updated_at,source,cycleNum) values(?,?,?,?,?,?,?,?)", customerId, amount, type, note, new Date(), new Date(), ruleCode, cyclenum);
					changeCustomerGrowth(customerId, amount);
				} catch (Exception e) {
					Log.error(e.getMessage() + "改变值失败");
					return false;
				}
				return true;
			}
		});
		
		return success ? 0 : 2;
    }
    
    /**
     * 创建成长值明细
     * @param model
     * @retrun
     */
    /*public static ServiceCode createCoustomerGrowth(CustomerGrowth model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	model.set("created_at", new Date());
    	model.set("updated_at", new Date());
    	if(!model.save()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }*/

}