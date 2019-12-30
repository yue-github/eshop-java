package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.eshop.finance.ExcelService;
import com.eshop.helper.ExcelHelper;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.wallet.Recharge;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 钱包管理
 * @author TangYiFeng
 */
public class WalletController extends AdminBaseController {
	
    /**
     * Default constructor
     */
    public WalletController() {
    }
    
    /**
     * 获取充值记录列表
     * @param token
     * @param start
     * @param offset 页码
     * @param length 每页显示条数
     * @param customerName 用户名称  选填
     * @param customerPhone 手机号码  选填
     * @param event 充值类型(3支付宝充值 , 4微信充值，6银联充值)  选填  
     * @param tradeNo 商户单号  选填
     * @param startTime 充值开始时间   选填
     * @param endTime 充值结束时间   选填
     * @return 成功：{error: 0,totalMoney:总充值金额, offset: 页码, totalRow: 总数, recordsFiltered: 过滤后总数, data:[{id:id,tradeNo:商户单号,event:类型(3支付宝充值 , 4微信充值，6银联充值),money:充值金额,created_at:充值时间,customerName:充值人姓名,customerName:充值人手机号},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	String customerName = getPara("customerName");
    	String customerPhone = getPara("customerPhone");
    	Integer event = getParaToIntegerDefault("event");
    	String tradeNo = getPara("tradeNo");
    	String startTime = getParaToDateTimeDefault("startTime");
    	String endTime = getParaToDateTimeDefault("endTime");
    	Integer isPaySuccess = 1;
    	
    	BigDecimal moneyMoreThan = getParaToDecimalDefault("moneyMoreThan");
    	BigDecimal moneyLessThan = getParaToDecimalDefault("moneyLessThan");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> data = Recharge.findRechargeItems(offset, length, null, event, startTime, endTime, null, null, tradeNo, isPaySuccess, null, null, customerName, customerPhone, orderByMap, moneyMoreThan, moneyLessThan);
    	int total = Recharge.countRechargeItems(null, event, startTime, endTime, null, null, tradeNo, isPaySuccess, null, null, customerName, customerPhone, moneyMoreThan, moneyLessThan);
    	List<Record> list = Recharge.findRechargeItems(null, event, startTime, endTime, null, null, tradeNo, isPaySuccess, null, null, customerName, customerPhone, orderByMap, moneyMoreThan, moneyLessThan);
    	Record statistics = Recharge.calculateRechargeItems(list);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("totalMoney", statistics.getDouble("totalMoney"));
    	renderJson(jsonObject);
    }
    
    /**
	 * 导出充值记录
	 * @param customerName 用户名称  选填
     * @param customerPhone 手机号码  选填
     * @param event 充值类型(3支付宝充值 , 4微信充值，6银联充值)  选填  
     * @param tradeNo 商户单号  选填
     * @param startTime 充值开始时间   选填
     * @param endTime 充值结束时间   选填
	 * @return 成功：{error: 0,path:文件路径}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void exportRecharge() {
		String[] params = {"offset", "length"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	String customerName = getPara("customerName");
    	String customerPhone = getPara("customerPhone");
    	Integer event = getParaToIntegerDefault("event");
    	String tradeNo = getPara("tradeNo");
    	String startTime = getParaToDateTimeDefault("startTime");
    	String endTime = getParaToDateTimeDefault("endTime");
    	Integer isPaySuccess = 1;
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
		Map<String, String> headers = ExcelService.exportRecharge();
		List<Record> list = Recharge.findRechargeItems(null, event, startTime, endTime, null, null, tradeNo, isPaySuccess, null, null, customerName, customerPhone, orderByMap, null, null);
		Record statistics = Recharge.calculateRechargeItems(list);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "recharge_" + dateFormat.format(new Date());
		String path = ExcelHelper.exportRecharge(fileName, headers, list, statistics);
		jsonObject.put("path", this.getPath(path));
		renderJson(jsonObject);
	}

    /**
     * 获取某条充值明细
     * @param id 充值id
     * @return 成功：{error: 0,data:{id:id,event:类型(3支付宝充值 , 4微信充值，6银联充值),money:充值金额,created_at:充值时间,customerName:充值人姓名,customerName:充值人手机号}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record wallet = Recharge.get(id);
    	
    	jsonObject.put("data", wallet);
    	renderJson(jsonObject);
    }
    
}