package com.eshop.promotion.test;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.jfinal.ext.test.ControllerTestCase;

public class PromotionBaoyouControllerTest extends ControllerTestCase<TestProjectConfig> {
	
	/**
     * 创建促销活动
     * @param token 用户登录口令
     * @param type 活动类型
     * @param title 促销活动名称
     * @param desc 活动描述(选填)
     * @param startDate 开始时间
     * @param endDate 截止时间
     * @param scope 适用范围(1全平台，2自己店铺可用)
     * @param baseOn 产品适用范围(1全订单，2特定产品，3特定分类，4特定单品)
     * @param full 满多少钱
     * @param mainPic 活动主图（选填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
	@Test
	public void testCreate() {
		
	}
}
