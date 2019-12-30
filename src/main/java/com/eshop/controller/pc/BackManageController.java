package com.eshop.controller.pc;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Back;
import com.eshop.model.Customer;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.service.Merchant;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 退货控制器
 *   @author TangYiFeng
 */
public class BackManageController extends PcBaseController {
	
	private Merchant merchant;
	
    /**
     * Default constructor
     */
    public BackManageController() {
    	merchant = new Merchant();
    }

    /**
     * 商家退货列表
     *  @param token 帐户访问口令（必填）
     *  @param offset 当前页，用于分页
     *  @param length
     *  @param status 状态(-1所有)
     *  @param orderCode 订单号
     *  @param startTime 开始时间
     *  @return 成功：{error: 0, totalPages:总页数， total: 结果总数, backs:[{id,...}...]}；失败：{error: >0, errmsg: 错误信息}
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
    	String startTime = getPara("startTime");
    	String orderCode = getPara("orderCode");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	Integer shopId = customer.getShopId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
        List<Record> list = User.findReturnedItems(offset, length, null, shopId, status, startTime, null, null, null, orderCode, null, null, null, orderByMap);
        int total = User.countReturnedItems(null, shopId, status, startTime, null, null, null, orderCode, null, null, null);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("total", total);
    	jsonObject.put("list", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取一条退货
     * @param token 帐户访问口令（必填）
     * @param id 主键
     * @return 成功：{error: 0, data: {id: 退货id, customerName: 客户名称, customerPhone: 客户手机号, backType: 退货类型, productName: 产品名称, refundCash: 退款金额, tradeCash: 交易金额, status: 状态}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Back back = User.getReturned(id);
    	
    	ProductOrder productOrder = User.getProductOrder(back.getProductOrderId());
    	Order order = User.getOrder(productOrder.getOrderId());
    	back.put("order", order);
    	
    	Product product = Merchant.getProduct(productOrder.getProductId());
    	String mainPic = ResourceService.getPath(product.getMainPic());
    	productOrder.put("mainPic", mainPic);
    	back.put("productOrder", productOrder);
    	
    	Customer customer = Member.getCustomer(back.getCustomerId());
    	String customerName = BaseDao.getCustomerName(customer);
    	String customerPhone = (customer != null) ? customer.getMobilePhone() : "";
    	back.put("customerName", customerName);
    	back.put("customerPhone", customerPhone);
    	
    	double allowDeliveryPrice = User.getAllowDeliveryPriceWithBack(id);
    	back.put("allowedDeliveryPrice", allowDeliveryPrice);
    	
    	jsonObject.put("data", back);
    	renderJson(jsonObject);
    }

    /**
     * 审核退货
     * @param token 帐户访问口令（必填）
     * @param id 退货id（必填）
     * @param status 状态（1审核通过，2审核不通过）（必填）
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
    	
    	BigDecimal deliveryPrice = (getPara("deliveryPrice") != null) ? getParaToDecimal("deliveryPrice") : new BigDecimal(0);
    	String refuseNote = (getPara("refuseNote") != null) ?  getPara("refuseNote") : "";
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	
    	ServiceCode code = merchant.auditReturned(id, status, refuseNote, customer.getName(), applyCash, deliveryPrice);
    	
    	if (code != ServiceCode.Success) {
    		jsonObject.put("error", 1);
    		jsonObject.put("errmsg", "审核失败");
    	}
    	
    	renderJson(jsonObject);
    }
}