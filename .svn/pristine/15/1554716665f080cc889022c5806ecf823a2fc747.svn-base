package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.model.BankCard;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.wallet.Card;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 *   添加银行卡2控制器
 *   @author TangYiFeng
 */
public class AddCardController extends WebappBaseController {
	
    /**
     * 创建银行卡
     *  @param token 帐户访问口令（必填）
     *  @param bankName  开户银行（必填）
     *  @param BankBranch  开户支行（必填）
     *  @param accountNumber  帐户号码（必填）
     *  @param accountName 帐户名（必填）
     *  @param ContactNumber  联系电话（必填）
     *  @return 成功：{error: 0,error:-1 验证码错误}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void createBankCard() {
    	String[] params = {"bankName", "bankBranch", "accountNumber", "accountName", "contactNumber"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String token = getPara("token");
    	String bankName = getPara("bankName");
    	String bankBranch = getPara("bankBranch");
    	String accountNumber = getPara("accountNumber");
    	String accountName = getPara("accountName");
    	String contactNumber = getPara("contactNumber");
    	
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
     * 获取手机验证码
     * @param phone（必填）
     * @return 成功：{error:0,codeToken:token值} 失败：{error:>0, errmsg:错误信息}
     */
    public void getCode() {
    	String[] params = {"phone"};
    	if (!this.validate(params)) {
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
     * 获取银行卡信息
     * @param token 帐户访问口令（必填）
     * @param id 银行卡id
     * @return 成功：{error: 0, data:{id:id,customer_id:会员id,accoutNumber:卡号,accountName:持卡人姓名,bankName:银行名称,bankBranch:支行名称,contactNumber:持卡人姓名}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	BankCard model = Card.getCard(id);
    	
    	jsonObject.put("data", model);
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
    @Before(CustomerWebAppAuthInterceptor.class)
    public void updateBankCard() {
    	String[] params = {"id", "bankName", "bankBranch", "accountNumber", "accountName", "contactNumber"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	String bankName = getPara("bankName");
    	String bankBranch = getPara("bankBranch");
    	String accountNumber = getPara("accountNumber");
    	String accountName = getPara("accountName");
    	String contactNumber = getPara("contactNumber");
    	
    	BankCard model = BankCard.dao.findById(id);
    	model.setBankName(bankName);
    	model.setBankBranch(bankBranch);
    	model.setAccoutNumber(accountNumber);
    	model.setAccountName(accountName);
    	model.setContactNumber(contactNumber);
    	
    	Card card = new Card();
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
    @Before(CustomerWebAppAuthInterceptor.class)
    public void removeBankCard() {
    	String[] params = {"id"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	if(Card.deleteCard(id) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "delete bankCard failed");
    	}
    	
    	renderJson(jsonObject);
    }

}