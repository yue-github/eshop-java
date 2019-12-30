package com.eshop.controller.pc;

import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.controller.admin.BaseController;
import com.eshop.model.Category;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.io.*;
/**
 * 商品详情控制器
 * @author TangYiFeng
 */
public class ProductsController extends PcBaseController {
	
	private CategoryService categoryService;

    /**
     * Default constructor
     */
    public ProductsController() {
    	categoryService = new CategoryService();
    }

    /**
     * 获取所有分类
     */
    public void getAllCategories() {
    	//一级分类和二级分类
    	List<Category> categories = CategoryService.getAllCategory();
    	
    	jsonObject.put("categories", categories);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有产品 -根据分类名称
     * @param cateName
     * @param offset
     * @param length
     * @return 成功：{error: 0,totalPage:总页数,totalRow:总行数,products: [{name: 产品名称, summary: 摘要说明, suggestedRetailUnitPrice: 价格, mainPic...}{}]}；失败：{error: >0, errmsg: 错误信息}
     */
    @SuppressWarnings("unlikely-arg-type")
	public void products() {
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	String sort = getPara("sort");
    	String cateName = (getPara("cateName") != null) ? getPara("cateName") : "";
    	
    	HashSet<String> set = new HashSet();
    	set.add(sort);
    	set.add(cateName);
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
		
    	List<Record> list = categoryService.getProductsByCateName(offset, length, cateName, sort);
    	int total = categoryService.countProductsByCateName(cateName);
    	jsonObject.put("cateName", cateName);
    	
    	jsonObject.put("products", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有产品 -根据分类id
     * @param cateId
     * @param offset
     * @param length
     * @return 成功：{error: 0,totalPage:总页数,totalRow:总行数,products: [{name: 产品名称, summary: 摘要说明, suggestedRetailUnitPrice: 价格, mainPic...}{}]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void getProductByCateId() {
    	if (!this.validateRequiredString("cateId")) {
    		return;
    	}
    	int cateId = getParaToInt("cateId");
    	
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	String sort = getPara("sort");
    	List<Record> list = categoryService.getProductsByCateId(offset, length, cateId, sort);
    	int total = categoryService.countProductsByCateId(cateId);
    	
    	jsonObject.put("products", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有产品
     * @param offset
     * @param length
     * @return 成功：{error: 0,totalPage:总页数,totalRow:总行数,products: [{name: 产品名称, summary: 摘要说明, suggestedRetailUnitPrice: 价格, mainPic...}{}]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void getProductAll() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String sort = getPara("sort");
    	List<Record> list = categoryService.getProducts(offset, length, sort);
    	int total = categoryService.countProducts();
    	
    	jsonObject.put("products", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
}