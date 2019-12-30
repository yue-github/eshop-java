package com.eshop.invoice.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.invoice.EletronicInvoiceService;
import com.eshop.model.EletronicInvoice;
import com.jfinal.ext.test.ControllerTestCase;

public class EletronicInvoiceServiceTest extends ControllerTestCase<TestProjectConfig> {
	
	private EletronicInvoiceService eletronicInvoiceService = new EletronicInvoiceService();

	/**
	 * 查看电子发票详情
	 * @param id
	 * @return
	 */
	@Test
	public void testGetEletronicInvoiceI() {
		EletronicInvoice count = EletronicInvoiceService.getEletronicInvoice(3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

}
