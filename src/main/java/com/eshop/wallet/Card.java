package com.eshop.wallet;

import java.util.Date;
import java.util.List;

import com.eshop.model.BankCard;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class Card {
	
	/**
     * 创建银行卡
     * @param model 银行卡信息
     * @return 返回码
     */
    public static ServiceCode createCard(BankCard model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	if(!model.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }

    /**
     * 修改银行卡信息
     * @param model 银行卡信息
     * @param code 返回码
     */
    public ServiceCode updateCard(BankCard model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	model.setUpdatedAt(new Date());
    	
    	if (!model.update()) {
    		return ServiceCode.Failed;
    	}
    	
     	return ServiceCode.Success;
    }

    /**
     * 删除银行
     * @param id 银行卡id
     * @return 返回码
     */
    public static ServiceCode deleteCard(int id) {
    	if(BankCard.dao.deleteById(id)){
    		return ServiceCode.Success;
    	}
    	
    	return ServiceCode.Failed;
    }

    /**
     * 用id查询银行信息
     * @param id 银行卡id
     * @return 银行卡信息
     */
    public static BankCard getCard(int id) {
    	return BankCard.dao.findById(id);
    }

    /**
     * 批量查询银行卡
     * @param offset
     * @param count
     * @param customerId
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param contactNumber
     * @return
     */
    public static List<Record> findCardItems(int offset, int count, Integer customerId, String accountNumber, String accountName, String bankName, String bankBranch, String contactNumber) {
    	String sql = findCardItemsSql(customerId, accountNumber, accountName, bankName, bankBranch, contactNumber);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询银行卡
     * @param customerId
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param contactNumber
     * @return
     */
    public static List<Record> findCardItems(Integer customerId, String accountNumber, String accountName, String bankName, String bankBranch, String contactNumber) {
    	String sql = findCardItemsSql(customerId, accountNumber, accountName, bankName, bankBranch, contactNumber);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询银行卡的记录总条数
     * @param customerId
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param contactNumber
     * @return
     */
    public static int countCardItems(Integer customerId, String accountNumber, String accountName, 
    		String bankName, String bankBranch, String contactNumber) {
    	
    	String sql = findCardItemsSql(customerId, accountNumber, accountName, bankName, 
    			bankBranch, contactNumber);
    	
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param contactNumber
     * @return
     */
    private static String findCardItemsSql(Integer customerId, String accountNumber, String accountName, String bankName, String bankBranch, String contactNumber) {
    	String sql = "select * from bank_card where id != 0";
    	if (customerId != 0) 
    		sql += " and customer_id = " + customerId;
    	if (accountNumber != null && !accountNumber.equals(""))
    		sql += " and accountNumber like '%" + accountNumber + "%'";
    	if (accountName != null && !accountName.equals(""))
    		sql += " and accountName like '%" + accountName + "%'";
    	if (bankName != null && !bankName.equals(""))
    		sql += " and bankName like '%" + bankName + "%'";
    	if (bankBranch != null && !bankBranch.equals(""))
    		sql += " and bankBranch like '%" + bankBranch + "%'";
    	if (contactNumber != null && !contactNumber.equals(""))
    		sql += " and contactNumber like '%" + contactNumber + "%'";
    	return sql;
    }
    
}
