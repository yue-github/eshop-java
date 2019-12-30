package com.eshop.controller.pc;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;

/**
 * 我的宝贝控制器
 *   @author TangYiFeng
 */
public class MyProductsController extends PcBaseController {

    /**
     * Default constructor
     */
    public MyProductsController() {
    }

    /**
     * 批量删除宝贝
     * @param token 帐户访问口令（必填）
     * @param ids 宝贝ids数组
     * @return 成功  {error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void delete(){
    	if(!this.validateRequiredString("ids")) {
    		return;
    	}
    	String idsStr = getPara("ids");
    	
    	String token = getPara("token");
    	
    	HashSet<String> set = new HashSet();
    	set.add(idsStr);
    	set.add(token);
    	String matche_str = "'|\"|>|..|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|script|frame|;|or|-|+|,|)|etc|style|expression";
    	String[] strArr = matche_str.split("\\|");
    	for (int i=0 ; i < strArr.length ; i++ ){
    		if (set.add(strArr[i])) {
    			System.out.println(true + "不存在");
    		} else {
    			System.out.println(false + "存在");
    			return;
    		}
		}
    	Customer customer = (Customer)CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	if(shopId == 0){
    		jsonObject.put("error", "店铺不存在");
        	renderJson(jsonObject);
        	return;
    	}
    	
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	ServiceCode code = Merchant.deleteProduct(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "批量删除产品失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 产品上架
     * @param token 帐户访问口令（必填）
     * @param ids 宝贝ids
     * @return 成功  {error: 0 product:{.....}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void onShelf(){
    	if(!this.validateRequiredString("ids")) {
    		return;
    	}
    	String idsStr = getPara("ids");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	if(shopId == 0){
    		setError(ErrorCode.Exception, "店铺不存在");
        	renderJson(jsonObject);
        	return;
    	}
    	
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	ServiceCode code = Merchant.onShelf(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "批量删除产品失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 下架产品
     * @param token 帐户访问口令（必填）
     * @param ids 宝贝ids
     * @return 成功  {error: 0 product:{.....}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void offShelf(){
    	if(!this.validateRequiredString("ids")) {
    		return;
    	}
    	String idsStr = getPara("ids");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	if(shopId == 0){
    		setError(ErrorCode.Exception, "店铺不存在");
        	renderJson(jsonObject);
        	return;
    	}
    	
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	ServiceCode code = Merchant.offShelf(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "批量删除产品失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}