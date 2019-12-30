package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.content.ContentService;
import com.eshop.content.RecommendPositionService;
import com.eshop.model.RecommendPosition;
import com.eshop.service.Merchant;
import com.jfinal.plugin.activerecord.Record;

/**
 *   首页控制器
 *   @author TangYiFeng
 */
public class HomeController extends WebappBaseController {
	
	ContentService contentService;
	
	public HomeController() {
		contentService = new ContentService();
	}

    /**
     * 获取主页轮播图
     * @return 成功：{error: 0, advertisements:[{path: 图片路径, url: 链接, note: 描述说明}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void banners() {
    	int type = RecommendPositionService.HOME_BANNAR;
    	List<Record> list = ContentService.findPcBannders(type);
    	
    	jsonObject.put("error", 0);
    	jsonObject.put("advertisements", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取推荐广告
     * @return 成功:{error:0, data:[{id:id,note:广告标题,url:url,recommendPic:广告图片},...]} 失败：{error:>0, errmsg:错误信息}
    */
    public void recommendsAdv() {
    	int type = 13;
    	List<Record> list = ContentService.getRecommendAdvByPositionType(type);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 根据分类id搜索产品
     * @param cateId 必填
     * @param offset 必填
     * @param length 必填
     * @param priceSort 价格排序(desc降序，asc升序) 选填
     * @param salesVolume 销量排序 选填
     * @param commentNum 评论数排序 选填
     * @return 成功:{error:0,totalPage:总页数,totalRow:总行数, data:[{id:id,name:产品名称,suggestedRetailUnitPrice:价格,summary:摘要,mainPic:产品主图,shopName:店铺名称},...]} 失败：{error:>0, errmsg:错误信息}
    */
    public void searchProductByCateId() {
    	String[] params = {"offset", "length", "cateId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int isSale = 1;
    	int isDeleted = 0;
    	Integer cateId = getParaToIntegerDefault("cateId");
    	String priceSort = getPara("priceSort") != null ? getPara("priceSort") : "asc";
    	String salesVolume = getPara("salesVolume");
    	String commentNum = getPara("commentNum");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("suggestedRetailUnitPrice", priceSort);
    	if(salesVolume != null && !"".equals(salesVolume)) {
    		orderByMap.put("salesVolume", salesVolume);
    	}
    	if(commentNum != null && !"".equals(commentNum)) {
    		orderByMap.put("commentNum", commentNum);
    	}
    	
    	List<Record> list = Merchant.findProductItems(offset, length, null, null, cateId, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	int total = Merchant.countProductItems(null, null, cateId, null, isSale, isDeleted, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 根据产品关键字搜索产品
     * @param offset 必填
     * @param length 必填
     * @param keyName 产品名称关键字 选填
     * @param shopId 店铺id 选填
     * @param cateId 分类id 选填
     * @param priceSort 价格排序(desc降序，asc升序) 选填
     * @param salesVolume 销量排序 选填
     * @param commentNum 评论数排序 选填
     * @return 成功:{error:0,totalPage:总页数,totalRow:总行数, data:[{id:id,name:产品名称,suggestedRetailUnitPrice:价格,summary:摘要,mainPic:产品主图,shopName:店铺名称},...]} 失败：{error:>0, errmsg:错误信息}
    */
    public void searchProduct() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	int isSale = 1;
    	int isDeleted = 0;
    	String keyName = getPara("keyName");
    	Integer shopId = getParaToInt("shopId");
    	Integer cateId = getParaToInt("cateId");
    	String priceSort = getPara("priceSort") != null ? getPara("priceSort") : "asc";
    	String salesVolume = getPara("salesVolume");
    	String commentNum = getPara("commentNum");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("suggestedRetailUnitPrice", priceSort);
    	if(salesVolume != null && !"".equals(salesVolume)) {
    		orderByMap.put("salesVolume", salesVolume);
    	}
    	if(commentNum != null && !"".equals(commentNum)) {
    		orderByMap.put("commentNum", commentNum);
    	}
    	List<Record> list = Merchant.findProductItems(offset, length, shopId, keyName, cateId, null, isSale, isDeleted, null, null, null, null, null, orderByMap);
    	int total = Merchant.countProductItems(shopId, keyName, cateId, null, isSale, isDeleted, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 根据推荐位置id，获取推荐产品
     * @param positionId 位置id
     * @param pageNumber 页码
     * @param pageSize 每页条数
     * @return {totalPage:总页数,totalRow:总行数,data:[{id:id,name:产品名称},...]}
     */
    public void paginateRecommendProdByPosId() {
    	String[] params = {"positionId", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int positionId = getParaToInt("positionId");
    	Integer offset = getParaToInt("offset");
    	if(offset == null) {
    		offset = 0;
    	}
    	int length = getParaToInt("length");
    	
    	List<Record> data = ContentService.findRecommendProdByPosId(offset, length, positionId);
    	int totalRow = ContentService.countRecommendProdByPosId(positionId);
    	
    	jsonObject.put("totalRow", totalRow);
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取app图标
     * @return 成功：{error:0, data:[{id:id,name:位置名称}]}, 失败：{error:>0,errmsg:错误信息}
     */
    public void getPosInApp() {
    	int[] arr = {15, 16, 17}; 
    	
    	List<RecommendPosition> list = contentService.getPositionInApp(arr);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取主页推荐内容,位置类型：1首页轮播图，2首页推荐产品,13首页广告；内容类型：
     * @return 成功：{error: 0, data:[{name:位置名称，type:位置类型,records:[{type:类型, id:产品id, name:产品名称, summary:摘要, suggestedRetailUnitPrice:价格, mainPic:产品主图}, {}...]}, {}...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void recommends() {
    	String baseUrl = this.getBaseUrl();
		List<RecommendPosition> ps = ContentService.getRecommends("2", baseUrl);
		
    	jsonObject.put("data", ps);
    	renderJson(jsonObject);
    }

}