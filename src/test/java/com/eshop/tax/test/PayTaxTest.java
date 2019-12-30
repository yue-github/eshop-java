package com.eshop.tax.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.PayRate;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.tax.PayTax;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class PayTaxTest extends ControllerTestCase<TestProjectConfig> {
	
	private PayTax payTax = new PayTax();


	/**
     * 获取某一条
     * @param id
     */
	@Test
	public void testGet() {
		PayRate count = PayTax.get(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 批量查询支付手续费记录
     * @param offset
     * @param count
     * @param payType
     * @param enable
     */
	@Test
	public void testFindPayRateItems() {
		List<Record> count = PayTax.findPayRateItems(10, 5, 3, 4);
		boolean flag = count != null;
		assertEquals(true, flag);
	}
    
    /**
     * 批量查询支付手续费记录的总数量
     * @param payType
     * @param enable
     */
	@Test
	public void testCountFindPayRateItems() {
		int count = PayTax.countFindPayRateItems(4, 2);
		boolean flag = count > 0;
		assertEquals(true, flag);
	}
	   
    /**
     * 创建
     * @param model
     */
	@Test
	public void testCreate() {
		PayRate model = new PayRate();
		model.set("2", "5");
		ServiceCode code = PayTax.create(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
    
    /**
     * 修改
     * @param model
     */
	@Test
	public void testUpdate() {
		PayRate model = new PayRate();
		model.set("2", "5");
		ServiceCode code = PayTax.update(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
	   
    /**
     * 删除
     * @param id
     */
	@Test
	public void testDelete() {
		ServiceCode count = PayTax.delete(2);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
