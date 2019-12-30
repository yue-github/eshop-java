package com.eshop.model.dao.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.model.Property;
import com.jfinal.ext.test.ControllerTestCase;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;

public class ManagerDaoTest extends ControllerTestCase<TestProjectConfig> {
	
	private Manager managerDao = new Manager();
	
	/*@Test
	public void testFindCategoryItems() {
		List<Record> back = managerDao.findCategoryItems(10, 2, 4, "s");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetCategoryItemsCount() {
		int count = managerDao.getCategoryItemsCount(4, "2");
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

	@Test
	public void testGetCategory() {
		Category back = managerDao.getCategory(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}*/

	/*@Test
	public void testCreateCategory() {
		Category model = new Category();
		model.setName("男装");
		model.setParentId(252);
		ServiceCode code = ManagerDao.createCategory(model, "c.png");
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testUpdateCategory() {
		Category model = new Category();
		model.set("2", "5");
		ServiceCode code = managerDao.updateCategory(model, "");
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}*/

	/*@Test
	public void testDeleteCategory() {
		ServiceCode code = ManagerDao.deleteCategory(252);
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testFindPropertyItems() {
		List<Record> back = managerDao.findPropertyItems(5, 4, 5, "2", 5);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetPropertyItemsCount() {
		int count = managerDao.getPropertyItemsCount(1, "d", 2);
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

	@Test
	public void testGetProperty() {
		Property back = managerDao.getProperty(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}*/

	/*@Test
	public void testCreateProperty() {
		Property model = new Property();
		model.setName("颜色");
		model.setCategoryId(252);
		ServiceCode code = ManagerDao.createProperty(model);
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testUpdateProperty() {
		Property model = new Property();
		model.set("2", "5");
		ServiceCode code = managerDao.updateProperty(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}*/

	@Test
	public void testDeleteProperty() {
		Property property = Property.dao.findFirst("select * from property");
		ServiceCode code = Manager.deleteProperty(property.getId());
		assertEquals(ServiceCode.Success, code);
	}

	/*@Test
	public void testFindCustomerItems() {
		List<Record> back = managerDao.findCustomerItems(2, 3, "33", 2, "1525254624", "asd", "asd");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetCustomerItemsCount() {
		int count = managerDao.getCustomerItemsCount("as", 4, "22", "33", "44");
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

	@Test
	public void testForbiddenCustomer() {
		ServiceCode code = managerDao.forbiddenCustomer(1);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testActivate() {
		ServiceCode code = managerDao.activate(1);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testAuditShop() {
		ServiceCode code = managerDao.auditShop(1 , 5);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testForbiddenShop() {
		ServiceCode code = managerDao.forbiddenShop(1);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testFindShopItems() {
		List<Record> back = managerDao.findShopItems(5, 7, "asss", "sds", "223", 3);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetShopItemsCount() {
		int count = managerDao.getShopItemsCount(44, 22, "asd", "22", "dd", 2);
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

	@Test
	public void testGetShop() {
		Shop back = managerDao.getShop(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testCreateSupplier() {
		Supplier model = new Supplier();
		model.set("2", "5");
		ServiceCode code = managerDao.createSupplier(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testGetSupplier() {
		Supplier back = managerDao.getSupplier(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testUpdateSupplier() {
		Supplier model = new Supplier();
		model.set("2", "5");
		ServiceCode code = managerDao.updateSupplier(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testFindSupplierItem() {
		List<Record> back = managerDao.findSupplierItems(2, 1, "22ss", "5152421512", "23132", 4, "5245");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetSupplierItemCount() {
		int count = managerDao.getSupplierItemCount("asd", "23", "23", 5, "23");
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

	@Test
	public void testDeleteSupplier() {
		ServiceCode code = managerDao.deleteSupplier(1);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testFindPropValueItems() {
		List<Record> back = managerDao.findPropValueItems(2, 3, "sd", 2);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetPropValueItemsCount() {
		int count = managerDao.getPropValueItemsCount("asd", 2);
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

	@Test
	public void testGetPropValue() {
		PropertyValue back = managerDao.getPropValue(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testCreatePropValue() {
		PropertyValue model = new PropertyValue();
		model.set("2", "5");
		ServiceCode code = managerDao.createPropValue(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testUpdatePropValue() {
		PropertyValue model = new PropertyValue();
		model.set("2", "5");
		ServiceCode code = managerDao.updatePropValue(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testDeletePropValue() {
		ServiceCode code = managerDao.deletePropValue(1);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}*/
	
}
