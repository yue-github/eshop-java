package com.eshop.permission.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.permission.NavigationService;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class NavigationServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private NavigationService navigationService = new NavigationService();

	@Test
	public void testGetRoleNavs() {
		
		List<Record> count = NavigationService.getRoleNavs(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
