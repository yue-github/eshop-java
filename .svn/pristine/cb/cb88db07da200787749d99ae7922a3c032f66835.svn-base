package com.eshop.content;

import java.util.ArrayList;
import java.util.List;
import com.eshop.helper.MathHelper;
import com.eshop.helper.PathHelper;
import com.eshop.model.Category;
import com.eshop.model.Product;
import com.eshop.model.RecommendPosition;
import com.eshop.model.Resource;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao;
import com.eshop.service.Manager;
import com.eshop.visit.CustomerLookRecordService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ContentService {

	/**
	 * 获取首页轮播图
	 * @param type
	 * @return
	 */
	public static List<Record> findPcBannders(int type) {
        List<Record> list = new ArrayList<Record>();
        RecommendPosition p = RecommendPosition.dao.findFirst("select * from recommend_position where type = ?", type);
        
        if(p == null) {
        	return list;
        }
        
        String sql = "select a.*, b.path, b.category from recommend as a" +
        		" left join resource as b on a.recommendPic = b.id" +
        		" where a.recommendPosition_id = " + p.getId() +
        		" order by a.sortNumber asc";
        list = Db.find(sql);
        
        List<Record> advs = Db.find("select * from advertisement");
        
        for (Record item : list) {
        	int rtype = item.getInt("type");
        	String url = "";
        	if (rtype == 4) {	// 广告
        		Record adv = BaseDao.findItem(item.getInt("relate_Id"), advs, "id");
        		url = adv.getStr("url");
        	}
        	item.set("url", url);
        }
        
    	return list;
    }
	
	/**
     * 获取各个页面的推荐    位置类型： 1首页轮播图，2首页推荐产品,4首页推荐服务,5首页健康食品,13首页广告；内容类型：1产品，2服务，3店铺，4广告
     * @param positionType 位置类型   格式：type,type
     * @return 推荐信息    格式: [{name:位置名称, records:List<Record>]
     */
    public static List<RecommendPosition> getRecommends(String types, String baseUrl) {
    	List<RecommendPosition> positions = RecommendPosition.dao.find("select * from recommend_position where type = ? order by sortNumber", 2);
    	
    	for (RecommendPosition item : positions) {
    		int psId = item.getId();
    		
    		List<Record> products = new ArrayList<Record>();
    		List<Record> advs = new ArrayList<Record>();
    		List<Record> shops = new ArrayList<Record>();
    		
    		List<Record> recommends = Db.find("select * from recommend where recommendPosition_id = ? order by sortNumber", psId);
    		
    		for (Record item2 : recommends) {
    			int type = item2.getInt("type");
    			int relateId = item2.getInt("relate_Id");
    			
    			String recommendPic = ResourceService.getPathByResId(item2.getInt("recommendPic"));
    			
    			//内容类型：1产品，2服务，3店铺，4广告
    			switch (type) {
	    			case 1:
	    				Record product = Db.findById("product", relateId);
	    				if (product != null) {
	    					String mainPic = ResourceService.getPathByResId(product.getInt("mainPic"));
	    					
	    					if (recommendPic == null || recommendPic.equals("")) {
	    						recommendPic = mainPic;
	    					}
	    					
	    					product.set("mainPic", PathHelper.getAbsolutePath(baseUrl, mainPic));
	    					product.set("recommendPic", PathHelper.getAbsolutePath(baseUrl, recommendPic));
	    					products.add(product);
	    				}
	    				break;
	    				
	    			case 3:
	    				Record shop = Db.findById("shop", relateId);
	    				if (shop != null) {
	    					String logoPic = ResourceService.getPathByResId(shop.getInt("logoPic"));
	    					
	    					if (recommendPic == null || recommendPic.equals("")) {
	    						recommendPic = logoPic;
	    					}
	    					
	    					shop.set("logoPic", PathHelper.getAbsolutePath(baseUrl, logoPic));
	    					shop.set("recommendPic", PathHelper.getAbsolutePath(baseUrl, recommendPic));
	    					shops.add(shop);
	    				}
	    				break;
	    				
	    			case 4:
	    				Record adv = Db.findById("advertisement", relateId);
	    				if (adv != null) {
	    					adv.set("recommendPic", PathHelper.getAbsolutePath(baseUrl, recommendPic));
	    					advs.add(adv);
	    				}
	    				break;
    			}
    		}
    		
    		item.put("products", products);
    		item.put("advs", advs);
    		item.put("shops", shops);
    	}
    	
    	return positions;
    }
    
    /**
     * 根据位置类型获取推荐广告
     * @param type 位置类型
     * @return [{id:id,note:广告标题,url:url,recommendPic:广告图片},...]
     */
    public static List<Record> getRecommendAdvByPositionType(int type) {
    	List<Record> list = new ArrayList<Record>();
        List<Record> ps = Db.find("select * from recommend_position where type = ?", type);
        String whereIn = BaseDao.getWhereIn(ps, "id");
        
        if(ps == null) {
        	return list;
        }
        
        String sql = "select a.note, a.url, r.recommendPic from recommend as r " +
        		" left join advertisement as a on r.relate_Id = a.id " +
        		" where r.recommendPosition_id in " + whereIn +
        		" and r.type = " + RecommendService.ADV +
        		" order by r.sortNumber desc";
    	
    	return Db.find(sql);
    }
    
    /**
     * 根据推荐位置id，获取推荐产品
     * @param positionId
     * @param pageNumber
     * @param pageSize
     * @return {totalPage:总页数,totalRow:总行数,data:[{},...]}
     */
    public static List<Record> findRecommendProdByPosId(int offset, int length, int positionId) {
    	String sql = findRecommendProdByPosIdSql(positionId);
    	sql = BaseDao.appendLimitSql(sql, offset, length);
    	
    	List<Record> list = Db.find(sql);
    	
    	for (Record item : list) {
    		Integer recommendPic = item.getInt("recommendPic");
    		if (recommendPic != null) {
    			item.set("mainPic", recommendPic);
    		} else {
    			String mainPic = ResourceService.getPath(item.getInt("mainPic"));
    			item.set("mainPic", mainPic);
    		}
    	}
    	
    	return list;
    }
    
    public static int countRecommendProdByPosId(int positionId) {
    	String sql = findRecommendProdByPosIdSql(positionId);
    	return Db.find(sql).size();
    }
    
    private static String findRecommendProdByPosIdSql(int positionId) {
    	String sql = "select b.*, a.recommendPic " +
    			"from recommend as a left join product as b on a.relate_Id = b.id " +
    			"where a.type = 1 " + 
    			"and a.recommendPosition_id = " + positionId + " " +
    			"and b.is_sale = 1 " +
    			"and b.isDelete = 0";
    	
    	return sql;
    }
    
    /**
     * 查找所有一级分类
     * @return
     */
    public static List<Record> categories() {
    	return Manager.findCategoryItems(null, null, 0, null, 0, null);
    }
    
    /**
     * 根据位置类型获取推荐产品
     * @param type 位置类型
     * @return [{positionName:位置名称, productList:[{id:id,name:产品名称, suggestedRetailUnitPrice:价格, recommendPic:推荐图片, summary:摘要},...]},...]
     */
    public static List<Record> getRecommendProductsByPositionType(int type) {
    	List<Record> list = new ArrayList<Record>();
    	
        List<RecommendPosition> ps = RecommendPosition.dao.find("select * from recommend_position where type = " + type + " order by sortNumber");
        
        if(ps == null) {
        	return list;
        }
        
        for (RecommendPosition p : ps) {
        	Record record = new Record();
        	List<Record> recs = Db.find("select p.*, r.recommendPic from product as p "
            		+ "left join recommend as r on r.relate_Id = p.id "
            		+ "where p.is_sale=1 and p.isDelete = 0 and r.recommendPosition_id = ? order by r.sortNumber desc", p.getId());
        	
        	for (Record item : recs) {
        		int recommendPic = item.getInt("recommendPic");
        		Resource res = Resource.dao.findById(recommendPic);
        		if (res != null) {
        			item.set("recommendPic", res.getPath());
        			continue;
        		}
        		
        		int mainPic = item.getInt("mainPic");
        		Resource res2 = Resource.dao.findById(mainPic);
        		if (res2 != null) {
        			item.set("recommendPic", res2.getPath());
        			continue;
        		}
        		item.set("recommendPic", "");
        		
        		Shop shop = Shop.dao.findById(item.get("shop_id"));
        		int shopId = 0;
        		int shopType = 3; //自营
        		String shopName = "";
        		
        		if (shop != null) {
        			shopId = shop.getId();
        			shopType = shop.getShopType();
        			shopName = shop.getName();
        		}
        		
        		item.set("shopId", shopId);
    			item.set("shopType", shopType);
    			item.set("shopName", shopName);
        	}
        	
        	record.set("positionName", p.getName());
        	record.set("productList", recs);
        	list.add(record);
        }
        
    	return list;
    }
    
    /**
     * 根据位置类型，获取推荐位置
     * @param positionType
     * @return list
     */
    public List<RecommendPosition> getPositionInApp(int[] types) {
    	String whereIn = BaseDao.getWhereIn(types);
    	String sql = "select * from recommend_position where type in " + whereIn;
    	
    	List<RecommendPosition> list = RecommendPosition.dao.find(sql);
    	
    	return list;
    }
    
    /**
	 * 根据用户习惯获取推荐产品
	 * @param row 条数
	 * @return 成功：[{id:产品id,name:产品名称,shop_id:店铺id,shopName:店铺名称,shopType:店铺类型,mainPicPath:产品主图,summary:产品摘要,suggestedRetailUnitPrice:价格},...]
	 */
    public static List<Product> getRecommendProductsByCustomer(Integer customerId, int row) {
    	
    	//获取用户查看商品习惯
    	List<Record> listLook = CustomerLookRecordService.customerLookProducts(customerId, row);
    	
    	List<Product> list = new ArrayList<>();
    	//根据习惯中的商品id获取商品中的信息
		for (int i = 0; i < listLook.size(); i++) {
    		Integer productId = listLook.get(i).get("product_id");
    		String sql = "select * from product where is_sale = 1 and isDelete = 0 and id = " + productId;
        	
    		Product product = Product.dao.findFirst(sql);
        	if (product != null) {
        		list.add(product);
    		}
    	}
		
		int size = list.size();
		
		//不够row条, 再从所有商品中随机抽取
		if (row > size) {
			String sql = "select * from product where is_sale = 1 and isDelete = 0 order by rand() limit " + (row - size);
        	
			List<Product> products = Product.dao.find(sql);
        	for (Product product : products) {
        		list.add(product);
    		}
		}
		
		//随机数集合
		List<Integer> randoms = new ArrayList<>();
		int length = randoms.size();
		while (length < row) {
			Integer number = MathHelper.getRandom(0, list.size());
			
			if (!randoms.contains(number)) {
				randoms.add(number);
			}
			length = randoms.size();
		}
		
		//随机排序一次（如果不随机排序，用户查看商品习惯略少，会定死在第一条）
		List<Product> result = new ArrayList<>();
		if (randoms.size() > 0) {
			for (int i = 0; i < randoms.size(); i++) {
				result.add(list.get(randoms.get(i)));
			}
		}
		
    	for (Product item : result) {
    		int mainPicInt = item.getMainPic();
    		String mainPic = ResourceService.getPath(mainPicInt);
    		
    		Shop shop = Shop.dao.findById(item.getShopId());
    		String shopName = "";
    		int shopType = 3;
    		if (shop != null) {
    			shopName = shop.getName();
    			shopType = shop.getShopType();
    		}
    		
    		item.put("shopName", shopName);
    		item.put("shopType", shopType);
    		item.put("mainPicPath", mainPic);
    	}
    	
    	return result;
    }
    
    /**
     * 获取特产分类
     * @param cateName
     * @return
     */
    public static List<Category> getSpecialtyCategories(String cateName) {
    	Category model = Category.dao.findFirst("select * from category where name = ? and isDelete = ?", cateName, 0);
    	
    	int categoryId = model != null ? model.getId() : 0;
    	//获取该id下的子分类
    	List<Category> many = Category.dao.find("select * from category where parent_id = ? and isDelete = ?",categoryId, 0);
    	for(Category item : many) {
    		//二级分类
    		List<Category> category1 = Category.dao.find("select * from category where parent_id = ? and isDelete = ?", item.getId(), 0);
    		int num = category1.size();
    		if(num > 0) {
    			item.put("subcategories",category1);
    		}
    	}
    	return many;
    }
    
}
