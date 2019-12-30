package com.eshop.controller.webapp;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.Product;
import com.eshop.model.ProductReview;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 *   评价控制器
 *   @author TangYiFeng
 */
public class CommentController extends WebappBaseController {

    /**
     * 评价产品订单
     * @param token 用户登录口令(必填)
     * @param productId 产品订单id(必填)
     * @param comment 评价(必填)
     * @param ratings 评价等级（必填）
     * @param imgs 图片  格式[path, path, ...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void submit() {
    	String[] params = {"productId", "productOrderId", "comment", "ratings"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	Integer productOrderId = getParaToInt("productOrderId");
    	int productId = getParaToInt("productId");
    	int ratings = getParaToInt("ratings");
    	String comments = getPara("comment");
    	String img = getPara("imgs");
    	
    	List<String> imgs = JSON.parseArray(img, String.class);
    	
    	ProductReview model = new ProductReview();
    	model.setProductId(productId);
    	model.setCustomerId(customerId);
    	model.setComments(comments);
    	model.setRatings(ratings);
    	
    	if(Member.submitComment(model, productOrderId,  imgs) != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "评论产品失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取订单明细信息
     * @param productOrderId 产品订单id
     * @return 成功：{error:0,data:{id:产品订单id,mainPic:产品主图,actualUnitPrice:价格,unitOrdered:购买数量}} 失败：{error:>0,errmsg:错误信息}
     */
    public void getProductOrder() {
    	if (!this.validateRequiredString("productOrderId")) {
    		return;
    	}
    	
    	int productOrderId = getParaToInt("productOrderId");
    	Record model = Db.findById("product_order", productOrderId);
    	Product product = Merchant.getProduct(model.getInt("product_id"));
    	String mainPic = (product != null) ? ResourceService.getPath(product.getMainPic()) : "";
    	model.set("mainPic", mainPic);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    
    
    

}