package com.eshop.groupcard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eshop.model.GroupActivities;
import com.eshop.model.GroupSetMeals;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购套餐
 * @author lenovo
 *
 */
public class GroupSetMealService {
	
	private final int CARD_CODE_ERROR = -1;		// 卡号不正确
	private final int MONEY_ERROR = -2;			// 订单金额大于购物卡金额
	private final int SET_MEAL_NOT_FOUND = -3;	// 套餐不存在
	private final int CARD_IS_USED = -4;		// 卡号已被使用
	private final int PRODUCT_IS_NULL = -5;		// 产品为空
	private final int IS_NOT_START = -6;		// 活动还没开始
	private final int IS_ENDED = -7;			// 活动已结束
	private final int FAIL = 1;					// 成功
	private final int SUCCESS = 0;				// 失败

	/**
	 * 创建
	 * @param groupActivityId
	 * @param title
	 * @return
	 */
	public ServiceCode create(GroupSetMeals model) {
		GroupActivityService groupActivityService = new GroupActivityService();
		GroupActivities activity = groupActivityService.get(model.getGroupActivityId());
		model.setDiscount(activity.getDiscount());
		if (model.save()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 修改
	 * @param id
	 * @param groupActivityId
	 * @param title
	 * @return
	 */
	public ServiceCode update(GroupSetMeals model) {
		if (model.update()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public ServiceCode delete(int id) {
		String sql = "delete group_set_meals, group_set_meal_products" + 
				" from group_set_meals" + 
				" left join group_set_meal_products on group_set_meals.id = group_set_meal_products.group_set_meal_id" + 
				" where group_set_meals.id = " + id;
		Db.update(sql);
		return ServiceCode.Success;
	}
	
	/**
	 * 查询
	 * @param offset
	 * @param length
	 * @param groupActivityId
	 * @param title
	 * @return
	 */
	public List<Record> list(int offset, int length, Integer groupActivityId, String title, String isSale) {
		String sql = sql(groupActivityId, title, isSale);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		return Db.find(sql);
	}
	
	public List<Record> all(Integer groupActivityId, String title, String isSale) {
		String sql = sql(groupActivityId, title, isSale);
		List<Record> list = Db.find(sql);
		list = setItem(groupActivityId, list);
		return list;
	}
	
	private List<Record> setItem(int groupActivityId, List<Record> list) {
		GroupActivities model = GroupActivities.dao.findById(groupActivityId);
		
		Date now = new Date();
		int expire_status = 0;
		
		if (now.compareTo(model.getStartTime()) < 0) {
			expire_status = -1;
		} else if (now.compareTo(model.getEndTime()) > 0) {
			expire_status = 1;
		}
		
		for (Record item : list) {
			item.set("expire_status", expire_status);
		}
		
		return list;
	}
	
	public int count(Integer groupActivityId, String title, String isSale) {
		String sql = sql(groupActivityId, title, isSale);
		return Db.find(sql).size();
	}
	
	private String sql(Integer groupActivityId, String title, String isSale) {
		String sql = "select * from group_set_meals" +
				" where id != 0";
		if (groupActivityId != null) {
			sql += " and group_activity_id = " + groupActivityId;
		}
		if (title != null && !title.equals("")) {
			sql += " and title like '%" + title + "%'";
		}
		if (isSale != null && !isSale.equals("")) {
			sql += " and is_sale = '" + isSale + "'";
		}
		return sql;
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public GroupSetMeals get(int id) {
		return GroupSetMeals.dao.findById(id);
	}
	
	public List<Record> orderList(int groupSetMealId, int addressId) {
		String sql = "select a.amount, a.amount * b.suggestedRetailUnitPrice as totalPrice," + 
				" b.*, b.name as productName, b.id as productId, b.logistics_template_id as template_id," + 
				" 0 as price_id, '' as selectProterties, b.id as product_id, b.suggestedRetailUnitPrice as price" +
				" from group_set_meal_products as a" +
				" left join product as b on a.product_id = b.id" +
				" where a.group_set_meal_id = " + groupSetMealId;
		List<Record> products = Db.find(sql);
		
		List<Record> resources = Db.find("select * from resource");
		for (Record item : products) {
			Record resource = BaseDao.findItem(item.getInt("mainPic"), resources, "id");
        	String mainPic = resource.getStr("path");
        	item.set("mainPic", mainPic);
		}
		
		List<Record> deliveryOrders = Member.splitOrder(products, addressId);
    	return deliveryOrders;
	}
	
	public List<Record> productList(int groupSetMealId) {
		String sql = "select a.amount, a.amount * b.suggestedRetailUnitPrice as totalPrice," + 
				" b.*, b.name as productName, b.id as productId, b.logistics_template_id as template_id," + 
				" 0 as price_id, '' as selectProterties, b.id as product_id" +
				" from group_set_meal_products as a" +
				" left join product as b on a.product_id = b.id" +
				" where a.group_set_meal_id = " + groupSetMealId;
		List<Record> products = Db.find(sql);
		for (Record item : products) {
			item.set("totalPrice", item.getBigDecimal("totalPrice").doubleValue());
		}
    	return products;
	}
	
	public int submitOrder(int groupSetMealId, int customerId, String cardCode, String cardPassword, Record other) {
		int count = Db.find("select * from group_set_meal_products where group_set_meal_id = ?", groupSetMealId).size();
		if (count <= 0) {
			return PRODUCT_IS_NULL;
		}
		
		GroupSetMeals model = GroupSetMeals.dao.findById(groupSetMealId);
		if (model == null) {
			return SET_MEAL_NOT_FOUND;
		}
		
		GroupActivities activity = GroupActivities.dao.findById(model.getGroupActivityId());
		Date now = new Date();
		if (now.compareTo(activity.getStartTime()) < 0) {
			return IS_NOT_START;
		}
		if (now.compareTo(activity.getEndTime()) > 0) {
			return IS_ENDED;
		}
		
		count = Db.find("select * from group_activity_cards where code = ? and is_used = ?", cardCode, "是").size();
		if (count > 0) {
			return CARD_IS_USED;
		}
		
		count = Db.find("select * from group_activity_cards where code = ? and password = ? and group_activity_id = ?", cardCode, cardPassword, model.getGroupActivityId()).size();
		if (count <= 0) {
			return CARD_CODE_ERROR;
		}
		
		
		List<Record> products = productList(groupSetMealId);
		double totalProdPrice = Member.calculateProductTotalPayable(products);
		double discount = model.getDiscount().doubleValue();
		if (totalProdPrice > discount) {
			return MONEY_ERROR;
		}
		
		// 提交订单
		Member member = new Member();
		String result = member.submitOrder(customerId, products, other);
		if (result == null) {
			return FAIL;
		}
		
		// 修改订单状态为已支付
		Db.update("update `order` set status = 2, payTime = ? where theSameOrderNum = ?", new Date(), result);
		
		// 该卡号变为已使用状态
		Db.update("update group_activity_cards set is_used = ?, use_time = ?, member_id = ? where code = ?", "是", new Date(), customerId, cardCode);
	
		return SUCCESS;
	}
	
	public Record detail(int id) {
		String sql = "select b.mainPic from group_set_meal_products as a" + 
				" left join product as b on a.product_id = b.id" +
				" where a.group_set_meal_id = ?";
		List<Record> setMealProducts = Db.find(sql, id);
		
		List<String> pics = new ArrayList<String>();
		List<Record> resources = Db.find("select * from resource");
		
		for (Record item : setMealProducts) {
			Record res = BaseDao.findItem(item.getInt("mainPic"), resources, "id");
			String path = (res != null) ? res.getStr("path") : "";
			pics.add(path);
		}
		
		String mainPic = (pics.size() > 0) ? pics.get(0) : "";
		
		Record model = Db.findById("group_set_meals", id);
		GroupActivities activity = GroupActivities.dao.findById(model.getInt("group_activity_id"));
		
		Date now = new Date();
		int expire_status = 0;
		if (now.compareTo(activity.getStartTime()) < 0) {
			expire_status = -1;
		} else if (now.compareTo(activity.getEndTime()) > 0) {
			expire_status = 1;
		}
		
		model.set("expire_status", expire_status);
		model.set("pics", pics);
		model.set("mainPic", mainPic);
		
		return model;
	}
	
}
