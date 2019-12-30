package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.logistics.CustomerAddress;
import com.eshop.model.Address;
import com.eshop.model.Customer;
import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;

/**
 *   我的地址控制器
 *   @author TangYiFeng
 */
public class AddressesController extends WebappBaseController {

    /**
     * 我的收货地址列表，默认地址排序第一
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0, addresses: [{id: 地址id, contacts: 姓名, phone: 手机号, isDefault: 是否默认地址(true, false), province: 省, city: 市, district: 区, detailedAddress: 详细地址}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void many() {
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	Integer offset = getParaToInt("offset");
    	if(offset == null) {
    		offset = 0;
    	}
    	Integer length = getParaToInt("length");
    	List<Address> address = CustomerAddress.findAddressesItems(offset, length, customerId, null, null);
    	List<Address> total = CustomerAddress.findAddressesItems(customerId, null, null);
    	jsonObject.put("addresses",address);
    	jsonObject.put("totalRow", total.size());
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	renderJson(jsonObject);
    }
    
    /**
     * 删除地址
     * @param token 帐户访问口令（必填）
     * @param addressId（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void remove() {
    	String[] params = {"addressId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int addressId = getParaToInt("addressId");
    	
    	if(CustomerAddress.deleteAddress(addressId) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "deleteAddress deleteAddress failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 设置默认地址
     * @param token 帐户访问口令（必填）
     * @param addressId 地址addressId（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void setDefault() {
    	String[] params = {"addressId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int addressId = getParaToInt("addressId");
    	
    	if(CustomerAddress.setDefaultAddress(addressId) != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "设置默认地址失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 编辑地址
     * @param token 帐户访问口令（必填）
     * @param id 地址id
     * @param contacts 姓名（必填）
     * @param phone 手机号（必填）
     * @param province_id 省id（必填）
     * @param city_id 市id（必填）
     * @param distric_id 区id（必填）
     * @param detailedAddress 详细地址（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void updateAddress() {
    	String[] params = {"id", "province_id", "city_id", "district_id", "detailedAddress", 
    			"contacts", "phone"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int provinceId = getParaToInt("province_id");
    	int cityId = getParaToInt("city_id");
    	int districtId = getParaToInt("district_id");
    	String detailedAddress = getPara("detailedAddress");
    	String contacts = getPara("contacts");
    	String phone = getPara("phone");
    	
    	Address address = Address.dao.findById(id);
    	address.setContacts(contacts);
    	address.setPhone(phone);
    	address.setProvinceId(provinceId);
    	address.setCityId(cityId);
    	address.setDistrictId(districtId);
    	address.setDetailedAddress(detailedAddress);
    	address.setCreatedAt(new Date());
    	address.setUpdatedAt(new Date());
    	
    	if(CustomerAddress.updateAddress(address) != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "createAddress createAddress failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 添加地址
     * @param token 帐户访问口令（必填）
     * @param contacts 姓名（必填）
     * @param phone 手机号（必填）
     * @param province_id 省id（必填）
     * @param city_id 市id（必填）
     * @param district_id 区id（必填）
     * @param detailedAddress 详细地址（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void save() {
    	String[] params = {"province_id", "city_id", "district_id", "detailedAddress", 
    			"contacts", "phone"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int provinceId = getParaToInt("province_id");
    	int cityId = getParaToInt("city_id");
    	int districtId = getParaToInt("district_id");
    	String detailedAddress = getPara("detailedAddress");
    	String contacts = getPara("contacts");
    	String phone = getPara("phone");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Address address = new Address();
    	address.setContacts(contacts);
    	address.setPhone(phone);
    	address.setProvinceId(provinceId);
    	address.setCityId(cityId);
    	address.setDistrictId(districtId);
    	address.setDetailedAddress(detailedAddress);
    	address.setCustomerId(customerId);
    	address.setCreatedAt(new Date());
    	address.setUpdatedAt(new Date());
    	
    	if(CustomerAddress.createAddress(address) != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "createAddress createAddress failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有省
     * @return 成功：{error: 0, provinces: [{id: 省id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void provinces() {
    	List<SProvince> provinces = CustomerAddress.getAllProvince();
    	
    	jsonObject.put("provinces", provinces);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有市
     * @param provinceId 省id（必填）
     * @return 成功：{error: 0, cities: [{id: 市id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void cities() {
    	String[] params  = {"provinceId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int provinceId = getParaToInt("provinceId");
    	List<SCity> citys = CustomerAddress.getCityByProvinceId(provinceId);
    	
    	jsonObject.put("cities", citys);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有区
     * @param cityId 市id（必填）
     * @return 成功：{error: 0, districts: [{id: 区id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void districts() {
    	String[] params  = {"cityId"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int cityId = getParaToInt("cityId");
    	List<SDistrict> districts = CustomerAddress.getDistrictByCityId(cityId);
    	
    	jsonObject.put("districts", districts);
    	renderJson(jsonObject);
    }
}