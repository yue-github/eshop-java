package com.eshop.controller.pc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.DateHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionMansong;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BasePromotion;
import com.eshop.promotion.ManSong;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 促销活动控制器
 * @author TangYiFeng
 */
public class PromotionMansongController extends PcBaseController {

    /**
     * Default constructor
     */
    public PromotionMansongController() {
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
     * @param mainPic 活动主图（选填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void create() {
    	String[] params = {"type", "title", "startDate", "endDate", "scope", "baseOn", "full"};
    	
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
    	
    	PromotionMansong mansong = new PromotionMansong();
    	mansong.setFull(full);
    	
    	ServiceCode code = ManSong.create(promotion, mansong, mainPic);
    	
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
     * @param mainPic 活动主图（选填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "type", "title", "startDate", "endDate", "scope", "baseOn", "full"};
    	
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
    	
    	PromotionMansong mansong = ManSong.getPromotionMansong(id);
    	if(mansong != null) {
    		mansong.setFull(full);
    	}
    	ServiceCode code = ManSong.update(promotion, mansong, mainPic);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "创建失败");
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

    	List<Record> list = ManSong.findPromotionItems(offset, length, title, null, startDate, endDate, startCreatedAt, endCreatedAt, scope, baseOn, shopId, null, null, orderByMap);
    	int total = ManSong.countPromotionItems(title, null, startDate, endDate, startCreatedAt, endCreatedAt, scope, baseOn, shopId, null, null);
    	    	
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
    	PromotionMansong mansong = ManSong.getPromotionMansong(id);
    	if(model != null) {
	    	if(null != model.getMainPic()) {
	    		model.put("mainPic", ResourceService.getPath(model.getMainPic()));
	    	}
	    	model.put("full", mansong.getFull());
    	}
    	
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
    	
    	ServiceCode code = ManSong.delete(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 赠品列表
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getGifts() {
    	String[] params = {"offset", "length", "promotion_id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int promotionId = getParaToInt("promotion_id");
    	
    	Record result = ManSong.getGifts(offset, length, promotionId);
    	
    	jsonObject.put("data", result.get("data"));
    	jsonObject.put("total", result.getInt("total"));
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	renderJson(jsonObject);
    }
    
    @Before(CustomerPcAuthInterceptor.class)
    public void createGifts() {
    	String[] params = {"objects", "promotion_id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	JSONArray objects = JSON.parseArray(getPara("objects"));
    	System.out.println(objects);
    	int promotionId = getParaToInt("promotion_id");
    	
    	ManSong.createGift(objects, promotionId);
    	
    	renderJson(jsonObject);
    }
    
    public void deleteGift() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	ManSong.deleteGift(id);
    	
    	renderJson(jsonObject);
    }
    
    @Before(CustomerPcAuthInterceptor.class)
    public void giftProducts() {
    	String[] params = {"offset", "length", "promotion_id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int promotionId = getParaToInt("promotion_id");
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Record result = ManSong.giftProducts(offset, length, promotionId, shopId);
    	
    	jsonObject.put("data", result.get("data"));
    	jsonObject.put("total", result.getInt("total"));
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	renderJson(jsonObject);
    }
}