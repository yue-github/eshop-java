package com.eshop.promotion;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eshop.content.ResourceService;
import com.eshop.helper.DateHelper;
import com.eshop.log.Log;
import com.eshop.model.Product;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionMansong;
import com.eshop.model.PromotionSku;
import com.eshop.model.Resource;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 
 */
public class ManSong extends BasePromotion {

    /**
     * 创建满送活动
     * @param promotion
     * @param mansong
     * @return
     */
    public static ServiceCode create(final Promotion promotion, final PromotionMansong mansong, final String mainPic) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					promotion.setCreatedAt(new Date());
					promotion.setUpdatedAt(new Date());
					promotion.save();
					
					mansong.setPromotionId(promotion.getId());
					mansong.save();
					if(mainPic != null && mainPic.length() >0) {
						int resId = ResourceService.insertResource(mainPic, promotion.getId(), ResourceService.PROMOTION, ResourceService.PICTURE);
						promotion.setMainPic(resId);
						promotion.update();
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",创建满送活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 修改满送活动
     * @param promotion
     * @param mansong
     * @return
     */
    public static ServiceCode update(final Promotion promotion, final PromotionMansong mansong, final String mainPic) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					mansong.update();
					promotion.update();
					if(mainPic != null && mainPic.length() > 0) {
						// 已存在
						Resource res = ResourceService.get(promotion.getMainPic());
						if (res != null) {
							res.setPath(mainPic);
							res.update();
						} else {	// 不存在
							int resId = ResourceService.insertResource(mainPic, promotion.getId(), ResourceService.PROMOTION, ResourceService.PICTURE);
							promotion.setMainPic(resId);
							promotion.update();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",修改满送活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 查看满送活动详情
     * @param promotionId
     * @return
     */
    public static PromotionMansong getPromotionMansong(int promotionId) {
    	return PromotionMansong.dao.findFirst("select * from promotion_mansong where promotion_id = ?", promotionId);
    }

    /**
     * 删除满送活动
     * @param id
     * @return
     */
    public static ServiceCode delete(final int id) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					//删除促销产品
					Db.update("delete from promotion_condition where promotion_id = ?", id);
					//删除扩展信息
					Db.update("delete from promotion_mansong where promotion_id = ?", id);
					//删除基础信息
					Db.update("delete from promotion where id = ?", id);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除满送活动失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 批量删除
     * @param ids
     * @return
     */
    public static ServiceCode delete(final List<Integer> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (Integer id : ids) {
						delete(id);
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除活动失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 批量查询满送活动
     * @param offset
     * @param count
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param minFull
     * @param maxFull
     * @return
     */
    public static List<Record> findPromotionItems(int offset, int count, String title, String desc, 
    		String startDate, String endDate, String startCreatedAt, String endCreatedAt, Integer scope, 
    		Integer baseOn, Integer shopId, Double minFull, Double maxFull, Map<String, String> orderByMap) {
    	
    	String sql = findPromotionItemsSql(title, desc, startDate, endDate, startCreatedAt, endCreatedAt, 
    			scope, baseOn, shopId, minFull, maxFull, orderByMap);
    	
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询满送活动的总数量
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param minFull
     * @param maxFull
     * @return
     */
    public static int countPromotionItems(String title, String desc, 
    		String startDate, String endDate, String startCreatedAt, String endCreatedAt, Integer scope, 
    		Integer baseOn, Integer shopId, Double minFull, Double maxFull) {
    	
    	String sql = findPromotionItemsSql(title, desc, startDate, endDate, startCreatedAt, endCreatedAt, 
    			scope, baseOn, shopId, minFull, maxFull, null);
    	
    	return Db.find(sql).size();
    }
    
    /**
     * 组织sql语句
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param minFull
     * @param maxFull
     * @return
     */
    public static String findPromotionItemsSql(String title, String desc, String startDate, String endDate, 
    		String startCreatedAt, String endCreatedAt, Integer scope, Integer baseOn, Integer shopId, 
    		Double minFull, Double maxFull, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, b.full from promotion as a" +
    			" left join promotion_mansong as b on a.id = b.promotion_id" +
    			" where a.type = " + MANSONG;
    	
    	if (title != null && !title.equals("")) {
			sql += " and a.title like '%" + title + "%'";
		}
    	if (desc != null && !desc.equals("")) {
			sql += " and a.desc like '%" + desc + "%'";
		}
    	if (startDate != null && !startDate.equals("")) {
			sql += " and DATE_FORMAT(a.startDate, '%Y-%m-%d') <= '" + startDate + "'";
		}
    	if (endDate != null && !endDate.equals("")) {
			sql += " and DATE_FORMAT(a.endDate, '%Y-%m-%d') >= '" + endDate + "'";
		}
    	if (startCreatedAt != null && !startCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startCreatedAt + "'";
		}
    	if (endCreatedAt != null && !endCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endCreatedAt + "'";
		}
    	if (scope != null) {
			sql += " and a.scope = " + scope;
		}
    	if (baseOn != null) {
			sql += " and a.baseOn = " + baseOn;
		}
    	if (shopId != null) {
			sql += " and a.shop_id = " + shopId;
		}
    	if (minFull != null) {
			sql += " and b.full >= " + minFull;
		}
    	if (maxFull != null) {
			sql += " and b.full <= " + maxFull;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
    
    /**
     * 获取赠品
     * @param promotionId
     * @return
     */
    public static Record getGifts(int offset, int length, int promotionId) {
    	String sql = "select a.*, b.name, b.mainPic from promotion_sku as a" +
    			" left join product as b on a.product_id = b.id" +
    			" where a.promotion_id = " + promotionId;
    	int total = Db.find(sql).size();
    	sql = BaseDao.appendLimitSql(sql, offset, length);
    	List<Product> products = Product.dao.find(sql);
    	
    	List<Record> resources = Db.find("select * from resource");
    	
    	for (Product item : products) {
    		Record record = BaseDao.findItem(item.getMainPic(), resources, "id");
    		String mainPic = record.getStr("path");
    		item.set("mainPic", mainPic);
    	}
    	
    	Record record = new Record();
    	record.set("total", total);
    	record.set("data", products);
    	return record;
    }
    
    /**
     * 创建赠品
     * @param productId
     * @param promotionId
     * @return
     */
    public static boolean createGift(JSONArray objects, int promotionId) {
    	for (int i = 0; i < objects.size(); i++) {
    		JSONObject object = objects.getJSONObject(i);
    		int productId = object.getIntValue("id");
    		Product product = Product.dao.findById(productId);
        	PromotionSku gift = new PromotionSku();
        	gift.setProductId(productId);
        	gift.setPromotionId(promotionId);
        	gift.setProductName(product.getName());
        	gift.setPromotionPrice(new BigDecimal(0));
        	gift.setPrice(new BigDecimal(0));
        	gift.save();
    	}
    	return true;
    }
    
    /**
     * 待筛选的产品列表
     * @param offset
     * @param length
     * @param promotionId
     * @param shopId
     * @return
     */
    public static Record giftProducts(int offset, int length, int promotionId, int shopId) {
    	String sql = "select * from product where shop_id = " + shopId + " and id not in (select product_id from promotion_sku where promotion_id = " + promotionId + ")";
    	int total = Db.find(sql).size();
    	sql = BaseDao.appendLimitSql(sql, offset, length);
    	List<Record> data = Db.find(sql);
    	
    	Record result = new Record();
    	result.set("total", total);
    	result.set("data", data);
    	return result;
    }
    
    /**
     * 删除赠品
     * @param id
     * @return
     */
    public static boolean deleteGift(int id) {
    	PromotionSku.dao.deleteById(id);
    	return true;
    }
    
    /**
     * 赠品已被领取数量
     * @param promotionId
     * @param productId
     * @param priceId
     * @return
     */
    public static int usedAmount(int promotionId, int productId, int priceId) {
    	return Db.find("select * from product_order where product_id = ? and priceId = ? and promotion_id = ?", productId, priceId, promotionId).size();
    }
    
    /**
     * 赠品剩余数量
     * @param promotionId
     * @param productId
     * @param priceI
     * @return
     */
    public static int remainAmount(int promotionId, int productId, int priceId) {
    	PromotionSku promotionSku = PromotionSku.dao.findFirst("select * from promotion_sku where promotion_id = ? and product_id = ? and price_id = ?", promotionId, productId, priceId);
    	int limitStock = promotionSku.getLimitStock();
    	int usedAmount = usedAmount(promotionId, productId, priceId);
    	int remainAmount = limitStock - usedAmount;
    	return remainAmount;
    }
    
    /**
     * 赠品是否已被领完
     * @param promotionId
     * @param productId
     * @param priceId
     * @return
     */
    public static boolean giftIsEmpty(int promotionId, int productId, int priceId) {
    	int remainAmount = remainAmount(promotionId, productId, priceId);
    	return remainAmount <= 0;
    }
    
    public static Record gifts(List<Record> products) {
    	List<Integer> shopIds = new ArrayList<Integer>();
    	for (Record product : products) {
    		if (!shopIds.contains(product.getInt("shop_id"))) {
    			shopIds.add(product.getInt("shop_id"));
    		}
    	}
    	List<Record> list = new ArrayList<Record>();
    	for (Integer shopId : shopIds) {
    		Record item = new Record();
    		item.set("shop_id", shopId);
    		List<Record> prods = new ArrayList<Record>();
    		for (Record product : products) {
    			if (product.getInt("shop_id") == shopId) {
    				prods.add(product);
    			}
    		}
    		item.set("products", prods);
    		list.add(item);
    	}
    	String now = DateHelper.formatDate(new Date());
    	List<Record> gifts = new ArrayList<Record>();
    	for (Record item : list) {
    		int shopId = item.getInt("shop_id");
    		List<Record> prods = item.get("products");
    		List<Record> mansongs = Db.find("select a.*, b.full from promotion as a left join promotion_mansong as b on a.id = b.promotion_id where a.type = 3 and a.scope = 2 and a.shop_id = ? and DATE_FORMAT(a.startDate, '%Y-%m-%d') <= ? and DATE_FORMAT(a.endDate, '%Y-%m-%d') >= ?", shopId, now, now);
    		for (Record mansong : mansongs) {
    			int promotionId = mansong.getInt("id");
    			int baseOn = mansong.getInt("baseOn");
    			double full = mansong.getNumber("full").doubleValue();
    			if (baseOn == BasePromotion.BASEON_ALL) {
    				if (isMeetBaseAll(prods, full)) {
    					List<Record> pds = Db.find("select b.* from promotion_sku as a left join product as b on a.product_id = b.id where a.promotion_id = ?", promotionId);
    					for (Record p : pds) {
    						p.set("productName", p.getStr("name"));
    						gifts.add(p);
    					}
    				}
    			} else if (baseOn == BasePromotion.BASEON_PRODUCT) {
    				if (isMeetBaseProduct(prods, full, promotionId)) {
    					List<Record> pds = Db.find("select b.* from promotion_sku as a left join product as b on a.product_id = b.id where a.promotion_id = ?", promotionId);
    					for (Record p : pds) {
    						String mainPic = ResourceService.getPath(p.getInt("mainPic"));
    						p.set("mainPic", mainPic);
    						p.set("productName", p.getStr("name"));
    						gifts.add(p);
    					}
    				}
    			}
    		}
    	}
    	Record record = new Record();
    	record.set("id", 0);
    	record.set("name", "赠品");
    	record.set("logoPic", "");
    	record.set("products", gifts);
    	return record;
    }
    
    public static boolean isMeetBaseAll(List<Record> products, double full) {
    	double total = 0;
    	for (Record product : products) {
    		total += product.getDouble("totalPrice");
    	}
    	return total > full;
    }
    
    public static boolean isMeetBaseProduct(List<Record> products, double full, int mansongId) {
    	List<Record> skus = Db.find("select * from promotion_condition where type = 2 and promotion_id = ? group by object_id", mansongId);
    	List<Record> list = new ArrayList<Record>();
    	for (Record sku : skus) {
    		for (Record product : products) {
    			System.out.println(sku.getInt("object_id")+","+product.getInt("product_id"));
    			if (sku.getInt("object_id").equals(product.getInt("product_id"))) {
    				list.add(product);
    				break;
    			}
    		}
    	}
    	double total = 0;
    	for (Record product : list) {
    		total += product.getDouble("totalPrice");
    	}
    	return total > full;
    }

}