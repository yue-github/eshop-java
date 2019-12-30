package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.collection.CollectionService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 店铺收藏控制器
 * @author TangYiFeng
 */
public class ShopCollectionsController extends WebappBaseController {

    /**
     * 构造方法
     */
    public ShopCollectionsController() {
    }

    /**
     * 我的店铺收藏列表
     * @param token(必填)
     * @param offset
     * @param length
     * @return 成功：{error:0, totalPages:总页数， data:[{id:id, name:店铺名称, logoPic:店铺logo},...]} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void myShopCollections() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> data = CollectionService.findCollectionItems(offset, length, CollectionService.SHOP, 
    			customerId, null, null, null, null);
    	int totalRow = CollectionService.countCollectionItems(CollectionService.SHOP, customerId, null, null, 
    			null, null);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }
    
    /**
     * 取消店铺收藏
     * @param token(必填)
     * @param id 收藏id(必填)
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void cancleShopCollection() {
    	String[] params = {"id"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = CollectionService.cancelCollect(id);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "取消收藏失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 是否已收藏该店铺
     * @param token
     * @param shopId
     * @return {error:0,error:-1(还未登录),error:-2(已收藏),-3(未收藏)} 失败：{error:>0,errmsg:错误信息}
     */
    public void isCollect() {
    	String[] params = {"shopId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	boolean isCollect = CollectionService.isCollect(customerId, shopId, CollectionService.SHOP);
    	
    	if (isCollect) {
    		setError(-2, "已收藏");
    	} else {
    		setError(-3, "未收藏");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 取消店铺收藏
     * @param token(必填)
     * @param shopId 店铺id(必填)
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void cancelShopByShopId() {
    	String[] params = {"shopId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = CollectionService.cancelCollect(CollectionService.SHOP, shopId, customerId);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "取消收藏失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 店铺收藏
     * @param token(必填)
     * @param shopId 店铺id(必填)
     * @return 成功：{error:0,error:-1(已收藏)} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void collectShop() {
    	String[] params = {"shopId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	int shopId = getParaToInt("shopId");
    	ServiceCode code = CollectionService.collectShop(customerId, shopId);
    	
    	if (code == ServiceCode.Validation) {
    		returnError(-1, "已收藏");
    		return;
    	}
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "收藏失败");
    	}
    	
    	renderJson(jsonObject);
    }

}