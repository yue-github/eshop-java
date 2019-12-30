package com.eshop.controller.pc;

import java.io.IOException;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.wallet.Recharge;
import com.jfinal.aop.Before;

/**
 * 我的钱包-控制器
 * @author TangYiFeng
 */
public class WalletController extends PcBaseController {
	
    /**
     * 构造方法
     */
    public WalletController() {
    }

    /**
     * 获取帐户余额
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0, balance: 余额}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
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
     * @param event 类型(3支付宝充值 , 4微信充值，6银联充值)（必填）
     * @param note 备注
     * @return 成功：{error: 0,error:-1(金额不能为0),payType:支付类型(4微信,3支付宝),payInfo:支付信息}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void recharge() {
    	/*if (!this.validateRequiredString("money")) {
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
    	
    	if (event == 4) {   //微信支付
    		String notifyUrl = "http://service.eebin.com/pc/wallet/wxPcRechargeCallback";
    		payInfo = orderService.wxpay("乐驿", tradeNo + "", totalPayInt + "", this.getRequest(), notifyUrl);
    		
    		String path = this.getBaseUrl();
    		if (path.indexOf("eebin.com") != -1) {
    			path = this.getHostName() + "/";
    		}
    		
    		payInfo = path + payInfo;
    	} else if (event == 3) {  //支付宝支付
    		String notifyUrl = "http://service.eebin.com/pc/wallet/alipayRechargeCallBack";
    		String returnUrl = "http://www.eebin.com/#/home";
    		payInfo = orderService.alipay(tradeNo + "", "乐驿商城", totalPayInt * 0.01 + "", "", notifyUrl, returnUrl);
    	}  else if (event == 6) {  //银联宝支付
    		String notifyUrl = "http://service.eebin.com/pc/wallet/unionRechargeCallBack";
    		String returnUrl = "http://www.eebin.com";
    		payInfo = unionpayHelper.getHtml(totalPayInt + "", tradeNo, returnUrl, notifyUrl);
    	}
    	
    	jsonObject.put("payType", event);
    	jsonObject.put("payInfo", payInfo);
    	renderJson(jsonObject);*/
    }
    
    /**
     * 获取充值记录
     *  @param token 帐户访问口令（必填）
     *  @param pageNumber 当前页，用于分页（必填）
     *  @return 成功：{error: 0, totalPages: 结果总数, data:[{id:id, event:类型, money:金额, created_at:充值时间, finishTime:完成时间}]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void many() {
    	/*if (!this.validateRequiredString("token")) {
    		return;
    	}
    	
    	if (!this.validateRequiredString("pageNumber")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	int pageNumber = getParaToInt("pageNumber");
    	
    	Page<Wallet> page = walletService.recharge(customerId, pageNumber, 20);
    	
    	jsonObject.put("data", page.getList());
    	jsonObject.put("totalPages", page.getTotalPage());
    	renderJson(jsonObject);*/
    }
    
    /**
	 * 微信充值回调
	 * @return
	 * @throws IOException
	 */
	public void wxPcRechargeCallback() throws IOException {
		/*HttpServletRequest request  = this.getRequest();
	    InputStream inputStream;  
	    StringBuffer sb = new StringBuffer();  
	    inputStream = request.getInputStream();  
	    String s ;  
	    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));  
	    
	    while ((s = in.readLine()) != null){  
	        sb.append(s);  
	    } 
	    
	    in.close();  
	    inputStream.close();  
		
		String data = sb.toString();
		
	    if(WxPayPCHelper.wxPcRechargeCallback(data) == ServiceCode.Success) {
	    	renderText("success");
	    }
	    
	    renderText("falied");*/
	}
	
	/**
	 * 支付宝充值回调
	 * @throws IOException 
	 */
	public void alipayRechargeCallBack() throws IOException {
		/*HttpServletResponse response = this.getResponse();
		PrintWriter out = response.getWriter();
		
		HttpServletRequest request = this.getRequest();
		Map requestParams = request.getParameterMap();
		
		//商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
		//支付宝交易号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

		boolean isSuccess = AlipayHelper.alipayRechargeCallBack(requestParams, out_trade_no, trade_no, trade_status);
		
		if (!isSuccess) {
			out.print("fail");
		}
		
		out.print("success");*/
	}
	
	/**
	 * 银联充值回调地址
	 */
	public void unionRechargeCallBack() {
		/*try {
			UnionpayHelper.backRcvResponseRecharge(this.getRequest(), this.getResponse());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	/**
	 * 添加余额支付密码
	 * @balancePwd
	 * @reBalancePwd
	 * @return 成功：{error:0,error:-1(支付密码不能为空),error:-2(输入的密码不一致)} 失败：{error:>0, errmsg:错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void addBalancePwd() {
		/*if (!this.validateRequiredString("balancePwd")) {
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
		
		renderJson(jsonObject);*/
	}
	
	/**
	 * 判断余额支付密码是否正确
	 * @param balancePwd
	 * @return 成功：{error:0(密码正确), error:-1(密码错误)} 错误：{error:>0,errmsg:错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void isCorrectBalancePwd() {
		/*if (!this.validateRequiredString("balancePwd")) {
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
		
		renderJson(jsonObject);*/
	}
	
	/**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    public void getCode() {
    	/*if (!this.validateRequiredString("phone")) {
    		return;
    	}
    	
    	String phone = getPara("phone");
    	String code = CommonService.getRandom(100000, 1000000) + "";
    	String content = "【乐驿商城】您设置余额支付密码的手机验证码为" + code;
    	
    	Record result = CommonService.sendMessage(phone, content, code);
    	
    	if (result.getInt("error") != 0 ) {
    		returnError(ErrorCode.Exception, "发送短信失败");
    		return;
    	}
    	
    	jsonObject.put("codeToken", result.get("codeToken"));
    	renderJson(jsonObject);*/
    }
    
    /**
     * 找回余额支付密码
     * @param balancePwd
     * @param reBalancePwd
     * @param codeToken 手机验证码token
     * @param code 手机验证码
     * @return 成功：{error:0,error:-1(支付密码不能为空),error:-2(输入的密码不一致),error:-3(手机验证码不正确)} 失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void findBalancePwd() {
    	/*if (!this.validateRequiredString("balancePwd")) {
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
    	
    	renderJson(jsonObject);*/
    }
}