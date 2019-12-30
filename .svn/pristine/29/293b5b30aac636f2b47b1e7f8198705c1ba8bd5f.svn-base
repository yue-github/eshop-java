package com.eshop.controller.admin;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.content.AdvertisementService;
import com.eshop.content.RecommendPositionService;
import com.eshop.content.RecommendService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Recommend;
import com.eshop.model.RecommendPosition;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 推荐管理
 *   @author TangYiFeng
 */
public class RecommendController extends AdminBaseController {

    /**
     * Default constructor
     */
    public RecommendController() {
    }
    
    /**
     * 创建推荐位 推荐产品
     * @param recommendPositionId 位置id
     * @param sortNumber 排序值
     * @param relate_Id 关联id
     * @param type 类型
     * @param recommendPic 推荐主图(选填)
     * @return 成功：{error: 0}
     */
    @Before(AdminAuthInterceptor.class)
    public void createRecommend() {
    	String[] params = {"recommendPosition_id", "sortNumber", "relate_Id", "type"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int recommendpositionId = getParaToInt("recommendPosition_id");
    	int sortNumber = getParaToInt("sortNumber");
    	int relateId = getParaToInt("relate_Id");
    	int type = getParaToInt("type");
    	String recommendPic = getPara("recommendPic");
    	
    	Recommend recommend = new Recommend();
    	recommend.setRecommendpositionId(recommendpositionId);
    	recommend.setSortNumber(sortNumber);
    	recommend.setRelateId(relateId);
    	recommend.setType(type);
    	
    	ServiceCode code = RecommendService.createRecommend(recommend, recommendPic);
    	
    	if(code != ServiceCode.Success){
    		returnError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改推荐
     */
    @Before(AdminAuthInterceptor.class)
    public void updateRecommend() {
    	String[] params = {"id", "recommendPosition_id", "sortNumber", "relate_Id", "type"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int recommendpositionId = getParaToInt("recommendPosition_id");
    	int sortNumber = getParaToInt("sortNumber");
    	int relateId = getParaToInt("relate_Id");
    	int type = getParaToInt("type");
    	String recommendPic = getPara("recommendPic");
    	
    	Recommend recommend = RecommendService.getRecommend(id);
    	recommend.setRecommendpositionId(recommendpositionId);
    	recommend.setSortNumber(sortNumber);
    	recommend.setRelateId(relateId);
    	recommend.setType(type);
    	
    	ServiceCode code = RecommendService.updateRecommend(recommend, recommendPic);
    	
    	if(code != ServiceCode.Success){
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	renderJson(jsonObject);
    }
    
    /**
     * 批量创建推荐位 推荐产品
     * @param recommendPositionId 位置id
     * @param sortNumber 排序值
     * @param relateIds 关联id [1,2,3,...]
     * @param type 类型
     * @param recommendPic 推荐主图(选填)
     * @return 成功：{error: 0}
     */
    @Before(AdminAuthInterceptor.class)
    public void batchCreateRecommend() {
    	String[] params = {"recommendPositionId", "sortNumber", "relateIds", "type"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int recommendpositionId = getParaToInt("recommendPositionId");
    	int sortNumber = getParaToInt("sortNumber");
    	String idsStr = getPara("relateIds");
    	int type = getParaToInt("type");
    	String recommendPic = getPara("recommendPic");
    	
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	
    	for (String item : ids) {
    		int relateId = Integer.parseInt(item);
    		
    		Recommend recommend = new Recommend();
        	recommend.setRecommendpositionId(recommendpositionId);
        	recommend.setSortNumber(sortNumber);
        	recommend.setRelateId(relateId);
        	recommend.setType(type);
        	
        	if(RecommendService.createRecommend(recommend, recommendPic) != ServiceCode.Success){
        		returnError(ErrorCode.Exception, "create Recommend failed");
        		return;
        	}
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有推荐产品
     * @param token 帐户访问口令（必填）
     * @param offset 必填
     * @param length 必填
     * @param nam 名称 选填
     * @param sortNumber 排序值 选填
     * @param recommendPositionId  位置id  选填
     * @param type  类型  (1产品，2服务，3店铺，4广告)  选填
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, recordsFiltered: 过滤后总数, data: [{id: 分类id, name: 内容名称, type:类型， relate_Id：关联id, updated_at:修改时间，recommendPositionName:推荐位置名称}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void findRecommend() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	String name = getPara("name");
    	Integer sortNumber = getParaToIntegerDefault("sortNumber");
    	Integer recommendPositionId = getParaToIntegerDefault("recommendPositionId");
    	Integer type = getParaToIntegerDefault("type");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> list = RecommendService.findRecommendItems(offset, length, recommendPositionId, type, name, null, sortNumber, orderByMap);
    	int total = RecommendService.countRecommendItems(recommendPositionId, type, name, null, sortNumber);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取单条推荐
     */
    @Before(AdminAuthInterceptor.class)
    public void getRecommend() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Recommend recommend = RecommendService.getRecommend(id);
    	
    	jsonObject.put("recommend", recommend);
    	renderJson(jsonObject);
    }
    
    /**
     * 批量删除推荐
     */
    @Before(AdminAuthInterceptor.class)
    public void deleteRecommend() {
    	String[] params = {"ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	
    	ServiceCode code = RecommendService.batchDeleteRecommend(ids);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "批量删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有推荐位置
     * @param token 帐户访问口令（必填）
     * @param offset
     * @param length
     * @param name
     * @param type
     * @param sortNumber
     */
    @Before(AdminAuthInterceptor.class)
    public void findRecommendPosition() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	String name = getPara("name");
    	Integer type = getParaToIntegerDefault("type");
    	Integer sortNumber = getParaToIntegerDefault("sortNumber");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("created_at", "desc");
    	
    	List<Record> list = RecommendPositionService.findPositionItems(offset, length, name, type, null, sortNumber, orderByMap);
    	int total = RecommendPositionService.countPositionItems(name, type, null, sortNumber);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有推荐位置(无分页)
     */
    @Before(AdminAuthInterceptor.class)
    public void RecommendPositions() {
    	List<RecommendPosition> list = RecommendPositionService.getAll();
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 添加推荐位置
     */
    @Before(AdminAuthInterceptor.class)
    public void createPosition() {
    	String[] params = {"name", "sortNumber", "type"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String name = getPara("name");
    	int sortNumber = getParaToInt("sortNumber");
    	int type = getParaToInt("type");
    	String note  = getPara("note");
    	String image = getPara("image");
    	
    	RecommendPosition recommendPosition = new RecommendPosition();
    	recommendPosition.setName(name);
    	recommendPosition.setSortNumber(sortNumber);
    	recommendPosition.setType(type);
    	recommendPosition.setNote(note);
    	recommendPosition.setImage(image);
    	
    	ServiceCode code = RecommendPositionService.createPosition(recommendPosition);
    	
    	if(code != ServiceCode.Success){
    		returnError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改推荐位置
     */
    @Before(AdminAuthInterceptor.class)
    public void updatePosition() {
    	String[] params = {"id", "name", "sortNumber", "type"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String name = getPara("name");
    	int sortNumber = getParaToInt("sortNumber");
    	int type = getParaToInt("type");
    	String note  = getPara("note");
    	String image = getPara("image");
    	
    	RecommendPosition recommendPosition = RecommendPositionService.getPosition(id);
    	recommendPosition.setName(name);
    	recommendPosition.setSortNumber(sortNumber);
    	recommendPosition.setType(type);
    	recommendPosition.setNote(note);
    	recommendPosition.setImage(image);
    	
    	ServiceCode code = RecommendPositionService.updatePosition(recommendPosition);
    	
    	if(code != ServiceCode.Success){
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取单条推荐位置
     */
    @Before(AdminAuthInterceptor.class)
    public void getPosition() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	RecommendPosition recommendPosition = RecommendPositionService.getPosition(id);
    	
    	jsonObject.put("recommendPosition", recommendPosition);
    	renderJson(jsonObject);
    }

    /**
     * 批量删除推荐位置
     */
    @Before(AdminAuthInterceptor.class)
    public void deletePosition() {
    	String[] params = {"ids"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseArray(idsStr, String.class);
    	
    	ServiceCode code = RecommendPositionService.batchDeletePosition(ids);
    	
    	if(code != ServiceCode.Success){
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有产品
     */
    @Before(AdminAuthInterceptor.class)
    public void products() {
    	int isSale = 1;
    	int isDeleted = 0;
    	List<Record> list = Merchant.findProductItems(null, null, null, null, isSale, isDeleted, null, null, null, null, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有广告
     * @return 成功：{error:0, data[{id:广告id, name:广告名称},...]} 失败:{error:>0, errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void getAllAdvs() {
    	List<Record> list = AdvertisementService.findAdvItems(null, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有店铺
     * @return 成功：{error:0, data[{id:店铺id, name:店铺名称},...]} 失败:{error:>0, errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void getAllShops() {
    	Integer status = 1;
    	List<Record> list = Manager.findShopItems(null, null, null, status, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
}