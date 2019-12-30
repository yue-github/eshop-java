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
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Back;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.model.User;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 退货管理-控制器
 * @author TangYiFeng
 */
public class BackController extends AdminBaseController {
	
	Merchant merchant;
	
    /**
     * 构造方法
     */
    public BackController() {
    	merchant = new Merchant();
    }
    
    /**
     * 获取所有退货单（按退货时间排序）
     * @param token 帐户访问口令（必填）
     * @param offset 页码
     * @param length 每页显示条数
     * @param startTime 申请开始时间
     * @param endTime 申请结束时间
     * @param status 状态(0未审核,2审核不通过,1审核通过,4退款成功)
     * @param order_code 订单号 选填
     * @param tradeCode 商户单号
     * @param tradeNo 流水号
     * @return 成功：{error:0, offset:页码, totalRow:总数, data:[{id: 退货id, customerName: 客户名称, customerPhone: 客户手机号, backType: 退货类型, productName: 产品名称, refundCash: 退款金额, tradeCash: 交易金额, status: 状态}, ...]}；失败：{error: >0, errmsg: 错误信息}
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
    	String orderCode = getPara("orderCode");
    	String tradeCode = getPara("tradeCode");
    	String tradeNo = getPara("tradeNo");
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		String productName = getPara("productName");
		String shopName = getPara("name");
		String operator = getPara("operator");
		
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("a.created_at", "desc");
		
		List<Record> data = com.eshop.service.User.findReturnedItems(offset, length, null, null, status, startTime, endTime, productName, shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
		List<Record> list = com.eshop.service.User.findReturnedItems(null, null, status, startTime, endTime, productName, shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
    	Record statistics = com.eshop.service.User.calculateReturnedItems(list);
    	int total = list.size();
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("totalRefundCash", cutDecimal(statistics.getDouble("totalRefundCash")));
    	jsonObject.put("totalTradeCash", cutDecimal(statistics.getDouble("totalTradeCash")));
    	renderJson(jsonObject);
    }
    
    /**
     * 根据查询结果导出退货单
     * @param startTime 申请开始时间
     * @param endTime 申请结束时间
     * @param status 状态(0未审核,2审核不通过,1审核通过,4退款成功)
     * @param order_code 订单号 选填
     * @param tradeCode 商户单号
     * @param tradeNo 流水号
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0,errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportBack() {
		String[] params = {"status"};
		if (!this.validate(params)) {
			return;
		}
		
		Integer status = getParaToIntegerDefault("status");
    	String orderCode = getPara("orderCode");
    	String tradeCode = getPara("tradeCode");
    	String tradeNo = getPara("tradeNo");
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		String productName = getPara("productName");
		String shopName = getPara("name");
		String operator = getPara("operator");
		
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("a.created_at", "desc");
		
		Map<String, String> headers = ExcelService.exportBack();
		List<Record> list = com.eshop.service.User.findReturnedItems(null, null, status, startTime, endTime, productName, shopName, orderCode, tradeCode, tradeNo, operator, orderByMap);
		Record statistics = com.eshop.service.User.calculateReturnedItems(list);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "back_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportBack(fileName, headers, list, status, statistics);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
    
    /**
     * 获取一条退货
     * @param token 帐户访问口令（必填）
     * @param id 主键
     * @return 成功：{error: 0, data: {id: 退货id, customerName: 客户名称, customerPhone: 客户手机号, backType: 退货类型, productName: 产品名称, refundCash: 退款金额, tradeCash: 交易金额, status: 状态}}；失败：{error: >0, errmsg: 错误信息}
     */
	@Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	Back back = com.eshop.service.User.getReturned(id);
    	ProductOrder productOrder = com.eshop.service.User.getProductOrder(back.getProductOrderId());
    	Order order = com.eshop.service.User.getOrder(productOrder.getOrderId());
    	
    	// 获取图片
    	Product product = Merchant.getProduct(productOrder.getProductId());
    	String mainPic = ResourceService.getPath(product.getMainPic());
    	productOrder.put("mainPic", mainPic);
    	
    	List<ProductOrder> productOrders = new ArrayList<ProductOrder>();
    	productOrders.add(productOrder);
    	double allowDeliveryPrice = com.eshop.service.User.getAllowDeliveryPriceWithBack(id);
    	
    	back.put("productOrder", productOrders);
    	back.put("order", order);
    	back.put("allowedDeliveryPrice", allowDeliveryPrice);
    	
    	jsonObject.put("data", back);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改退货
     * @param token 帐户访问口令（必填）
     * @param id
     * @param refundCash 退款金额（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
	@Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "refundCash"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	BigDecimal refundCash = getParaToDecimal("refundCash");
    	
    	Back model = Back.dao.findById(id);
    	model.setRefundCash(refundCash);
    	
    	ServiceCode code = com.eshop.service.User.updateRefundCash(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 审核退货
     * @param token 帐户访问口令（必填）
     * @param id 退货id（必填）
     * @param status 状态（1审核通过，2审核不通过）（必填）
     * @param applyCash
     * @param deliveryPrice
     * @param refuseNote 审核不通过原因
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
	@Before(AdminAuthInterceptor.class)
    public void audit() {
		String[] params = {"id", "status", "applyCash", "deliveryPrice"};
		
		if (!validate(params)) {
			return;
		}
    	
		int id = getParaToInt("id");
		int status = getParaToInt("status");
    	BigDecimal applyCash = getParaToDecimal("applyCash");
    	BigDecimal deliveryPrice = getParaToDecimal("deliveryPrice");
    	String refuseNote = getPara("refuseNote");
    	
    	String token = getPara("adminToken");
    	User user = (User) CacheHelper.get(token);
    	String operator = (user != null) ? user.getNickName() : "";
    	
    	ServiceCode code = merchant.auditReturned(id, status, refuseNote, operator, applyCash, deliveryPrice);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "审核失败");
    	}
    	
    	renderJson(jsonObject);
    }
	
	/**
     * 批量删除退货
     * @param token 帐户访问口令（必填）
     * @param ids 分类id数组:[1, 2, ...]（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
	@Before(AdminAuthInterceptor.class)
    public void batchDelete() {
    	renderJson(jsonObject);
    }
    
    /**
     * 确认收货
     * @param token 帐户访问口令（必填）
     * @param ids [id, id, ...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
	@Before(AdminAuthInterceptor.class)
    public void confirmReceive() {
    	renderJson(jsonObject);
    }
    
    /**
     * 确认退款
     * @param token 帐户访问口令（必填）
     * @param ids [id, id, ...]
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void confirmRefund() {
    	renderJson(jsonObject);
    }
    
    /**
     * 审核退款，改变状态
     */
    public void check() {
    	String[] params = {"id", "status"};
		
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
		Back back = Back.dao.findById(id);
		BigDecimal refundCash = back.getApplyCash().add(deliveryPrice);
		back.setStatus(status);
		back.setUpdatedAt(new Date());
		back.setRemitTime(new Date());
		back.setOperator(operator);
		back.setRefuseNote(refuseNote);
		back.setRefundCash(refundCash);
		back.setDeliveryPrice(deliveryPrice);
		ServiceCode code = merchant.backdUpdate(back);
		if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "审核失败");
    	}
		renderJson(jsonObject);
    }
    
}