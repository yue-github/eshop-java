package com.eshop.controller.pc;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.collection.CollectionService;
import com.eshop.coupon.CouponService;
import com.eshop.coupon.CustomerCouponService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.BankCard;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.wallet.Card;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 个人中心-控制器
 * @author TangYiFeng
 */
public class CenterController extends PcBaseController {
	
    /**
     * Default constructor
     */
    public CenterController() {
    }

    /**
     * 获取帐户信息
     *  @param token 帐户访问口令（必填）
     *  @return 成功：{error: 0, info:{nickName昵称, headImg头像,momey账号余额，bankNum我的银行卡数，goldNum我的金币数,couponAmout:折扣券数量,cashAmount:现金券数量,note:简介}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void info() {
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
        int CustomerId = customer.getId();
                
        Customer model = Member.info(CustomerId);
        
        jsonObject.put("info", model);
        renderJson(jsonObject);
    }
    
    /**
     * 修改用户信息
     *  @param token 帐户访问口令（必填）
     *  @param  nickName 昵称
     *  @param  headImg 头像
     *  @param  name 真实姓名
     *  @param  gender 性别
     *  @param  mobilePhone 联系电话 
     *  @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @SuppressWarnings("unlikely-arg-type")
	public void updateInfo() {
    	int CustomerId = getParaToInt("id");
    	
    	String name = getPara("name");
    	String nickName = getPara("nickName");
    	int gender = getParaToInt("gender");
    	String headImg = getPara("headImg");
    	String mobilePhone = getPara("mobilePhone");
    	
    	HashSet<String> set = new HashSet();
    	set.add(name);
    	set.add(nickName);
    	set.add(headImg);
    	set.add(mobilePhone);
    	String matche_str = "'|\"|>|..|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|script|frame|;|or|-|+|,|)|etc|style|expression";
    	String[] strArr = matche_str.split("\\|");
    	for (int i=0 ; i < strArr.length ; i++ ){
    		if (set.add(strArr[i])) {
    			System.out.println(true + "不存在");
    		} else {
    			System.out.println(false + "存在");
    			return;
    		}
		}
    	
    	Customer model = new Customer();
    	model.setId(CustomerId);
    	model.setName(name);
    	model.setNickName(nickName);
    	model.setGender(gender);
    	model.setMobilePhone(mobilePhone);
    	model.setHeadImg(headImg);
    	
    	if(Member.updateInfo(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "update dateInfo failed");
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 修改用户密码
     *  @param token 帐户访问口令（必填）
     *  @param  oldpassword 旧密码（必填）
     *  @param  namepassword 新密码（必填）
     *  @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    public void updatePwd() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
        int id = getParaToInt("id");
        if(!this.validateRequiredString("oldpassword")) {
    		return;
    	}
        String oldpassword = getPara("oldpassword");
        if(!this.validateRequiredString("namepassword")) {
    		return;
    	}
        
        String namepassword = getPara("namepassword");
        
        if(!this.validatePassword("namepassword")) {
    		return;
    	}
        
        if(Member.setPassword(id, oldpassword, namepassword) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "update password failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取我的优惠券
     * @param token
     * @param offset
     * @param length
     * @param isTimeOut (0未过期，1已过期, -1所有)
     * @return 成功：{error: 0, totalPage:总页数,  data: [{id:id, title:折扣券标题, full:最低购买金额, value:折扣, isUsed:是否已使用，startDate:开始时间, endDate:结束时间},...]}  失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getMyCoupons() {
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	
    	Integer isTimeOut = null;
    	if (getParaToInt("isTimeOut") != null) {
    		isTimeOut = getParaToInt("isTimeOut");
    		isTimeOut = isTimeOut == -1 ? null : isTimeOut;
    	}
    	
    	Customer customer = (Customer) CacheHelper.get(getPara("token"));
    	int customerId = customer.getId();
    	
    	List<Record> list = CustomerCouponService.findCustomerCouponItems(offset, length, customerId, isTimeOut);
    	int total = CustomerCouponService.countCustomerCouponItems(customerId, isTimeOut);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取我的现金券
     * @param token
     * @param offset
     * @param length
     * @param isTimeOut (0未过期，1已过期)
     * @return 成功：{error: 0, totalPage:总页数,  data: [{id:id, cashTitle:现金券标题, minPurchaseAmount:最低购买金额, cashDiscount:优惠金额, is_used:是否已使用，depositTime:开始时间, expirationTime:结束时间},...]}  失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getMyCashs() {
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Integer isTimeOut = null;
    	if (getPara("isTimeOut") != null) {
    		isTimeOut = getParaToInt("isTimeOut");
    		isTimeOut = isTimeOut != -1 ? isTimeOut : null;
    	}
    	
    	List<Record> list = CustomerCouponService.findCustomerCouponItems(offset, length, customerId, isTimeOut, CouponService.COUPON_CASH);
    	int total = CustomerCouponService.countCustomerCouponItems(customerId, isTimeOut, CouponService.COUPON_CASH);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 我的店铺收藏列表
     * @param token(必填)
     * @param offset 当前页码(必填)
     * @param length
     * @return 成功：{error:0, totalPages:总页数， data:[{id:id, name:店铺名称, mainPic:店铺主图},...]} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void myShopCollections() {
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = CollectionService.findCollectionItems(offset, length, CollectionService.SHOP, customerId, null, null, null, null);
    	int total = CollectionService.countCollectionItems(CollectionService.SHOP, customerId, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 取消店铺收藏
     * @param token(必填)
     * @param id 收藏id(必填)
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void cancleShopCollection() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	ServiceCode code = CollectionService.cancelCollect(id);
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "取消收藏失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 我的产品收藏列表
     * @param token（必填）
     * @param offset 当前页码（必填）
     * @return 成功：{error:0, totalPages:总页数， data:[{id:id, name:产品名称，mainPic:产品主图},...]}  失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void myProductCollections() {
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	String name = getPara("name");
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	String[] array = {name, token};
    	String matche_str = "'|\"|>|..|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|script|frame|;|or|-|+|,|)|etc|style|expression";
    	String[] strArr = matche_str.split("\\|");
    	for (int i=0 ; i < array.length ; i++ ){
    		if (strArr.equals(array[i])){
    			return;
    		}
		}
    	List<Record> list = CollectionService.findCollectionItems(offset, length, CollectionService.PRODUCT, customerId, name, null, null, null);
    	int total = CollectionService.countCollectionItems(CollectionService.PRODUCT, customerId, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 取消产品收藏
     * @param token(必填)
     * @param ids [1,2,3,...](必填)
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void cancelProductCollection() {
    	if (!this.validateRequiredString("ids")) {
    		return;
    	}
    	String idsStr = getPara("ids");
    	
    	List<Integer> ids = JSON.parseArray(idsStr, Integer.class);
    	ServiceCode code = CollectionService.cancelCollect(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "取消收藏失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 退出登录
     * @param token
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void logout() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	CacheHelper.remove(token);
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取银行卡信息
     * @param token
     * @param bankCardId 银行id
     * @return 成功：{error:0, data:{id:id,accoutNumber:银行账号}} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getBankCard() {
    	if (!this.validateRequiredString("bankCardId")) {
    		return;
    	}
    	
    	int bankCardId = getParaToInt("bankCardId");
    	BankCard model = Card.getCard(bankCardId);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    public void getCode() {
    	if (!this.validateRequiredString("phone")) {
    		return;
    	}
    	
    	String phone = getPara("phone");
    	Record result = SMSHelper.sendCode(phone);
    	
    	jsonObject.put("codeToken", result.get("codeToken"));
    	jsonObject.put("code", result.get("code"));
    	jsonObject.put("error", result.get("error"));
    	renderJson(jsonObject);
    }
    
    /**
     * 绑定手机
     * @token
     * @phone 新手机号码
     * @code 手机验证码
     * @codeToken 验证码token
     * @return 成功:{error:0,error:-1(验证码不正确)} 失败:{error>0,errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void bindPone() {
    	if (!this.validateRequiredString("phone")) {
    		return;
    	}
    	String phone = getPara("phone");
    	
    	if (!this.validateRequiredString("code")) {
    		return;
    	}
    	String code = getPara("code");
    	
    	if (!this.validateRequiredString("codeToken")) {
    		return;
    	}
    	String codeToken = getPara("codeToken");
    	
    	//判断验证码是否正确
    	if (Member.hasCode(codeToken, code) != ServiceCode.Success) {
    		returnError(-1, "验证码不正确");
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode serCode = Member.bindPhone(customerId, phone);
    	
    	if (serCode != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "绑定手机号码错误");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }

}