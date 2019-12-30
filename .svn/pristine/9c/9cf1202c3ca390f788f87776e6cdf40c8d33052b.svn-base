package com.eshop.controller.webapp;

import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 *   物流详情控制器
 *   @author TangYiFeng
 */
public class LogisticsDetailsController extends WebappBaseController {

    /**
     * 获取物流详情
     * @param token
     * @param orderId 订单id
     * @return 成功：{error:0,logisticsName:物流公司,expressCode:快递,detail:[{title:标题,time:时间},...]}
     */
	@Before(CustomerWebAppAuthInterceptor.class)
    public void logisticsDetail() {
    	if (!this.validateRequiredString("orderId")) {
    		return;
    	}
    	
    	int orderId = getParaToInt("orderId");
    	Record result = User.getExpressDetail(orderId);
    	
    	jsonObject.put("logisticsName", result.get("logisticsName"));
    	jsonObject.put("expressCode", result.get("expressCode"));
    	jsonObject.put("detail", result.get("list"));
    	renderJson(jsonObject);
    }

}