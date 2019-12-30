package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.service.Member;
import com.jfinal.plugin.activerecord.Record;

/**
 *   商品详情评论控制器
 *   @author TangYiFeng
 */
public class ProductCommentsController extends WebappBaseController {

    /**
     * 构造方法
     */
    public ProductCommentsController() {
    }

    /**
     * 获取产品评价
     * @param id 产品id（必填）
     * @param offset 必填
     * @param length 必填
     * @return 成功：{error: 0, reviews: [{nickName: 会员昵称, headImg: 头像, comments: 评论内容, ratings: 评价等级, created_at: 评论时间, pics: [图片路径, ...]}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    public void productReview() {
    	String[] params = {"id", "offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("id");
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	List<Record> data = Member.findCommentItems(offset, length, productId, null, null, null, null, null, null, null);
    	int totalRow = Member.countCommentItems(productId, null, null, null, null, null, null, null);
    	
    	jsonObject.put("reviews", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }

}