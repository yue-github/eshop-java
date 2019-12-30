package com.eshop.controller.pc;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eshop.auditprice.AuditPriceService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.DateHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.logistics.Logistics;
import com.eshop.model.Customer;
import com.eshop.model.Product;
import com.eshop.model.ProductPrice;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.pickupaddress.PickUpAddressService;
import com.eshop.service.Merchant;
import com.eshop.tax.ProductTax;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 添加产品控制器
 * @author TangYiFeng
 */
public class AddProductInfoController extends PcBaseController {
	
	Merchant merchant;
	
	/**
	 * 构造方法
	 */
	public AddProductInfoController() {
		merchant = new Merchant();
	}

	/**
	 * 根据分类id获取产品属性 -发布产品
	 * @param id 分类id(必填)
	 * @return data 产品属性数组  格式：[{propertyId:属性id，propertyName：属性名，propertyValueList:[{propertyValueId：属性值id，propertyValueName：属性名称，isSelected：是否被选中},...]},...]
	 */
	public void getAttrs() {
		if (!this.validateRequiredString("id")) {
			return;
		}
		int id = getParaToInt("id");
		
		List<Record> commonAttrs = Merchant.getAllNosalePropertyByCategoryId(id);
		List<Record> saleAttrs = Merchant.getAllsalePropertyByCategoryId(id);

		jsonObject.put("commonAttrs", commonAttrs);
		jsonObject.put("saleAttrs", saleAttrs);
		renderJson(jsonObject);
	}

	/**
	 * 创建新产品
	 * @param token 帐户访问口令（必填）
	 * @param category_id 分类id（必填）
	 * @param name 产品名称（必填）
	 * @param pricingUnit 单位（必填）
	 * @param unitCost 成本价格（选填）
	 * @param suggestedRetailUnitPrice 指导价格（必填）
	 * @param suggestedRetailUnitDeliveryCharge 运费
	 * @param storeAmount 库存（必填）
	 * @param warningValue 库存预警值（必填）
	 * @param pics 图片 [path1,path2,...]（必填）
	 * @param note 产品详情（必填）
	 * @param taxId 税率id(必填)
	 * @param summary 产品摘要
	 * @param prod_prop 非销售属性 格式：属性值id,属性值id,...（1,2,3）
	 * @param logistics_template_id 运费模板id（必填）
	 * @param weight 重量
	 * @param volume 体积
	 * @param isSupportReturn 是否支持7天包退换（必填）
	 * @param originUnitPrice 产品原价（必填）
	 * @param attrTypeList 产品价格，格式: [{"attrType":"1,2", "price":1.22,"productNumber":22, "originUnitPrice"}]
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void acreate() {
		String[] params = {"name", "prod_type", "supplier_id", "taxRate", "unitCost", "unitCostNoTax",
				"originUnitPrice", "suggestedRetailUnitPrice", "storeAmount", "warningValue", 
				"invoiceType", "note", "logistics_template_id", "pics", "taxId", "is_pre_sale",
				"is_allow_pick"};
		if (!this.validate(params)) {
			return;
		}
		
		int categoryId = getParaToInt("category_id");
		int taxId = getParaToIntDefault("taxId");
		int storeAmount = getParaToInt("storeAmount");
		int warningValue = getParaToInt("warningValue");
		int prodType = getParaToInt("prod_type");
		int logisticsTemplateId = getParaToInt("logistics_template_id");
		int supplierId = getParaToInt("supplier_id");
		BigDecimal unitCost = getParaToDecimal("unitCost");
		BigDecimal unitCostNoTax = getParaToDecimal("unitCostNoTax");
		BigDecimal suggestedRetailUnitPrice = getParaToDecimal("suggestedRetailUnitPrice");
		BigDecimal originUnitPrice = getParaToDecimal("originUnitPrice");
		BigDecimal taxRate = getParaToDecimal("taxRate");
		String name = getPara("name");
		String invoiceType = getPara("invoiceType");
		String pics = getPara("pics");
		String note = getPara("note");
		String summary = (getPara("summary") != null) ? getPara("summary") : "";
		String prodProp = (getPara("prod_prop") != null) ? getPara("prod_prop") : "";
		String attrTypeList = (getPara("attrTypeList") != null) ? getPara("attrTypeList") : "";
		String hotelsList = (getPara("hotels") != null) ? getPara("hotels") : "";
		Integer isDelete = 0;
		int isPreSale = getParaToInt("is_pre_sale");
		Date preEndTime = isPreSale == 1 ? DateHelper.strToDateTime(getPara("pre_end_time")) : null;
		Date preStartTime = isPreSale == 1 ? DateHelper.strToDateTime(getPara("pre_start_time")) : null;
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int shopId = customer.getShopId();
		String composeProducts = getPara("composeProducts");

		Product product = new Product();
		product.setName(name);
		product.setCategoryId(categoryId);
		product.setTaxId(taxId);
		product.setTaxRate(taxRate);
		product.setUnitCost(unitCost);
		product.setUnitCostNoTax(unitCostNoTax);
		product.setSuggestedRetailUnitPrice(suggestedRetailUnitPrice);
		product.setOriginUnitPrice(originUnitPrice);
		product.setIsDelete(isDelete);
		product.setNote(note);
		product.setSummary(summary);
		product.setProdProp(prodProp);
		product.setShopId(shopId);
		product.setStoreAmount(storeAmount);
		product.setWarningValue(warningValue);
		product.setLogisticsTemplateId(logisticsTemplateId);
		product.setSupplierId(supplierId);
		product.setInvoiceType(invoiceType);
		product.setProdType(prodType);
		product.setIsPreSale(isPreSale);
		product.setPreEndTime(preEndTime);
		product.setPreStartTime(preStartTime);
		product.setIsAllowPick(getParaToInt("is_allow_pick"));
		product.setComposeProducts(composeProducts);
		
		if(!isEmpty("id")) {
			product.setId(getParaToInt("id"));
		}
		if(!isEmpty("isSupportReturn")) {
			product.setIsSupportReturn(getParaToInt("isSupportReturn"));
		}
		if (!isEmpty("weight")) {
			product.setWeight(getParaToDecimal("weight"));
		}
		if (!isEmpty("volume")) {
			product.setVolume(getParaToDecimal("volume"));
		}
		if (!isEmpty("commission")) {
			product.setCommission(getParaToDecimal("commission"));
		}
		
		if(!isEmpty("product_brand_id")) {
			product.setProductBrandId(getParaToInt("product_brand_id"));
		}

		int id = (!isEmpty("id")) ? getParaToInt("id") : -1;
		Product oldProduct = Product.dao.findById(id);
		List<ProductPrice> oldPrices = ProductPrice.dao.find("select * from product_price where product_id = ?", id);

		ServiceCode code = Merchant.publishProduct(product, hotelsList, attrTypeList, pics);
		
		// 保存自提地点
		int isAllowPick = getParaToInt("is_allow_pick");
		if (isAllowPick == 1) {
			JSONArray productAddresses = JSON.parseArray(getPara("product_addresses"));
			PickUpAddressService.createProductAddresses(product.getId(), productAddresses);
		} else {
			PickUpAddressService.deleteProductAddressesByProductId(product.getId());
		}
		
		Product newProduct = Product.dao.findById(product.getId());
		List<ProductPrice> newPrices = ProductPrice.dao.find("select * from product_price where product_id = ?", product.getId());
		if (newProduct != null) {
			AuditPriceService.create(customer, id, oldProduct, newProduct, oldPrices, newPrices);
		}

		if (code != ServiceCode.Success) {
			jsonObject.put("error", 1);
			jsonObject.put("errmsg", "保存产品失败");
		}

		renderJson(jsonObject);
	}
	
	/**
	 * 修改产品
	 * @param token 帐户访问口令（必填）
	 * @param category_id 分类id（必填）
	 * @param name 产品名称（必填）
	 * @param pricingUnit 单位（必填）
	 * @param unitCost 成本价格（必填）
	 * @param suggestedRetailUnitPrice 指导价格（必填）
	 * @param suggestedRetailUnitDeliveryCharge 运费
	 * @param storeAmount 库存（必填）
	 * @param warningValue 库存预警值（必填）
	 * @param mainPic 图片 [path1,path2,...]（必填）
	 * @param note 产品详情（必填）
	 * @param summary 产品摘要
	 * @param logistics_template_id 运费模板id（必填）
	 * @param weight 重量
	 * @param volume 体积
	 * @param prod_prop 非销售属性 格式：属性值id,属性值id,...（1,2,3）
	 * @param attrTypeList 产品价格，格式: [{"attrType":"1,2", "price":1.22,"productNumber":22}]
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "name", "prod_type", "supplier_id", "taxRate", "unitCost", "unitCostNoTax", 
				"originUnitPrice", "suggestedRetailUnitPrice", "storeAmount", "warningValue", 
				"invoiceType", "note", "logistics_template_id", "pics"};
		if (!this.validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		int logisticsTemplateId = getParaToInt("logistics_template_id");
		int prodType = getParaToInt("prod_type");
		int storeAmount = getParaToInt("storeAmount");
		int warningValue = getParaToInt("warningValue");
		BigDecimal unitCost = getParaToDecimal("unitCost");
		BigDecimal suggestedRetailUnitPrice = getParaToDecimal("suggestedRetailUnitPrice");
		BigDecimal originUnitPrice = getParaToDecimal("originUnitPrice");
		BigDecimal unitCostNoTax = getParaToDecimal("unitCostNoTax");
		BigDecimal taxRate = getParaToDecimal("taxRate");
		String name = getPara("name");
		String mainPicStr = getPara("pics");
		String note = getPara("note");
		String attrTypeList = getPara("attrTypeList");
		String invoiceType = getPara("invoiceType");
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int shopId = customer.getShopId();

		Product product = new Product();
		product.setId(id);
		product.setName(name);
		product.setInvoiceType(invoiceType);
		product.setUnitCost(unitCost);
		product.setSuggestedRetailUnitPrice(suggestedRetailUnitPrice);
		product.setUnitCostNoTax(unitCostNoTax);
		product.setTaxRate(taxRate);
		product.setStoreAmount(storeAmount);
		product.setWarningValue(warningValue);
		product.setNote(note);
		product.setShopId(shopId);
		product.setLogisticsTemplateId(logisticsTemplateId);
		product.setProdType(prodType);
		product.setOriginUnitPrice(originUnitPrice);
		
		if (getPara("summary") != null) {
			product.setSummary(getPara("summary"));
		}
		if (getPara("prodProp") != null) {
			product.setProdProp(getPara("prodProp"));
		}
		if (getPara("isSupportReturn") != null) {
			product.setIsSupportReturn(getParaToInt("isSupportReturn"));
		}
		if (getPara("weight") != null) {
			product.setWeight(getParaToDecimal("weight"));
		}
		if (getPara("volume") != null) {
			product.setVolume(getParaToDecimal("volume"));
		}
		if (getPara("taxId") != null) {
			product.setTaxId(getParaToInt("taxId"));
		}
		
		if(!isEmpty("product_brand_id")) {
			product.setProductBrandId(getParaToInt("product_brand_id"));
		}

		ServiceCode code = merchant.updateProduct(product, attrTypeList, mainPicStr);

		if (code != ServiceCode.Success) {
			jsonObject.put("error", 1);
			jsonObject.put("errmsg", "保存产品失败");
		}

		renderJson(jsonObject);
	}
    
    /**
     * 获取宝贝信息
     * @param token 帐户访问口令（必填）
     * @param id 宝贝id
     * @return 成功  {error: 0 product:{.....}}；失败：{error: >0, errmsg: 错误信息}
     */
    public void get() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Product product = Merchant.getProductDetail(id);
    	
    	if (product == null) {
    		returnError(ErrorCode.Exception, "产品不存在");
    		return;
    	}
    	
    	List<String> pics = product.get("pics");
    	List<String> newPics = new ArrayList<String>();
    	
    	for (String item : pics) {
    		newPics.add(item);
    	}
    	
    	product.put("pics", newPics);
    	
    	jsonObject.put("product", product);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取店铺下的所有运费模板
     * @param token
     * @return 成功：{error:0, data:[{id:模板id,shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					             expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{province_id:省id,city_id:市id,isAllProvince:是否省下面所有市,firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费},...],
     *                              },...]
     *                        },...]
     * 
     *              } 
     *            
     *         失败：{error:>0, errmsg:错误信息}   
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void findLogisticsTemplateByShopId() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	
    	if (customer == null) {
    		this.returnError(ErrorCode.Exception, "登录失败");
    		return;
    	}
    	
    	int shopId = customer.getShopId();
    	List<Record> list = Logistics.findLogisticsTemplateByShopId(shopId);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
	
    /**
     * 商品回收站列表
     * @param token
     * @param offset
     * @param length
     * @param name 产品名称
     * @param supplierName 供应商名称
     * @return 成功：{error:0,totalPage：总页数,totalRow:总行数,data:[{id:id,name:产品名称,...},...]}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getDeletedProducts() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int isDeleted = 1;
    	String productName = getPara("name");
    	String supplierName = getPara("supplierName");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, productName, null, null, null, isDeleted, null, null, null, supplierName, "(0,1,2)", orderByMap);
    	int count = Merchant.countProductItems(shopId, productName, null, null, null, isDeleted, null, null, null, supplierName, "(0,1,2)");
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("total", count);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 还原商品
     * @param ids
     * @return 成功：{error:0} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void recoverProducts() {
    	if (!this.validateRequiredString("ids")) {
    		return;
    	}
    	
    	String ids = getPara("ids");
    	List<String> idsArr = JSON.parseArray(ids, String.class);
    	
    	for (String item : idsArr) {
    		int id = Integer.parseInt(item);
    		ServiceCode code = Merchant.recoverProduct(id);
    		if (code != ServiceCode.Success) {
    			this.returnError(ErrorCode.Exception, "还原产品失败");
    			return;
    		}
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 上架商品
     * @param ids
     * @return 成功：{error:0} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void onShelfProducts() {
    	if (!this.validateRequiredString("ids")) {
    		return;
    	}
    	
    	String ids = getPara("ids");
    	List<String> idsArr = JSON.parseArray(ids, String.class);
    	
    	for (String item : idsArr) {
    		int id = Integer.parseInt(item);
    		ServiceCode code = Merchant.onShelf(id);
    		if (code != ServiceCode.Success) {
    			this.returnError(ErrorCode.Exception, "上架产品失败");
    			return;
    		}
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有可用税率
     * @return 成功：{error:0,data:[{id:id,name:名称,rate:税率值}, ...]} 失败：{error:>0,errmsg:错误信息}
     */
    public void manyTax() {
    	List<Record> list = ProductTax.findTaxItems(null, 1);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
}