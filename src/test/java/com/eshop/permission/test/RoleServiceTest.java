package com.eshop.permission.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Role;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.permission.RoleService;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class RoleServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private RoleService roleService = new RoleService();

	/**
     * 获取所有菜单
     * @param roleId
     */
	@Test
	public void testAllNavs() {
		List<Record> count = RoleService.allNavs(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询角色
     * @param offset
     * @param count
     * @param name
     */
	@Test
	public void testFindRoleItems() {
		List<Record> count = RoleService.findRoleItems(10, 5, "jq");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询角色的总数量
     * @param name
     */
	@Test
	public void testCountRoleItems() {
		int count = RoleService.countRoleItems("assss");
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
    
    /**
     * 获取某个角色
     * @param id
     */
	@Test
	public void testGet() {
		Record count = RoleService.get(2);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	   
    /**
     * 创建角色
     */
	@Test
	public void testCreate() {
		ServiceCode count = RoleService.create("sd", "2");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	   
    /**
     * 修改角色
     * @param model
     */
	@Test
	public void testUpdate() {
		ServiceCode count = RoleService.update(2, "zxc", "252");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 删除某个角色
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = RoleService.delete(5);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetAllRoles() {
		List<Role> count = RoleService.getAllRoles();
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
