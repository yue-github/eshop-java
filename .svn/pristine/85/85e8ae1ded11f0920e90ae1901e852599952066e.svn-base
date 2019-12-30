package com.eshop.controller.webapp;


import java.util.List;

import com.eshop.coupon.CouponService;
import com.eshop.coupon.CustomerCouponService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 我的现金券控制器
 * @author TangYiFeng
 */
public class CashController extends WebappBaseController {

    /**
     * 获取我的现金券
     * @param token
     * @param offset
     * @param length
     * @param isTimeOut (0未过期，1已过期)
     * @return 成功：{error: 0, totalPages:总页数,  data: [{id:id, shopName:店铺名称, cashTitle:现金券标题, minPurchaseAmount:最低购买金额, cashDiscount:优惠金额, is_used:是否已使用，depositTime:开始时间, expirationTime:结束时间},...]}  失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void getMyCashs() {
    	String[] params = {"length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	Integer offset = getParaToInt("offset");
    	if(offset == null) {
    		offset = 0;
    	}
    	int length = getParaToInt("length");
    	int type = CouponService.COUPON_CASH;
    	Integer isTimeOut = getParaToIntegerDefault("isTimeOut");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> data = CustomerCouponService.findCustomerCouponItems(offset, length, customerId, isTimeOut, type);
    	int totalRow = CustomerCouponService.countCustomerCouponItems(customerId, isTimeOut, type);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }

}