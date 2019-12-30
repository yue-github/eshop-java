package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCouponProduct<M extends BaseCouponProduct<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
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

	public void setCouponId(java.lang.Integer couponId) {
		set("couponId", couponId);
	}

	public java.lang.Integer getCouponId() {
		return get("couponId");
	}

	public void setType(java.lang.Integer type) {
		set("type", type);
	}

	public java.lang.Integer getType() {
		return get("type");
	}

	public void setObjectId(java.lang.Integer objectId) {
		set("objectId", objectId);
	}

	public java.lang.Integer getObjectId() {
		return get("objectId");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setMainPic(java.lang.Integer mainPic) {
		set("mainPic", mainPic);
	}

	public java.lang.Integer getMainPic() {
		return get("mainPic");
	}

}
