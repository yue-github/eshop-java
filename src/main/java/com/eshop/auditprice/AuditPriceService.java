package com.eshop.auditprice;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.eshop.log.Log;
import com.eshop.model.AuditPrice;
import com.eshop.model.AuditPriceItems;
import com.eshop.model.Customer;
import com.eshop.model.Product;
import com.eshop.model.ProductPrice;
import com.eshop.model.Shop;
import com.eshop.model.Supplier;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class AuditPriceService {
	
	/**
	 * 批量查询定价列表
	 * @param offset
	 * @param count
	 * @param productName
	 * @param supplierName
	 * @param property
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @param orderByMap
	 * @return
	 */
	public static List<Record> findAuditPriceItems(int offset, int count, String productName, 
			String supplierName, String property, String status, String publishStatus, 
			String startTime, String endTime) {
		
		String sql = findAuditPriceItemsSql(productName, supplierName, property, status, publishStatus,
				startTime, endTime);
		sql = BaseDao.appendLimitSql(sql, offset, count);
		return Db.find(sql);
	}
	
	/**
	 * 统计批量查询定价列表的总条数
	 * @param productName
	 * @param supplierName
	 * @param property
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int countAuditPriceItems(String productName, String supplierName, String property, 
			String status, String publishStatus, String startTime, String endTime) {
		
		String sql = findAuditPriceItemsSql(productName, supplierName, property, status, publishStatus,
				startTime, endTime);
		return Db.find(sql).size();
	}
	
	private static String findAuditPriceItemsSql(String productName,String supplierName, 
			String property, String status, String publishStatus, String startTime, String endTime) {
		
		String sql = "select max(id) as id from audit_price where id != 0";
		
		if (productName != null && !productName.equals("")) {
			sql += " and product_name like '%" + productName + "%'";
		}
		if (supplierName != null && !supplierName.equals("")) {
			sql += " and supplier_name like '%" + supplierName + "%'";
		}
		if (property != null && !property.equals("")) {
			sql += " and property like '%" + property + "%'";
		}
		if (status != null) {
			sql += " and status = '" + status + "'";
		}
		if (publishStatus != null) {
			sql += " and publish_status = '" + publishStatus + "'";
		}
		if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
		
		sql += " group by product_id, publish_status";
		sql = "select b.* from (" + sql + ") as a" +
				" left join audit_price as b on a.id = b.id" +
				" order by b.created_at desc";
    	
    	return sql;
	}
	
	/**
	 * 获取详细信息
	 * @param id
	 * @return
	 */
	public static List<Record> getItems(int theSameNum) {
		return Db.find("select * from audit_price where theSameNum = ?", theSameNum);
	}
	
	public static AuditPrice get(int theSameNum) {
		return AuditPrice.dao.findFirst("select * from audit_price where theSameNum = ?", theSameNum);
	}
	
	/**
	 * 审核
	 * @param id
	 * @param status
	 * @return
	 */
	public static ServiceCode audit(final int theSameNum, final String status, final String operator) {
		final AuditPrice model = AuditPrice.dao.findFirst("select * from audit_price where theSameNum = ?", theSameNum);
		
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		String oldStatus = model.getStatus();
		
		if (status.equals("normal") && (oldStatus.equals("pass") || oldStatus.equals("refuse"))) {
			return ServiceCode.Validation;
		}
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Date auditTime = new Date();
					
					List<AuditPrice> list = AuditPrice.dao.find("select * from audit_price where theSameNum = ?", theSameNum);
					for (AuditPrice item : list) {
						item.setStatus(status);
						item.setLastAuditTime(auditTime);
						item.update();
					}
					
					AuditPriceItems pi = new AuditPriceItems();
					pi.setOperator(operator);
					pi.setProductId(model.getProductId());
					pi.setTheSameNum(theSameNum);
					pi.setStatus(status);
					pi.setAuditTime(auditTime);
					pi.save();
					
					Product product = Product.dao.findById(model.getProductId());
					if (status.equals("pass")) {
						product.setIsSale(1);
					} else {
						product.setIsSale(0);
					}
					product.update();
				} catch (Exception e) {
					Log.error(e.getMessage() + ",定价审核失败");
					return false;
				}
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 记录变价记录
	 * @param customer
	 * @param id
	 * @param oldProduct
	 * @param newProduct
	 * @param oldPrices
	 * @param newPrices
	 * @return
	 */
	public static ServiceCode create(Customer customer, int id, Product oldProduct, Product newProduct, 
			List<ProductPrice> oldPrices, List<ProductPrice> newPrices) {
		
		Supplier supplier = Supplier.dao.findById(newProduct.getSupplierId());
		Shop shop = Shop.dao.findById(newProduct.getShopId());
		String supplierName = supplier.getName();
		String productName = newProduct.getName();
		String shopName = shop.getName();
		
		int productId = newProduct.getId();
		int theSameNum = 0;
		String publisher = getPublisher(customer);
		
		Record record = Db.findFirst("select max(id) as maxId from audit_price");
		if (record.get("maxId") != null) {
			theSameNum = record.getInt("maxId") + 1;
		}
		
		if (id == -1) {
			if (newPrices == null || newPrices.size() <= 0) {
				AuditPrice model = new AuditPrice();
				model.setProductId(productId);
				model.setNewAmount(newProduct.getStoreAmount());
				model.setNewOriginUnitPrice(newProduct.getOriginUnitPrice());
				model.setNewSuggestedRetailUnitPrice(newProduct.getSuggestedRetailUnitPrice());
				model.setNewUnitCost(newProduct.getUnitCost());
				model.setStatus("normal");
				model.setPublishStatus("create");
				model.setCreatedAt(new Date());
				model.setUpdatedAt(new Date());
				model.setProductName(productName);
				model.setSupplierName(supplierName);
				model.setTheSameNum(theSameNum);
				model.setPublisher(publisher);
				model.setShopName(shopName);
				model.save();
			} else {
				for (ProductPrice item : newPrices) {
					String property = BaseDao.getselectProterties(item.getNumber("id").intValue());
					AuditPrice model = new AuditPrice();
					model.setProductId(productId);
					model.setPriceId(item.getNumber("id").intValue());
					model.setNewAmount(item.getProductNumber());
					model.setNewOriginUnitPrice(item.getOriginUnitPrice());
					model.setNewSuggestedRetailUnitPrice(item.getPrice());
					model.setNewUnitCost(item.getUnitCost());
					model.setProperty(property);
					model.setStatus("normal");
					model.setPublishStatus("create");
					model.setCreatedAt(new Date());
					model.setUpdatedAt(new Date());
					model.setProductName(productName);
					model.setSupplierName(supplierName);
					model.setTheSameNum(theSameNum);
					model.setPublisher(publisher);
					model.setShopName(shopName);
					model.save();
				}
			}
		} else {
			if (newPrices == null || newPrices.size() <= 0) {
				if (isProductUpadate(oldProduct, newProduct)) {
					AuditPrice model = new AuditPrice();
					model.setProductId(productId);
					model.setOldAmount(oldProduct.getStoreAmount());
					model.setOldOriginUnitPrice(oldProduct.getOriginUnitPrice());
					model.setOldSuggestedRetailUnitPrice(oldProduct.getSuggestedRetailUnitPrice());
					model.setOldUnitCost(oldProduct.getUnitCost());
					model.setNewAmount(newProduct.getStoreAmount());
					model.setNewOriginUnitPrice(newProduct.getOriginUnitPrice());
					model.setNewSuggestedRetailUnitPrice(newProduct.getSuggestedRetailUnitPrice());
					model.setNewUnitCost(newProduct.getUnitCost());
					model.setStatus("normal");
					model.setPublishStatus("update");
					model.setCreatedAt(new Date());
					model.setUpdatedAt(new Date());
					model.setProductName(productName);
					model.setSupplierName(supplierName);
					model.setTheSameNum(theSameNum);
					model.setPublisher(publisher);
					model.setShopName(shopName);
					model.save();
				}
			} else {
				for (ProductPrice item : newPrices) {
					for (ProductPrice item2 : oldPrices) {
						if (!item.getTypeAttr().equals(item2.getTypeAttr())) {
							continue;
						}
						if (!isProductPriceUpdate(item2, item)) {
							continue;
						}
						String property = BaseDao.getselectProterties(item.getNumber("id").intValue());
						AuditPrice model = new AuditPrice();
						model.setProductId(productId);
						model.setPriceId(item.getNumber("id").intValue());
						model.setOldAmount(item2.getProductNumber());
						model.setOldOriginUnitPrice(item2.getOriginUnitPrice());
						model.setOldSuggestedRetailUnitPrice(item2.getPrice());
						model.setOldUnitCost(item2.getUnitCost());
						model.setNewAmount(item.getProductNumber());
						model.setNewOriginUnitPrice(item.getOriginUnitPrice());
						model.setNewSuggestedRetailUnitPrice(item.getPrice());
						model.setNewUnitCost(item.getUnitCost());
						model.setProperty(property);
						model.setStatus("normal");
						model.setPublishStatus("update");
						model.setCreatedAt(new Date());
						model.setUpdatedAt(new Date());
						model.setProductName(productName);
						model.setSupplierName(supplierName);
						model.setTheSameNum(theSameNum);
						model.setPublisher(publisher);
						model.setShopName(shopName);
						model.save();
					}
				}
			}
		}
		
		return ServiceCode.Success;
	}
	
	private static String getPublisher(Customer customer) {
		return customer.getMobilePhone() != null ? customer.getMobilePhone() : customer.getEmail();
	}
	
	private static boolean isProductUpadate(Product oldProduct, Product newProduct) {
		BigDecimal oldSuggestedRetailUnitPrice = oldProduct.getSuggestedRetailUnitPrice();
		BigDecimal oldUnitCost = oldProduct.getUnitCost();
		BigDecimal oldOriginUnitPrice = oldProduct.getOriginUnitPrice();
		BigDecimal newSuggestedRetailUnitPrice = newProduct.getSuggestedRetailUnitPrice();
		BigDecimal newUnitCost = newProduct.getUnitCost();
		BigDecimal newOriginUnitPrice = newProduct.getOriginUnitPrice();
		
		if (oldSuggestedRetailUnitPrice.compareTo(newSuggestedRetailUnitPrice) != 0) {
			return true;
		}
		if (oldUnitCost.compareTo(newUnitCost) != 0) {
			return true;
		}
		if (oldOriginUnitPrice.compareTo(newOriginUnitPrice) != 0) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isProductPriceUpdate(ProductPrice oldProductPrice, ProductPrice newProductPrice) {
		BigDecimal oldPrice = oldProductPrice.getPrice();
		BigDecimal oldUnitCost = oldProductPrice.getUnitCost();
		BigDecimal oldOriginUnitPrice = oldProductPrice.getOriginUnitPrice();
		BigDecimal newPrice = newProductPrice.getPrice();
		BigDecimal newUnitCost = newProductPrice.getUnitCost();
		BigDecimal newOriginUnitPrice = newProductPrice.getOriginUnitPrice();
		
		if (oldPrice.compareTo(newPrice) != 0) {
			return true;
		}
		if (oldUnitCost.compareTo(newUnitCost) != 0) {
			return true;
		}
		if (oldOriginUnitPrice.compareTo(newOriginUnitPrice) != 0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 查询定价审核详情
	 * @param offset
	 * @param length
	 * @param theSameNum
	 * @param operator
	 * @return
	 */
	public static List<Record> findAuditItems(int offset, int length, Integer theSameNum, String operator) {
		String sql = findAuditItemsSql(theSameNum, operator);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		return Db.find(sql);
	}
	
	private static int countAuditItems(Integer theSameNum, String operator) {
		String sql = findAuditItemsSql(theSameNum, operator);
		return Db.find(sql).size();
	}
	
	private static String findAuditItemsSql(Integer theSameNum, String operator) {
		String sql = "select * from audit_price_items where id != 0";
		
		if (theSameNum != null) {
			sql += " and theSameNum = " + theSameNum;
		}
		if (operator != null && !operator.equals("")) {
			sql += " and operator like '%" + operator + "%'";
		}
		
		sql += " order by audit_time desc";
		
		return sql;
	}
	
}
