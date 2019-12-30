package com.eshop.pickupaddress;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eshop.helper.AddressHelper;
import com.eshop.model.PickupAddress;
import com.eshop.model.ProductPickupAddress;
import com.eshop.model.SCity;
import com.eshop.model.SDistrict;
import com.eshop.model.SProvince;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class PickUpAddressService {

	public ServiceCode create(PickupAddress model) {
		try {
			SProvince province = AddressHelper.getProvince(model.getProvinceId());
			SCity city = AddressHelper.getCity(model.getCityId());
			SDistrict district = AddressHelper.getDistrict(model.getDistrictId());
			model.setProvince(province.getName());
			model.setCity(city.getName());
			model.setDistrict(district.getName());
			model.save();
			return ServiceCode.Success;
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceCode.Failed;
		}
	}
	
	public ServiceCode update(PickupAddress model) {
		try {
			SProvince province = AddressHelper.getProvince(model.getProvinceId());
			SCity city = AddressHelper.getCity(model.getCityId());
			SDistrict district = AddressHelper.getDistrict(model.getDistrictId());
			model.setProvince(province.getName());
			model.setCity(city.getName());
			model.setDistrict(district.getName());
			model.update();
			return ServiceCode.Success;
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceCode.Failed;
		}
	}
	
	public PickupAddress get(int id) {
		return PickupAddress.dao.findById(id);
	}
	
	public List<Record> list(int offset, int length, Integer shopId, String title) {
		String sql = sql(shopId, title);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		return Db.find(sql);
	}
	
	public List<Record> all(Integer shopId, String title) {
		String sql = sql(shopId, title);
		return Db.find(sql);
	}
	
	public List<Record> productAddresses(Integer productId, String title) {
		String sql = "select b.*" + 
				" from product_pickup_address as a" +
				" left join pickup_address as b on a.pickup_address_id = b.id" +
				" where a.id != 0";
		if (productId != null) {
			sql += " and a.product_id = " + productId;
		}
		if (title != null && !title.equals(title)) {
			sql += " and b.title like '%" + title + "%'";
		}
		return Db.find(sql);
	}
	
	public ServiceCode createProductAddresses(int productId, int pickupAddressId) {
		int count = Db.find("select * from product_pickup_address where product_id = ? and pickup_address_id = ?", productId, pickupAddressId).size();
		if (count <= 0) {
			ProductPickupAddress model = new ProductPickupAddress();
			model.setProductId(productId);
			model.setPickupAddressId(pickupAddressId);
			model.save();
		}
		return ServiceCode.Success;
	}
	
	public static void createProductAddresses(final int productId, final JSONArray productAddresses) {
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from product_pickup_address where product_id = ?", productId);
					for (int i = 0; i < productAddresses.size(); i++) {
						JSONObject item = productAddresses.getJSONObject(i);
						ProductPickupAddress model = new ProductPickupAddress();
						model.setProductId(productId);
						model.setPickupAddressId(item.getIntValue("id"));
						model.save();
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
	}
	
	public ServiceCode deleteProductAddresses(int id) {
		boolean flag = ProductPickupAddress.dao.deleteById(id);
		return flag ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	public static void deleteProductAddressesByProductId(int productId) {
		Db.update("delete from product_pickup_address where product_id = ?", productId);
	}
	
	public int count(Integer shopId, String title) {
		String sql = sql(shopId, title);
		return Db.find(sql).size();
	}
	
	private String sql(Integer shopId, String title) {
		String sql = "select * from pickup_address where id != 0";
		if (shopId != null) {
			sql += " and shop_id = " + shopId;
		}
		if (title != null && !title.equals("")) {
			sql += " and title like '%" + title + "%'";
		}
		return sql;
	}
	
	public ServiceCode delete(final int id) {
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from product_pickup_address where pickup_address_id = ?", id);
					PickupAddress.dao.deleteById(id);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	public List<Record> getCommonPickupAddresses(String orders) {
		// 获取所有产品
		JSONArray jarray = JSON.parseArray(orders);
		List<Integer> products = new ArrayList<Integer>();
		for (int i = 0; i < jarray.size(); i++) {
			JSONObject item = jarray.getJSONObject(i);
			JSONArray parray = item.getJSONArray("products");
			for (int j = 0; j < parray.size(); j++) {
				JSONObject pd = parray.getJSONObject(j);
				if (!products.contains(pd.getInteger("productId")))
					products.add(pd.getInteger("productId"));
			}
		}
		
		// 获取所有自提地址
		List<Record> addresses = new ArrayList<Record>();
		for (Integer productId : products) {
			List<ProductPickupAddress> list = ProductPickupAddress.dao.find("select * from product_pickup_address where product_id = ?", productId);
			List<Integer> addressIds = new ArrayList<Integer>();
			for (ProductPickupAddress item : list) {
				if (!addressIds.contains(item.getPickupAddressId())) {
					addressIds.add(item.getPickupAddressId());
				}
			}
			Record record = new Record();
			record.set("address_ids", addressIds);
			addresses.add(record);
		}
		
		// 获取共同自提地址
		List<Integer> common = addresses.get(0).get("address_ids");
		for (Record item : addresses) {
			List<Integer> addressIds = item.get("address_ids");
			common.retainAll(addressIds);
		}
		
		String whereIn = BaseDao.getWhereIn(common);
		List<Record> pickupAddresses = Db.find("select * from pickup_address where id in " + whereIn);
		
		return pickupAddresses;
	}
	
}
