package com.eshop.invoice.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.invoice.PlainInvoiceService;
import com.eshop.model.PlainInvoice;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class PlainInvoiceServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private PlainInvoiceService plainInvoiceService = new PlainInvoiceService();
	
	/**
	 * 创建普通发票
	 * @param model
	 */
	@Test
	public void testCreate() {
		PlainInvoice model = new PlainInvoice();
		model.set("2", "5");
		ServiceCode count = PlainInvoiceService.create(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 修改普通发票
	 * @param model
	 */
	@Test
	public void testUpdate() {
		PlainInvoice model = new PlainInvoice();
		model.set("2", "5");
		ServiceCode count = PlainInvoiceService.create(model);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 查看普通发票详情
	 * @param id
	 */
	@Test
	public void testGet() {
		PlainInvoice count = PlainInvoiceService.get(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 批量查询普通发票记录
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 */
	@Test
	public void testFindPlainInvoiceItemsIntegerStringInteger() {
		List<Record> count = PlainInvoiceService.findPlainInvoiceItems(3, "3", 4);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 批量查询普通发票记录
	 * @param offset
	 * @param count
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 */
	@Test
	public void testFindPlainInvoiceItemsIntIntIntegerStringInteger() {
		List<Record> count = PlainInvoiceService.findPlainInvoiceItems(10, 5, 4, "21", 3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 批量查询普通发票的总数量
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 */
	@Test
	public void testCountPlainInvoiceItems() {
		int count = PlainInvoiceService.countPlainInvoiceItems(2, "sas", 1);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}
	
	/**
	 * 组装sql语句
	 * @param customerId
	 * @param invoiceHead
	 * @param type
	 */
	@Test
	public void testFindPlainInvoiceItemsSql() {
		String count = PlainInvoiceService.findPlainInvoiceItemsSql(3, "11", 1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
	
	/**
	 * 删除一条普通发票记录
	 * @param id
	 */
	@Test
	public void testDelete() {
		ServiceCode count = PlainInvoiceService.delete(4);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
