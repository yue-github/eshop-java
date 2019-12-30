package com.eshop.wallet.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Wallet;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.wallet.Recharge;
import com.jfinal.ext.test.ControllerTestCase;

public class RechargeTest extends ControllerTestCase<TestProjectConfig> {
	
	private Recharge recharge = new Recharge();

	
	/**
     * 充值
     * @param model
     */
	@Test
	public void testRecharge() {
		Wallet model = new Wallet();
		model.set("2", "5");
		ServiceCode code = Recharge.recharge(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
    
    /**
     * 获取某条充值记录
     * @param id
     */
	@Test
	public void testGetRecharge() {
		Wallet count = recharge.getRecharge(2);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 我的钱包余额
     * 算法：支付宝充值金额+微信充值金额+银联充值金额-提交订单使用余额-提现金额
     * 2提现 , 3支付宝充值 , 4微信充值，5提交订单使用余额，6银联充值
     * @param customerId 客户/员工id
     * @return result 余额
     */
	@Test
	public void testMyBalance() {
		double count = Recharge.myBalance(5);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
