package com.eshop.controller.pc;

import com.eshop.controller.admin.BaseController.ErrorCode;
import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.logistics.CustomerAddress;
import com.eshop.model.Address;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;

/**
 * 我的地址控制器
 *   @author TangYiFeng
 */
public class EditAddressController extends PcBaseController {
	
    /**
     * Default constructor
     */
    public EditAddressController() {
    }

    /**
     * 修改地址
     * @param token 帐户访问口令（必填）
     * @param id 地址（必填）
     * @param contacts 姓名（必填）
     * @param phone 手机号（必填）
     * @param provinceId 省id（必填）
     * @param cityId 市id（必填）
     * @param districtId 区id（必填）
     * @param detailedAddress 详细地址（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void editaddress() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
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
    	
    	Address address = Address.dao.findById(id);
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	if(!address.getCustomerId().equals(customer.getId())) {
    		 returnError(ErrorCode.Authority, "没有权限修改");
    		 return;
    	}
    	
    	address.set("contacts", contacts);
    	address.set("phone", phone);
    	address.set("province_id", provinceId);
    	address.set("city_id", cityId);
    	address.set("district_id", districtId);
    	address.set("detailedAddress", detailedAddress);
    	
    	if(CustomerAddress.updateAddress(address) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "update updateAddress failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取地址明细
     * @param token 帐户访问口令（必填）
     * @param id 地址id（必填）
     * @return 成功：{error: 0, address: {id: 地址id, contacts: 姓名, phone: 手机号, isDefault: 是否默认地址(true, false), province: 省, city: 市, district: 区, detailedAddress: 详细地址}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void get() {
    	if(!this.validateRequiredString("addressId")) {
    		return;
    	}
    	int id = getParaToInt("addressId");  
    	
    	Address address = CustomerAddress.getAddress(id);
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	if(!address.getCustomerId().equals(customer.getId())) {
    		 returnError(ErrorCode.Authority, "没有权限");
    		 return;
    	}
    	
    	jsonObject.put("address", address);
    	renderJson(jsonObject);
    }
}