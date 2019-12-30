package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGroupProducts<M extends BaseGroupProducts<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setProductId(java.lang.Integer productId) {
		set("product_id", productId);
	}

	public java.lang.Integer getProductId() {
		return get("product_id");
	}

	public void setGroupActivityId(java.lang.Integer groupActivityId) {
		set("group_activity_id", groupActivityId);
	}

	public java.lang.Integer getGroupActivityId() {
		return get("group_activity_id");
	}

	public void setGroupSetMealId(java.lang.Integer groupSetMealId) {
		set("group_set_meal_id", groupSetMealId);
	}

	public java.lang.Integer getGroupSetMealId() {
		return get("group_set_meal_id");
	}

}