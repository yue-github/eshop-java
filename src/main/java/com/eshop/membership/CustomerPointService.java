package com.eshop.membership;

import java.sql.SQLException;
import java.util.*;

import com.eshop.helper.DateHelper;
import com.eshop.log.Log;
import com.eshop.model.CustomerPoint;
import com.eshop.model.CustomerPointRule;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 会员积分类
 */
public class CustomerPointService extends MemberShip {

    /**
     * 批量查询积分明细
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
    public static List<Record> findCustomerPointItems(int offset, int count, Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
        String sql = findCustomerPointItemsSql(customerId, amount, type, source, startTime, endTime, name);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        return Db.find(sql);
    }


	/**
     * 批量查询积分明细的总数量
     * @param customerId
     * @param amount
     * @param type
     * @param source
     * @param startTime
     * @param endTime
     * @return
     */
    public static int countCustomerPointItems(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
    	String sql = findCustomerPointItemsSql(customerId, amount, type, source, startTime, endTime, name);
    	return Db.find(sql).size();
    }
    
    /**
     * 查看某个会员 获取/扣除的总积分
     * @param customerId
     * @param type 1:获取, 2:扣除
     */
    public static Record countPoints(int customerId, int type) {
		String sql = "SELECT SUM(amount) AS pointAmount FROM customer_point WHERE id != 0 AND customer_id = " + customerId + " AND type = " + type;
		return Db.findFirst(sql);
	}
    
    public static Record countPoints(int customerId) {
		String sql = "SELECT SUM(amount) AS pointAmount FROM customer_point WHERE customer_id = " + customerId;
		return Db.findFirst(sql);
	}

	/**
	 *	查看积分详情
	 * @param customerId
	 * @return
	 */
	public static Record countToPoints(Integer customerId){
		String sql = "select * from customer where id= ?";
		return Db.findFirst(sql,customerId);
	}
    public static String findCustomerPointItemsSql(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime) {
    	return findCustomerPointItemsSql(customerId, amount, type, source, startTime, endTime, null);
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
    public static String findCustomerPointItemsSql(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
        String sql = "select a.*, b.name as customerName from customer_point as a" +
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
     * @param amount 
     * @param source 
     * @param relateId 
     * @param cycleNum 
     * @return 0成功，1今天已签到，2失败
     */
    public static int signIn(final int customerId) {
    	String today = DateHelper.today();
		String yesterday = DateHelper.yesterday();
		CustomerPoint ys = CustomerPoint.dao.findFirst("select * from customer_point where customer_id=? and DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", customerId, yesterday, SIGN_IN_POINT);
		CustomerPoint cr = CustomerPoint.dao.findFirst("select * from customer_point where customer_id=? and DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", customerId, today, SIGN_IN_POINT);
		
		if (cr != null) {
			return 1;
		}
		
		final int DAYS = 10;
		final int cyclenum = (ys == null) ? 1 : (ys.getCycleNum() % DAYS + 1);
		
		CustomerPointRule rule = CustomerPointRule.dao.findFirst("select * from customer_point_rule where code = ? and original = ?", SIGN_IN_POINT, cyclenum);
		if (rule == null) {
			return 2;
		}
		
		final int amount = rule.getTarget().intValue();
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("insert into customer_point(customer_id,amount,type,note,created_at,updated_at,source,cycleNum) values(?,?,?,?,?,?,?,?)", customerId, amount, 1, "签到", new Date(), new Date(), SIGN_IN_POINT, cyclenum);
					changeCustomerPoint(customerId, amount);
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
     * +-积分
     * @param customerId
     * @param type
     * @param ruleCode
     * @param note
     * @return 0成功，1今日积分限制， 2失败
     */
    public static int updatePoint(final int customerId, final int type, final int ruleCode, final String note) {
    	
    	String today = DateHelper.today();
    	CustomerPoint todayPoint = CustomerPoint.dao.findFirst("SELECT SUM(amount) AS todayAmount FROM customer_point WHERE DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", today, ruleCode);
    	Integer todayAmount = todayPoint.getBigDecimal("todayAmount") == null ? 0 : todayPoint.getBigDecimal("todayAmount").intValue();
    	if (ruleCode == MemberShip.CLICK_ADV_POINT) {
			if (todayAmount == 20) {
				return 1;
			}
		} else if (ruleCode == MemberShip.SHARE_LINK_POINT) {
			if (todayAmount == 20) {
				return 1;
			}
		}
    	
    	String yesterday = DateHelper.yesterday();
		CustomerPoint ys = CustomerPoint.dao.findFirst("select * from customer_point where customer_id=? and DATE_FORMAT(created_at,'%Y-%m-%d')=? and source=?", customerId, yesterday, ruleCode);
		
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
					Db.update("insert into customer_point(customer_id,amount,type,note,created_at,updated_at,source,cycleNum) values(?,?,?,?,?,?,?,?)", customerId, amount, type, note, new Date(), new Date(), ruleCode, cyclenum);
					changeCustomerPoint(customerId, amount);
				} catch (Exception e) {
					Log.error(e.getMessage() + "改变值失败");
					return false;
				}
				return true;
			}
		});
		
		return success ? 0 : 2;
    }

}