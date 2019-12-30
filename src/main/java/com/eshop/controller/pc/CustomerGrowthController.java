package com.eshop.controller.pc;

import java.util.List;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.membership.CustomerGrowthService;
import com.eshop.model.Customer;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 我的成长值
 * @author TangYiFeng
 *
 */
public class CustomerGrowthController extends PcBaseController {
	
	/**
	 * 我的成长值
     * @param token 账户访问口令(必填)
     * @return 成功：{error:0, growthAmount:成长值}  失败：{error:>0, errmsg:错误信息}
     */
	@Before(CustomerPcAuthInterceptor.class)
	public void growthAmount() {
		
		if (!this.validateRequiredString("token")) {
			return;
		}
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		
		int growthAmount = customer.getGrowths();
		
		jsonObject.put("growthAmount", growthAmount);
		renderJson(jsonObject);
	}
	
	/**
     * 我的成长值明细
     * @param token
     * @param offset
     * @param length
     * @return 成功：{error:0, totalPages:总页数， data:[{id:id,source:来源，amount:数量, created_at：时间},...]} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void myGrowthList() {
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
    	
    	List<Record> list = CustomerGrowthService.findCustomerGrowthItems(offset, length, customerId, null, null, null, null, null, null);
    	int total = CustomerGrowthService.countCustomerGrowthItems(customerId, null, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
}
