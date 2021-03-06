package com.eshop.promotion;

import java.sql.SQLException;
import java.util.*;

import com.eshop.content.ResourceService;
import com.eshop.log.Log;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionDazhe;
import com.eshop.model.Resource;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 
 */
public class ManDaZhe extends BasePromotion {

    /**
     * 创建打折活动
     * @param promotion
     * @param dazhe
     * @return
     */
    public static ServiceCode create(final Promotion promotion, final PromotionDazhe dazhe, final String mainPic) {
    	double value = dazhe.getBigDecimal("value").doubleValue();
    	
    	if (value < 0 || value > 100) {
    		return ServiceCode.Validation;
    	}
    		
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					
					promotion.setCreatedAt(new Date());
					promotion.setUpdatedAt(new Date());
					promotion.save();
					
					dazhe.setPromotionId(promotion.getId());
					dazhe.save();
					if(mainPic != null && mainPic.length() > 0) {
						int resId = ResourceService.insertResource(mainPic, promotion.getId(), ResourceService.PROMOTION, ResourceService.PICTURE);
						promotion.setMainPic(resId);
						promotion.update();
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",创建打折活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 修改打折活动
     * @param promotion
     * @param dazhe
     * @return
     */
    public static ServiceCode update(final Promotion promotion, final PromotionDazhe dazhe, final String mainPic) {
    	double value = dazhe.getBigDecimal("value").doubleValue();
    	
    	if (value < 0 || value > 100) {
    		return ServiceCode.Validation;
    	}
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					dazhe.update();
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
					Log.error(e.getMessage() + ",修改打折活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 查看打折活动详情
     * @param promotionId
     * @return
     */
    public static PromotionDazhe getPromotionDazhe(int promotionId) {
    	return PromotionDazhe.dao.findFirst("select * from promotion_dazhe where promotion_id = ?", promotionId);
    }

    /**
     * 删除打折活动
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
					Db.update("delete from promotion_dazhe where promotion_id = ?", id);
					//删除基础信息
					Db.update("delete from promotion where id = ?", id);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除打折活动失败");
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
     * 批量查询打折活动
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
     * @param minValue
     * @param maxValue
     * @return
     */
    public static List<Record> findPromotionItems(int offset, int count, String title, String desc, 
    		String startDate, String endDate, String startCreatedAt, String endCreatedAt, Integer scope, 
    		Integer baseOn, Integer shopId, Double minFull, Double maxFull, Double minValue, 
    		Double maxValue, Map<String, String> orderByMap) {
    	
    	String sql = findPromotionItemsSql(title, desc, startDate, endDate, startCreatedAt, endCreatedAt, 
    			scope, baseOn, shopId, minFull, maxFull, minValue, maxValue, orderByMap);
    	
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询打折活动的总数量
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
     * @param minValue
     * @param maxValue
     * @return
     */
    public static int countPromotionItems(String title, String desc, String startDate, String endDate, 
    		String startCreatedAt, String endCreatedAt, Integer scope, Integer baseOn, Integer shopId, 
    		Double minFull, Double maxFull, Double minValue, Double maxValue) {
    	
    	String sql = findPromotionItemsSql(title, desc, startDate, endDate, startCreatedAt, endCreatedAt, 
    			scope, baseOn, shopId, minFull, maxFull, minValue, maxValue, null);
    	
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
     * @param minValue
     * @param maxValue
     * @return
     */
    public static String findPromotionItemsSql(String title, String desc, 
    		String startDate, String endDate, String startCreatedAt, String endCreatedAt, Integer scope, 
    		Integer baseOn, Integer shopId, Double minFull, Double maxFull, Double minValue, 
    		Double maxValue, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, b.full, b.value from promotion as a" +
    			" left join promotion_dazhe as b on a.id = b.promotion_id" +
    			" where a.type = " + DAZHE;
    	
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
    	if (minValue != null) {
			sql += " and b.value >= " + minValue;
		}
    	if (maxValue != null) {
			sql += " and b.value <= " + maxValue;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }

}