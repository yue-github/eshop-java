package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseLogisticsTemplateDetail<M extends BaseLogisticsTemplateDetail<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setTemplateId(java.lang.Integer templateId) {
		set("template_id", templateId);
	}

	public java.lang.Integer getTemplateId() {
		return get("template_id");
	}

	public void setExpressType(java.lang.Integer expressType) {
		set("expressType", expressType);
	}

	public java.lang.Integer getExpressType() {
		return get("expressType");
	}

	public void setFirstUnit(java.math.BigDecimal firstUnit) {
		set("firstUnit", firstUnit);
	}

	public java.math.BigDecimal getFirstUnit() {
		return get("firstUnit");
	}

	public void setFirstPay(java.math.BigDecimal firstPay) {
		set("firstPay", firstPay);
	}

	public java.math.BigDecimal getFirstPay() {
		return get("firstPay");
	}

	public void setAddUnit(java.math.BigDecimal addUnit) {
		set("addUnit", addUnit);
	}

	public java.math.BigDecimal getAddUnit() {
		return get("addUnit");
	}

	public void setAddPay(java.math.BigDecimal addPay) {
		set("addPay", addPay);
	}

	public java.math.BigDecimal getAddPay() {
		return get("addPay");
	}

	public void setProvinceId(java.lang.Integer provinceId) {
		set("province_id", provinceId);
	}

	public java.lang.Integer getProvinceId() {
		return get("province_id");
	}

	public void setIsAllProvince(java.lang.Integer isAllProvince) {
		set("isAllProvince", isAllProvince);
	}

	public java.lang.Integer getIsAllProvince() {
		return get("isAllProvince");
	}

	public void setCityId(java.lang.Integer cityId) {
		set("city_id", cityId);
	}

	public java.lang.Integer getCityId() {
		return get("city_id");
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

	public void setIsDefaultFreight(java.lang.Integer isDefaultFreight) {
		set("isDefaultFreight", isDefaultFreight);
	}

	public java.lang.Integer getIsDefaultFreight() {
		return get("isDefaultFreight");
	}

	public void setTheSameItemNum(java.lang.Integer theSameItemNum) {
		set("theSameItemNum", theSameItemNum);
	}

	public java.lang.Integer getTheSameItemNum() {
		return get("theSameItemNum");
	}

}