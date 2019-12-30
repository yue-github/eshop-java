package com.eshop.controller.admin;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.content.AdvertisementService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Advertisement;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 广告管理-控制器
 * @author TangYiFeng
 */
public class AdvertisementController extends AdminBaseController{
	
    /**
     * 构造方法
     */
    public AdvertisementController() {
    }
    
    /**
     * 获取广告列表
     * @param token 帐户访问口令（必填）
     * @param offset 页码
     * @param length 数目
     * @param note
     * @param url
     * @param sort_number
     * @return 成功：{error:0, offset:页码, totalRow:总数, data:[]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	
    	String note = getPara("note");
    	String url = getPara("url");
    	Integer sort_number = (getPara("sort_number") != null) ? getParaToInt("sort_number") : null;
    	
    	System.out.println("sort_number:"+sort_number);
    	List<Record> list = AdvertisementService.findAdvItems(offset, length, note, url, sort_number);
    	int total = AdvertisementService.countAdvItems(note, url, sort_number);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取广告详情
     * @param token 帐户访问口令（必填）
     * @param id
     * @return 成功：{error: 0, data: {id: 广告id, path: 图片路径, note: 描述, url:超链接, sort_number: 排序 }}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record result = AdvertisementService.getAdv(id);
    	
    	System.out.println(result);
    	jsonObject.put("data", result);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改广告信息
     * @param token 帐户访问口令（必填）
     * @param id 广告id（必填）
     * @param path 图片路径（必填）
     * @param note 描述
     * @param url 超链接
     * @param sort_number 排序
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "path"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String path = getPara("path");
    	String note = getPara("note");
    	String url = getPara("url");
    	int sortNumber = getParaToIntDefault("sort_number");
    	
    	Advertisement model = AdvertisementService.get(id);
    	model.setId(id);
    	model.setNote(note);
    	model.setUrl(url);
    	model.setSortNumber(sortNumber);
    	model.setUpdatedAt(new Date());
    	
    	ServiceCode code = AdvertisementService.updateAdv(model, path);
    	
    	if(code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 创建广告信息
     * @param token 帐户访问口令（必填）
     * @param path 图片路径（必填）
     * @param note 描述
     * @param url 超链接
     * @param sort_number 排序
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"path"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String path = getPara("path");
    	String note = getPara("note");
    	String url = getPara("url");
    	int sortNumber = getParaToIntDefault("sort_number");
    	
    	Advertisement model = new Advertisement();
    	model.setNote(note);
    	model.setUrl(url);
    	model.setSortNumber(sortNumber);
    	model.setUpdatedAt(new Date());
    	
    	ServiceCode code = AdvertisementService.createAdv(model, path);
    	
    	if(code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除广告
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
    	ServiceCode code = AdvertisementService.deleteAdv(id);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 批量删除广告
     * @param token 帐户访问口令（必填）
     * @param ids 产品id数组:[1, 2, ...]（必填）
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
    	
    	ServiceCode code = AdvertisementService.batchDeleteAdv(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }

}