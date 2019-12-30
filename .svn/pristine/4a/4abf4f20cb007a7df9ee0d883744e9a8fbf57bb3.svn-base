package com.eshop.controller.webapp;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Back;
import com.eshop.model.Customer;
import com.eshop.model.Shop;
import com.eshop.service.Member;
import com.jfinal.aop.Before;

/**
 *   退货控制器
 *   @author TangYiFeng
 */
public class BackGoodsController extends WebappBaseController {

    /**
     * 提交退货申请
     * @param token 帐户访问口令（必填）
     * @param productOrderId 订单明细id（必填）
     * @param refundAmount 退货数量（必填）
     * @param refundCash 退款金额（必填）
     * @param isGeted 是否已收到货（必填）
     * @param reason 退款原因（必填）
     * @param note 退款说明（必填）
     * @param pics 图片凭证（[图片路径1,图片路径2,...]）
     * @return 成功：{error: 0,-1(已退款，不能再申请退款),-2(不是待发货状态，不能申请退款)}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void submitBack() {
    	String[] params = {"productOrderId", "refundAmount", "refundCash", "isGeted", "reason", "note"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productOrderId = getParaToInt("productOrderId");
    	int refundAmount = getParaToInt("refundAmount");
    	boolean isGeted = getParaToBoolean("isGeted");
    	BigDecimal refundCash = getParaToDecimal("refundCash");
    	String reason = getPara("reason");
    	String note = getPara("note");
    	String pics = getPara("pics");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Back model = new Back();
    	model.setCustomerId(customerId);
    	model.setProductOrderId(productOrderId);
    	model.setRefundAmount(refundAmount);
    	model.setRefundCash(refundCash);
    	model.setIsGeted(isGeted);
    	model.setReason(reason);
    	model.setNote(note);
    	model.setStatus(0);
    	model.setShopId(1);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	int code = Member.returned(model, pics);
    	
    	if (code == -1) {
    		returnError(-1, "已申请退货，不能再重复申请");
    		return;
    	}
    	
    	if (code == -2) {
    		returnError(-2, "还没发货，不能申请退货");
    		return;
    	}
    	
    	if (code == -3) {
    		returnError(-3, "退货数量不正确");
    		return;
    	}
    	
    	if (code == -4) {
    		returnError(-4, "退货金额不正确");
    		return;
    	}
    	
    	if(code != 0){
    		returnError(ErrorCode.Exception, "申请退货失败");
    		return;
    	}
    	
    	if (code == 0) {
    		//客户退款通知
        	Shop shop = Member.getShopByproductOrderId(productOrderId);
        	Member.informShop(shop, "【乐驿商城】您有一条退货申请，请前往处理", "【乐驿商城】退货申请提醒", "【乐驿商城】尊敬的商家您好，您有一条退货申请，请前往处理");
    	}
    	
    	renderJson(jsonObject);
    }

}