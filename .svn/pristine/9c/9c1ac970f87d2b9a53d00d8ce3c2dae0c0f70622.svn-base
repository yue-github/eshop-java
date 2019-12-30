package com.eshop.controller.pc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Product;
import com.eshop.model.Promotion;
import com.eshop.promotion.BasePromotion;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 促销活动控制器
 * @author TangYiFeng
 */
public class PromotionController extends PcBaseController {

    /**
     * Default constructor
     */
    public PromotionController() {
    }
    
    /**
     * 查看产品适用的促销活动
     * @param productId
     * @return 成功：{error:0, data:[]} 失败：{error:>0, errmsg:错误信息}
     */
    public void productPromotions() {
    	String[] params = {"productId"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("productId");
    	
    	List<Promotion> list = BasePromotion.geProductPromSlogans(productId);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 查看促销活动对应的产品
     * @param offset
     * @param length
     * @param promotionId
     * @return 成功：{error:0, data:[]} 失败：{error:>0, errmsg:错误信息}
     */
    public void promotionProducts() {
    	String[] params = {"offset", "length", "promotionId"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int promotionId = getParaToInt("promotionId");
    	
    	List<Product> list = BasePromotion.getPromotProducts(offset, length, promotionId);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 计算优惠金额
     * @param products [{product_id:1, price_id:4, amount:2, price:5, shop_id:3},...]
     * @return 成功：{error:0, discount:22} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void discount() {
    	String[] params = {"products"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String products = getPara("products");
    	List<Map> map = JSON.parseArray(products, Map.class);
    	List<Record> records = new ArrayList<>();
    	for(Map o: map) {
    		Record record = new Record();
    		record.setColumns(o);
    		records.add(record);
    	}
    	
    	double discount = BasePromotion.calculateDiscount(records);
    	
    	jsonObject.put("discount", discount);
    	renderJson(jsonObject);
    }
    
    public void promotions() {
    	List<Promotion> list = BasePromotion.getPromotions();
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}