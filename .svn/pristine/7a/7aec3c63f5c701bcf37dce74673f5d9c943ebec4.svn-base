package com.eshop.collection;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.eshop.model.Collection;
import com.eshop.model.Product;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class CollectionService {
	
	public final static int PRODUCT = 1;
	public final static int SHOP = 2;

	/**
	 * 批量查询搜藏记录
	 * @param offset
	 * @param count
	 * @param type
	 * @param customerId
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Record> findCollectionItems(int offset, int count, Integer type, Integer customerId, 
			String name, String startTime, String endTime, Integer relateId) {
		
		String sql = findCollectionItemsSql(type, customerId, name, startTime, endTime, relateId);
		sql = BaseDao.appendLimitSql(sql, offset, count);
		
		List<Record> list = Db.find(sql);
		List<Record> resources = Db.find("select * from resource");
		List<Record> products = Db.find("select * from product");
		
		for (Record item : list) {
			Record product = BaseDao.findItem(item.getInt("relate_Id"), products, "id");
			Record resource = BaseDao.findItem(item.getInt("image"), resources, "id");
			String mainPic = (resource != null) ? resource.getStr("path") : "";
			double suggestedRetailUnitPrice = (product != null) ? product.getBigDecimal("suggestedRetailUnitPrice").doubleValue() : 0;
			item.set("mainPic", mainPic);
			item.set("logoPic", mainPic);
			item.set("suggestedRetailUnitPrice", suggestedRetailUnitPrice);
		}
		
		return list;
	}
	
	/**
	 * 批量查询收藏记录的总数量
	 * @param type
	 * @param customerId
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int countCollectionItems(Integer type, Integer customerId, String name, String startTime, 
			String endTime, Integer relateId) {
		
		String sql = findCollectionItemsSql(type, customerId, name, startTime, endTime, relateId);
		return Db.find(sql).size();
	}
	
	/**
	 * 组装sql语句
	 * @param type
	 * @param customerId
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private static String findCollectionItemsSql(Integer type, Integer customerId, String name, 
			String startTime, String endTime, Integer relateId) {
		
		String sql = "select a.*, b.path, b.path as mainPic, b.path as logoPic from collection as a" +
				" left join resource as b on a.image = b.id" +
				" where a.id != 0";
		
		if (type != null) {
			sql += " and a.type = " + type;
		}
		if (customerId != null) {
			sql += " and a.customer_id = " + customerId;
		}
		if (name != null && !name.equals("")) {
			sql += " and a.name like concat('%'," + '"' + name + '"' + ",'%')";
		}
		if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FOMAT(a.created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FOMAT(a.created_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
		if (relateId != null) {
			sql += " and a.relate_Id = " + relateId;
		}
		return sql;
	}
	
	public static ServiceCode cancelCollect(final int type, final int relateId, final int customerId) {
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from collection where type = ? and relate_Id = ? and customer_id = ?", type, relateId, customerId);
				} catch (Exception e) {
					return false;
				}
				
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 取消收藏
	 * @param id
	 * @return
	 */
	public static ServiceCode cancelCollect(int id) {
		if (!Collection.dao.deleteById(id)) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
	}
	
	/**
	 * 批量取消收藏
	 * @param ids
	 * @return
	 */
	public static ServiceCode cancelCollect(final List<Integer> ids) {
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (Integer id : ids) {
						Collection.dao.deleteById(id);
					}
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}

	/**
	 *
	 * 取消收藏
	 * @param customerId
	 * @param product_id
	 */
	public static Integer cancelCollect(Integer customerId,Integer product_id){

		Integer count = Db.update(" delete from collection where customer_id = ? and relate_Id=?",customerId,product_id);
		return count;
	}
	/**
	 * 收藏商品
	 * @param customerId
	 * @param productId
	 * @return
	 */
	public static ServiceCode collectProduct(int customerId, int productId) {
		if (isCollect(customerId, productId, PRODUCT)) {
			return ServiceCode.Validation;
		}
		
		Product product = Product.dao.findById(productId);
		Collection model = new Collection();
		model.setRelateId(productId);
		model.setType(PRODUCT);
		model.setCustomerId(customerId);
		model.setImage(product.getMainPic());
		model.setName(product.getName());
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		model.save();
		return ServiceCode.Success;
	}
	
	/**
	 * 收藏店铺
	 * @param customerId
	 * @param productId
	 * @return
	 */
	public static ServiceCode collectShop(int customerId, int shopId) {
		if (isCollect(customerId, shopId, SHOP)) {
			return ServiceCode.Validation;
		}
		
		Shop shop = Shop.dao.findById(shopId);
		Collection model = new Collection();
		model.setRelateId(shopId);
		model.setType(SHOP);
		model.setCustomerId(customerId);
		model.setImage(shop.getLogoPic());
		model.setName(shop.getName());
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		model.save();
		return ServiceCode.Success;
	}
	
	/**
	 * 是否已收藏
	 * @param customerId
	 * @param relateId
	 * @param type
	 * @return
	 */
	public static boolean isCollect(int customerId, int relateId, int type) {
		int count = Db.find("select * from collection where customer_id = ? and relate_Id = ? and type = ?", customerId, relateId, type).size();
		return count > 0;
	}
	
}
