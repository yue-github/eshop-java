package com.eshop.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.eshop.config.ITask;
import com.eshop.model.Order;
import com.eshop.model.ProductOrder;
import com.eshop.model.ProductReview;
import com.eshop.service.Member;
 
public class CommentOrderTask implements ITask {
	
    @Override
	public void run() {
    	String[] comments = {
			"产品很不错",
			"挺好，挺满意的",
			"下次还会再买",
			"很正品，就是发货有点慢",
			"和我预想的一样"
    	};
    	Date now = new Date();
    	long expireTime = 15 * 24 * 60 * 60 * 1000;
    	
	    List<Order> orders = Order.dao.find("select * from `order` where status = ?", 4);
	    
	    for (Order order : orders) {
	    	Date receiveTime = order.getReceiveTime();
	    	
	    	long interval = 0;
	    	if (receiveTime != null) {
	    		interval = now.getTime() - receiveTime.getTime();
	    	}
	    	
	    	//时间已到
	    	if (interval >= expireTime) {
	    		int orderId = order.getId();
	    		List<ProductOrder> productOrders = ProductOrder.dao.find("select * from product_order where order_id = ? and status = ?", orderId, 1);
	    		
	    		for (ProductOrder item : productOrders) {
	    			// 设置评价时间
	    	    	Date date = order.getReceiveTime();
	    	    	if (date == null) {
	    	    		date = new Date();
	    	    	}
	    	    	Calendar ca = Calendar.getInstance();
	    	    	ca.setTime(date);
	    	    	ca.add(Calendar.DATE, 15);
	    	    	date = ca.getTime();
	    	    	
	    	    	Random rand = new Random();
	    	    	int index = rand.nextInt(comments.length);
	    	    	
	    			ProductReview model = new ProductReview();
	    			model.setProductId(item.getProductId());
	    	    	model.setCustomerId(order.getCustomerId());
	    	    	model.setComments(comments[index]);
	    	    	model.setRatings(5);
	    	    	model.setCreatedAt(date);
	    	    	model.setUpdatedAt(date);
	    	    	
	    	    	List<String> images = new ArrayList<String>();
	    	    	
	    	    	//提交评论
	    	    	Member.submitComment(model, item.getId(), images);
	    		}
	    	}
	    }
	    
    }
   
    @Override
	public void stop() {
        //这里的代码会在 task 被关闭前调用
    }
}