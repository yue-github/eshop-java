package com.eshop.wallet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eshop.helper.MathHelper;
import com.eshop.model.Wallet;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class Recharge {
	
	/**
     * 充值
     * @param model
     * @return
     */
    public static ServiceCode recharge(Wallet model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	if (!model.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 查看充值详情
     * @param id
     * @return
     */
    public static Record get(int id) {
    	String sql = "select a.*, b.name as customerName, b.mobilePhone as customerPhone from wallet as a" +
    			" left join customer as b on a.customer_id = b.id" +
    			" where a.id = " + id;
    	
    	return Db.findFirst(sql);
    }
    
    /**
     * 批量查询充值记录
     * @param offset
     * @param count
     * @param customerId
     * @param event
     * @param startCreated
     * @param endCreated
     * @param startFinishTime
     * @param endFinishTime
     * @param tradeNo
     * @param isPaySuccess
     * @param transactionId
     * @param source
     * @return
     */
    public static List<Record> findRechargeItems(int offset, int count, Integer customerId, Integer event, 
    		String startCreated, String endCreated, String startFinishTime, String endFinishTime, 
    		String tradeNo, Integer isPaySuccess, String transactionId, Integer source, 
    		String customerName, String customerPhone, Map<String, String> orderByMap){
    	
    	List<Record> list = findRechargeItems(offset, count, customerId, event, startCreated, endCreated, startFinishTime, 
    			endFinishTime, tradeNo, isPaySuccess, transactionId, source, customerName, customerPhone, 
    			orderByMap, null, null);
    	
    	return list;
    }
    
    public static List<Record> findRechargeItems(int offset, int count, Integer customerId, 
    		Map<String, String> orderByMap){
    	
    	int isPaySuccess = 1;
    	List<Record> list = findRechargeItems(offset, count, customerId, null, null, null, null, 
    			null, null, isPaySuccess, null, null, null, null, 
    			orderByMap, null, null);
    	
    	return list;
    }
    
    public static int countRechargeItems(Integer customerId){
    	int isPaySuccess = 1;
    	int total = countRechargeItems(customerId, null, null, null, null, null, null, isPaySuccess, 
    			null, null, null, null, null, null);
    	return total;
    }
    
    public static List<Record> findRechargeItems(int offset, int count, Integer customerId, Integer event, 
    		String startCreated, String endCreated, String startFinishTime, String endFinishTime, 
    		String tradeNo, Integer isPaySuccess, String transactionId, Integer source, 
    		String customerName, String customerPhone, Map<String, String> orderByMap,
    		BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = findRechargeItemsSql(customerId, event, startCreated, endCreated, startFinishTime, 
    			endFinishTime, tradeNo, isPaySuccess, transactionId, source, customerName, customerPhone,
    			orderByMap, moneyMoreThan, moneyLessThan);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	return Db.find(sql);
    }
    
    /**
     * 批量查询充值记录
     * @param customerId
     * @param event
     * @param startCreated
     * @param endCreated
     * @param startFinishTime
     * @param endFinishTime
     * @param tradeNo
     * @param isPaySuccess
     * @param transactionId
     * @param source
     * @param customerName
     * @param customerPhone
     * @param orderByMap
     * @return
     */
    public static List<Record> findRechargeItems(Integer customerId, Integer event, 
    		String startCreated, String endCreated, String startFinishTime, String endFinishTime, 
    		String tradeNo, Integer isPaySuccess, String transactionId, Integer source, 
    		String customerName, String customerPhone, Map<String, String> orderByMap,
    		BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = findRechargeItemsSql(customerId, event, startCreated, endCreated, startFinishTime, 
    			endFinishTime, tradeNo, isPaySuccess, transactionId, source, customerName, customerPhone,
    			orderByMap, moneyMoreThan, moneyLessThan);
    	
    	return Db.find(sql);
    }
    
    /**
     * 批量查询充值记录的总数量
     * @param customerId
     * @param event
     * @param startCreated
     * @param endCreated
     * @param startFinishTime
     * @param endFinishTime
     * @param tradeNo
     * @param isPaySuccess
     * @param transactionId
     * @param source
     * @return
     */
    public static int countRechargeItems(Integer customerId, Integer event, String startCreated, 
    		String endCreated, String startFinishTime, String endFinishTime, String tradeNo, 
    		Integer isPaySuccess, String transactionId, Integer source, String customerName, 
    		String customerPhone, BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = findRechargeItemsSql(customerId, event, startCreated, endCreated, startFinishTime, 
    			endFinishTime, tradeNo, isPaySuccess, transactionId, source, customerName, customerPhone,
    			null, moneyMoreThan, moneyLessThan);
    	
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param event
     * @param startCreated
     * @param endCreated
     * @param startFinishTime
     * @param endFinishTime
     * @param tradeNo
     * @param isPaySuccess
     * @param transactionId
     * @param source
     * @return
     */
    private static String findRechargeItemsSql(Integer customerId, Integer event, String startCreated, 
    		String endCreated, String startFinishTime, String endFinishTime, String tradeNo, 
    		Integer isPaySuccess, String transactionId, Integer source, String customerName, 
    		String customerPhone, Map<String, String> orderByMap,
    		BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = "select a.*, b.name as customerName, b.mobilePhone as customerPhone from wallet as a" +
    			" left join customer as b on a.customer_id = b.id" +
    			" where a.event in (3, 4, 6)" + 
    			" and a.transactionId is not null";
    	
    	if (customerId != null)
    		sql += " and a.customer_id = " + customerId;
    	if (event != null)
    		sql += " and a.event = " + event;
    	if (startCreated != null && !startCreated.equals(""))
    		sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startCreated + "'";
    	if (endCreated != null && !endCreated.equals(""))
    		sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endCreated + "'";
    	if (startFinishTime != null && !startFinishTime.equals(""))
    		sql += " and DATE_FORMAT(a.finishTime, '%Y-%m-%d') >= '" + startFinishTime + "'";
    	if (endFinishTime != null && !endFinishTime.equals(""))
    		sql += " and DATE_FORMAT(a.finishTime, '%Y-%m-%d') <= '" + endFinishTime + "'";
    	if (tradeNo != null && !tradeNo.equals(""))
    		sql += " and a.tradeNo like '%" + tradeNo + "%'";
    	if (isPaySuccess != null)
    		sql += " and a.isPaySuccess = " + isPaySuccess;
    	if (transactionId != null && !transactionId.equals(""))
    		sql += " and a.transactionId like '%" + transactionId + "%'";
    	if (source != null)
    		sql += " and a.source = " + source;
    	if (customerName != null && !customerName.equals(""))
    		sql += " and b.name like '%" + customerName + "%'";
    	if (customerPhone != null && !customerPhone.equals(""))
    		sql += " and b.mobilePhone like '%" + customerPhone + "%'";
    	if (moneyMoreThan != null && !moneyMoreThan.equals(""))
    		sql += " and a.money >=" + moneyMoreThan;
    	if (moneyLessThan != null && !moneyLessThan.equals(""))
    		sql += " and a.money <=" + moneyLessThan;
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
    
    /**
     * 计算充值金额
     * @param list
     * @return
     */
    public static Record calculateRechargeItems(List<Record> list) {
    	Record result = new Record();
    	double totalMoney = 0;
    	
    	for (Record item : list) {
    		totalMoney += item.getBigDecimal("money").doubleValue();
    	}
    	
    	result.set("totalMoney", totalMoney);
    	return result;
    }
    
    /**
     * 获取某条充值记录
     * @param id
     * @return
     */
    public Wallet getRecharge(int id) {
    	return Wallet.dao.findById(id);
    }
    
    /**
     * 我的钱包余额
     * 算法：支付宝充值金额+微信充值金额+银联充值金额-提交订单使用余额-提现金额
     * 2提现 , 3支付宝充值 , 4微信充值，5提交订单使用余额，6银联充值
     * @param customerId 客户/员工id
     * @return result 余额
     */
    public static double myBalance(int customerId) {
    	List<Wallet> models = Wallet.dao.find("select * from wallet where customer_id = ?", customerId);
    	double result = 0;
    	
    	for (Wallet item : models) {
    		int event = item.getEvent();
    		int isPaySuccess = item.getIsPaySuccess();
    		double money = item.getMoney().doubleValue();
    		
    		switch (event) {
    		case 2: //提现
    			result += Math.abs(money) * -1;
    			break;
    		case 3: //支付宝充值
    			if (isPaySuccess == 1)
    				result += Math.abs(money);
    			break;
    		case 4: //微信充值
    			if (isPaySuccess == 1)
    				result += Math.abs(money);
    			break;
    		case 5: //提交订单使用余额
    			result += Math.abs(money) * -1;
    			break;
    		case 6: //银联充值
    			if (isPaySuccess == 1)
    				result += Math.abs(money);
    			break;
    		}
    	}
    	
    	return MathHelper.cutDecimal(result);
    }
	
}
