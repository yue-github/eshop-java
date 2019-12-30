package com.eshop.groupcard;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.eshop.model.GroupActivities;
import com.eshop.model.GroupActivityCards;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购卡记录
 *
 */
public class GroupActivityCardService {
	
	private final int SUCCESS = 0;
	private final int HAS_CARD = -1;
	private final int IS_NOT_INTEGER = -2;

	private String getCardCode() {
		int length = 6;
		int size = Db.find("select * from group_activity_cards").size() + 1;
		String str_size = String.valueOf(size);
		// 补零
		String zero = "";
		for (int i = 0; i < (length - str_size.length()); i++) {
			zero += "0";
		}
		StringBuilder builder = new StringBuilder(zero + str_size);
		// 往流水号中间插入随机数
		int count = builder.toString().length();
		Random rd = new Random();
		int offset = 1;
		for (int i = 1; i < count; i++) {
			builder = builder.insert(offset, rd.nextInt(10));
			offset += 2;
		}
		return "ty" + builder.toString();
	}
	
	private String generatePassword() {
		int length = 6;
		String password = "";
		Random rd = new Random();
		
		for (int i = 0; i < length; i++) {
			password += String.valueOf(rd.nextInt(10));
		}
		
		return password;
	}
	
	/**
	 * 生成团购卡
	 * @param group_activity_id
	 * @param total_amount
	 * @param total_money
	 * @return 0成功，-1已生成团购卡不能重新生成，-2总金额和金额不能整除，不允许生成
	 */
	public int generateCards(int group_activity_id) {
		int count = Db.find("select * from group_activity_cards where group_activity_id = ?", group_activity_id).size();
		if (count > 0) {
			return this.HAS_CARD;
		}
		
		GroupActivities activity = GroupActivities.dao.findById(group_activity_id);
		BigDecimal total_money = activity.getTotalMoney();
		BigDecimal discount = activity.getDiscount();
		
		BigDecimal remain = total_money.remainder(discount);
		if (remain.compareTo(new BigDecimal(0)) != 0) {
			return this.IS_NOT_INTEGER;
		}
		
		int totalAmount = total_money.divide(discount).intValue();
		for (int i = 0; i < totalAmount; i++) {
			GroupActivityCards model = new GroupActivityCards();
			model.setCode(getCardCode());
			model.setPassword(generatePassword());
			model.setDiscount(discount);
			model.setIsUsed("否");
			model.setGroupActivityId(group_activity_id);
			model.setCreatedAt(new Date());
			model.setUpdatedAt(new Date());
			model.save();
		}
		
		return this.SUCCESS;
	}
	
	/**
	 * 团购卡列表
	 * @param offset
	 * @param length
	 * @param code
	 * @param isUsed
	 * @param groupActivityId
	 * @return
	 */
	public List<Record> list(int offset, int length, String code, String isUsed, Integer groupActivityId) {
		String sql = sql(code, isUsed, groupActivityId);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		return list;
	}
	
	public List<Record> all(String code, String isUsed, Integer groupActivityId) {
		String sql = sql(code, isUsed, groupActivityId);
		List<Record> list = Db.find(sql);
		return list;
	}
	
	public int count(String code, String isUsed, Integer groupActivityId) {
		String sql = sql(code, isUsed, groupActivityId);
		return Db.find(sql).size();
	}
	
	private String sql(String code, String isUsed, Integer groupActivityId) {
		String sql = "select a.*, case when b.nickName is null then b.mobilePhone else b.nickName end as member_name" +
				" from group_activity_cards as a" +
				" left join customer as b on a.member_id = b.id" +
				" where a.id != 0";
		if (code != null && !code.equals("")) {
			sql += " and a.code like '%" + code + "%'";
		}
		if (isUsed != null && !isUsed.equals("")) {
			sql += " and isUsed = '" + isUsed + "'";
		}
		if (groupActivityId != null) {
			sql += " and group_activity_id = " + groupActivityId;
		}
		return sql;
	}
	
}
