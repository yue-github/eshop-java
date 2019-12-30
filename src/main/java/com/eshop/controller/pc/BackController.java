package com.eshop.controller.pc;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Back;
import com.eshop.model.Customer;
import com.eshop.model.Order;
import com.eshop.model.ProductOrder;
import com.eshop.model.Refund;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 退货控制器
 *   @author TangYiFeng
 */
public class BackController extends PcBaseController {
	
    /**
     * Default constructor
     */
    public BackController() {
    }

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
     * @return 成功：{error: 0,error:-1(已申请退货，不能再申请),-2(还没发货，不能申请退货)}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void submitBack() {
    	String[] params = {"productOrderId", "refundAmount", "refundCash", "isGeted", "reason", "note"};
		
		if (!this.validate(params)) {
			return;
		}
		
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	int productOrderId = getParaToInt("productOrderId");
    	int refundAmount = getParaToInt("refundAmount");
    	BigDecimal refundCash = getParaToDecimal("refundCash");
    	boolean isGeted = getParaToBoolean("isGeted");
    	String reason = getPara("reason");
    	String note = getPara("note");
    	String pics = getPara("pics");
    	
    	Back model = new Back();
    	model.setCustomerId(customerId);
    	model.setProductOrderId(productOrderId);
    	model.setRefundAmount(refundAmount);
    	model.setRefundCash(refundCash);
    	model.setApplyCash(refundCash);
    	model.setIsGeted(isGeted);
    	model.setReason(reason);
    	model.setNote(note);
    	model.setStatus(0);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	int code = Member.returned(model, pics);
    	
    	// -4退货金额不正确，-3退货数量不正确，-2还未发货不能申请退货，-1已申请过退货，0成功，1失败
    	if (code == -1) {
    		returnError(-1, "已申请退货，不能再重复申请");
    		return;
    	}
    	
    	if (code == -2) {
    		returnError(-2, "订单还没发货，请点击申请退款");
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
    @Before(CustomerPcAuthInterceptor.class)
    public void submitRefund() {
    	String[] params = {"productOrderId", "refundAmount", "refundCash", "reason", "note", 
    			"couponDiscount"};
    	
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	int productOrderId = getParaToInt("productOrderId");
    	int refundAmount = getParaToInt("refundAmount");
    	BigDecimal refundCash = getParaToDecimal("refundCash");
    	String reason = getPara("reason");
    	String note = getPara("note");
    	String pics = getPara("pics");
    	
    	Refund model = new Refund();
    	model.setCustomerId(customerId);
    	model.setProductOrderId(productOrderId);
    	model.setRefundAmount(refundAmount);
    	model.setRefundCash(refundCash);
    	model.setApplyCash(refundCash);
    	model.setReason(reason);
    	model.setNote(note);
    	model.setStatus(0);
    	model.setShopId(1);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	ServiceCode code = Member.refund(model, pics);
    	
    	if(code == ServiceCode.Function){
    		returnError(-1, "已申请过退款，不能再申请");
    		return;
    	}
    	
    	if(code == ServiceCode.Validation){
    		returnError(-2, "订单已发货，请点击申请退货");
    		return;
    	}
    	
    	if(code != ServiceCode.Success){
    		returnError(ErrorCode.Exception, "申请退款失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 我的退货
     * @param token 帐户访问口令（必填）
     * @param offset 页码（必填）
     * @param length 每页条数（必填）
     * @param orderCode 订单号（选填）
     * @param status 退货状态（选填）
     * @return 成功：{error: 0, data: [{id: 主键, orderTime 订单时间, orderCode:订单号, productId: 产品id, productName: 产品名称, summary: 摘要说明, actualUnitPrice: 价格, mainPic: 图片, isGeted 是否已收到货, refundAmount 退货数量, tradeCash交易金额, refundCash退款金额, reason 退款原因， status:状态}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getBacks() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String orderCode = getPara("orderCode");
    	Integer status = getParaToIntegerDefault("status");
    	
    	String token = getPara("token");
    	HashSet<String> set = new HashSet();
    	set.add(orderCode);
    	set.add(token);
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
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> list = User.findReturnedItems(offset, length, customerId, null, status, null, null, null, null, orderCode, null, null, null, orderByMap);
    	int total = User.countReturnedItems(customerId, null, status, null, null, null, null, orderCode, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 我的退款
     * @param token 帐户访问口令（必填）
     * @param offset 页码（必填）
     * @param length 每页条数（必填）
     * @param orderCode 订单号（选填）
     * @param status 退款状态（选填）
     * @return 成功：{error: 0, data: [{id: 主键, orderTime 订单时间, orderCode:订单号, productId: 产品id, ProductName: 产品名称, summary: 摘要说明, actualUnitPrice: 价格, mainPic: 图片, refundAmount 退货数量, tradeCash交易金额, refundCash退款金额, reason 退款原因， status:状态}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @SuppressWarnings("unlikely-arg-type")
	@Before(CustomerPcAuthInterceptor.class)
    public void getRefunds() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String orderCode = getPara("orderCode");
    	Integer status = getParaToIntegerDefault("status");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	String[] array = {orderCode, token};
    	HashSet<String> set = new HashSet();
    	set.add(orderCode);
    	set.add(token);
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
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> list = User.findRefundItems(offset, length, customerId, null, status, null, null, orderCode, null, null, orderByMap);
    	int total = User.countRefundItems(customerId, null, status, null, null, orderCode, null, null);
    			
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 根据productOrderId获取订单信息
     * @param token 帐户访问口令（必填）
     * @param id 产品订单id
     * @return 成功：{error: 0, order:{id:订单id, order_code:订单号, address:收货地址, completeTime：成交时间}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getOrder() {
    	int id = getParaToInt("id");
    	ProductOrder productOrder = ProductOrder.dao.findById(id);
    	
    	if (productOrder == null) {
    		jsonObject.put("error", 1);
    		jsonObject.put("errmsg", "该产品订单不存在");
    		renderJson(jsonObject);
    		return;
    	}
    	
    	int orderId = productOrder.getOrderId();
    	Order order = User.getOrder(orderId);
    	
    	if (order == null) {
    		jsonObject.put("error", 2);
    		jsonObject.put("errmsg", "该订单不存在");
    		renderJson(jsonObject);
    		return;
    	}
    	
    	jsonObject.put("order", order);
    	renderJson(jsonObject);
    }
    /**
     * 获取退货商品
     *  @param token 帐户访问口令（必填）
     *  @param productOrderId 产品订单id（必填）
     *  @return 成功：{error: 0, productOrder: {id:id, product_name:产品名称, mainPic:产品主图, actualUnitPrice:价格, unitOrdered:数量, totalActualProductPrice:总价, shopName:店铺名称}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getProductOrder() {
    	String[] params = {"productOrderId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
        int productOrderId = getParaToInt("productOrderId");  
        ProductOrder productOrder = User.getProductOrder(productOrderId);
        
        if (productOrder == null) {
        	setError(ErrorCode.Exception, "该产品订单不存在");
        	return;
        }
        
        String mainPic = productOrder.get("mainPic");
        productOrder.put("mainPic", mainPic);
        
        jsonObject.put("productOrder", productOrder);
        renderJson(jsonObject);
    }

}