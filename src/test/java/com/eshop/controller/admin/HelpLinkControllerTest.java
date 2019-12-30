package com.eshop.controller.admin;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.jfinal.ext.test.ControllerTestCase;

public class HelpLinkControllerTest extends ControllerTestCase<TestProjectConfig>{
	
	@Test
	public void testMany() {
		System.out.println(use("/admin/helpLink/many?"
				+ "adminToken=4f4768e9-2d1b-4484-be6d-110282e8bfde&"
				+ "offset=1&"
				+ "length=15&"
				+ "title=关于").invoke());
	}

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllHelpLinks() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreate() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchDelete() {
		fail("Not yet implemented");
	}

}
