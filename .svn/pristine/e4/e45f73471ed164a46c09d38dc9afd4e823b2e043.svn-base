package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.wallet.Recharge;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 充值明细控制器
 * @author TangYiFeng
 */
public class RechargeDetailsController extends WebappBaseController {

    /**
     * 构造方法
     */
    public RechargeDetailsController() {
    }

    /**
     * 获取充值记录
     * @param token 帐户访问口令（必填）
     * @param offset
     * @param length
     * @return 成功：{error: 0, totalPages: 结果总数, data:[{id:id, event:类型, money:金额, created_at:充值时间, finishTime:完成时间}]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> data = Recharge.findRechargeItems(offset, length, customerId, orderByMap);
    	int totalRow = Recharge.countRechargeItems(customerId);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }
}