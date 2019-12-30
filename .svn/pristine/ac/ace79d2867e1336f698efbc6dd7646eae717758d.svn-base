package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.logistics.CustomerAddress;
import com.eshop.model.Address;
import com.eshop.model.Customer;
import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;

/**
 * 我的地址控制器
 *   @author TangYiFeng
 */
public class AddressController extends PcBaseController {
	
    
    /**
     * Default constructor
     */
    public AddressController() {
    	
    }

    /**
     * 我的收货地址，默认地址排序第一
     * @param token 帐户访问口令（必填）
     * @param offset
     * @param length
     * @return 成功：{error: 0, addresses: [{id: 地址id, contacts: 姓名, phone: 手机号, isDefault: 是否默认地址(true, false), province: 省, city: 市, district: 区, detailedAddress: 详细地址}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	Integer offset = getParaToInt("offset");
    	Integer length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Address> address = CustomerAddress.findAddressesItems(offset, length,customerId, null, null);
    	List<Address> total = CustomerAddress.findAddressesItems(customerId, null, null);
    	jsonObject.put("addresses",address);
    	jsonObject.put("totalRow", total.size());
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	renderJson(jsonObject);
    }
    
    /**
     * 添加地址
     * @param token 帐户访问口令（必填）
     * @param contacts 姓名（必填）
     * @param phone 手机号（必填）
     * @param provinceId 省id（必填）
     * @param cityId 市id（必填）
     * @param districtId 区id（必填）
     * @param detailedAddress 详细地址（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void save() {
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	if(!this.validateRequiredString("contacts")) {
    		return;
    	}
    	String contacts = getPara("contacts");
    	
    	if(!this.validateRequiredString("phone")) {
    		return;
    	}
    	String phone = getPara("phone");
    	if(!this.validateRequiredString("provinceId")) {
    		return;
    	}
    	int provinceId = getParaToInt("provinceId");
    	if(!this.validateRequiredString("cityId")) {
    		return;
    	}
    	int cityId = getParaToInt("cityId");
    	if(!this.validateRequiredString("districtId")) {
    		return;
    	}
    	int districtId = getParaToInt("districtId");
    	if(!this.validateRequiredString("detailedAddress")) {
    		return;
    	}
    	String detailedAddress = getPara("detailedAddress");
    	
    	Address address = new Address();
    	address.setCustomerId(customerId);
    	address.setContacts(contacts);
    	address.setPhone(phone);
    	address.setProvinceId(provinceId);
    	address.setCityId(cityId);
    	address.setDistrictId(districtId);
    	address.setDetailedAddress(detailedAddress);
    	address.setCreatedAt(new Date());
    	address.setUpdatedAt(new Date());
    	if(CustomerAddress.createAddress(address) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createAddress createAddress failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除地址
     * @param token 帐户访问口令（必填）
     * @param addressId 地址addressId（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void remove() {
    	if(!this.validateRequiredString("addressId")) {
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
    @Before(CustomerPcAuthInterceptor.class)
    public void setDefault() {
    	if(!this.validateRequiredString("addressId")) {
    		return;
    	}
    	int addressId = getParaToInt("addressId");
    	
    	if(CustomerAddress.setDefaultAddress(addressId) != ServiceCode.Success) {
    		jsonObject.put("error", 1);
    		jsonObject.put("errmsg", "设置地址错误");
        	renderJson(jsonObject);
    	}
    	
    	jsonObject.put("error", 0);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有省
     * @return 成功：{error: 0, provinces: [{id: 省id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void provinces() {
    	List<SProvince> provinces = CustomerAddress.getAllProvince();
    	
    	jsonObject.put("error", 0);
    	jsonObject.put("provinces", provinces);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个省的所有市
     * @param provinceId 省id（必填）
     * @return 成功：{error: 0, cities: [{id: 市id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void cities() {
    	int provinceId = getParaToInt("provinceId");
    	List<SCity> citys = CustomerAddress.getCityByProvinceId(provinceId);
    	jsonObject.put("error", 0);
    	jsonObject.put("cities", citys);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个市的所有区
     * @param cityId 市id（必填）
     * @return 成功：{error: 0, districts: [{id: 区id, name: 姓名}；失败：{error: >0, errmsg: 错误信息}
     */
    public void districts() {
    	int cityId = getParaToInt("cityId");
    	List<SDistrict> districts = CustomerAddress.getDistrictByCityId(cityId);
    	jsonObject.put("error", 0);
    	jsonObject.put("districts", districts);
    	renderJson(jsonObject);
    }
}