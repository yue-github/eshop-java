package com.eshop.controller.admin;



import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Navigation;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.permission.NavigationService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 菜单管理
 * 
 * @author TangYiFeng
 */
public class NavigationController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public NavigationController() {
    }
    
    /**
     * 分页获取菜单列表
     * @param offset 页码
     * @param length 每页显示条数
     * @param displayName 菜单名称
     * @param sortNumber
     * @return @return 成功：{error: 0, data: [{id:id,name:英文名称,displayName:中文名称,icon:图标,url:路由,sortNumber:排序值},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	String displayName = getPara("displayName");
    	Integer sortNumber = getParaToIntegerDefault("sortNumber");
    	
    	List<Record> list = NavigationService.findNavigationItems(offset, length, null, displayName, sortNumber, null, null);
    	int total = NavigationService.countNavigationItems(null, displayName, sortNumber, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个菜单
     * @return 成功：{error: 0, data: {id:id,name:英文名称,displayName:中文名称,icon:图标,url:路由,sortNumber:排序值,parent_id:父id}}；失败：{error: >0, errmsg: 错误信息}
     */
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	Navigation model = NavigationService.get(id);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }

    /**
     * 添加菜单
     * @param token 帐户访问口令（必填）
     * @param name 英文名
     * @param displayName 中文名
     * @param icon 图标
     * @param url 前端路由
     * @param sortNumber 排序值
     * @param parent_id 父id
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"name", "displayName", "icon", "url", "sortNumber", "parent_id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String name = getPara("name");
    	String displayName = getPara("displayName");
    	String icon = getPara("icon");
    	String url = getPara("url");
    	int sortNumber = getParaToInt("sortNumber");
    	long parentId = getParaToInt("parent_id");
    	
    	Navigation model = new Navigation();
    	model.setName(name);
    	model.setDisplayName(displayName);
    	model.setIcon(icon);
    	model.setUrl(url);
    	model.setSortNumber(sortNumber);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	model.setParentId(parentId);
    	
    	ServiceCode code = NavigationService.create(model);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改菜单
     * @param token 帐户访问口令（必填）
     * @param id 用户id
     * @param name 英文名
     * @param displayName 中文名
     * @param icon 图标
     * @param url 前端路由
     * @param sortNumber 排序值
     * @param parent_id 父id
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "name", "displayName", "icon", "url", "sortNumber", "parent_id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String name = getPara("name");
    	String displayName = getPara("displayName");
    	String icon = getPara("icon");
    	String url = getPara("url");
    	int sortNumber = getParaToInt("sortNumber");
    	long parentId = getParaToInt("parent_id");
    	
    	Navigation model = NavigationService.get(id);
    	model.setName(name);
    	model.setDisplayName(displayName);
    	model.setIcon(icon);
    	model.setUrl(url);
    	model.setSortNumber(sortNumber);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	model.setParentId(parentId);
    	
    	ServiceCode code = NavigationService.update(model);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 批量删除
     * @param ids [1,2,3,...]
     */
    @Before(AdminAuthInterceptor.class)
    public void deleteBatch() {
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
		
    	ServiceCode code = NavigationService.batchDelete(ids);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "删除失败");
    	}
    	
		renderJson(jsonObject);
    }
    
    /**
     * 获取所有菜单
     */
    @Before(AdminAuthInterceptor.class)
    public void all() {
    	List<Navigation> list = NavigationService.all();
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
}