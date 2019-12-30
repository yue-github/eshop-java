package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.wallet.WithDraw;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 提现管理
 * @author TangYiFeng
 */
public class WithdrawCashController extends AdminBaseController {
	
    /**
     * Default constructor
     */
    public WithdrawCashController() {
    }
    
    /**
     * 获取提现列表
     * @param token(必填)
     * @return 成功：{error:0,totalMoney:总提现金额, data:[{id:id, customerName:会员姓名, created_at:申请日期},...]} 失败：{error:>0, errmsg:错误信息}
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
    	String mobilePhone = getPara("mobilePhone");
    	String email = getPara("email");
    	String aplipayAccount = getPara("aplipayAccount");
    	String weixinAccount = getPara("weixinAccount");
    	
    	Integer status = getParaToIntegerDefault("status");
    	String startTime = getParaToDateTimeDefault("startTime");
    	String endTime = getParaToDateTimeDefault("endTime");
    	
    	BigDecimal moneyMoreThan = getParaToDecimalDefault("moneyMoreThan");
    	BigDecimal moneyLessThan = getParaToDecimalDefault("moneyLessThan");
    	Integer accountType = getParaToIntegerDefault("accountType");
    	
    	Map<String, String> orderByMap = new HashMap<String, String>();
    	orderByMap.put("a.created_at", "desc");
    	
    	List<Record> data = WithDraw.findWithDrawItems(offset, length, null, customerName, mobilePhone, email, accountType, aplipayAccount, weixinAccount, status, null, null, null, null, startTime, endTime, null, null, orderByMap, moneyMoreThan, moneyLessThan);
    	int total = WithDraw.countWithDrawItems(null, customerName, mobilePhone, email, accountType, aplipayAccount, weixinAccount, status, null, null, null, null, startTime, endTime, null, null, moneyMoreThan, moneyLessThan);
    	List<Record> list = WithDraw.findWithDrawItems(null, customerName, mobilePhone, email, accountType, aplipayAccount, weixinAccount, status, null, null, null, null, startTime, endTime, null, null, orderByMap, moneyMoreThan, moneyLessThan);
    	Record statistics = WithDraw.calculateWithDraw(list);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", data);
    	jsonObject.put("totalMoney", statistics.getDouble("totalMoney"));
    	renderJson(jsonObject);
    }
    
    /**
     * 获取一条记录
     * @param token(必填)
     * @param id
     * @return 成功：{error:0, data:{id:id, customerName:会员姓名, created_at:申请日期}} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Record model = WithDraw.get(id);
    	
		jsonObject.put("data", model);
		renderJson(jsonObject);
		
    }
    
    /**
     * 处理提现  0提交申请，1通过申请，2拒绝申请，3已转账
     * @param token(必填)
     * @param id(必填)
     * @param status 状态(必填)
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "status"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int status = getParaToInt("status");
    	String note = getPara("note");
    	
    	ServiceCode code = WithDraw.auditWithDraw(id, status, note);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "修改失败");
    	}
    	
		renderJson(jsonObject);
    }

}