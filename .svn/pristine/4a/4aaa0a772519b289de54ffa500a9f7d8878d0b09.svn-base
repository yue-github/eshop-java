package com.eshop.controller.admin;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.permission.RoleService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 角色管理-控制器
 * @author TangYiFeng
 */
public class RoleController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public RoleController() {
    }

    /**
     * 角色列表
     * @param token 帐户访问口令（必填）
     * @param offset 页码
     * @param length 每页显示条数
     * @param name 角色名称
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, recordsFiltered: 过滤后总数, data: [{id:id,name:名称}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	String name = getPara("name");
    	
    	List<Record> list = RoleService.findRoleItems(offset, length, name);
    	int total = RoleService.countRoleItems(name);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个角色
     * @param id
     * @return 成功：{error: 0, data:{id:id,name:名称,navs:[{id:3,displayName:订单管理,children:[{id:2,display:物流管理},...]},...]}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record result = RoleService.get(id);
    	
    	jsonObject.put("data", result);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建
     * @param name 角色名称
     * @param ids 菜单id，格式：[1,2,3,...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"name", "ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String name = getPara("name");
    	String ids = getPara("ids");
    	
    	ServiceCode code = RoleService.create(name, ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改
     * @param id id值
     * @param name 角色名称
     * @param ids 菜单id，格式：[1,2,3,...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "name", "ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String name = getPara("name");
    	String ids = getPara("ids");
    	
    	ServiceCode code = RoleService.update(id, name, ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除
     * @param id
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void delete() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = RoleService.delete(id);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject); 
    }
    
    /**
     * 批量删除
     * @param ids [1,2,3,...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void batchDelete() {
    	String[] params = {"ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
		
    	ServiceCode code = RoleService.batchDelete(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "批量删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有菜单
     * @param id
     * @return 成功：{error: 0, data:[{id:3,displayName:订单管理,children:[{id:2,display:物流管理},...]},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void navs() {
    	List<Record> list = RoleService.allNavs(0);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
}