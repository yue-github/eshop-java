package com.eshop.promotion.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.PromotionSku;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.PromotionSkuService;
import com.jfinal.ext.test.ControllerTestCase;

public class PromotionSkuServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private PromotionSkuService promotionSkuService = new PromotionSkuService();

    /**
     * 创建促销单品
     * @param model
     */
	@Test
	public void testCreate() {
		PromotionSku model = new PromotionSku();
//		model.set("2", "5");
		model.setProductId(1);
		model.setPriceId(1);
		model.setLimitStock(10);
		model.setPromotionId(166);
		ServiceCode count = PromotionSkuService.create(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 修改促销单品
     * @param model
     */
	@Test
	public void testUpdate() {
		PromotionSku model = new PromotionSku();
		model.set("2", "5");
		ServiceCode count = PromotionSkuService.update(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 查看促销单品详情
     * @param id
     */
	@Test
	public void testGet() {
		PromotionSku count = PromotionSkuService.get(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 删除促销单品
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = PromotionSkuService.delete(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    
    /**
     * 批量查询促销单品的总数量
     * @param productId
     * @param minPromotionPrice
     * @param maxPromotionPrice
     * @param minLimitStock
     * @param maxLimitStock
     * @param promotionId
     * @param productName
     * @param selectProterties
     */
	@Test 
	public void testCountPromotionSku() {
		int count = PromotionSkuService.countPromotionSku(1, 0.1, 0.1, 0.1, 0.1, 1, "", "");
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    

}
