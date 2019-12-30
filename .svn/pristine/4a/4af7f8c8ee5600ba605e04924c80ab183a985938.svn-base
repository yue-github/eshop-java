package com.eshop.promotion.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.PromotionBaoyou;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BaoYou;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class BaoYouTest extends ControllerTestCase<TestProjectConfig> {
	
	private BaoYou baoYou = new BaoYou();

    /**
     * 查看包邮活动详情
     * @param promotionId
     */
	@Test
	public void testGetPromotionBaoyou() {
		PromotionBaoyou count = BaoYou.getPromotionBaoyou(11);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除包邮活动
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = BaoYou.delete(175);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 批量查询包邮活动的总数量
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param full
     * @param minFull
     * @param maxFull
     */
	@Test
	public void testCountPromotionItems() {
		int count = BaoYou.countPromotionItems("1", "1", "1", "1", "1", "1", 1, 1, 1, 0.1, 0.1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 是否满足全场包邮
     * @param products [{product_id:1,amount:3,price:8},...] 整个订单的产品列表
     */
	@Test
	public void testIsMeetAllFree() {
		boolean count = BaoYou.isMeetAllFree(new ArrayList<Record>(1));
		assertEquals(false, count);
	}
    
}
