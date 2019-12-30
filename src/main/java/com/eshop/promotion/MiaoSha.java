package com.eshop.promotion;

import java.sql.SQLException;
import java.util.*;

import com.eshop.content.ResourceService;
import com.eshop.helper.DateHelper;
import com.eshop.log.Log;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionMiaosha;
import com.eshop.model.PromotionSku;
import com.eshop.model.Resource;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 秒杀类
 */
public class MiaoSha extends BasePromotion {

    /**
     * 创建秒杀活动
     * @param promotion
     * @param miaosha
     * @return
     */
    public static ServiceCode create(final Promotion promotion, final PromotionMiaosha miaosha, final String mainPic) {
        boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					promotion.setCreatedAt(new Date());
					promotion.setUpdatedAt(new Date());
					promotion.save();
					
					miaosha.setPromotionId(promotion.getId());
					miaosha.save();
					if(mainPic != null && mainPic.length() > 0) {
						int resId = ResourceService.insertResource(mainPic, promotion.getId(), ResourceService.PROMOTION, ResourceService.PICTURE);
						promotion.setMainPic(resId);
						promotion.update();
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",创建秒杀活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 修改秒杀活动
     * @param promotion
     * @param miaosha
     * @return
     */
    public static ServiceCode update(final Promotion promotion, final PromotionMiaosha miaosha, final String mainPic) {
        boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					miaosha.update();
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
					Log.error(e.getMessage() + ",修改秒杀活动失败");
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 查看秒杀活动详情
     * @param id
     * @return
     */
    public static PromotionMiaosha getPromotionMiaosha(int promotionId) {
    	return PromotionMiaosha.dao.findFirst("select * from promotion_miaosha where promotion_id = ?", promotionId);
    }

    /**
     * 删除活动
     * @param id
     * @return
     */
    public static ServiceCode delete(final int id) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					//删除促销产品
					Db.update("delete from promotion_sku where promotion_id = ?", id);
					//删除扩展信息
					Db.update("delete from promotion_miaosha where promotion_id = ?", id);
					//删除基础信息
					Db.update("delete from promotion where id = ?", id);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除秒杀活动失败");
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
     * 批量查询秒杀活动
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
     * @param minLimitAmount
     * @param maxLimitAmount
     * @return
     */
    public static List<Record> findPromotionItems(int offset, int count, String title, String desc, 
    		String startDate, String endDate, String startCreatedAt, String endCreatedAt, Integer scope, 
    		Integer baseOn, Integer shopId, Integer minLimitAmount, Integer maxLimitAmount, 
    		Map<String, String> orderByMap) {
    	
    	String sql = findPromotionItemsSql(title, desc, startDate, endDate, startCreatedAt, endCreatedAt, 
    			scope, baseOn, shopId, minLimitAmount, maxLimitAmount, orderByMap);
    	
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询秒杀活动的总数量
     * @param title
     * @param desc
     * @param startDate
     * @param endDate
     * @param startCreatedAt
     * @param endCreatedAt
     * @param scope
     * @param baseOn
     * @param shopId
     * @param minLimitAmount
     * @param maxLimitAmount
     * @return
     */
    public static int countPromotionItems(String title, String desc, String startDate, String endDate, 
    		String startCreatedAt, String endCreatedAt, Integer scope, Integer baseOn, Integer shopId, 
    		Integer minLimitAmount, Integer maxLimitAmount) {
    	
    	String sql = findPromotionItemsSql(title, desc, startDate, endDate, startCreatedAt, endCreatedAt, 
    			scope, baseOn, shopId, minLimitAmount, maxLimitAmount, null);
    	
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
     * @param minLimitAmount
     * @param maxLimitAmount
     * @return
     */
    public static String findPromotionItemsSql(String title, String desc, String startDate, String endDate, 
    		String startCreatedAt, String endCreatedAt, Integer scope, Integer baseOn, Integer shopId, 
    		Integer minLimitAmount, Integer maxLimitAmount, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, b.limit_amount from promotion as a" +
    			" left join promotion_miaosha as b on a.id = b.promotion_id" +
    			" where a.type = " + MIAOSHA;
    	
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
    	if (minLimitAmount != null) {
			sql += " and b.limit_amount >=" + minLimitAmount;
		}
    	if (maxLimitAmount != null) {
			sql += " and b.limit_amount <= " + maxLimitAmount;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
    
    /**
     * 计算秒杀价，如果该sku没有参加秒杀活动，则没有秒杀价，返回原价
     * @param productId
     * @param priceId
     * @return
     */
    public static double calculatePromotionPrice(int productId, int priceId) {
    	List<Promotion> promotions = Promotion.dao.find("select * from promotion where type = ? and startDate <= ? and endDate >= ?", BasePromotion.MIAOSHA, new Date(), new Date());
    	
    	PromotionSku promotionSku = null;
    	
    	for (Promotion item : promotions) {
    		promotionSku = PromotionSku.dao.findFirst("select * from promotion_sku where promotion_id = ? and product_id = ? and price_id = ?", item.getId(), productId, priceId);
    		if (promotionSku != null) {
				break;
			}
    	}
    	
    	if (promotionSku == null) {
    		Record record = BaseDao.getSku(productId, priceId);
    		return record.getDouble("price");
    	} else {
    		return promotionSku.getPromotionPrice().doubleValue();
    	}
    }
    
    /**
     * 获取该单品参加了哪个秒杀活动
     * @param productId
     * @param priceId
     * @return
     */
    public static Promotion getMiaoshaSlogan(int productId, int priceId) {
    	String now = DateHelper.formatDateTime(new Date());
    	String sql = "select * from promotion as a" +
    			" left join promotion_miaosha as b on a.id = b.promotion_id" +
    			" left join promotion_sku as c on b.promotion_id = c.promotion_id" +
    			" where a.type = " + BasePromotion.MIAOSHA +
    			" and a.startDate <= '" + now + "'" +
    			" and a.endDate >= '" + now + "'" +
    			" and c.product_id = " + productId +
    			" and c.price_id = " + priceId;
    	return Promotion.dao.findFirst(sql);
    }

}