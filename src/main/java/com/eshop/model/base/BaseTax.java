package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTax<M extends BaseTax<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setRate(java.math.BigDecimal rate) {
		set("rate", rate);
	}

	public java.math.BigDecimal getRate() {
		return get("rate");
	}

	public void setEnable(java.lang.Integer enable) {
		set("enable", enable);
	}

	public java.lang.Integer getEnable() {
		return get("enable");
	}

}
