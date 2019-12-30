package com.eshop.model.dao.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Customer;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Merchant;
import com.jfinal.ext.test.ControllerTestCase;

public class MerchantDaoTest extends ControllerTestCase<TestProjectConfig> {
	
	private Merchant merchantDao = new Merchant();
	private Manager manager = new Manager();

	/*@Test
	public void testGetShop() {
		Shop back = merchantDao.getShop(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testUpdateShop() {
		Shop model = new Shop();
		model.set("2", "5");
		ServiceCode code = merchantDao.updateShop(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}*/

	/*@Test
	public void testCreateProduct() {
		Shop shop = Shop.dao.findFirst("select * from shop");
		Product product = new Product();
		product.setName("产品A");
		product.setUnitCost(new BigDecimal(3));
		product.setSuggestedRetailUnitPrice(new BigDecimal(4));
		product.setOriginUnitPrice(new BigDecimal(5));
		product.setIsSale(1);
		product.setStoreAmount(100);
		product.setShopId(shop.getId());
		ServiceCode code = MerchantDao.createProduct(product, "", "['a.png']");
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testOnShelf() {
		ServiceCode code = merchantDao.onShelf(1);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testOffShelf() {
		ServiceCode code = merchantDao.offShelf(1);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testFindProductItems() {
		List<Record> back = merchantDao.findProductItems(3, 2, 1, "2", 3, 4, 5, "1", "1");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetProductItemsCount() {
		int count = merchantDao.getProductItemsCount(1, "2", 2, 3, 4, "2", "4");
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

	@Test
	public void testGetProduct() {
		Product back = merchantDao.getProduct(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testUpdateProduct() {
		
	}*/

	/*@Test
	public void testAuditRefund() {
		ServiceCode code = merchantDao.auditRefund(203, 2, "", "管理员", new BigDecimal(8), new BigDecimal(0));
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testRemit() {
		
	}*/

	/*@Test
	public void testAuditReturned() {
		ServiceCode code = merchantDao.auditReturned(81, 2, "", "管理员", new BigDecimal(8), new BigDecimal(0));
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testDispatch() {
		Order order = Order.dao.findFirst("select * from `order` where status = ?", BaseDao.PAYED);
		ServiceCode code = merchantDao.delivery(order.getId(), "顺丰", "shunfeng", "15456468746456476846");
		assertEquals(ServiceCode.Success, code);
	}*/

	/**
	 * 开店流程
	 */
	@Test
	public void testSetUpShop() {
		//提交开店申请
		Customer customer = Customer.dao.findFirst("select * from customer");
		Shop model = new Shop();
		model.setName("店铺A");
		model.setShopType(1);
		ServiceCode code = Merchant.setUpShop(model, customer.getId(), "a.png", "b.png", "c.png");
		assertEquals(ServiceCode.Success, code);
		//管理员审核店铺
		ServiceCode auditCode = Manager.auditShop(model.getId(), 1);
		assertEquals(ServiceCode.Success, auditCode);
	}

}
