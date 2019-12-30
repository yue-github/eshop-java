package com.eshop.promotion.test;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.jfinal.ext.test.ControllerTestCase;

public class PromotionControllerTest extends ControllerTestCase<TestProjectConfig> {
	
	/**
     * 查看产品适用的促销活动
     * @param productId
     * @return 成功：{error:0, data:[]} 失败：{error:>0, errmsg:错误信息}
     */
	@Test
	public void productPromotionsTest() {
		String param = "?productId=1";
		String msg = use("/pc/promotion/productPromotions" + param).invoke();
		System.out.println(msg);
	}
	
	 /**
     * 查看促销活动对应的产品
     * @param promotionId
     * @return 成功：{error:0, data:[]} 失败：{error:>0, errmsg:错误信息}
     */
	@Test
	public void promotionProductsTest() {
		String param = "?offset=0&length=1&promotionId=86";
		String msg = use("/pc/promotion/promotionProducts" + param).invoke();
		System.out.println(msg);
	}
	
	 /**
     * 计算优惠金额
     * @param products [{product_id:1, price_id:4, amount:2, price:5, shop_id:3},...]
     * @return 成功：{error:0, discount:22} 失败：{error:>0, errmsg:错误信息}
     */
	@Test
	public void discountTest() {
		String param = "?products=[{'product_id':1, 'price_id':4, 'amount':2, 'price':5, 'shop_id':3}]";
		String msg = use("/pc/promotion/discount" + param).invoke();
		System.out.println(msg);
	}
}
