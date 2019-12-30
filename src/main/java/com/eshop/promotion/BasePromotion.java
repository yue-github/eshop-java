package com.eshop.promotion;

import java.util.*;

import com.eshop.category.CategoryService;
import com.eshop.content.ContentService;
import com.eshop.content.ResourceService;
import com.eshop.helper.DateHelper;
import com.eshop.helper.SortHelper;
import com.eshop.model.Category;
import com.eshop.model.Product;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionCondition;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 促销基础类
 */
public class BasePromotion {
	
	public final static int SCOPE_ALL = 1;
	public final static int SCOPE_SHOP = 2;
	public final static int BASEON_ALL = 1;
	public final static int BASEON_PRODUCT = 2;
	public final static int BASEON_CATEGORY = 3;
	public final static int BASEON_SKU = 4;
	public final static int MANJIAN = 1;
	public final static int DAZHE = 2;
	public final static int MANSONG = 3;
	public final static int MIAOSHA = 4;
	public final static int PINTUANG = 5;
	public final static int BAOYOU = 6;
	public final static int KANJIA= 8;

	
	/**
	 * 查看促销活动基础信息
     * @return
     */
    public static Promotion getPromotion(int id) {
    	return Promotion.dao.findById(id);
    }
//    通过shop_id获取促销砍价内容-WuTongYue
	public static <T>T getPromotionByShopId(int shopId,int type) {
    	Date date = new Date();
		return (T)Db.find("select p.*, k.* from promotion p inner join promotion_kanjia k on p.id = k.promotion_id where p.startDate <= ? and p.endDate >= ? and p.type in (?) and p.baseOn in (?) and p.shop_id in (?)", date, date, type, BASEON_ALL, shopId);
	}

    public static List<Promotion> getPromotions() {
    	Date date = new Date();
    	String type = MANJIAN+","+DAZHE+","+MANSONG+","+MIAOSHA;
    	List<Promotion> list = Promotion.dao.find("select * from promotion where startDate <= ? and endDate >= ? and type in (?)", date, date, type);
    	for (Promotion item : list) {
    		int mainPic = item.getMainPic() != null ? item.getMainPic() : 0;
    		String path = ResourceService.getPath(mainPic);
    		item.put("promPic", path);
    	}
    	return list;
    }
    
    /**
     * 找出产品参与了哪些促销活动
     * 算法：1、先找出所有促销活动；2、遍历集合，找出符合条件的促销活动；
     * @param offset
     * @param length
     * @param productId
     * @return
     */
    public static List<Promotion> geProductPromSlogans(int productId) {
    	Product product = Product.dao.findById(productId);
    	List<Promotion> promotions = Promotion.dao.find("select * from promotion where type != ? and type != ? and startDate <= ? and endDate >= ?", MIAOSHA, PINTUANG, new Date(), new Date());
    	
    	List<Promotion> result = new ArrayList<Promotion>();
    	for (Promotion promotion : promotions) {
    		int scope = promotion.getScope();
    		int baseOn = promotion.getBaseOn();
    		if (SCOPE_SHOP == scope && BASEON_ALL == baseOn) {
    			if (getPromSlogansWithShopAll(promotion, product)) {
					result.add(promotion);
				}
    		} else if (SCOPE_SHOP == scope && BASEON_CATEGORY == baseOn) {
    			if (getPromSlogansWithShopCategory(promotion, product)) {
					result.add(promotion);
				}
    		} else if (SCOPE_SHOP == scope && BASEON_PRODUCT == baseOn) {
    			if (getPromSlogansWithShopProduct(promotion, product)) {
					result.add(promotion);
				}
    		}
    	}
    	//限制输出数据条数
//    	List<Promotion> pageList = new ArrayList<>();
//    	//限制量：（0 + 1） * 8 = 8；
//    	int limitLength = (offset + 1) * length;
//    	if (result.size() < limitLength) {
//    		pageList = result;
//		} else {
//			for (int i = offset; i < limitLength; i++) {
//				pageList.add(result.get(i));
//			}
//		}
    	return result;
    }
    
    private static boolean getPromSlogansWithShopAll(Promotion promotion, Product product) {
    	if (promotion.getShopId() != product.getShopId()) {
			return false;
		} else {
			return true;
		}
    }
    
    private static boolean getPromSlogansWithShopCategory(Promotion promotion, Product product) {
    	if (promotion.getShopId() != product.getShopId()) {
			return false;
		}
    	
    	List<PromotionCondition> promotionConditions = PromotionCondition.dao.find("select * from promotion_condition where promotion_id = ? and type = ? and object_id = ?", promotion.getId(), PromotionConditionService.TYPE_CATEGORY);
    	for (PromotionCondition promotionCondition : promotionConditions) {
    		int objectId = promotionCondition.getObjectId();
    		int categoryId = product.getCategoryId();
    		if (objectId == categoryId) {
				return true;
			}
    	}
    	
    	return false;
    }
    
    private static boolean getPromSlogansWithShopProduct(Promotion promotion, Product product) {
    	if (promotion.getShopId() != product.getShopId()) {
			return false;
		}
    	
    	List<PromotionCondition> promotionConditions = PromotionCondition.dao.find("select * from promotion_condition where promotion_id = ? and object_id and type = ? ", promotion.getId(), PromotionConditionService.TYPE_PRODUCT);
    	for (PromotionCondition promotionCondition : promotionConditions) {
    		int objectId = promotionCondition.getObjectId();
    		int productId = product.getId();
    		if (objectId == productId) {
				return true;
			}
    	}
    	
    	return false;
    }
    
    /**
     * 查看促销活动关联产品
     * @param promotionId
     * @return
     */
    public static List<Product> getPromotProducts(int offset, int length, int promotionId) {
    	Promotion promotion = Promotion.dao.findById(promotionId);
    	if(promotion != null) {
	    	int type = promotion.getType();
	    	
	    	if (type == MIAOSHA || type == PINTUANG) {
				return getPromotSkus(offset, length, promotionId);
			} else {
				return getPromotConditions(offset, length, promotionId);
			}
    	}
		return Collections.emptyList();
    }
    
    private static List<Product> getPromotSkus(int offset, int length, int promotionId) {
    	List<Product> list = Product.dao.find("select b.* from promotion_sku as a left join product as b on a.product_id = b.id and b.is_sale = ? and isDelete = ? limit ?, ?", 1, 0, offset, length);
    	for (Product item : list) {
    		String mainPic = ResourceService.getPathByResId(item.getMainPic());
    		item.set("mainPic", mainPic);
    	}
    	return list;
    }
    
    private static List<Product> getPromotConditions(int offset, int length, int promotionId) {
    	Promotion promotion = Promotion.dao.findById(promotionId);
    	
    	int baseOn = promotion.getBaseOn();
    	int shopId = promotion.getShopId();
    	
    	List<Product> list = new ArrayList<Product>();
    	System.out.println("baseOn="+baseOn);
    	
    	if (baseOn == BASEON_ALL) {
    		list = Product.dao.find("select * from product where is_sale = ? and isDelete = ? and shop_id = ? limit ?, ?", 1, 0, shopId, offset, length);
    	} else if (baseOn == BASEON_CATEGORY) {
    		CategoryService categoryService = new CategoryService();
    		
    		List<PromotionCondition> promotionConditions = PromotionCondition.dao.find("select * from promotion_condition where promotion_id = ? and type = ?", promotionId, PromotionConditionService.TYPE_CATEGORY);
    		
    		for (PromotionCondition item : promotionConditions) {
    			List<Category> categories = categoryService.getAllChildAndInclude(item.getObjectId());
    			String whereIn = CategoryService.getWhereInIds(categories);
    			list = Product.dao.find("select * from product where is_sale = ? and isDelete = ? and shop_id = ? and category_id in ? limit ?, ?", 1, 0, shopId, whereIn, offset, length);
    		}
    	} else if (baseOn == BASEON_PRODUCT) {
    		list = Product.dao.find("select b.* from promotion_condition as a left join product as b on a.object_id = b.id where a.promotion_id = ? and a.type = ? and b.is_sale=? and b.isDelete = ? limit ?, ?", promotionId, PromotionConditionService.TYPE_PRODUCT, 1, 0, offset, length);
    	}
    	
    	for (Product item : list) {
    		String mainPic = ResourceService.getPathByResId(item.getMainPic());
    		item.set("mainPic", mainPic);
    	}
    	
    	return list;
    }
    
    /**
     * 购物车列表，每个店铺满足条件的促销活动
     * @param shopId
     * @param products [{product_id:1, price_id:4, amount:2, price:5, shop_id:3},...]
     * @return [{id:1,title:促销活动标题,isMeet:false,discount:33},...]；isMeet表示金额是否满足，如果满足
     * 则不需要凑单，不满足则需要凑单；discount表示优惠金额
     */
    public static List<Record> getShopCartPromotion(int shopId, List<Record> products) {
    	// 找出符合条件的活动
    	Date now = new Date();
    	List<Record> manjians = Db.find("select a.*, b.full, b.value from promotion as a left " +
				"join promotion_manjian as b on a.id = b.promotion_id " +
				"where a.shop_id = ? and type=1 and a.scope = ? and" +
				" DATE_FORMAT(a.endDate,'%Y-%m-%d') >= DATE_FORMAT(NOW(),'%Y-%m-%d') " +
				"and DATE_FORMAT(a.startDate,'%Y-%m-%d') <= DATE_FORMAT(NOW(),'%Y-%m-%d')" +
				" order by b.full desc", shopId, SCOPE_SHOP);
    	List<Record> dazhes = Db.find("select a.*, b.full, b.value from" +
				" promotion as a left join promotion_dazhe as b on a.id = b.promotion_id  " +
				"where a.shop_id = ? and type=2 and a.scope = ? and DATE_FORMAT(a.endDate,'%Y-%m-%d') >=" +
				" DATE_FORMAT(NOW(),'%Y-%m-%d') and DATE_FORMAT(a.startDate,'%Y-%m-%d') <= DATE_FORMAT(NOW(),'%Y-%m-%d') " +
				"order by b.full desc", shopId, SCOPE_SHOP);
    	List<Record> mansongs = Db.find("select a.*, b.full from promotion as a left join" +
				" promotion_mansong as b on a.id = b.promotion_id where a.shop_id = ? and type=3 and a.scope = ? " +
				"and DATE_FORMAT(a.endDate,'%Y-%m-%d') >= DATE_FORMAT(NOW(),'%Y-%m-%d') and DATE_FORMAT(a.startDate,'%Y-%m-%d')" +
				" <= DATE_FORMAT(NOW(),'%Y-%m-%d') order by b.full desc", shopId, SCOPE_SHOP);
    	
    	// 计算每个活动符合的购买金额，并计算差值，差值 = full - 购买金额
    	calculateDiffer(manjians, products);
    	calculateDiffer(dazhes, products);
    	calculateDiffer(mansongs, products);
    	
    	// 对活动列表按差值从小到大排序
    	sortItems(manjians, "differ");
    	sortItems(dazhes, "differ");
    	sortItems(mansongs, "differ");
    	
    	// 从每种类型的活动中挑出最优的一个活动
    	Record manjian = filter(manjians);
    	Record dazhe = filter(dazhes);
    	Record mansong = filter(mansongs);
    	
    	// 合并挑选的结果
    	List<Record> result = new ArrayList<Record>();
    	
    	if (manjian != null && dazhe != null) {
    		if (manjian.getDouble("discount") > dazhe.getDouble("discount")) {
    			result.add(manjian);
    		} else {
    			result.add(dazhe);
    		}
		} else if (manjian != null && dazhe == null) {
			result.add(manjian);
		} else if (manjian == null && dazhe != null) {
			result.add(dazhe);
		}
    	
    	if (mansong != null) {
			result.add(mansong);
		}
    	
    	return result;
    }
    
    private static Record filter(List<Record> list) {
    	return list.size() > 0 ? list.get(0) : null;
    	
//    	for (int i = 0; i < list.size() - 1; i++) {
//    		Record current = list.get(i);
//    		Record next = list.get(i + 1);
//    		
//    		double currDiffer = current.getDouble("differ");
//    		double nextDiffer = next.getDouble("differ");
//    		
//    		
//    		if (currDiffer >= 0) {
//    			return current;
//    		} else {
//    			if (nextDiffer > 0) {
//    				return current;
//    			}
//    		}
//    	}
//    	
//    	return null;
    }
    
    private static void sortItems(List<Record> list, String key) {
    	SortHelper.sortAsc(list, key);
    }
    
    private static void calculateDiffer(List<Record> list, List<Record> products) {
    	for (Record item : list) {
    		int promotionId = item.getInt("id");
    		int baseOn = item.getInt("baseOn");
    		int type = item.getInt("type");
    		double totalPayable = 0;
    		if(item.getBigDecimal("full") == null) {
    			continue;
    		}
    		double full = item.getBigDecimal("full").doubleValue();
    		double differ = 0;
    		
    		switch (baseOn) {
    		case BASEON_ALL:
    			totalPayable = getTotalPayableByBaseonAll(products);
    			break;
    		case BASEON_CATEGORY:
    			totalPayable = getTotalPayableByBaseonCategory(products, promotionId);
    			break;
    		case BASEON_PRODUCT:
    			totalPayable = getTotalPayableByBaseonProduct(products, promotionId);
    			break;
    		}
    		
    		differ = full - totalPayable;
    		
    		boolean isMeet = (differ > 0) ? false : true;
    		double discount = 0;
    		
    		// 计算优惠金额
    		if (isMeet) {
    			switch (type) {
        		case MANJIAN:
        			discount = item.getBigDecimal("value").doubleValue();
        			break;
        		case DAZHE:
        			double rate = item.getBigDecimal("value").doubleValue() * 0.01;
        			discount = totalPayable * (1 - rate);
        			break;
        		}
    		}
    		
    		item.set("discount", discount);
    		item.set("isMeet", isMeet);
    		item.set("totalPayable", totalPayable);
    		item.set("differ", differ);
    	}
    }
    
    private static double getTotalPayableByBaseonAll(List<Record> products) {
    	double totalPayable = 0;
    	
    	for (Record item : products) {
    		List<Record> listProductsItem =  item.get("products");
    		if (listProductsItem != null) {
    			for (Record productsItem : listProductsItem) {
        			totalPayable += productsItem.getInt("amount") * productsItem.getNumber("price").doubleValue();
        		}
			} else {
				totalPayable += item.getInt("amount") * item.getNumber("price").doubleValue();
			}
    	}
    	return totalPayable;
    }
    
    private static double getTotalPayableByBaseonCategory(List<Record> products, int promotionId) {
    	List<PromotionCondition> list = PromotionCondition.dao.find("select * from promotion_condition where promotion_id = ?", promotionId);
    	List<Record> prods = Db.find("select * from product");
    	List<Integer> allIds = new ArrayList<Integer>();
    	
    	for (PromotionCondition item : list) {
    		int objectId = item.getObjectId();
    		
    		CategoryService categoryService = new CategoryService();
    		List<Category> cates = categoryService.getAllChildAndInclude(objectId);
    		
    		List<Integer> ids = CategoryService.getCateIds(cates);
    		allIds.addAll(ids);
    	}
    	
    	double totalPayable = 0;
    	
    	for (Record item : products) {
    		Record prod = BaseDao.findItem(item.getInt("id"), prods, "id");
    		if(prod != null){
    		int categoryId = prod.getInt("category_id");
    			if (allIds.contains(categoryId)) {
    				totalPayable += item.getInt("amount") * item.getDouble("price");
    			}
    		}
    		
    	
    	}
    	
    	return totalPayable;
    }
    
    private static double getTotalPayableByBaseonProduct(List<Record> products, int promotionId) {
    	List<PromotionCondition> promotionConditions = PromotionCondition.dao.find("select * from promotion_condition where promotion_id = ?", promotionId);
    	List<Integer> objectIds = new ArrayList<Integer>();
    	
    	for (PromotionCondition item : promotionConditions) {
    		objectIds.add(item.getObjectId());
    	}
    	
    	double totalPayable = 0;
    	for (Record item : products) {
    		
    		if (item.get("products") != null) {
    			
				List<Record> itemProducts = item.get("products");
				for (Record product : itemProducts) {
					int productId = product.getInt("product_id");
					
			    	if (objectIds.contains(productId)) {
			    		int amount = product.getInt("amount");
			    		double price = product.getDouble("price");
			    		totalPayable += amount * price;
					}
				}
		    	
			} else {
				
				int productId = item.getInt("product_id");
	    		if (objectIds.contains(productId)) {
	    			int amount = item.getInt("amount");
	    			double price = item.getDouble("price");
	    			totalPayable += amount * price;
	    		}
	    	}
		}
    
    	return totalPayable;
    }
    
    /**
     * 计算总优惠金额
     * @param products [{product_id:1, price_id:4, amount:2, price:5, shop_id:3},...]
     * @return
     */
    public static double calculateDiscount(List<Record> products) {
    	List<Integer> shopIds = new ArrayList<Integer>();
    	for (Record item : products) {
    		int shopId = item.getInt("shop_id");
    		if (!shopIds.contains(shopId)) {
    			shopIds.add(shopId);
    		}
    	}
    	
    	double totalDiscount = 0;
    	
    	for (Integer shopId : shopIds) {
    		List<Record> sProducts = getProductsByShopId(shopId, products);
    		List<Record> promotions = getShopCartPromotion(shopId, sProducts);

    		for (Record item : promotions) {
    			totalDiscount += item.getDouble("discount");
    		}
    	}
    	
    	return totalDiscount;
    }
    
    public static List<Record> getProductsByShopId(int shopId, List<Record> products) {
    	List<Record> list = new ArrayList<Record>();
    	for (Record product : products) {
    		if (shopId == product.getInt("shop_id")) {
    			list.add(product);
    		}
    	}
    	return list;
    }
    
    /**
     * 找出所有满足条件的促销活动
     * @param products
     * @return
     */
    public static List<Record> getMeetPromotion(List<Record> products) {
    	List<Integer> shopIds = new ArrayList<Integer>();
    	for (Record item : products) {
    		if(item.getInt("shop_id") != null){
    			int shopId = item.getInt("shop_id");
    			if (!shopIds.contains(shopId)) {
    				shopIds.add(shopId);
    			}
    		}
    	}
    	
    	List<Record> result = new ArrayList<Record>();
    	
    	for (Integer shopId : shopIds) {
    		List<Record> promotions = getShopCartPromotion(shopId, products);
    		result.addAll(promotions);
    	}
    	
    	return result;
    }
    
}