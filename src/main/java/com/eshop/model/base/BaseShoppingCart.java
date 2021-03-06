package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseShoppingCart<M extends BaseShoppingCart<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setCustomerId(java.lang.Integer customerId) {
		set("customer_id", customerId);
	}

	public java.lang.Integer getCustomerId() {
		return get("customer_id");
	}

	public void setAmount(java.lang.Integer amount) {
		set("amount", amount);
	}

	public java.lang.Integer getAmount() {
		return get("amount");
	}

	public void setProductId(java.lang.Integer productId) {
		set("product_id", productId);
	}

	public java.lang.Integer getProductId() {
		return get("product_id");
	}

	public void setSelectProterties(java.lang.String selectProterties) {
		set("selectProterties", selectProterties);
	}

	public java.lang.String getSelectProterties() {
		return get("selectProterties");
	}

	public void setCreatedAt(java.util.Date createdAt) {
		set("created_at", createdAt);
	}

	public java.util.Date getCreatedAt() {
		return get("created_at");
	}

	public void setUpdatedAt(java.util.Date updatedAt) {
		set("updated_at", updatedAt);
	}

	public java.util.Date getUpdatedAt() {
		return get("updated_at");
	}

	public void setShopId(java.lang.Integer shopId) {
		set("shop_id", shopId);
	}

	public java.lang.Integer getShopId() {
		return get("shop_id");
	}

	public void setPriceId(java.lang.Integer priceId) {
		set("price_id", priceId);
	}

	public java.lang.Integer getPriceId() {
		return get("price_id");
	}

	public void setTypeAttr(java.lang.String typeAttr) {
		set("type_attr", typeAttr);
	}

	public java.lang.String getTypeAttr() {
		return get("type_attr");
	}

	public void setIsSelected(java.lang.Integer isSelected) {
		set("isSelected", isSelected);
	}

	public java.lang.Integer getIsSelected() {
		return get("isSelected");
	}

}
