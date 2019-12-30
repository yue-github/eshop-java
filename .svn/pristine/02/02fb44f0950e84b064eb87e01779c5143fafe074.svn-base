package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.PayRate;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.tax.PayTax;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 税率管理-控制器
 * @author TangYiFeng
 */
public class PayRateController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public PayRateController() {
    }

    /**
     * 分页列表
     * @param token 帐户访问口令（必填）
     * @param offset 页码
     * @param length 每页显示条数
     * @param payType
     * @param enable
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, recordsFiltered: 过滤后总数, data: [{id:id,payType:支付类型(1: 微信支付, 2: 支付宝, 3银联支付),rate:比例值（单位：百分之一）,enable:是否启用(0不启用,1启用)}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	Integer payType = getParaToIntegerDefault("payType");
    	Integer enable = getParaToIntegerDefault("enable");
    	
    	List<Record> list = PayTax.findPayRateItems(offset, length, payType, enable);
    	int total = PayTax.countFindPayRateItems(payType, enable);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某条记录
     * @param id
     * @return 成功：{error: 0, data:{id:id,payType:支付类型(1: 微信支付, 2: 支付宝, 3银联支付),rate:比例值（单位：百分之一）,enable:是否启用(0不启用,1启用)}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	PayRate model = PayTax.get(id);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建
     * @param payType 支付方式
     * @param rate 比例值
     * @param enable 是否启用
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"payType", "rate", "enable"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int payType = getParaToInt("payType");
    	BigDecimal rate = getParaToDecimal("rate");
    	int enable = getParaToInt("enable");
    	
    	PayRate model = new PayRate();
    	model.setPayType(payType);
    	model.setRate(rate);
    	model.setEnable(enable);
    	
    	ServiceCode code = PayTax.create(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改
     * @param id
     * @param payType 支付方式
     * @param rate 比例值
     * @param enable 是否启用
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "payType", "rate", "enable"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int payType = getParaToInt("payType");
    	BigDecimal rate = getParaToDecimal("rate");
    	int enable = getParaToInt("enable");
    	
    	PayRate model = PayTax.get(id);
    	model.setPayType(payType);
    	model.setRate(rate);
    	model.setEnable(enable);
    	
    	ServiceCode code = PayTax.update(model);
    	
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
    	ServiceCode code = PayTax.delete(id);
    	
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
		
    	ServiceCode code = PayTax.batchDelete(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "批量删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}