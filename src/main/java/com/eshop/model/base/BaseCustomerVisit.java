package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCustomerVisit<M extends BaseCustomerVisit<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setIp(java.lang.String ip) {
		set("ip", ip);
	}

	public java.lang.String getIp() {
		return get("ip");
	}

	public void setCustomerId(java.lang.Integer customerId) {
		set("customer_id", customerId);
	}

	public java.lang.Integer getCustomerId() {
		return get("customer_id");
	}

	public void setCustomerName(java.lang.String customerName) {
		set("customer_name", customerName);
	}

	public java.lang.String getCustomerName() {
		return get("customer_name");
	}

	public void setPage(java.lang.String page) {
		set("page", page);
	}

	public java.lang.String getPage() {
		return get("page");
	}

	public void setCreateAt(java.util.Date createAt) {
		set("create_at", createAt);
	}

	public java.util.Date getCreateAt() {
		return get("create_at");
	}

	public void setSource(java.lang.Integer source) {
		set("source", source);
	}

	public java.lang.Integer getSource() {
		return get("source");
	}

}
