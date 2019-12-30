package com.eshop.promotion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eshop.model.Promotion;
import com.eshop.model.PromotionBaoyou;
import com.eshop.model.Resource;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.content.ResourceService;
import com.eshop.log.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 
 */
public class BaoYou extends BasePromotion {

    /**
     * 创建包邮活动
     * @param promotion
     * @param baoyou
     * @return
     */
    public static ServiceCode create(final Promotion promotion, final PromotionBaoyou baoyou, final String mainPic) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					promotion.setBaseOn(BasePromotion.BASEON_ALL);
					promotion.setCreatedAt(new Date());
					promotion.setUpdatedAt(new Date());
					promotion.save();
					
					baoyou.setPromotionId(promotion.getId());
					baoyou.save();
					
					if(mainPic != null && mainPic.length() > 0) {
						int resId = ResourceService.insertResource(mainPic, promotion.getId(), ResourceService.PROMOTION, ResourceService.PICTURE);
						promotion.setMainPic(resId);
						promotion.update();
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",创建包邮活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 修改包邮活动
     * @param promotion
     * @param baoyou
     * @return
     */
    public static ServiceCode update(final Promotion promotion, final PromotionBaoyou baoyou, final String mainPic) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					baoyou.update();
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
					Log.error(e.getMessage() + ",修改包邮活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 查看包邮活动详情
     * @param promotionId
     * @return
     */
    public static PromotionBaoyou getPromotionBaoyou(int promotionId) {
    	return PromotionBaoyou.dao.findFirst("select * from promotion_baoyou where promotion_id = ?", promotionId);
    }

    /**
     * 删除包邮活动
     * @param id
     * @return
     */
    public static ServiceCode delete(final int id) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					//删除促销条件
					Db.update("delete from promotion_condition where promotion_id = ?", id);
					//删除扩展信息
					Db.update("delete from promotion_baoyou where promotion_id = ?", id);
					//删除基础信息
					Db.update("delete from promotion where id = ?", id);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除包邮活动失败");
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
					for (int i=0; i<ids.size(); i++) {
						int id = ids.get(i);
						delete(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",删除包邮活动失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 批量查询包邮活动
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
     * @param full
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
     * 批量查询包邮活动的总数量
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param full
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
     * 组装sql语句
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
    public static String findPromotionItemsSql(String title, String desc, 
    		String startDate, String endDate, String startCreatedAt, String endCreatedAt, Integer scope, 
    		Integer baseOn, Integer shopId, Double minFull, Double maxFull, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, b.full from promotion as a" +
    			" left join promotion_baoyou as b on a.id = b.promotion_id" +
    			" where a.type = " + BAOYOU;
    	
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
     * 是否满足全场包邮
     * @param products [{product_id:1,amount:3,price:8},...] 整个订单的产品列表
     * @return
     */
    public static boolean isMeetAllFree(List<Record> products) {
    	double totalPayable = 0;
    	for (Record item : products) {
    		totalPayable += item.getInt("amount") * item.getDouble("price");
    	}
    	
    	Date now = new Date();
    	List<Record> baoyous = Db.find("select * from promotion as a left join promotion_baoyou" +
				" as b on a.id = b.promotion_id where a.type = ? and" +
				" a.startDate <= ? and a.endDate >= ? and a.scope = ? and b.full <= ?",
				BasePromotion.BAOYOU, now, now, BasePromotion.SCOPE_ALL, totalPayable);
    	
    	if (baoyous.size() <= 0) {
			return false;
		} else {
			return true;
		}
    }
    
    /**
     * 满足店铺包邮的包邮活动
     * @param products [{product_id:1,amount:3,price:8,shop_id:2},...] 一个店铺的产品列表
     * @return
     */
    public static List<Record> meetShopFree(List<Record> products) {
    	Date now = new Date();
    	List<Record> list = Db.find("select a.*, b.full from promotion as a left join promotion_baoyou as b on a.id = b.promotion_id where a.type = ? and a.startDate <= ? and a.endDate >= ? and a.scope = ?", BasePromotion.BAOYOU, now, now, BasePromotion.SCOPE_SHOP);
    
    	List<Record> result = new ArrayList<Record>();
    	
    	for (Record item : list) {
    		int shopId = item.getInt("shop_id");
    		double full = item.getBigDecimal("full").doubleValue();
    		
    		double totalPayable = 0;
    		
    		for (Record product : products) {
    			if (product.getInt("shop_id") == shopId) {
    				totalPayable += product.getInt("amount") * product.getDouble("price");
    			}
    		}
    		
    		if (totalPayable >= full) {
    			result.add(item);
    		}
    	}
    	
    	return result;
    }
    
}