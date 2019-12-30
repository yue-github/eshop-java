package com.eshop.promotion.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.PromotionCondition;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.PromotionConditionService;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class PromotionConditionServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private PromotionConditionService promotionConditionService = new PromotionConditionService();
	
    /**
     * 创建促销条件
     * @param model
     */
	@Test
	public void testCreate() {
		PromotionCondition model = new PromotionCondition();
//		model.set("2", "5");
		model.setPromotionId(166);
		model.setType(2);
		model.setObjectId(1);
		
		ServiceCode count = PromotionConditionService.create(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 修改促销条件
     * @param model
     */
	@Test
	public void testUpdate() {
		PromotionCondition model = new PromotionCondition();
//		model.set("2", "1");
		model.setId(615);
		model.setPromotionId(166);
		model.setType(3);
		model.setObjectId(1);
		ServiceCode count = PromotionConditionService.update(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 查看促销条件详情
     * @param id
     */
	@Test
	public void testGet() {
		PromotionCondition count = PromotionConditionService.get(615);
		System.out.println(count.toString());
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除促销条件
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = PromotionConditionService.delete(544);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询促销条件
     * @param offset
     * @param count
     * @param promotionId
     * @param type
     * @param name
     */
	@Test
	public void testFindPromotionConditionItemsIntIntIntegerIntegerString() {
//		List<Record> count = promotionConditionService.findPromotionConditionItems(10, 5, 1, 1, "jaing");
		List<Record> count = PromotionConditionService.findPromotionConditionItems(0, 3, null, null, null);
		System.out.println(count.toString());
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询促销条件
     * @param promotionId
     * @param type
     * @param name
     */
	@Test
	public void testFindPromotionConditionItemsIntegerIntegerString() {
		List<Record> count = PromotionConditionService.findPromotionConditionItems(166, 3, "cetegoryName");
		System.out.println(count.toString());
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	   
    /**
     * 批量查询促销条件的总数量
     * @param promotionId
     * @param type
     * @param name
     */
	@Test
	public void testCountPromotionConditionItems() {
		int count = PromotionConditionService.countPromotionConditionItems(null, 3, null);
		System.out.println(count);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 组装sql语句
     * @param promotionId
     * @param type
     * @param name
     */
	@Test
	public void testFindPromotionConditionItemsSql() {
		String count = PromotionConditionService.findPromotionConditionItemsSql(1, 1, "jiang");
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
