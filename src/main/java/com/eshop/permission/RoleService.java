package com.eshop.permission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.model.Role;
import com.eshop.model.RoleNavigation;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class RoleService {

	/**
     * 获取所有菜单
     * @param roleId
     * @return
     */
    public static List<Record> allNavs(int roleId) {
    	List<Record> roleNavs = Db.find("select * from role_navigation where role_id = ?", roleId);
    	List<Record> navs = Db.find("select * from navigation");
    	
    	//判断某个菜单是否被选中
    	for (Record nav : navs) {
    		nav.set("selected", false);
    		for (Record roleNav : roleNavs) {
    			if (nav.getLong("id") == roleNav.getLong("navigation_id")) {
					nav.set("selected", true);
				}
    		}
    	}
    	
    	//对所有菜单进行分类
    	List<Record> result = new ArrayList<Record>();
    	for (Record nav : navs) {
    		Record item = null;
    		List<Record> children = new ArrayList<Record>();
    		if (nav.getLong("parent_id") == 0) {
				item = nav;
			} else {
				continue;
			}
    		
    		for (Record nav2 : navs) {
    			if (nav2.getLong("parent_id") == nav.getLong("id")) {
					children.add(nav2);
				}
    		}
    		
    		if (children.size() > 0) {
				item.set("children", children);
			}
    		
    		result.add(item);
    	}
    	
    	return result;
    }
    
    /**
     * 批量查询角色
     * @param offset
     * @param count
     * @param name
     * @return
     */
    public static List<Record> findRoleItems(int offset, int count, String name) {
    	String sql = findRoleItemsSql(name);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询角色的总数量
     * @param name
     * @return
     */
    public static int countRoleItems(String name) {
    	String sql = findRoleItemsSql(name);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param name
     * @return
     */
    private static String findRoleItemsSql(String name) {
    	String sql = "select * from role where id != 0";
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	return sql;
    }
    
    /**
     * 获取某个角色
     * @param id
     * @return
     */
    public static Record get(int id) {
    	Record result = Db.findFirst("select * from role where id = ?", id);
    	List<Record> navs = allNavs(id);
    	result.set("navs", navs);
    	return result;
    }
    
    /**
     * 创建角色
     * @return
     */
    public static ServiceCode create(String name, String navIds) {
    	Role role = new Role();
    	role.setName(name);
    	if (!role.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	List<String> ids = JSON.parseArray(navIds, String.class);
    	for (String item : ids) {
    		int id = Integer.parseInt(item);
    		RoleNavigation rn = new RoleNavigation();
    		rn.setRoleId((long)role.getId());
    		rn.setNavigationId((long)id);
    		rn.setReadable(1);
    		rn.setEditable(1);
    		rn.setCreatable(1);
    		rn.setCreatedAt(new Date());
    		rn.setUpdatedAt(new Date());
    		rn.save();
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 修改角色
     * @param model
     * @return
     */
    public static ServiceCode update(int id, String name, String navIds) {
    	Role role = Role.dao.findById(id);
    	if (role == null) {
    		return ServiceCode.Failed;
    	}
    	role.setName(name);
    	if (!role.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	Db.update("delete from role_navigation where role_id = ?", id);
    	
    	List<String> ids = JSON.parseArray(navIds, String.class);
    	for (String item : ids) {
    		int navId = Integer.parseInt(item);
    		RoleNavigation rn = new RoleNavigation();
    		rn.setRoleId((long)role.getId());
    		rn.setNavigationId((long)navId);
    		rn.setReadable(1);
    		rn.setEditable(1);
    		rn.setCreatable(1);
    		rn.setCreatedAt(new Date());
    		rn.setUpdatedAt(new Date());
    		rn.save();
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 删除某个角色
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
    	if (!Role.dao.deleteById(id)) {
    		return ServiceCode.Failed;
    	}
    	
    	Db.update("delete from role_navigation where role_id = ?", id);
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 批量删除
     * @param ids
     * @return
     */
    public static ServiceCode batchDelete(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						delete(id);
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 获取所有角色
     * @return
     */
    public static List<Role> getAllRoles() {
    	return Role.dao.find("select * from role");
    }
	
}
