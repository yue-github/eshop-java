package com.eshop.controller.pc;

import java.math.BigInteger;
import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.membership.CustomerPointService;
import com.eshop.model.Customer;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 会员积分控制器
 * @author TangYiFeng
 */
public class CustomerPointController extends PcBaseController {

    /**
     * Default constructor
     */
    public CustomerPointController() {
    }

    /**
     * 积分明细
     * @param token 账户访问口令(必填)
     * @param offset 当前页码(必填)
     * @param length
     * @param customerId 会员id
     * @return 成功：{error:0, totalPages:总页数， data[{id:id, amount:积分数量, note：备注, source:来源（整数）, created_at：日期},...]}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void many() {
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = CustomerPointService.findCustomerPointItems(offset, length, customerId, null, null, null, null, null, null);
    	int total = CustomerPointService.countCustomerPointItems(customerId, null, null, null, null, null, null);

    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 我的总积分
     * @param token
     * @return 成功：{error:0, data:{totalPoint:总积分}} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void myPoint() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);

    	Record totalPoint = CustomerPointService.countToPoints(customer.getId());
    	HashMap<String,Integer>hashMap = new HashMap<String,Integer>();
    	hashMap.put("pointAmount",totalPoint.getInt("points"));
		jsonObject.put("data",hashMap);
		renderJson(jsonObject);
    }

}