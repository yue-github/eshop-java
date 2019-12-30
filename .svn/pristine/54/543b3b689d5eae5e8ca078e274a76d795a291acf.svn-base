package com.eshop.content;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eshop.log.Log;
import com.eshop.model.Advertisement;
import com.eshop.model.Product;
import com.eshop.model.Recommend;
import com.eshop.model.RecommendPosition;
import com.eshop.model.Shop;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class RecommendService {
	
	//内容类别
	public final static int PRODUCT = 1;
	public final static int SERVICE = 2;
	public final static int SHOP = 3;
	public final static int ADV = 4;
	
	/**
     * 添加推荐
     * @param model 推荐
     * @return code
     */
    public static ServiceCode createRecommend(final Recommend model, final String recommendPic) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	int type = model.getType();
    	int relateId = model.getRelateId();
    	int mainPic = 0;
    	String name = "";
    	
    	//推荐位置名称
    	RecommendPosition position = RecommendPosition.dao.findById(model.getRecommendpositionId());
    	String recommendPositionName = (position != null) ? position.getName() : "";
    	
    	if (type == RecommendService.ADV) {
    		Advertisement adv = Advertisement.dao.findById(relateId);
    		mainPic = adv.getMainPic();
    		name = (adv != null) ? adv.getNote() : "";
    	} else if (type == RecommendService.PRODUCT) {
    		Product product = Product.dao.findById(relateId);
    		mainPic = product.getMainPic();
    		name = (product != null) ? product.getName() : "";
    	} else if (type == RecommendService.SHOP) {
    		Shop shop = Shop.dao.findById(relateId);
    		mainPic = shop.getLogoPic();
    		name = (shop != null) ? shop.getName() : "";
    	}
    		
    	model.setName(name);
    	model.setRecommendPositionName(recommendPositionName);
    	model.setRecommendPic(mainPic);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	model.save();
    	
    	if (recommendPic != null && !recommendPic.equals("")) {
    		mainPic = ResourceService.insertResource(recommendPic, model.getId(), ResourceService.RECOMMEND, ResourceService.PICTURE);
    		model.setRecommendPic(mainPic);
    		model.update();
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 修改推荐
     * @param model
     * @return code
     */
    public static ServiceCode updateRecommend(Recommend model, String recommendPic) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	int type = model.getType();
    	int relateId = model.getRelateId();
    	int mainPic = 0;
    	String name = "";
    	
    	//推荐位置名称
    	RecommendPosition position = RecommendPosition.dao.findById(model.getRecommendpositionId());
    	String recommendPositionName = (position != null) ? position.getName() : "";
    	
    	if (type == RecommendService.ADV) {
    		Advertisement adv = Advertisement.dao.findById(relateId);
    		mainPic = adv.getMainPic();
    		name = (adv != null) ? adv.getNote() : "";
    	} else if (type == RecommendService.PRODUCT) {
    		Product product = Product.dao.findById(relateId);
    		mainPic = product.getMainPic();
    		name = (product != null) ? product.getName() : "";
    	}
    	
    	if (recommendPic != null && !recommendPic.equals("")) {
    		mainPic = ResourceService.insertResource(recommendPic, relateId, ResourceService.RECOMMEND, ResourceService.PICTURE);
		}
    	
    	model.setName(name);
    	model.setRecommendPositionName(recommendPositionName);
    	model.setRecommendPic(mainPic);
    	model.setUpdatedAt(new Date());
    	model.update();
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 删除推荐
     * @param id 推荐id
     * @return code
     */
    public static ServiceCode deleteRecommend(int id) {
    	if (Recommend.dao.deleteById(id)) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
    }
    
    public static ServiceCode batchDeleteRecommend(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						deleteRecommend(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",批量删除推荐内容失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 获取一条推荐
     * @param id
     * @return model
     */
    public static Recommend getRecommend(int id) {
    	String sql = "select a.*, b.path, b.path as recommendPic from recommend as a" + 
    			" left join resource as b on a.recommendPic = b.id" + 
    			" where a.id = " + id;
    	return Recommend.dao.findFirst(sql);
    }
    
    /**
     * 批量查询推荐记录
     * @param offset
     * @param count
     * @param recommendPosition_id
     * @param type
     * @param name
     * @param recommendPositionName
     * @param sortNumber
     * @return
     */
    public static List<Record> findRecommendItems(int offset, int count, Integer recommendPosition_id, 
    		Integer type, String name, String recommendPositionName, Integer sortNumber, 
    		Map<String, String> orderByMap) {
    	
    	String sql = findRecommendItemsSql(recommendPosition_id, type, name, recommendPositionName, 
    			sortNumber, orderByMap);
    	
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	return Db.find(sql);
    }
    
    /**
     * 批量查询推荐记录的总数量
     * @param recommendPosition_id
     * @param type
     * @param name
     * @param recommendPositionName
     * @param sortNumber
     * @return
     */
    public static int countRecommendItems(Integer recommendPosition_id, Integer type, String name, 
    		String recommendPositionName, Integer sortNumber) {
    	
    	String sql = findRecommendItemsSql(recommendPosition_id, type, name, recommendPositionName, 
    			sortNumber, null);
    	
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param recommendPosition_id
     * @param type
     * @param name
     * @param recommendPositionName
     * @param sortNumber
     * @return
     */
    private static String findRecommendItemsSql(Integer recommendPosition_id, Integer type, String name, 
    		String recommendPositionName, Integer sortNumber, Map<String, String> orderByMap) {
    	
    	String sql = "select a.*, b.path, b.path as recommendPic from recommend as a" +
    			" left join resource as b on a.recommendPic = b.id" +
    			" where a.id != 0";
    	
    	if (recommendPosition_id != null) {
			sql += " and a.recommendPosition_id = " + recommendPosition_id;
		}
    	if (type != null) {
			sql += " and a.type = " + type;
		}
    	if (name != null && !name.equals("")) {
			sql += " and a.name like '%" + name + "%'";
		}
    	if (recommendPositionName != null && !recommendPositionName.equals("")) {
			sql += " and recommendPositionName like '%" + recommendPositionName + "%'";
		}
    	if (sortNumber != null) {
			sql += " and a.sortNumber = " + sortNumber;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
    
}
