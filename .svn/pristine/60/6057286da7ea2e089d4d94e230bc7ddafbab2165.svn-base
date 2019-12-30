package com.eshop.controller.pc;

import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.collection.CollectionService;
import com.eshop.content.ContentService;
import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Category;
import com.eshop.model.Customer;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 店铺主页控制器
 *   @author TangYiFeng
 */
public class ShopController extends PcBaseController{
	
    /**
     * Default constructor
     */
    public ShopController() {
    }
    
    /**
     * 获取分类
     * @param token
     * @return 成功：{error: 0, data:[{id:id, name:分类名称},...]}, 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getAllCategories() {
    	List<Category> list = CategoryService.getAllFirstCategories();
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 创建店铺
     * @param token 账户访问口令(必填)
     * @param name 店铺名称(必填)
     * @param shopType 店铺类型(必填)
     * @param contacts 联系人姓名(必填)
     * @param phone 联系电话(必填)
     * @param idcard 身份证号码(必填)
     * @param businessLicense 营业执照号码(必填)
     * @param idcardPic 身份证图片(必填)
     * @param businessLicensePic 营业执照图片(必填)
     * @param logoPic 店铺logo(必填)
     * @return 成功：{error: 0}, 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void createShop() {
    	if(!this.validateRequiredString("name")) {
    		return;
    	}
    	String name = getPara("name");
    	if(!this.validateRequiredString("shopType")) {
    		return;
    	}
    	int shopType = getParaToInt("shopType");
    	if(!this.validateRequiredString("contacts")) {
    		return;
    	}
    	String contacts = getPara("contacts");
    	if(!this.validateRequiredString("phone")) {
    		return;
    	}
    	String phone = getPara("phone");
    	if(!this.validateRequiredString("idcard")) {
    		return;
    	}
    	String idcard = getPara("idcard");
    	if(!this.validateRequiredString("businessLicense")) {
    		return;
    	}
    	String businessLicense = getPara("businessLicense");
    	if(!this.validateRequiredString("idcardPic")) {
    		return;
    	}
    	String idcardPic = getPara("idcardPic");
    	if(!this.validateRequiredString("businessLicensePic")) {
    		return;
    	}
    	String businessLicensePic = getPara("businessLicensePic");
    	if (!this.validateRequiredString("logoPic")) {
    		return;
    	}
    	String logoPic = getPara("logoPic");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Shop model = new Shop();
    	model.setName(name);
    	model.setShopType(shopType);
    	model.setContacts(contacts);
    	model.setPhone(phone);
    	model.setIdcard(idcard);
    	model.setBusinessLicense(businessLicense);
    	
    	ServiceCode code = Merchant.setUpShop(model, customerId, idcardPic, businessLicensePic, logoPic);
    	
    	if (code == ServiceCode.Validation) {
    		returnError(-1, "已注册店铺，不能再注册");
    	} else if (code == ServiceCode.Failed) {
    		returnError(1, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改店铺
     * @param token 账户访问口令(必填)
     * @param name 店铺名称(必填)
     * @param contacts 联系人姓名(必填)
     * @param phone 联系电话(必填)
     * @param gender 性别(必填)
     * @param logoPic 店铺logo(必填)
     * @return 成功：{error: 0}, 失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void updateShop() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	if(!this.validateRequiredString("name")) {
    		return;
    	}
    	String name = getPara("name");
    	if(!this.validateRequiredString("contacts")) {
    		return;
    	}
    	String contacts = getPara("contacts");
    	if(!this.validateRequiredString("phone")) {
    		return;
    	}
    	String phone = getPara("phone");
    	if (!this.validateRequiredString("logoPic")) {
    		return;
    	}
    	String logoPic = getPara("logoPic");
    	if (!this.validateRequiredString("gender")) {
    		return;
    	}
    	int gender = getParaToInt("gender");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Shop model = Shop.dao.findById(shopId);
    	if (model == null) {
    		this.returnError(ErrorCode.Exception, "该店铺不存在");
    		return;
    	}
    	
    	model.setName(name);
    	model.setContacts(contacts);
    	model.setPhone(phone);
    	model.setGender(gender);
    	model.setAddress(getPara("address"));
    	
    	ServiceCode code = Merchant.updateShop(model, logoPic);
    	
    	if (code != ServiceCode.Success) {
    		jsonObject.put("error", 1);
    		jsonObject.put("errmsg", "创建店铺失败");
    	} 
    	
    	renderJson(jsonObject);
    	
    }

    /**
     * 收藏商店
     * @param token
     * @param shopId 店铺id
     * @return 成功 {error:0} 失败 {error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void collection() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	
    	if (!this.validateRequiredString("shopId")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	int shopId = getParaToInt("shopId");
    	
    	ServiceCode code = CollectionService.collectShop(customerId, shopId);
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "收藏店铺失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 根据token获取店铺信息
     * @token
     * @return 成功：{error: 0, data: {id:id, name: 店铺名称, logoPic: 店铺图片,sales:销量 数量,collect:收藏数量,backs：退货退款数量}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getShopByToken() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Shop shop = Merchant.getShop(shopId);
    	if (shop == null) {
    		returnError(-1, "该店铺不存在");
    		return;
    	}
    	String logoPic = ResourceService.getPath(shop.getLogoPic());
    	shop.set("logoPic", logoPic);
    	
    	jsonObject.put("data", shop);
    	renderJson(jsonObject);
    }

    /**
     * 获取店铺信息
     * @param shopId
     * @return 成功：{error: 0, data: {id:id, name: 店铺名称, mainPic: 店铺图片,sales:销量 ,collect:收藏,backs：退货退款}；失败：{error: >0, errmsg: 错误信息}
     */
    public void get() {
    	if (!this.validateRequiredString("shopId")) {
    		return;
    	}
    	int shopId = getParaToInt("shopId");
    	
    	Shop shop = Merchant.getShop(shopId);
    	String logoPic = ResourceService.getPath(shop.getLogoPic());
    	shop.set("logoPic", logoPic);
    	
    	jsonObject.put("data", shop);
    	renderJson(jsonObject);
    }
    
    /**
     * 店铺主页轮播图
     * @return 成功：{error: 0, data: [{id:id, url: url, note: 标题, path: 图片},...]；失败：{error: >0, errmsg: 错误信息}
     */
    public void banners() {
    	List<Record> list = ContentService.findPcBannders(6);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取服务区店铺
     * @return 成功：{error:0,data:[{id:店铺id,name:店铺名称},...]} 失败：{error:>0,errmsg:错误信息}
     */
    public void getServiceShop() {
    	List<Record> list = Manager.findShopItems(null, null, null, null, 2, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 搜索产品
     * @param keyName 产品名称关键字
     * @param shopId
     * @param offset
     * @param length
     * @return 成功:{error:0,totalPage:总页数,totalRow:总行数, data:[{id:id,name:产品名称,suggestedRetailUnitPrice:价格,summary:摘要,mainPic:产品主图,shopName:店铺名称},...]} 失败：{error:>0, errmsg:错误信息}
    */
    public void searchProduct() {
    	String[] params = {"shopId", "offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int shopId = getParaToInt("shopId");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int isSale = 1;
    	int isDeleted = 0;
    	String keyName = getPara("keyName");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("updated_at", "desc");
    	
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, keyName, null, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
}