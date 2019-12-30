package com.eshop.task;

import java.util.Date;
import java.util.List;

import com.eshop.config.ITask;
import com.eshop.model.Order;
import com.eshop.service.Member;

public class CancelOrderTask implements ITask {
	
    @Override
	public void run() {
        //如果订单24小时内还没付款则取消订单，取消订单的同时加回库存
    	Date now = new Date();
    	long expireTime = 3 * 24 * 60 * 60 * 1000;
    	
	    List<Order> orders = Order.dao.find("select * from `order` where status = ?", 1);
	    
	    for (Order order : orders) {
	    	Date orderTime = order.getCreatedAt();
	    	long interval = now.getTime() - orderTime.getTime();
	    	
	    	//时间已到,取消订单
	    	if (interval >= expireTime) {
	    		int orderId = order.getId();
	    		Member.cancel(orderId, "", "");
	    	}
	    }
	    //System.out.println("定时查询订单是否已付款");
    }
   
    @Override
	public void stop() {
        //这里的代码会在 task 被关闭前调用
    }
	
}