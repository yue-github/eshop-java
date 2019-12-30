package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProductPickupAddress<M extends BaseProductPickupAddress<M>> extends Model<M> implements IBean {

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

	public void setPickupAddressId(java.lang.Integer pickupAddressId) {
		set("pickup_address_id", pickupAddressId);
	}

	public java.lang.Integer getPickupAddressId() {
		return get("pickup_address_id");
	}

}
