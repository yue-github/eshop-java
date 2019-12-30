package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.collection.CollectionService;
import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 店铺首页控制器
 * @author TangYiFeng
 */
public class ShopHomeController extends WebappBaseController {

    /**
     * 构造方法
     */
    public ShopHomeController() {
    }

    /**
     * 店内搜索产品
     * @param keyName 产品名称关键字
     * @param shopId 店铺id 必填
     * @param offset 必填
     * @param length 必填
     * @return 成功：{error:0,totalPage:总页数,totalRow:总行数,data:[{id:产品id,mainPic:产品主图,name:产品名称,summary:产品摘要,suggestedRetailUnitPrice:单价,shopName:店铺名称},...]}
     */
    public void searchProduct() {
    	String[] params = {"offset", "length", "shopId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int shopId = getParaToInt("shopId");
    	int isSale = 1;
    	int isDeleted = 0;
    	String keyName = getPara("keyName");
    	String priceSort = getPara("priceSort") != null ? getPara("priceSort") : "asc";
    	String salesVolume = getPara("salesVolume");
    	String commentNum = getPara("commentNum");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("suggestedRetailUnitPrice", priceSort);
    	if(salesVolume != null && !"".equals(salesVolume)) {
    		orderByMap.put("salesVolume", salesVolume);
    	}
    	if(commentNum != null && !"".equals(commentNum)) {
    		orderByMap.put("commentNum", commentNum);
    	}
    	
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, keyName, null, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	int total = Merchant.countProductItems(shopId, keyName, null, null, isSale, isDeleted, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 关注该店铺
     * @param token
     * @param shopId 店铺id
     * @return 成功：{error:0,error:-1(该店铺已经收藏)} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void collectShop() {
    	String[] params = {"shopId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	
    	String token = (getPara("token") != null) ? getPara("token") : "";
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = CollectionService.collectShop(customerId, shopId);
    	
    	if (code == ServiceCode.Validation) {
    		returnError(-1, "该店铺已经收藏");
    		return;
    	}
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "收藏店铺失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取店铺信息
     * @param shopId 店铺id
     * @return 成功：{error:0,data:{id:id, name: 店铺名称, logoPic: 店铺图片,sales:销量数量,collect:收藏数量,backs：退货退款数量}} 失败：{error:>0,errmsg:错误信息}
     */
    public void shopInfo() {
    	String[] params = {"shopId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	Shop shop = Merchant.getShop(shopId);
    	
    	if (shop == null) {
    		returnError(ErrorCode.Exception, "店铺不存在");
        	return;
    	}
    	
    	String logoPic = ResourceService.getPath(shop.getLogoPic());
    	shop.set("logoPic", logoPic);
    	
    	int collect = CollectionService.countCollectionItems(CollectionService.SHOP, null, null, null, null, shopId);
    	shop.put("collect", collect);
    	
    	Record saleRecord = Merchant.calculateShopSales(shopId);
    	shop.put("sales", saleRecord.getInt("totalSaleAmount"));
    	
    	Record backRecord = Merchant.countRefundAndBack(shopId);
    	int backs = backRecord.getInt("backs") + backRecord.getInt("refunds");
    	shop.put("backs", backs);
    	
    	jsonObject.put("data", shop);
    	renderJson(jsonObject);
    }
    
    /**
     * 店铺所有已上架商品
     * @param shopId 店铺id
     * @param offset
     * @param length
     * @param priceSort 价格排序(desc降序，asc升序) 选填
     * @param salesVolume 销量排序 选填
     * @param commentNum 评论数排序 选填
     * @return 成功：{error:0,totalPage:总页数,totalRow:总行数,data:[{id:产品id,mainPic:产品主图,name:产品名称,summary:摘要,suggestedRetailUnitPrice:价格,shopName:店铺名称},...]} 失败:{error:>0, errmsg:错误信息}
     */
    public void onShelfProduct() {
    	String[] params = {"shopId", "offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int isSale = 1;
    	int isDeleted = 0;
    	
    	String priceSort = getPara("priceSort") != null ? getPara("priceSort") : "asc";
    	String salesVolume = getPara("salesVolume");
    	String commentNum = getPara("commentNum");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("suggestedRetailUnitPrice", priceSort);
    	if(salesVolume != null && !"".equals(salesVolume)) {
    		orderByMap.put("salesVolume", salesVolume);
    	}
    	if(commentNum != null && !"".equals(commentNum)) {
    		orderByMap.put("commentNum", commentNum);
    	}
    	
    	List<Record> data = Merchant.findProductItems(offset, length, shopId, null, null, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	int totalRow = Merchant.countProductItems(shopId, null, null, null, isSale, isDeleted, null, null, null, null, null);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取店铺最新产品
     * @param shopId 店铺id
     * @param offset
     * @param length
     * @return 成功：{error:0,totalPage:总页数,totalRow:总行数,data:[{id:产品id,mainPic:产品主图,name:产品名称,summary:摘要,suggestedRetailUnitPrice:价格,shopName:店铺名称},...]} 失败:{error:>0, errmsg:错误信息}
     */
    public void shopLastProduct() {
    	String[] params = {"shopId", "offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int isSale = 1;
    	int isDeleted = 0;
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> data = Merchant.findProductItems(offset, length, shopId, null, null, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	int totalRow = Merchant.countProductItems(shopId, null, null, null, isSale, isDeleted, null, null, null, null, null);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }
    
    /**
     * 店铺优惠券列表
     * @param shopId 店铺id
     * @return 成功：{error:0, data:[{id:优惠券id,type:优惠券类型(1优惠券，2现金券),title:优惠券标题,discount:优惠金额}]}   失败:{error:>0, errmsg:错误信息}
     */
    public void couponList() {
    	
    }

}