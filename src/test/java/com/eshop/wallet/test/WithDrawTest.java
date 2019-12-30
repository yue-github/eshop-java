package com.eshop.wallet.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.WithdrawCash;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.wallet.WithDraw;
import com.jfinal.ext.test.ControllerTestCase;

public class WithDrawTest extends ControllerTestCase<TestProjectConfig> {
	private WithDraw withDraw = new WithDraw();

    
    /**
     * 批量查询提现记录的总数量
     * @param customerId
     * @param customerName
     * @param mobilePhone
     * @param email
     * @param accountType
     * @param aplipayAccount
     * @param weixinAccount
     * @param status
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param startCreated
     * @param endCreated
     * @param startArriveTime
     * @param endArriveTime
     */
	/*@Test
	public void testCountWithDrawItems() {
		int count = withDraw.countWithDrawItems(6, "ss", "qq", "ec", 6, "ccd", "qw", 1, "ert", "vg", "aax", "gh", "aas", "ferer", "eet", "sadf", null, null);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}*/
	   
    /**
     * 申请提现（如果提现到银行卡，需要通过银行卡id把银行卡信息查询出来放到提现记录里）
     * @param model 提现记录
     * @param bankCardId 银行卡id
     * @return 状态码
     */
	@Test
	public void testApplyCash() {
		WithdrawCash model = new WithdrawCash();
		model.set("2", "5");
		ServiceCode code = withDraw.applyCash(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
	   
    /**
     * 处理提现  0提交申请，1通过申请，2拒绝申请，3已转账
     * @param id
     * @param status
     * @param note
     */
	@Test
	public void testAuditWithDraw() {
		ServiceCode count = WithDraw.auditWithDraw(3, 4, "33");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 拒绝提现申请
     * @param id 提现申请id
     * @param note 理由
     */
	@Test
	public void testRefuseWithDraw() {
		ServiceCode count = WithDraw.refuseWithDraw(2, "2");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 同意提现申请
     * @param id 提现申请id
     * @param note 理由
     * @return 状态码
     */
	@Test
	public void testPassWithDraw() {
		ServiceCode count = WithDraw.passWithDraw(3, "4");
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 确认已转帐
     * @param id
     * @return 状态码
     */
	@Test
	public void testFinishWithDraw() {
		ServiceCode count = WithDraw.finishWithDraw(56, "5454");
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
