package com.eshop.controller.pc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.DateHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionManjian;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BasePromotion;
import com.eshop.promotion.Manjian;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 促销活动控制器
 * @author TangYiFeng
 */
public class PromotionManjianController extends PcBaseController {

    /**
     * Default constructor
     */
    public PromotionManjianController() {
    }
    
    /**
     * 创建促销活动
     * @param token 用户登录口令
     * @param type 活动类型
     * @param title 促销活动名称
     * @param desc 活动描述(选填)
     * @param startDate 开始时间
     * @param endDate 截止时间
     * @param scope
     * @param baseOn
     * @param full 满多少钱
     * @param value
     * @param mainPic 活动主图（选填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void create() {
    	String[] params = {"type", "title", "endDate", "startDate", "scope", "baseOn", "full", "value"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int type = getParaToInt("type");
    	String title = getPara("title");
    	String desc = getPara("desc");
    	Date startDate = getParaToDate("startDate");
    	Date endDate = getParaToDate("endDate");
    	int scope = getParaToInt("scope");
    	int baseOn = getParaToInt("baseOn");
    	BigDecimal full = getParaToDecimal("full");
    	BigDecimal value = getParaToDecimal("value");
    	String mainPic = getPara("mainPic");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Promotion promotion = new Promotion();
    	promotion.setType(type);
    	promotion.setTitle(title);
    	promotion.setDesc(desc);
    	promotion.setStartDate(startDate);
    	promotion.setEndDate(endDate);
    	promotion.setScope(scope);
    	promotion.setBaseOn(baseOn);
    	promotion.setShopId(shopId);
    	
    	PromotionManjian manjian = new PromotionManjian();
    	manjian.setFull(full);
    	manjian.setValue(value);
    	
    	ServiceCode code = Manjian.create(promotion, manjian, mainPic);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 修改促销活动
     * @param token 用户登录口令
     * @param id 促销活动id
     * @param type 活动类型
     * @param title 促销活动名称
     * @param desc 活动描述(选填)
     * @param startDate 开始时间
     * @param endDate 截止时间
     * @param scope
     * @param baseOn
     * @param full 满多少钱
     * @param value
     * @param mainPic 活动主图（选填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "type", "title", "startDate", "endDate", "scope", "baseOn", "full", "value"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int type = getParaToInt("type");
    	String title = getPara("title");
    	String desc = getPara("desc");
    	Date startDate = getParaToDate("startDate");
    	Date endDate = getParaToDate("endDate");
    	int scope = getParaToInt("scope");
    	int baseOn = getParaToInt("baseOn");
    	BigDecimal full = getParaToDecimal("full");
    	BigDecimal value = getParaToDecimal("value");
    	String mainPic = getPara("mainPic");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Promotion promotion = BasePromotion.getPromotion(id);
    	promotion.setType(type);
    	promotion.setTitle(title);
    	promotion.setDesc(desc);
    	promotion.setStartDate(startDate);
    	promotion.setEndDate(endDate);
    	promotion.setScope(scope);
    	promotion.setBaseOn(baseOn);
    	promotion.setShopId(shopId);
    	
    	PromotionManjian manjian = Manjian.getPromotionManjian(id);
		manjian.setFull(full);
		manjian.setValue(value);
    	
    	ServiceCode code = Manjian.update(promotion, manjian, mainPic);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取促销活动
     * @param token 用户登录口令
     * @param offset
     * @param length
     * @param title
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @return 成功：{error: 0, totalRow:总行数, data:[]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String title = getPara("title");
    	String startDate = (getPara("startDate") != null) ? DateHelper.formatDateTime(getParaToDate("startDate")) : null;
    	String endDate = (getPara("endDate") != null) ? DateHelper.formatDateTime(getParaToDate("endDate")) : null;
    	String startCreatedAt = (getPara("startCreatedAt") != null) ? DateHelper.formatDateTime(getParaToDate("startCreatedAt")) : null;
    	String endCreatedAt = (getPara("endCreatedAt") != null) ? DateHelper.formatDateTime(getParaToDate("endCreatedAt")) : null;
    	Integer scope = (getPara("scope") != null) ? getParaToInt("scope") : null;
    	Integer baseOn = (getPara("baseOn") != null) ? getParaToInt("baseOn") : null;
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");

    	List<Record> list = Manjian.findPromotionItems(offset, length, title, null, startDate, endDate, startCreatedAt,
														endCreatedAt, scope, baseOn,
														shopId, null, null, null, null, orderByMap);
    	
    	for(Record item: list) {
    		if(null != item.get("mainPic")) {
    			item.set("mainPic", ResourceService.getPath(item.getInt("mainPic")));
	    	}
    	}
    	int total = Manjian.countPromotionItems(title, null, startDate, endDate, startCreatedAt, endCreatedAt, scope, baseOn, shopId, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 查看促销活动
     * @param token 用户登录口令
     * @param id 促销活动id
     * @return 成功：{error: 0 data:{}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Promotion model = BasePromotion.getPromotion(id);
    	PromotionManjian manjian = Manjian.getPromotionManjian(id);
    	if(model != null) {
	    		
	    	if(null != model.getMainPic()) {
	    		model.put("mainPic", ResourceService.getPath(model.getMainPic()));
	    	}
    	}
    	model.put("full", manjian.getFull());
    	model.put("value", manjian.getValue());
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 删除促销活动
     * @param token
     * @param ids 促销活动ids[1,2,...]
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void delete() {
    	String[] params = {"ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<Integer> ids = JSON.parseArray(idsStr, Integer.class);
    	
    	ServiceCode code = Manjian.delete(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}