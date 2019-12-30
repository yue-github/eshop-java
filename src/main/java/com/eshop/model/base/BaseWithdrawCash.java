package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseWithdrawCash<M extends BaseWithdrawCash<M>> extends Model<M> implements IBean {

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

	public void setAccountType(java.lang.Integer accountType) {
		set("accountType", accountType);
	}

	public java.lang.Integer getAccountType() {
		return get("accountType");
	}

	public void setAplipayAccount(java.lang.String aplipayAccount) {
		set("aplipayAccount", aplipayAccount);
	}

	public java.lang.String getAplipayAccount() {
		return get("aplipayAccount");
	}

	public void setWeixinAccount(java.lang.String weixinAccount) {
		set("weixinAccount", weixinAccount);
	}

	public java.lang.String getWeixinAccount() {
		return get("weixinAccount");
	}

	public void setBankcardId(java.lang.Integer bankcardId) {
		set("bankCard_id", bankcardId);
	}

	public java.lang.Integer getBankcardId() {
		return get("bankCard_id");
	}

	public void setMoney(java.math.BigDecimal money) {
		set("money", money);
	}

	public java.math.BigDecimal getMoney() {
		return get("money");
	}

	public void setNote(java.lang.String note) {
		set("note", note);
	}

	public java.lang.String getNote() {
		return get("note");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setAccountNumber(java.lang.String accountNumber) {
		set("accountNumber", accountNumber);
	}

	public java.lang.String getAccountNumber() {
		return get("accountNumber");
	}

	public void setAccountName(java.lang.String accountName) {
		set("accountName", accountName);
	}

	public java.lang.String getAccountName() {
		return get("accountName");
	}

	public void setBankName(java.lang.String bankName) {
		set("bankName", bankName);
	}

	public java.lang.String getBankName() {
		return get("bankName");
	}

	public void setBankBranch(java.lang.String bankBranch) {
		set("bankBranch", bankBranch);
	}

	public java.lang.String getBankBranch() {
		return get("bankBranch");
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

	public void setArriveTime(java.util.Date arriveTime) {
		set("arriveTime", arriveTime);
	}

	public java.util.Date getArriveTime() {
		return get("arriveTime");
	}

	public void setAuditTime(java.util.Date auditTime) {
		set("auditTime", auditTime);
	}

	public java.util.Date getAuditTime() {
		return get("auditTime");
	}

}