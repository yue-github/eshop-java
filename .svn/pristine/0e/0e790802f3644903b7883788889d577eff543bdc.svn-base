package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.ShopDecorationModule;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Merchant;
import com.eshop.shopdecoration.ShopDecorationService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 店铺装修控制器
 * @author TangYiFeng
 */
public class ShopDecorationController extends PcBaseController {

    /**
     * Default constructor
     */
    public ShopDecorationController() {
    }

    /**
     * 获取店铺已上架产品
     * @param token
     * @param offset
     * @param length
     * @return 成功：{error:0, data:[{id:产品id,name:产品名称,summary:产品摘要,suggestedRetailUnitPrice:产品单价,mainPic:产品主图},...]}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getOnShelfProduct() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int isSale = 1;
    	int isDeleted = 0;
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, null, null, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	int total = Merchant.countProductItems(shopId, null, null, null, isSale, isDeleted, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 保存店铺装修样式
     * @param token
     * @param attr json字符串
     * @return 成功：{error:0} 失败：{error:>0}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void saveShopDecoration() {
    	String[] params = {"attr"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String attr = getPara("attr");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	ShopDecorationModule model = new ShopDecorationModule();
    	model.setShopId(shopId);
    	model.setAttr(attr);
    	
    	ServiceCode code = ShopDecorationService.createShopDecorationModule(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改店铺装修样式
     * @param token
     * @param attr json字符串
     * @return 成功：{error:0} 失败：{error:>0}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void updateShopDecoration() {
    	String[] params = {"attr"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String attr = getPara("attr");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	ShopDecorationModule model = ShopDecorationService.getShopDecorationModuleByShopId(shopId);
    	model.setAttr(attr);
    	
    	ServiceCode code = ShopDecorationService.updateShopDecorationModule(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取店铺装修样式
     * @param shopId
     * @return 成功：{error:0, data:{shop_id:店铺id,attr:json值}} 失败：{error:>0}
     */
    public void getShopDecoration() {
    	String[] params = {"shopId"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	
    	ShopDecorationModule model = ShopDecorationService.getShopDecorationModuleByShopId(shopId);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
}