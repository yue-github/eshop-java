package com.eshop.promotion.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.PromotionManjian;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.Manjian;
import com.jfinal.ext.test.ControllerTestCase;

public class ManjianTest extends ControllerTestCase<TestProjectConfig> {
	
	private Manjian manjian = new Manjian();

    /**
     * 查看满减活动详情
     * @param promotionId
     */
	@Test
	public void testGetPromotionManjian() {
		PromotionManjian count = Manjian.getPromotionManjian(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除满减活动
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = Manjian.delete(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
}
