package com.eshop.reconciliation.test;

import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.finance.SaleFinanceService;
import com.eshop.model.ProductOrder;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class SaleSummaryServiceTest extends ControllerTestCase<TestProjectConfig> {
	private SaleFinanceService sale = new SaleFinanceService();
	@Test
	public void test() {
		Page<ProductOrder> page = SaleFinanceService.getOrderSummaryList(1, 10, null, null, null, null, null);
		System.out.println(page.getList().size());
		System.out.println(page.getTotalRow());
	
	}
	@Test
	public void test1() {
		BigDecimal totalActualProductPrice = new BigDecimal(2.33);
		BigDecimal totalActualProductPrice1 = new BigDecimal(2.33);
		totalActualProductPrice = totalActualProductPrice.add(totalActualProductPrice1);
		System.out.println(totalActualProductPrice);
	}
	
	@Test
	public void test2() {
		List<ProductOrder> allOrderSummaryList = SaleFinanceService.getAllOrderSummaryList( null, null, null, null, null);
		Record total = SaleFinanceService.totalAllOrderSummaryList(allOrderSummaryList);
		System.out.println(total);
	}
}
