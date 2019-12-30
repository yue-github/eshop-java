package com.eshop.auditprice.test;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.eshop.config.TestProjectConfig;
import com.eshop.helper.DateHelper;
import com.eshop.model.ProductTrip;
import com.jfinal.ext.test.ControllerTestCase;

public class AuditPriceServiceTest extends ControllerTestCase<TestProjectConfig> {

	/*@Test
	public void testGet() {
		Record record = Db.findFirst("select max(id) as maxId from audit_price");
		System.out.println(record);
		if (record.get("maxId") == null) {
			System.out.println(0);
		} else {
			System.out.println(record.get("maxId"));
		}
		assertEquals(0, 0);
	}*/
	@Test
	public void test() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateS1 = "2018-05-01";
		String dateS2 = "2018-05-07";
		Date date1 = format.parse(dateS1);
		Date date2 = format.parse(dateS2);
		Calendar calendar = Calendar.getInstance();
		String dayAndDay = "";
		double price = 0;
		String sql = "select * from product_trip where product_id=? and DATE_FORMAT(start_at,'%Y-%m-%d')<=? and DATE_FORMAT(end_at,'%Y-%m-%d')>=?";
		//List<ProductTrip> trips = ProductTrip.dao.find(sql, 776, a);
		int day = DateHelper.differentDays(date1, date2);
		for(int i = 0; i<day; i ++) {
			if(i == 0) {
				dayAndDay = dateS1;
			}else {
				dayAndDay = DateHelper.addDay(dayAndDay, 1);
			}
			List<ProductTrip> date = ProductTrip.dao.find(sql, 776, dayAndDay, dayAndDay);
			price += date.get(0).getPrice().doubleValue(); 
			System.out.println("一共需要+"+ price);
		}
	}
	 
}
