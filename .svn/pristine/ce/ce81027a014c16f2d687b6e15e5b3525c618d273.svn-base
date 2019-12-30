package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.wallet.Card;
import com.eshop.wallet.Recharge;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 我的钱包控制器
 * @author TangYiFeng
 */
public class WalletController extends WebappBaseController {

    /**
     * 构造方法
     */
    public WalletController() {
    }
    
    /**
     * 充值
     * @param token 帐户访问口令（必填）
     * @param money 金额（必填）
     * @param event 类型(6银联充值)（必填）
     * @param note 备注
     * @return 成功：{error: 0,error:-1(金额不能为0),payType:支付类型(4微信,3支付宝),payInfo:支付信息}；失败：{error: >0, errmsg: 错误信息}
     */
    /*@Before(CustomerPcAuthInterceptor.class)
    public void recharge() {
    	if (!this.validateRequiredString("money")) {
    		return;
    	}
    	
    	if (!this.validateRequiredString("event")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	int event = getParaToInt("event");
    	String note = (getPara("note") != null) ? getPara("note") : "";
    	BigDecimal money = getParaToDecimal("money");
    	
    	if (money.doubleValue() < 0.01) {
    		this.returnError(-1, "金额不能小于0.01");
    		return;
    	}
    	
    	SimpleDateFormat ft =new SimpleDateFormat("yyyyMMddHHmmss");
    	String tradeNo = "1" + ft.format(new Date()) + "2";
    	
    	Wallet model = new Wallet();
    	model.setCustomerId(customerId);
    	model.setEvent(event);
    	model.setMoney(money);
    	model.setNote(note);
    	model.setTradeNo(tradeNo);
    	
    	ServiceCode code = walletService.createWallet(model);
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "充值失败");
    		return;
    	}
    	
    	String payInfo = "";
    	double totalPay = money.doubleValue() * 100;
    	int totalPayInt = (int) totalPay;
    	
    	if (event == 6) {  //银联宝支付
    		String notifyUrl = "http://service.eebin.com/pc/wallet/unionRechargeCallBack";
    		String returnUrl = "http://www.eebin.com";
    		payInfo = unionpayHelper.getHtml(totalPayInt + "", tradeNo, returnUrl, notifyUrl);
    	}
    	
    	jsonObject.put("payType", event);
    	jsonObject.put("payInfo", payInfo);
    	renderJson(jsonObject);
    }*/

    /**
     * 获取帐户信息
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0,info:{money:账号余额，bankNum:我的银行卡数，goldNum:我的金币数}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void balance() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Record result = new Record();
    	double money = Recharge.myBalance(customerId);
    	int bankNum = Card.countCardItems(customerId, null, null, null, null, null);
    	int goldNum = customer.getGolds();
    	result.set("money", money);
    	result.set("bankNum", bankNum);
    	result.set("goldNum", goldNum);
        
        jsonObject.put("info", result);
        renderJson(jsonObject);
    }
    
    /**
	 * 判断是否已经设置余额支付密码
	 * @return 成功：{error:0(已经设置余额支付密码)，error:-1(还没设置余额支付密码)} 失败：{error:>0, errmsg:错误信息}
	 */
	/*@Before(CustomerWebAppAuthInterceptor.class)
	public void isSetBalancePwd() {
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int customerId = customer.getId();
		
		boolean isSet = walletService.isSetBalancePwd(customerId);
		
		if (!isSet) {
			returnError(-1, "还没设置余额支付密码");
			return;
		}
		 
		renderJson(jsonObject);
	}*/
	
	/**
	 * 添加余额支付密码
	 * @balancePwd
	 * @reBalancePwd
	 * @return 成功：{error:0,error:-1(支付密码不能为空),error:-2(输入的密码不一致)} 失败：{error:>0, errmsg:错误信息}
	 */
	/*@Before(CustomerPcAuthInterceptor.class)
	public void addBalancePwd() {
		if (!this.validateRequiredString("balancePwd")) {
			return;
		}
		
		if (!this.validateRequiredString("reBalancePwd")) {
			return;
		}
		
		String balancePwd = getPara("balancePwd");
		String reBalancePwd = getPara("reBalancePwd");
		
		if (balancePwd.equals("") || reBalancePwd.equals("")) {
			returnError(-1, "支付密码不能为空");
			return;
		}
		
		if (!balancePwd.equals(reBalancePwd)) {
			returnError(-2, "输入的支付密码不一致");
			return;
		}
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int customerId = customer.getId();
		
		ServiceCode code = walletService.setBalancePwd(customerId, reBalancePwd);
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "添加支付密码失败");
			return;
		}
		
		renderJson(jsonObject);
	}*/
	
	/**
	 * 判断余额支付密码是否正确
	 * @param balancePwd
	 * @return 成功：{error:0(密码正确), error:-1(密码错误)} 错误：{error:>0,errmsg:错误信息}
	 */
	/*@Before(CustomerPcAuthInterceptor.class)
	public void isCorrectBalancePwd() {
		if (!this.validateRequiredString("balancePwd")) {
			return;
		}
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int customerId = customer.getId();
		
		String balancePwd = getPara("balancePwd");
		
		boolean isCorrect = walletService.isCorrectBalancePwd(customerId, balancePwd);
		
		if (!isCorrect) {
			this.returnError(-1, "密码错误");
			return;
		}
		
		renderJson(jsonObject);
	}*/
	
	/**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    public void getCode() {
    	if (!this.validateRequiredString("phone")) {
    		return;
    	}
    	
    	String phone = getPara("phone");
    	Record result = SMSHelper.sendCode(phone);
    	
    	if (result.getInt("error") != 0 ) {
    		returnError(ErrorCode.Exception, "发送短信失败");
    		return;
    	}
    	
    	jsonObject.put("codeToken", result.get("codeToken"));
    	renderJson(jsonObject);
    }
    
    /**
     * 找回余额支付密码
     * @param balancePwd
     * @param reBalancePwd
     * @param codeToken 手机验证码token
     * @param code 手机验证码
     * @return 成功：{error:0,error:-1(支付密码不能为空),error:-2(输入的密码不一致),error:-3(手机验证码不正确)} 失败：{error:>0,errmsg:错误信息}
     */
    /*@Before(CustomerPcAuthInterceptor.class)
    public void findBalancePwd() {
    	if (!this.validateRequiredString("balancePwd")) {
			return;
		}
		
		if (!this.validateRequiredString("reBalancePwd")) {
			return;
		}
		
		if (!this.validateRequiredString("codeToken")) {
			return;
		}
		
		if (!this.validateRequiredString("code")) {
			return;
		}
		
		String balancePwd = getPara("balancePwd");
		String reBalancePwd = getPara("reBalancePwd");
		String codeToken = getPara("codeToken");
		String code = getPara("code");
		
		if (balancePwd.equals("") || reBalancePwd.equals("")) {
			returnError(-1, "支付密码不能为空");
			return;
		}
		
		if (!balancePwd.equals(reBalancePwd)) {
			returnError(-2, "输入的支付密码不一致");
			return;
		}
		
		ServiceCode sCode = accountService.hasCode(codeToken, code);
    	
    	//判断验证码是否正确
    	if (sCode != ServiceCode.Success) {
    		returnError(-3, "手机验证码不正确");
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode rCode = walletService.setBalancePwd(customerId, reBalancePwd);
    	
    	if (rCode != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "设置支付密码失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }*/
}