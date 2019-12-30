package com.eshop.invoice.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.invoice.VatInvoiceService;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Record;

public class VatInvoiceServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private  VatInvoiceService  vatInvoiceService = new  VatInvoiceService();
	
	/**
	 * 查看增值税发票详情
	 * @param customerId
	 */
	@Test
	public void testGet() {
		Record count = VatInvoiceService.get(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
