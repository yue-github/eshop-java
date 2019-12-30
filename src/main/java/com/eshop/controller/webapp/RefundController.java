package com.eshop.controller.webapp;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.Refund;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.jfinal.aop.Before;

/**
 *   退款控制器
 *   @author TangYiFeng
 */
public class RefundController extends WebappBaseController {

    /**
     * 构造方法
     */
    public RefundController() {
    }

    /**
     * 提交退款申请
     * @param token 帐户访问口令（必填）
     * @param productOrderId 订单明细id（必填）
     * @param refundAmount 退款数量（必填）
     * @param refundCash退款金额（必填）
     * @param reason 退款原因（必填）
     * @param note 退款说明（必填）
     * @param pics 图片凭证（[图片路径1,图片路径2,...]）
     * @return 成功：{error: 0,-1(已退款，不能再申请退款),-2(不是待发货状态，不能申请退款)}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void submitRefund() {
    	String[] params = {"productOrderId", "refundAmount", "refundCash", "reason", "note"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productOrderId = getParaToInt("productOrderId");
    	int refundAmount = getParaToInt("refundAmount");
    	BigDecimal refundCash = getParaToDecimal("refundCash");
    	String reason = getPara("reason");
    	String note = getPara("note");
    	String pics = getPara("pics");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Refund model = new Refund();
    	model.setCustomerId(customerId);
    	model.setProductOrderId(productOrderId);
    	model.setRefundAmount(refundAmount);
    	model.setRefundCash(refundCash);
    	model.setReason(reason);
    	model.setNote(note);
    	model.setStatus(0);
    	model.setShopId(1);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	model.setRefundTime(new Date());
    	
    	ServiceCode code = Member.refund(model, pics);
    	
    	if(code == ServiceCode.Function){
    		returnError(-1, "已申请过退款，不能再申请");
    		return;
    	}
    	
    	if(code == ServiceCode.Validation){
    		returnError(-2, "不是待发货状态，不能申请退款");
    		return;
    	}
    	
    	if(code != ServiceCode.Success){
    		returnError(ErrorCode.Exception, "申请退款失败");
    	}
    	
    	renderJson(jsonObject);
    }

}