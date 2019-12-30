package com.eshop.logistics;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.eshop.log.Log;
import com.eshop.model.Address;
import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class CustomerAddress {

	/**
	 * 设置默认地址
	 * @param id
	 * @return
	 */
    public static ServiceCode setDefaultAddress(final int id) {
    	Address address = Address.dao.findById(id);
    	final int customerId = address.getCustomerId();
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("update address set isDefault = 0 where customer_id = ?", customerId);
			    	Db.update("update address set isDefault = 1 where id = ?", id);
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(Arrays.toString(e.getStackTrace()) + e.getMessage() + ",设置默认地址失败,id" + id);
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 获取默认地址
     * @param customerId
     * @return
     */
    public static Address getDefaultAddress(int customerId) {
    	return Address.dao.findFirst("select * from address where customer_id = ?", customerId);
    }
    
    /**
     * 创建会员地址
     * 算法：如果是第一次创建地址，则设置该地址为默认地址
     * @param address 地址信息
     * @return 返回码
     */
    public static ServiceCode createAddress(Address model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	String province = CustomerAddress.getProvinceName(model.getProvinceId());
    	String city = CustomerAddress.getCityName(model.getCityId());
    	String district = CustomerAddress.getDistrictName(model.getDistrictId());
    	
    	model.setProvince(province);
    	model.setCity(city);
    	model.setDistrict(district);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	if (model.save()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
    }
    
    /**
     * 修改会员地址
     * @param address 地址信息
     * @return 返回码
     */
    public static ServiceCode updateAddress(Address model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	String province = CustomerAddress.getProvinceName(model.getProvinceId());
    	String city = CustomerAddress.getCityName(model.getCityId());
    	String district = CustomerAddress.getDistrictName(model.getDistrictId());
    	
    	model.setProvince(province);
    	model.setCity(city);
    	model.setDistrict(district);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	if (model.update()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
    }
    
    /**
     * 删除会员地址
     * @param addressId 地址id
     * @return
     */
    public static ServiceCode deleteAddress(int id) {
    	if (Address.dao.deleteById(id)) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
    }
    
    /**
     * 查看地址
     * @param addressId 
     * @return
     */
    public static Address getAddress(int id) {
    	return Address.dao.findById(id);
    }
    
    /**
     * 批量查询地址
     * @param offset
     * @param count
     * @param customerId
     * @param contacts
     * @param phone
     * @return
     */
    public static List<Address> findAddressesItems(int offset, int count, Integer customerId, 
    		String contacts, String phone) {
    	
    	String sql = findAddressesItemsSql(customerId, contacts, phone);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	return Address.dao.find(sql);
    }
    
    /**
     * 批量查询地址
     * @param customerId
     * @param contacts
     * @param phone
     * @return
     */
    public static List<Address> findAddressesItems(Integer customerId, String contacts, String phone) {
    	String sql = findAddressesItemsSql(customerId, contacts, phone);
    	return Address.dao.find(sql);
    }
    
    /**
     * 批量查询地址的总数量
     * @param customerId
     * @param contacts
     * @param phone
     * @return
     */
    public static int countAddressesItems(Integer customerId, String contacts, String phone) {
    	String sql = findAddressesItemsSql(customerId, contacts, phone);
    	return Db.find(sql).size();
    }
	
    /**
     * 组装sql语句
     * @param customerId
     * @param contacts
     * @param phone
     * @return
     */
    public static String findAddressesItemsSql(Integer customerId, String contacts, String phone) {
    	String sql = "select * from address where id != 0";
    	if (customerId != null) {
			sql += " and customer_id = " + customerId;
		}
    	if (contacts != null && !contacts.equals("")) {
			sql += " and contacts like '%" + contacts + "%'";
		}
    	if (phone != null && !phone.equals("")) {
			sql += " and phone like '%" + phone + "%'";
		}
    	sql += " order by isDefault desc";
    	return sql;
    }
    
    /**
     * 获取省份
     * @param id
     * @return
     */
    public static SProvince getProvince(int id) {
    	return SProvince.dao.findById(id);
    }
    
    /**
     * 获取省名
     * @param id
     * @return
     */
    public static String getProvinceName(int id) {
    	SProvince province = getProvince(id);
    	return province != null ? province.getName() : "";
    }
    
    /**
     * 获取某个省份的所有市
     * @param provinceId
     * @return
     */
    public static List<SCity> getCityByProvinceId(int provinceId) {
    	return SCity.dao.find("select * from s_city where ProvinceID = ?", provinceId);
    }
    
    /**
     * 获取市
     * @param id
     * @return
     */
    public static SCity getCity(int id) {
    	return SCity.dao.findById(id);
    }
    
    /**
     * 获取市名
     * @param id
     * @return
     */
    public static String getCityName(int id) {
    	SCity city = getCity(id);
    	return city != null ? city.getName() : "";
    }
    
    /**
     * 获取某个市下面的所有区
     * @param cityId
     * @return
     */
    public static List<SDistrict> getDistrictByCityId(int cityId) {
    	return SDistrict.dao.find("select * from s_district where CityID = ?", cityId);
    }
    
    /**
     * 获取区
     * @param id
     * @return
     */
    public static SDistrict getDistrict(int id) {
    	return SDistrict.dao.findById(id);
    }
    
    /**
     * 获取区名
     * @param id
     * @return
     */
    public static String getDistrictName(int id) {
    	SDistrict district = getDistrict(id);
    	return district != null ? district.getName() : "";
    }
    
    /**
     * 获取所有省份
     * @return
     */
    public static List<SProvince> getAllProvince() {
    	return SProvince.dao.find("select * from s_province");
    }
    
    /**
     * 获取所有市
     * @return
     */
    public static List<SCity> getAllCity() {
    	return SCity.dao.find("select * from s_city");
    }
    
    /**
     * 获取所有区
     * @return
     */
    public static List<SDistrict> getAllDistrict() {
    	return SDistrict.dao.find("select * from s_district");
    }
    
    /**
     * 获取所有省和市
     * @return [{id:省id,name:省名称,cities:[{id:市id,name:市名称},...]},...]
     */
    public static List<Record> getProvinceAndCity() {
    	List<Record> provinces = Db.find("select * from s_province");
    	
    	for (Record province : provinces) {
    		List<Record> cities = Db.find("select * from s_city where ProvinceID = ?", province.getInt("id"));
    		province.set("cities", cities);
    	}
    	
    	return provinces;
    }
    
}
