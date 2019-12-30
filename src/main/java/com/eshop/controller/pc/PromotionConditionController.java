package com.eshop.controller.pc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Category;
import com.eshop.model.Customer;
import com.eshop.model.Product;
import com.eshop.model.PromotionCondition;
import com.eshop.model.Resource;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.PromotionConditionService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class PromotionConditionController extends PcBaseController {

	/**
	 * 创建促销产品
	 * @param token
	 * @param promotionId
	 * @param objectId
	 * @param type
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void create() {
		String[] params = {"promotionId", "objectId", "type"};
		
		if (!validate(params)) {
			return;
		}
		
		int promotionId = getParaToInt("promotionId");
		int objectId = getParaToInt("objectId");
		int type = getParaToInt("type");
		
		PromotionCondition model = new PromotionCondition();
		model.setPromotionId(promotionId);
		model.setObjectId(objectId);
		model.setType(type);
		
		ServiceCode code = PromotionConditionService.create(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "创建失败");
		}
		
		renderJson(jsonObject);
	}
	

	/**
	 * 批量创建促销产品
	 * @param token 用户登录口令
	 * @param promotionId 促销活动id
	 * @param object 产品类型或产品id和名称
	 * @param type 产品适用范围
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void batchCreate() {
		String[] params = {"objects"};
		
		if (!validate(params)) {
			return;
		}
	
		String string = getPara("objects");
		List<Map> array = JSON.parseArray(string, Map.class);
		ServiceCode code = PromotionConditionService.batchCreate(array);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "创建失败");
		}
		
		renderJson(jsonObject);
	}
	
	
	/**
	 * 修改促销产品
	 * @param token
	 * @param id
	 * @param promotionId
	 * @param objectId
	 * @param type
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "promotionId", "objectId", "type"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		int promotionId = getParaToInt("promotionId");
		int objectId = getParaToInt("objectId");
		int type = getParaToInt("type");
		
		PromotionCondition model = PromotionConditionService.get(id);
		model.setPromotionId(promotionId);
		model.setObjectId(objectId);
		model.setType(type);
		
		ServiceCode code = PromotionConditionService.update(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "修改失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 查看促销产品
	 * @param id
	 * @return 成功：{error:0, data:{}} 失败：{error:>0, errmsg:错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		
		PromotionCondition model = PromotionConditionService.get(id);
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	/**
	 * 批量查询促销产品
	 * @param token
	 * @param offset
	 * @param length
	 * @param promotionId
	 * @prarm type
	 * @param name
	 * @return 成功：{error:0, data:[]} 失败：{error:>0, errmsg:错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void many() {
		String[] params = {"offset", "length"};
		
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer promotionId = (getPara("promotionId") != null) ? getParaToInt("promotionId") : null;
		Integer type = getParaToInt("type");
		String name = getPara("name");
		List<Record> records = new ArrayList<>();
		List<Record> list = PromotionConditionService.findPromotionConditionItems(offset, length, promotionId, type, name);
		int total = PromotionConditionService.countPromotionConditionItems(promotionId, null, name);
		for(Record record: list) {
			Product pro = Product.dao.findById(record.getInt("object_id"));
			if(pro!=null) {
				Resource res = Resource.dao.findById(pro.getInt("mainPic"));
				if(res != null){
					if(res.getPath() != null && !"".equals(res.getPath())){
						record.set("mainPic", res.getPath());
					}else {
						record.set("mainPic", "");
					}
				}
			}
			records.add(record);
		}
		jsonObject.put("data", records);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		renderJson(jsonObject);
	}
	
	/**
	 * 删除促销产品
	 * @param token
	 * @param id
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		
		ServiceCode code = PromotionConditionService.delete(id);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 批量删除促销产品
	 * @param token
	 * @param ids
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void batchDelete() {
		String[] params = {"ids"};
		
		if (!validate(params)) {
			return;
		}
		
		String idsStr = getPara("ids");
		List<Integer> ids = JSON.parseArray(idsStr, Integer.class);
		
		ServiceCode code = PromotionConditionService.delete(ids);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
	}
	
	  
    /**
     * 获取店铺适合促销活动的商品
     * @param token 用户登录指令
     * @param promotionId 促销活动id
     * @param type 类型
     * 
     */
    public void getAllProject() {
    	String[] params = {"promotionId", "type", "pageIndex", "length"};
    	if(!validate(params)) {
    		return;
    	}
    	int pageIndex = getParaToInt("pageIndex");
		int length = getParaToInt("length");
    	String token = getPara("token");
    	String promotionId = getPara("promotionId");
    	String type = getPara("type");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	Page<Product> page = PromotionConditionService.getAllProject(pageIndex, length, shopId, promotionId, type);
    	//将数据封装
		jsonObject.put("pageIndex", pageIndex);
		jsonObject.put("totalRow", page.getTotalRow());
		jsonObject.put("data", page.getList());
		renderJson(jsonObject);
    }
    
    /**
     * 获取店铺适合促销活动的类型
     * @param token 用户登录指令
     * @param promotionId 促销活动id
     * @param type 类型
     * 
     */
    public void getAllCategory() {
    	String[] params = {"promotionId", "type", "pageIndex", "length"};
    	if(!validate(params)) {
    		return;
    	}
    	int pageIndex = getParaToInt("pageIndex");
		int length = getParaToInt("length");
    	String promotionId = getPara("promotionId");
    	String type = getPara("type");
    	Page<Category> page = PromotionConditionService.getAllCategory(pageIndex, length, promotionId, type);
    	//将数据封装
		jsonObject.put("pageIndex", pageIndex);
		jsonObject.put("totalRow", page.getTotalRow());
		jsonObject.put("data", page.getList());
		renderJson(jsonObject);
    }
    
    
	
}
