package com.eshop.content.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.content.RecommendPositionService;
import com.eshop.model.RecommendPosition;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;

public class RecommendPositionServiceTest  extends ControllerTestCase<TestProjectConfig> {
	
	private RecommendPositionService recommendPositionService = new RecommendPositionService();
	public void RecommendPositionServiceTest() {
		try {
			ControllerTestCase.start(TestProjectConfig.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		recommendPositionService = new RecommendPositionService();
	}

	

	/**
     * 创建位置
     * @param model
     */
	@Test
	public void testCreatePosition() {
		RecommendPosition model = new RecommendPosition();
		model.set("2", "5");
		ServiceCode count = RecommendPositionService.createPosition(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 修改位置
     * @param model
     */
	@Test
	public void testUpdatePosition() {
		RecommendPosition model = new RecommendPosition();
		model.set("2", "5");
		ServiceCode count = RecommendPositionService.updatePosition(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 删除位置
     * @param id
     * @return
     */
	@Test
	public void testDeletePosition() {
		ServiceCode count = RecommendPositionService.deletePosition(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 查看位置明细
     * @param id
     */
	@Test
	public void testGetPosition() {
		RecommendPosition count = RecommendPositionService.getPosition(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
