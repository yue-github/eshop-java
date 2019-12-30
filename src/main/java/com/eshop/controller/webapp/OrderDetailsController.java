package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 *   订单详情控制器
 *   @author TangYiFeng
 */
public class OrderDetailsController extends WebappBaseController {

    /**
     * 构造方法
     */
    public OrderDetailsController() {
    }

    /**
     * 获取物流信息
     * @param token
     * @param orderId 订单id
     * @return 成功：{error: 0, data:[{title:标题,time:时间},...]}
     */
    public void getLogisticsInfo() {
    	String[] params = {"orderId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int orderId = getParaToInt("orderId");
    	Record result = User.getExpressDetail(orderId);
    	
    	jsonObject.put("data", result.get("list"));
    	renderJson(jsonObject);
    }

    /**
     * 获取订单详情
     * @param token 帐户访问口令（必填）
     * @param id 订单id
     * @return 成功：{error: 0, order: {id:id,preferredContactPhone:收货人手机,receiveName:收货人姓名,receiveProvince:收货省,receiveCity:收货市,receiveDistrict:收货区,receiveDetailAddress:收货详细地址,shopName:店铺名称,status:订单状态(1: 待付款,2: 待发货,3: 待收货,4: 待评价,5: 已评价,6: 已取消, 7订单完成),deliveryPrice:运费,totalPayable:总金额(包含运费),order_code:订单编号,orderTime:订单创建时间,payType:支付方式,couponTitle:优惠券标题,details:[{id:产品id,name:产品名称,summary:产品摘要,actualUnitPrice:价格,selectProterties:所选属性,amount:数量,mainPic:产品主图},...]}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record order = Db.findById("order", id);
    	List<Record> productOrders = User.findProductOrderItems(id, null, null);
    	order.set("details", productOrders);
    	
    	jsonObject.put("order", order);
    	renderJson(jsonObject);
    }

}