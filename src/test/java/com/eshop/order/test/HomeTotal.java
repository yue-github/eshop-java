package com.eshop.order.test;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.service.HomeTotalService;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class HomeTotal extends ControllerTestCase<TestProjectConfig> {
/*
	@Test
	public void getMakeABargain() {
		Record makeABargain = HomeTotalService.getMakeABargain();
		
	}*/
/*	@Test
	public void getWeekMakeABargain() {
		Record makeABargain = HomeTotalService.getWeekMakeABargain();
	}*/
	@Test
	public void getWeekMakeABargain() {
		Record makeABargain = HomeTotalService.getWeekMakeAMoney();
	}
}
