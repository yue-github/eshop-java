package com.eshop.controller.admin;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.content.AdvertisementService;
import com.eshop.content.RecommendPositionService;
import com.eshop.content.RecommendService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.OperationLog;
import com.eshop.model.ProductBrand;
import com.eshop.model.Recommend;
import com.eshop.model.RecommendPosition;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 品牌管理
 *   @author TangYiFeng
 */
public class ProductBrandController extends AdminBaseController {

    /**
     * Default constructor
     */
    public ProductBrandController() {
    }
    
    /**
     * 创建品牌
     * @param recommendPositionId 位置id
     * @param sortNumber 排序值
     * @return 成功：{error: 0}
     */
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	String[] params = {"name", "sortNumber"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int sortNumber = getParaToInt("sortNumber");
    	String name = getPara("name");
    	String note = getPara("note");
    	
    	ProductBrand model = new ProductBrand();
    	model.setName(name);
    	model.setNote(note);
    	model.setSortNumber(sortNumber);
    	model.setUpdatedAt(new Date());
    	model.setCreatedAt(new Date());
    	
    	if(!model.save()){
    		returnError(ErrorCode.Exception, "创建失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改品牌
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "name", "sortNumber"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int sortNumber = getParaToInt("sortNumber");
    	String name = getPara("name");
    	String note = getPara("note");
    	
    	ProductBrand model =  ProductBrand.dao.findById(id);
    	model.setName(name);
    	model.setNote(note);
    	model.setSortNumber(sortNumber);
    	model.setUpdatedAt(new Date());
    	
    	if(!model.update()){
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	renderJson(jsonObject);
    }
    
    
    
    /**
     * 获取所有品牌
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
    public void search() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	String name = getPara("name");
    	
    	
    	String sqlExceptSelect = "from product_brand ";
    	if(null != name && !name.equals("")) {
    		sqlExceptSelect += "where name like '%"+name+"%'";
    	}
    	
    	sqlExceptSelect += "order by created_at desc";
    	
    	Page<ProductBrand> page = ProductBrand.dao.paginate(offset/length + 1, length, "select *", sqlExceptSelect);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", page.getTotalRow());
    	jsonObject.put("data", page.getList());
    	renderJson(jsonObject);
    }
    
    /**
     * 获取单条品牌
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ProductBrand productBrand = ProductBrand.dao.findById(id);
    	
    	jsonObject.put("data", productBrand);
    	renderJson(jsonObject);
    }
    
    
    /**
     * 批量删除品牌
     */
    @Before(AdminAuthInterceptor.class)
    public void delete() {
		String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
		int id = getParaToInt("id");
    	
    	if(!ProductBrand.dao.deleteById(id)){
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
}