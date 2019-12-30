package com.eshop.promotion.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.PromotionDazhe;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.ManDaZhe;
import com.jfinal.ext.test.ControllerTestCase;

public class ManDaZheTest extends ControllerTestCase<TestProjectConfig> {
	
	private static final PromotionDazhe PromotionDazhe = null;
	private ManDaZhe manDaZhe = new ManDaZhe();

    /**
     * 查看打折活动详情
     * @param promotionId
     */
	@Test
	public void testGetPromotionDazhe() {
		com.eshop.model.PromotionDazhe count = ManDaZhe.getPromotionDazhe(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除打折活动
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = ManDaZhe.delete(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 批量查询打折活动的总数量
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param minFull
     * @param maxFull
     * @param minValue
     * @param maxValue
     */
	@Test
	public void testCountPromotionItems() {
		int count = ManDaZhe.countPromotionItems("1", "1", "1", "1", "1", "1", 1, 1, 1, 0.1, 0.1, 0.1, 0.1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
