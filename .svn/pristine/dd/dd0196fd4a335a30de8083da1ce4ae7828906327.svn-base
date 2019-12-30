package com.eshop.helper;

import java.util.List;

import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;

public class AddressHelper {
	
	/**
	 * 查询所有省
	 * @return
	 */
	public static List<SProvince> provinces() {
		return SProvince.dao.find("select * from s_province");
	}
	
	/**
	 * 查询某个省下面的市
	 * @param provinceId
	 * @return
	 */
	public static List<SCity> citiesByProvinceId(int provinceId) {
		return SCity.dao.find("select * from s_city where ProvinceID = ?", provinceId);
	}
	
	/**
	 * 查询某个市下面的区
	 * @param provinceId
	 * @return
	 */
	public static List<SDistrict> districtsByCityId(int cityId) {
		return SDistrict.dao.find("select * from s_district where CityID = ?", cityId);
	}
	
	/**
	 * 获取省详情
	 * @param id
	 * @return
	 */
	public static SProvince getProvince(int id) {
		return SProvince.dao.findById(id);
	}
	
	/**
	 * 获取市详情
	 * @param id
	 * @return
	 */
	public static SCity getCity(int id) {
		return SCity.dao.findById(id);
	}
	
	/**
	 * 获取区详情
	 * @param id
	 * @return
	 */
	public static SDistrict getDistrict(int id) {
		return SDistrict.dao.findById(id);
	}
	
}
