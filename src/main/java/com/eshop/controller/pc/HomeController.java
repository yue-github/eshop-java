package com.eshop.controller.pc;

import java.util.*;

import com.eshop.content.ContentService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.membership.CustomerGoldService;
import com.eshop.membership.CustomerGrowthService;
import com.eshop.membership.CustomerPointService;
import com.eshop.membership.MemberShip;
import com.eshop.model.Customer;
import com.eshop.model.RecommendPosition;
import com.eshop.model.Searchword;
import com.eshop.searchword.SearchWordService;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.regex.*;

/**
 * PC首页控制器
 *   @author TangYiFeng
 */
public class HomeController extends PcBaseController {
    
    /**
     * 获取主页轮播图
     * @return 成功：{error: 0, advertisements:[{path: 图片路径, url: 链接, note: 描述说明}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void banners() {
    	List<Record> list = ContentService.findPcBannders(1);
    	
    	for(Record item : list) {
    		String path = PropKit.use("callBackUrl.txt").get("apiHostName") + "/" + item.getStr("path");
    		System.out.println("轮播图"+path);
    		item.set("path", path);
    	}
    	
    	jsonObject.put("error", 0);
    	jsonObject.put("advertisements", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取推荐产品,1首页轮播图，2首页推荐产品,13首页广告
     * @return 成功:{error:0, data:[{positionName:位置名称, productList:[{id:id,name:产品名称,shopType:店铺类型(1个人店铺,2服务区,3自营),shopName:店铺名称,suggestedRetailUnitPrice:价格, recommendPic:推荐图片, summary:摘要},...]},...]} 失败：{error:>0, errmsg:错误信息}
    */
    public void recommends() {
    	String baseUrl = this.getBaseUrl();
		List<RecommendPosition> ps = ContentService.getRecommends("2", baseUrl);
		
    	jsonObject.put("data", ps);
    	renderJson(jsonObject);
    }
    
    /**
     * 搜索产品
     * @param keyName 产品名称关键字
     * @param offset
     * @param length
     * @param priceSort 价格排序(desc降序，asc升序) 选填
     * @return 成功:{error:0,totalPage:总页数,totalRow:总行数, data:[{id:id,name:产品名称,suggestedRetailUnitPrice:价格,summary:摘要,mainPic:产品主图,shopName:店铺名称},...]} 失败：{error:>0, errmsg:错误信息}
    */
    @SuppressWarnings("unlikely-arg-type")
	public void searchProduct() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
 
    	String keyName = getPara("keyName");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String priceSort = getPara("priceSort") != null ? getPara("priceSort") : "asc";
    	String salesVolume = getPara("salesVolume");
    	String commentNum = getPara("commentNum");
    	
    	HashSet<String> set = new HashSet();
    	set.add(keyName);
    	set.add(priceSort);
    	set.add(salesVolume);
    	set.add(commentNum);
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
    	
    	int isSale = 1;
    	int isDeleted = 0;
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("suggestedRetailUnitPrice", priceSort);
    	if(salesVolume != null && !"".equals(salesVolume)) {
    		orderByMap.put("salesVolume", salesVolume);
    	}
    	if(commentNum != null && !"".equals(commentNum)) {
    		orderByMap.put("commentNum", commentNum);
    	}
    	
    	System.out.println("keyname="+keyName);
    	List<Record> list = Merchant.findProductItems(offset, length, null, keyName, null, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	int total = Merchant.countProductItems(null, keyName, null, null, isSale, isDeleted, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }

    private String[] split(String matche_str, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * 签到
     * @param token
     * @return 成功：{error:0,-1:今天已签到} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void sign() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	int result = CustomerPointService.signIn(customerId);
    	CustomerGrowthService.signIn(customerId);
    	
    	if (result == 1) {
    		returnError(-1, "今天已经签到");
    		return;
    	}
    	
    	//签到增加通币
    	CustomerGoldService.updateGold(customerId, 1, 100, "签到");
    	
    	
//    	customer.setGolds(customer.getGolds() + 1);
//    	customer.update();
    	
    	jsonObject.put("error", result);
    	renderJson(jsonObject);
    }
    
    /**
     * 最近签到
     * @param token
     * @param offset
     * @param length
     * @return 成功：{error:0,data:[{id:id,amount:积分数量,created_at:日期},...]} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void signList() {
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
    	
    	List<Record> list = CustomerPointService.findCustomerPointItems(offset, length, customerId, null, null, MemberShip.SIGN_IN_POINT, null, null, null);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 关键字
     */
    public void searchword() {
    	List<Searchword> list = SearchWordService.searchword();
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}