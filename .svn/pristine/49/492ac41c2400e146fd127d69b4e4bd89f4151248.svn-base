package com.eshop.controller.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.eshop.finance.ExcelService;
import com.eshop.finance.ExportDataService;
import com.eshop.helper.ExcelHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.invoice.InvoiceRecordService;
import com.eshop.model.Customer;
import com.eshop.service.User;
import com.eshop.wallet.Recharge;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * excel-控制器
 * @author TangYiFeng
 */
public class ExcelController extends AdminBaseController {
	
	public ExcelController() {
	}
	
	/**
     * 导出发票
     * @param orderId
     * @param type
     * @param invoiceHead
     * @param orderCode
     * @param invoiceCode
     * @param theSameOrderNum
     * @param startTime
     * @param endTime
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0,errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportInvoice() {
		Integer type = getParaToInt("type");
    	String invoiceHead = getPara("invoiceHead");
    	String orderCode = getPara("orderCode");
    	String invoiceCode = getPara("invoiceCode");
    	String theSameOrderNum = getPara("theSameOrderNum");
    	String startTime = getParaToDateTimeDefault("startTime");
    	String endTime = getParaToDateTimeDefault("endTime");
    	Double moneyMoreThan = getParaToDoubleDefault("moneyMoreThan");
    	Double moneyLessThan = getParaToDoubleDefault("moneyLessThan");
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
		Map<String, String> headers = ExcelService.exportInvoice();
		List<Record> list = InvoiceRecordService.findInvoiceRecordItems(null, type, invoiceHead, null, null, null, null, null, null, null, null, null, null, null, null, orderCode, startTime, endTime, invoiceCode, theSameOrderNum,moneyMoreThan,moneyLessThan, orderByMap);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "invoice_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportInvoice(fileName, headers, list);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
	/**
     * 根据税率标识导出订单
     * @param startTime
     * @param endTime
     * @return 成功：{error:0,path:excel文件绝对路径}  失败：{error:>0, errmsg:错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportByOrderTax() {
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		
		Map<String, String> headers = ExcelService.exportByOrderTax();
		List<Record> list = ExportDataService.findExportOrderTaxItems(null, null, startTime, endTime);
		Record statistics = ExportDataService.calculateExportOrderTaxItems(list);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "taxOrder_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportByOrderTax(fileName, headers, list, statistics);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}
	
	/**
	 * 导出用户余额
	 * @return 成功：{error: 0,path:文件路径}；失败：{error: >0, errmsg: 错误信息}
	 */
	public void exportBalance() {
		List<Customer> customers = Customer.dao.find("select * from customer");
		
		for (Customer item : customers) {
			int customerId = item.getId();
			double balance = Recharge.myBalance(customerId);
			item.put("balance", balance);
		}
		
		List<Customer> list = new ArrayList<Customer>();
		
		for (Customer item : customers) {
			double balance2 = item.getDouble("balance");
			if (balance2 > 0) {
				list.add(item);
			}
		}
		
		Map<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("num", "序列");
    	headers.put("name", "姓名");
    	headers.put("mobilePhone", "手机号码");
    	headers.put("balance", "余额");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "balance_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportBalance(fileName, headers, list);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}

	/**
     * 导出订单
     * @param status  订单状态（-1为全部,1待付款,2待发货,3已发货,4已收货,5取消订单） 必填
     * @param startTime 开始时间  选填
     * @param endTime  结束时间  选填
     * @param order_code 订单号  选填
     * @param tradeCode 商户单号  选填
     * @param tradeNo  流水号   选填
     * @return 成功：{error: 0,path:文件路径}；失败：{error: >0, errmsg: 错误信息}
     */
	@Before(AdminAuthInterceptor.class)
	public void exportOrders() {
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
		
		Map<String, String> headers = ExcelService.exportOrders();
		List<Record> data = ExcelService.findExportOrderItems(null, null, orderCode, tradeCode, tradeNo, 
				status, startTime, endTime, receiveName, preferredContactPhone, source, payType, 
				logisticsName, expressCode, null, null);
		List<Record> list = User.findOrderItems(null, null, orderCode, tradeCode, tradeNo, 
				status, startTime, endTime, receiveName, preferredContactPhone, source, 
				payType, logisticsName, expressCode, null, null);
		Record statistics = User.calculateOrderItems(list);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "order_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportOrder(fileName, headers, data, status, statistics);
		
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}

}
