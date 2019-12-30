package com.eshop.controller.pc;

import java.util.*;

import com.eshop.model.ProductBrand;
import com.jfinal.plugin.activerecord.Record;
import com.eshop.category.CategoryService;
import com.eshop.model.Product;

/**
 * 品牌产品控制器
 *   @author TangYiFeng
 */
public class ProductBrandController extends PcBaseController {
    private CategoryService service;
    
    public ProductBrandController() {
    	service = new CategoryService();
    }
    
    /**
     * 获取下级分类
     * @param id 当前分类id
     * @return 成功:{error:0, data:[{id:id, name:分类名称},...]}, 失败：{error:>0, errmsg:错误信息}
     */
    public void getList() {
    	List<ProductBrand> list = ProductBrand.dao.find("select * from product_brand");
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取品牌下所有产品
     */
    public void getProducts() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	List<Record> list = service.getProductsBrandyId(0, 1000, id);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}