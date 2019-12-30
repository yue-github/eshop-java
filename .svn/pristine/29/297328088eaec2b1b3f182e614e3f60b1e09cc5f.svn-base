package com.eshop.controller.pc;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.model.Refund;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.service.Merchant;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 退款控制器
 * 
 * @author TangYiFeng
 */
public class RefundManageController extends PcBaseController {
	
	private Merchant merchant;

	/**
	 * Default constructor
	 */
	public RefundManageController() {
		merchant = new Merchant();
	}

	/**
	 * 商家退款列表
	 * @param token 帐户访问口令（必填）
	 * @param page 当前页，用于分页
	 * @param length
	 * @param status
	 * @param startTime 申请开始时间
	 * @param endTime 申请结束时间
	 * @return 成功：{error: 0, totalPages:总页数， total: 结果总数,
	 *         refunds:[......]}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void many() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		
		Integer status = getParaToIntegerDefault("status");
		String orderCode = getPara("orderCode");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");

		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int shopId = customer.getShopId();
		
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("a.created_at", "desc");
		
		List<Record> list = User.findRefundItems(offset, length, null, shopId, status, startTime, endTime, orderCode, null, null, orderByMap);
		int total = User.countRefundItems(null, shopId, status, startTime, endTime, null, null, null);
		
		jsonObject.put("refunds", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		renderJson(jsonObject);
	}
	
	/**
     * 获取退款
     * @param token 帐户访问口令（必填）
     * @param id 主键
     * @return 成功：{error: 0, data: {id: 退款id, customerName: 客户名称, customerPhone: 客户手机号, refundType: 退款类型, productName: 产品名称, refundCash: 退款金额, tradeCash: 交易金额, status: 状态}}；失败：{error: >0, errmsg: 错误信息}
     */
	@Before(CustomerPcAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	Refund refund = User.getRefund(id);
    	ProductOrder productOrder = User.getProductOrder(refund.getProductOrderId());
    	Order order = User.getOrder(productOrder.getOrderId());
    	refund.put("order", order);
    	
    	Product product = Merchant.getProduct(productOrder.getProductId());
    	int mainPicId = (product != null) ? product.getMainPic() : 0;
    	String mainPic = ResourceService.getPath(mainPicId);
    	productOrder.put("mainPic", mainPic);
    	refund.put("productOrder", productOrder);
    	
    	Customer customer = Member.getCustomer(refund.getCustomerId());
    	String customerName = BaseDao.getCustomerName(customer);
    	String customerPhone = (customer != null) ? customer.getMobilePhone() : "";
    	refund.put("customerName", customerName);
    	refund.put("customerPhone", customerPhone);
    	
    	double allowDeliveryPrice = User.getAllowDeliveryPriceWithRefund(id);
    	refund.put("allowedDeliveryPrice", allowDeliveryPrice);
    	
    	jsonObject.put("data", refund);
    	renderJson(jsonObject);
    }

	/**
	 * 审核退款
	 * @param token 帐户访问口令（必填）
	 * @param id 退款id
	 * @param status 状态（1审核通过，2审核不通过）
	 * @param refuseNote 审核不通过原因
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void audit() {
		if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if (!this.validateRequiredString("status")) {
    		return;
    	}
    	int status = getParaToInt("status");
    	
    	if (!this.validateRequiredString("applyCash")) {
    		return;
    	}
    	BigDecimal applyCash = getParaToDecimal("applyCash");
    	
    	if (!this.validateRequiredString("deliveryPrice")) {
    		return;
    	}
    	BigDecimal deliveryPrice = getParaToDecimal("deliveryPrice");
    	
    	String refuseNote = getPara("refuseNote");
    	
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		Shop shop = Merchant.getShop(customer.getShopId());
		String name = shop.getContacts();

		ServiceCode code = merchant.auditRefund(id, status, refuseNote, name, applyCash, deliveryPrice);
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "审核失败");
		}

		renderJson(jsonObject);
	}

}