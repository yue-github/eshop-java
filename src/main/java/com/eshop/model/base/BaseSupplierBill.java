package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSupplierBill<M extends BaseSupplierBill<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setSupplierId(java.lang.Integer supplierId) {
		set("supplier_id", supplierId);
	}

	public java.lang.Integer getSupplierId() {
		return get("supplier_id");
	}

	public void setSupplierName(java.lang.String supplierName) {
		set("supplier_name", supplierName);
	}

	public java.lang.String getSupplierName() {
		return get("supplier_name");
	}

	public void setTotalPayable(java.math.BigDecimal totalPayable) {
		set("totalPayable", totalPayable);
	}

	public java.math.BigDecimal getTotalPayable() {
		return get("totalPayable");
	}

	public void setBillCode(java.lang.String billCode) {
		set("bill_code", billCode);
	}

	public java.lang.String getBillCode() {
		return get("bill_code");
	}

	public void setCreatedAt(java.util.Date createdAt) {
		set("created_at", createdAt);
	}

	public java.util.Date getCreatedAt() {
		return get("created_at");
	}

	public void setPayedAt(java.util.Date payedAt) {
		set("payed_at", payedAt);
	}

	public java.util.Date getPayedAt() {
		return get("payed_at");
	}

	public void setAuditOperator(java.lang.String auditOperator) {
		set("audit_operator", auditOperator);
	}

	public java.lang.String getAuditOperator() {
		return get("audit_operator");
	}

	public void setPayOperator(java.lang.String payOperator) {
		set("pay_operator", payOperator);
	}

	public java.lang.String getPayOperator() {
		return get("pay_operator");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}

	public java.lang.String getStatus() {
		return get("status");
	}

}
