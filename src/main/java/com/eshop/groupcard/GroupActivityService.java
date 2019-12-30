package com.eshop.groupcard;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.eshop.log.Log;
import com.eshop.model.GroupActivities;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购卡活动
 * @author lenovo
 *
 */
public class GroupActivityService {
	
	private final int NOT_ALLOW_UPDATE = -1;
	private final int NOT_ALLOW_DELETE = -2;
	private final int IS_NOT_INTEGER = -3;
	private final int FAIL = 1;
	private final int SUCCESS = 0;
	
	private boolean isDivide(BigDecimal total_money, BigDecimal money) {
		BigDecimal remain = total_money.remainder(money);
		if (remain.compareTo(new BigDecimal(0)) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * 创建
	 * @param model
	 * @return
	 */
	public int create(GroupActivities model) {
		if (!this.isDivide(model.getTotalMoney(), model.getDiscount())) {
			return this.IS_NOT_INTEGER;
		}
		try {
			model.setCreatedAt(new Date());
			model.setUpdatedAt(new Date());
			return model.save() ? this.SUCCESS : this.FAIL;
		} catch (Exception e) {
			Log.error(e.getMessage());
			return this.FAIL;
		}
	}
	
	/**
	 * 修改
	 * @param model
	 * @return
	 */
	public int update(final GroupActivities model) {
		int count = Db.find("select * from group_activity_cards where group_activity_id = ? and is_used = ?", model.getId(), "是").size();
		if (count > 0) {
			return this.NOT_ALLOW_UPDATE;
		}
		if (!this.isDivide(model.getTotalMoney(), model.getDiscount())) {
			return this.IS_NOT_INTEGER;
		}
		boolean flag = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("update group_activity_cards set discount = ? where group_activity_id = ?", model.getDiscount(), model.getId());
					Db.update("update group_set_meals set discount = ? where group_activity_id = ?", model.getDiscount(), model.getId());
					model.update();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
		return flag ? this.SUCCESS : this.FAIL;
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int delete(final int id) {
		int count = Db.find("select * from group_activity_cards where group_activity_id = ? and is_used = ?", id, "是").size();
		if (count > 0) {
			return this.NOT_ALLOW_DELETE;
		}
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from product where prod_type = ? and relate_id = ?", 3, id);
					Db.update("delete from group_activity_cards where group_activity_id = ?", id);
					Db.update("delete from group_set_meal_products where group_activity_id = ?", id);
					Db.update("delete from group_set_meals where group_activity_id = ?", id);
					Db.update("delete from group_activities where id = ?", id);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
		return success ? this.SUCCESS : this.FAIL;
	}
	
	/**
	 * 查询
	 * @param offset
	 * @param length
	 * @return
	 */
	public List<Record> list(int offset, int length, String title) {
		String sql = sql(title);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		return list;
	}
	
	/**
	 * 记录总条数
	 * @param title
	 * @return
	 */
	public int count(String title) {
		String sql = sql(title);
		return Db.find(sql).size();
	}
	
	private String sql(String title) {
		String sql = "select *" +
				" from group_activities" +
				" where id != 0";
		if (title != null && !title.equals("")) {
			sql += " and title like '%" + title + "%'";
		}
		sql += " order by created_at desc";
		return sql;
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public GroupActivities get(int id) {
		return GroupActivities.dao.findById(id);
	}
	
}
