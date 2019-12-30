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
import com.eshop.model.Product;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionMiaosha;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BasePromotion;
import com.eshop.promotion.MiaoSha;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.tencent.service.BaseService;

/**
 * 促销活动控制器
 *   @author TangYiFeng
 */
public class PromotionMiaoshaController extends PcBaseController {

    /**
     * Default constructor
     */
    public PromotionMiaoshaController() {
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
     * @param limitAmount
     * @param mainPic 活动主图（选填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void save() {
    	String[] params = {"id", "sec_price", "sec_start_time", "sec_end_time"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("id");
    	int isSeckill = 1;
    	BigDecimal secPrice = getParaToDecimal("sec_price");
    	Date secStartTime = getParaToDate("sec_start_time");
    	Date secEndTime = getParaToDate("sec_end_time");
 
    	Product product = Product.dao.findById(productId);
    	product.setIsSeckill(isSeckill);
    	product.setSecPrice(secPrice);
    	product.setSecStartTime(secStartTime);
    	product.setSecEndTime(secEndTime);
    	product.update();
    	
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
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	String sql = "select * from product where is_seckill = 1 and shop_id = " + shopId;
    	String sql2 = BaseDao.appendLimitSql(sql, offset, length);
    	List<Record> data = Db.find(sql2);
    	List<Record> list = Db.find(sql);
    	int total = list.size();
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    @Before(CustomerPcAuthInterceptor.class)
    public void products() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	String sql = "select id, name, suggestedRetailUnitPrice from product where is_sale = 1 and isDelete = 0 and shop_id = " + shopId;
    	List<Record> list = Db.find(sql);
    	int total = list.size();
    	
    	jsonObject.put("data", list);
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
    	Product product = Product.dao.findById(id);
    	
    	jsonObject.put("data", product);
    	renderJson(jsonObject);
    }
    
    /**
     * 删除促销活动
     * @param token
     * @param ids 促销活动ids[1,2,...]
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void delete() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Product product = Product.dao.findById(id);
    	product.setIsSeckill(0);
    	product.setSecPrice(null);
    	product.setSecStartTime(null);
    	product.setSecEndTime(null);
    	product.update();
    	
    	renderJson(jsonObject);
    }
    
}