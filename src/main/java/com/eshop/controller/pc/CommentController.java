package com.eshop.controller.pc;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.membership.CustomerGrowthService;
import com.eshop.membership.CustomerPointService;
import com.eshop.membership.MemberShip;
import com.eshop.model.Customer;
import com.eshop.model.ProductOrder;
import com.eshop.model.ProductReview;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.service.User;
import com.jfinal.aop.Before;

/**
 * 订单评价控制器
 *   @author TangYiFeng
 */
public class CommentController extends PcBaseController {
	
    
    /**
     * Default constructor
     */
    public CommentController() {
    }

    /**
     * 评价订单产品
     * @param token 用户登录口令(必填)
     * @param productOrderId 产品订单id(必填)
     * @param comment 评价(必填)
     * @param ratings 评价等级(必填)
     * @param imgs 图片  格式[path, path, ...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void submit() {
    	String[] params = {"productOrderId", "comment", "ratings"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int ratings = getParaToInt("ratings");
    	int productOrderId = getParaToInt("productOrderId");
    	String comments = getPara("comment");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	String img = getPara("imgs");
    	List<String> imgs = JSON.parseArray(img, String.class);
    	
    	ProductOrder productOrder = User.getProductOrder(productOrderId);
    	int productId = productOrder.getProductId();
    	
    	ProductReview model = new ProductReview();
    	model.setCustomerId(customerId);
    	model.setComments(comments);
    	model.setRatings(ratings);
    	model.setProductId(productId);
    	
    	if(Member.submitComment(model,productOrderId, imgs) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "评论商品失败");
    		return;
    	}
    	
    	//通知商家，买家评论订单产品
    	Shop shop = Member.getShopByproductOrderId(productOrderId);
    	String subject = "【乐驿商城】尊敬的商家您好，您有一条订单产品评价信息，请前往查看";
    	String content = "【乐驿商城】买家评价提醒";
    	Member.informShop(shop, content, subject, content);
    	
    	//评价成功 +积分
    	CustomerPointService.updatePoint(customerId, 1, MemberShip.SUBMIT_COMMENT_POINT, "评论商品");
    	//评价成功 +成长值
    	CustomerGrowthService.updateGrowth(customerId, 1, MemberShip.SUBMIT_COMMENT_GROWTH, "评论商品");
    	
    	renderJson(jsonObject);
    }


}