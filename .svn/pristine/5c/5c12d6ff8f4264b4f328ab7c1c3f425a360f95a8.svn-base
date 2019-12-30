package com.eshop.controller.webapp;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eshop.helper.AlipayHelper;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.MathHelper;
import com.eshop.helper.UnionpayHelper;
import com.eshop.helper.WxPayPCHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.invoice.PlainInvoiceService;
import com.eshop.logistics.CustomerAddress;
import com.eshop.logistics.Logistics;
import com.eshop.model.Address;
import com.eshop.model.Customer;
import com.eshop.model.Order;
import com.eshop.model.PlainInvoice;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BasePromotion;
import com.eshop.service.Member;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 *   确认订单控制器
 *   @author TangYiFeng
 */
public class ConfirmController extends WebappBaseController {
	
	private Logistics logistics;
	private Member member;
	
	public ConfirmController() {
		logistics = new Logistics();
		member = new Member();
	}

    /**
     * 我的收货地址，默认地址排序第一
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0, addresses: [{id: 地址id, contacts: 姓名, phone: 手机号, isDefault: 是否默认地址(true, false), province: 省, city: 市, district: 区, detailedAddress: 详细地址}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void many() {
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Address> address = CustomerAddress.findAddressesItems(customerId, null, null);
    	
    	jsonObject.put("addresses",address);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取待提交的订单列表 -直接购买
     * @param token
     * @param id 产品id（必填）
     * @param amount 数量（必填）
     * @param priceId 价格id,没有则填0（必填）
     * @param address_id 地址id 选填
     * @return 成功：{error: 0,data:{allPrice:总金额(包含运费), totalAmount:总购买数量, shopList:[{shop_id:店铺id,shopName:店铺名称,shopLogo:店铺logo,deliveryPrice:运费, productList:[{product_id:产品id,productName:产品名称,mainPic:产品主图,selectProterties:所选属性,amount:购买数量,suggestedRetailUnitPrice:产品单价,totalPrice:产品总价},...]}]}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void productInfo() {
    	String[] params = {"id", "priceId", "amount"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int priceId = getParaToInt("priceId");
    	int amount = getParaToInt("amount");
    	String startAt = getPara("startAt");
    	String endAt = getPara("endAt");
    	
    	int addressId = 0;
    	if ((getPara("address_id") != null && !getPara("address_id").equals(""))) {
    		addressId = getParaToInt("address_id");
    	}
    	
    	Record other = new Record();
    	other.set("address_id", addressId);
    	
    	List<Record> list = Member.deliveryOrdersByDirect(id, priceId, amount, startAt, endAt, addressId);
    	List<Record> products = Member.getProductsByDirect(id, priceId, amount, startAt, endAt);
    	double totalProdPrice = Member.calculateProductTotalPayable(products);
    	double totalAmount = Member.calculateProductTotalAmount(products);
    	double totalPromDiscount = BasePromotion.calculateDiscount(products);
    	double totalCouponDiscount = 0;
    	double deliveryPrice = logistics.calculateDeliveryPrice(list, other);
    	double totalPayable = totalProdPrice + deliveryPrice - totalPromDiscount - totalCouponDiscount;
    	
    	jsonObject.put("data", list);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("totalPromDiscount", totalPromDiscount);
    	jsonObject.put("totalCouponDiscount", totalCouponDiscount);
    	jsonObject.put("deliveryPrice", deliveryPrice);
    	jsonObject.put("totalPayable", totalPayable);
    	renderJson(jsonObject);
    }
    
    /**
     * 提交订单-直接购物
     * @param token 帐户访问口令（必填）
     * @param address_id 地址id 必填
     * @param source 平台类型 必填
     * @param payType 支付类型 必填
     * @param couponId 优惠券id 选填
     * @param productId 产品id 必填
     * @param priceId 单品id 必填
     * @param amount 购买数量 必填
     * @return 成功：{error: 0,payType:支付类型(1微信,2支付宝),payInfo:支付信息}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void saveOrder() {
    	String[] params = {"address_id", "source", "payType", "productId", "priceId", "amount"};
    	if (!this.validate(params)) {
    		return;
    	}
		double totalPayable = getParaToDoubleDefault("totalPayable");
    	int addressId = getParaToInt("address_id");
    	int source = getParaToInt("source");
    	int payType = getParaToInt("payType");
    	int productId = getParaToInt("productId");
    	int priceId = getParaToInt("priceId");
    	int amount = getParaToInt("amount");
    	int couponId = 0;
    	String startAt = getPara("startAt");
    	String endAt = getPara("endAt");
    	if ((getPara("couponId") != null && !getPara("couponId").equals(""))) {
    		couponId = getParaToInt("couponId");
    	}
    	
    	Product product = Product.dao.findById(productId);
    	int prodType = product.getProdType();
    	
    	Record other = new Record();
    	other.set("address_id", addressId);
    	other.set("source", source);
    	other.set("payType", payType);
    	other.set("couponId", couponId);
    	other.set("order_type", prodType);
    	other.set("note", getPara("note"));
    	
    	if (getPara("invoice") != null) {
    		String invoiceStr = getPara("invoice");
    		JSONObject object = JSON.parseObject(invoiceStr);
    		
    		Record invoice = new Record();
    		invoice.set("type", object.getInteger("type"));
    		invoice.set("invoiceHead", object.getString("invoiceHead"));
    		invoice.set("invoiceContent", object.getString("invoiceContent"));
    		
    		other.set("invoice", invoice);
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
		CacheHelper.put("totalPayable",totalPayable);
    	String theSameOrderNum = member.submitOrderWithProduct(customerId, productId, priceId, amount, 
    			other, startAt, endAt);
    	
    	if (theSameOrderNum == null) {
    		returnError(ErrorCode.Exception, "提交订单失败");
    		return;
    	}
    	
    	double totalPay = Member.calculateOrderPayable(theSameOrderNum);
    	totalPay = MathHelper.cutDecimal(totalPay);
    	int totalPayInt = (int) (totalPay * 100);
    	
    	String payInfo = "";
    	
    	if (payType == 1) {   //微信支付
    		
    	} else if (payType == 2) {  //支付宝支付
    		payInfo = AlipayHelper.getUrl(theSameOrderNum + "", "乐驿商城", totalPay + "", "");
    		
    	} else if (payType == 3) {  //银联支付
    		payInfo = UnionpayHelper.getHtml(totalPayInt + "", theSameOrderNum + "");
    		
    	} else if (payType == 4) {
    		this.returnError(-1, "非法操作");
    	}
    	
    	jsonObject.put("theSameOrderNum", theSameOrderNum);
    	jsonObject.put("orderCode", "");
    	jsonObject.put("orderId", 0);
    	jsonObject.put("payType", payType);
    	jsonObject.put("payInfo", payInfo);
    	jsonObject.put("totalFee", totalPay);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取待提交的订单列表 -购物车
     * @param token
     * @param address_id
     * @return 成功：{error:0, data:{allPrice:总订单金额,shopList:[{shop_id:店铺id,shopName:店铺名称,shopLogo:店铺logo,totalPayable:店铺订单总金额(包含运费),deliveryPrice:店铺订单运费,couponList:[{id:优惠券id,type:优惠券类型(1折扣券,2现金quan),title:标题},...],productList:[{product_id:产品id,productName:产品名称,mainPic:产品主图,selectProterties:所选属性,amount:购买数量,suggestedRetailUnitPrice:产品价格,totalPrice:总价},...]},...]}}
     * 		       失败：{error:>0, errmsg:错误信息}	
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void getDeliveryList() {
    	int addressId = (getPara("address_id") != null && !getPara("address_id").equals("")) ? getParaToInt("address_id") : 0;
    	Record other = new Record();
    	other.set("address_id", addressId);
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = Member.deliveryOrdersByShopCart(customerId, addressId);
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, 1);
    	
    	double totalProdPrice = Member.calculateProductTotalPayable(products);
    	double totalAmount = Member.calculateProductTotalAmount(products);
    	double totalPromDiscount = BasePromotion.calculateDiscount(products);
    	double totalCouponDiscount = 0;
    	double deliveryPrice = logistics.calculateDeliveryPrice(list, other);
    	double totalPayable = totalProdPrice + deliveryPrice - totalPromDiscount - totalCouponDiscount;

    	jsonObject.put("data", list);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("totalPromDiscount", totalPromDiscount);
    	jsonObject.put("totalCouponDiscount", totalCouponDiscount);
    	jsonObject.put("deliveryPrice", deliveryPrice);
    	jsonObject.put("totalPayable", totalPayable);
    	renderJson(jsonObject);
    }
    
    /**
     * 提交订单-购物车
     * @param token 帐户访问口令（必填）
     * @param address_id
     * @param source
     * @param payType
     * @param couponId 选填
     * @param invoice:{type:发票类型(1普通发票,2电子发票,3增值税发票),invoiceHead:发票抬头,invoiceContent:发票内容}
     * @return 成功：{error: 0,payType:支付类型(1微信,2支付宝),payInfo:支付信息}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void saveOrderByCart() {
    	String[] params = {"address_id", "source", "payType"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int addressId = getParaToInt("address_id");
    	int source = getParaToInt("source");
    	int payType = getParaToInt("payType");
    	int couponId = (getPara("couponId") != null && !getPara("couponId").equals("")) ? getParaToInt("couponId") : 0;
    	
    	Record other = new Record();
    	other.set("address_id", addressId);
    	other.set("source", source);
    	other.set("payType", payType);
    	other.set("couponId", couponId);
    	other.set("order_type", 0);
    	other.set("note", getPara("note"));
    	
    	if (getPara("invoice") != null) {
    		String invoiceStr = getPara("invoice");
    		JSONObject object = JSON.parseObject(invoiceStr);
    		
    		Record invoice = new Record();
    		invoice.set("type", object.getInteger("type"));
    		invoice.set("invoiceHead", object.getString("invoiceHead"));
    		invoice.set("invoiceContent", object.getString("invoiceContent"));
    		
    		other.set("invoice", invoice);
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	String theSameOrderNum = member.submitOrderWithShoppingCart(customerId, other);
    	if (theSameOrderNum == null) {
    		returnError(ErrorCode.Exception, "提交订单失败");
    		return;
    	}
    	
    	double totalPay = Member.calculateOrderPayable(theSameOrderNum);
    	totalPay = MathHelper.cutDecimal(totalPay);
    	int totalPayInt = (int) new BigDecimal(totalPay + "").multiply(new BigDecimal(100 + "")).doubleValue();
    	
    	String payInfo = "";
    	if (payType == 1) {   //微信支付
    		
    	} else if (payType == 2) {  //支付宝支付
    		payInfo = AlipayHelper.getUrl(theSameOrderNum, "乐驿商城", totalPay + "", "");
    	} else if (payType == 3) {  //银联支付
    		payInfo = UnionpayHelper.getHtml(totalPayInt + "", theSameOrderNum);
    	} else if (payType == 4) {  //余额支付
    		this.returnError(-1, "非法操作");
    	}
    	
    	jsonObject.put("theSameOrderNum", theSameOrderNum);
    	jsonObject.put("orderCode", "");
    	jsonObject.put("orderId", 0);
    	jsonObject.put("payType", payType);
    	jsonObject.put("payInfo", payInfo);
    	jsonObject.put("totalFee", totalPay);
    	renderJson(jsonObject);
    }
    
    /**
     * 支付订单
     * @param theSameOrder 商户订单号
     * @return 成功：{error: 0,data:{payType:支付类型(1微信,2支付宝),payInfo:支付信息}}；失败：{error: >0, errmsg: 错误信息}
     */
    public void payOrder() {
    	if (!this.validateRequiredString("theSameOrder")) {
    		return;
    	}
    	String theSameOrderNum = getPara("theSameOrder");
    	
    	Order order = Member.getOrder(theSameOrderNum);
    	
    	if (order == null) {
    		returnError(ErrorCode.Exception, "支付失败");
    	}
    	
    	int payType = order.getPayType();
    	double totalPay = Member.calculateOrderPayable(theSameOrderNum);
    	totalPay = MathHelper.cutDecimal(totalPay);
    	int totalPayInt = (int) (totalPay * 100);
    	
    	String payInfo = "";
    	
    	if (payType == 1) {   //微信支付
    		payInfo = WxPayPCHelper.wxpay("乐驿商城", theSameOrderNum, totalPayInt + "", this.getRequest());
    		String path = this.getBaseUrl();
    		if (path.indexOf("eebin.com") != -1) {
    			path = this.getHostName() + "/";
    		}
    		payInfo = path + payInfo;
    	} else if (payType == 2) {  //支付宝支付
    		payInfo = AlipayHelper.getUrl(theSameOrderNum, "乐驿商城", totalPay + "", "");
    	} else if (payType == 3) {  //银联支付
    		payInfo = UnionpayHelper.getHtml(totalPayInt + "", theSameOrderNum);
    	} else if (payType == 4) {  //余额支付
    		this.returnError(-1, "非法操作");
    	}
    	
    	jsonObject.put("theSameOrderNum", theSameOrderNum);
    	jsonObject.put("payType", payType);
    	jsonObject.put("payInfo", payInfo);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改立即购买产品数量
     * @param productId
     * @param priceId
     * @param amount
     * @return 成功：{error:0,error:-1(库存不足),storeAmount:库存数量} 失败：{error:1}
     */
    public void changeOrderProductAmount() {
    	String[] params = {"productId", "priceId", "amount"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int productId = getParaToInt("productId");
    	int priceId = getParaToInt("priceId");
    	int amount = getParaToInt("amount");
    	
    	int currentStoreAmount = BaseDao.getStoreAmount(productId, priceId);
    	
    	if (amount > currentStoreAmount) {
    		setError(-1, "库存不足");
    	}
    	
    	jsonObject.put("storeAmount", currentStoreAmount);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取发票抬头列表
     * @param token
     * @return 成功：{error:0, data:[id:id, invoiceName:发票抬头]} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getinVoice() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	int offset = 0;
    	int length = 5;
    	List<Record> list = PlainInvoiceService.findPlainInvoiceItems(offset, length, customerId, null, null);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 添加发票抬头
     * @param token
     * @param invoiceHead 发票抬头
     * @return 成功：{error:0} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void createInvoice() {
    	String[] params = {"invoiceHead", "invoiceContent"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String companyCode = getPara("companyCode");
    	int invoiceContent = getParaToInt("invoiceContent");
    	String invoiceHead = getPara("invoiceHead");
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	PlainInvoice model = new PlainInvoice();
    	model.setCustomerId(customerId);
    	model.setInvoiceHead(invoiceHead);
    	model.setType(PlainInvoiceService.TYPE_COMPANY);
    	model.setCompanyCode(companyCode);
    	model.setInvoiceContent(invoiceContent);
    	
    	ServiceCode code = PlainInvoiceService.create(model);
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除发票抬头
     * @param token
     * @param id 发票抬头id
     * @return 成功：{error:0} 失败：{error: >0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void deleteInvoice() {
    	String[] params = {"id"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = PlainInvoiceService.delete(id);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}