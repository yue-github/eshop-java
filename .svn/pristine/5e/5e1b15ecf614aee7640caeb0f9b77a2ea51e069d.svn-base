package com.eshop.permission.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.User;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.permission.UserService;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class UserServiceTest extends ControllerTestCase<TestProjectConfig> {

	private UserService userService = new UserService();

	/**
     * 管理员登录
     */
	@Test
	public void testLogin() {
		User count = UserService.login("annnna", "123456");
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 禁用管理员
     * @param id
     */
	@Test
	public void testForbidden() {
		ServiceCode count = UserService.forbidden(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 创建管理员
     * @param userName
     * @param password
     * @param roleId
     */
//	@Test
//	public void testCreate() {
//		ServiceCode count = UserService.create("asder", "123456", 5, "sd", 0);
//		boolean flag = count != null;
//		assertEquals(false, flag);
//	}

    /**
     * 修改管理员
     * @param id
     * @param userName
     * @param roleId
     */
	@Test
	public void testUpdate() {
		/*ServiceCode count = userService.update(2, "asd", 2, "qja");
		boolean flag = count != null;
		assertEquals(false, flag);*/
	}

    /**
     * 获取某个管理员信息
     * @param id
     */
	@Test
	public void testGet() {
		Record count = UserService.get(4);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

    /**
     * 批量查询管理员的总数量
     * @param userName
     * @param nickName
     * @param disabled
     * @param roleId
     * @param roleName
     */
	@Test
	public void testCountUserItems() {
		int count = UserService.countUserItems("zws", "23", 2, 1, "1452");
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

    /**
     * 删除某个管理员
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = UserService.delete(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
