package com.eshop.promotion.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Product;
import com.eshop.model.PromotionMansong;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.ManSong;
import com.jfinal.ext.test.ControllerTestCase;

public class ManSongTest extends ControllerTestCase<TestProjectConfig> {
	
	private ManSong manSong = new ManSong();


    /**
     * 查看满送活动详情
     * @param promotionId
     */
	@Test
	public void testGetPromotionMansong() {
		PromotionMansong count = ManSong.getPromotionMansong(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除满送活动
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = ManSong.delete(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    
    /**
     * 批量查询满送活动的总数量
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
     */
	@Test
	public void testCountPromotionItems() {
		int count = ManSong.countPromotionItems("1", "1", "1", "1", "1", "1", 1, 1, 1, 0.1, 0.1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    
    /**
     * 获取赠品
     * @param promotionId
     */
//	@Test
//	public void testGetGifts() {
//		List<Product> count = ManSong.getGifts(1);
//		boolean flag = count != null;
//		assertEquals(false, flag);
//	}
	   
    /**
     * 赠品已被领取数量
     * @param promotionId
     * @param productId
     * @param priceId
     */
	@Test
	public void testUsedAmount() {
		int count = ManSong.usedAmount(1, 1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 赠品剩余数量
     * @param promotionId
     * @param productId
     * @param priceI
     */
	@Test
	public void testRemainAmount() {
		int count = ManSong.remainAmount(1, 1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 赠品是否已被领完
     * @param promotionId
     * @param productId
     * @param priceId
     */
	@Test
	public void testGiftIsEmpty() {
		boolean count = ManSong.giftIsEmpty(1, 1, 1);
		assertEquals(false, count);
	}

}
