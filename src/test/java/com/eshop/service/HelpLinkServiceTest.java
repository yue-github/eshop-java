package com.eshop.service;

import org.junit.Test;
import com.eshop.config.TestProjectConfig;
import com.eshop.helper.SMSHelper;
import com.jfinal.ext.test.ControllerTestCase;

public class HelpLinkServiceTest extends ControllerTestCase<TestProjectConfig> {

	/*@Test
	public void testFindHelpLinkItems() {
		int offset = 1;
		int count = 15;
		String title = "关于";
		List<Record> lists = HelpLinkService.findHelpLinkItems(offset, count, title);
		for(Record record : lists){
			System.out.println(record);
		}
	}

	@Test
	public void testCountRoleItems() {
		String title = "关于";
		int total = HelpLinkService.countRoleItems(title);
		System.out.println(total);
	}

	@Test
	public void testGet() {
		System.out.println(HelpLinkService.get(1));
	}

	@Test
	public void testCreate() {
		String title = "关于我1";
		String content = "射手座";
		int sortNumber = 1;
		System.out.println(HelpLinkService.create(title, content, sortNumber));
	}

	@Test
	public void testUpdate() {
		int id = 2;
		String title = "关于你";
		String content = "狮子座";
		int sortNumber = 2;
		System.out.println(HelpLinkService.update(id, title, content, sortNumber));
	}

	@Test
	public void testDelete() {
		System.out.println(HelpLinkService.delete(1));
	}

	@Test
	public void testBatchDelete() {
		List<String> ids = new ArrayList<String>();
		ids.add("2");
		ids.add("3");
		System.out.println(HelpLinkService.batchDelete(ids));
	}

	@Test
	public void testGetAllHelpLinks() {
		System.out.println(HelpLinkService.getAllHelpLinks());
	}*/
	
	@Test
	public void testSendMsg() {
		String phone = "13428282238";
		String content = "【乐驿商城】”河源和平趣香3号绿心猕猴桃奇异果现摘新鲜水果广东包邮“剩余库存不足，请及时补充";
		long code = SMSHelper.sendMessage(phone, content);
		System.out.println("code=" + code);
	}
	
}
