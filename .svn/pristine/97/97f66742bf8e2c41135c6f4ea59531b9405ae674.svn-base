package com.eshop.content.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.content.AdvertisementService;
import com.eshop.model.Advertisement;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class AdvertisementServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private AdvertisementService advertisementService;
		
	public AdvertisementServiceTest() {
		try {
			ControllerTestCase.start(TestProjectConfig.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		advertisementService = new AdvertisementService();
	}
	
	/**
     * 创建广告
     * @param model 广告信息
     * @param path 图片路径
     */
	@Test
	public void testCreateAdv() {
		Advertisement model = new Advertisement();
		model.set("2", "5");
		ServiceCode count = AdvertisementService.createAdv(model, "2");
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 修改广告
     * @param model 广告信息
     * @param path 图片路径
     */
	@Test
	public void testUpdateAdv() {
		Advertisement model = new Advertisement();
		model.set("2", "5");
		ServiceCode count = AdvertisementService.updateAdv(model, "1");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 删除广告
     * @param id 广告id
     */
	@Test
	public void testDeleteAdv() {
		ServiceCode count = AdvertisementService.deleteAdv(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 获取广告
     * @param id
     * @return model
     */
	@Test
	public void testGetAdv() {
		Record count = AdvertisementService.getAdv(5);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 批量查询广告记录
     * @param offset
     * @param count
     * @param note
     * @param url
     * @param sort_number
     */
	@Test
	public void testFindAdvItems() {
		List<Record> count = AdvertisementService.findAdvItems(10, 20, "111", "335s25.com", 2);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询广告记录的总数量
     * @param note
     * @param url
     * @param sort_number
     */
	@Test
	public void testCountAdvItems() {
		int count = AdvertisementService.countAdvItems("jiangquan", "jiang.com", 3);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
