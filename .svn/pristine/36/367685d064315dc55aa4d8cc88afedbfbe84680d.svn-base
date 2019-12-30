package com.eshop.controller.admin;

import java.util.*;

import com.eshop.content.ResourceService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 店铺管理
 *   @author TangYiFeng
 */
public class ShopController extends AdminBaseController {

    /**
     * 构造方法
     */
    public ShopController() {
    }
    
    /**
     * 店铺列表
     * @param token 帐户访问口令（必填）
     * @param offset 页码
     * @param length 每页显示条数
     * @param status 店铺状态(0:未审核，-1:已审核[-1不是真实状态])
     * @return 成功：{error: 0 data:{{id...}...}} 失败：{error: >0, errmsg: 错误信息}
     */
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	Integer status = getParaToIntegerDefault("status");
    	Integer shopType = getParaToIntegerDefault("shopType");
    	String name = getPara("name") != null ? getPara("name").trim():null;
    	String contacts = getPara("contacts") != null ? getPara("contacts").trim():null;
    	String phone = getPara("phone") != null ? getPara("phone").trim():null;
    	String startTime = getParaToDateTimeDefault("startTime") != null ? getParaToDateTimeDefault("startTime").trim():null;
    	String endTime = getParaToDateTimeDefault("endTime") != null ? getParaToDateTimeDefault("endTime").trim():null;

    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = Manager.findShopItems(offset, length, name, contacts, phone, status, shopType, orderByMap, startTime, endTime);
    	int total = Manager.countShopItems(name, contacts, phone, status, shopType, startTime, endTime);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 禁用店铺     -status 状态（1通过2不通过3禁用）
     * @param id 店铺id
     * @return 成功：{error: 0} 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void disable(){
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = Manager.forbiddenShop(id);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "禁用店铺失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 启用店铺
     * id 店铺id
     * @return 成功：{error: 0} 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void enable(){
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = Manager.activateShop(id);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "激活店铺失败");
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 通过店铺
     * id 店铺id
     * @return 成功：{error: 0} 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void pass(){
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = Manager.auditShop(id, 1);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "通过店铺失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 不通过店铺
     * id 店铺id
     * @return 成功：{error: 0} 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void refuse(){
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = Manager.auditShop(id, 2);
    	
    	if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取店铺信息
     * id 店铺id
     * @return 成功：{error: 0, data:{id:店铺id,idcardPic:身份证图片,businessLicensePic:营业执照图片,logoPic:图片logo}} 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get(){
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	Shop shop = Merchant.getShop(id);
    	String logoPic = ResourceService.getPath(shop.getLogoPic());
    	shop.set("logoPic", logoPic);
    	String idcardPic = ResourceService.getPath(shop.getIdcardPic());
    	shop.set("idcardPic", idcardPic);
    	String businessLicensePic = ResourceService.getPath(shop.getBusinessLicensePic());
    	shop.set("businessLicensePic", businessLicensePic);
    	
    	jsonObject.put("data", shop);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改店铺
     * @param id 店铺id
     * @param shopType 店铺类型(1个人店铺，2服务区，3自营)
     * @param status 状态(0审核中，1通过，2不通过，3禁用)
     * @return 成功：{error: 0} 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "shopType", "status"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int shopType = getParaToInt("shopType");
    	int status = getParaToInt("status");
    	
    	Shop model = Merchant.getShop(id);
    	model.setShopType(shopType);
    	model.setStatus(status);
    	
    	ServiceCode code = Merchant.updateShop(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改店铺失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}