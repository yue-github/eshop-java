package com.eshop.controller.pc;

import java.util.*;

import com.eshop.finance.ExcelService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.DateHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Merchant;
import com.eshop.service.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;


/**
 * 店铺订单控制器
 * @author TangYiFeng
 */
public class ShopOrdersController extends PcBaseController {
	
	ExcelService excelService;
	
    /**
     * Default constructor
     */
    public ShopOrdersController() {
    	excelService = new ExcelService();
    }

    /**
     * 商家订单列表
     * @param token 帐户访问口令（必填）
     * @param offset 当前页，用于分页（必填）
     * @param length 每页条数（必填）
     * @param status 订单状态（null为全部,1待支付,2待发货,3待收货,4已收货） 必填
     * @param order_code 订单号 选填
     * @param startTime 开始时间  选填
     * @param endTime  结束时间  选填
     * @param timeType 时间类型(1下单时间，2支付时间，3发货时间，4收货时间，5取消订单时间)  选填
     * @return 成功：{error: 0, error:-1(店铺不存在), totalPages:总页数 orders: [{id:订单id, order_code:订单号, created_at:下单时间, shopName:店铺名称, shopLogo:店铺logo, details: [{id:订单明细id,product_id:产品id,product_name:产品名称,mainPic:主图,unitOrdered:数量,actualUnitPrice:价格,totalActualProductPrice:总价,selectProterties:属性}, ...]}；失败：{error: >0, errmsg: 错误信息}
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
    	status = (status != null) ? status : -1;
    	String orderCode = getPara("order_code");
    	String startTime = null;
    	String endTime = null;
    	if (getPara("startTime") != null) {
    		startTime = DateHelper.formatDate(getParaToDate("startTime"));
    	}
    	if (getPara("endTime") != null) {
    		endTime = DateHelper.formatDate(getParaToDate("endTime"));
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("o.created_at", "desc");
    	
    	List<Record> data = User.findOrderItems(offset, length, null, shopId, orderCode, null, null, 
    			status, startTime, endTime, null, null, null, null, null, null, null, orderByMap);
    	int total = User.countOrderItems(null, shopId, orderCode, null, null, status, startTime, endTime, 
    			null, null, null, null, null, null);
    	
    	for (Record item : data) {
    		List<Record> details = User.findProductOrderItems(item.getInt("id"), null, null);
    		item.set("details", details);
    	}
    	
    	jsonObject.put("orders", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 订单发货
     * @param token 帐户访问口令（必填）
     * @param id 订单id（必填）
     * @param deliveryCompany: 快递公司（必填）
     * @param deliveryCompanyNum 快递公司编号
     * @param trackingNumber: 快递单号（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void sendOut() {
    	String[] params = {"id", "deliveryCompany", "deliveryCompanyNum", "trackingNumber"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int orderId = getParaToInt("id");
    	String deliveryCompany = getPara("deliveryCompany");
    	String deliveryCompanyNum = getPara("deliveryCompanyNum");
    	String trackingNumber = getPara("trackingNumber");
    	
    	Merchant merchant = new Merchant();
    	ServiceCode code = merchant.delivery(orderId, deliveryCompany, deliveryCompanyNum, trackingNumber);
    	
    	if(code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "sendOut "+ orderId +" failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 导出店铺订单
     * @param token 帐户访问口令（必填）
     * @param status 订单状态（null为全部,1待支付,2待发货,3待收货,4已收货） 必填
     * @param order_code 订单号 选填
     * @param startTime 开始时间  选填
     * @param endTime  结束时间  选填
     * @param timeType 时间类型(1下单时间，2支付时间，3发货时间，4收货时间，5取消订单时间)  选填
     * @return 成功：{error: 0, error:-1(店铺不存在), totalPages:总页数 orders: [{id:订单id, order_code:订单号, created_at:下单时间, shopName:店铺名称, shopLogo:店铺logo, details: [{id:订单明细id,product_id:产品id,product_name:产品名称,mainPic:主图,unitOrdered:数量,actualUnitPrice:价格,totalActualProductPrice:总价,selectProterties:属性}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
	//@Before(CustomerPcAuthInterceptor.class)
	public void exportOrders2() {
		Integer status = getParaToIntegerDefault("status");
		status = (status != null) ? status : -1;
		String orderCode = getPara("order_code");
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
		
		Map<String, String> headers = ExcelService.exportOrders();
		List<Record> data = ExcelService.findExportOrderItems(null, null, orderCode, tradeCode, tradeNo, 
				status, startTime, endTime, receiveName, preferredContactPhone, source, payType, 
				logisticsName, expressCode, null, null);
		List<Record> list = User.findOrderItems(null, null, orderCode, tradeCode, tradeNo, 
				status, startTime, endTime, receiveName, preferredContactPhone, source, 
				payType, logisticsName, expressCode, null, null);
		Record statistics = User.calculateOrderItems(list);
		
		String path = ExcelHelper.exportOrder("订单列表", headers, data, status, statistics);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
}