package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.wallet.Card;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 *   银行卡控制器
 *   @author TangYiFeng
 */
public class BankcardsController extends WebappBaseController {

    /**
     * 我的银行卡
     *  @param token 帐户访问口令（必填）
     *  @param offset 必填
     *  @param length 必填
     *  @return 成功：{error: 0, data:[{id:id, accountNumber:卡号, bankBranch:银行卡名称}]}；失败：{error: >0, errmsg: 错误信息}
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
    	
    	List<Record> list = Card.findCardItems(offset, length, customerId, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	renderJson(jsonObject);
    }

}