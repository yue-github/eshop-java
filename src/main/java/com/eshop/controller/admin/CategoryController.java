package com.eshop.controller.admin;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.content.ResourceService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Category;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 分类管理-控制器
 * @author TangYiFeng
 */
public class CategoryController extends AdminBaseController {
	
    /** 
     * 构造方法
     */
    public CategoryController() {
    }

    /**
     * 查找分类
     * @param token 帐户访问口令（必填）
     * @param id 分类id
     * @param parentId 父分类id
     * @param name 分类名称
     * @param sortNumber 排序
     * @param offset 页码
     * @param length 每页显示条数
     * @return 成功：{error: 0, offset: 页码, recordsTotal: 总数, recordsFiltered: 过滤后总数, data: [{id: 分类id, name: 名称, parentName: 上级分类名称, parent_id:上级分类id, sortNumber: 序号, note: 备注, mainPic:分类图片}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	Integer id = getParaToIntegerDefault("id");
    	Integer sortNumber = getParaToIntegerDefault("sortNumber");
    	Integer parentId = getParaToIntegerDefault("parentId");
    	Integer isDelete = 0;
    	String name = getPara("name");
    	
    	List<Record> list = Manager.findCategoryItems(offset, length, id, name, parentId, sortNumber, isDelete, null);
    	int total = Manager.countCategoryItems(id, name, parentId, sortNumber, isDelete);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取分类详情
     * @param token 帐户访问口令（必填）
     * @param id 分类id（必填）
     * @return 成功：{error: 0, category: {id: 分类id, name: 名称, parent_id: 上级分类id, sortNumber: 序号, note: 备注,mainPic:分类图片}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Category model = Manager.getCategory(id);
    	model.set("mainPic", ResourceService.getPath(model.getMainPic()));
    	
    	jsonObject.put("category", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建分类 
     * @param token 帐户访问口令（必填）
     * @param name 名称（必填）
     * @param parent_id 上级分类id
     * @param sortNumber 排序
     * @param note 备注
     * @param mainPic 分类图片
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"name", "parent_id", "sortNumber"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int parent_id = getParaToInt("parent_id");
    	int sortNumber = getParaToInt("sortNumber");
    	String name = getPara("name");
    	String note = getPara("note");
    	String mainPic = getPara("mainPic");
    	
    	Category model = new Category();
    	model.set("name", name);
    	model.set("parent_id", parent_id);
    	model.set("sortNumber", sortNumber);
    	model.set("note", note);
    	
    	ServiceCode code = Manager.createCategory(model, mainPic);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改分类 
     * @param token 帐户访问口令（必填）
     * @param id 分类id（必填）
     * @param name 名称（必填）
     * @param parent_id 上级分类id
     * @param sortNumber 排序
     * @param note 备注
     * @param mainPic 分类图片
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "name", "parent_id", "sortNumber"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int parent_id = getParaToInt("parent_id");
    	int sortNumber = getParaToInt("sortNumber");
    	String name = getPara("name");
    	String note = getPara("note");
    	String mainPic = getPara("mainPic");
    	
    	Category model = Manager.getCategory(id);
    	model.set("name", name);
    	model.set("parent_id", parent_id);
    	model.set("sortNumber", sortNumber);
    	model.set("note", note);
    	
    	ServiceCode code = Manager.updateCategory(model, mainPic);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "修改失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除分类 
     * @param token 帐户访问口令（必填）
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
    	ServiceCode code = Manager.deleteCategory(id);
    	
    	if (ServiceCode.Success != code) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 批量删除分类 
     * @param token 帐户访问口令（必填）
     * @param ids 分类id数组:[1, 2, ...]（必填）
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
    	
    	ServiceCode code = Manager.batchDeleteCategory(ids);
    	
    	if (ServiceCode.Success != code) {
    		setError(ErrorCode.Exception, "批量删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}