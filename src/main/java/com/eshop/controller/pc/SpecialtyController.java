package com.eshop.controller.pc;

import java.util.*;

import com.eshop.content.ContentService;
import com.eshop.model.Category;

/**
 * 商品详情控制器
 *   @author TangYiFeng
 */
public class SpecialtyController extends PcBaseController {

    /**
     * Default constructor
     */
    public SpecialtyController() {
    }

    /**
     * 获取地方特产下的分类
     * @param cateName
     * @return 成功：{error:0 data:[{{id,name,parent_id,sortNumber,note...}...}]} 失败：{error: >0, errmsg: 错误信息}
     */
    public void categories() {
    	String cateName = "土特产";
    	if (getPara("cateName") != null) {
    		cateName = getPara("cateName");
    	}
    	
    	List<Category> list = ContentService.getSpecialtyCategories(cateName);
    	    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}