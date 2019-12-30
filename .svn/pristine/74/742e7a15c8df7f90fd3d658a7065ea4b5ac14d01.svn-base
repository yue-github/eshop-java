package com.eshop.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class QueryHelper {
    private  String fromClause = "";
    private String whereClause = "";
    private String orderByClause = "";
    private String groupByClause = "";
    private List<Object> parameters = null;
    //排序顺序
    public static String ORDER_BY_DESC = "DESC";//降序
    public static String ORDER_BY_ASC = "ASC";//升序

    
    public QueryHelper() {
	}
    /**
     * 构造from字句
     * @param clazz 实体类
     * @param alias 实体类对应别名
     */
    public QueryHelper(String tableName , String alias){
        fromClause = "FROM " + tableName + " " + alias;
    }

    /**
     * 构造where字句
     * @param condition 查询条件语句，如：u.name=?
     * @param params 查询条件对应的参数，如："张三"
     */
    public void addCondition(String condition , Object params){
        if(whereClause.length() < 1){//一个查询条件时
            whereClause += " WHERE " + condition;
        }else {
            whereClause += " AND " + condition;
        }
        if(parameters == null){
            parameters = new ArrayList<>();
        }
        if(params != null) {
        	parameters.add(params);
        }
    }

    /**
     * 构造order by 字句
     * @param property order by 语句，如：u.age
     * @param order 排列顺序，如：DESC 或 ASC
     */
    public void addOrderByProperty(String property , String order){
        //当是第一个的时候
        if(orderByClause.length() < 1){
            orderByClause += " ORDER BY " + property + " " + order;
        }else{
            orderByClause += "," + property + " " + order;
        }
    }
    
    /**
     * 构造group by 字句
     * @param property order by 语句，如：u.age
     * @param order 排列顺序，如：DESC 或 ASC
     */
    public void addGroupByProperty(String property){
        //当是第一个的时候
        if(orderByClause.length() < 1){
            orderByClause += " GROUP BY " + property ;
        }else{
            orderByClause += "," + property;
        }
    }

    /**
     * Sql查询语句
     * @return Sql查询语句
     */
    public String getQuerySql(){
        return fromClause + whereClause + orderByClause;
    }
    

    /**
     * 查询语句需中?需要的条件值集合
     * @return 条件值集合
     */
    public List<Object> getParams(){
        if(parameters != null && parameters.size() > 0){
            return parameters;
        }else{
            return new ArrayList<>();//
        }
    }

    /**
     *查询统计数
     * @return 查询统计数的Sql语句
     */
    public String getCountSql(){
        return "SELECT COUNT(*) " + fromClause + whereClause;
    }
    /**
     *获取where子句
     * @return
     */
    public String getWhereClause() {
    	return whereClause;
    }
}
