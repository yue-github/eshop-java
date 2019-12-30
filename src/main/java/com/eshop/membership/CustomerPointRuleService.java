package com.eshop.membership;

import java.util.*;

import com.eshop.model.CustomerPointRule;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 积分规则类
 */
public class CustomerPointRuleService {

    /**
     * 批量查询积分规则
     * @param offset
     * @param count
     * @param code
     * @param note
     * @return
     */
    public static List<Record> fingPointRuleItems(int offset, int count, Integer code, String note) {
        String sql = findPointRuleItemsSql(code, note);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        return Db.find(sql);
    }

    /**
     * 批量查询积分规则的总数量
     * @param code
     * @param note
     * @return
     */
    public static int countPointRuleItems(Integer code, String note) {
        String sql = findPointRuleItemsSql(code, note);
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param code
     * @param note
     * @return
     */
    public static String findPointRuleItemsSql(Integer code, String note) {
        String sql = "select * from customer_point_rule where id != 0";
        if (code != null) {
			sql += " and code = " + code;
		}
        if (note != null && !note.equals("")) {
			sql += " and note like '%" + note + "%'";
		}
        sql += " order by created_at, id desc";
        return sql;
    }

    /**
     * 查看积分规则详情
     * @param id 
     * @return
     */
    public static CustomerPointRule get(int id) {
        return CustomerPointRule.dao.findById(id);
    }
    
    /**
     * 根据编码查看积分规则详情
     * @param code
     * @return
     */
    public static CustomerPointRule getByCode(int code) {
        return CustomerPointRule.dao.findFirst("select * from customer_point_rule where code = ?", code);
    }
    
    /**
     * 创建积分规则
     * @param model
     * @return
     */
    public static ServiceCode create(CustomerPointRule model) {
    	if (model == null) {
    		return ServiceCode.Failed;
		}
    	model.set("created_at", new Date());
    	model.set("updated_at", new Date());
    	if (!model.save()) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
    }

    /**
     * 修改积分规则
     * @param model 
     * @return
     */
    public static ServiceCode update(CustomerPointRule model) {
        if (model == null) {
        	return ServiceCode.Failed;
        }
        model.set("updated_at", new Date());
        if (!model.update()) {
        	return ServiceCode.Failed;
        } else {
        	return ServiceCode.Success;
        }
    }

    /**
     * 删除一条积分规则
     * @param id 
     * @return
     */
    public static ServiceCode delete(int id) {
        if (!CustomerPointRule.dao.deleteById(id)) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
    }

}