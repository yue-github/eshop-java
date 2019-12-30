package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Tax;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.tax.ProductTax;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 税率管理-控制器
 * @author TangYiFeng
 */
public class TaxController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public TaxController() {
    }

    /**
     * 税率列表
     * @param token 帐户访问口令（必填）
     * @param offset
     * @param length 每页显示条数
     * @param name
     * @param enable
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, data: [{id:id,name:名称,rate:税率值,enable:是否启用(0不启用,1启用)}, ...]}；失败：{error: >0, errmsg: 错误信息}
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
    	Integer enable = getParaToIntegerDefault("enable");
    	
    	List<Record> list = ProductTax.findTaxItems(offset, length, name, enable);
    	int total = ProductTax.countTaxItems(name, enable);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个税率
     * @param id
     * @return 成功：{error: 0, data:{id:id,name:名称,rate:税率值,enable:是否启用(0不启用,1启用)}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Tax model = ProductTax.get(id);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建
     * @param name 税率标识
     * @param rate 税率值
     * @param enable 是否启用
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"name", "rate", "enable"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String name = getPara("name");
    	BigDecimal rate = getParaToDecimal("rate");
    	int enable = getParaToInt("enable");
    	
    	Tax model = new Tax();
    	model.setName(name);
    	model.setRate(rate);
    	model.setEnable(enable);
    	
    	ServiceCode code = ProductTax.create(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改
     * @param id
     * @param name 税率标识
     * @param rate 税率值
     * @param enable 是否启用
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "name", "rate", "enable"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String name = getPara("name");
    	BigDecimal rate = getParaToDecimal("rate");
    	int enable = getParaToInt("enable");
    	
    	Tax model = ProductTax.get(id);
    	model.setName(name);
    	model.setRate(rate);
    	model.setEnable(enable);
    	
    	ServiceCode code = ProductTax.update(model);
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除
     * @param id
     * @return 成功：{error: 0, error:-1(该税率已被使用，不能删除)}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void delete() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = ProductTax.delete(id);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
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
		
    	ServiceCode code = ProductTax.batchDelete(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
}