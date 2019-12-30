package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 店铺产品管理控制器
 *   @author TangYiFeng
 */
public class ShopProductController extends PcBaseController {
     
    /**
     * Default constructor
     */
    public ShopProductController() {
    }

    /**
     * 已下架的产品
     * @param token（必填）
     * @param offset（必填）
     * @param length（必填）
     * @param categoryName 分类名称
     * @param name 产品名称
     * @return 成功：{error: 0, totalPages:总页数， data: [{id:id, name: 产品名称, suggestedRetailUnitPrice: 价格, mainPic: 图片},...]；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getProductsOffShelf() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String cateName = getPara("categoryName");
    	String productName = getPara("name");
    	String supplierName = getPara("supplierName");
    	Integer isSale = 0;
    	Integer isDeleted = 0;
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, productName, null, cateName, isSale, isDeleted, null, null, null, supplierName, "(0,1,2)", orderByMap);
    	int total = Merchant.countProductItems(shopId, productName, null, cateName, isSale, isDeleted, null, null, null, supplierName, "(0,1,2)");
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 已上架的产品 
     * @param offset（必填）
     * @param length（必填）
     * @param categoryName 分类名称
     * @param name 产品名称
     * @return 成功：{error: 0, totalPages:总页数， data: [{id:id, name: 产品名称, suggestedRetailUnitPrice: 价格, mainPic: 图片},...]；失败：{error: >0, errmsg: 错误信息}
     */
    public void getProducts() {
    	String[] params = {"offset", "length", "shopId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String cateName = getPara("categoryName");
    	String productName = getPara("name");
    	String supplierName = getPara("supplierName");
    	int isSale = 1;
    	int isDeleted = 0;
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, productName, null, cateName, isSale, isDeleted, null, null, null, supplierName, null, orderByMap);
    	int total = Merchant.countProductItems(shopId, productName, null, cateName, isSale, isDeleted, null, null, null, supplierName, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 已上架的产品
     * @param token（必填）
     * @param offset（必填）
     * @param length（必填）
     * @param categoryName 分类名称
     * @param name 产品名称
     * @param supplierName 供应商名称
     * @return 成功：{error: 0, totalPages:总页数， data: [{id:id, name: 产品名称, suggestedRetailUnitPrice: 价格, mainPic: 图片},...]；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getProductsOnShelf() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String cateName = getPara("categoryName");
    	String productName = getPara("productName");
    	String supplierName = getPara("supplierName");
    	int isSale = 1;
    	int isDeleted = 0;
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, productName, null, cateName, isSale, isDeleted, null, null, null, supplierName, "(0,1,2)", orderByMap);
    	int total = Merchant.countProductItems(shopId, productName, null, cateName, isSale, isDeleted, null, null, null, supplierName, "(0,1,2)");
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
}