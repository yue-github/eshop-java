package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.membership.CustomerPointService;
import com.eshop.model.Customer;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 *   我的积分控制器
 *   @author TangYiFeng
 */
public class PointsController extends WebappBaseController {

    /**
     * 构造方法
     */
    public PointsController() {
    }
    
    /**
     * 签到
     * @param token
     * @return 成功：{error:0,-1:今天已签到} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void sign() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	int code = CustomerPointService.signIn(customerId);
    	
    	if (code == 1) {
    		returnError(-1, "今天已经签到");
    		return;
    	}
    	
    	if (code == 2) {
    		returnError(1, "签到失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 用户积分明细
     * @param token 账户访问口令(必填)
     * @param offset 必填
     * @param length 必填
     * @return 成功：{error:0, totalPages:总页数， data[{id:id, amount:积分数量, note：备注, source:来源（1下订单）, created_at：日期},...]}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void many() {
    	String[] params = {"length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	Integer offset = getParaToInt("offset");
    	if(offset == null) {
    		offset = 0;
    	}
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> data = CustomerPointService.findCustomerPointItems(offset, length, customerId, null, 
    			null, null, null, null, null);
    	
    	int totalRow = CustomerPointService.countCustomerPointItems(customerId, null, null, null, null, 
    			null, null);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }
    
    /**
     * 我的总积分
     * @param token
     * @return 成功：{error:0, data:{totalPoint:总积分}} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void myPoint() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	
    	jsonObject.put("data", customer.getPoints());
    	renderJson(jsonObject);
    }

}