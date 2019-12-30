package com.eshop.permission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eshop.log.Log;
import com.eshop.model.Navigation;
import com.eshop.model.UserRole;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class NavigationService {

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static List<Record> getRoleNavs(int userId) {
		List<UserRole> list = UserRole.dao.find("select * from user_role where user_id = ?", userId);
		List<Record> tempNavs = new ArrayList<Record>();

		for (UserRole item : list) {
			List<Record> ns = Db.find("select n.*, rn.readable, rn.editable, rn.creatable as rid from role_navigation as rn "
					+ "left join navigation as n on n.id = rn.navigation_id "
					+ "where role_id = ? order by parent_id, sortNumber", item.getRoleId());

			tempNavs.addAll(ns);
		}

		List<Record> navs = new ArrayList<Record>();

		for (Record r : tempNavs) {
			if(r.get("parent_id") == null) {
				continue;
			}
			
			if (r.getLong("parent_id") == 0 && !navs.contains(r)) {
				navs.add(r);
			} else {
				Record tr = findNav(r.getLong("parent_id").intValue(), navs);
				if (tr != null) {
					List<Record> children = new ArrayList<Record>();
					if (tr.getColumns().containsKey("children")) {
						children = (List<Record>) tr.get("children");
					} else {
						tr.set("children", children);
					}

					children.add(r);
				}
			}
		}

		return navs;
	}
	
	/**
	 * 
	 * @param parentId
	 * @param navs
	 * @return
	 */
	private static Record findNav(int parentId, List<Record> navs) {
		Record n = null;
		for (Record r : navs) {
			if (r.getLong("id") == parentId) {
				n = r;

				break;
			} else {
				if (!r.getColumns().containsKey("children")) {
					continue;
				} else {
					Object cs = r.get("children");
					n = findNav(parentId, (List<Record>) cs);
				}
			}
		}

		return n;
	}
	
	/**
     * 获取某个菜单
     * @param id
     * @return
     */
    public static Navigation get(int id) {
    	return Navigation.dao.findById(id);
    }
    
    /**
     * 创建菜单
     * @return
     */
    public static ServiceCode create(Navigation model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	if (!model.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 修改菜单
     * @param model
     * @return
     */
    public static ServiceCode update(Navigation model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	if (!model.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 删除某个菜单
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
    	if (!Navigation.dao.deleteById(id)) {
    		return ServiceCode.Failed;
    	}
    	
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
						Navigation.dao.deleteById(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",批量删除导航菜单失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 获取所有菜单
     * @return
     */
    public static List<Navigation> all() {
    	return Navigation.dao.find("select * from navigation");
    }
    
    /**
     * 批量查询导航菜单
     * @param offset
     * @param count
     * @param name
     * @param displayName
     * @param sortNumber
     * @param parentId
     * @param orderByMap
     * @return
     */
    public static List<Record> findNavigationItems(int offset, int count, String name, 
    		String displayName, Integer sortNumber, Integer parentId, Map<String, String> orderByMap) {
    	
    	String sql = findNavigationItemsSql(name, displayName, sortNumber, parentId, orderByMap);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询导航菜单的总条数
     * @param name
     * @param displayName
     * @param sortNumber
     * @param parentId
     * @return
     */
    public static int countNavigationItems(String name, 
    		String displayName, Integer sortNumber, Integer parentId) {
    	
    	String sql = findNavigationItemsSql(name, displayName, sortNumber, parentId, null);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param name
     * @param displayName
     * @param sortNumber
     * @param parentId
     * @param orderByMap
     * @return
     */
    public static String findNavigationItemsSql(String name, 
    		String displayName, Integer sortNumber, Integer parentId, Map<String, String> orderByMap) {
    	
    	String sql = "select * from navigation where id != 0";
    	
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	if (displayName != null && !displayName.equals("")) {
			sql += " and displayName like '%" + displayName + "%'";
		}
    	if (sortNumber != null) {
			sql += " and sortNumber = " + sortNumber;
		}
    	if (parentId != null) {
			sql += " and parent_id = " + parentId;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
	
}
