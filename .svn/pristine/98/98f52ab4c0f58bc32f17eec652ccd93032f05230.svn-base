package com.eshop.controller.pc;

import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.model.Category;

/**
 * 购物车控制器
 *   @author TangYiFeng
 */
public class CategoryController extends PcBaseController {
    
    /**
     * 获取下级分类
     * @param id 当前分类id
     * @return 成功:{error:0, data:[{id:id, name:分类名称},...]}, 失败：{error:>0, errmsg:错误信息}
     */
    public void getNextCategories() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	List<Category> list = CategoryService.getNextCategories(id);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}