package com.eshop.permission;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eshop.helper.MD5Helper;
import com.eshop.model.User;
import com.eshop.model.UserRole;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class UserService {

	/**
     * 管理员登录
     */
    public static User login(String name, String password) {
    	List<User> users = User.dao.find("select * from user where userName =?",name);
    	System.out.println("用户:"+ users);
    	if(users.size() > 0){
    		User user = users.get(0);
    		String password1 = user.getPassword();
    		System.out.println("password" + password);
    		System.out.println("password1" + password1);
    		if(!password1.equals(MD5Helper.Encode(password))){
    			return null;
    		}
    		return user;
    	}else{
    		return null;
    	}
    }
    
    /**
     * 管理员手机登录
     */
    public static User login(String phone) {
    	List<User> users = User.dao.find("select * from user where phone =?",phone);
    	if(users.size() > 0){
    		User user = users.get(0);
    		return user;
    	}else{
    		return null;
    	}
    }
    
    /**
     * 禁用管理员
     * @param id
     * @return
     */
    public static ServiceCode forbidden(int id) {
    	Db.update("update user set disabled = 1 where id = ?", id);
    	return ServiceCode.Success;
    }
    
    /**
     * 创建管理员
     * @param userName
     * @param password
     * @param roleId
     * @param disabled 
     * @return
     */
    @Before(Tx.class)
    public static ServiceCode create(String userName, String password, int roleId, String nickName, Integer disabled, String phone) {
    	User user = new User();
    	user.setUserName(userName);
    	user.setNickName(nickName);
    	user.setPassword(MD5Helper.Encode(password));
    	user.setCreatedAt(new Date());
    	user.setUpdatedAt(new Date());
    	user.setDisabled(disabled);
    	user.setPhone(phone);
    	
    	if(!user.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	UserRole userRole = new UserRole();
    	userRole.setUserId(user.getId());
    	userRole.setRoleId(roleId);
    	userRole.setCreatedAt(new Date());
    	userRole.setUpdatedAt(new Date());
    	
    	if (!userRole.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 修改管理员
     * @param id
     * @param userName
     * @param roleId
     * @param disabled 
     * @return
     */
    public static ServiceCode update(int id, String userName, int roleId, String nickName, Integer disabled, String phone, String password) {
    	User user = User.dao.findById(id);
    	if (user == null) {
    		return ServiceCode.Failed;
    	}
    	if(!user.getPassword().equals(MD5Helper.Encode(password))) {
    		user.setPassword(MD5Helper.Encode(password));
    	}
    	user.setUserName(userName);
    	user.setNickName(nickName);
    	user.setDisabled(disabled);
    	user.setPhone(phone);
    	user.setUpdatedAt(new Date());
    	
    	if (!user.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	Db.update("delete from user_role where user_id = ?", id);
    	
    	UserRole userRole = new UserRole();
    	userRole.setUserId(id);
    	userRole.setRoleId(roleId);
    	userRole.setCreatedAt(new Date());
    	userRole.setUpdatedAt(new Date());
    	
    	if (!userRole.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 获取某个管理员信息
     * @param id
     * @return
     */
    public static Record get(int id) {
    	String sql = "select a.*, b.role_id from user as a" + 
    			" left join user_role as b on a.id = b.user_id" + 
    			" where a.id = " + id;
    	
    	Record result = Db.findFirst(sql);
    	return result;
    }
    
    /**
     * 批量查询管理员
     * @param offset
     * @param count
     * @param userName
     * @param nickName
     * @param disabled
     * @param roleId
     * @param roleName
     * @return
     */
    public static List<Record> findUserItems(int offset, int count, String userName, String nickName, 
    		Integer disabled, Integer roleId, String roleName, Map<String, String> orderByMap) {
    	
    	String sql = findUserItemsSql(userName, nickName, disabled, roleId, roleName, orderByMap);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询管理员的总数量
     * @param userName
     * @param nickName
     * @param disabled
     * @param roleId
     * @param roleName
     * @return
     */
    public static int countUserItems(String userName, String nickName, Integer disabled, Integer roleId, 
    		String roleName) {
    	
    	String sql = findUserItemsSql(userName, nickName, disabled, roleId, roleName, null);
    	
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param userName
     * @param nickName
     * @param disabled
     * @param roleId
     * @param roleName
     * @return
     */
    public static String findUserItemsSql(String userName, String nickName, Integer disabled, 
    		Integer roleId, String roleName, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, c.name as roleName, a.nickName from user as a" +
    			" left join user_role as b on a.id = b.user_id" +
    			" left join role as c on b.role_id = c.id" +
    			" where a.id != 0";
    	
    	if (userName != null && !userName.equals("")) {
			sql += " and a.userName like '%" + userName + "%'";
		}
    	if (nickName != null && !nickName.equals("")) {
			sql += " and a.nickName like '%" + nickName + "%'";
		}
    	if (disabled != null) {
			sql += " and a.disabled = " + disabled;
		}
    	if (roleId != null) {
			sql += " and b.id = " + roleId;
		}
    	if (roleName != null && !roleName.equals("")) {
			sql += " and c.name like '%" + roleName + "%'";
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
    
    /**
     * 删除某个管理员
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
    	if (!User.dao.deleteById(id)) {
			return ServiceCode.Failed;
		}
    	
    	Db.update("delete from user_role where user_id = ?", id);
    	
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
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }
	
}
