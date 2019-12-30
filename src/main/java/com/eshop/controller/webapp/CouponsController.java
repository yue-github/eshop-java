package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.coupon.CustomerCouponService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 我的折扣券控制器
 * @author TangYiFeng
 */
public class CouponsController extends WebappBaseController {

    /**
     * 获取我的优惠券
     * @param token
     * @param offset
     * @param length
     * @param type
     * @param isTimeOut (0未过期，1已过期)
     * @return 成功：{error: 0, totalPages:总页数,  data: [{id:id, shopName:店铺名称, couponTitle:折扣券标题, minPurchaseAmount:最低购买金额, percentageDiscount:折扣, is_used:是否已使用，depositTime:开始时间, expirationTime:结束时间},...]}  失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void getMyCoupons() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	Integer type = getParaToIntegerDefault("type");
    	Integer isTimeOut = getParaToIntegerDefault("isTimeOut");
    	
    	Customer customer = (Customer) CacheHelper.get( getPara("token"));
    	Integer customerId = customer.getId();
    	
    	List<Record> data = CustomerCouponService.findCustomerCouponItems(offset, length, customerId, isTimeOut, type);
    	int totalRow = CustomerCouponService.countCustomerCouponItems(customerId, isTimeOut, type);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }
    
}