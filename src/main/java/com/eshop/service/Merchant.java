package com.eshop.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eshop.category.CategoryService;
import com.eshop.content.ResourceService;
import com.eshop.event.EventEnum;
import com.eshop.event.param.OrderParam;
import com.eshop.helper.AlipayRefundHelper;
import com.eshop.helper.DateHelper;
import com.eshop.helper.FileHelper;
import com.eshop.helper.UnionRefundHelper;
import com.eshop.helper.WalletRefundHelper;
import com.eshop.helper.WxRefundHelper;
import com.eshop.log.Log;
import com.eshop.model.Back;
import com.eshop.model.Category;
import com.eshop.model.Customer;
import com.eshop.model.Order;
import com.eshop.model.Product;
import com.eshop.model.ProductHotel;
import com.eshop.model.ProductOrder;
import com.eshop.model.ProductPrice;
import com.eshop.model.Property;
import com.eshop.model.PropertyValue;
import com.eshop.model.Refund;
import com.eshop.model.Resource;
import com.eshop.model.Shop;
import com.eshop.model.Supplier;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 
 */
public class Merchant extends User {
	
	private static CategoryService categoryService = new CategoryService();

    /**
     * 创建店铺
     * @param model 
     * @return
     */
    public static ServiceCode setUpShop(final Shop model, final int customerId, final String idcardPic, final String businessLicensePic, final String logoPic) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	// 判断是否已创建店铺
    	Customer customer = Customer.dao.findById(customerId);
    	if (customer.getShopId() > 0) {
    		return ServiceCode.Validation;
    	}
    	
    	model.set("created_at", new Date());
    	model.set("updated_at", new Date());
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					model.save();
					
					int shopId = model.getId();
					int idcard = ResourceService.insertResource(idcardPic, shopId, ResourceService.SHOP_IDCARD, ResourceService.PICTURE);
					int license = ResourceService.insertResource(businessLicensePic, shopId, ResourceService.SHOP_LICENSE, ResourceService.PICTURE);
					int logo = ResourceService.insertResource(logoPic, shopId, ResourceService.SHOP_LOGO, ResourceService.PICTURE);
				
					model.setIdcardPic(idcard);
					model.setBusinessLicensePic(license);
					model.setLogoPic(logo);
					model.update();
					
					Customer customer = Customer.dao.findById(customerId);
					customer.setShopId(shopId);
					customer.update();
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",创建店铺失败");
					return false;
				}
				
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 查看店铺
     * @param id 
     * @return
     */
    public static Shop getShop(int id) {
        return Shop.dao.findById(id);
    }

    /**
     * 修改店铺信息
     * @param model 店铺信息
     * @param logoPic 店铺logo
     * @return 返回码
     */
    public static ServiceCode updateShop(Shop model, String logoPic) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	model.setUpdatedAt(new Date());
    	
     	if(!model.update()){
     		return ServiceCode.Failed;
     	}
     	
     	if (model.getLogoPic() != null) {
     		Resource res = Resource.dao.findById(model.getLogoPic());
     		res.setPath(logoPic);
     		res.update();
     	} else {
     		int resId = ResourceService.insertResource(logoPic, 0, ResourceService.SHOP_LOGO, ResourceService.PICTURE);
        	model.setLogoPic(resId);
        	model.update();
     	}
    	
     	return ServiceCode.Success;
    }
    
    /**
     * 修改店铺
     * @param model
     * @return
     */
    public static ServiceCode updateShop(Shop model) {
    	if (model.update()) {
    		return ServiceCode.Success;
    	}
    	
    	return ServiceCode.Failed;
    }
    
    /**
     * 根据分类id获取非销售属性  -发布产品
     * @param categoryId 分类id
     * @return list 非销售属性数组  格式：[{propertyId:属性id，propertyName：属性名，propertyValueList:[{propertyValueId：属性值id，propertyValueName：属性名称，isSelected：是否被选中},...]},...]
     */
    public static List<Record> getAllNosalePropertyByCategoryId(int categoryId) {
    	List<Property> model = Property.dao.find("select * from property where category_id = ? and is_sale_pro = 0",categoryId);
    	
		List<Record> records = new ArrayList<Record>();
    	for (Property item : model) {
    		Record record = new Record();
    		record.set("propertyId", item.getId());
    		record.set("propertyName", item.getName());
    		
    		List<PropertyValue> propertyValue = PropertyValue.dao.find("select * from property_value where property_id = ?",item.getId());
    		for(PropertyValue item1 : propertyValue){
    			item1.put("propertyValueId",item1.getId());
    			item1.put("propertyValueName",item1.getName());
    			item1.put("isSelected",0);
    		}
    		record.set("propertyValueList", propertyValue);
    		
    		records.add(record);
    	}
    	
    	return records;
    }
    
    /**
     * 根据分类id获取销售属性  -发布产品
     * @param categoryId 分类id
     * @return list 销售属性数组  格式：[{propertyId:属性id，propertyName：属性名，propertyValueList:[{propertyValueId：属性值id，propertyValueName：属性名称，isSelected：是否被选中},...]},...]
     */
    public static List<Record> getAllsalePropertyByCategoryId(int categoryId) {
    	List<Property> model = Property.dao.find("select * from property where category_id = ? and is_sale_pro = 1",categoryId);
    	
		List<Record> records = new ArrayList<Record>();
    	for (Property item : model) {
    		Record record = new Record();
    		record.set("propertyId", item.getId());
    		record.set("propertyName", item.getName());
    		
    		List<PropertyValue> propertyValue = PropertyValue.dao.find("select * from property_value where property_id = ?",item.getId());
    		for(PropertyValue item1 : propertyValue){
    			item1.put("propertyValueId",item1.getId());
    			item1.put("propertyValueName",item1.getName());
    			item1.put("isSelected",0);
    		}
    		record.set("propertyValueList", propertyValue);
    		
    		records.add(record);
    	}
    	
    	return records;
    }
    
    /**
     * 根据产品id获取销非售属性 -产品详情页
     * @param productId
     * @return list 非销售属性数组， 格式：[{propertyId:属性id，propertyName：属性名，propertyValueId：属性值id，propertyValueName：属性名称},...]
     */
    public static List<Record> getNosalePropertyByProductId(int productId) {
    	List<Record> list = new ArrayList<Record>();
    	Product product = Product.dao.findById(productId);
    	String prodProp = product.getProdProp();
    	
    	if (prodProp == null || prodProp.equals("")) {
    		return list;
    	}
    	
    	String[] prodPropArr = prodProp.split(",");
    	for (String prodPropStr : prodPropArr) {
    		Record record = new Record();
    		
    		int propertyValueId = Integer.parseInt(prodPropStr);
    		PropertyValue propertyValue = PropertyValue.dao.findById(propertyValueId);
    		Property property = Property.dao.findById(propertyValue.getPropertyId());
    		List<PropertyValue> propertyValues = PropertyValue.dao.find("select * from property_value where property_id = ?", property.getId());
    		List<PropertyValue> newPropertyValues = new ArrayList<PropertyValue>();
    		for (PropertyValue propertyValueItem : propertyValues) {
    			propertyValueItem.put("propertyValueId", propertyValueItem.getId());
    			propertyValueItem.put("propertyValueName", propertyValueItem.getName());
    			if (propertyValueItem.getId() == propertyValueId) {
    				propertyValueItem.put("isSelected", 1);
    				newPropertyValues.add(propertyValueItem);
    			}
    		}
    		
    		record.set("propertyId", property.getId());
    		record.set("propertyName", property.getName());
    		record.set("propertyValueList", newPropertyValues);
    		list.add(record);
    	}
    	
    	return list;
    }
    
    /**
     * 根据产品id获取销售属性 -产品详情页
     * @param productId
     * @return list 销售属性数组， 格式：[{propertyId:属性id，propertyName：属性名，propertyValueList：[{propertyValueId：属性值id，propertyValueName：属性名称}，...]},...]
     */
    public static List<Record> getSalePropertyByProductId(int productId) {
    	List<Record> result = new ArrayList<Record>();
    	List<ProductPrice> list = ProductPrice.dao.find("select * from product_price where product_id = ?", productId);
    	
    	if (list.size() <= 0) {
    		return result;
    	}
    	
    	ProductPrice productPrice = ProductPrice.dao.findFirst("select * from product_price where product_id = ?", productId);
    	String[] arr = productPrice.getTypeAttr().split(",");
    	List<Property> propertys = new ArrayList<Property>();
    	
    	for (String item : arr) {
    		if (!item.equals("")) {
    			int propertyValueId = Integer.parseInt(item);
        		PropertyValue pv = PropertyValue.dao.findById(propertyValueId);
        		Property pt = Property.dao.findById(pv.getPropertyId());
        		propertys.add(pt);
    		}
    	}
    	
    	for (Property item : propertys) {
    		Record record = new Record();
    		record.set("propertyId", item.getId());
    		record.set("propertyName", item.getName());
    		List<Record> propertyValueList = new ArrayList<Record>();
    		for (ProductPrice item2 : list) {
    			Record propertyValueItem = new Record();
    			String pvStr = item2.getTypeAttr();
    			String[] pvIds = pvStr.split(",");
    			for (String item3 : pvIds) {
    				int propertyValueId = Integer.parseInt(item3);
    	    		PropertyValue pv = PropertyValue.dao.findById(propertyValueId);
    	    		Property pt = Property.dao.findById(pv.getPropertyId());
    	    		if (pt.getId() == item.getId()) {
    	    			propertyValueItem.set("propertyValueId", pv.getId());
    	    			propertyValueItem.set("propertyValueName", pv.getName());
    	    			break;
    	    		}
    			}
    			
    			if(!existPropertyValue(propertyValueList, propertyValueItem.getInt("propertyValueId"))) {
    				propertyValueList.add(propertyValueItem);
    			}
    		}
    		record.set("propertyValueList", propertyValueList);
    		result.add(record);
    	}
    	
    	return result;
    }
    
    /**
     * 判断是否已属性值
     * @param list
     * @param id
     * @return
     */
    private static boolean existPropertyValue(List<Record> list, int id) {
    	for(Record r : list) {
    		if(r.getInt("propertyValueId") == id) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * 获取产品价格
     * @param productId 产品id
     * @param typeAttr 销售属性
     * @return
     */
    public static ProductPrice getPrice(int productId, String typeAttr) {
    	List<ProductPrice> list =  ProductPrice.dao.find("select * from product_price where product_id = ? and type_attr = ?", productId, typeAttr);
    	
    	if(list.size() <= 0) {
    		return null;
    	}
    	
    	return list.get(0);
    }

    /**
     * 发布产品
     * @param product
     * @param attrTypeList
     * @param mainPicStr [path1,path2,...]
     * @return
     */
    public static ServiceCode publishProduct(final Product product, final String hotelList, final String attrTypeList, final String mainPicStr) {
    	if (product == null) {
    		return ServiceCode.Failed;
    	}

    	boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
			    	int id = product.get("id", -1);
			    	if(id == -1) {  //添加
			    		product.set("created_at", new Date());
			    		product.set("updated_at", new Date());
			    		product.save();
			    	} else {  //修改
			    		product.set("updated_at", new Date());
			    		product.update();
			    		
			    		List<ProductPrice> prices = ProductPrice.dao.find("select * from product_price where product_id = ?", product.getId());
			        	for(ProductPrice item : prices) {
			        		item.delete();
			        	}
			        	
			        	List<Resource> resources = Resource.dao.find("select * from resource where category = ? and type = ? and relate_id = ?", 1, 1, product.getId());
			        	for (Resource item : resources) {
			        		item.delete();
			        		FileHelper.delete(item.getPath());
			        	}
			    	}
			    	
			    	if(product.getProdType() != 1) {
				    	if (attrTypeList != null && !attrTypeList.equals("")) {
				    		createProductPrice(product.getId(), attrTypeList);
				    	}
			    	}else {
			    		if (hotelList != null && !hotelList.equals("")) {
				    		createProductHotel(product.getId(), hotelList);
				    	}
			    	}
			    	
			    	if (mainPicStr != null && !mainPicStr.equals("")) {
			    		createProductResource(product.getId(), mainPicStr);
			    	}
			    	
			    	// 计算未含税成本
			    	calcualteUnitCostNoTax(product);
			    	
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 计算公式：
     * 增值税普通发票：含税成本=未税成本
     * 增值税专用发票：未税成本=含税成本/(1+税率)
     * @param product
     */
    public static void calcualteUnitCostNoTax(final Product product) {
    	Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					String invoiceType = product.getInvoiceType();
					BigDecimal taxRate = product.getTaxRate().multiply(new BigDecimal(0.01));
			    	BigDecimal remainRate = new BigDecimal(1);
			    	
			    	if (invoiceType.equals("value_add")) {
			    		remainRate = remainRate.add(taxRate);
			    	} 
			    	
			    	BigDecimal unitCost = product.getUnitCost();
			    	BigDecimal unitCostNoTax = unitCost.divide(remainRate, 2, BigDecimal.ROUND_HALF_DOWN);
			    	product.setUnitCostNoTax(unitCostNoTax);
			    	product.update();
			    	
			    	List<ProductPrice> prices = ProductPrice.dao.find("select * from product_price where product_id = ?", product.getId());
			    	for (ProductPrice item : prices) {
			    		BigDecimal unitCost2 = item.getUnitCost();
			    		if (unitCost2 != null) {
			    			BigDecimal unitCostNoTax2 = unitCost2.divide(remainRate, 2, BigDecimal.ROUND_HALF_DOWN);
			    			item.setUnitCostNoTax(unitCostNoTax2);
			    			item.update();
			    		}
			    	}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
    }
    
    /**
     * 创建酒店产品价格
     * @param productId 产品id
     * @param attrTypeList 产品价格，格式: [{"attrType":"1,2", "price":1.22, "productNumber":22}]
     * @return code
     */
    private static ServiceCode createProductHotel(int productId, String tripList) {
    	Product product = Product.dao.findById(productId);
    	
    	if (product == null) {
    		return ServiceCode.Failed;
    	}
    	
    	if (tripList == null || tripList.equals("")) {
    		return ServiceCode.Failed;
    	}
    	
    	JSONArray list = JSON.parseArray(tripList);
    	
    	Db.update("delete from product_hotel where product_id = ?", productId);
    	
    	for (int i = 0; i < list.size(); i++) {
    		JSONObject item = list.getJSONObject(i);
    		
			int productNumber = item.getIntValue("product_number");
			BigDecimal price = item.getBigDecimal("price");
			BigDecimal unitCost = item.getBigDecimal("unitCost");
			
			ProductHotel model = new ProductHotel();
			model.setStartAt(item.getDate("start_at"));
			model.setEndAt(item.getDate("end_at"));
			model.setPrice(price);
			model.setUnitCost(unitCost);
			model.setProductNumber(productNumber);
			model.setProductId(productId);
    		if (item.get("originUnitPrice") != null && !item.get("originUnitPrice").toString().equals("")) {
    			model.setOriginUnitPrice(new BigDecimal(item.get("originUnitPrice").toString()));
    		}
    		
    		model.save();
    	}
    	
		return ServiceCode.Success;
    }
    
    
    /**
     * 创建产品价格
     * @param productId 产品id
     * @param attrTypeList 产品价格，格式: [{"attrType":"1,2", "price":1.22, "productNumber":22}]
     * @return code
     */
    private static ServiceCode createProductPrice(int productId, String attrTypeList) {
    	Product product = Product.dao.findById(productId);
    	
    	if (product == null) {
    		return ServiceCode.Failed;
    	}
    	
    	JSONArray list = JSON.parseArray(attrTypeList);
    	
    	for (int i = 0; i < list.size(); i++) {
    		JSONObject item = list.getJSONObject(i);
    		
			int productNumber = item.getIntValue("productNumber");
			String attrType = item.getString("attrType");
			BigDecimal price = item.getBigDecimal("price");
			BigDecimal unitCost = item.getBigDecimal("unitCost");
			BigDecimal originUnitPrice = item.getBigDecimal("originUnitPrice");
			
    		ProductPrice model = new ProductPrice();
    		model.setProductId(productId);
    		model.setTypeAttr(attrType);
    		model.setPrice(price);
    		model.setProductNumber(productNumber);
    		model.setUnitCost(unitCost);
    		model.setOriginUnitPrice(originUnitPrice);
    		model.setCreatedAt(new Date());
    		model.setUpdatedAt(new Date());
    		model.save();
    	}
    	
		return ServiceCode.Success;
    }
    
    /**
     * 创建产品图片
     * @param productId 产品图片
     * @param mainPicStr [path1,path2,...]
     */
    private static ServiceCode createProductResource(int productId, String mainPicStr) {
    	Product product = Product.dao.findById(productId);
    	
    	if (product == null) {
    		return ServiceCode.Failed;
    	}
    	
    	if (mainPicStr == null || mainPicStr.equals("")) {
    		return ServiceCode.Failed;
    	}
    	
    	List<String> list = JSON.parseArray(mainPicStr, String.class);
    	int mainPic = 0;
    	
		for (String item : list) {
			Resource res = new Resource();
			res.setCategory(1);
			res.setType(1);
			res.setRelateId(productId);
			res.setPath(item);
			res.setCreatedAt(new Date());
			res.setUpdatedAt(new Date());
			res.save();
			
			if (mainPic == 0) {
				mainPic = res.getId();
			}
		}
		
		product.setMainPic(mainPic);
		product.update();
		
		return ServiceCode.Success;
    }
    
    /**
     * 改变产品状态
     * @param id
     * @param targetStatus
     * @return
     */
    private static ServiceCode changeShelfState(int id, int targetStatus) {
    	Product product = Product.dao.findById(id);
    	if (product == null) {
			return ServiceCode.Failed;
		}
    	product.setIsSale(targetStatus);
    	product.update();
    	return ServiceCode.Success;
    }
    
    private static ServiceCode changeShelfState(int id, int isSale, int isDelete) {
    	Product product = Product.dao.findById(id);
    	if (product == null) {
			return ServiceCode.Failed;
		}
    	product.setIsSale(isSale);
    	product.setIsDelete(isDelete);
    	product.update();
    	return ServiceCode.Success;
    }

    /**
     * 上架产品
     * @param id 
     * @return
     */
    public static ServiceCode onShelf(int id) {
        return changeShelfState(id, 1, 0);
    }
    
    /**
     * 批量上架产品
     * @param ids
     * @return
     */
    public static ServiceCode onShelf(final List<String> ids) {
       boolean success = Db.tx(new IAtom() {
		
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						ServiceCode code = onShelf(id);
						
						if (code != ServiceCode.Success) {
							return false;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",批量下架产品失败");
					return false;
				}
				return true;
			}
		});
       
       return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 下架产品
     * @param id 
     * @return
     */
    public static ServiceCode offShelf(int id) {
        return changeShelfState(id, 0);
    }
    
    /**
     * 批量下架产品
     * @param ids
     * @return
     */
    public static ServiceCode offShelf(final List<String> ids) {
       boolean success = Db.tx(new IAtom() {
		
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						ServiceCode code = offShelf(id);
						
						if (code != ServiceCode.Success) {
							return false;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",批量下架产品失败");
					return false;
				}
				return true;
			}
		});
       
       return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 删除产品
     * @param id
     * @return
     */
    public static ServiceCode deleteProduct(int id) {
        Db.update("update product set isDelete = 1 where id = ?", id);
        return ServiceCode.Success;
    }
    
    /**
     * 批量删除产品
     * @param ids
     * @return
     */
    public static ServiceCode deleteProduct(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						deleteProduct(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",批量删除产品失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 还原商品
     * @param id
     * @return code
     */
    public static ServiceCode recoverProduct(int id) {
    	Product product = Product.dao.findById(id);
    	
    	if (product == null) {
    		return ServiceCode.Failed;
    	}
    	
    	product.setIsDelete(0);
    	product.update();
    	
    	return ServiceCode.Success;
    }
    
    public static List<Record> findProductItems(int offset, int count, Integer shopId, String productName,
    		Integer cateId, String cateName, Integer isSale, Integer isDeleted, String startTime, 
    		String endTime, Integer supplierId, String supplierName, BigDecimal unitCostMoreThan, 
    		BigDecimal unitCostLessThan, BigDecimal suggestedRetailUnitPriceMoreThan, 
    		BigDecimal suggestedRetailUnitPriceLessThan, String prodType, Map<String, String> orderByMap) {
    	
        String sql = findProductItemsSql(shopId, productName, cateId, cateName, isSale, isDeleted, startTime, endTime, supplierId, supplierName, unitCostMoreThan, unitCostLessThan, suggestedRetailUnitPriceMoreThan, suggestedRetailUnitPriceLessThan, prodType, orderByMap);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        System.out.println(sql);
        List<Record> list = Db.find(sql);
        List<Record> resources = Db.find("select * from resource");
        List<Record> shops = Db.find("select * from shop");
        
        for (Record item : list) {
        	Record resouce = BaseDao.findItem(item.getInt("mainPic"), resources, "id");
        	String mainPic = (resouce != null) ? resouce.getStr("path") : "";
        	Record shop = BaseDao.findItem(item.getInt("shop_id"), shops, "id");
        	item.set("mainPic", mainPic);
        	item.set("shopName", shop.getStr("name"));
        }
        
        return list;
    }
    
    public static int countProductItems(Integer shopId, String productName,
    		Integer cateId, String cateName, Integer isSale, Integer isDeleted, String startTime, 
    		String endTime, Integer supplierId, String supplierName, BigDecimal unitCostMoreThan, 
    		BigDecimal unitCostLessThan, BigDecimal suggestedRetailUnitPriceMoreThan, 
    		BigDecimal suggestedRetailUnitPriceLessThan, String prodType) {
    	
        String sql = findProductItemsSql(shopId, productName, cateId, cateName, isSale, isDeleted, startTime, endTime, supplierId, supplierName, unitCostMoreThan, unitCostLessThan, suggestedRetailUnitPriceMoreThan, suggestedRetailUnitPriceLessThan, prodType, null);
        
        return Db.find(sql).size();
    }
    
    public static List<Record> findProductItems(int offset, int count, Integer shopId, String productName,
    		Integer cateId, String cateName, Integer isSale, Integer isDeleted, String startTime, 
    		String endTime, Integer supplierId, String supplierName, String prodType, 
    		Map<String, String> orderByMap) {
    	
        List<Record> list = findProductItems(offset, count, shopId, productName, cateId, cateName, isSale, isDeleted, startTime, endTime, supplierId, supplierName, null, null, null, null, prodType, orderByMap);
        
        return list;
    }
    
    public static List<Record> findProductItems(Integer shopId, String productName, Integer cateId, 
    		String cateName, Integer isSale, Integer isDeleted, String startTime, String endTime, 
    		Integer supplierId, String supplierName, String prodType, Map<String, String> orderByMap) {
    	
        String sql = findProductItemsSql(shopId, productName, cateId, cateName, isSale, isDeleted, startTime, endTime, supplierId, supplierName, null, null, null, null, prodType, orderByMap);
        
        return Db.find(sql);
    }
    
    /**
     * 批量查询产品的总数
     * @param shopId
     * @param productName
     * @param cateId
     * @param cateName
     * @param isSale
     * @param isDeleted
     * @param startTime
     * @param endTime
     * @return
     */
    public static int countProductItems(Integer shopId, String productName, Integer cateId, String cateName,
    		Integer isSale, Integer isDeleted, String startTime, String endTime, Integer supplierId, 
    		String supplierName, String prodType) {
        
    	int count = countProductItems(shopId, productName, cateId, cateName, isSale, isDeleted, startTime, endTime, supplierId, supplierName, null, null, null, null, prodType);
    	
    	return count;
    }
    
    private static String findProductItemsSql(Integer shopId, String productName, Integer cateId, 
    		String cateName, Integer isSale, Integer isDeleted, String startTime, String endTime, 
    		Integer supplierId, String supplierName, BigDecimal unitCostMoreThan, 
    		BigDecimal unitCostLessThan, BigDecimal suggestedRetailUnitPriceMoreThan, 
    		BigDecimal suggestedRetailUnitPriceLessThan, String prodType,
    		Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, b.name as categoryName, c.name as supplierName from product as a" +
    			" left join category as b on a.category_id = b.id" +
    			" left join supplier as c on c.id = a.supplier_id" +
    			" where a.id != 0";
    	
    	if (shopId != null){
    		sql += " and a.shop_id = " + shopId;
    	}
    	System.out.println("productName="+productName);
    	if (productName != null && !productName.equals("")) {
			sql += " and a.name like concat('%'," + '"' +productName + '"' + ",'%')";
		}
    	if (cateId != null) {
        	List<Category> list = new CategoryService().getAllChildAndInclude(cateId);
        	String whereIn = CategoryService.getWhereInIds(list);
        	sql += " and a.category_id in " + whereIn;
    	}
    	if (cateName != null && !cateName.equals("")) {
        	List<Category> list = categoryService.getAllChildAndInclude(cateName);
        	String whereIn = CategoryService.getWhereInIds(list);
        	sql += " and a.category_id in " + whereIn;
    	}
    	if (isSale != null) {
			sql += " and a.is_sale = " + isSale;
		}
    	if (isDeleted != null) {
			sql += " and a.isDelete = " + isDeleted;
		}
    	if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
    	if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
    	if (supplierId != null) {
    		sql += " and a.supplier_id = " + supplierId;
    	}
    	if (supplierName != null && !supplierName.equals("")) {
    		sql += " and c.name like '%" + supplierName + "%'";
    	}
    	if (unitCostMoreThan != null && !unitCostMoreThan.equals("")){
    		sql += " and a.unitCost > " + unitCostMoreThan;
    	}
    	if (unitCostLessThan != null && !unitCostLessThan.equals("")){
    		sql += " and a.unitCost < " + unitCostLessThan;
    	}
    	if (suggestedRetailUnitPriceLessThan != null && !suggestedRetailUnitPriceLessThan.equals("")){
    		sql += " and a.suggestedRetailUnitPrice < " + suggestedRetailUnitPriceLessThan;
    	}
    	if (suggestedRetailUnitPriceMoreThan != null && !suggestedRetailUnitPriceMoreThan.equals("")){
    		sql += " and a.suggestedRetailUnitPrice > " + suggestedRetailUnitPriceMoreThan;
    	}
    	if (prodType != null && !prodType.equals("")) {
    		sql += " and a.prod_type in " + prodType;
    	}
    	
    	// 排序
    	String orderBySql = BaseDao.getOrderSql(orderByMap);
    	sql = sql + orderBySql;
    	
    	return sql;
    }    
    
    /**
     * 查看产品
     * @param id
     * @return
     */
    public static Product getProduct(int id) {
    	return Product.dao.findById(id);
    }

    /**
     * 查看产品
     * @param id 
     * @return
     */
    public static Product getProductDetail(int id) {
    	Product product = Product.dao.findById(id);
    	
    	// 产品是否预售
    	String pre_sale_status = "正常售卖";
    	Date now = new Date();
    	if (product.getIsPreSale() == 1 && now.compareTo(product.getPreStartTime()) < 0) {
    		pre_sale_status = "未开始预售";
    	} else if (product.getIsPreSale() == 1 
			&& now.compareTo(product.getPreStartTime()) >= 0
			&& now.compareTo(product.getPreEndTime()) <= 0) {
    		pre_sale_status = "预售中";
    	}
    	product.put("pre_sale_status", pre_sale_status);
    	
    	Supplier supplier = Supplier.dao.findById(product.getSupplierId());
    	product.put("supplier_name", supplier.getName());
    	
    	if(product.getId() == 0) {
    		product.put("shopType", 3);
    	} else {
    		Shop shop = Shop.dao.findById(product.getId());
    		if(shop == null) {
    			product.put("shopType", 3);
    		} else {
    			product.put("shopType", shop.getShopType());
    		}
    	}
    	
    	//商品主图
    	if(product.getMainPic() != null) {
    		Resource res = Resource.dao.findById(product.getMainPic());
    		String mainPic = (res != null) ? res.getPath() : "";
			product.put("mainPic", mainPic);
		} else {
			product.put("mainPic","");
		}
    	
    	//商品图片
    	List<String> pics = new ArrayList<String>();
    	List<Resource> recs = Resource.dao.find("select * from resource where category = ? and type = ? and relate_id = ?", 1, 1, product.getId());
    	for (Resource item : recs) {
    		pics.add(item.getPath());
    	}
    	product.put("pics", pics);
    	
    	List<ProductPrice> prices = ProductPrice.dao.find("select * from product_price where product_id = ?", product.getId());
    	if(product.getProdType() == 1) {
    		List<ProductHotel> hotels = ProductHotel.dao.find("select * from product_hotel where product_id = ?", product.getId());
    		
    		product.put("hotels", hotels);
    	}
    	product.put("productPrices", prices);
    	
    	//商品总库存
    	if(prices.size() > 0) {
    		int storeAmount = 0;
    		for(ProductPrice p : prices) {
    			storeAmount += p.getProductNumber();
    		}
    		product.setStoreAmount(storeAmount);
    	}
    	
    	// 该产品是否参加秒杀
    	int isSeckill = isSeckill(product);
    	product.setIsSeckill(isSeckill);
    	if (isSeckill == 1) {
    		product.setSuggestedRetailUnitPrice(product.getSecPrice());
    	}
    	
    	BigDecimal suggestedRetailUnitPrice = product.getSuggestedRetailUnitPrice();
    	product.put("actualUnitPrice", suggestedRetailUnitPrice.doubleValue());
        return product;
    }
    
    public static int isSeckill(Product product) {
    	if (product.getIsSeckill() == 1) {
    		Date startTime = product.getSecStartTime();
    		Date endTime = product.getSecEndTime();
    		Date now = new Date();
    		if (startTime.compareTo(now) < 0 && endTime.compareTo(now) > 0) {
    			return 1;
    		}
    		return 0;
    	} else {
    		return 0;
    	}
    }
    
    /**
     * 修改产品
     * @param product
     * @param attrTypeList
     * @param mainPicStr
     * @return
     */
    public ServiceCode updateProduct(Product product, String attrTypeList, String mainPicStr) {
    	if (product == null) {
    		return ServiceCode.Failed;
    	}
    	
    	product.set("updated_at", new Date());
    	
    	if (!product.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	List<ProductPrice> prices = ProductPrice.dao.find("select * from product_price where product_id = ?", product.getId());
    	for(ProductPrice item : prices) {
    		item.delete();
    	}
    	
    	if (attrTypeList != null && !attrTypeList.equals("")) {
    		createProductPrice(product.getId(), attrTypeList);
    	}
    	
    	List<Resource> resources = Resource.dao.find("select * from resource where category = ? and type = ? and relate_id = ?", 1, 1, product.getId());
    	for (Resource item : resources) {
    		item.delete();
    		FileHelper.delete(item.getPath());
    	}
    	
    	if (mainPicStr != null && !mainPicStr.equals("")) {
    		createProductResource(product.getId(), mainPicStr);
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 判断退款申请金额是否正确
     * @param productOrderId
     * @param applyCash
     * @return
     */
    private boolean checkRefundApplyCash(int id, int productOrderId, BigDecimal applyCash) {
    	return checkApplyCash(1, id, productOrderId, applyCash);
    }
    
    /**
     * 判断退货申请金额是否正确
     * @param productOrderId
     * @param applyCash
     * @return
     */
    private boolean checkReturnedApplyCash(int id, int productOrderId, BigDecimal applyCash) {
    	return checkApplyCash(2, id, productOrderId, applyCash);
    }
    
    /**
     * 判断退款申请金额是否正确
     * @param sql
     * @param productOrderId
     * @param applyCash
     * @return
     */
    private boolean checkApplyCash(int type, int id, int productOrderId, BigDecimal applyCash) {
    	String sql;
    	if (type == 1) {
    		sql = "select id, applyCash, product_order_id from refund where status in (0, 1, 3) and id != " + id +
        			" union" +
        			" select id, applyCash, product_order_id from back where status in (0, 1, 4)";
    	} else {
    		sql = "select id, applyCash, product_order_id from refund where status in (0, 1, 3)" +
        			" union" +
        			" select id, applyCash, product_order_id from back where status in (0, 1, 4) and id != " + id;
    	}
    	sql = "select * from " + "(" + sql + ") as t" + " where product_order_id = " + productOrderId;
    	
    	List<Record> lst = Db.find(sql);
    	BigDecimal totalCash = new BigDecimal(0);
    	for (Record item : lst) {
    		totalCash = totalCash.add(item.getBigDecimal("applyCash"));
    	}
    	totalCash = totalCash.add(applyCash);
    	
    	ProductOrder productOrder = getProductOrder(productOrderId);
//    	人玮并非将totalActualProductPrice视为实际支付的价格，而是将totalActualDeliveryCharge视为实际支付的钱
    	BigDecimal totalActualProductPrice = productOrder.getTotalActualDeliveryCharge();
        Boolean c = totalCash.compareTo(totalActualProductPrice) <= 0;
    	return totalCash.compareTo(totalActualProductPrice) <= 0;
    }
    
    /**
     * 判断退款运费是否合理
     * @param id
     * @param productOrderId
     * @param deliveryPrice
     * @return
     */
    private boolean checkRefundDeliveryPrice(int id, int productOrderId, BigDecimal deliveryPrice) {
    	return checkDeliveryPrice(1, id, productOrderId, deliveryPrice);
    }
    
    /**
     * 判断退货运费是否合理
     * @param id
     * @param productOrderId
     * @param deliveryPrice
     * @return
     */
    private boolean checkReturnedDeliveryPrice(int id, int productOrderId, BigDecimal deliveryPrice) {
    	return checkDeliveryPrice(2, id, productOrderId, deliveryPrice);
    }
    
    /**
     * 检测退款运费
     * @param type
     * @param id
     * @param productOrderId
     * @param deliveryPrice
     * @return
     */
    private boolean checkDeliveryPrice(int type, int id, int productOrderId, BigDecimal deliveryPrice) {
    	Order order = BaseDao.getOrderByProductOrderId(productOrderId);
    	List<Record> productOrders = Db.find("select * from product_order where order_id = ?", order.getId());
    	String whereIn = BaseDao.getWhereIn(productOrders, "id");
    	
    	String sql;
    	if (type == 1) {
    		sql = "select id, deliveryPrice, product_order_id from refund where status in (0, 1, 3) and id != " + id +
        			" union" +
        			" select id, deliveryPrice, product_order_id from back where status in (0, 1, 4)";
    	} else {
    		sql = "select id, deliveryPrice, product_order_id from refund where status in (0, 1, 3)" +
        			" union" +
        			" select id, deliveryPrice, product_order_id from back where status in (0, 1, 4) and id != " + id;
    	}
    	sql = "select * from " + "(" + sql + ") as t" + " where product_order_id in " + whereIn;
    	
    	List<Record> lst = Db.find(sql);
    	BigDecimal totalCash = new BigDecimal(0);
    	for (Record item : lst) {
    		totalCash = totalCash.add(item.getBigDecimal("deliveryPrice"));
    	}
    	totalCash = totalCash.add(deliveryPrice);
    	return order.getDeliveryPrice().compareTo(totalCash) >= 0;
    }
    
    /**
     * 审核退款单
     * @param id
     * @param status
     * @param refuseNote
     * @param operator
     * @param applyCash
     * @param deliveryPrice
     * @return
     */
    public ServiceCode auditRefund(int id, int status, String refuseNote, String operator, BigDecimal applyCash, BigDecimal deliveryPrice) {
    	final Refund model = Refund.dao.findById(id);
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	//判断退款申请金额是否合理
		Boolean a = !checkRefundApplyCash(model.getId(), model.getProductOrderId(), applyCash);
    	if (!checkRefundApplyCash(model.getId(), model.getProductOrderId(), applyCash)) {
			return ServiceCode.Validation;
		}

    	//判断退款运费是否合理
    	if (!checkRefundDeliveryPrice(model.getId(), model.getProductOrderId(), deliveryPrice)) {
			return ServiceCode.Validation;
		}
    	final int productOrderId = model.getProductOrderId();
    	//重新计算退款金额
    	BigDecimal refundCash = applyCash.add(deliveryPrice);
    	model.setRefundCash(refundCash);
    	model.setApplyCash(applyCash);
    	model.setDeliveryPrice(deliveryPrice);
    	//审核通过便原路退款
    	if (status == 1) {
    		boolean isSuccess = remit(model);
    		if (!isSuccess) {
    			return ServiceCode.Failed;
    		}
    		ProductOrder prodOrder = getProductOrder(productOrderId);
    		Order order = Order.dao.findById(prodOrder.getOrderId());
    		int payType = order.getPayType();
    		//如果是钱包支付则直接退款成功
    		if (payType == 4) {
    			status = 3;
    		}
    	}
    	//设置审核时间
		model.setRemitTime(new Date());
    	model.set("status", 3);
    	model.set("refuseNote", refuseNote);
    	model.set("operator", operator);
    	final int tmpStatus = status;
    	boolean success = Db.tx(new IAtom() {
            @Override
			public boolean run() throws SQLException {
                try {
                	model.update();
                	//审核不通过则恢复订单为正常状态 1:退款中 2：审核不通过 3：退款成功
                	if (tmpStatus == 2) {
                		Db.update("update product_order set status = 1 where id = ?", productOrderId);
                	}
                } catch (Exception e) {
                	Log.error(e.getMessage() + "退款审核失败");
                    return false;
                }
                return true;
            }
        });
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 审核退货单
     * @param id
     * @param status
     * @param refuseNote
     * @param operator
     * @param applyCash
     * @param deliveryPrice
     * @return
     */
    public ServiceCode auditReturned(int id, int status, String refuseNote, String operator, BigDecimal applyCash, BigDecimal deliveryPrice) {
    	final Back model = Back.dao.findById(id);
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	//判断退款申请金额是否合理
    	if (!checkReturnedApplyCash(model.getId(), model.getProductOrderId(), applyCash)) {
			return ServiceCode.Validation;
		}
    	
    	//判断退款运费是否合理
    	if (!checkReturnedDeliveryPrice(model.getId(), model.getProductOrderId(), deliveryPrice)) {
			return ServiceCode.Validation;
		}
    	
    	//重新计算退款金额
    	final int productOrderId = model.getProductOrderId();
    	BigDecimal refundCash = applyCash.add(deliveryPrice);
    	model.setRefundCash(refundCash);
    	model.setApplyCash(applyCash);
    	model.setDeliveryPrice(deliveryPrice);
    	
    	//0待审核；1退款中；2审核不通过；3已收货；4退款成功；5退款失败
    	//审核通过则原路打回退款金额
    	if (status == 1) {
    		boolean isSuccess = remit(model);
    		
    		if (!isSuccess) {
    			return ServiceCode.Failed;
    		}
    		
    		ProductOrder prodOrder = getProductOrder(productOrderId);
    		Order order = Order.dao.findById(prodOrder.getOrderId());
    		
    		int payType = order.getPayType();
    		
    		//如果是钱包支付则直接退款成功
    		if (payType == 4) {
    			status = 3;
    		}
    	}
    	
    	//设置打款时间
		model.setRemitTime(new Date());
    	model.set("status", 4);
    	model.set("refuseNote", refuseNote);
    	model.set("operator", operator);
    	
    	final int tmpStatus = status;
    	boolean success = Db.tx(new IAtom() {
            @Override
			public boolean run() throws SQLException {
                try {
                	model.update();
                	if (tmpStatus == 2) {
                		Db.update("update product_order set status = ? where id = ?", 1,productOrderId);
                	}
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.error(e.getMessage() + ",申请退货失败");
                    return false;
                }
                return true;
            }
        });
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 审核不通过则恢复订单为正常状态
     * @param productOrderId
     * @return
     */
    public ServiceCode recoverProductOrder(int productOrderId) {
    	ProductOrder model = ProductOrder.dao.findById(productOrderId);
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	model.setStatus(1);
    	if (!model.update()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }

    /**
     * 打款
     * @param refund
     * @return
     */
    private boolean remit(Refund refund) {
    	int productOrderId = refund.getProductOrderId();
    	ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    	
    	if (productOrder == null) {
    		return false;
    	}
    	
    	int orderId = productOrder.getOrderId();
    	Order order = Order.dao.findById(orderId);
    	
    	if (order == null) {
    		return false;
    	}
    	
    	int codeType = order.getCodeType();
    	//商户订单号
    	String outTradeNo = (codeType == 1) ? order.getTheSameOrderNum() : order.getOrderCode();
    	/*//订单来源，1pc、2微信端、3android、4苹果
    	int source = order.getSource();*/
    	//支付类型,1: 微信支付, 2: 支付宝, 3银联支付，4钱包支付
    	int payType = order.getPayType();
    	//退款单号
    	String outRefundNo = refund.getRefundCode();
    	//订单总金额(单位为分)
    	int totalFee = (int) (order.getTotalPayable().doubleValue() * 100);
    	//退款金额(单位为分)
    	int refundFee = (int) (refund.getRefundCash().doubleValue() * 100);
    	//交易流水号
    	String origQryId = order.getTradeNo();
    	//退款人id
    	int customerId = order.getCustomerId();
    	
    	if (payType == 1) {	 //微信退款 
    		WxRefundHelper refundHelper = new WxRefundHelper();
    		refundHelper.refundPc(outTradeNo, outRefundNo, totalFee, refundFee, 1);
    		refund = Refund.dao.findFirst("select * from refund where refund_code = ?", outRefundNo);
//			测试退款单号：1577003603957  表为refund，id为336---》吴同岳
//    		if (refund.getStatus() == 1) {
//    			return true;
//    		}
    		
    		refundHelper.refundAndroid(outTradeNo, outRefundNo, totalFee, refundFee, 1);
    		refund = Refund.dao.findFirst("select * from refund where refund_code = ?", outRefundNo);
    		
    		if (refund.getStatus() == 1) {
    			return true;
    		}
    		
    		return false;
    	} else if (payType == 2) {  //支付宝退款
    		AlipayRefundHelper alipayRefundHelper = new AlipayRefundHelper();
    		
    		return alipayRefundHelper.refund(outTradeNo, refundFee*0.01, outRefundNo);
    	} else if (payType == 3) {  //银联退款
    		UnionRefundHelper unionRefundHelper = new UnionRefundHelper();
    		
    		return unionRefundHelper.refund(origQryId, refundFee+"", outRefundNo, "");
    	}  else if (payType == 4) {  //余额退款
    		WalletRefundHelper walletRefundHelper = new WalletRefundHelper();
    		
    		return walletRefundHelper.refund(customerId, productOrderId, refundFee * 0.01);
    	}
    	
    	return true;
    }
    
    /**
     * 打款
     * @param back
     * @return
     */
    private boolean remit(Back back) {
    	int productOrderId = back.getProductOrderId();
    	ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    	
    	if (productOrder == null) {
    		return false;
    	}
    	
    	int orderId = productOrder.getOrderId();
    	Order order = Order.dao.findById(orderId);
    	
    	if (order == null) {
    		return false;
    	}
    	
    	int codeType = order.getCodeType();
    	//商户订单号
    	String outTradeNo = (codeType == 1) ? order.getTheSameOrderNum() : order.getOrderCode();
    	/*//订单来源，1pc、2微信端、3android、4苹果
    	int source = order.getSource();*/
    	//支付类型,1: 微信支付, 2: 支付宝, 3银联支付，4钱包支付
    	int payType = order.getPayType();
    	//退款单号
    	String outRefundNo = back.getRefundCode();
    	//订单总金额
    	int totalFee = (int) (order.getTotalPayable().doubleValue() * 100);
    	//退款金额
    	int refundFee = (int) (back.getRefundCash().doubleValue() * 100);
    	//交易流水号
    	String origQryId = order.getTradeNo();
    	//退款人id
    	int customerId = order.getCustomerId();
    	
    	//微信退款 0待审核；1退款中；2审核不通过；3退款成功；4退款失败
    	if (payType == 1) { //微信支付
    		WxRefundHelper refundHelper = new WxRefundHelper();
    		
    		refundHelper.refundPc(outTradeNo, outRefundNo, totalFee, refundFee, 2);
    		
    		back = Back.dao.findFirst("select * from back where refund_code = ?", outRefundNo);
    		
    		if (back.getStatus() == 1) {
    			return true;
    		}
    		
    		refundHelper.refundAndroid(outTradeNo, outRefundNo, totalFee, refundFee, 2);
    		
    		back = Back.dao.findFirst("select * from back where refund_code = ?", outRefundNo);
    		
    		if (back.getStatus() == 1) {
    			return true;
    		}
    		
    		return false;
    	} else if (payType == 2) {  //支付宝退款
    		AlipayRefundHelper alipayRefundHelper = new AlipayRefundHelper();
    		
    		return alipayRefundHelper.refund(outTradeNo, refundFee*0.01, outRefundNo);
    	} else if (payType == 3) {  //银联退款
    		UnionRefundHelper unionRefundHelper = new UnionRefundHelper();
    		
    		return unionRefundHelper.refund(origQryId, refundFee+"", outRefundNo, "");
    	} else if (payType == 4) {  //余额退款
    		WalletRefundHelper walletRefundHelper = new WalletRefundHelper();
    		
    		return walletRefundHelper.refund(customerId, productOrderId, refundFee * 0.01);
    	}
    	
    	return true;
    }

    /**
     * 发货
     * @param orderId 
     * @param expressCode 
     * @param expressName 
     * @return
     */
    public ServiceCode delivery(int orderId, String deliveryCompany, String deliveryCompanyNum, String trackingNumber) {
    	Order order = Order.dao.findById(orderId);
    	
    	if(order == null){
    		return ServiceCode.Failed;
    	}
    	
    	if(order.getStatus() != 2){
    		return ServiceCode.Failed;
    	}
    	
    	order.set("status", 3);
    	order.set("expressCode", trackingNumber);
    	order.set("logisticsName", deliveryCompany);
    	order.set("logisticsNum", deliveryCompanyNum);
    	order.setSendOutTime(new Date());
    	
    	if (!order.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	//发货事件
    	OrderParam orderParam = new OrderParam();
    	orderParam.setOrder(order);
    	eventRole.dispatch(EventEnum.EVENT_DISPATCH, orderParam);
    	
    	return ServiceCode.Success;
    }

	public ServiceCode refundUpdate(final Refund refund) {
		final int status = refund.getStatus();
		final ProductOrder productOrder = ProductOrder.dao.findById(refund.getProductOrderId());
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					if (status == 2) {  // 审核不通过
						productOrder.setStatus(1);
					} else if (status == 1) {  // 审核通过
						productOrder.setStatus(4);
					}
					
					productOrder.update();
					refund.update();
					
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	public ServiceCode backdUpdate(final Back back) {
		final int status = back.getStatus();
		final ProductOrder productOrder = ProductOrder.dao.findById(back.getProductOrderId());
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					if (status == 2) {  // 审核不通过
						productOrder.setStatus(1);
					} else if (status == 1) {  // 审核通过
						productOrder.setStatus(4);
					}
					
					productOrder.update();
					back.update();
					
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 计算店铺的总销售量和销售额
	 * @param shopId
	 * @return
	 */
	public static Record calculateShopSales(int shopId) {
		String sql = "select po.* from `order` as o" +
				" left join product_order as po on o.id = po.order_id" +
				" where o.status not in (1, 6)" +
				" and o.shop_id = " + shopId;
		
		List<Record> list = Db.find(sql);
		
		int totalSaleAmount = 0;
		BigDecimal totalSalePrice = new BigDecimal(0);
		Record result = new Record();
		
		for (Record item : list) {
			totalSaleAmount += item.getInt("unitOrdered");
			totalSalePrice = totalSalePrice.add(item.getBigDecimal("actualUnitPrice"));
		}
		
		result.set("totalSaleAmount", totalSaleAmount);
		result.set("totalSalePrice", totalSalePrice);
		return result;
	}

	/**
	 * 统计退货数量和退款数量
	 * @param shopId
	 * @return
	 */
	public static Record countRefundAndBack(int shopId) {
		int refunds = Refund.dao.find("select * from refund where shop_id = ?", shopId).size();
		int backs = Back.dao.find("select * from back where shop_id = ?", shopId).size();
		
		Record result = new Record();
		result.set("refunds", refunds);
		result.set("backs", backs);
		
		return result;
	}
	
	public static Map<String, Double> getHotelPrice(Date start, Date end, Integer id) {
		String startDate = DateHelper.formatDate(start);
		String dayAndDay = "";
		double price = 0;
		double originUnitPrice = 0;
		double unitCost = 0;
		String sql = "select * from product_hotel where product_id=? and DATE_FORMAT(start_at,'%Y-%m-%d')<=? and DATE_FORMAT(end_at,'%Y-%m-%d')>=?";
		int day = DateHelper.differentDays(start, end);
		//按天数匹配数据库中的记录并累计价格
		Map<String, Double> map = new HashMap<>();
		for(int i = 0; i<day; i ++) {
			if(i == 0) {
				dayAndDay = startDate;
			}else {
				dayAndDay = DateHelper.addDay(dayAndDay, 1);
			}
			List<ProductHotel> date = ProductHotel.dao.find(sql, id, dayAndDay, dayAndDay);
			if (date.size() > 0) {
				price += date.get(0).getPrice().doubleValue();
				originUnitPrice +=  date.get(0).getOriginUnitPrice().doubleValue();
				unitCost +=  date.get(0).getUnitCost().doubleValue();
			} else {
				map.put("error", 1.0);
				return map;
			}
		}
		map.put("price", price);
		map.put("unitCost", unitCost);
		map.put("originUnitPrice", originUnitPrice);
		return map;
	}
}