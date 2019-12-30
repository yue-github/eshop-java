package com.eshop.tax.test;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.service.Member;
import com.eshop.tax.ProductTax;
import com.jfinal.ext.test.ControllerTestCase;

public class ProductTaxTest extends ControllerTestCase<TestProjectConfig> {
	
	private ProductTax productTax = new ProductTax();
/*
	*//**
     * 获取某一条税率
     * @param id
     *//*
	@Test
	public void testGet() {
		Tax count = productTax.get(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    
    *//**
     * 批量查询税率
     * @param offset
     * @param count
     * @param name
     * @param enable
     *//*
	@Test
	public void testFindTaxItems() {
		List<Record> count = productTax.findTaxItems(10, 5, "jiang", 5);
		boolean flag = count != null;
		assertEquals(true, flag);
	}
    
    *//**
     * 批量查找税率的总数量
     * @param name
     * @param enable
     *//*
	@Test
	public void testCountTaxItems() {
		int count = productTax.countTaxItems("quan", 2);
		boolean flag = count > 0;
		assertEquals(true, flag);
	}
    
    *//**
     * 创建税率
     * @param model
     *//*
	@Test
	public void testCreate() {
		Tax model = new Tax();
		model.set("2", "5");
		ServiceCode code = productTax.create(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
	   
    *//**
     * 修改税率
     * @param model
     *//*
	@Test
	public void testUpdate() {
		Tax model = new Tax();
		model.set("2", "5");
		ServiceCode code = productTax.update(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
	
    *//**
     * 删除
     * 算法：如果该税率已经被产品使用，则不能删除
     * @param id
     *//*
	@Test
	public void testDelete() {
		ServiceCode count = productTax.delete(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}*/
	@Test
	public void testCOmment() {
	/*	Date now = new Date();
    	long expireTime = 15 * 24 * 60 * 60 * 1000;
    	
	    List<Order> orders = Order.dao.find("select * from `order` where status = ?", 4);
	    
	    for (Order order : orders) {
	    	Date receiveTime = order.getReceiveTime();
	    	
	    	long interval = now.getTime();
	    	if (receiveTime != null) {
	    		interval = now.getTime() - receiveTime.getTime();
	    	}
	    	
	    	//时间已到
	    	if (interval >= expireTime) {
	    		int orderId = order.getId();
	    		List<ProductOrder> productOrders = ProductOrder.dao.find("select * from product_order where order_id = ? and status = ?", orderId, 1);
	    		
	    		for (ProductOrder item : productOrders) {
	    			ProductReview model = new ProductReview();
	    			model.setProductId(item.getProductId());
	    	    	model.setCustomerId(order.getCustomerId());
	    	    	model.setComments("产品很好,好评!");
	    	    	model.setRatings(5);
	    	    	
	    	    	List<String> images = new ArrayList<String>();
	    	    	
	    	    	//提交评论
	    	    	Member.submitComment(item.getProductId(),model, images);
	    		}
	    	}
	    }
	    
	    System.out.println("系统自动评价订单");*/
		Member.updateProduct("1000004768");
		
	}
}
