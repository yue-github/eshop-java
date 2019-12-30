package com.eshop.content.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.content.ResourceService;
import com.jfinal.ext.test.ControllerTestCase;

public class ResourceServiceTest  extends ControllerTestCase<TestProjectConfig> {
	
	private ResourceService resourceService = new ResourceService();

	/**
     * 添加图片资源
     * @param path 图片路径
     * @param relateId 关联id
     */
	@Test
	public void testInsertResource() {
		int count = ResourceService.insertResource("index/qqq", 1, 1, 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
