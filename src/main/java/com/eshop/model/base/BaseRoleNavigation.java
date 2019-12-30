package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseRoleNavigation<M extends BaseRoleNavigation<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return get("id");
	}

	public void setRoleId(java.lang.Long roleId) {
		set("role_id", roleId);
	}

	public java.lang.Long getRoleId() {
		return get("role_id");
	}

	public void setNavigationId(java.lang.Long navigationId) {
		set("navigation_id", navigationId);
	}

	public java.lang.Long getNavigationId() {
		return get("navigation_id");
	}

	public void setReadable(java.lang.Integer readable) {
		set("readable", readable);
	}

	public java.lang.Integer getReadable() {
		return get("readable");
	}

	public void setEditable(java.lang.Integer editable) {
		set("editable", editable);
	}

	public java.lang.Integer getEditable() {
		return get("editable");
	}

	public void setCreatable(java.lang.Integer creatable) {
		set("creatable", creatable);
	}

	public java.lang.Integer getCreatable() {
		return get("creatable");
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

}
