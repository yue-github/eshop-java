package com.eshop.controller.webapp;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.collection.CollectionService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 商品收藏控制器
 * @author TangYiFeng
 */
public class ProductCollectionsController extends WebappBaseController {

    /**
     * 构造方法
     */
    public ProductCollectionsController() {
    }

    /**
     * 我的产品收藏列表
     * @param token（必填）
     * @param offset 必填
     * @param length 必填
     * @return 成功：{error:0,totalPages:总页数，data:[{id:id, name:产品名称，mainPic:产品主图,suggestedRetailUnitPrice:价格},...]}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void myProductCollections() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> data = CollectionService.findCollectionItems(offset, length, CollectionService.PRODUCT, 
    			customerId, null, null, null, null);
    	
    	int totalRow = CollectionService.countCollectionItems(CollectionService.PRODUCT, customerId, null, 
    			null, null, null);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }
    
    /**
     * 取消产品收藏
     * @param token(必填)
     * @param ids [1,2,3,...](必填)
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void cancelProductCollection() {
    	String[] params = {"ids"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<Integer> ids = JSON.parseArray(idsStr, Integer.class);
    	ServiceCode code = CollectionService.cancelCollect(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "取消收藏失败");
    	}
    	
    	renderJson(jsonObject);
    }
    @Before(CustomerWebAppAuthInterceptor.class)
    public void Cancelcollection(){

    	String token = getPara("token");
    	Customer customer =(Customer)CacheHelper.get(token);

    	if(customer ==null){
			returnError(-1, "还未登陆");
			return;
		}

		Integer customerId = customer.getId();
    	Integer productId = getParaToIntDefault("productId");
    	Integer serviceCode = CollectionService.cancelCollect(customerId,productId);
		if (serviceCode <0) {
			setError(ErrorCode.Exception, "取消收藏失败");
		}
		renderJson(jsonObject);
	}
    /**
     * 是否已收藏该产品
     * @param token
     * @param productId
     * @return {error:0,error:-1(还未登录),error:-2(已收藏),-3(未收藏)} 失败：{error:>0,errmsg:错误信息}
     */
    public void isCollect() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	
    	if (customer == null) {
    		returnError(-1, "还未登陆");
    		return;
    	}
    	
    	int customerId = customer.getId();
    	int productId = getParaToInt("productId");
    	
    	boolean isCollect = CollectionService.isCollect(customerId, productId, CollectionService.PRODUCT);
    	
    	if (isCollect) {
    		setError(-2, "已收藏");
    	} else {
    		setError(-3, "未收藏");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 收藏产品
     * @param token(必填)
     * @param productId 店铺id(必填)
     * @return 成功：{error:0,error:-1(已收藏)} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void collectProduct() {
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
    
}