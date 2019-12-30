package com.eshop.controller.pc;

import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.collection.CollectionService;
import com.eshop.content.ContentService;
import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Category;
import com.eshop.model.Customer;
import com.eshop.model.Product;
import com.eshop.model.ProductBrand;
import com.eshop.model.ProductPrice;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Member;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 商品详情控制器
 *   @author TangYiFeng
 */
public class ProductController extends PcBaseController {
	
	private CategoryService categoryService;

    /**
     * Default constructor
     */
    public ProductController() {
    	categoryService = new CategoryService();
    }
    
    /**
     * 产品详情
     * @param id 产品id
     * @return
     * "error":0,
     * "product":{
	 *  "name":"特大水蜜桃",
	 *  "summary":"新鲜采摘，水分充足",
	 *  "actualUnitPrice":18  实际价格
	 *  "suggestedRetailUnitPrice":"20",指导价格
	 *  "suggestedRetailUnitDeliveryCharge":"8",
	 *  "pics":["1.png","2.png",...],
	 *  "note":"<img src='images/promotion2.png'/><p>111夏橙产自“夏橙之乡”泄滩，新鲜成熟，现摘现发，酸甜宜人。由于夏季温度过高，为了避免容易腐烂和水分散失的问题。夏橙都经过食用蜡处理，食用蜡具有无毒、无味、无污染、无副作用的特性，亲们可以放心食用。夏橙相比伦晚个头小，果径在60-65mm左右夏橙皮薄多汁，但和伦晚脐橙相比有籽、不太化渣。所以夏橙更适合榨汁。夏橙是酸甜口感，7分天3分酸，属于酸甜爽口型，夏天吃起来非常清爽，一点酸都不能接受的慎拍哦！夏橙于头年春季开花，第二次夏季的4月底5月初成熟采收结的果，经过“三青三黄”，可能亲收到的部分橙子是青色的，这属于正常的成熟状态。</p>"
	 *  }
     */
    public void productInfo() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Product product = Merchant.getProductDetail(id);

    	if (product == null) {
    		setError(ErrorCode.Exception, "产品不存在");
    		return;
    	}
    	
    	jsonObject.put("error", 0);
		jsonObject.put("product", product);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取产品评价
     * @param id 产品id（必填）
     * @param offset 页码  必填
     * @param length  每页条数  必填
     * @return 成功：{error: 0, totalPages:总页数, totalRow:总行数, reviews: [{nickName: 会员昵称, headImg: 头像, comments: 评论内容, ratings: 评价等级, created_at: 评论时间, pics: [图片路径, ...]}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void productReview() {
    	String[] params = {"id", "offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("id");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	List<Record> list = Member.findCommentItems(offset, length, productId, null, null, null, null, null, null, null);
    	int total = Member.countCommentItems(productId, null, null, null, null, null, null, null);
    	
    	//评价率
    	Map<String, Integer> ratings = Member.getProductRatings(productId);
    	
    	jsonObject.put("reviews", list);
    	jsonObject.put("rangs", ratings);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 是否收藏商品
     * @param token(必填)
     * @param productId 产品id(必填)
     * @return 成功：{error:0}  失败：{error:>0, errmsg:错误信息}
     */
    public void whetherCollection() {
    	String[] params = {"productId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("productId");
    	int customerId = 0;
    	
    	if (getPara("token") != null && !getPara("token").equals("")) {
    		String token = getPara("token");
        	Customer customer = (Customer) CacheHelper.get(token);
        	customerId = customer.getId();
    	}
    	
    	boolean code = CollectionService.isCollect(customerId, productId, 1);
    	
    	if (code) {
    		this.returnError(1, "已收藏");
    		return;
    	}else{
    		this.returnError(2, "未收藏");
    		return;
    	}
    }
    
    /**
     * 收藏商品
     * @param token(必填)
     * @param productId 产品id(必填)
     * @return 成功：{error:0,error:-1 已收藏}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void collection() {
    	if (!this.validateRequiredString("productId")) {
    		return;
    	}
    	int productId = getParaToInt("productId");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = CollectionService.collectProduct(customerId, productId);
    	
    	if (code == ServiceCode.Validation) {
    		this.returnError(-1, "已收藏");
    		return;
    	}
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "收藏失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 店铺信息
     * @param productId 产品id(必填)
     * @return 成功：{error:0, data:[{id:id, name: 名称，phone:手机号, mainPic:店铺logo, productRating:商品评价}]}  失败：{error:>0, errmsg:错误信息}
     */
    public void getShop() {
    	if (!this.validateRequiredString("productId")) {
    		return;
    	}
    	int productId = getParaToInt("productId");
    	
    	Product product = Product.dao.findById(productId);
    	
    	if (product == null) {
    		returnError(ErrorCode.Exception, "该产品不存在");
    		return;
    	}
    	
    	Shop shop = Merchant.getShop(product.getShopId());
    	
    	if (shop == null) {
    		returnError(ErrorCode.Exception, "该店铺不存在");
    		return;
    	}
    	
    	String logoPic = ResourceService.getPath(shop.getLogoPic());
    	shop.put("mainPic", this.getResourcePath(logoPic));
    	shop.set("logoPic", this.getResourcePath(logoPic));
    	shop.put("productRating", 9.5);
    	
    	jsonObject.put("data", shop);
    	renderJson(jsonObject);
    }
    
    /**
     * 收藏店铺
     * @param token
     * @param shopId 店铺id
     * @return 成功 {error:0,error:-1 已收藏} 失败 {error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void collectionShop() {
    	if (!this.validateRequiredString("shopId")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	int shopId = getParaToInt("shopId");
    	
    	ServiceCode code = CollectionService.collectShop(customerId, shopId);
    	
    	if (code == ServiceCode.Validation) {
    		this.returnError(-1, "已收藏该店铺");
    		return;
    	}
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "收藏店铺失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
	 * 根据产品id获取产品属性
	 * @param id 产品id(必填)
	 * @return data 产品属性数组  格式：[{propertyId:属性id，propertyName：属性名，propertyValueList:[{propertyValueId：属性值id，propertyValueName：属性名称，isSelected：是否被选中},...]},...]
	 */
	public void getAttrs() {
		int id = getParaToInt("id");
		List<Record> commonAttrs = Merchant.getNosalePropertyByProductId(id);
		List<Record> saleAttrs = Merchant.getSalePropertyByProductId(id);

		jsonObject.put("commonAttrs", commonAttrs);
		jsonObject.put("saleAttrs", saleAttrs);
		renderJson(jsonObject);
	}
	
	/**
	 * 获取产品价格
	 * @param product_id 分类id(必填)
	 * @param type_attr 分类id(必填)
	 * @return data 
	 */
	public void getPrice() {
		if (!this.validateRequiredString("product_id")) {
    		return;
    	}
		int productId = getParaToInt("product_id");
		
		if (!this.validateRequiredString("type_attr")) {
    		return;
    	}
		String typeAttr = getPara("type_attr");
		
		ProductPrice price = Merchant.getPrice(productId, typeAttr);
		
		if(price == null) {
			this.returnError(ErrorCode.Exception, "数据异常");
			return;
		}
		
		jsonObject.put("data", price);
		renderJson(jsonObject);
	}
	
	/**
	 * 根据用户习惯获取推荐产品
	 * @param row 条数
	 * @return 成功：{error:0,data:[{id:产品id,name:产品名称,shop_id:店铺id,shopName:店铺名称,shopType:店铺类型,mainPicPath:产品主图,summary:产品摘要,suggestedRetailUnitPrice:价格},...]}
	 */
    public void getRecommendProductsByCustomer() {
    	if (!this.validateRequiredString("row")) {
    		return;
    	}
    	int row = getParaToInt("row");
    	String token = getPara("token");
    	Customer customer = null;
    	if (token != null && !token.equals("")) {
    		customer = (Customer) CacheHelper.get(token);
    	};
    	Integer customerId = null;
    	if (customer != null) {
			customerId = customer.getId();
		}
    	
    	List<Product> list = ContentService.getRecommendProductsByCustomer(customerId, row);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取供应商
     * 成功：{error:0,data:[{id:id,name:供应商名称},...]}  失败：{error:>0,errmsg:错误信息}
     */
    public void getSuppliers() {
    	List<Record> list = Manager.findSupplierItems(null, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取品牌
     * 成功：{error:0,data:[{id:id,name:品牌名称},...]}  失败：{error:>0,errmsg:错误信息}
     */
    public void getBrands() {
    	List<ProductBrand> list = ProductBrand.dao.find("select * from product_brand");
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取产品所属分类
     * @param id 产品id
     * 成功：{error:0,data:[{id:id,name:分类名称},...]}  失败：{error:>0,errmsg:错误信息}
     */
    public void getProductCategory() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Product product = Merchant.getProduct(id);
    	int cateId = product.getCategoryId();
    	List<Category> list = categoryService.getAllParentCategoryByIdAndInclude(cateId);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 计算酒店产品价格
     * @param id 产品id
     * 成功：
     */
    public void getHotelPrice() {
    	if (!this.validateRequiredString("startAt")) {
    		return;
    	}
    	if (!this.validateRequiredString("endAt")) {
    		return;
    	}
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	Date startAt = getParaToDate("startAt");
    	Date endAt = getParaToDate("endAt");
    	int id = getParaToInt("id");
    	Map<String, Double> map = Merchant.getHotelPrice(startAt, endAt, id);
    	jsonObject.putAll(map);
    	renderJson(jsonObject);
    }
    
}