package com.eshop.content;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eshop.log.Log;
import com.eshop.model.RecommendPosition;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class RecommendPositionService {
	
	public static final int HOME_BANNAR = 1;					// 首页轮播图
	public static final int HOME_RECOMMEND_PRODUCT = 2;			// 首页推荐产品
	public static final int HOME_RECOMMEND_SERVER = 4;			// 首页推荐服务
	public static final int SHOP_HOME_BANNAR = 6;				// 店铺主页轮播图
	public static final int TRAVEL_TOP = 7;						// 周边游页面位置顶部
	public static final int TRAVEL_CENTER = 8;					// 周边游页面位置中间
	public static final int TRAVEL_BOTTOM = 9;					// 周边游页面位置底部
	public static final int PROMOTION_BANNAR = 10;				// 优惠活动轮播图
	public static final int HOME_RECOMMEND_CATEGORY = 11;		// 首页推荐分类
	public static final int TRAVEL_ADV = 12;					// 周边游广告推荐
	public static final int HOME_ADV = 13;						// 首页广告
	public static final int PROMOTION_RECOMMEND_RODUCT = 14;	// 优惠活动推荐产品
	public static final int APP_HOME_ICON_RECOMMEND = 15;		// APP端首页图标推荐
	public static final int APP_HOME_ICON_TRAVEL = 16;
	public static final int APP_HOME_ICON_REGISTER = 17;
	

	/**
     * 创建位置
     * @param model
     */
    public static ServiceCode createPosition(RecommendPosition model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	if(!model.save()) {
			return ServiceCode.Failed;
		}
    	
    	return ServiceCode.Success;
    }

    /**
     * 修改位置
     * @param model
     */
    public static ServiceCode updatePosition(RecommendPosition model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	if (!model.update()) {
			return ServiceCode.Failed;
		}
    	
    	return ServiceCode.Failed;
    }

    /**
     * 删除位置
     * @param id
     * @return
     */
    public static ServiceCode deletePosition(final int id) {
    	//删除广告位，也删除相关推荐
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from recommend_position where id = ?", id);
					Db.update("delete from recommend where recommendPosition_id = ?", id);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除位置失败");
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
    public static ServiceCode batchDeletePosition(final List<String> ids) {
    	//删除广告位，也删除相关推荐
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						deletePosition(id);
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",批量删除位置失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 查看位置明细
     * @param id
     */
    public static RecommendPosition getPosition(int id) {
    	return RecommendPosition.dao.findById(id);
    }
    
    /**
     * 获取所有推荐位置
     * @return
     */
    public static List<RecommendPosition> getAll() {
    	return  RecommendPosition.dao.find("select * from recommend_position");
    }

    /**
     * 批量查询位置记录
     * @param offset
     * @param count
     * @param name
     * @param type
     * @param note
     * @return
     */
    public static List<Record> findPositionItems(int offset, int count, String name, Integer type, 
    		String note, Integer sortNumber, Map<String, String> orderByMap) {
    	
    	String sql = findPositionItemsSql(name, type, note, sortNumber, orderByMap);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询位置记录的总数量
     * @param name
     * @param type
     * @param note
     * @return
     */
    public static int countPositionItems(String name, Integer type, String note, Integer sortNumber) {
    	String sql = findPositionItemsSql(name, type, note, sortNumber, null);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param name
     * @param type
     * @param note
     * @return
     */
    private static String findPositionItemsSql(String name, Integer type, String note, Integer sortNumber,
    		Map<String, String> orderByMap) {
    	
    	String sql = "select * from recommend_position where id != 0";
    	
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	if (type != null) {
			sql += " and type = " + type;
		}
    	if (note != null && !note.equals("")) {
			sql += " and note like '%" + note + "%'";
		}
    	if (sortNumber != null) {
			sql += " and sortNumber = " + sortNumber;
		}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
	
}
