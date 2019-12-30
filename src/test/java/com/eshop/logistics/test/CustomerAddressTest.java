package com.eshop.logistics.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.logistics.CustomerAddress;
import com.eshop.model.Address;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;

public class CustomerAddressTest extends ControllerTestCase<TestProjectConfig> {
	
	private CustomerAddress customerAddress = new CustomerAddress();

	/**
     * 设置默认地址
     */
	@Test
	public void testSetDefaultAddress() {
		ServiceCode count = CustomerAddress.setDefaultAddress(1);
		boolean flag = count != null;
		assertEquals(false, flag);
	}
    
    /**
     * 创建会员地址
     * 算法：如果是第一次创建地址，则设置该地址为默认地址
     * @param address 地址信息
     * @return 返回码
     */
	@Test
	public void testCreateAddress() {
		Address model = new Address();
		model.set("2", "5");
		ServiceCode code = CustomerAddress.createAddress(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
    
    /**
     * 修改会员地址
     * @param address 地址信息
     * @return 返回码
     */
	@Test
	public void testUpdateAddress() {
		Address model = new Address();
		model.set("2", "5");
		ServiceCode code = CustomerAddress.updateAddress(model);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}
    
    /**
     * 删除会员地址
     * @param customer_id 会员id
     * @param addressId 地址id
     * @return
     */
	@Test
	public void testDeleteAddress() {
		ServiceCode count = CustomerAddress.deleteAddress(1);
		boolean flag = count != null;
		assertEquals(true, flag);
	}
    
    /**
     * 查看地址
     * @param addressId 
     * @return
     */
	@Test
	public void testGetAddress() {
		Address count = CustomerAddress.getAddress(1);
		boolean flag = count != null;
		assertEquals(true, flag);
	}
    
    /**
     * 批量查询地址
     * @param customerId 会员id
     * @return 地址数组
     */
	@Test
	public void testFindAddressesItems() {
		List<Address> count = CustomerAddress.findAddressesItems(10, 2, 2, "34", "12");
		boolean flag = count != null;
		assertEquals(true, flag);
	}

    /**
     * 批量查询地址的总数量
     * @param customerId
     * @param contacts
     * @param phone
     * @return
     */
	@Test
	public void testCountAddressesItems() {
		int count = CustomerAddress.countAddressesItems(2, "2", "14718201120");
		boolean flag = count > 0;
		assertEquals(true, flag);
	}
	
    /**
     * 组装sql语句
     * @param customerId
     * @param contacts
     * @param phone
     */
	@Test
	public void testFindAddressesItemsSql() {
		String count = CustomerAddress.findAddressesItemsSql(2, "33", "14545525555");
		boolean flag = count != null;
		assertEquals(true, flag);
	}

}
