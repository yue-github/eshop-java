package com.eshop.promotion;

import java.sql.SQLException;
import java.util.*;
import com.eshop.log.Log;
import com.eshop.model.Category;
import com.eshop.model.Product;
import com.eshop.model.ProductPrice;
import com.eshop.model.PromotionCondition;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 促销条件
 */
public class PromotionConditionService {
	
	public static final int TYPE_PRODUCT = 2;	// 类型
	public static final int TYPE_CATEGORY = 3;
	public static final int TYPE_SKU = 4;

	private static String getName(int type, int objectId) {
		String name = "";
		switch (type) {
		case TYPE_PRODUCT:
			Product product = Product.dao.findById(objectId);
			name = product.getName();
			break;
		case TYPE_CATEGORY:
			Category category = Category.dao.findById(objectId);
			name = category.getName();
			break;
		case TYPE_SKU:
			ProductPrice sku = ProductPrice.dao.findById(objectId);
			Product pd = Product.dao.findById(sku.getProductId());
			name = pd.getName();
			break;
		}
		return name;
	}
	
    /**
     * 创建促销条件
     * @param model
     * @return
     */
    public static ServiceCode create(PromotionCondition model) {
        if (model == null) {
			return ServiceCode.Failed;
		}
        
        String name = getName(model.getType(), model.getObjectId());
        model.setName(name);
        
        if (!model.save()) {
			return ServiceCode.Failed;
		}
        
        return ServiceCode.Success;
    }
    
    /**
     * 批量创建
     * @param model
     * @return
     */
    public static ServiceCode batchCreate( final  List<Map> objects) {
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
			
				try {
					//批量插入
					for(Map object: objects) {
						
							PromotionCondition model = new PromotionCondition();
							model.setPromotionId(Integer.parseInt(object.get("promotionId").toString()));
							model.setType(Integer.parseInt(object.get("type").toString()));
							model.setObjectId(Integer.parseInt(object.get("id").toString()));
							model.setName(object.get("name").toString());
							model.save();
	
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + "创建失败");
					return false;
				}
				return true;
			}
		});
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
    
    /**
     * 修改促销条件
     * @param model
     * @return
     */
    public static ServiceCode update(PromotionCondition model) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	String name = getName(model.getType(), model.getObjectId());
        model.setName(name);
        
        if (!model.update()) {
			return ServiceCode.Failed;
		}
        
        return ServiceCode.Success;
    }

    /**
     * 查看促销条件详情
     * @param id
     * @return
     */
    public static PromotionCondition get(int id) {
        return PromotionCondition.dao.findById(id);
    }

    /**
     * 删除促销条件
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
        if (PromotionCondition.dao.deleteById(id)) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
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
						PromotionCondition.dao.deleteById(id);
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 批量查询促销条件
     * @param offset
     * @param count
     * @param promotionId
     * @param type
     * @param name
     * @return
     */
    public static List<Record> findPromotionConditionItems(int offset, int count, Integer promotionId, Integer type, String name) {
        String sql = findPromotionConditionItemsSql(promotionId, type, name);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        return Db.find(sql);
    }
    
    /**
     * 批量查询促销条件
     * @param promotionId
     * @param type
     * @param name
     * @return
     */
    public static List<Record> findPromotionConditionItems(Integer promotionId, Integer type, String name) {
        String sql = findPromotionConditionItemsSql(promotionId, type, name);
        return Db.find(sql);
    }
    
    /**
     * 批量查询促销条件的总数量
     * @param promotionId
     * @param type
     * @param name
     * @return
     */
    public static int countPromotionConditionItems(Integer promotionId, Integer type, String name) {
        String sql = findPromotionConditionItemsSql(promotionId, type, name);
        return Db.find(sql).size();
    }
    
    /**
     * 获取店铺适合促销活动的商品
     * @param shopId
     * @param promotionId
     * @param type
     * @return
     */
	public static Page<Product> getAllProject(Integer pageIndex, Integer length, int shopId, String promotionId, String type) {
		
		//isDelete 是否已上架，0下架，1上架
		//is_sale 是否删除，0未删除，1已删除
		Object[] params = {shopId, type, promotionId};
		String sql = "FROM product AS a LEFT JOIN resource AS e ON a.mainPic=e.id  WHERE a.isDelete=0 AND a.is_sale=1 AND a.shop_id=? AND a.id NOT IN(SELECT o.object_id FROM promotion_condition AS o WHERE o.type=? AND o.promotion_id=?)"; 
		//String sql = "SELECT a.id, a.name,a.mainPic,b.object_id FROM (SELECT id,NAME,mainPic FROM product WHERE isDelete=0 AND is_sale=1 AND shop_id=?) AS a LEFT JOIN (SELECT object_id FROM promotion_condition WHERE TYPE=? AND promotion_id=?)AS b " + 
				//" ON a.id=b.object_id " + 
				//" WHERE b.object_id IS NULL";
		Page<Product> list = Product.dao.paginate(pageIndex, length, "SELECT a.id, a.name, e.path AS mainPic ", sql, params);
		System.out.println(list.getList());
		System.out.println(promotionId);
		return list;
		
	}
	 /**
     * 获取店铺适合促销活动的类型
     * @param promotionId
     * @param type
     * @return
     */
	public static Page<Category> getAllCategory(Integer pageIndex, Integer length, String promotionId, String type) {
		
		//isDelete 是否已上架，0下架，1上架
		Object[] params = {type, promotionId};
		String sql = " FROM category AS a LEFT JOIN resource AS e ON a.mainPic=e.id WHERE a.isDelete=0 AND a.id NOT IN(SELECT o.object_id FROM promotion_condition AS o WHERE o.type=? AND o.promotion_id=?)"; 
		Page<Category> list = Category.dao.paginate(pageIndex, length, "SELECT a.id, a.name, e.path AS manPic ", sql, params);
		return list;
	}
	
	
    

    /**
     * 组装sql语句
     * @param promotionId
     * @param type
     * @param name
     * @return
     */
    public static String findPromotionConditionItemsSql(Integer promotionId, Integer type, String name) {
        String sql = "select * from promotion_condition where id != 0";
        if (promotionId != null) {
			sql += " and promotion_id = " + promotionId;
		}
        if (type != null) {
			sql += " and type = " + type;
		}
        if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
        return sql;
    }


}