package com.eshop.content.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.content.RecommendService;
import com.eshop.model.Recommend;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;

public class RecommendServiceTest  extends ControllerTestCase<TestProjectConfig> {
	
	private RecommendService recommendService = new RecommendService();

	/**
     * 添加推荐
     * @param model 推荐
     */
	@Test
	public void testCreateRecommend() {
		Recommend model = new Recommend();
		model.set("2", "5");
		ServiceCode count = RecommendService.createRecommend(model, "1");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 修改推荐
     * @param model
     * @return code
     */
	@Test
	public void testUpdateRecommend() {
		Recommend model = new Recommend();
		model.set("2", "5");
		ServiceCode count = RecommendService.updateRecommend(model, "1");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 删除推荐
     * @param id 推荐id
     * @return code
     */
	@Test
	public void testDeleteRecommend() {
		ServiceCode count = RecommendService.deleteRecommend(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	   
    /**
     * 获取一条推荐
     * @param id
     * @return model
     */
	@Test
	public void testGetRecommend() {
		Recommend count = RecommendService.getRecommend(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询推荐记录的总数量
     * @param recommendPosition_id
     * @param type
     * @param name
     * @param recommendPositionName
     * @param sortNumber
     */
	@Test
	public void testCountRecommendItems() {
		int count = RecommendService.countRecommendItems(1, 1, "aaa", "ccc", 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
