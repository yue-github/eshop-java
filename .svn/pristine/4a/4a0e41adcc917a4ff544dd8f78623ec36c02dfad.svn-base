package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.membership.CustomerGoldService;
import com.eshop.model.Customer;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 我的金币控制器
 * @author TangYiFeng
 */
public class GoldController extends WebappBaseController {

    /**
     * 我的金币数量
     * @param token 账户访问口令(必填)
     * @return 成功：{error:0, goldAmount:金币数量}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void goldAmount() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int goldAmount = customer.getGolds();
    	
    	jsonObject.put("goldAmount", goldAmount);
    	renderJson(jsonObject);
    	
    }
    
    /**
     * 我的金币明细
     * @param token
     * @param offset
     * @param length
     * @return 成功：{error:0, totalPages:总页数， data:[{id:id,source:来源(来源, 1支付订单抵扣)，amount:数量,created_at：时间},...]} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void myGoldList() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> data = CustomerGoldService.findCustomerGoldItems(offset, length, customerId, 
    			null, null, null, null, null, null);
    	int totalRow = CustomerGoldService.countCustomerGoldItems(customerId, null, null, null, 
    			null, null, null);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }

}