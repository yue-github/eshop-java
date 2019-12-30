package com.eshop.coupon.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.coupon.CouponProductService;
import com.eshop.model.CouponProduct;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.ext.test.ControllerTestCase;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class CouponProductServiceTest extends ControllerTestCase<TestProjectConfig> {
	

	/**
     * 批量查询优惠券适用品类
     * @param offset
     * @param count
     * @param couponId
     * @param type
     * @param name
     * @return
     */
	@Test
	public void testFindCouponProductItems() {
		List<Record> count = CouponProductService.findCouponProductItems(0, 2, null, null, null, "会陈皮玻璃瓶");
		System.out.println(count);
		boolean flag = count != null;
		assertEquals(true, flag);
	}

    /**
     * 批量查询优惠券适用品类的总数量
     * @param couponId
     * @param type
     * @param name
     */
	@Test
	public void testCountCouponProductItems() {
		int count = CouponProductService.countCouponProductItems(50, 2, 222, "玻璃瓶250g蕴月");
		boolean flag = count > 0;
		assertEquals(true, flag);
	}

    /**
     * 查看优惠券适用品类详情
     * @param id 
     */
	@Test
	public void testGetCouponProduct() {
		CouponProduct count = CouponProductService.getCouponProduct(54);
		boolean flag = count != null;
		assertEquals(true, flag);
	}
    /**
     * 创建优惠券适用品类
     * @param model
     */
	@Test
	public void testCreateCouponProduct() {
		CouponProduct model = new CouponProduct();
		model.setObjectId(1);
		model.setType(1);
		model.setCouponId(50);
		ServiceCode count = CouponProductService.createCouponProduct(model);
		boolean flag = count != null;
		assertEquals(true, flag);
	}


    /**
     * 修改优惠券使用品类
     * @param model 
     */
	@Test
	public void testUpdateCouponProduct() {
		CouponProduct model = new CouponProduct();
		model.setId(54);
		model.setObjectId(169);
		model.setType(2);
		model.setCouponId(50);
		ServiceCode count = CouponProductService.updateCouponProduct(model);
		boolean flag = count != null;
		assertEquals(true, flag);
	}

    /**
     * 删除优惠券适用品类
     * @param id 
     */
	@Test
	public void testDeleteCouponProduct() {
		ServiceCode count = CouponProductService.deleteCouponProduct(53);
		boolean flag = count != null;
		assertEquals(true, flag);
	}
	
	@Test
	public void test() {
		Page<Product> page = CouponProductService.getProductsNoThisCoupon(1, 2, 51, 1, "");
		System.out.println(page.getList());
	}
	
	@Test
	public void testBatchCreate() {
		String params = "?type=2&couponId=50&objects=['1', '2', '3']";
		String url = "/pc/couponPrduct/batchCreateCouponProduct"+params;
		String msg = use(url).invoke();
		System.out.println("ss:"+msg);
	}

}
