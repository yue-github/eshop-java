package com.eshop.wallet.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.BankCard;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.wallet.Card;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class CardTest extends ControllerTestCase<TestProjectConfig> {
	
	private Card card = new Card();
	
	/**
     * 创建银行卡
     * @param model 银行卡信息
     * @return 返回码
     */
	@Test
	public void testCreateCard() {
		BankCard model = new BankCard();
		model.set("2", "5");
		ServiceCode code = Card.createCard(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

    /**
     * 修改银行卡信息
     * @param model 银行卡信息
     * @param code 返回码
     */
	@Test
	public void testUpdateCard() {
		BankCard model = new BankCard();
		model.set("2", "5");
		ServiceCode code = card.updateCard(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

    /**
     * 删除银行
     * @param id 银行卡id
     * @return 返回码
     */
	@Test
	public void testDeleteCard() {
		ServiceCode count = Card.deleteCard(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
    /**
     * 用id查询银行信息
     * @param id 银行卡id
     * @return 银行卡信息
     */
	@Test
	public void testGetCard() {
		BankCard count = Card.getCard(4);
		boolean flag = count != null;
		assertEquals(false, flag);
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
     */
	@Test
	public void testFindCardItems() {
		List<Record> count = Card.findCardItems(10, 5, 3, "9", "jiang", "j", "q", "2");
		boolean flag = count != null;
		assertEquals(true, flag);
	}
    
    /**
     * 批量查询银行卡的记录总条数
     * @param customerId
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param contactNumber
     */
	@Test
	public void testCountCardItems() {
		int count = Card.countCardItems(3, "ji", "an", "gq", "ua", "n2");
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

}
