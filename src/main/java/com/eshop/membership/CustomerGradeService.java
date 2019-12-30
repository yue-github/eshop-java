package com.eshop.membership;

import java.util.*;

import com.eshop.model.CustomerGrade;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 等级规则类
 */
public class CustomerGradeService {

    /**
     * 批量查询等级规则
     * @param offset
     * @param count
     * @param name
     * @param sort
     * @return
     */
    public static List<Record> findCustomerGradeItems(int offset, int count, String name, Integer sort) {
        String sql = findCustomerGradeItemsSql(name, sort);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        List<Record> list = Db.find(sql);
        list = appendCustomerGradeItems(list);
        
        return list;
    }
    
    private static List<Record> appendCustomerGradeItems(List<Record> list) {
    	// 判断会员等级是否允许删除
    	List<String> useGrades = getUseCustomerGrades();
    	for (Record item : list) {
    		String name = item.getStr("name");
    		if (useGrades.contains(name)) {
    			item.set("notDeletedable", true);
    		} else {
    			item.set("notDeletedable", false);
    		}
    	}
    	return list;
    }
    
    private static List<String> getUseCustomerGrades() {
    	String sql = "select * from customer_grade" +
    			" where name in (select grade from customer group by grade)" +
    			" order by start desc";
    	
    	Record grade = Db.findFirst(sql);
    	List<String> list = new ArrayList<String>();
    	
    	if (grade != null) {
    		List<CustomerGrade> models = CustomerGrade.dao.find("select * from customer_grade where start <= ?", grade.getInt("start"));
        	for (CustomerGrade item : models) {
        		list.add(item.getName());
        	}
    	}
    	
    	return list;
    }

    /**
     * 批量查询等级规则的总数量
     * @param name
     * @param sort
     * @return
     */
    public static int countCustomerGradeItems(String name, Integer sort) {
        String sql = findCustomerGradeItemsSql(name, sort);
        return Db.find(sql).size();
    }
    
    /**
     * 查询正在被客户使用的等级规则
     * @return
     */
    public static List<Record> findByApply() {
    	String sql = "SELECT g.* FROM customer_grade g"
    			+ " JOIN customer c"
    			+ " ON g.name = c.grade"
    			+ " GROUP BY g.id";
    	return Db.find(sql);
    }
    
    /**
     * 组装sql语句
     * @param name
     * @param sort
     * @return
     */
    public static String findCustomerGradeItemsSql(String name, Integer sort) {
        String sql = "select * from customer_grade where id != 0";
        if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
        if (sort != null) {
			sql += " and sort = " + sort;
		}
        sql += " order by start desc";
        return sql;
    }

    /**
     * 查看等级规则详情
     * @param id 
     * @return
     */
    public static CustomerGrade get(int id) {
        return CustomerGrade.dao.findById(id);
    }
    
    /**
     * 创建等级规则
     * @param model
     * @return ServiceCode
     */
    public static ServiceCode create(CustomerGrade model) {
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
     * 修改等级规则
     * @param model 
     * @return
     */
    public static ServiceCode update(CustomerGrade model) {
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
     * 删除一条等级规则
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
        if (!CustomerGrade.dao.deleteById(id)) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
    }
    
    /**
     * 检测修改后的字段的合法性
     * @param model
     * @return
     */
    public static Map<Integer, String> checkGrade(int oldStart, int oldEnd, int newStart, int newEnd) {
    	Map<Integer, String> map = new HashMap<Integer, String>();
    	
    	// 检测最小值
    	CustomerGrade lessOne = CustomerGrade.dao.findFirst("select * from customer_grade where start < ? order by end desc", oldStart);
    	if (lessOne != null) {
    		int end2 = lessOne.getEnd();
    		if (newStart <= end2) {
    			map.put(1, "最小值不能小于或等于" + end2);
    			return map;
    		}
    	}
    	
    	// 检测最大值
    	CustomerGrade moreOne = CustomerGrade.dao.findFirst("select * from customer_grade where start > ? order by start asc", oldStart);
    	if (moreOne != null) {
    		int start2 = moreOne.getStart();
    		if (newEnd >= start2) {
    			map.put(2, "最大值不能大于或等于" + start2);
    			return map;
    		}
    	}
    	
    	map.put(0, "成功");
    	return map;
    }

}