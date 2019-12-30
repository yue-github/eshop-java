package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePromotionMiaosha<M extends BasePromotionMiaosha<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setPromotionId(java.lang.Integer promotionId) {
		set("promotion_id", promotionId);
	}

	public java.lang.Integer getPromotionId() {
		return get("promotion_id");
	}

	public void setLimitAmount(java.lang.Integer limitAmount) {
		set("limit_amount", limitAmount);
	}

	public java.lang.Integer getLimitAmount() {
		return get("limit_amount");
	}

}