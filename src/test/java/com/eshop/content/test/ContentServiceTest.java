package com.eshop.content.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.content.ContentService;
import com.jfinal.ext.test.ControllerTestCase;
import com.eshop.config.TestProjectConfig;
import com.jfinal.plugin.activerecord.Record;

public class ContentServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private ContentService contentService = new ContentService();
	public void ContentServiceTest() {
		try {
			ControllerTestCase.start(TestProjectConfig.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		contentService = new ContentService();
	}


	//获取首页轮播图
	@Test
	public void testFindPcBannders() {
		List<Record> count = ContentService.findPcBannders(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

	/**
     * 获取各个页面的推荐    位置类型： 1首页轮播图，2首页推荐产品,4首页推荐服务,5首页健康食品,13首页广告；内容类型：1产品，2服务，3店铺，4广告
     * @param positionType 位置类型   格式：type,type
     * @return 推荐信息    格式: [{name:位置名称, records:List<Record>]
     */
	@Test
	public void testGetRecommends() {
		List<Record> count = ContentService.getRecommendProductsByPositionType(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	 
    /**
     * 查找所有一级分类
     */
	@Test
	public void testCategories() {
		List<Record> count = ContentService.categories();
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	 
    /**
     * 根据位置类型获取推荐产品
     * @param type 位置类型
     */
	@Test
	public void testGetRecommendProductsByPositionType() {
		List<Record> count = ContentService.getRecommendProductsByPositionType(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
