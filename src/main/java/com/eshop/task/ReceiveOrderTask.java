package com.eshop.task;

import java.util.Date;
import java.util.List;

import com.eshop.config.ITask;
import com.eshop.model.Order;
import com.eshop.service.Member;
 
public class ReceiveOrderTask implements ITask {
	
    @Override
	public void run() {
        //如果10天后没有签收订单，则系统自动签收订单
    	Date now = new Date();
    	long expireTime = 10 * 24 * 60 * 60 * 1000;
    	
	    List<Order> orders = Order.dao.find("select * from `order` where status = ?", 3);
	    
	    for (Order order : orders) {
	    	Date sendOutTime = order.getSendOutTime();
	    	long interval = now.getTime();
	    	
	    	if (sendOutTime != null)
	    		interval = now.getTime() - sendOutTime.getTime();
	    	
	    	//时间已到
	    	if (interval >= expireTime) {
	    		//判断该订单是否有效
	    		int count = Order.dao.find("select * from `product_order` where status = ?", 1).size();
	    		if (count > 0) {
	    			Member.confirm(order.getId());
	    		}
	    	}
	    }
	    System.out.println("定时签收订单");
    }
   
    @Override
	public void stop() {
        //这里的代码会在 task 被关闭前调用
    }
}