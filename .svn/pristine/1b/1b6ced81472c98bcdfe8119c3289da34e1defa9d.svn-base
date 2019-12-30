package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.logistics.CustomerAddress;
import com.eshop.model.Address;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;

/**
 * 编辑地址控制器
 * @author TangYiFeng
 */
public class EditAddressController extends WebappBaseController {

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
	@Before(CustomerWebAppAuthInterceptor.class)
    public void update() {
		String[] params = {"id", "contacts", "phone", "provinceId", "cityId", "districtId", "detailedAddress"};
		if (!this.validate(params)) {
			return;
		}
		
    	int id = getParaToInt("id");
    	int provinceId = getParaToInt("provinceId");
    	int cityId = getParaToInt("cityId");
    	int districtId = getParaToInt("districtId");
    	String contacts = getPara("contacts");
    	String phone = getPara("phone");
    	String detailedAddress = getPara("detailedAddress");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	Address address = Address.dao.findById(id);
    	address.set("contacts", contacts);
    	address.set("phone", phone);
    	address.set("province_id", provinceId);
    	address.set("city_id", cityId);
    	address.set("district_id", districtId);
    	address.set("detailedAddress", detailedAddress);
    	address.set("customer_id", customerId);
    	
    	ServiceCode code = CustomerAddress.createAddress(address);
    	
    	if(code != ServiceCode.Success) {
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
	@Before(CustomerWebAppAuthInterceptor.class)
    public void get() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
    	
    	int id = getParaToInt("id");
    	Address model = CustomerAddress.getAddress(id);
    	
    	jsonObject.put("address", model);
    	renderJson(jsonObject);
    }

}