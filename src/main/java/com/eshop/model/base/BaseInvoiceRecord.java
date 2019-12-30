package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseInvoiceRecord<M extends BaseInvoiceRecord<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setInvoiceCode(java.lang.String invoiceCode) {
		set("invoiceCode", invoiceCode);
	}

	public java.lang.String getInvoiceCode() {
		return get("invoiceCode");
	}

	public void setMoney(java.math.BigDecimal money) {
		set("money", money);
	}

	public java.math.BigDecimal getMoney() {
		return get("money");
	}

	public void setOrderId(java.lang.Integer orderId) {
		set("orderId", orderId);
	}

	public java.lang.Integer getOrderId() {
		return get("orderId");
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

	public void setTheSameOrderNum(java.lang.String theSameOrderNum) {
		set("theSameOrderNum", theSameOrderNum);
	}

	public java.lang.String getTheSameOrderNum() {
		return get("theSameOrderNum");
	}

	public void setOrderCode(java.lang.String orderCode) {
		set("order_code", orderCode);
	}

	public java.lang.String getOrderCode() {
		return get("order_code");
	}

	public void setTotalPayable(java.math.BigDecimal totalPayable) {
		set("totalPayable", totalPayable);
	}

	public java.math.BigDecimal getTotalPayable() {
		return get("totalPayable");
	}

}
