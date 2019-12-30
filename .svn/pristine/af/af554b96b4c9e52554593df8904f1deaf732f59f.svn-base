package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.BankCard;
import com.eshop.model.Customer;
import com.eshop.wallet.Card;
import com.eshop.wallet.Recharge;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 提现控制器
 * @author TangYiFeng
 */
public class WithdrawController extends WebappBaseController {
	
    /**
     * 构造方法
     */
    public WithdrawController() {
    }

    /**
     * 我的账号余额
     * @param token
     * @return 成功：{error:0, walletAmount:账号余额} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void myWalletAmount() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	double walletAmount = Recharge.myBalance(customerId);
    	
    	jsonObject.put("walletAmount", walletAmount);
    	renderJson(jsonObject);
    }
    
    /**
     * 申请提现
     * @param token 帐户访问口令（必填）
     * @param accountType 帐户类型（1支付宝账号，2微信账号，3银行卡）（必填）
     * @param money 金额（必填）
     * @param aplipayAccount  支付宝帐号
     * @param weixinAccount  微信帐号
     * @param bankCard_id  银行卡id
     * @param note  备注
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
//    @Before(CustomerWebAppAuthInterceptor.class)
//    public void submit() {
//    	if (!this.validateRequiredString("token")) {
//        	return;
//        }
//        
//        if (!this.validateRequiredString("accountType")) {
//        	return;
//        }
//        
//        if (!this.validateRequiredString("money")) {
//        	return;
//        }
//        
//        String token = getPara("token");
//        Customer customer = (Customer) CacheHelper.get(token);
//        int customerId = customer.getId();
//        
//        int accountType = getParaToInt("accountType");
//        BigDecimal money = getParaToDecimal("money");
//        
//        WithdrawCash model = new WithdrawCash();
//        model.setCustomerId(customerId);
//        model.setAccountType(accountType);
//        model.setMoney(money);
//        
//        if (getPara("aplipayAccount") != null) {
//        	model.setAplipayAccount(getPara("aplipayAccount"));
//        }
//        
//        if (getPara("weixinAccount") != null) {
//        	model.setWeixinAccount(getPara("weixinAccount"));
//        }
//        
//        if (getPara("bankCard_id") != null) {
//        	model.setBankcardId(getParaToInt("bankCard_id"));
//        }
//        
//        if (getPara("note") != null) {
//        	model.setNote(getPara("note"));
//        }
//        
//        ServiceCode code = withdrawCashService.applyCash(model);
//        
//        if (code != ServiceCode.Success) {
//        	this.returnError(ErrorCode.Exception, "申请提现失败");
//        	return;
//        }
//        
//        renderJson(jsonObject);
//    }
    
    /**
     * 我的银行卡列表
     * @param token
     * @param offset
     * @param length
     * @return 成功：{error:0, data:[{id:id, bankName:账号名称}]} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void myBankCardList() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> data = Card.findCardItems(offset, length, customerId, null, null, null, null, null);
    	int totalRow = Card.countCardItems(customerId, null, null, null, null, null);
    	
    	jsonObject.put("data", data);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", totalRow);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取银行卡信息
     * @param token
     * @param bankCardId 银行id
     * @return 成功：{error:0, data:{id:id,accoutNumber:银行账号}} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void getBankCard() {
    	String[] params = {"bankCardId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int bankCardId = getParaToInt("bankCardId");
    	BankCard model = Card.getCard(bankCardId);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
}