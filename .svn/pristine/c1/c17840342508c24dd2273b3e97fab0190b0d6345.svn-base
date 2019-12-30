package com.eshop.controller.webapp;


import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.wallet.Recharge;
import com.jfinal.aop.Before;

/**
 * 充值控制器
 * @author TangYiFeng
 */
public class RechargeController extends WebappBaseController {

    /**
     * 构造方法
     */
    public RechargeController() {
    }

    /**
     * 获取帐户余额
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0, balance: 余额}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void balance() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	double balance = Recharge.myBalance(customerId);
    	
    	jsonObject.put("balance", balance);
    	renderJson(jsonObject);
    }
    
    /**
     * 充值
     * @param token 帐户访问口令（必填）
     * @param money 金额（必填）
     * @param event 类型(3支付宝充值 , 4微信充值)（必填）
     * @param note 备注
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
//    @Before(CustomerWebAppAuthInterceptor.class)
//    public void recharge() {
//    	if (!this.validateRequiredString("token")) {
//    		return;
//    	}
//    	
//    	if (!this.validateRequiredString("money")) {
//    		return;
//    	}
//    	
//    	if (!this.validateRequiredString("event")) {
//    		return;
//    	}
//    	
//    	String token = getPara("token");
//    	Customer customer = (Customer) CacheHelper.get(token);
//    	int customerId = customer.getId();
//    	
//    	BigDecimal money = getParaToDecimal("money");
//    	int event = getParaToInt("event");
//    	String note = (getPara("note") != null) ? getPara("note") : "";
//    	
//    	SimpleDateFormat ft =new SimpleDateFormat("yyyyMMddHHmmss");
//    	String tradeNo = "1" + ft.format(new Date()) + "2";
//    	
//    	Wallet model = new Wallet();
//    	model.setCustomerId(customerId);
//    	model.setEvent(event);
//    	model.setMoney(money);
//    	model.setNote(note);
//    	model.setTradeNo(tradeNo);
//    	
//    	ServiceCode code = walletService.createWallet(model);
//    	
//    	if (code != ServiceCode.Success) {
//    		this.returnError(1, "充值失败");
//    		return;
//    	}
//    	
//    	jsonObject.put("money", CommonService.cutDecimal(money.doubleValue()));
//    	jsonObject.put("tradeNo", model.getTradeNo());
//    	renderJson(jsonObject);
//    	
//    }
    
    /**
     * 改变支付状态
     */
//    @Before(CustomerWebAppAuthInterceptor.class)
//    public void changePayByTradeNo() {
//    	if (!this.validateRequiredString("tradeNo")) {
//    		return;
//    	}
//    	
//    	String tradeNo = getPara("tradeNo");
//    	
//    	ServiceCode code = walletService.changePayByTradeNo(tradeNo);
//    	
//    	if (code != ServiceCode.Success) {
//    		this.returnError(ErrorCode.Exception, "失败");
//    		return;
//    	}
//    	
//    	renderJson(jsonObject);
//    }

}