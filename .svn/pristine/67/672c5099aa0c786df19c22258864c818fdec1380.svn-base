package com.eshop.controller.pc;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.jfinal.aop.Before;

/**
 * 会员金币控制器
 * @author TangYiFeng
 */
public class CustomerGradeController extends PcBaseController {
	
	public CustomerGradeController() {}
	
	/**
	 * 我的等级
     * @param token 账户访问口令(必填)
     * @return 成功：{error:0, myGrade:等级名称}  失败：{error:>0, errmsg:错误信息}
     */
	@Before(CustomerPcAuthInterceptor.class)
	public void myGrade() {
		
		if (!this.validateRequiredString("token")) {
			return;
		}
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		
		String myGrade = customer.getGrade();
		
		jsonObject.put("myGrade", myGrade);
		renderJson(jsonObject);
	}
	
}
