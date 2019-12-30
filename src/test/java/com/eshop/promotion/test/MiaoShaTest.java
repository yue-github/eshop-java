package com.eshop.promotion.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionMiaosha;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.MiaoSha;
import com.jfinal.ext.test.ControllerTestCase;

public class MiaoShaTest extends ControllerTestCase<TestProjectConfig> {
	
	private MiaoSha miaoSha = new MiaoSha();

    /**
     * 查看秒杀活动详情
     * @param id
     */
	@Test
	public void testGetPromotionMiaosha() {
		PromotionMiaosha count = MiaoSha.getPromotionMiaosha(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除活动
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = MiaoSha.delete(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 批量查询秒杀活动的总数量
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param minLimitAmount
     * @param maxLimitAmount
     */
	@Test
	public void testCountPromotionItems() {
		int count = MiaoSha.countPromotionItems("", "", "", "", "", "", 1, 1, 1, 1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询秒杀活动的总数量
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param minLimitAmount
     * @param maxLimitAmount
     */
	@Test
	public void testFindPromotionItemsSql() {
		int count = MiaoSha.countPromotionItems("", "", "", "", "", "", 1, 1, 1, 1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 计算秒杀价，如果该sku没有参加秒杀活动，则没有秒杀价，返回原价
     * @param productId
     * @param priceId
     */
	@Test
	public void testCalculatePromotionPrice() {
		int count = (int) MiaoSha.calculatePromotionPrice(1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 获取该单品参加了哪个秒杀活动
     * @param productId
     * @param priceId
     */
	@Test
	public void testGetMiaoshaSlogan() {
		Promotion count = MiaoSha.getMiaoshaSlogan(1, 1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
