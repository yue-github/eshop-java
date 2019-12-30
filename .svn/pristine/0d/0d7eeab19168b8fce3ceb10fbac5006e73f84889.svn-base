package com.eshop.service;

import java.sql.SQLException;
import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.content.ResourceService;
import com.eshop.log.Log;
import com.eshop.model.Category;
import com.eshop.model.Customer;
import com.eshop.model.Property;
import com.eshop.model.PropertyValue;
import com.eshop.model.Resource;
import com.eshop.model.Shop;
import com.eshop.model.Supplier;
import com.eshop.model.SupplierContract;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 
 */
public class Manager extends User {

    /**
     * 批量查询分类
     * @param offset 
     * @param count 
     * @param cateId 
     * @param name 
     * @return
     */
    public static List<Record> findCategoryItems(int offset, int count, Integer cateId, String name, 
    		Integer parentId, Integer sortNumber, Integer isDelete, Map<String, String> orderByMap) {
    	
    	String sql = findCategoryItemsSql(cateId, name, parentId, sortNumber, isDelete, orderByMap);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	List<Record> list = Db.find(sql);
    	list = appendCategoryItems(list);
    	
    	return list;
    }
    
    /**
     * 批量查询分类
     * @param cateId
     * @param name
     * @param parentId
     * @return
     */
    public static List<Record> findCategoryItems(Integer cateId, String name, Integer parentId, 
    		Integer sortNumber, Integer isDelete, Map<String, String> orderByMap) {
    	
    	String sql = findCategoryItemsSql(cateId, name, parentId, sortNumber, isDelete, orderByMap);
    	List<Record> list = Db.find(sql);
    	list = appendCategoryItems(list);
    	
    	return list;
    }
    
    private static List<Record> appendCategoryItems(List<Record> list) {
    	List<Record> resources = ResourceService.getAll();
    	List<Record> categories = Db.find("select * from category");
    	
    	for (Record item : list) {
    		Record resource = BaseDao.findItem(item.getInt("mainPic"), resources, "id");
    		String mainPic = resource != null ? resource.getStr("path") : "";
    		item.set("mainPic", mainPic);
    		
    		String parentName = "顶级";
    		int parentId = item.getInt("parent_id");
    		if (parentId != 0) {
    			Record category = BaseDao.findItem(item.getInt("parent_id"), categories, "id");
        		parentName = category != null ? category.getStr("name") : "";
    		}
    		item.set("parentName", parentName);
    	}
    	
    	return list;
    }

    /**
     * 批量查询分类的总数量
     * @param offset 
     * @param count 
     * @param cateId 
     * @param name 
     * @return
     */
    public static int countCategoryItems(Integer cateId, String name, Integer parentId, 
    		Integer sortNumber, Integer isDelete) {
    	
        String sql = findCategoryItemsSql(cateId, name, parentId, sortNumber, isDelete, null);
        
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param offset
     * @param count
     * @param cateId
     * @param name
     * @return
     */
    public static String findCategoryItemsSql(Integer cateId, String name, Integer parentId, 
    		Integer sortNumber, Integer isDelete, Map<String, String> orderByMap) {
    	
    	String sql = "select * from category where id != 0";
    	
    	if (cateId != null) {
			sql += " and id = " + cateId;
		}
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	if (parentId != null) {
    		List<Category> cates = new CategoryService().getAllChildAndInclude(parentId);
    		String whereIn = CategoryService.getWhereInIds(cates);
			sql += " and parent_id in " + whereIn;
		}
    	if (sortNumber != null) {
			sql += " and sortNumber = " + sortNumber;
		}
    	if (isDelete != null) {
			sql += " and isDelete = " + isDelete;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }

    /**
     * 查看分类
     * @param id
     * @return
     */
    public static Category getCategory(int id) {
    	return Category.dao.findById(id);
    }

    /**
     * 创建分类
     * @param model
     * @param mainPic
     * @return
     */
    public static ServiceCode createCategory(final Category model, final String mainPic) {
    	boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					model.set("created_at", new Date());
			    	model.set("updated_at", new Date());
			    	model.save();
			    	
			    	if (mainPic != null && !mainPic.equals("")) {
			    		Resource res = new Resource();
			        	res.setCategory(10);
			        	res.setType(1);
			        	res.setPath(mainPic);
			        	res.setRelateId(model.getId());
			        	res.setCreatedAt(new Date());
			        	res.setUpdatedAt(new Date());
			        	res.save();
			        	
			        	model.setMainPic(res.getId());
			        	model.update();
			    	}
				} catch (Exception e) {
					return false;
				}
				
				return true;
			}
    	});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 修改分类
     * @param model
     * @param mainPic
     * @return
     */
    public static ServiceCode updateCategory(final Category model, final String mainPic) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					model.update();
					if (mainPic == null || mainPic.equals("")) {
			    		model.setMainPic(0);
			    		model.update();
			    	} else {
			    		Resource res = ResourceService.get(model.getMainPic());
				    	if (res != null) {
				    		res.setPath(mainPic);
				    		res.update();
				    	} else {
				    		int resId = ResourceService.insertResource(mainPic, model.getId(), 
				    				ResourceService.CATEGORY, ResourceService.PICTURE);
				    		
				        	model.setMainPic(resId);
				        	model.update();
				    	}
			    	}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",修改分类失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    public static ServiceCode deleteCategory(int id) {
    	Category model = Category.dao.findById(id);
    	
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	CategoryService categoryService = new CategoryService();
    	List<Category> list = categoryService.getAllChildAndInclude(id);
    	
    	String whereInIds = CategoryService.getWhereInIds(list);
    	String sql = "update category set isDelete = 1 where id in " + whereInIds;
    	Db.update(sql);
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 批量删除分类
     * @param id
     * @return
     */
    public static ServiceCode batchDeleteCategory(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						ServiceCode code = deleteCategory(id);
						if (code != ServiceCode.Success) {
							return false;
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",批量删除分类错误");
					return false;
				}
				
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 批量查询属性
     * @param offset 
     * @param count 
     * @param cateId 
     * @param name 
     * @param isSale 
     * @return
     */
    public static List<Record> findPropertyItems(int offset, int count, Integer cateId, String name, 
    		Integer isSale, Integer sortNumber, Integer parentId, Map<String, String> orderByMap, String categoryName) {
    	
        String sql = findPropertyItemsSql(cateId, name, isSale, sortNumber, parentId, orderByMap, categoryName);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        return Db.find(sql);
    }

    /**
     * 批量查询属性的总数量
     * @param offset 
     * @param count 
     * @param cateId 
     * @param name 
     * @param isSale 
     * @return
     */
    public static int countPropertyItems(Integer cateId, String name, Integer isSale, Integer sortNumber, 
    		Integer parentId) {
    	
        String sql = findPropertyItemsSql(cateId, name, isSale, sortNumber, parentId, null);
        return Db.find(sql).size();
    }
    
    public static String findPropertyItemsSql(Integer cateId, String name, Integer isSale, Integer sortNumber, 
    		Integer parentId, Map<String, String> orderByMap){
    	return findPropertyItemsSql(cateId, name, isSale, sortNumber, parentId, orderByMap, null);
    }
    /**
     * 组装sql语句
     * @param cateId
     * @param name
     * @param isSale
     * @param sortNumber
     * @param parentId
     * @param orderByMap
     * @return
     */
    public static String findPropertyItemsSql(Integer cateId, String name, Integer isSale, Integer sortNumber, 
    		Integer parentId, Map<String, String> orderByMap, String cateName) {
    	
    	String sql = "select a.*, b.name as categoryName from property as a" +
    			" left join category as b on a.category_id = b.id" +
    			" where a.isDelete = 0";
    	
    	if (cateId != null) {
    		List<Category> cates = new CategoryService().getAllChildAndInclude(cateId);
    		String whereIn = CategoryService.getWhereInIds(cates);
    		sql += " and a.category_id in " + whereIn;
    	}
    	if (name != null && !name.equals("")) {
			sql += " and a.name like '%" + name + "%'";
		}
    	if (isSale != null) {
			sql += " and a.is_sale_pro = " + isSale;
		}
    	if (sortNumber != null) {
			sql += " and a.sortNumber = " + sortNumber;
		}
    	if (parentId != null) {
			sql += " and a.parent_id = " + parentId;
		}
    	if (cateName != null && !cateName.equals("")) {
    		List<Category> cates = new CategoryService().getAllChildAndInclude(cateName);
    		String whereIn = CategoryService.getWhereInIds(cates);
    		System.out.println(whereIn);
    		sql += " and a.category_id in " + whereIn;
    	}
    		
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	System.out.println(sql);
    	
    	return sql;
    }

    /**
     * 查看属性
     * @param id
     * @return
     */
    public static Property getProperty(int id) {
    	return Property.dao.findById(id);
    }

    /**
     * 创建属性
     * @param model
     * @return
     */
    public static ServiceCode createProperty(Property model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	model.set("created_at", new Date());
    	model.set("updated_at", new Date());
    	if(!model.save()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }

    /**
     * 修改属性
     * @param model
     * @return
     */
    public static ServiceCode updateProperty(Property model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
     	if(!model.update()){
     		return ServiceCode.Failed;
     	}
     	return ServiceCode.Success;
    }

    /**
     * 删除属性
     * @param id
     * @return
     */
    public static ServiceCode deleteProperty(final int id) {
    	boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
		    		Db.update("update property set isDelete = 1 where id = ?", id);
		    		Db.update("update property_value set isDelete = 1 where property_id = ?", id);
		        	return true;
		    	} catch (Exception e) {
		    		Log.error(e.getMessage() + ",删除属性失败");
		    		return false;
		    	}
			}
    	});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 批量删除属性
     * @param ids
     * @return
     */
    public static ServiceCode batchDeleteProperty(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						deleteProperty(id);
					}
		    	} catch (Exception e) {
		    		Log.error(e.getMessage() + ",删除属性失败");
		    		return false;
		    	}
				return true;
			}
    	});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 批量查询会员
     * @param offset 
     * @param count 
     * @param name 
     * @param gender 
     * @param phone 
     * @param email 
     * @param nickName 
     * @param weiXinOpenId 
     * @param mobilePhone 
     * @return
     */
    public static List<Record> findCustomerItems(int offset, int count, String name, Integer gender, 
    		String phone, String email, String nickName, Map<String, String> orderByMap, String weiXinOpenId, String mobilePhone) {
    	
        String sql = findCustomerItemsSql(name, gender, phone, email, nickName, orderByMap, weiXinOpenId, mobilePhone);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        return Db.find(sql);
    }

    /**
     * 查询会员的总数量
     * @param offset 
     * @param count 
     * @param name 
     * @param gender 
     * @param phone 
     * @param email 
     * @param nickName 
     * @param weiXinOpenId 
     * @param mobilePhone 
     * @return
     */
    public static int countCustomerItems(String name, Integer gender, String phone, String email, 
    		String nickName, String weiXinOpenId, String mobilePhone) {
    	
        String sql = findCustomerItemsSql(name, gender, phone, email, nickName, null, weiXinOpenId, mobilePhone);
        return Db.find(sql).size();
    }
    
    
    public static String findCustomerItemsSql(String name, Integer gender, String phone, String email, 
    		String nickName, Map<String, String> orderByMap) {
    	return findCustomerItemsSql(name, gender, phone, email, nickName, orderByMap, null, null);
    }
    /**
     * 组装sql语句
     * @param name
     * @param gender
     * @param phone
     * @param email
     * @param nickName
     * @param mobilePhone 
     * @return
     */
    public static String findCustomerItemsSql(String name, Integer gender, String phone, String email, 
    		String nickName, Map<String, String> orderByMap, String weiXinOpenId, String mobilePhone) {
    	
    	String sql = "select * from customer where id != 0";
    	
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	if (gender != null) {
			sql += " and gender = " + gender;
		}
    	if (phone != null && !phone.equals("")) {
			sql += " and mobilePhone like '%" + phone + "%'";
		}
    	if (email != null && !email.equals("")) {
			sql += " and email like '%" + email + "%'";
		}
    	if (nickName != null && !nickName.equals("")) {
			sql += " and nickName like '%" + nickName + "%'";
		}
    	if (weiXinOpenId != null && !weiXinOpenId.equals("")) {
			sql += " and weiXinOpenId != 'null'";
		}
    	if (mobilePhone != null && !mobilePhone.equals("")) {
			sql += " and mobilePhone != 'null'";
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
    
    /**
     * 修改会员状态
     * @param id
     * @param targetState
     * @return
     */
    private static ServiceCode changeCustomerState(int id, int targetState) {
    	Customer customer = Customer.dao.findById(id);
        if (customer == null) {
			return ServiceCode.Failed;
		}
        customer.setDisable(targetState);
        customer.update();
        return ServiceCode.Success;
    }

    /**
     * 禁用会员
     * @param id
     */
    public static ServiceCode forbiddenCustomer(int id) {
        return changeCustomerState(id, 1);
    }

    /**
     * 激活会员
     * @param id
     */
    public static ServiceCode activate(int id) {
    	return changeCustomerState(id, 0);
    }

    /**
     * 审核店铺
     * @param id
     * @param status
     * @return
     */
    public static ServiceCode auditShop(int id, int status) {
        Shop shop = Shop.dao.findById(id);
        
        if (shop == null) {
			return ServiceCode.Failed;
		}
        
        shop.setStatus(status);
        shop.update();
        
        return ServiceCode.Success;
    }

    /**
     * 禁用店铺
     * @param id
     */
    public static ServiceCode forbiddenShop(int id) {
        return auditShop(id, 3);
    }
    
    /**
     * 激活店铺
     * @param id
     * @return
     */
    public static ServiceCode activateShop(int id) {
        return auditShop(id, 1);
    }
    
   /* public static List<Record> findShopItems(int offset, int count, String name, String contactas, 
    		String phone, Integer status, Integer shopType, Map<String, String> orderByMap) {
    	return findShopItems(offset, count, name, contactas, phone, status, shopType, orderByMap, null, null);
    }*/
    /**
     * 批量查询店铺
     * @param offset 
     * @param count 
     * @param name 
     * @param contactas 
     * @param phone 
     * @param status 
     * @return
     */
    public static List<Record> findShopItems(int offset, int count, String name, String contactas, 
    		String phone, Integer status, Integer shopType, Map<String, String> orderByMap ,String startTime ,String endTime) {
    	
        String sql = findShopItemsSql(name, contactas, phone, status, shopType, orderByMap, startTime, endTime);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        return Db.find(sql);
    }
    
    public static List<Record> findShopItems(String name, String contactas, String phone, 
    		Integer status, Integer shopType, Map<String, String> orderByMap) {
    	
        String sql = findShopItemsSql(name, contactas, phone, status, shopType, orderByMap);
        return Db.find(sql);
    }
    
    public static int countShopItems(String name, String contactas, String phone, 
    		Integer status, Integer shopType) {
    	return countShopItems(name, contactas, phone, status, shopType, null, null);
    }
    /**
     * 批量查询店铺的总数量
     * @param offset 
     * @param count 
     * @param name 
     * @param contactas 
     * @param phone 
     * @param status 
     * @return
     */
    public static int countShopItems(String name, String contactas, String phone, 
    		Integer status, Integer shopType ,String startTime ,String endTime) {
    	
        String sql = findShopItemsSql(name, contactas, phone, status, shopType, null,startTime,endTime);
        return Db.find(sql).size();
    }
    
    public static String findShopItemsSql(String name, String contactas, String phone, Integer status, 
    		Integer shopType, Map<String, String> orderByMap) {
    	return findShopItemsSql(name, contactas, phone, status, shopType, orderByMap, null, null);
    }
    
    /**
     * 组装sql语句
     * @param name
     * @param contactas
     * @param phone
     * @param status
     * @return
     */
    public static String findShopItemsSql(String name, String contactas, String phone, Integer status, 
    		Integer shopType, Map<String, String> orderByMap,String startTime ,String endTime) {
    	
    	String sql = "select * from shop where id != 0";
    	
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	if (contactas != null && !contactas.equals("")) {
			sql += " and contacts like '%" + contactas + "%'";
		}
    	if (phone != null && !phone.equals("")) {
			sql += " and phone like '%" + phone + "%'";
		}
    	if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d  %H') >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(updated_at, '%Y-%m-%d  %H') <= '" + endTime + "'";
		}
    	if (status != null) {
    		if (status == -1) {
				sql += " and status != " + 0;
			} else {
				sql += " and status = " + status;
			} 
    	}
    		
    	if (shopType != null) {
			sql += " and shopType = " + shopType;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }

    /**
     * 查看店铺
     * @param id 
     * @return
     */
    public Shop getShop(int id) {
        return Shop.dao.findById(id);
    }

    /**
     * 创建供应商
     * @param model
     * @return
     */
    public static ServiceCode createSupplier(Supplier model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	if (!model.save()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }
    
    /**
     * 判读账号是否已存在
     * @param bankAccount
     * @return
     */
    public static ServiceCode isExetBankAccount(String bankAccount) {
    	int count = Supplier.dao.find("select * from supplier where bankAccount = ?", bankAccount).size();
    	return count > 0 ? ServiceCode.Validation : ServiceCode.Success;
    }
    
    /**
     * 判读供应商名称是否已存在
     * @param name
     * @return
     */
    public static ServiceCode isExetSupplierName(String name) {
    	int count = Supplier.dao.find("select * from supplier where name = ?", name).size();
    	return count > 0 ? ServiceCode.Validation : ServiceCode.Success;
    }

    /**
     * 查看供应商
     * @param id 
     * @return
     */
    public static Supplier getSupplier(int id) {
        return Supplier.dao.findById(id);
    }
    
    /**
     * 根据供应商名查看供应商
     * @param name
     * @return
     */
    public static Record getSupplierByName(String supplierName) {
    	Record supplier = Db.findFirst("select * from supplier where name = ? ", supplierName);
    	return supplier;
    }

    /**
     * 修改供应商
     * @param model
     * @return
     */
    public static ServiceCode updateSupplier(Supplier model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	model.setUpdatedAt(new Date());
    	if (!model.update()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }

    /**
     * 批量查询供应商
     * @param offset 
     * @param count 
     * @param name 
     * @param phone 
     * @return
     */
    public static List<Record> findSupplierItems(int offset, int count, String name, String phone, 
    		String zipcode, Integer type, String contactPerson, Map<String, String> orderByMap) {
    	
        String sql = findSupplierItemsSql(name, phone, zipcode, type, contactPerson, orderByMap);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        List<Record> list = Db.find(sql);
        list = setSupplierItem(list);
        return list;
    }
    
    public static List<Record> findSupplierItems(String name, String phone, String zipcode, Integer type, 
    		String contactPerson, Map<String, String> orderByMap) {
    	
        String sql = findSupplierItemsSql(name, phone, zipcode, type, contactPerson, orderByMap);
        List<Record> list = Db.find(sql);
        list = setSupplierItem(list);
        return list;
    }
    
    private static List<Record> setSupplierItem(List<Record> list) {
    	List<Record> provinces = Db.find("select * from s_province");
    	List<Record> cities = Db.find("select * from s_city");
    	List<Record> districts = Db.find("select * from s_district");
    	for (Record item : list) {
    		Record province = BaseDao.findItem(item.getInt("province_id"), provinces, "id");
    		String str_province = province != null ? province.getStr("name") : "";
    		Record city = BaseDao.findItem(item.getInt("city_id"), cities, "id");
    		String str_city = city != null ? city.getStr("name") : "";
    		int districtId = item.getNumber("district_id") != null ? item.getInt("district_id") : 0;
    		Record district = BaseDao.findItem(districtId, districts, "id");
    		String str_district = district != null ? district.getStr("name") : "";
    		item.set("province", str_province);
    		item.set("city", str_city);
    		item.set("district", str_district);
    	}
    	return list;
    }

    /**
     * 批量查询供应商的总数量
     * @param offset 
     * @param count 
     * @param name 
     * @param phone 
     * @return
     */
    public static int countSupplierItem(String name, String phone, String zipcode, Integer type, 
    		String contactPerson) {
    	
        String sql = findSupplierItemsSql(name, phone, zipcode, type, contactPerson, null);
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param name
     * @param phone
     * @param zipcode
     * @param type
     * @param contactPerson
     * @return
     */
    public static String findSupplierItemsSql(String name, String phone, String zipcode, Integer type, 
    		String contactPerson, Map<String, String> orderByMap) {
    	
    	String sql = "select * from supplier where id != 0";
    	
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	if (phone != null && !phone.equals("")) {
			sql += " and (phone1 like '%" + phone + "%'" +
    				" or phone2 like '%" + phone + "%'" +
    				" or phone3 like '%" + phone + "%')";
		}
    	if (zipcode != null && !zipcode.equals("")) {
			sql += " and zipcode like '%" + zipcode + "%'";
		}
    	if (type != null) {
			sql += " and type = " + type;
		}
    	if (contactPerson != null && !contactPerson.equals("")) {
			sql += " and contactPerson like '%" + contactPerson + "%'";
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;	
    }
    
    /**
     * 根据供应商ID获取店铺
     * @param supplierId
     * @param shopName
     * @return
     */
    public static List<Record> getShopsBySupplierId(int supplierId, String shopName) {
    	List<Record> products = Db.find("select shop_id from product where supplier_id = ? group by shop_id", supplierId);
    	String shopIds = BaseDao.getWhereIn(products, "shop_id");
    	
    	String select = "select id, name from shop" +
    			" where id in " + shopIds;
    	
    	if (shopName != null) {
    		select += " and name like '%" + shopName + "%'";
    	}
    	
    	return Db.find(select);
    }

    /**
     * 删除供应商
     * @param id
     * @return
     */
    public static ServiceCode deleteSupplier(int id) {
        if (!Supplier.dao.deleteById(id)) {
			return ServiceCode.Failed;
		}
        
        return ServiceCode.Success;
    }
    
    /**
     * 批量删除供应商
     * @param ids
     * @return
     */
    public static ServiceCode batchDeleteSupplier(final List<String> ids) {
        boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						deleteSupplier(id);
					}
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
     * 批量查询属性值
     * @param name
     * @param propId
     * @param sortNumber
     * @param isDelete
     * @param orderByMap
     * @return
     */
    public static List<Record> findPropValueItems(String name, Integer propId, 
    		Integer sortNumber, Integer isDelete, Map<String, String> orderByMap) {
    	
        String sql = findPropValueItemsSql(name, propId, sortNumber, isDelete, orderByMap);
        
        return Db.find(sql);
    }

    /**
     * 批量查询属性值
     * @param offset
     * @param count
     * @param name
     * @param propId
     * @return
     */
    public static List<Record> findPropValueItems(int offset, int count, String name, Integer propId, 
    		Integer sortNumber, Integer isDelete, Map<String, String> orderByMap) {
    	
        String sql = findPropValueItemsSql(name, propId, sortNumber, isDelete, orderByMap);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        return Db.find(sql);
    }

    /**
     * 批量查询属性值的总数量
     * @param offset
     * @param count
     * @param name
     * @param propId
     * @return
     */
    public static int countPropValueItems(String name, Integer propId, Integer sortNumber, Integer isDelete) {
    	
        String sql = findPropValueItemsSql(name, propId, sortNumber, isDelete, null);
        return Db.find(sql).size();
        
    }
    
    /**
     * 组装sql语句
     * @param name
     * @param propId
     * @return
     */
    public static String findPropValueItemsSql(String name, Integer propId, Integer sortNumber, 
    		Integer isDelete, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, b.name as propertyName from property_value as a" +
    			" left join property as b on a.property_id = b.id" + 
    			" where a.id != 0";
    	
    	if (name != null && !name.equals("")) {
			sql += " and a.name like '%" + name + "%'";
		}
    	if (propId != null) {
			sql += " and a.property_id = " + propId;
		}
    	if (sortNumber != null) {
			sql += " and a.sortNumber = " + sortNumber;
		}
    	if (isDelete != null) {
			sql += " and a.isDelete = " + isDelete;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }

    /**
     * 查看属性值
     * @param id
     * @return
     */
    public static PropertyValue getPropValue(int id) {
        return PropertyValue.dao.findById(id);
    }

    /**
     * 创建属性值
     * @param model
     * @return
     */
    public static ServiceCode createPropValue(PropertyValue model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	if(!model.save()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }

    /**
     * 修改属性值
     * @param model
     * @return
     */
    public static ServiceCode updatePropValue(PropertyValue model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
     	if(!model.update()){
     		return ServiceCode.Failed;
     	}
     	return ServiceCode.Success;
    }

    /**
     * 删除属性值
     * @param id
     * @return
     */
    public static ServiceCode deletePropValue(int id) {
    	PropertyValue model = PropertyValue.dao.findById(id);
    	
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	model.setIsDelete(1);
    	model.update();
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 批量删除属性值
     * @param ids
     * @return
     */
    public static ServiceCode batchDeletePropValue(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						deletePropValue(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ", 批量删除属性值失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 批量查询供应商合同
     * @param offset
     * @param length
     * @param contractId
     * @param supplierId
     * @param accountPeriod
     * @return
     */
    public static List<Record> findContractItems(int offset, int length, Integer contractId, Integer supplierId, Integer accountPeriod) {
    	
    	String sql = findContractItemsSql(contractId, supplierId, accountPeriod);
    	sql = BaseDao.appendLimitSql(sql, offset, length);
    	
    	return Db.find(sql);
    }
    
    /**
     * 获取某个供应商合同
     * @param id
     * @return
     */
    public static SupplierContract getSupplierContract(Integer contractId) {
    	return SupplierContract.dao.findById(contractId);
    }
    
    /**
     * 根据供应商ID获取合同
     * @param supplierId
     * @return
     */
    public static List<Record> getContractBySupplierId(int supplierId) {
    	
    	String sql = findContractItemsSql(null, supplierId, null);
    	
    	return Db.find(sql);
    }
    
    /**
     * 创建供应商合同
     */
    public static ServiceCode createSupplierContract(final SupplierContract model) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					model.save();
					Supplier supplier = Supplier.dao.findById(model.getSupplierId());
					supplier.setSupplierContractId(model.getId());
					supplier.update();
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 修改供应商合同
     * @param model
     * @return
     */
    public static ServiceCode updateSupplierContract(SupplierContract model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	if (!model.update()) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }
    
    /**
     * 组装sql语句
     * @param contractId
     * @param supplierId
     * @param accountPeriod
     * @return
     */
    public static String findContractItemsSql(Integer contractId, Integer supplierId, Integer accountPeriod) {
    	
    	String sql = "select a.*, b.name as supplierName from supplier_contract as a" +
    			" left join supplier as b on a.supplier_id = b.id" + 
    			" where a.id != 0";
    	if (contractId != null) {
    		sql += " and a.id = " + contractId;
    	}
    	if (supplierId != null) {
    		sql += " and supplier_id = " + supplierId;
    	}
    	if (accountPeriod != null) {
    		sql += " and account_period = " + accountPeriod;
    	}
    	
    	return sql;
    }

    /**
     * 获取成交用户
     * @param offset
     * @param length
     * @param email 
     * @param phone 
     * @return
     */
	public static List<Record> findCustomerItemsByOrder(Integer offset, Integer length, String phone, String email) {
		String sql = findCustomerItemsByOrdersql(phone, email);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		
		for (Record item : list) {
			item.set("nickName", item.getStr("receiveName"));
			item.set("mobilePhone", item.getStr("preferredContactPhone"));
		}
		
        return list;
	}
	
	/**
	 * 组装sql语句
	 * @param email 
	 * @param phone 
	 * @param offset
	 * @param length
	 * @return
	 */
	public static String findCustomerItemsByOrdersql(String phone, String email){
		String sql = "select c.*,o.`status`,o.receiveName,o.preferredContactPhone from customer as c" +
    			" left join `order` as o on c.id = o.customer_id" + 
    			" where c.id != 0 and o.status NOT IN(1,6)";
		if (phone != null && !phone.equals("")) {
			sql += " and o.preferredContactPhone like '%" + phone + "%'";
		}
    	if (email != null && !email.equals("")) {
			sql += " and c.email like '%" + email + "%'";
		}
    	sql = sql + " GROUP BY preferredContactPhone";
		
		return sql;
	}

	public static int countCustomerItemsByOrder(String phone, String email) {
		String sql = findCustomerItemsByOrdersql(phone, email);
		return Db.find(sql).size();
	}

}