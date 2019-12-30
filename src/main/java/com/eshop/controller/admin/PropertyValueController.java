package com.eshop.controller.admin;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.PropertyValue;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 属性值管理
 * @author TangYiFeng
 */
public class PropertyValueController extends AdminBaseController {
	
    /**
     * Default constructor
     */
    public PropertyValueController() {
    }
    
    /**
     * 获取属性值（按sort_number排序）
     * @param token 帐户访问口令（必填）
     * @param propertyId 属性id
     * @param name
     * @param sortNumber
     * @param offset 页码
     * @param length 每页显示条数
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, data: [{id:id, name:属性值名称, property_id:属性id，propertyName:属性名称， sortNumber:排序值，created_at：创建时间}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	Integer propertyId = getParaToIntegerDefault("propertyId");
    	Integer sortNumber = getParaToIntegerDefault("sortNumber");
    	String name = getPara("name");
    	Integer isDelete = 0;
    	
    	List<Record> list = Manager.findPropValueItems(offset, length, name, propertyId, sortNumber, isDelete, null);
    	int total = Manager.countPropValueItems(name, propertyId, isDelete, sortNumber);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建产品属性值
     * @param token 帐户访问口令（必填）
     * @param name 名称(必填)
     * @param propertyId 分类id(必填)
     * @param sortNumber 排序(必填)
     * @return 成功：{error:0,}   失败：{error:>0, errmsg：错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"name", "propertyId", "sortNumber"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String name = getPara("name");
    	int propertyId = getParaToInt("propertyId");
    	int sortNumber = getParaToInt("sortNumber");
    	
    	PropertyValue model = new PropertyValue();
    	model.setName(name);
    	model.setPropertyId(propertyId);
    	model.setSortNumber(sortNumber);
    	
    	ServiceCode code = Manager.createPropValue(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改产品属性值
     * @param token 帐户访问口令（必填）
     * @param id 属性值id
     * @param name 名称(必填)
     * @param propertyId 分类id(必填)
     * @param sortNumber 排序(必填)
     * @return 成功：{error:0,}   失败：{error:>0, errmsg：错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "name", "propertyId", "sortNumber"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String name = getPara("name");
    	int propertyId = getParaToInt("propertyId");
    	int sortNumber = getParaToInt("sortNumber");
    	
    	PropertyValue model = Manager.getPropValue(id);
    	model.setName(name);
    	model.setPropertyId(propertyId);
    	model.setSortNumber(sortNumber);
    	
    	ServiceCode code = Manager.updatePropValue(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取产品属性值
     * @param token 帐户访问口令（必填）
     * @param id 产品属性id(必填)
     * @return 成功：{error:0, data:{id:id, name:属性值名称, property_id:属性id， sortNumber:排序值，created_at：创建时间}}   失败：{error:>0, errmsg：错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	PropertyValue model = Manager.getPropValue(id);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 批量删除产品属性值
     * @param token 帐户访问口令（必填）
     * @param ids 格式：[id,id,...] (必填)
     * @return 成功：{error:0,}   失败：{error:>0, errmsg：错误信息}
     */
    public void batchDelete() {
    	String[] params = {"ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	
    	ServiceCode code = Manager.batchDeletePropValue(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "批量删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有的属性
     * @return 成功：{error: 0, data: [{id:id, name:属性名称, category_id:分类id， sortNumber:排序值, is_sale_pro:是否销售属性，parent_id:父id，created_at：创建时间}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void getAllProperty() {
    	List<Record> list = Manager.findPropValueItems(null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}