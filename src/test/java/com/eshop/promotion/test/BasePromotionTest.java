package com.eshop.promotion.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Product;
import com.eshop.model.Promotion;
import com.eshop.promotion.BasePromotion;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class BasePromotionTest extends ControllerTestCase<TestProjectConfig> {
	
	private BasePromotion basePromotion = new BasePromotion();
	
	/**
	 * 查看促销活动基础信息
     */
	@Test
	public void testGetPromotion() {
		Promotion count = BasePromotion.getPromotion(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 找出产品参与了哪些促销活动
     * 算法：1、先找出所有促销活动；2、遍历集合，找出符合条件的促销活动；
     * @param productId
     */
	@Test
	public void testGeProductPromSlogans() {
//		Promotion count = (Promotion) basePromotion.geProductPromSlogans(1); //???
		List<Promotion> count = BasePromotion.geProductPromSlogans(1);
		System.out.println(count);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 查看促销活动关联产品
     * @param promotionId
     */
	@Test
	public void testGetPromotProducts() {
//		Promotion count = (Promotion) basePromotion.getPromotProducts(1);
		List<Product> count = BasePromotion.getPromotProducts(0, 2, 86);
		System.out.println(count);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 购物车列表，每个店铺满足条件的促销活动
     * @param shopId
     * @param products
     * @return [{id:1,title:促销活动标题,isMeet:false,discount:33},...]；isMeet表示金额是否满足，如果满足
     * 则不需要凑单，不满足则需要凑单；discount表示优惠金额
     */
	@Test
	public void testGetShopCartPromotion() {
		Promotion count = (Promotion) BasePromotion.getShopCartPromotion(1, new ArrayList<Record>(1));
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
