package com.eshop.service;

import java.util.LinkedHashMap;
import java.util.Map;
import com.eshop.helper.DateHelper;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


public class HomeTotalService {

	/**
	 * 获取月成交量，昨日成交量，今日成交量
	 * @return
	 */
	public static Record getMakeABargain() {
		//本月时间
		String firstDay = DateHelper.firstDay();
		String lastDay = DateHelper.lastDay();
		//今日时间
		String today = DateHelper.today();
		//昨日时间
		String yesterday = DateHelper.yesterday();
		//本月成交量sql
		StringBuffer monthSql = new StringBuffer(200);
		monthSql.append("SELECT SUM(a.totalPayable) as makeABargain FROM `order` AS a ");
		monthSql.append(" WHERE a.`status` NOT IN(1,6)");
		monthSql.append(" AND  DATE_FORMAT(a.`orderTime`,'%Y-%m-%d')>=?");
		monthSql.append(" AND  DATE_FORMAT(a.`orderTime`,'%Y-%m-%d')<=?");
	
		Record monthTotal = Db.findFirst(monthSql.toString(), firstDay, lastDay);
		Record todayTotal = Db.findFirst(getSql().toString(), today);
		Record yesterdayTotal = Db.findFirst(getSql().toString(), yesterday);
		Record record = new Record();
		record.set("monthTotal", monthTotal.getBigDecimal("makeABargain")==null?0:monthTotal.getBigDecimal("makeABargain"));
		record.set("todayTotal", todayTotal.getBigDecimal("makeABargain")==null?0:todayTotal.getBigDecimal("makeABargain"));
		record.set("yesterdayTotal", yesterdayTotal.getBigDecimal("makeABargain")==null?0:yesterdayTotal.getBigDecimal("makeABargain"));
		return record;
	}
	
	/**
	 * 最近7天成交量
	 * @return
	 */
	public static Record getWeekMakeABargain() {
		Map<String, String> days = getWeek();
		StringBuffer sql = new StringBuffer(200);
		sql.append("SELECT COUNT(*) as makeABargain FROM `order` ");
		sql.append(" WHERE `status` NOT IN(1,6)");
		sql.append(" AND  DATE_FORMAT(`orderTime`,'%Y-%m-%d')=?");
		Record record = new Record();
		for (Map.Entry<String, String> entry : days.entrySet()) {  
			Record re = Db.findFirst(sql.toString(), entry.getValue());
			record.set(entry.getKey(), re.getLong("makeABargain")==null?0:re.getLong("makeABargain"));
		}  
		Record target = new Record();
		target.set("days", days);
		target.set("daysTotal", record);
		return target;
	}
	
	/**
	 * 最近7天成交金额
	 * @return
	 */
	public static Record getWeekMakeAMoney() {
		Map<String, String> days = getWeek();
		StringBuffer sql = new StringBuffer(200);
		sql.append("SELECT SUM(totalPayable) as totalMoney FROM `order` ");
		sql.append(" WHERE `status` NOT IN(1,6)");
		sql.append(" AND  DATE_FORMAT(`orderTime`,'%Y-%m-%d')=?");
		Record record = new Record();
		for (Map.Entry<String, String> entry : days.entrySet()) {  
			Record re = Db.findFirst(sql.toString(), entry.getValue());
			record.set(entry.getKey(), re.getBigDecimal("totalMoney")==null?0:re.getBigDecimal("totalMoney"));
		}  
		Record target = new Record();
		target.set("days", days);
		target.set("daysTotal", record);
		return target;
	}
	
	private static String getSql() {
		//今日、昨日成交量sql
		StringBuffer sql = new StringBuffer(200);
		sql.append("SELECT SUM(a.totalPayable) as makeABargain FROM `order` AS a");
		sql.append(" WHERE a.`status` NOT IN(1,6)");
		sql.append(" AND  DATE_FORMAT(a.`orderTime`,'%Y-%m-%d')=?");
		return sql.toString();
	}
	
	private static Map<String, String> getWeek(){
		Map<String, String> date = new LinkedHashMap<>();
		//今日时间
		String today = DateHelper.today();
		
		//获取最近7天时间
		int index = 1;
		for(int i=6; i>0; i--) {
			String day = DateHelper.addDay(today, -i);
			date.put("day"+index, day);
			index ++;
		}
		date.put("day7", today);
	
		return date;
	}
}
