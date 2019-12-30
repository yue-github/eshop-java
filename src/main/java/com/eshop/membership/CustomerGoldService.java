package com.eshop.membership;

import java.sql.SQLException;
import java.util.*;

import com.eshop.log.Log;
import com.eshop.model.CustomerGold;
import com.eshop.model.CustomerPointRule;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import net.sf.ehcache.search.aggregator.Count;

/**
 * 会员金币类
 */
public class CustomerGoldService extends MemberShip {

    /**
     * 批量查询金币明细
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
    public static List<Record> findCustomerGoldItems(int offset, int count, Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
        String sql = findCustomerGoldItemsSql(customerId, amount, type, source, startTime, endTime, name);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        return Db.find(sql);
    }

    /**
     * 批量查询金币明细的总数量
     * @param customerId
     * @param amount
     * @param type
     * @param source
     * @param startTime
     * @param endTime
     * @return
     */
    public static int countCustomerGoldItems(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime, String name) {
        String sql = findCustomerGoldItemsSql(customerId, amount, type, source, startTime, endTime, name);
        return Db.find(sql).size();
    }
    
    /**
     * 查看某个会员 获取/扣除的总金币
     * @param customerId
     * @param type 1:获取, 2:扣除
     */
    public static Record countGolds (Integer customerId, Integer type) {
		String sql = "SELECT SUM(amount) AS goldAmount FROM customer_gold WHERE id != 0 AND customer_id = " + customerId;
		if(type != null) {
			sql +=" AND type = " + type;
		}
		return Db.findFirst(sql);
	}
    
    public static String findCustomerGoldItemsSql(Integer customerId, Integer amount, Integer type, Integer source, String startTime, String endTime){
    	return findCustomerGoldItemsSql(customerId, amount, type, source, startTime, endTime, null);
    }
    /**
     * 组装sql语句
     * @param customerId
     * @param amount
     * @param type
     * @param source
     * @param startTime
     * @param endTime
     * @param name
     * @return
     */
    public static String findCustomerGoldItemsSql(Integer customerId, Integer amount, Integer type, 
    		Integer source, String startTime, String endTime, String name) {
    	
        String sql = "select a.*, b.name as customerName from customer_gold as a" +
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
     * 积分兑换
     * @param gold
     * @return
     */
    public static double getGoldDiscount(int gold) {
		CustomerPointRule rule = CustomerPointRuleService.getByCode(GOLD_EXCHNAGE);
		
		if (rule == null) {
			return 0;
		}
		double original = rule.getOriginal().doubleValue();
		double target = rule.getTarget().doubleValue();
		double money = gold / original * target;
		
		return money;
	}
    
    /**
     * +-通币
     * @param customerId
     * @param type 1:获取, 2:扣除
     * @param ruleCode 会员体系基类MemberShip
     * @param note 行为备注
     * @return 0成功，2失败
     */
    public static int updateGold(final int customerId, final int type, final int ruleCode, final String note) {
		
		CustomerPointRule rule = CustomerPointRule.dao.findFirst("select * from customer_point_rule where code = ?", ruleCode);
		
		int target = rule.getTarget().intValue();
		
		if (type == 2) {
			target = -target;
		}
		
		final int amount = target;
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("insert into customer_gold(customer_id,amount,type,note,created_at,updated_at,source) values(?,?,?,?,?,?,?)", customerId, amount, type, note, new Date(), new Date(), ruleCode);
					changeCustomerGold(customerId, amount);
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
     * 创建金币明细
     * @param model
     * @retrun
     */
    public static ServiceCode createCoustomerGold(CustomerGold model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	model.set("created_at", new Date());
    	model.set("updated_at", new Date());
    	if(!model.save()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }

}