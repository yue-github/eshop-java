package com.eshop.controller.admin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eshop.finance.AutoBalanceService;
import com.eshop.finance.CaiwuSaleFinaceService;
import com.eshop.finance.ExcelService;
import com.eshop.finance.SaleFinanceService;
import com.eshop.helper.DateHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.helper.MathHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Order;
import com.eshop.model.ProductOrder;
import com.eshop.model.Supplier;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Member;
import com.eshop.service.Merchant;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 订单管理
 * @author TangYiFeng
 */
public class OrderController extends AdminBaseController {

	/**
	 * 构造方法
	 */
	public OrderController() {
	}

	/**
	 * 订单管理
	 * @param token 帐户访问口令（必填）
	 * @param offset
	 * @param length
	 * @param status 订单状态（-1为全部,1待付款,2待发货,3已发货,4已收货,5取消订单） 必填
	 * @param order_code 订单号 选填
	 * @param startTime 开始时间 选填
	 * @param endTime 结束时间 选填
	 * @param tradeCode 商户单号 选填
	 * @param tradeNo 交易流水号 选填
	 * @param payType 支付方式
	 * @param source 平台
	 * @param preferredContactPhone 收货人姓名
	 * @param receiveName 收获人姓名
	 * @param expressCode 物流号
	 * @param logisticsName 快递公司名称
	 * @return 成功：{error: 0,totalCost:订单总成本,totalPayable:订单总金额, offset: 页码,
	 *         totalRow: 总数, recordsFiltered: 过滤后总数, data: [{id: 订单id, code:
	 *         订单号, created_at: 下单时间, totalPayable: 订单总额, customerPhone: 客户手机号,
	 *         source: 来源, status: 订单状态}； 失败：{error: >0, errmsg: 错误信息}
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
		String orderCode = getPara("code");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");
		Integer payType = getParaToIntegerDefault("payType");
		Integer source = getParaToIntegerDefault("source");
		String preferredContactPhone = getPara("preferredContactPhone");
		String receiveName = getPara("receiveName");
		String expressCode = getPara("expressCode");
		String logisticsName = getPara("logisticsName");
		
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("o.created_at", "desc");
		
		List<Record> data = User.findOrderItems(offset, length, null, null, orderCode, tradeCode, 
				tradeNo, status, startTime, endTime, receiveName, preferredContactPhone, source, 
				payType, logisticsName, expressCode, null, orderByMap);
		List<Record> list = User.findOrderItems(null, null, orderCode, tradeCode, tradeNo, status, 
				startTime, endTime, receiveName, preferredContactPhone, source, payType, logisticsName, 
				expressCode, null, orderByMap);
		Record statistics = User.calculateOrderItems(list);
		int total = list.size();
		
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("data", data);
		jsonObject.put("totalCost", MathHelper.cutDecimal(statistics.getDouble("totalProductCost")));
		jsonObject.put("totalPayable", MathHelper.cutDecimal(statistics.getDouble("totalPayable")));
		renderJson(jsonObject);
	}

	/**
	 * 用id获取订单明细
	 * @param token 帐户访问口令（必填）
	 * @param id 订单id
	 * @return 成功：{error: 0, order: {id: 订单id, order_code: 订单号, created_at:
	 *         下单时间, customerPhone: 客户手机号, source: 来源, couponType: 优惠类型,
	 *         couponTitle: 优惠劵或活动劵名称, deliveryCompany: 快递公司, trackingNumber:
	 *         快递单号, contacts: 姓名, phone: 手机号,province: 省, city: 市, district: 区,
	 *         detailedAddress: 详细地址,salerCode 销售员电话号, payType 支付方式, expressType
	 *         配送方式, created_at: 下单时间, totalPayable: 订单总额, status: 订单状态,
	 *         details: [{name: 产品名称, actualUnitPrice: 价格,
	 *         totalActualProductPrice:小计, summary: 摘要说明, mainPic: 图片路径, amount:
	 *         数量, status: 明细状态}}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		Order order = User.getOrder(id);
		List<Record> productOrders = User.findProductOrderItems(order.getId(), null, null);
		order.put("details", productOrders);
		
		jsonObject.put("order", order);
		renderJson(jsonObject);
	}

	/**
	 * 取消订单（只有未支付的订单才能取消）
	 * @param token 帐户访问口令（必填）
	 * @param ids 订单id数组:[1, 2, ...]（必填）
	 * @param cancel_reason 取消订单原因(必填)
	 * @param cancel_note 取消订单备注
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void batchCancel() {
		String[] params = {"ids", "cancel_reason"};
		if (!validate(params)) {
			return;
		}
		
		String idsStr = getPara("ids");
		String cancelReason = getPara("cancel_reason");
		String cancelNote = getPara("cancel_note");
		List<String> ids = JSON.parseArray(idsStr, String.class);
		
		ServiceCode code = Member.batchCancel(ids, cancelReason, cancelNote);

		if (ServiceCode.Success != code) {
			setError(ErrorCode.Exception, "失败");
		}
		
		renderJson(jsonObject);
	}

	/**
	 * 删除订单（只有已取消的订单才能删除）
	 * @param token 帐户访问口令（必填）
	 * @param ids 订单id数组:[1, 2, ...]（必填）
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void batchDelete() {
		String[] params = {"ids"};
		
		if (!validate(params)) {
			return;
		}
		
		String idsStr = getPara("ids");
		List<String> ids = JSON.parseArray(idsStr, String.class);
		
		ServiceCode code = Member.batchDeleteOrder(ids);

		if (ServiceCode.Success != code) {
			setError(ErrorCode.Exception, "失败");
		}
		
		renderJson(jsonObject);
	}

	/**
	 * 订单发货
	 * @param token 帐户访问口令（必填）
	 * @param id 订单id（必填）
	 * @param deliveryCompany 快递公司（必填）
	 * @param deliveryCompanyNum 快递公司编号（必填）
	 * @param trackingNumber 快递单号（必填）
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void sendOut() {
		String[] params = {"id", "deliveryCompany", "deliveryCompanyNum", "trackingNumber"};
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		String deliveryCompany = getPara("deliveryCompany");
		String deliveryCompanyNum = getPara("deliveryCompanyNum");
		String trackingNumber = getPara("trackingNumber");
		
		Merchant merchant = new Merchant();
		ServiceCode code = merchant.delivery(id, deliveryCompany, deliveryCompanyNum, trackingNumber);

		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "发货失败");
		}

		renderJson(jsonObject);
	}

	/**
	 * 销售汇总列表
	 * @param offset 页码
	 * @param length 每页显示条数
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param payType 支付方式 选填
	 * @param status 状态(0销售汇总,2平台类型汇总,1支付方式汇总) 选填
	 * @param source 平台类型 选填
	 * @return 成功：{error:0,totalBalance:总划账金额,totalPayRateSum:总手续费,totalPayable:总销售额,totalRefundCash:总退款金额,totalTaxCost:总税额,offset:页码,totalRow:总数,recordsFiltered:过滤后总数,
	 *         data:[{createdAt:日期,source:交易平台类型(1pc、2微信端、3android、4苹果),payType:支付方式,sumTotalPayable:销售总额,totalRefundCash:退款总额,totalTaxCost:税额,totalPayRateSum:手续费,balance:账号余额},...]}
	 *         失败：{error:>0,errmsg:错误}
	 */
	@Before(AdminAuthInterceptor.class)
	public void orderSummaryList() {
		String[] params = {"offset", "length", "status"};
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer summaryType = getParaToIntegerDefault("status");
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		
		if (getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = getPara("startTime");
		}
		if (getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = getPara("endTime");
		}
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		List<Record> data = SaleFinanceService.findSaleSummaryItems(offset, length, payType, source, startTime, endTime, summaryType, whereInPayType);
		List<Record> list = SaleFinanceService.findSaleSummaryItems(payType, source, startTime, endTime, summaryType, whereInPayType);
		Record statistics = SaleFinanceService.calculateSaleSummaryItems(list);
		int total = list.size();

		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("startTime", startTime);
		jsonObject.put("endTime", endTime);
		jsonObject.put("data", data);
		jsonObject.put("totalPayable", statistics.getDouble("totalPayable"));
		jsonObject.put("totalPayRateSum", statistics.getDouble("totalPayRateSum"));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalTaxCost", statistics.getDouble("totalTaxCost"));
		jsonObject.put("totalBalance", statistics.getDouble("totalBalance"));
		renderJson(jsonObject);
	}
	
	/**
	 * 财务管理-销售汇总
	 * @param offset
	 * @param length 长度
	 */
	public void orderSummarys() {
		String[] params = {"offset", "length"};
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer supplierId = getParaToInt("supplierId");
		String supplierName = getPara("supplierName");
		Integer status = getParaToInt("status");
		String startTime = DateHelper.firstDay();
		String endTime  = DateHelper.lastDay();
		
		if(getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = getPara("startTime");
    	}
		if(getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = getPara("endTime");
    	}
    	
		List<Record> data = SaleFinanceService.findOrderSummaryItems(offset, length, supplierId, 
				supplierName, status, startTime, endTime);
		List<Record> list = SaleFinanceService.findOrderSummaryItems(supplierId, supplierName, 
				status, startTime, endTime);
		int totalRow = list.size();
		Record statistics = SaleFinanceService.calculateOrderSummaryItems(list);
		
		int startStatus = startTime.compareTo("2017-07-25");
		int endStatus = endTime.compareTo("2017-07-25");
		boolean dateStatus = (startStatus <= 0) && (endStatus >= 0);
		
		double totalPayable = statistics.getDouble("totalPayable");
		//double totalCost = statistics.getDouble("totalCost");
		if ((status == 2 || status == 3) && dateStatus) {
//			totalPayable -= 48.31;
			//totalCost -= 27.83;
		}
		
		//将数据封装
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", totalRow);
		jsonObject.put("data", data);
		jsonObject.put("list", list);
		jsonObject.put("startTime", startTime);
		jsonObject.put("endTime", endTime);
		jsonObject.put("totalPayable", MathHelper.cutDecimal(totalPayable));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalCouponDiscount", statistics.getDouble("totalCouponDiscount"));
		jsonObject.put("totalWeixinpay", statistics.getDouble("totalWeixinpay"));
		jsonObject.put("totalAlipay", statistics.getDouble("totalAlipay"));
		jsonObject.put("totalUnionpay", statistics.getDouble("totalUnionpay"));
		jsonObject.put("totalWalletpay", statistics.getDouble("totalWalletpay"));
		jsonObject.put("totalCardpay", statistics.getDouble("totalCardpay"));
		jsonObject.put("totalPointpay", statistics.getDouble("totalPointpay"));
		jsonObject.put("totalUnitOrdered", statistics.getInt("totalUnitOrdered"));
		jsonObject.put("totalRefundAmount", statistics.getInt("totalRefundAmount"));
		renderJson(jsonObject);
	}
	
	/**
	 * 财务管理-销售汇总
	 * @param pageIndex 当前页
	 * @param length 长度
	 */
	public void allOrderSummarys() {
		Integer supplierId = getParaToInt("supplierId");
		String supplierName = getPara("supplierName");
		Integer status = getParaToInt("status");
		String startDate = getPara("startDate");
		String endDate  = getPara("endDate");
		
		if(startDate == null || "".equals(startDate)) {
			startDate = DateHelper.firstDay();
    	}
    	if(endDate == null || "".equals(endDate)) {
    		endDate = DateHelper.lastDay();
    	}
		//返回
		renderJson(jsonObject);
		List<ProductOrder> list = SaleFinanceService.getAllOrderSummaryList(supplierId, supplierName, status, startDate, endDate);
		Record total = SaleFinanceService.totalAllOrderSummaryList(list);
		//将数据封装
		jsonObject.put("data", list);
		jsonObject.put("totalActualProductPrice", total.getDouble("totalActualProductPrice"));
		jsonObject.put("totalZhifubaoPrice", total.getDouble("totalZhifubaoPrice"));
		jsonObject.put("totalRefundAmount", total.getInt("totalRefundAmount"));
		jsonObject.put("totalCouponDiscount", total.getDouble("totalCouponDiscount"));
		jsonObject.put("totalUnitOrdered", total.getInt("totalUnitOrdered"));
		jsonObject.put("totalRefundCash", total.getDouble("totalRefundCash"));
		jsonObject.put("totalWeixinPrice", total.getDouble("totalWeixinPrice"));
		jsonObject.put("totalYinlianPrice", total.getDouble("totalYinlianPrice"));
	
		renderJson(jsonObject);
	}
	
	
	/**
	 * 获取所有销售汇总列表
	 * @param offset 页码
	 * @param length 每页显示条数
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param payType 支付方式 选填
	 * @param status 状态(0销售汇总,2平台类型汇总,1支付方式汇总) 选填
	 * @param source 平台类型 选填
	 * @return 成功：{error:0,totalBalance:总划账金额,totalPayRateSum:总手续费,totalPayable:总销售额,totalRefundCash:总退款金额,totalTaxCost:总税额,offset:页码,totalRow:总数,recordsFiltered:过滤后总数,
	 *         data:[{createdAt:日期,source:交易平台类型(1pc、2微信端、3android、4苹果),payType:支付方式,sumTotalPayable:销售总额,totalRefundCash:退款总额,totalTaxCost:税额,totalPayRateSum:手续费,balance:账号余额},...]}
	 *         失败：{error:>0,errmsg:错误}
	 */
	@Before(AdminAuthInterceptor.class)
	public void allOrderSummaryList() {
		Integer summaryType = getParaToIntegerDefault("status");
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		
		if (getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = DateHelper.formatDateTime(getParaToDate("startTime"));
		}

		if (getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = DateHelper.formatDateTime(getParaToDate("endTime"));
		}
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}

		List<Record> list = SaleFinanceService.findSaleSummaryItems(payType, source, startTime, endTime, summaryType, whereInPayType);
		Record statistics = SaleFinanceService.calculateSaleSummaryItems(list);

		jsonObject.put("data", list);
		jsonObject.put("totalPayable", statistics.getDouble("totalPayable"));
		jsonObject.put("totalPayRateSum", statistics.getDouble("totalPayRateSum"));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalTaxCost", statistics.getDouble("totalTaxCost"));
		jsonObject.put("totalBalance", statistics.getDouble("totalBalance"));
		renderJson(jsonObject);
	}

	/******************************************分割线***************************************************************
	 * 根据支付方式导出订单
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param order_code 订单号 选填
	 * @param payType 支付方式 选填
	 * @param source 平台类型 选填
	 * @return 成功：{error:0,path:excel文件绝对路径} 失败：{error:>0,errmsg:错误信息}
	 */
	//@Before(AdminAuthInterceptor.class)
	public void exportByOrderSource() {
		Integer summaryType = getParaToIntegerDefault("summaryType");
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();

		if (getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = getPara("startTime");
		}
		if (getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = getPara("endTime");
		}
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}

		Map<String, String> headers = ExcelService.exportByOrderSource();
		List<Record> list = SaleFinanceService.findSaleSummaryItems(payType, source, startTime, endTime, summaryType, whereInPayType);
		Record statistics = SaleFinanceService.calculateSaleSummaryItems(list);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "xiaoshouhuizhong_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportByOrderSource(fileName, headers, list, statistics);
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}

	/**
	 * 销售明细 -销售管理（包括余额支付）
	 * @param offset
	 * @param length
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param order_code 订单号 选填
	 * @param payType 支付方式 选填
	 * @param source 平台类型 选填
	 * @param status 订单状态(1待发货,2待收货,3已收货)
	 * @return 成功：{error:0,totalPayable:总销售额,totalPayRateSum:总手续费,totalRefundCash:总退款金额,totalTaxCost:总税额,totalProductCost:总产品成本,totalActualProductPrice:总产品价格,offset:页码,totalRow:总数,recordsFiltered:过滤后总数,
	 *         data:[{order_code:订单号,created_at:下单时间,source:交易平台类型(1pc、2微信端、3android、4苹果),payType:支付方式,tradeNo:流水号,sumTotalPayable:销售总额,totalRefundCash:退款总额,totalTaxCost:税额,totalPayRateSum:手续费,balance:账号余额,totalCost:订单总成本},...]}
	 *         失败：{error:>0,errmsg:错误}
	 */
	@Before(AdminAuthInterceptor.class)
	public void orderList() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String orderCode = getPara("order_code");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");
		String shopName = getPara("shopName");
		String supplierName = getPara("supplierName");
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		Integer supplierId = getParaToIntegerDefault("supplierId");
		Integer status = getParaToIntegerDefault("status");
		status = (status != null) ? status : 1;
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		
		if (getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = getPara("startTime");
		}
		if (getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = getPara("endTime");
		}
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		List<Record> data = SaleFinanceService.findSaleDetailItems(offset, length, payType, source, status, orderCode, tradeCode, tradeNo, shopName, supplierName, startTime, endTime, supplierId, whereInPayType);
		List<Record> list = SaleFinanceService.findSaleDetailItems(payType, source, status, orderCode, tradeCode, tradeNo, shopName, supplierName, startTime, endTime, supplierId, whereInPayType);
		Record statistics = SaleFinanceService.calculateSaleDetailItems(list);
		int total = list.size();
		
		int startStatus = startTime.compareTo("2017-07-25");
		int endStatus = endTime.compareTo("2017-07-25");
		boolean dateStatus = (startStatus <= 0) && (endStatus >= 0);
		
		double totalPayable = statistics.getDouble("totalPayable");
		double totalCost = statistics.getDouble("totalCost");
		if ((status == 2 || status == 3) && dateStatus) {
//			totalPayable -= 48.31;
//			totalCost -= 27.83;
		}
		
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("data", data);
		jsonObject.put("list", list);
		jsonObject.put("totalPayable", MathHelper.cutDecimal(totalPayable));
		jsonObject.put("totalPayRateSum", statistics.getDouble("totalPayRateSum"));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalTaxCost", statistics.getDouble("totalTaxCost"));
		jsonObject.put("totalProductCost", statistics.getDouble("totalProductCost"));
		jsonObject.put("totalActualProductPrice", statistics.getDouble("totalActualProductPrice"));
		jsonObject.put("totalCost", MathHelper.cutDecimal(totalCost));
		jsonObject.put("startTime", startTime);
		jsonObject.put("endTime", endTime);
		renderJson(jsonObject);
	}
	
	/**
	 * 导出销售明细（销售管理）
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param order_code 订单号 选填
	 * @param payType 支付方式 选填
	 * @param source 平台类型 选填
	 * @param status 订单状态(1待发货,2待收货货,3已收货)
	 * @return 成功：{error:0,path:excel文件绝对路径} 失败：{error:>0,errmsg:错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void exportByOrderList() {
		String orderCode = getPara("order_code");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");
		String shopName = getPara("shopName");
		String supplierName = getPara("supplierName");
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		Integer supplierId = getParaToIntegerDefault("supplierId");
		Integer status = getParaToIntegerDefault("status");
		status = (status != null) ? status : 1;
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		
		if (getPara("startTime") != null) {
			startTime = getPara("startTime");
		}
		if (getPara("endTime") != null) {
			endTime = getPara("endTime");
		}
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		String strPayType = getPayTypes(getPara("payType"));
		String strStatus = getOrderStatus(status);
		String strTime = startTime + "至" + endTime;
		String strSupplierName = supplierName;
		
		status = (status != 0) ? status : null;
		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("strStatus", strStatus);
		searchMap.put("strPayType", strPayType);
		searchMap.put("strTime", strTime);
		searchMap.put("strSupplierName", strSupplierName);
		
		List<Record> list = SaleFinanceService.findSaleDetailItems(payType, source, status, orderCode, tradeCode, tradeNo, shopName, supplierName, startTime, endTime, supplierId, whereInPayType);
		Record statistics = SaleFinanceService.calculateSaleDetailItems(list);
		
		int startStatus = startTime.compareTo("2017-07-25");
		int endStatus = endTime.compareTo("2017-07-25");
		boolean dateStatus = (startStatus <= 0) && (endStatus >= 0);
		double totalPayable = statistics.getDouble("totalPayable");
		double totalCost = statistics.getDouble("totalCost");
		if ((status == 2 || status == 3) && dateStatus) {
//			totalPayable -= 48.31;
//			totalCost -= 27.83;
		}
		statistics.set("totalPayable", totalPayable);
		statistics.set("totalCost", totalCost);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "orderList_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportByOrderList(fileName, list, statistics, searchMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}

	/**
	 * 销售明细 -财务管理(不包括余额支付订单)
	 * @param offset 页码
	 * @param length 每页显示条数
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param order_code 订单号 选填
	 * @param tradeCode 商户订单号 选填
	 * @param tradeNo 交易流水号 选填
	 * @param payType 支付方式 选填
	 * @param source 平台类型 选填
	 * @param status 订单状态(1待发货,2待收货,3已收货)
	 * @return 成功：{error:0,totalPayable:总销售额,totalPayRateSum:总手续费,totalRefundCash:总退款金额,totalTaxCost:总税额,totalProductCost:总产品成本,totalActualProductPrice:总产品价格,offset:页码,totalRow:总数,recordsFiltered:过滤后总数,
	 *         data:[{order_code:订单号,created_at:下单时间,source:交易平台类型(1pc、2微信端、3android、4苹果),payType:支付方式,tradeNo:流水号,sumTotalPayable:销售总额,totalRefundCash:退款总额,totalTaxCost:税额,totalPayRateSum:手续费,balance:账号余额,totalCost:订单总成本},...]}
	 *         失败：{error:>0,errmsg:错误}
	 */
	@Before(AdminAuthInterceptor.class)
	public void orderList2() {
		String[] params = {"offset", "length"};
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		Integer status = getParaToIntegerDefault("status");
		status = (status != null) ? status : 0;
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		String orderCode = getPara("order_code");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");
		String shopName = getPara("shopName");
		String supplierName = getPara("supplierName");
		Integer orderType = getParaToInt("orderType");
		Integer supplierId = getParaToInt("supplier_id");
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		if (getPara("startTime") != null) {
			startTime = getPara("startTime");
		}
		if (getPara("endTime") != null) {
			endTime = getPara("endTime");
		}
		
		List<Record> data = CaiwuSaleFinaceService.findSaleDetailItems(offset, length, payType, source, status, orderCode, tradeCode, tradeNo, shopName, supplierName, startTime, endTime, orderType, supplierId, whereInPayType);
		List<Record> list = CaiwuSaleFinaceService.findSaleDetailItems(payType, source, status, orderCode, tradeCode, tradeNo, shopName, supplierName, startTime, endTime, orderType, supplierId, whereInPayType);
		Record statistics = CaiwuSaleFinaceService.calculateSaleDetailItems(list);
		int total = list.size();
		
		int startStatus = startTime.compareTo("2017-07-25");
		int endStatus = endTime.compareTo("2017-07-25");
		boolean dateStatus = (startStatus <= 0) && (endStatus >= 0);
		
		double totalPayable = statistics.getDouble("totalPayable");
		double totalCost = statistics.getDouble("totalCost");
		if ((status == 2 || status == 3) && dateStatus) {
//			totalPayable -= 48.31;
//			totalCost -= 27.83;
		}

		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("data", data);
		jsonObject.put("list", list);
		jsonObject.put("totalPayable", MathHelper.cutDecimal(totalPayable));
		jsonObject.put("totalPayRateSum", statistics.getDouble("totalPayRateSum"));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalTaxCost", statistics.getDouble("totalTaxCost"));
		jsonObject.put("totalProductCost", statistics.getDouble("totalProductCost"));
		jsonObject.put("totalActualProductPrice", statistics.getDouble("totalActualProductPrice"));
		jsonObject.put("totalCost", MathHelper.cutDecimal(totalCost));
		jsonObject.put("startTime", startTime);
		jsonObject.put("endTime", endTime);
		renderJson(jsonObject);
	}
	
	/**
	 * 获取所有销售明细 -不包括余额支付订单
	 */
	@Before(AdminAuthInterceptor.class)
	public void allOrderList2() {
		Integer status = getParaToIntegerDefault("status");
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		String orderCode = getPara("order_code");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");
		String shopName = getPara("shopName");
		String supplierName = getPara("supplierName");
		Integer orderType = getParaToInt("orderType");
		Integer supplierId = getParaToInt("supplier_id");
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		List<Record> list = CaiwuSaleFinaceService.findSaleDetailItems(payType, source, status, orderCode, tradeCode, tradeNo, shopName, supplierName, startTime, endTime, orderType, supplierId, whereInPayType);
		Record statistics = CaiwuSaleFinaceService.calculateSaleDetailItems(list);

		jsonObject.put("data", list);
		jsonObject.put("totalPayable", statistics.getDouble("totalPayable"));
		jsonObject.put("totalPayRateSum", statistics.getDouble("totalPayRateSum"));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalTaxCost", statistics.getDouble("totalTaxCost"));
		jsonObject.put("totalProductCost", statistics.getDouble("totalProductCost"));
		jsonObject.put("totalActualProductPrice", statistics.getDouble("totalActualProductPrice"));
		renderJson(jsonObject);
	}
	
	private String getOrderStatus(int status) {
		String strStatus = "";
		switch (status) {
		case 1:
			strStatus = "已付";
			break;
		case 2:
			strStatus = "发货";
			break;
		case 3:
			strStatus = "收货";
			break;
		}
		return strStatus;
	}
	
	private String getPayType(int payType) {
		String strPayType = "";
		switch (payType) {
		case 1:
			strPayType = "微信支付";
			break;
		case 2:
			strPayType = "支付宝支付";
			break;
		case 3:
			strPayType = "银联支付";
			break;
		case 5:
			strPayType = "团购卡支付";
			break;
		case 6:
			strPayType = "积分兑换支付";
			break;	
		}
		return strPayType;
	}
	
	private String getPayTypes(String payTypes) {
		String str = "";
		if (payTypes == null || payTypes.equals("")) {
			return str;
		}
		JSONArray array = JSON.parseArray(payTypes);
		for (int i = 0; i < array.size(); i++) {
			int payType = array.getIntValue(i);
			if (i == array.size() - 1) {
				str += getPayType(payType);
				break;
			} else {
				str += getPayType(payType) + ",";
			}
		}
		return str;
	}

	/**
	 * 导出销售明细（财务）
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param order_code 订单号 选填
	 * @param payType 支付方式 选填
	 * @param source 平台类型 选填
	 * @param status 订单状态(1待发货,2待收货货,3已收货)
	 * @return 成功：{error:0,path:excel文件绝对路径} 失败：{error:>0,errmsg:错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void exportByOrderList2() {
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		Integer status = getParaToIntegerDefault("status");
		status = (status != null) ? status : 0;
		Integer payType = null;
		Integer source = getParaToIntegerDefault("source");
		String orderCode = getPara("order_code");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");
		String shopName = getPara("shopName");
		String supplierName = getPara("supplierName");
		Integer orderType = getParaToInt("orderType");
		Integer supplierId = getParaToInt("supplier_id");
		
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		if (getPara("startTime") != null) {
			startTime = getPara("startTime");
		}
		if (getPara("endTime") != null) {
			endTime = getPara("endTime");
		}
		
		String strPayType = getPayTypes(getPara("payType"));
		String strStatus = getOrderStatus(status);
		String strTime = startTime + "至" + endTime;
		String strSupplierName = supplierName;
		
		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("strStatus", strStatus);
		searchMap.put("strPayType", strPayType);
		searchMap.put("strTime", strTime);
		searchMap.put("strSupplierName", strSupplierName);
		
		List<Record> list = CaiwuSaleFinaceService.findSaleDetailItems(payType, source, status, orderCode, 
				tradeCode, tradeNo, shopName, supplierName, startTime, endTime, orderType, supplierId, whereInPayType);
		Record statistics = CaiwuSaleFinaceService.calculateSaleDetailItems(list);
		String fileName = "xiaoshoumingxi_" + DateHelper.formatDate(new Date(), "yyyyMMddHHmmss");
		String path = ExcelHelper.exportByOrderList2(fileName, list, statistics, searchMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	} 

	/**
	 * 财务自动对账
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param payType 支付方式 1: 微信支付, 2: 支付宝, 3银联支付，
	 * @param file 文件名
	 * @return 成功:{error:0,error:-2:文件数据格式错误,data:[{"tradeCode":商户订单号,"tradeNo":流水号,"tradeType":交易类型(1消费,2退款),"payable":商户交易金额,"thirdPayable":平台交易金额,"tradeTime":交易时间,"payType":支付方式(1微信支付,2支付宝,3银联支付),"differ":对比结果,"status":数据状态(1一一对应,2只存在于数据库,3只存在于excel表)},...]}
	 */
	@Before(AdminAuthInterceptor.class)
	public void checkBalance() {
		String[] params = {"startTime", "endTime", "payType", "file"};
		
		if (!validate(params)) {
			return;
		}
		
		int payType = getParaToInt("payType");
		Date startTime = getParaToDate("startTime");
		Date endTime = getParaToDate("endTime");
		String file = getPara("file");
		
		try {
			AutoBalanceService service = new AutoBalanceService();
			List<Record> list = service.checkBalance(file, payType, startTime, endTime);
			jsonObject.put("data", list);
		} catch (IOException e) {
			e.printStackTrace();
			setError(-2, "文件数据格式错误");
		}

		renderJson(jsonObject);
	}

	/**
	 * 对账明细列表
	 * @param token 帐户访问口令（必填）
	 * @param offset
	 * @param length
	 * @param payType 支付方式 选填
	 * @param tradeType 交易类型(1消费，2退款) 选填
	 * @param status 状态(1:正常,2:平台数据缺失,3:系统数据缺失) 选填
	 * @param differ（1表示等于0，2表示大于0，3表示小于0） 选填
	 * @param startTime 开始时间 选填
	 * @param endTime 结束时间 选填
	 * @return 成功:{error:0,totalPayable:商户交易总金额,totalThirdPayable:平台交易总金额,totalDiffer:对比结果总金额,data:[{"tradeCode":商户订单号,"tradeNo":流水号,"tradeType":交易类型(1消费,2退款),"payable":商户交易金额,"thirdPayable":平台交易金额,"tradeTime":交易时间,"payType":支付方式(1微信支付,2支付宝,3银联支付),"differ":对比结果,"status":数据状态(1一一对应,2只存在于数据库,3只存在于excel表)},...]}
	 */
	@Before(AdminAuthInterceptor.class)
	public void auditRecords() {
		String[] params = {"offset", "length"};
		
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");

		Integer payType = getParaToIntegerDefault("payType");
		Integer tradeType = getParaToIntegerDefault("tradeType");
		Integer status = getParaToIntegerDefault("status");
		Integer differ = getParaToIntegerDefault("differ");
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");

		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("tradeTime", "desc");
		
		List<Record> data = AutoBalanceService.findAuditRecordItems(offset, length, tradeCode, tradeNo, 
				tradeType, startTime, endTime, payType, differ, status, orderByMap);
		int total = AutoBalanceService.countAuditRecordItems(tradeCode, tradeNo, tradeType, startTime, 
				endTime, payType, differ, status);
		List<Record> list = AutoBalanceService.findAuditRecordItems(tradeCode, tradeNo, tradeType, startTime, 
				endTime, payType, differ, status, orderByMap);
		Record statistics = AutoBalanceService.calculateAuditRecord(list);

		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("data", data);
		jsonObject.put("totalPayable", statistics.getDouble("totalPayable"));
		jsonObject.put("totalThirdPayable", statistics.getDouble("totalThirdPayable"));
		jsonObject.put("totalDiffer", statistics.getDouble("totalDiffer"));
		renderJson(jsonObject);
	}

	/**
	 * 导出对账表
	 * @param token 帐户访问口令（必填）
	 * @param payType 支付方式 选填
	 * @param tradeType 交易类型(1消费，2退款) 选填
	 * @param status 状态(1:正常,2:平台数据缺失,3:系统数据缺失) 选填
	 * @param differ（1表示等于0，2表示大于0，3表示小于0） 选填
	 * @param startTime 开始时间 选填
	 * @param endTime 结束时间 选填
	 * @return 成功：{error:0,path:excel文件绝对路径} 失败：{error:>0,errmsg:错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void exportAuditRecords() {
		Integer payType = getParaToIntegerDefault("payType");
		Integer tradeType = getParaToIntegerDefault("tradeType");
		Integer status = getParaToIntegerDefault("status");
		Integer differ = getParaToIntegerDefault("differ");
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		String tradeCode = getPara("tradeCode");
		String tradeNo = getPara("tradeNo");

		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("tradeTime", "desc");
		Map<String, String> headers = ExcelService.exportAuditRecord();
		List<Record> list = AutoBalanceService.findAuditRecordItems(tradeCode, tradeNo, tradeType, 
				startTime, endTime, payType, differ, status, orderByMap);
		Record statistics = AutoBalanceService.calculateAuditRecord(list);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "auditRecord_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportAuditRecord(fileName, headers, list, startTime, endTime, payType, statistics);

		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
	/**
	 * 销售汇总表  -财务
	 * @param offset
	 * @param length
	 * @param payType 支付方式 选填
	 * @param startTime
	 * @param endTime
	 * @return 成功:{error:0,error:-1:文件上传失败,data:[{"payType":支付方式,"totalPayable":销售金额,"totalCost":销售成本,"totalRefundCash":退款金额,"payRateSum":手续费,"totalTaxCost":税额,"balance":余额)},...]}
	 */
	@Before(AdminAuthInterceptor.class)
	public void orderPayList() {
		String[] params = {"offset", "length"};
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		
		if (getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = DateHelper.formatDate(getParaToDate("startTime"));
		}
		
		if (getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = DateHelper.formatDate(getParaToDate("endTime"));
		}
		
		List<Record> list = CaiwuSaleFinaceService.findSaleSummaryItems(null, startTime, endTime, whereInPayType);
		int total = CaiwuSaleFinaceService.countSaleSummaryItems(list);
		Record statistics = CaiwuSaleFinaceService.calculateSaleSummaryItems(list);
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("totalCost", statistics.getDouble("totalCost"));
		jsonObject.put("totalActualProductPrice", statistics.getDouble("totalActualProductPrice"));
		jsonObject.put("totalTaxCost", statistics.getDouble("totalTaxCost"));
		jsonObject.put("totalPayRateSum", statistics.getDouble("totalPayRateSum"));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalPayable", statistics.getDouble("totalPayable"));
		jsonObject.put("totalProductCost", statistics.getDouble("totalProductCost"));
		renderJson(jsonObject);
	}
	
	/**
	 * 获取所有销售汇总表  -财务
	 * @param offset
	 * @param length
	 * @param payType 支付方式 选填
	 * @param startTime
	 * @param endTime
	 * @return 成功:{error:0,error:-1:文件上传失败,data:[{"payType":支付方式,"totalPayable":销售金额,"totalCost":销售成本,"totalRefundCash":退款金额,"payRateSum":手续费,"totalTaxCost":税额,"balance":余额)},...]}
	 */
	@Before(AdminAuthInterceptor.class)
	public void allOrderPayList() {
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		
		if (getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = DateHelper.formatDateTime(getParaToDate("startTime"));
		}
		
		if (getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = DateHelper.formatDateTime(getParaToDate("endTime"));
		}
		
		List<Record> list = CaiwuSaleFinaceService.findSaleSummaryItems(null, startTime, endTime, whereInPayType);
		Record statistics = CaiwuSaleFinaceService.calculateSaleSummaryItems(list);
		
		jsonObject.put("data", list);
		jsonObject.put("totalCost", statistics.getDouble("totalCost"));
		jsonObject.put("totalActualProductPrice", statistics.getDouble("totalActualProductPrice"));
		jsonObject.put("totalTaxCost", statistics.getDouble("totalTaxCost"));
		jsonObject.put("totalPayRateSum", statistics.getDouble("totalPayRateSum"));
		jsonObject.put("totalRefundCash", statistics.getDouble("totalRefundCash"));
		jsonObject.put("totalPayable", statistics.getDouble("totalPayable"));
		renderJson(jsonObject);
	}
	
	
	/**
     * 导出销售汇总表(财务)
	 * @param startTime 开始时间  选填
	 * @param endTime 结束时间  选填
	 * @param payType 支付方式  选填
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0,errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportOrderPayList() {
		String whereInPayType = null;
		if (getPara("payType") != null && !getPara("payType").equals("")) {
			whereInPayType = BaseDao.getWhereIn(getPara("payType"));
		}
		
		String startTime = DateHelper.firstDay();
		String endTime = DateHelper.lastDay();
		
		if (getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = DateHelper.formatDateTime(getParaToDate("startTime"));
		}
		
		if (getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = DateHelper.formatDateTime(getParaToDate("endTime"));
		}
		
		List<Record> list = CaiwuSaleFinaceService.findSaleSummaryItems(null, startTime, endTime, whereInPayType);
		Map<String, String> headers = ExcelService.exportOrderPayList();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "orderPayList_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportOrderPayList(fileName, headers, list);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
	/**
     * 导出销售汇总表(财务)
	 * @param startTime 开始时间  选填
	 * @param endTime 结束时间  选填
	 * @param payType 支付方式  选填
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0,errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportSalesSummary() {
		Integer supplierId = getParaToIntegerDefault("supplierId");
		String supplierName = getPara("supplierName");
		Integer status = getParaToInt("status");
		String startTime = DateHelper.firstDay();
		String endTime  = DateHelper.lastDay();
		
		if(getPara("startTime") != null && !getPara("startTime").equals("")) {
			startTime = getPara("startTime");
		}
		if(getPara("endTime") != null && !getPara("endTime").equals("")) {
			endTime = getPara("endTime");
		}
		
		List<Record> list = SaleFinanceService.findOrderSummaryItems(supplierId, supplierName, status, 
				startTime, endTime);
		Record statistics = SaleFinanceService.calculateOrderSummaryItems(list);
		
		int spId = (supplierId != null) ? supplierId : 0;
		Supplier supplier = Manager.getSupplier(spId);
		String spName = (supplier != null) ? supplier.getName() : "";
		
		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("supplierName", spName);
		searchMap.put("startTime", startTime);
		searchMap.put("endTime", endTime);
		searchMap.put("status", getStatus(status));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "salesSummary_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportSaleSummry(fileName, list, statistics, searchMap);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
	private String getStatus(Integer status) {
		if (status == null) {
			return "";
		}
		
		switch (status) {
		case 1:
			return "已支付";
		case 2:
			return "已发货";
		case 3:
			return "已收货";
		}
		
		return "";
	}
}