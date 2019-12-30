package com.eshop.coupon;

import java.sql.SQLException;
import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.log.Log;
import com.eshop.model.Category;
import com.eshop.model.Coupon;
import com.eshop.model.CouponProduct;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 优惠券基类
 */
public abstract class CouponService {
	
	public static final int COUPON_DISCOUNT = 1;
	public static final int COUPON_CASH = 2;
	public static final int BASEON_ALL = 1;
	public static final int BASEON_PRODUCT = 2;
	public static final int BASEON_CATEGORY = 3;
	public static final int SCOPE_ALL = 1;
	public static final int SCOPE_SHOP = 2;

    /**
     * 计算优惠金额
     * @param couponId 
     * @param totalPayable 
     * @return
     */
    public abstract double calculateDiscount(int couponId, double totalPayable);
    
    /**
     * 计算优惠金额
     * @param couponId
     * @param products
     * @return
     */
    public abstract double calculateDiscount(int couponId, List<Record> products);
    
    /**
     * 计算符合条件的购买金额
     * @param couponId
     * @param products
     * @return
     */
    protected static double getCouponTotalPayable(int couponId, List<Record> products) {
    	Coupon coupon = Coupon.dao.findById(couponId);
		int scope = coupon.getScope();
		int baseOn = coupon.getBaseOn();
		
		double totalPayable = 0;
		
		if (scope == SCOPE_ALL && baseOn == BASEON_ALL) {
			totalPayable = getTotalPrice(products);
		} else if (scope == SCOPE_ALL && baseOn == BASEON_CATEGORY) {
			totalPayable = getTotalPriceCategory(coupon, products);
		} else if (scope == SCOPE_ALL && baseOn == BASEON_PRODUCT) {
			totalPayable = getTotalPrice(coupon, products);
		} else if (scope == SCOPE_SHOP && baseOn == BASEON_ALL) {
			totalPayable = getShopTotalPrice(coupon, products);
		} else if (scope == SCOPE_SHOP && baseOn == BASEON_CATEGORY) {
			totalPayable = getShopCategoryTotalPrice(coupon, products);
		} else if (scope == SCOPE_SHOP && baseOn == BASEON_PRODUCT) {
			totalPayable = getShopSpecialTotalPrice(coupon, products);
		}
		
		return totalPayable;
    }
    
    /**
     * 查看该产品参与了哪些优惠券活动
     * 算法：1、先找出所有优惠券；2、再逐个筛选出符合条件的优惠券
     * @param productId
     * @return
     */
    public static List<Coupon> getCouponSlogans(int productId) {
    	Date now = new Date();
    	List<Coupon> coupons = Coupon.dao.find("select * from coupon where startDate <= ? and endDate >= ?", now, now);
    	
    	Product product = Product.dao.findById(productId);
    	List<Coupon> result = new ArrayList<Coupon>();
    	
    	for (Coupon coupon : coupons) {
    		int baseOn = coupon.getBaseOn();
    		int scope = coupon.getScope();
    		if (scope == SCOPE_ALL && baseOn == BASEON_ALL) {
    			if (isMeetSlogan1(coupon, product)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_ALL && baseOn == BASEON_CATEGORY) {
    			if (isMeetSloganAllCategory(coupon, product)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_ALL && baseOn == BASEON_PRODUCT) {
    			if (isMeetSlogan2(coupon, product)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_SHOP && baseOn == BASEON_ALL) {
    			if (isMeetSlogan3(coupon, product)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_SHOP && baseOn == BASEON_CATEGORY) {
    			if (isMeetSloganShopCategory(coupon, product)) {
					result.add(coupon);
				}
			} else if (scope == SCOPE_SHOP && baseOn == BASEON_PRODUCT) {
    			if (isMeetSlogan4(coupon, product)) {
					result.add(coupon);
				}
			}
    	}
    	
    	return result;
    }
    
    private static boolean isMeetSlogan1(Coupon coupon, Product product) {
    	return true;
    }
    
    private static boolean isMeetSloganAllCategory(Coupon coupon, Product product) {
    	return productIsInCategories(coupon, product);
    }
    
    private static boolean isMeetSlogan2(Coupon coupon, Product product) {
    	int count = CouponProductService.countCouponProductItems(coupon.getId(), CouponProductService.TYPE_PRODUCT, product.getId(), null);
    	return count > 0;
    }
    
    private static boolean isMeetSlogan3(Coupon coupon, Product product) {
    	if (coupon.getShopId() == product.getShopId()) {
			return true;
		} else {
			return false;
		}
    }
    
    private static boolean isMeetSloganShopCategory(Coupon coupon, Product product) {
    	if (coupon.getShopId() != product.getShopId()) {
			return false;
		}
    	
    	return productIsInCategories(coupon, product);
    }
    
    private static boolean productIsInCategories(Coupon coupon, Product product) {
    	List<Record> couponProducts = CouponProductService.findCouponProductItems(coupon.getId(), BASEON_CATEGORY, null, null);
    	
    	int cateId = product.getCategoryId();
    	CategoryService categoryService = new CategoryService();
    	
    	for (Record item : couponProducts) {
    		int objectId = item.getInt("objectId");
    		List<Category> categories = categoryService.getAllChild(objectId);
    		List<Integer> cateIds = CategoryService.getCateIds(categories);
    		
    		if (cateIds.contains(cateId)) {
				return true;
			}
    	}
    	
    	return false;
    }
    
    private static boolean isMeetSlogan4(Coupon coupon, Product product) {
    	if (coupon.getShopId() != product.getShopId()) {
			return false;
		}
    	
    	int count = CouponProductService.countCouponProductItems(coupon.getId(), CouponProductService.TYPE_PRODUCT, product.getId(), null);
    	
    	return count > 0;
    }

    /**
     * 下单时满足优惠条件的优惠券
     * 算法：1、找出用户已领取的优惠券（未使用并在有效期之内）；2、遍历集合，找出符合条件的优惠券
     * @param products [{product_id:1,amount:3,price:8},...], 整个订单的产品列表
     * @return
     */
    public static List<Coupon> coupons(int customerId, List<Record> products) {
    	Date now = new Date();
    	
    	//找出用户已领取的优惠券
    	List<Coupon> list = Coupon.dao.find("select b.* from customer_coupon as a left join coupon as b on a.couponId = b.id where a.customerId = ? and a.isUsed = ? and b.startDate <= ? and b.endDate >= ?", customerId, CustomerCouponService.NO_USED, now, now);
    	System.out.println(list);
    	
    	//筛选出符合条件的优惠券
    	List<Coupon> result = new ArrayList<Coupon>();
    	for (Coupon coupon : list) {
    		int baseOn = coupon.getBaseOn();
    		int scope = coupon.getScope();
    		if (scope == SCOPE_ALL && baseOn == BASEON_ALL) {
    			if (isMeet1(coupon, products)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_ALL && baseOn == BASEON_CATEGORY) {
    			if (isMeetAllCategory(coupon, products)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_ALL && baseOn == BASEON_PRODUCT) {
    			if (isMeet2(coupon, products)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_SHOP && baseOn == BASEON_ALL) {
    			if (isMeet3(coupon, products)) {
					result.add(coupon);
				}
    		} else if (scope == SCOPE_SHOP && baseOn == BASEON_CATEGORY) {
    			if (isMeetShopCategory(coupon, products)) {
					result.add(coupon);
				}
			} else if (scope == SCOPE_SHOP && baseOn == BASEON_PRODUCT) {
    			if (isMeet4(coupon, products)) {
					result.add(coupon);
				}
			}
    	}
    	
        return result;
    }
    
    /**
     * 计算总购买金额
     * @param products
     * @return
     */
    private static double getTotalPrice(List<Record> products) {
    	double totalPrice = 0;
    	for (Record item : products) {
    		totalPrice += item.getDouble("price") * item.getInt("amount");
    	}
    	return totalPrice;
    }
    
    /**
     * 计算符合优惠券的特定品类的购买金额
     * @param coupon
     * @param products
     * @return
     */
    private static double getTotalPrice(Coupon coupon, List<Record> products) {
    	List<CouponProduct> couponProducts = CouponProduct.dao.find("select * from coupon_product where couponId = ? and type = ?", coupon.getId(), CouponProductService.TYPE_PRODUCT);
    	
    	List<Record> list = new ArrayList<Record>();
    	for (Record item1 : products) {
    		int productId = item1.getInt("product_id");
    		for (CouponProduct couponProduct : couponProducts) {
    			int objectId = couponProduct.getObjectId();
    			if (productId == objectId) {
    				list.add(item1);
    			}
    		}
    	}
    	
    	double totalPrice = getTotalPrice(list);
    	
    	return totalPrice;
    }
    
    /**
     * 计算符合优惠券的特定分类的购买金额
     * @param coupon
     * @param products
     * @return
     */
    private static double getTotalPriceCategory(Coupon coupon, List<Record> products) {
    	List<CouponProduct> couponProducts = CouponProduct.dao.find("select * from coupon_product where couponId = ? and type = ?", coupon.getId(), CouponProductService.TYPE_CATEGORY);
    	
    	CategoryService categoryService = new CategoryService();
    	
    	List<Record> list = new ArrayList<Record>();
    	
    	for (Record item1 : products) {
    		int productId = item1.getInt("product_id");
    		Product product = Product.dao.findById(productId);
    		
    		int cateId = product.getCategoryId();
    		
    		for (CouponProduct couponProduct : couponProducts) {
    			int objectId = couponProduct.getObjectId();
    			List<Category> categories = categoryService.getAllChildAndInclude(objectId);
    			List<Integer> cateIds = CategoryService.getCateIds(categories);
    			
    			if (cateIds.contains(cateId)) {
    				list.add(item1);
    			}
    		}
    	}
    	
    	double totalPrice = getTotalPrice(list);
    	
    	return totalPrice;
    }
    
    /**
     * 全场全品类
     * 算法：购买金额只需满足优惠金额即可
     * @param coupon
     * @param products
     */
    private static boolean isMeet1(Coupon coupon, List<Record> products) {
    	double totalPrice = getTotalPrice(products);
    	double full = coupon.getFull().doubleValue();
    	
    	if (totalPrice < full) {
			return false;
		} else {
			return true;
		}
    }
    
    /**
     * 全场特定分类
     * 算法：订单中特定品类的购买金额是否满足优惠金额
     * @param coupon
     * @param products
     * @return
     */
    private static boolean isMeetAllCategory(Coupon coupon, List<Record> products) {
    	double totalPrice = getTotalPriceCategory(coupon, products);
    	double full = coupon.getFull().doubleValue();
    	
    	if (totalPrice < full) {
			return false;
		} else {
			return true;
		}
    }
    
    /**
     * 全场特定品类
     * 算法：订单中特定品类的购买金额是否满足优惠金额
     * @param coupon
     * @param products
     * @return
     */
    private static boolean isMeet2(Coupon coupon, List<Record> products) {
    	double totalPrice = getTotalPrice(coupon, products);
    	double full = coupon.getFull().doubleValue();
    	
    	if (totalPrice < full) {
			return false;
		} else {
			return true;
		}
    }
    
    /**
     * 店铺全品类
     * 算法：购买该店铺产品的购买金额是否满足优惠金额
     * @param coupon
     * @param products
     */
    private static boolean isMeet3(Coupon coupon, List<Record> products) {
    	double totalPrice = getShopTotalPrice(coupon, products);
    	double full = coupon.getFull().doubleValue();
    	
    	if (totalPrice < full) {
			return false;
		} else {
			return true;
		}
    }
    
    /**
     * 计算购买该店铺产品的购买金额
     * @param coupon
     * @param products
     * @return
     */
    private static double getShopTotalPrice(Coupon coupon, List<Record> products) {
    	int shopId = coupon.getShopId();
    	
    	List<Record> list = new ArrayList<Record>();
    	System.out.println(products);
    	for (Record item : products) {
    		if (item.getInt("shop_id") == shopId) {
				list.add(item);
			}
    	}
    	
    	double totalPrice = getTotalPrice(list);
    	
    	return totalPrice;
    }
    
    /**
     * 店铺特定分类
     * 算法：特定店铺的特定品类的购买金额是否满足优惠金额
     * @param coupon
     * @param products
     * @return
     */
    private static boolean isMeetShopCategory(Coupon coupon, List<Record> products) {
    	double totalPrice = getShopCategoryTotalPrice(coupon, products);
    	double full = coupon.getFull().doubleValue();
    	
    	if (full < totalPrice) {
			return false;
		} else {
			return true;
		}
    }
    
    /**
     * 店铺特定品类
     * 算法：特定店铺的特定品类的购买金额是否满足优惠金额
     * @param coupon
     * @param products
     * @return
     */
    private static boolean isMeet4(Coupon coupon, List<Record> products) {
    	double totalPrice = getShopSpecialTotalPrice(coupon, products);
    	double full = coupon.getFull().doubleValue();
    	
    	if (totalPrice < full) {
			return false;
		} else {
			return true;
		}
    }
    
    /**
     * 特定店铺的特定分类的购买金额
     * @param coupon
     * @param products
     * @return
     */
    private static double getShopCategoryTotalPrice(Coupon coupon, List<Record> products) {
    	int shopId = coupon.getShopId();
    	
    	//筛选出该店铺的购买记录
    	List<Record> shopProducts = new ArrayList<Record>();
    	for (Record item : products) {
    		if (item.getInt("shop_id") == shopId) {
				shopProducts.add(item);
			}
    	}
    	
    	//筛选出满足优惠券的特定产品
    	List<Record> result = new ArrayList<Record>();
    	List<CouponProduct> couponProducts = CouponProduct.dao.find("select * from coupon_product where couponId = ? and type = ?", coupon.getId(), CouponProductService.TYPE_CATEGORY); 
    	
    	CategoryService categoryService = new CategoryService();
    	
    	for (Record item : shopProducts) {
    		int productId = item.getInt("product_id");
    		Product product = Product.dao.findById(productId);
    		int cateId = product.getCategoryId();
    		
    		for (CouponProduct couponProduct : couponProducts) {
    			int objectId = couponProduct.getObjectId();
    			List<Category> categories = categoryService.getAllChild(objectId);
    			List<Integer> cateIds = CategoryService.getCateIds(categories);
    			
    			if (cateIds.contains(cateId)) {
    				result.add(item);
    			}
    		}
    	}
    	
    	double totalPrice = getTotalPrice(result);
    	
    	return totalPrice;
    }
    
    /**
     * 特定店铺的特定品类的购买金额
     * @param coupon
     * @param products
     * @return
     */
    private static double getShopSpecialTotalPrice(Coupon coupon, List<Record> products) {
    	int shopId = coupon.getShopId();
    	
    	//筛选出该店铺的购买记录
    	List<Record> shopProducts = new ArrayList<Record>();
    	for (Record item : products) {
    		if (item.getInt("shop_id") == shopId) {
				shopProducts.add(item);
			}
    	}
    	
    	//筛选出满足优惠券的特定产品
    	List<Record> result = new ArrayList<Record>();
    	List<CouponProduct> couponProducts = CouponProduct.dao.find("select * from coupon_product where couponId = ? and type = ?", coupon.getId(), CouponProductService.TYPE_PRODUCT); 
    	for (Record item : shopProducts) {
    		int productId = item.getInt("product_id");
    		for (CouponProduct couponProduct : couponProducts) {
    			int objectId = couponProduct.getObjectId();
    			if (productId == objectId) {
    				result.add(item);
    			}
    		}
    	}
    	
    	double totalPrice = getTotalPrice(result);
    	
    	return totalPrice;
    }

    /**
     * 批量查询优惠券列表
     * @param offset
     * @param count
     * @param startCreatedAt
     * @param endCreatedAt
     * @param title
     * @param startDate
     * @param endDate
     * @param baseOn
     * @param scope
     * @param type
     * @param minFull
     * @param maxFull
     * @param minValue
     * @param maxValue
     * @param shopId
     * @param description
     * @param minAmount
     * @param maxAmount
     * @param shopName
     * @return
     */
    public static List<Record> findCouponItems(int offset, int count, String startCreatedAt, String endCreatedAt, 
    		String title, String startDate, String endDate, Integer baseOn, Integer scope, Integer type, 
    		Double minFull, Double maxFull, Double minValue, Double maxValue, Integer shopId, String description,
    		Integer minAmount, Integer maxAmount, String shopName) {
        
    	String sql = findCouponItemsSql(startCreatedAt, endCreatedAt, title, startDate, endDate, baseOn, scope, type, minFull, maxFull, minValue, maxValue, shopId, description, minAmount, maxAmount, shopName);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	List<Record> data = Db.find(sql);
    	return findCouponItems(data);
    }
    
    /**
     * 批量查询优惠券列表
     * @param startCreatedAt
     * @param endCreatedAt
     * @param title
     * @param startDate
     * @param endDate
     * @param baseOn
     * @param scope
     * @param type
     * @param minFull
     * @param maxFull
     * @param minValue
     * @param maxValue
     * @param shopId
     * @param description
     * @param minAmount
     * @param maxAmount
     * @param shopName
     * @return
     */
    public static List<Record> findCouponItems(String startCreatedAt, String endCreatedAt, 
    		String title, String startDate, String endDate, Integer baseOn, Integer scope, Integer type, 
    		Double minFull, Double maxFull, Double minValue, Double maxValue, Integer shopId, String description,
    		Integer minAmount, Integer maxAmount, String shopName) {
        
    	String sql = findCouponItemsSql(startCreatedAt, endCreatedAt, title, startDate, endDate, baseOn, scope, type, minFull, maxFull, minValue, maxValue, shopId, description, minAmount, maxAmount, shopName);
    	List<Record> data = Db.find(sql);
    	return findCouponItems(data);
    }
    
    /**
     * 给优惠券添加扩展字段
     * @param coupons
     * @return
     */
    public static List<Record> findCouponItems(List<Record> coupons) {
    	for (Record coupon : coupons) {
    		int couponId = coupon.getInt("id");
    		int receivedAmount = Db.find("select * from customer_coupon where couponId = ?", couponId).size();
    		int usedAmount = Db.find("select * from customer_coupon where couponId = ? and isUsed = ?", couponId, 1).size();
    		boolean useup = coupon.getInt("amount") <= receivedAmount;
    		coupon.set("receivedAmount", receivedAmount);
    		coupon.set("usedAmount", usedAmount);
    		coupon.set("useup", useup);
    	}
    	return coupons;
    }

    /**
     * 批量查询优惠券列表的总条数
     * @param startCreatedAt
     * @param endCreatedAt
     * @param title
     * @param startDate
     * @param endDate
     * @param baseOn
     * @param scope
     * @param type
     * @param minFull
     * @param maxFull
     * @param minValue
     * @param maxValue
     * @param shopId
     * @param description
     * @param minAmount
     * @param maxAmount
     * @param shopName
     * @return
     */
    public static int countCouponItems(String startCreatedAt, String endCreatedAt, 
    		String title, String startDate, String endDate, Integer baseOn, Integer scope, Integer type, 
    		Double minFull, Double maxFull, Double minValue, Double maxValue, Integer shopId, String description,
    		Integer minAmount, Integer maxAmount, String shopName) {
        
    	String sql = findCouponItemsSql(startCreatedAt, endCreatedAt, title, startDate, endDate, baseOn, scope, type, minFull, maxFull, minValue, maxValue, shopId, description, minAmount, maxAmount, shopName);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param startCreatedAt
     * @param endCreatedAt
     * @param title
     * @param startDate
     * @param endDate
     * @param baseOn
     * @param scope
     * @param type
     * @param minFull
     * @param maxFull
     * @param minValue
     * @param maxValue
     * @param shopId
     * @param description
     * @param minAmount
     * @param maxAmount
     * @param shopName
     * @return
     */
    public static String findCouponItemsSql(String startCreatedAt, String endCreatedAt, 
    		String title, String startDate, String endDate, Integer baseOn, Integer scope, Integer type, 
    		Double minFull, Double maxFull, Double minValue, Double maxValue, Integer shopId, String description,
    		Integer minAmount, Integer maxAmount, String shopName) {
        
    	String sql = "select * from coupon where id != 0";
    	
    	if (startCreatedAt != null && !startCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d') >= '" + startCreatedAt + "'";
		}
    	if (endCreatedAt != null && !endCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d') <= '" + endCreatedAt + "'";
		}
    	if (title != null && !title.equals("")) {
			sql += " and title like '%" + title + "%'";
		}
    	if (startDate != null && !startDate.equals("")) {
			sql += " and DATE_FORMAT(startDate, '%Y-%m-%d') <= '" + startDate + "'";
		}
    	if (endDate != null && !endDate.equals("")) {
			sql += " and DATE_FORMAT(endDate, '%Y-%m-%d') >= '" + endDate + "'";
		}
    	if (baseOn != null) {
			sql += " and baseOn = " + baseOn;
		}
    	if (scope != null) {
			sql += " and scope = " + scope;
		}
    	if (type != null) {
			sql += " and type = " + type;
		}
    	if (minFull != null) {
			sql += " and full >= " + minFull;
		}
    	if (maxFull != null) {
			sql += " and full <= " + maxFull;
		}
    	if (minValue != null) {
			sql += " and value >= " + minValue;
		}
    	if (maxValue != null) {
			sql += " and value <= " + maxValue;
		}
    	if (shopId != null) {
			sql += " and shopId = " + shopId;
		}
    	if (description != null && !description.equals("")) {
			sql += " and description like '%" + description + "%'";
		}
    	if (minAmount != null) {
			sql += " and amount >= " + minAmount;
		}
    	if (maxAmount != null) {
			sql += " and amount <= " + maxAmount;
		}
    	if (shopName != null && !shopName.equals("")) {
			sql += " and shopName like '%" + shopName + "%'";
		}
    	
    	return sql;
    }

    /**
     * 查看优惠券详情
     * @param id 
     * @return
     */
    public static Coupon getCoupon(int id) {
        return Coupon.dao.findById(id);
    }

    /**
     * 创建优惠券
     * @param model 
     * @return
     */
    public static ServiceCode createCoupon(Coupon model) {
        if (model == null) {
			return ServiceCode.Failed;
		}
        
        model.setCreatedAt(new Date());
        model.setUpdatedAt(new Date());
        
        if (!model.save()) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
    }

    /**
     * 修改优惠券
     * @param model 
     * @return
     */
    public static ServiceCode updateCoupon(Coupon model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
        
        if (!model.update()) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
    }

    /**
     * 删除优惠券
     * 算法：如果该优惠券已被领取或使用，则不允许删除；如果还没被领取或使用，则允许删除
     * @param id 
     * @return
     */
    public static ServiceCode deleteCoupon(final int id) {
    	int amount = Db.find("select * from customer_coupon where couponId = ?", id).size();
    	
    	if (amount > 0) {
			return ServiceCode.Validation;
		}
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from coupon_product where couponId = ?", id);
					Db.update("delete from coupon where id = ?", id);
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",删除优惠券模板失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
}