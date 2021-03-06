package com.eshop.coupon;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.eshop.content.ResourceService;
import com.eshop.log.Log;
import com.eshop.model.*;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * 
 */
public class CouponProductService {
	
	public final static int TYPE_PRODUCT = 2;
	public final static int TYPE_CATEGORY = 1;

    /**
     * 批量查询优惠券适用品类
     * @param offset
     * @param count
     * @param couponId
     * @param type
     * @param name
     * @return
     */
    public static List<Record> findCouponProductItems(int offset, int count, Integer couponId, Integer type, Integer objectId, String name) {
        String sql = findCouponProductSql(couponId, type, objectId, name);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        List<Record> data = Db.find(sql);
        return appendToCouponProductItems(data);
    }
    
    /**
     * 批量查询优惠券适用品类
     * @param couponId
     * @param type
     * @param objectId
     * @param name
     * @return
     */
    public static List<Record> findCouponProductItems(Integer couponId, Integer type, Integer objectId, String name) {
        String sql = findCouponProductSql(couponId, type, objectId, name);
        List<Record> data = Db.find(sql);
        return appendToCouponProductItems(data);
    }
    
    private static List<Record> appendToCouponProductItems(List<Record> data) {
    	for (Record item : data) {
    		String mainPic = ResourceService.getPathByResId(item.getInt("mainPic"));
    		item.set("mainPic", mainPic);
    	}
    	return data;
    }

    /**
     * 批量查询优惠券适用品类的总数量
     * @param couponId
     * @param type
     * @param objectId
     * @param name
     */
    public static int countCouponProductItems(Integer couponId, Integer type, Integer objectId, String name) {
        String sql = findCouponProductSql(couponId, type, objectId, name);
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param couponId
     * @param type
     * @param name
     * @return
     */
    private static String findCouponProductSql(Integer couponId, Integer type, Integer objectId, String name) {
    	String sql = "select * from coupon_product where id != 0";
    	
    	if (couponId != null) {
			sql += " and couponId = " + couponId;
		}
    	
    	if (objectId != null) {
			sql += " and objectId = " + objectId;
		}
    	
    	if (type != null) {
			sql += " and type = " + type;
		}
    	
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	
    	return sql;
    }

    /**
     * 查看优惠券适用品类详情
     * @param id 优惠券适用品类id
     * @return
     */
    public static CouponProduct getCouponProduct(int id) {
        CouponProduct couponProduct = CouponProduct.dao.findById(id);
        
        String mainPic = ResourceService.getPathByResId(couponProduct.getMainPic());
        couponProduct.put("image", mainPic);
        
        return couponProduct;
    }

    /**
     * 创建优惠券适用品类
     * @param model 
     * @return
     * 修改2018-04-26： 查找商品时 type==2。 if (type == 1){...} else{....} 改为 if (type == 2){...} else{....}
     */
    public static ServiceCode createCouponProduct(CouponProduct model) {
        if (model == null) {
			return ServiceCode.Failed;
		}
        
        int mainPic;
        String name;
        
        int type = model.getType();
        int objectId = model.getObjectId();
        
        if (type == 2) {
        	Product product = Product.dao.findById(objectId);
        	name = product.getName();
        	mainPic = product.getMainPic();
        } else {
        	Category category = Category.dao.findById(objectId);
        	name = category.getName();
        	mainPic = category.getMainPic();
        }
        
        model.setName(name);
        model.setMainPic(mainPic);
        model.setCreatedAt(new Date());
        model.setUpdatedAt(new Date());
        model.save();
        
        return ServiceCode.Success;
    }

    /**
     * 修改优惠券使用品类
     * @param model 
     * @return
     */
    public static ServiceCode updateCouponProduct(CouponProduct model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
        
        int mainPic;
        String name;
        
        int type = model.getType();
        int objectId = model.getObjectId();
        
        if (type == 1) {
        	Product product = Product.dao.findById(objectId);
        	name = product.getName();
        	mainPic = product.getMainPic();
        } else {
        	Category category = Category.dao.findById(objectId);
        	name = category.getName();
        	mainPic = category.getMainPic();
        }
        
        model.setName(name);
        model.setMainPic(mainPic);
        model.setCreatedAt(new Date());
        model.setUpdatedAt(new Date());
        model.update();
        
        return ServiceCode.Success;
    }

    /**
     * 删除优惠券适用品类
     * @param id 
     * @return
     */
    public static ServiceCode deleteCouponProduct(int id) {
        if (!CouponProduct.dao.deleteById(id)) {
			return ServiceCode.Failed;
		}
        
        return ServiceCode.Success;
    }
    
    /**
     * 查询没有添加在某优惠券的所有商品
     * @param offset 开始页数 1
     * @param length 条数
     * @param couponId 优惠券id
     */
    public static Page<Product> getProductsNoThisCoupon(int offset, int length, Integer couponId, 
    		Integer shopId, String productName) {
    	
    	String select = "select p.id, p.name, r.path AS mainPic";
    	String sqlExceptSelect = "FROM product AS p LEFT JOIN resource AS r "
    			+ " ON p.mainPic=r.id WHERE p.shop_id=" + shopId
    			+ " AND p.is_sale = 1"
    			+ " AND p.isDelete = 0"
    			+ " AND p.id NOT IN ("
    			+ " SELECT objectId FROM coupon_product AS cp"
    			+ " WHERE cp.type=2  AND cp.couponId=" + couponId + ")";
    	
    	if (productName != null && !productName.equals("")) {
    		sqlExceptSelect += " AND p.name like '%" + productName + "%'";
    	}
    	
    	Page<Product> page = Product.dao.paginate(offset, length, select, sqlExceptSelect);
    	
    	return page;
	}
    
    /**
	 * 批量创建优惠券产品
	 * @param couponId 优惠券Id
	 * @param type 1分类， 2产品
	 * @param objectIds 1,2,...
	 * @return
	 */
	public static ServiceCode batchCreateCouponProducts(final Integer type, final Integer couponId, String objectIds) {
        
       final  String[] ids = objectIds.split(",");
        boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				
				int mainPic = 0;
			    String name = null;
				
				for (int i = 0; i < ids.length; i++) {
					
					int objectId = Integer.parseInt(ids[i]);
					
					if (type == 2) {
						Product product = Product.dao.findById(objectId);
						name = product.getName();
						mainPic = product.getMainPic();
					} else {
						Category category = Category.dao.findById(objectId);
						name = category.getName();
						mainPic = category.getMainPic();
					}
					CouponProduct model = new CouponProduct();
					model.setObjectId(objectId);
					model.setType(type);
					model.setCouponId(couponId);
					model.setName(name);
					model.setMainPic(mainPic);
					model.setCreatedAt(new Date());
					model.setUpdatedAt(new Date());
					if(!model.save()) {
						return false;
					}
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
		
	}

	/**
	 * 查询优惠卷列表
	 * @return
	 */
	public static List<Record> listCoupon(Integer offset,Integer length){

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String date = df.format(new Date());
		String sql = "select a.*,b.* from coupon as a LEFT JOIN " +
				     "coupon_product as b on a.id = b.couponId where startDate<='"+date+"' and endDate>='"+date+"'";

		sql = BaseDao.appendLimitSql(sql, offset, length);
		return Db.find(sql);
	}

	/**
	 *
	 * 查询个人可用优惠卷
	 * @return
	 */
	public static List<Record> listCustomerCoupon(Integer customerId){

		String sql = "select * from customer_coupon where customerId = ? and isUsed = ?";
		return Db.find(sql,customerId,0);
	}

	/**
	 *修改优惠卷使用状态
	 * @param customerId
	 * @param couponId
	 * @return
	 */
	public static ServiceCode updateCustomerCoupon(final Integer customerId,final Integer couponId){

		boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				try {

					 String sql = "select * from customer_coupon where customerId =? and couponId=?";
					 Record record = Db.findFirst(sql,customerId,couponId);
					 if(record!=null){

					 	Db.update("update customer_coupon set isUsed=? where customerId =? and couponId=? ",1,customerId,couponId);
					 }

				}catch (Exception e){
					Log.error(e.getMessage() + "修改优惠卷失败");
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}

	/**
	 *
	 * 根据customerid查询 判断是否已经领取优惠卷
	 *0：未领取 1：已领取
	 * @param args
	 */
	public static Integer listCustCoupon(Integer customerId,Integer couponId){

		String sql = "select * from customer_coupon where customerId =? and couponId=?";
		return Db.find(sql,customerId,couponId).size();
	}


	/**
	 * 领取优惠卷
	 * @param customerId couponId
	 */
	public static ServiceCode saveCustomerCoupon(final Integer customerId,final Integer couponId,final String phone){

		boolean success = Db.tx(new IAtom(){
			@Override
			public boolean run() throws SQLException {
				try {

					CustomerCoupon customerCoupon = new CustomerCoupon();
					customerCoupon.setCustomerId(customerId);
					customerCoupon.setCouponId(couponId);
					customerCoupon.setCreatedAt(new Date());
					customerCoupon.setUpdatedAt(new Date());
					customerCoupon.setIsUsed(0);
					customerCoupon.setUseTime(new Date());
					customerCoupon.setCode("11111");
					customerCoupon.setPhone(phone);
					customerCoupon.save();
				}catch (Exception e){
					Log.error(e.getMessage() + ",添加优惠卷失败");
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}

}