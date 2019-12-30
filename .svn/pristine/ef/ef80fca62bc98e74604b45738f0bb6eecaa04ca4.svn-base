package com.eshop.promotion;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import com.eshop.helper.DateHelper;
import com.eshop.log.Log;
import com.eshop.model.Product;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionSku;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 促销单品类
 * 修改： subSql加了 (大括号)；
 */
public class PromotionSkuService {
	
	private static String getProductName(int productId) {
		Product product = Product.dao.findById(productId);
		return (product != null) ? product.getName() : "";
	}
	
	private static boolean checkSkuIsExist(PromotionSku promotionSku) {
		return checkSkuIsExist(promotionSku, false);
	}
	
	private static boolean checkSkuIsExist(PromotionSku promotionSku, boolean isUpdated) {
		Promotion promotion = Promotion.dao.findById(promotionSku.getPromotionId());
		
		int productId = promotionSku.getProductId();
		int priceId = promotionSku.getPriceId();
		
		String startDate = DateHelper.formatDateTime(promotion.getStartDate());
		String endDate = DateHelper.formatDateTime(promotion.getEndDate());
		String now = DateHelper.formatDateTime(new Date());
		
		String subSql = "((DATE_FORMAT(a.startDate, '%Y-%m-%d') <= '" + startDate + "'" + " and " + " DATE_FORMAT(a.endDate, '%Y-%m-%d') >= '" + startDate + "')" + 
				" or (DATE_FORMAT(a.startDate, '%Y-%m-%d') <= '" + endDate + "'" + " and " + " DATE_FORMAT(a.endDate, '%Y-%m-%d') >= '" + endDate + "')" + 
				" or (DATE_FORMAT(a.startDate, '%Y-%m-%d') >= '" + startDate + "'" + " and " + " DATE_FORMAT(a.endDate, '%Y-%m-%d') <= '" + endDate + "'))";
		
		String sql = "select * from promotion as a" +
				" left join promotion_sku as b on a.id = b.promotion_id" +
				" where a.type = " + BasePromotion.MIAOSHA +
				" and DATE_FORMAT(a.endDate, '%Y-%m-%d') >= '" + now + "'" +
				" and b.product_id = " + productId +
				" and b.price_id = " + priceId + 
				" and " + subSql;
		
		if (isUpdated) {
			sql += " and b.id != " + promotionSku.getPromotionId();
		}
		
		int count = Db.find(sql).size();
		
		return count > 0;
	}

    /**
     * 创建促销单品
     * @param model
     * @return
     */
    public static ServiceCode create(PromotionSku model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	// 判断sku是否已存在，同一个sku不能同时存在
    	if (checkSkuIsExist(model)) {
			return ServiceCode.Validation;
		}
    	
    	String productName = getProductName(model.getProductId());
    	String selectProperties = BaseDao.getselectProterties(model.getPriceId());
    	Record sku = BaseDao.getSku(model.getProductId(), model.getPriceId());
    	double price = sku.getDouble("price");
    	
    	model.setProductName(productName);
    	model.setSelectProterties(selectProperties);
    	model.setPrice(new BigDecimal(price));
    	
    	if (!model.save()) {
			return ServiceCode.Failed;
		}
    	
    	return ServiceCode.Success;
    }

    /**
     * 修改促销单品
     * @param model
     * @return
     */
    public static ServiceCode update(PromotionSku model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	if (checkSkuIsExist(model, true)) {
			return ServiceCode.Validation;
		}
    	
    	String productName = getProductName(model.getProductId());
    	String selectProperties = BaseDao.getselectProterties(model.getPriceId());
    	Record sku = BaseDao.getSku(model.getProductId(), model.getPriceId());
    	double price = sku.getDouble("price");
    	
    	model.setProductName(productName);
    	model.setSelectProterties(selectProperties);
    	model.setPrice(new BigDecimal(price));
    	
    	if (!model.update()) {
			return ServiceCode.Failed;
		}
    	
    	return ServiceCode.Success;
    }

    /**
     * 查看促销单品详情
     * @param id
     * @return
     */
    public static PromotionSku get(int id) {
        return PromotionSku.dao.findById(id);
    }
    
    /**
     * 删除促销单品
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
        if (PromotionSku.dao.deleteById(id)) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
    }
    
    /**
     * 删除促销单品
     * @param ids
     * @return
     */
    public static ServiceCode delete(final List<Integer> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (Integer id : ids) {
						PromotionSku.dao.deleteById(id);
					}
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
     * 批量查询促销单品
     * @param offset
     * @param count
     * @param productId
     * @param minPromotionPrice
     * @param maxPromotionPrice
     * @param minLimitStock
     * @param maxLimitStock
     * @param promotionId
     * @param productName
     * @param selectProterties
     * @return
     */
    public static List<Record> findPromotionSku(int offset, int count, Integer productId, 
    		Double minPromotionPrice, Double maxPromotionPrice, Double minLimitStock, 
    		Double maxLimitStock, Integer promotionId, String productName, String selectProterties, 
    		Map<String, String> orderByMap) {

    	String sql = findPromotionSkuSql(productId, minPromotionPrice, maxPromotionPrice, minLimitStock, 
    			maxLimitStock, promotionId, productName, selectProterties, orderByMap);
    	
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	return Db.find(sql);
    }
    
    /**
     * 批量查询促销单品的总数量
     * @param productId
     * @param minPromotionPrice
     * @param maxPromotionPrice
     * @param minLimitStock
     * @param maxLimitStock
     * @param promotionId
     * @param productName
     * @param selectProterties
     * @return
     */
    public static int countPromotionSku(Integer productId, Double minPromotionPrice, 
    		Double maxPromotionPrice, Double minLimitStock, Double maxLimitStock, Integer promotionId, 
    		String productName, String selectProterties) {

    	String sql = findPromotionSkuSql(productId, minPromotionPrice, maxPromotionPrice, minLimitStock, 
    			maxLimitStock, promotionId, productName, selectProterties, null);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param productId
     * @param minPromotionPrice
     * @param maxPromotionPrice
     * @param minLimitStock
     * @param maxLimitStock
     * @param promotionId
     * @param productName
     * @param selectProterties
     * @return
     */
    public static String findPromotionSkuSql(Integer productId, Double minPromotionPrice, 
    		Double maxPromotionPrice, Double minLimitStock, Double maxLimitStock, Integer promotionId, 
    		String productName, String selectProterties, Map<String, String> orderByMap) {

    	String sql = "select * from promotion_sku where id != 0";
    	
    	if (productId != null) {
			sql += " and product_id = " + productId;
		}
    	if (minPromotionPrice != null) {
			sql += " and promotion_price >= " + minPromotionPrice;
		}
    	if (maxPromotionPrice != null) {
			sql += " and promotion_price <= " + maxPromotionPrice;
		}
    	if (minLimitStock != null) {
			sql += " and limit_stock >= " + minLimitStock;
		}
    	if (maxLimitStock != null) {
			sql += " and limit_stock <= " + maxLimitStock;
		}
    	if (promotionId != null) {
			sql += " and promotion_id = " + promotionId;
		}
    	if (productName != null && !productName.equals("")) {
			sql += " and product_name like '%" + productName + "%'";
		}
    	if (selectProterties != null && !selectProterties.equals("")) {
			sql += " and selectProterties like '%" + selectProterties + "%'";
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }

}