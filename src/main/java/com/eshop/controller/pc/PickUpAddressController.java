package com.eshop.controller.pc;

import java.util.Date;
import java.util.List;

import com.eshop.helper.AddressHelper;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.PickupAddress;
import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.pickupaddress.PickUpAddressService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

public class PickUpAddressController extends PcBaseController {
	
	private PickUpAddressService service = new PickUpAddressService();
	
	@Before(CustomerPcAuthInterceptor.class)
	public void create() {
		String[] params = {"title", "province_id", "city_id", "district_id", "detail_address"};
		if (!this.validate(params)) {
			return;
		}
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int shopId = customer.getShopId();
		
		PickupAddress model = new PickupAddress();
		model.setTitle(getPara("title"));
		model.setDetailAddress(getPara("detail_address"));
		model.setCreatedAt(new Date());
		model.setShopId(shopId);
		model.setProvinceId(getParaToInt("province_id"));
		model.setCityId(getParaToInt("city_id"));
		model.setDistrictId(getParaToInt("district_id"));
		
		ServiceCode code = service.create(model);
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "创建失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "title", "province_id", "city_id", "district_id", "detail_address"};
		if (!this.validate(params)) {
			return;
		}
		
		PickupAddress model = service.get(getParaToInt("id"));
		model.setTitle(getPara("title"));
		model.setDetailAddress(getPara("detail_address"));
		model.setCreatedAt(new Date());
		model.setProvinceId(getParaToInt("province_id"));
		model.setCityId(getParaToInt("city_id"));
		model.setDistrictId(getParaToInt("district_id"));
		
		ServiceCode code = service.update(model);
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "修改失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		ServiceCode code = service.delete(getParaToInt("id"));
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "删除失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		PickupAddress model = service.get(getParaToInt("id"));
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void list() {
		String[] params = {"offset", "length"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String title = getPara("title");
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int shopId = customer.getShopId();
		
		List<Record> list = service.list(offset, length, shopId, title);
		int total = service.count(shopId, title);
		
		jsonObject.put("data", list);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void all() {
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		int shopId = customer.getShopId();
		
		String title = "";
		List<Record> list = service.all(shopId, title);
		int total = service.count(shopId, title);
		
		jsonObject.put("data", list);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void productAddresses() {
		String[] params = {"product_id"};
		if (!this.validate(params)) {
			return;
		}
		
		Integer productId = getParaToIntegerDefault("product_id");
		String title = getPara("title");
		List<Record> list = service.productAddresses(productId, title);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void createProductAddresses() {
		String[] params = {"product_id", "pickup_address_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int productId = getParaToInt("product_id");
		int pickupAddressId = getParaToInt("pickup_address_id");
		ServiceCode code = service.createProductAddresses(productId, pickupAddressId);
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "创建失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(CustomerPcAuthInterceptor.class)
	public void deleteProductAddresses() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		ServiceCode code = service.deleteProductAddresses(getParaToInt("id"));
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "删除失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	public void provinces() {
		List<SProvince> list = AddressHelper.provinces();
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	public void cities() {
		String[] params = {"province_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int provinceId = getParaToInt("province_id");
		List<SCity> list = AddressHelper.citiesByProvinceId(provinceId);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	public void districts() {
		String[] params = {"city_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int cityId = getParaToInt("city_id");
		List<SDistrict> list = AddressHelper.districtsByCityId(cityId);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	public void commonPickupAddresses() {
		String[] params = {"orders"};
		if (!this.validate(params)) {
			return;
		}
		
		String orders = getPara("orders");
		List<Record> list = service.getCommonPickupAddresses(orders);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
}
