package com.eshop.content;

import java.util.Date;
import java.util.List;

import com.eshop.model.Resource;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ResourceService {
	
	//分类 category
	public final static int PRODUCT = 1;
	public final static int SERVICE = 2;
	public final static int COMMENT = 3;
	public final static int ADV = 6;
	public final static int RETURNED = 7;
	public final static int REFUND = 8;
	public final static int RECOMMEND = 9;
	public final static int CATEGORY = 10;
	public final static int PROMOTION = 11;
	public final static int PROMOTION_POSITION = 11;
	public final static int SHOP_LOGO = 20;
	public final static int SHOP_IDCARD = 21;
	public final static int SHOP_LICENSE = 22;
	
	//文件类型 type
	public final static int PICTURE = 1;
	public final static int FILE = 2;
	public final static int VIDEO = 3;

	/**
     * 添加图片资源
     * @param path 图片路径
     * @param relateId 关联id
     */
    public static int insertResource(String path, int relateId, int category, int fileType) {
    	Resource res = new Resource();
		res.setCategory(category);
		res.setPath(path);
		res.setRelateId(relateId);
		res.setType(fileType);
		res.setCreatedAt(new Date());
		res.setUpdatedAt(new Date());
		res.save();
		return res.getId();
    }
    
    /**
     * 查看资源详情
     * @param id
     * @return
     */
    public static Resource get(int id) {
    	return Resource.dao.findById(id);
    }
    
    /**
     * 获取所有资源
     * @return
     */
    public static List<Record> getAll() {
    	return findResourceItems(null, null, null);
    }
    
    /**
     * 批量查询资源
     * @param category
     * @param type
     * @param relateId
     * @return
     */
    public static List<Record> findResourceItems(Integer category, Integer type, Integer relateId) {
    	String sql = findResourceItemsSql(category, type, relateId);
    	return Db.find(sql);
    }
    
    private static String findResourceItemsSql(Integer category, Integer type, Integer relateId) {
    	String sql = "select * from resource where id != 0";
    	
    	if (category != null) {
			sql += " and category = " + category;
		}
    	if (type != null) {
			sql += " and type = " + type;
		}
    	if (relateId != null) {
			sql += " and relate_id = " + relateId;
		}
    	
    	return sql;
    }
    
    /**
	 * 获取图片路径
	 * @param id
	 * @return
	 */
	public static String getPathByResId(int id) {
		Resource res = Resource.dao.findById(id);
		String path = res != null ? res.getPath() : "";
		return path;
	}
	
	/**
	 * 获取图片路径
	 * @param id
	 * @return
	 */
	public static String getPath(int id) {
		return getPathByResId(id);
	}
	
}
