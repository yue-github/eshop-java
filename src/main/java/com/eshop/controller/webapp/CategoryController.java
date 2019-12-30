package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.model.Category;

/**
 * 分类控制器
 * @author TangYiFeng
 */
public class CategoryController extends WebappBaseController {

    /**
     * 获取所有第一级分类
     * @return 成功：{error: 0,data:[{id:id,name:分类名称},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void getAllCategories() {
    	List<Category> data = CategoryService.getAllFirstCategories();
    	
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取子分类
     * @param cateId 分类id
     * @return 成功：{error: 0,data:[{id:id,name:分类名称,childCateList:[{id:id,name:分类名称,mainPic:分类图片},...]},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void getProductByCateId() {
    	if (!this.validateRequiredString("cateId")) {
    		return;
    	}
    	
    	int cateId = getParaToInt("cateId");
    	List<Category> list = CategoryService.getChildCates(cateId);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
}