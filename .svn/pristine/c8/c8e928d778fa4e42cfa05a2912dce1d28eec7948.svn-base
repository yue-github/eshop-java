package com.eshop.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Role;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.permission.RoleService;
import com.eshop.permission.UserService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 用户管理
 *   @author TangYiFeng
 */
public class UserController extends AdminBaseController{
	
    /**
     * 构造方法
     */
    public UserController() {
    }
    
    /**
     * 分页获取用户列表
     * @param start
     * @param offset 页码
     * @param length 每页显示条数
     * @param userName 用户名  选填
     * @param nickName 操作人姓名  选填
     * @param disabled 是否禁用（0不禁用，1禁用）  选填
     * @param roleId 角色id  选填
     * @param roleName
     * @return @return 成功：{error: 0, data: [{id:用户id,userName:用户名称,disabled:是否禁用,roleName:角色名称},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String userName = getPara("userName");
    	String nickName = getPara("nickName");
    	Integer disabled = getParaToIntegerDefault("disabled");
    	Integer roleId = getParaToIntegerDefault("roleId");
    	String roleName = getPara("roleName");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> list = UserService.findUserItems(offset, length, userName, nickName, disabled, roleId, roleName, orderByMap);
    	int total = UserService.countUserItems(userName, nickName, disabled, roleId, roleName);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个用户
     * @param id
     * @return 成功：{error: 0, data: {id:用户id,userName:用户名称,nickName:姓名,disabled:是否禁用,role_id:角色id}}；失败：{error: >0, errmsg: 错误信息}
     */
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record user = UserService.get(id);
    	
    	jsonObject.put("data", user);
    	renderJson(jsonObject);
    }

    /**
     * 添加用户
     * @param token 帐户访问口令（必填）
     * @param userName 用户名
     * @param nickName 姓名
     * @param password 密码
     * @param roleId 角色id
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"userName", "nickName", "password", "roleId"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	if(!this.validatePassword("password")) {
    		return;
    	}
    	
    	String userName = getPara("userName");
    	String password = getPara("password");
    	String phone = getPara("phone");
    	int roleId = getParaToInt("roleId");
    	String nickName = getPara("nickName");
    	Integer disabled = getParaToIntegerDefault("disabled");
    	ServiceCode code = UserService.create(userName, password, roleId, nickName, disabled, phone);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改用户
     * @param token 帐户访问口令（必填）
     * @param id 用户id
     * @param userName 用户名
     * @param disabled 是否禁用（0不禁用，1禁用）
     * @param nickName 姓名
     * @param roleId 角色id
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "userName", "nickName", "roleId", "password"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	if(!this.validatePassword("password")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String userName = getPara("userName");
    	int roleId = getParaToInt("roleId");
    	String nickName = getPara("nickName");
    	String phone = getPara("phone");
    	String password = getPara("password");
    	Integer disabled = getParaToIntegerDefault("disabled");
    	
    	ServiceCode code = UserService.update(id, userName, roleId, nickName, disabled, phone, password);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 批量删除
     * @param ids [1,2,3,...]
     */
    @Before(AdminAuthInterceptor.class)
    public void deleteBatch() {
    	String[] params = {"ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
		
    	ServiceCode code = UserService.batchDelete(ids);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
		renderJson(jsonObject);
    }
    
    /**
     * 获取所有角色
     * @param id
     * @return 成功：{error: 0, data:[{id:3,name:名称]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void allRoles() {
    	List<Role> list = RoleService.getAllRoles();
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
}