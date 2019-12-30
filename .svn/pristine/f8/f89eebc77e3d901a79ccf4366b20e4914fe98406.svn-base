package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eshop.content.ResourceService;
import com.eshop.finance.ExcelService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.helper.MathHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.model.Refund;
import com.eshop.model.User;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 退货管理-控制器
 * @author TangYiFeng
 */
public class RefundController extends AdminBaseController {
	
	Merchant merchant;
	
    /**
     * 构造方法
     */
    public RefundController() {
    	merchant = new Merchant();
    }
    
    /**
     * 获取所有退款（按退款时间排序）
     * @param token 帐户访问口令（必填）
     * @param offset 页码
     * @param length 每页显示条数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 状态(0未审核,2审核不通过,1审核通过,4退款成功) 选填
     * @param order_code 订单号  选填
     * @param tradeCode  商户单号  选填
     * @param tradeNo  流水号   选填
     * @return 成功：{error: 0, offset: 页码, totalRefundCash:退款总金额, totalTradeCash:交易总金额, totalRow: 总数, recordsFiltered: 过滤后总数, data: [{id: 退货id, customerName: 客户名称, customerPhone: 客户手机号, refundType: 退款类型, productName: 产品名称, refundCash: 退款金额, tradeCash: 交易金额, status: 状态}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	Integer status = getParaToIntegerDefault("status");
    	String startTime = getPara("startTime");
    	String endTime = getPara("endTime");
    	String orderCode = getPara("orderCode");
    	String tradeCode = getPara("tradeCode");
    	String tradeNo = getPara("tradeNo");
    	String productName = getPara("productName");
    	String shopName = getPara("name");
    	String operator = getPara("operator");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> list = com.eshop.service.User.findRefundItems(offset, length, null, null, status, startTime, endTime, productName, shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
    	int total = com.eshop.service.User.countRefundItems(null, null, status, startTime, endTime, productName, shopName, orderCode, tradeCode, tradeNo, operator);
    	
    	List<Record> data = com.eshop.service.User.findRefundItems(null, null, status, startTime, endTime, productName, shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
    	Record statistics = com.eshop.service.User.calculateReturnedItems(data);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	jsonObject.put("totalRefundCash", MathHelper.cutDecimal(statistics.getDouble("totalRefundCash")));
    	jsonObject.put("totalTradeCash", MathHelper.cutDecimal(statistics.getDouble("totalTradeCash")));
    	renderJson(jsonObject);
    }
    
    /**
     * 根据查询结果导出退款单
     * @param status 状态(0待审核；1退款中；2审核不通过) 必填
     * @param startTime
     * @param endTime
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0,errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportRefund() {
    	Integer status = getParaToIntegerDefault("status");
    	String startTime = getParaToDateTimeDefault("startTime");
    	String endTime = getParaToDateTimeDefault("endTime");
    	String orderCode = getPara("orderCode");
    	String tradeCode = getPara("tradeCode");
    	String tradeNo = getPara("tradeNo");
    	String productName = getPara("productName");
    	String shopName = getPara("name");
    	String operator = getPara("operator");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
		Map<String, String> headers = ExcelService.exportRefund();
		List<Record> list = com.eshop.service.User.findRefundItems(null, null, status, startTime, endTime, productName, shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
		Record statistics = com.eshop.service.User.calculateReturnedItems(list);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "refund_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportRefund(fileName, headers, list, status, statistics);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
    
    /**
     * 获取退款
     * @param token 帐户访问口令（必填）
     * @param id 主键
     * @return 成功：{error: 0, data: {id: 退款id, customerName: 客户名称, customerPhone: 客户手机号, refundType: 退款类型, productName: 产品名称, refundCash: 退款金额, tradeCash: 交易金额, status: 状态}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	Refund refund = com.eshop.service.User.getRefund(id);
    	ProductOrder productOrder = com.eshop.service.User.getProductOrder(refund.getProductOrderId());
    	Order order = com.eshop.service.User.getOrder(productOrder.getOrderId());
    	List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
    	productOrders.add(productOrder);
    	
    	// 获取图片
    	Product product = Merchant.getProduct(productOrder.getProductId());
    	String mainPic = ResourceService.getPath(product.getMainPic());
    	productOrder.put("mainPic", mainPic);
    	double allowDeliveryPrice = com.eshop.service.User.getAllowDeliveryPriceWithRefund(id);
    	
    	refund.put("productOrder", productOrders);
    	refund.put("order", order);
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
     * @param applyCash 申请退款金额
     * @param deliveryPrice 邮费
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void audit() {
    	String[] params = {"id", "status", "applyCash", "deliveryPrice"};
		
		if (!validate(params)) {
			return;
		}
    	
		int id = getParaToInt("id");//id
		int status = getParaToInt("status");//状态
    	BigDecimal applyCash = getParaToDecimal("applyCash");//
    	BigDecimal deliveryPrice = getParaToDecimal("deliveryPrice");//退款金额
    	String refuseNote = getPara("refuseNote");//拒绝通知
    	
    	String token = getPara("adminToken");
    	User user = (User) CacheHelper.get(token);
    	String operator = (user != null) ? user.getNickName() : "";
    	
    	ServiceCode code = merchant.auditRefund(id, status, refuseNote, operator, applyCash, deliveryPrice);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "审核失败");
    	}
    	
    	renderJson(jsonObject);
	    }
    /**
     * 审核退款，改变状态
     */
    public void check() {
    	String[] params = {"id", "status", "deliveryPrice"};
		if (!validate(params)) {
			return;
		}
		
    	Integer id = getParaToInt("id");
    	Integer status = getParaToInt("status");
    	String token = getPara("adminToken");
    	String refuseNote = getPara("refuseNote");
    	User user = (User) CacheHelper.get(token);
    	String operator = (user != null) ? user.getNickName() : "";
    	BigDecimal deliveryPrice = getParaToDecimal("deliveryPrice");
		Refund refund = Refund.dao.findById(id);
		BigDecimal refundCash = refund.getApplyCash().add(deliveryPrice);
		refund.setStatus(status);
		refund.setUpdatedAt(new Date());
		refund.setRefundTime(new Date());
		refund.setRefuseNote(refuseNote);
		refund.setOperator(operator);
		refund.setRefundCash(refundCash);
		refund.setDeliveryPrice(deliveryPrice);
		
		ServiceCode code = merchant.refundUpdate(refund);
		if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "审核失败");
    	}
		
		renderJson(jsonObject);
    }
    
}