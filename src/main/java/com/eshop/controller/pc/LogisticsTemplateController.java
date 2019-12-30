package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.logistics.CustomerAddress;
import com.eshop.logistics.Logistics;
import com.eshop.model.Customer;
import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 运费控制器
 * @author TangYiFeng
 */
public class LogisticsTemplateController extends PcBaseController {
	
	Logistics logistics;
	
    /**
     * Default constructor
     */
    public LogisticsTemplateController() {
    	logistics = new Logistics();
    }
    
    /**
     * 获取所有省和市
     * @param token
     * @return 成功：{error: 0, data: [{id:省id,name:省名称,cities:[{id:市id,name:市名称},...]},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getProvinceAndCity() {
    	List<Record> list = CustomerAddress.getProvinceAndCity();
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有省
     * @param token
     * @return 成功：{error: 0, provinces: [{id: 省id, name: 名称}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void provinces() {
    	List<SProvince> provinces = CustomerAddress.getAllProvince();
    	
    	jsonObject.put("provinces", provinces);
    	renderJson(jsonObject);
    }

    /**
     * 获取所有市
     * @param token
     * @param provinceId 省id（必填）
     * @return 成功：{error: 0, cities: [{id: 市id, name: 名称}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void cities() {
    	int provinceId = getParaToInt("provinceId");
    	List<SCity> citys = CustomerAddress.getCityByProvinceId(provinceId);
    	
    	jsonObject.put("cities", citys);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取所有区
     * @param token
     * @param cityId 市id（必填）
     * @return 成功：{error: 0, districts: [{id: 区id, name: 名称}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void districts() {
    	int cityId = getParaToInt("cityId");
    	List<SDistrict> districts = CustomerAddress.getDistrictByCityId(cityId);
    	
    	jsonObject.put("districts", districts);
    	renderJson(jsonObject);
    }
    
    /**
     * 添加运费模板
     * @param token(必填)
     * @param tempalte(必填) {shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{provinceItem:[{province_id:省id,city_id:市id,isAllProvince:是否省下面所有}],firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费},...]},...
     *                              ]
     *                 }
     *                 
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}                
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void createLogisticsTemplate() {
    	if (!this.validateRequiredString("template")) {
    		return;
    	}
    	String template = getPara("template");
    	
    	ServiceCode code = Logistics.createLogisticsTemplate(template);
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "添加运费模板失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 修改运费模板
     * @param token(必填)
     * @param template {id:模板id,shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{province_id:省id,city_id:市id,isAllProvince:是否省下面所有市,firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费}]},...
     *                              ]
     *                 }
     *                 
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}                    
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void updateLogisticsTemplate() {
    	if (!this.validateRequiredString("template")) {
    		return;
    	}
    	String template = getPara("template");
    	
    	ServiceCode code = Logistics.updateLogisticsTemplate(template);
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "修改运费模板失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 删除运费模板
     * @param token(必填)
     * @param templateId(必填) 模板id
     * @return 成功：{error:0} 失败：{error:>0, errmsg:错误信息}                    
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void deleteLogisticsTemplate() {
    	if (!this.validateRequiredString("templateId")) {
    		return;
    	}
    	int templateId = getParaToInt("templateId");
    	
    	ServiceCode code = Logistics.deleteLogisticsTemplate(templateId);
    	if (code == ServiceCode.Function) {
    		this.returnError(ErrorCode.Exception, "该运费模板已被使用，不能删除");
    		return;
    	}
    	
    	if (code != ServiceCode.Success) {
    		this.returnError(ErrorCode.Exception, "删除模板失败");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个模板信息
     * @param token(必填)
     * @param templateId(必填) 模板id
     * @return 成功：{id:模板id,shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费
     * 											provinceItem:[{province_id:省id,city_id:市id,provinceName:省名称,cityName:市名称,isAllProvince:是否省下面所有市},...]
     * 										  }],...
     *                              }]
     *                  }
     *         失败：{error:>0, errmsg:错误信息}                    
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getLogisticsTemplate() {
    	if (!this.validateRequiredString("templateId")) {
    		return;
    	}
    	int templateId = getParaToInt("templateId");
    	
    	Record record = Logistics.getLogisticsTemplate(templateId);
    	jsonObject.put("data", record);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个店铺下的所有模板 -后台
     * @param token(必填)
     * @param offset(必填)
     * @param length(必填)
     * @return 成功：{error:0, totalPage:总页数,totalRow:总行数, 
	 * 						 data:[{id:模板id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
	 * 				                details:[{expressType:运送方式,firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费,isDefaultFreight:是否全国默认运费,provinceItem:[{provinceName:省名称,cityName:市名称,province_id:省id,city_id:市id,isAllProvince:是否省下面所有市},...]},...]
	 *                            }]
     *              }
     *         失败：{error:>0, errmsg:错误信息}                    
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void paginateLogisticsTemplate() {
    	if (!this.validateRequiredString("offset")) {
    		return;
    	}
    	int offset = getParaToInt("offset");
    	
    	if (!this.validateRequiredString("length")) {
    		return;
    	}
    	int length = getParaToInt("length");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int shopId = customer.getShopId();
    	
    	List<Record> list = Logistics.findLogisticsTemplateItems(offset, length, shopId, null, null, null);
    	int total = Logistics.getLogisticsTemplateItemsCount(shopId, null, null, null);
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRrow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个商品的运费
     * @param id 产品id
     * @param provinceId 省id
     * @param cityId 市id
     * @return 成功：{error:0,data:[{template_id:模板id, expressType:运送方式(1快递，2EMS，3平邮), freight:运费},...]} 失败：{error:>0,errmsg:错误信息}
     */
    public void getFreightByProductId() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	
    	if (!this.validateRequiredString("provinceId")) {
    		return;
    	}
    	
    	if (!this.validateRequiredString("cityId")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int provinceId = getParaToInt("provinceId");
    	int cityId = getParaToInt("cityId");
    	List<Record> list = logistics.getFreightByProduct(id, provinceId, cityId);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }

}