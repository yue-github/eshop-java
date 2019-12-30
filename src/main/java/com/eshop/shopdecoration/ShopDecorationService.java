package com.eshop.shopdecoration;

import java.util.*;

import com.eshop.model.ShopDecorationModule;
import com.eshop.model.dao.BaseDao.ServiceCode;

/**
 * 店铺装修
 * @author TangYiFeng
 */
public class ShopDecorationService {
    
    /**
     * 保存店铺装修模块
     * @param model
     * @return code
     */
    public static ServiceCode createShopDecorationModule(ShopDecorationModule model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	if (!model.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 修改店铺装修模块
     * @param model
     * @return code
     */
    public static ServiceCode updateShopDecorationModule(ShopDecorationModule model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	model.setUpdatedAt(new Date());
    	if (!model.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 获取店铺装修模块
     * @param id
     * @retunr model
     */
    public static ShopDecorationModule getShopDecorationModule(int id) {
    	return ShopDecorationModule.dao.findById(id);
    }
    
    /**
     * 获取店铺装修模块
     * @param shopId
     * @retunr model
     */
    public static ShopDecorationModule getShopDecorationModuleByShopId(int shopId) {
    	return ShopDecorationModule.dao.findFirst("select * from shop_decoration_module where shop_id = ?", shopId);
    }
    
}