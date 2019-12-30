package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.BankCard;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.wallet.Card;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 我的银行卡控制器
 *   @author TangYiFeng
 */
public class CardController extends PcBaseController {
	
	private Card card;

    /**
     * Default constructor
     */
    public CardController() {
    	card = new Card();
    }
    
    /**
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    /*public void getCode() {
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
    }*/

    /**
     * 创建银行卡
     *  @param token 帐户访问口令（必填）
     *  @param bankName  开户银行（必填）
     *  @param BankBranch  开户支行（必填）
     *  @param accountNumber  帐户号码（必填）
     *  @param accountName 帐户名（必填）
     *  @param ContactNumber  联系电话（必填）
     *  @param code 验证码
     *  @param codeToken 验证码token值
     *  @return 成功：{error: 0,error:-1 验证码错误}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void createBankCard() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	String token = getPara("token");
    	if(!this.validateRequiredString("bankName")) {
    		return;
    	}
    	String bankName = getPara("bankName");
    	if(!this.validateRequiredString("bankBranch")) {
    		return;
    	}
    	String bankBranch = getPara("bankBranch");
    	if(!this.validateRequiredString("accountNumber")) {
    		return;
    	}
    	String accountNumber = getPara("accountNumber");
    	if(!this.validateRequiredString("accountName")) {
    		return;
    	}
    	String accountName = getPara("accountName");
    	if(!this.validateRequiredString("contactNumber")) {
    		return;
    	}
    	String contactNumber = getPara("contactNumber");
    	/*if(!this.validateRequiredString("code")) {
    		return;
    	}
    	String code = getPara("code");
    	if(!this.validateRequiredString("codeToken")) {
    		return;
    	}
    	String codeToken = getPara("codeToken");
    	
    	ServiceCode sCode = Member.hasCode(codeToken, code);
    	if (sCode != ServiceCode.Success) {
    		returnError(-1, "验证码不正确");
    		return;
    	}*/
    	
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	BankCard model = new BankCard();
    	model.setBankName(bankName);
    	model.setBankBranch(bankBranch);
    	model.setAccoutNumber(accountNumber);
    	model.setAccountName(accountName);
    	model.setContactNumber(contactNumber);
    	model.setCustomerId(customerId);
    	
    	if(Card.createCard(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "create bankCard failed");
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 修改银行卡
     *  @param token 帐户访问口令（必填）
     *  @param id 银行卡id（必填）
     *  @param bankName  开户银行（必填）
     *  @param BankBranch  开户支行（必填）
     *  @param accountNumber  帐户号码（必填）
     *  @param accountName 帐户名（必填）
     *  @param contactNumber  联系电话（必填）
     *  @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void updateBankCard() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	if(!this.validateRequiredString("bankName")) {
    		return;
    	}
    	String bankName = getPara("bankName");
    	if(!this.validateRequiredString("bankBranch")) {
    		return;
    	}
    	String bankBranch = getPara("bankBranch");
    	if(!this.validateRequiredString("accountNumber")) {
    		return;
    	}
    	String accountNumber = getPara("accountNumber");
    	if(!this.validateRequiredString("accountName")) {
    		return;
    	}
    	String accountName = getPara("accountName");
    	if(!this.validateRequiredString("contactNumber")) {
    		return;
    	}
    	String contactNumber = getPara("contactNumber");
    	
    	BankCard model = BankCard.dao.findById(id);
    	model.setBankName(bankName);
    	model.setBankBranch(bankBranch);
    	model.setAccoutNumber(accountNumber);
    	model.setAccountName(accountName);
    	model.setContactNumber(contactNumber);
    	
    	if(card.updateCard(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "update bankCard failed");
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 移除银行卡
     *  @param token 帐户访问口令（必填）
     *  @param id 银行卡ID（必填）
     *  @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void removeBankCard() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	
    	if(Card.deleteCard(id) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "delete bankCard failed");
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 我的银行卡
     *  @param token 帐户访问口令（必填）
     *  @return 成功：{error: 0, data:[{id:id, accountNumber:卡号, bankBranch:银行卡名称}]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void many() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = Card.findCardItems(customerId, null, null, null, null, null); 
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

    /**
     * 获取银行卡信息
     * @param token 帐户访问口令（必填）
     * @param id 银行卡id
     * @return 成功：{error: 0, data:{id:id, accountNumber:卡号, bankBranch:银行卡名称}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void get() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	BankCard model = Card.getCard(id);
    	
    	jsonObject.put("data", model);
    	renderJson(model);
    }

}