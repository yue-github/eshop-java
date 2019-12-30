package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.collection.CollectionService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.Product;
import com.eshop.model.ProductPrice;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BasePromotion;
import com.eshop.service.Member;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 *   商品详情控制器
 *   @author TangYiFeng
 */
public class ProductController extends WebappBaseController {

    /**
     * 构造方法
     */
    public ProductController() {
    }
    
    /**
     * 获取产品信息
     * @param id 产品id
     * @return 成功：{error:0,product:{id:产品id,note:产品详情}}, 失败：{error:>0,errmsg:错误信息}
     */
    public void getProduct() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Product product = Member.getProduct(id);
    	
    	jsonObject.put("product", product);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取产品信息
     * @param id 产品id
     * @return 成功：{error:0,product:{id:产品id,"name":"产品名称","summary":"摘要","actualUnitPrice":实际价格,"suggestedRetailUnitPrice":指导价格,note:详情,"pics":["images/product_01.jpg","images/product_01.jpg",...],productPrices:[{alert_number:警告库存,price:价格,product_id:产品id,weight:重量,product_number:库存},...]}}, 失败：{error:>0,errmsg:错误信息}
     */
    public void productInfo() {
    	String[] params = {"id"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	Product product = Merchant.getProductDetail(id);
    	
		jsonObject.put("product", product);
    	renderJson(jsonObject);
    }

    /**
	 * 获取产品价格
	 * @param product_id 产品id(必填)
	 * @param type_attr 10,12 属性值(必填)
	 * @return data:{id:产品价格id,product_id:产品id,product_number:库存,price:价格}
	 */
	public void getPrice() {
		String[] params = {"product_id", "type_attr"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
		int productId = getParaToInt("product_id");
		String typeAttr = getPara("type_attr");
		
		ProductPrice price = Merchant.getPrice(productId, typeAttr);
		
		jsonObject.put("data", price);
		renderJson(jsonObject);
	}
	
	/**
	 * 根据产品id获取产品属性
	 * @param id 产品id(必填)
	 * @return 成功：{error:0,saleAttrs：[{propertyId:属性id，propertyName：属性名，propertyValueList:[{propertyValueId：属性值id，propertyValueName：属性名称，isSelected：是否被选中},...]},...]}
	 */
	public void getAttrs() {
		String[] params = {"id"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
		int id = getParaToInt("id");
		List<Record> commonAttrs = Merchant.getNosalePropertyByProductId(id);
		List<Record> saleAttrs = Merchant.getSalePropertyByProductId(id);

		jsonObject.put("commonAttrs", commonAttrs);
		jsonObject.put("saleAttrs", saleAttrs);
		renderJson(jsonObject);
	}
	
	/**
     * 获取产品评价列表
     * @param id 产品id（必填）
     * @param offset 必填
     * @param length 必填
     * @return 成功：{error: 0, reviews: [{nickName: 会员昵称, headImg: 头像, comments: 评论内容, ratings: 评价等级, created_at: 评论时间, pics: [图片路径, ...]}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void productReview() {
    	String[] params = {"id", "offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("id");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	List<Record> data = Member.findCommentItems(offset, length, productId, null, null, null, null, null, 
    			null, null);
    	int totalRow = Member.countCommentItems(productId, null, null, null, null, null, null, null);
    	
    	//评价率
    	Map<String, Integer> ratings = Member.getProductRatings(productId);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("reviews", data);
    	jsonObject.put("rangs", ratings);
    	renderJson(jsonObject);
    }
    
    /**
     * 收藏商品
     * @param token(必填)
     * @param productId 产品id(必填)
     * @return 成功：{error:0，error:-1收藏失败}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void collection() {
    	String[] params = {"productId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("productId");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = CollectionService.collectProduct(customerId, productId);
    	
    	if (code == ServiceCode.Validation) {
    		setError(-1, "已收藏");
    	} else if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "收藏失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 加入购物车
     * @param id 产品id（必填）
     * @param priceId 价格id（必填）
     * @param amount 数量（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void addCart() {
    	String[] params = {"id", "priceId", "amount"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("id");
    	int priceId = getParaToInt("priceId");
    	int amount = getParaToInt("amount");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = Member.addShoppingCart(customerId, productId, priceId, amount);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "加入购物车失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 我的购物车
     *  @param token 帐户访问口令
     *  @return 成功：{error: 0, error:1-(还没登录), totalPrice:总金额, totalAmount:总购买数量, data:[{shop_id:店铺id,shopName:店铺名称,shopLogo:店铺logo,productList:[{id:购物车id,product_id:产品id,productName:产品名称,mainPic:产品主图,selectProterties:所选属性,amount:购买数量,suggestedRetailUnitPrice:产品单价,totalPrice:产品总价,isSelected:是否选中},...]}]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void getMyShoppingCart() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = Member.findShoppingCartItems(customerId, null, null);
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, 1);
        
        double totalProdPrice = Member.calculateProductTotalPayable(products);
        double totalAmount = Member.calculateProductTotalAmount(products);
        double promDiscount = BasePromotion.calculateDiscount(products);
        double totalPrice = totalProdPrice - promDiscount;
    	
    	jsonObject.put("totalPrice", totalPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("promDiscount", promDiscount);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
}