package com.eshop.collection.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.collection.CollectionService;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;
import com.eshop.config.TestProjectConfig;

public class CollectionServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private CollectionService collectionService;
	
	public CollectionServiceTest() {
		try {
			ControllerTestCase.start(TestProjectConfig.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		collectionService = new CollectionService();
	}
	
	/**
	 * 取消收藏
	 * @param id
	 */
	@Test
	public void testCancelCollect() {
		ServiceCode count = CollectionService.cancelCollect(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

	/**
	 * 收藏商品
	 * @param customerId
	 * @param productId
	 */
	@Test
	public void testCollectProduct() {
		ServiceCode count = CollectionService.collectProduct(2, 5);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 收藏店铺
	 * @param customerId
	 * @param productId
	 */
	@Test
	public void testCollectShop() {
		ServiceCode count = CollectionService.collectShop(1, 3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 是否已收藏
	 * @param customerId
	 * @param relateId
	 * @param type
	 */
	@Test
	public void testIsCollect() {
		boolean count = CollectionService.isCollect(2, 4, 1);
		
		assertEquals(false, "");
	}

}
