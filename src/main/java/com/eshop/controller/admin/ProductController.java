package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.category.CategoryService;
import com.eshop.content.ResourceService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Category;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 产品管理-控制器
 * @author TangYiFeng
 */
public class ProductController extends AdminBaseController {

	/**
     * 构造方法
     */
    public ProductController() {
    }

    /**
     * 获取所有产品
     * @param offset
     * @param length
     * @param category_id
     * @param keyname
     * @param is_sale
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, data: [{id: 产品id, name: 名称, categoryName: 分类名称, unitCost: 成本价, suggestedRetailUnitPrice: 指导价, is_sale: 是否上架, mainPic: 主图}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	Integer categoryId = getParaToIntegerDefault("category_id");
    	categoryId = (categoryId == null || categoryId == 0) ? null : categoryId;
    	Integer isSale = getParaToIntegerDefault("is_sale");
    	Integer isDeleted = 0;
    	
    	String productName = getPara("name");
    	String categoryName = getPara("categoryName");
    	BigDecimal unitCostMoreThan = getParaToDecimalDefault("unitCostMoreThan");
    	BigDecimal unitCostLessThan = getParaToDecimalDefault("unitCostLessThan");
    	BigDecimal suggestedRetailUnitPriceMoreThan = getParaToDecimalDefault("suggestedRetailUnitPriceMoreThan");
    	BigDecimal suggestedRetailUnitPriceLessThan = getParaToDecimalDefault("suggestedRetailUnitPriceLessThan");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Merchant.findProductItems(offset, length, null, productName, categoryId, categoryName, isSale, isDeleted, null, null, null, null, unitCostMoreThan, unitCostLessThan, suggestedRetailUnitPriceMoreThan, suggestedRetailUnitPriceLessThan, null, orderByMap);
    	int total = Merchant.countProductItems(null, productName, categoryId, categoryName, isSale, isDeleted, null, null, null, null, unitCostMoreThan, unitCostLessThan, suggestedRetailUnitPriceMoreThan, suggestedRetailUnitPriceLessThan, null);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    public void getChildCates() {
    	int id = getParaToInt("id");
    	List<Category> list = new CategoryService().getAllChildAndInclude(id);
    	String whereIn = CategoryService.getWhereInIds(list);
    	jsonObject.put("whereIn", whereIn);
    	renderJson(jsonObject);
    }
    
    /**
     * 用id获取产品
     * @param id 产品id（必填）
     * @return 成功：{error: 0, product: {id: 产品id, name: 名称, categoryName: 分类名称, unitCost: 成本价, suggestedRetailUnitPrice: 指导价, unitDeliverCost: 运费成本, suggestedRetailUnitDeliveryCharge: 指导运费, minAllowableUnitPrice: 最低售价, minAllowableUnitDeliveryCharge: 最低运费, is_sale: 是否上架, mainPic: 主图, note: 产品描述}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Product product = Merchant.getProduct(id);
    	
    	String mainPic = ResourceService.getPath(product.getMainPic());
    	product.put("mainPic", mainPic);
    	
    	Category category = Manager.getCategory(product.getCategoryId());
    	String categoryName = category != null ? category.getName() : "";
    	product.put("categoryName", categoryName);
    	
    	jsonObject.put("product", product);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建产品
     * @param token 帐户访问口令（必填）
     * @param name 名称
     * @param category_id 分类名称id
     * @param unitCost 成本价
     * @param suggestedRetailUnitPrice 指导价
     * @param unitDeliverCost 运费成本
     * @param suggestedRetailUnitDeliveryCharge 指导运费
     * @param minAllowableUnitPrice 最低售价
     * @param minAllowableUnitDeliveryCharge 最低运费
     * @param is_sale 是否上架
     * @param mainPic 主图
     * @param summary 摘要
     * @param note 产品描述
     * @param upc 通用产品代码
     * @param supplier_id 供应商id
     * @param model 型号
     * @param pricingUnit 单位
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    /*public void create() {
    	String name = getPara("name");
    	int categoryId = getParaToInt("category_id");
    	BigDecimal unitCost = getParaToDecimal("unitCost");
    	BigDecimal suggestedRetailUnitPrice = getParaToDecimal("suggestedRetailUnitPrice");
    	BigDecimal unitDeliverCost = getParaToDecimal("unitDeliverCost");
    	BigDecimal suggestedRetailUnitDeliveryCharge = getParaToDecimal("suggestedRetailUnitDeliveryCharge");
    	BigDecimal minAllowableUnitPrice = getParaToDecimal("minAllowableUnitPrice");
    	BigDecimal minAllowableUnitDeliveryCharge = getParaToDecimal("minAllowableUnitDeliveryCharge");
    	int isSale = getParaToInt("is_sale");
    	String upc = getPara("upc");
    	String model = getPara("model");
    	String pricingUnit = getPara("pricingUnit");
    	int supplierId = getParaToInt("supplier_id");
    	
    	String mainPic = getPara("mainPic");
    	String summary = getPara("summary");
    	String note = getPara("note");
    	
    	Product product = new Product();
    	product.setName(name);
    	product.setCategoryId(categoryId);
    	product.setUnitCost(unitCost);
    	product.setSuggestedRetailUnitPrice(suggestedRetailUnitPrice);
    	product.setUnitDeliverCost(unitDeliverCost);
    	product.setSuggestedRetailUnitDeliveryCharge(suggestedRetailUnitDeliveryCharge);
    	product.setMinAllowableUnitPrice(minAllowableUnitPrice);
    	product.setMinAllowableUnitDeliveryCharge(minAllowableUnitDeliveryCharge);
    	product.setIsSale(isSale);
    	product.setSummary(summary);
    	product.setNote(note);
    	product.setUpc(upc);
    	product.setModel(model);
    	product.setPricingUnit(pricingUnit);
    	product.setSupplierId(supplierId);
    	product.setCreatedAt(new Date());
    	product.setUpdatedAt(new Date());
    	
    	if(productService.createProduct(product, mainPic) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createAddress createAddress failed");
    	}
    	
    	renderJson(jsonObject);
    }*/
    
    /**
     * 修改产品 
     * @param token 帐户访问口令（必填）
     * @param id 产品id
     * @param categoryId 分类名称id
     * @param name 名称
     * @param unitCost 成本价
     * @param suggestedRetailUnitPrice 指导价
     * @param unitDeliverCost 运费成本
     * @param suggestedRetailUnitDeliveryCharge 指导运费
     * @param minAllowableUnitPrice 最低售价
     * @param minAllowableUnitDeliveryCharge 最低运费
     * @param is_sale 是否上架
     * @param mainPic 主图
     * @param summary 摘要
     * @param note 产品描述
     * @param upc 通用产品代码
     * @param supplier_id 供应商id
     * @param model 型号
     * @param pricingUnit 单位
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    /*public void update() {
    	int id = getParaToInt("id");
    	String name = getPara("name");
    	int categoryId = getParaToInt("categoryId");
    	BigDecimal unitCost = getParaToDecimal("unitCost");
    	BigDecimal suggestedRetailUnitPrice = getParaToDecimal("suggestedRetailUnitPrice");
    	BigDecimal unitDeliverCost = getParaToDecimal("unitDeliverCost");
    	BigDecimal suggestedRetailUnitDeliveryCharge = getParaToDecimal("suggestedRetailUnitDeliveryCharge");
    	BigDecimal minAllowableUnitPrice = getParaToDecimal("minAllowableUnitPrice");
    	BigDecimal minAllowableUnitDeliveryCharge = getParaToDecimal("minAllowableUnitDeliveryCharge");
    	int isSale = getParaToInt("is_sale");
    	String upc = getPara("upc");
    	String model = getPara("model");
    	String pricingUnit = getPara("pricingUnit");
    	int supplierId = getParaToInt("supplier_id");
    	
    	String mainPic = getPara("mainPic");
    	String summary = getPara("summary");
    	String note = getPara("note");
    	
    	Product product = new Product();
    	product.setId(id);
    	product.setName(name);
    	product.setCategoryId(categoryId);
    	product.setUnitCost(unitCost);
    	product.setSuggestedRetailUnitPrice(suggestedRetailUnitPrice);
    	product.setUnitDeliverCost(unitDeliverCost);
    	product.setSuggestedRetailUnitDeliveryCharge(suggestedRetailUnitDeliveryCharge);
    	product.setMinAllowableUnitPrice(minAllowableUnitPrice);
    	product.setMinAllowableUnitDeliveryCharge(minAllowableUnitDeliveryCharge);
    	product.setIsSale(isSale);
    	product.setSummary(summary);
    	product.setNote(note);
    	product.setUpc(upc);
    	product.setModel(model);
    	product.setPricingUnit(pricingUnit);
    	product.setSupplierId(supplierId);
    	
    	if(productService.update(product, mainPic) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createAddress createAddress failed");
    	}
    	
    	renderJson(jsonObject);
    }*/
    
    /**
     * 批量删除产品
     * @param token 帐户访问口令（必填）
     * @param ids 产品id数组:[1, 2, ...]（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    public void batchDelete() {
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	
    	ServiceCode code = Merchant.deleteProduct(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取供应商
     * @return 成功：{error: 0, data:[{id:id,name:供应商名称},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void getAllSuppliers() {
    	List<Record> list = Manager.findSupplierItems(null, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}