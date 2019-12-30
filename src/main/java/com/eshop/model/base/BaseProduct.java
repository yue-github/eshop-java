package com.eshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProduct<M extends BaseProduct<M>> extends Model<M> implements IBean {

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

	public void setSupplierId(java.lang.Integer supplierId) {
		set("supplier_id", supplierId);
	}

	public java.lang.Integer getSupplierId() {
		return get("supplier_id");
	}

	public void setUpc(java.lang.String upc) {
		set("upc", upc);
	}

	public java.lang.String getUpc() {
		return get("upc");
	}

	public void setCategoryId(java.lang.Integer categoryId) {
		set("category_id", categoryId);
	}

	public java.lang.Integer getCategoryId() {
		return get("category_id");
	}

	public void setPricingUnit(java.lang.String pricingUnit) {
		set("pricingUnit", pricingUnit);
	}

	public java.lang.String getPricingUnit() {
		return get("pricingUnit");
	}

	public void setUnitCost(java.math.BigDecimal unitCost) {
		set("unitCost", unitCost);
	}

	public java.math.BigDecimal getUnitCost() {
		return get("unitCost");
	}

	public void setSuggestedRetailUnitPrice(java.math.BigDecimal suggestedRetailUnitPrice) {
		set("suggestedRetailUnitPrice", suggestedRetailUnitPrice);
	}

	public java.math.BigDecimal getSuggestedRetailUnitPrice() {
		return get("suggestedRetailUnitPrice");
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

	public void setIsSale(java.lang.Integer isSale) {
		set("is_sale", isSale);
	}

	public java.lang.Integer getIsSale() {
		return get("is_sale");
	}

	public void setSummary(java.lang.String summary) {
		set("summary", summary);
	}

	public java.lang.String getSummary() {
		return get("summary");
	}

	public void setNote(java.lang.String note) {
		set("note", note);
	}

	public java.lang.String getNote() {
		return get("note");
	}

	public void setMainPic(java.lang.Integer mainPic) {
		set("mainPic", mainPic);
	}

	public java.lang.Integer getMainPic() {
		return get("mainPic");
	}

	public void setStoreAmount(java.lang.Integer storeAmount) {
		set("storeAmount", storeAmount);
	}

	public java.lang.Integer getStoreAmount() {
		return get("storeAmount");
	}

	public void setShopId(java.lang.Integer shopId) {
		set("shop_id", shopId);
	}

	public java.lang.Integer getShopId() {
		return get("shop_id");
	}

	public void setProdProp(java.lang.String prodProp) {
		set("prod_prop", prodProp);
	}

	public java.lang.String getProdProp() {
		return get("prod_prop");
	}

	public void setReviewNum1(java.lang.Integer reviewNum1) {
		set("reviewNum1", reviewNum1);
	}

	public java.lang.Integer getReviewNum1() {
		return get("reviewNum1");
	}

	public void setReviewNum2(java.lang.Integer reviewNum2) {
		set("reviewNum2", reviewNum2);
	}

	public java.lang.Integer getReviewNum2() {
		return get("reviewNum2");
	}

	public void setReviewNum3(java.lang.Integer reviewNum3) {
		set("reviewNum3", reviewNum3);
	}

	public java.lang.Integer getReviewNum3() {
		return get("reviewNum3");
	}

	public void setLogisticsTemplateId(java.lang.Integer logisticsTemplateId) {
		set("logistics_template_id", logisticsTemplateId);
	}

	public java.lang.Integer getLogisticsTemplateId() {
		return get("logistics_template_id");
	}

	public void setWeight(java.math.BigDecimal weight) {
		set("weight", weight);
	}

	public java.math.BigDecimal getWeight() {
		return get("weight");
	}

	public void setVolume(java.math.BigDecimal volume) {
		set("volume", volume);
	}

	public java.math.BigDecimal getVolume() {
		return get("volume");
	}

	public void setIsSupportReturn(java.lang.Integer isSupportReturn) {
		set("isSupportReturn", isSupportReturn);
	}

	public java.lang.Integer getIsSupportReturn() {
		return get("isSupportReturn");
	}

	public void setOriginUnitPrice(java.math.BigDecimal originUnitPrice) {
		set("originUnitPrice", originUnitPrice);
	}

	public java.math.BigDecimal getOriginUnitPrice() {
		return get("originUnitPrice");
	}

	public void setIsDelete(java.lang.Integer isDelete) {
		set("isDelete", isDelete);
	}

	public java.lang.Integer getIsDelete() {
		return get("isDelete");
	}

	public void setTaxId(java.lang.Integer taxId) {
		set("taxId", taxId);
	}

	public java.lang.Integer getTaxId() {
		return get("taxId");
	}

	public void setInvoiceType(java.lang.String invoiceType) {
		set("invoiceType", invoiceType);
	}

	public java.lang.String getInvoiceType() {
		return get("invoiceType");
	}

	public void setTaxRate(java.math.BigDecimal taxRate) {
		set("taxRate", taxRate);
	}

	public java.math.BigDecimal getTaxRate() {
		return get("taxRate");
	}

	public void setUnitCostNoTax(java.math.BigDecimal unitCostNoTax) {
		set("unitCostNoTax", unitCostNoTax);
	}

	public java.math.BigDecimal getUnitCostNoTax() {
		return get("unitCostNoTax");
	}

	public void setCommentNum(java.lang.Integer commentNum) {
		set("commentNum", commentNum);
	}

	public java.lang.Integer getCommentNum() {
		return get("commentNum");
	}

	public void setSalesVolume(java.lang.Integer salesVolume) {
		set("salesVolume", salesVolume);
	}

	public java.lang.Integer getSalesVolume() {
		return get("salesVolume");
	}

	public void setSaleroom(java.math.BigDecimal saleroom) {
		set("saleroom", saleroom);
	}

	public java.math.BigDecimal getSaleroom() {
		return get("saleroom");
	}

	public void setProdType(java.lang.Integer prodType) {
		set("prod_type", prodType);
	}

	public java.lang.Integer getProdType() {
		return get("prod_type");
	}

	public void setWarningValue(java.lang.Integer warningValue) {
		set("warningValue", warningValue);
	}

	public java.lang.Integer getWarningValue() {
		return get("warningValue");
	}

	public void setRelateId(java.lang.Integer relateId) {
		set("relate_id", relateId);
	}

	public java.lang.Integer getRelateId() {
		return get("relate_id");
	}

	public void setIsPreSale(java.lang.Integer isPreSale) {
		set("is_pre_sale", isPreSale);
	}

	public java.lang.Integer getIsPreSale() {
		return get("is_pre_sale");
	}

	public void setPreStartTime(java.util.Date preStartTime) {
		set("pre_start_time", preStartTime);
	}

	public java.util.Date getPreStartTime() {
		return get("pre_start_time");
	}

	public void setPreEndTime(java.util.Date preEndTime) {
		set("pre_end_time", preEndTime);
	}

	public java.util.Date getPreEndTime() {
		return get("pre_end_time");
	}

	public void setIsAllowPick(java.lang.Integer isAllowPick) {
		set("is_allow_pick", isAllowPick);
	}

	public java.lang.Integer getIsAllowPick() {
		return get("is_allow_pick");
	}

	public void setCommission(java.math.BigDecimal commission) {
		set("commission", commission);
	}

	public java.math.BigDecimal getCommission() {
		return get("commission");
	}

	public void setIsSeckill(java.lang.Integer isSeckill) {
		set("is_seckill", isSeckill);
	}

	public java.lang.Integer getIsSeckill() {
		return get("is_seckill");
	}

	public void setSecPrice(java.math.BigDecimal secPrice) {
		set("sec_price", secPrice);
	}

	public java.math.BigDecimal getSecPrice() {
		return get("sec_price");
	}

	public void setSecStartTime(java.util.Date secStartTime) {
		set("sec_start_time", secStartTime);
	}

	public java.util.Date getSecStartTime() {
		return get("sec_start_time");
	}

	public void setSecEndTime(java.util.Date secEndTime) {
		set("sec_end_time", secEndTime);
	}

	public java.util.Date getSecEndTime() {
		return get("sec_end_time");
	}

	public void setProductBrandId(java.lang.Integer productBrandId) {
		set("product_brand_id", productBrandId);
	}

	public java.lang.Integer getProductBrandId() {
		return get("product_brand_id");
	}

	public void setComposeProducts(java.lang.String composeProducts) {
		set("composeProducts", composeProducts);
	}

	public java.lang.String getComposeProducts() {
		return get("composeProducts");
	}

}