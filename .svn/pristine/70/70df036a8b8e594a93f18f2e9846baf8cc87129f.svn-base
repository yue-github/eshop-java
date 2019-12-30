package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.wallet.WithDraw;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 *   提现明细控制器
 *   @author TangYiFeng
 */
public class WithdrawDetailsController extends WebappBaseController {

    /**
     * 构造方法
     */
    public WithdrawDetailsController() {
    }

    /**
     * 我的提现明细
     * @param token
     * @param offset
     * @param length
     * @return 成功：{error:0, totalPages:总页数,totalRow:总行数,data:[{id:id, money:提现金额, bankName:账号名称, created_at:提现时间, arriveTime:到账时间,status:状态(0提交申请，1通过申请，2拒绝申请，3已转账)},...]} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void withDrawList() {
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
    	
    	List<Record> data = WithDraw.findWithDrawItems(offset, length, customerId, orderByMap);
    	int totalRow = WithDraw.countWithDrawItems(customerId);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }

}