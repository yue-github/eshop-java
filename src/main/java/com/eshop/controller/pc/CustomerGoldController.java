package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.membership.CustomerGoldService;
import com.eshop.model.Customer;
import com.eshop.model.CustomerGold;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 会员金币控制器
 * @author TangYiFeng
 */
public class CustomerGoldController extends PcBaseController {
	
    /**
     * Default constructor
     */
    public CustomerGoldController() {
    }

    /**
     * 我的金币数量
     * @param token 账户访问口令(必填)
     * @return 成功：{error:0, goldAmount:金币数量}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void goldAmount() {
   		String token = getPara("token");
  		Customer customer = (Customer) CacheHelper.get(token);

  		Integer  gAmount = Db.queryInt("select golds from customer where id=?",customer.getId());

//  		//int gAmount = customer.getGolds();
//
		HashMap<String,Integer>hashMap = new HashMap<>();
		hashMap.put("goldAmount",gAmount);
    	jsonObject.put("goldAmount", gAmount);
    	renderJson(hashMap);

    }

    /**
     * 我的金币明细
     * @param token
     * @param offset
     * @return 成功：{error:0, totalPages:总页数， data:[{id:id,source:来源，amount:数量, created_at：时间},...]} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void myGoldList() {
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
    	
    	List<Record> list = CustomerGoldService.findCustomerGoldItems(offset, length, customerId, null, null, null, null, null, null);
    	int total = CustomerGoldService.countCustomerGoldItems(customerId, null, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建金币明细信息
     * @param token
     * @param customerId
     * @param amount （获得/扣除金币的数量）
     * @param type (1：获取， 2：扣除)
     * @param source
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void createCustomerGoldInfo() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	
    	String[] params = {"customerId", "amount", "type", "source"};
    	
    	if (!validate(params)) {
			return;
		}
    	
    	Integer customerId = getParaToInt("customerId");
    	Integer amount = getParaToInt("amount");
    	Integer type = getParaToInt("type");
    	Integer source = getParaToInt("source");
    	
    	CustomerGold model = new CustomerGold();
    	model.setCustomerId(customerId);
    	model.setAmount(amount);
    	model.setType(type);
    	model.setSource(source);
    	
    	ServiceCode code = CustomerGoldService.createCoustomerGold(model);
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "添加失败");
    		return;
		}
    	
    	renderJson(jsonObject);
    }
    
    
    
}