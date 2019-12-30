package com.eshop.model.dao.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Back;
import com.eshop.model.Order;
import com.eshop.model.Refund;
import com.eshop.service.User;
import com.jfinal.ext.test.ControllerTestCase;

public class UserDaoTest extends ControllerTestCase<TestProjectConfig> {
	
	private User userDao = new User();


	@Test
	public void testGetOrder() {
		Order back = User.getOrder(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetRefund() {
		Refund back = User.getRefund(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetReturned() {
		Back back = User.getReturned(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

}
