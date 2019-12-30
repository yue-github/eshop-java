package com.eshop.content;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.eshop.log.Log;
import com.eshop.model.Advertisement;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class AdvertisementService {

	/**
     * 创建广告
     * @param model 广告信息
     * @param path 图片路径
     * @return
     */
    public static ServiceCode createAdv(final Advertisement model, final String path) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					model.setCreatedAt(new Date());
			    	model.setUpdatedAt(new Date());
					model.save();
					
					int mainPic = ResourceService.insertResource(path, model.getId(), ResourceService.ADV, ResourceService.PICTURE);
					
					model.setMainPic(mainPic);
					model.update();
				} catch (Exception e) {
					Log.error(e.getMessage() + ",创建广告失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 修改广告
     * @param model 广告信息
     * @param path 图片路径
     * @return 
     */
    public static ServiceCode updateAdv(final Advertisement model, final String path) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from resource where id = ?", model.getMainPic());
					int mainPic = ResourceService.insertResource(path, model.getId(), ResourceService.ADV, ResourceService.PICTURE);
					model.setMainPic(mainPic);
					model.update();
					Db.update("update recommend set recommendPic = ? where type = ? and relate_Id = ?", mainPic, 4, model.getId());
				} catch (Exception e) {
					Log.error(e.getMessage() + ",修改广告失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 删除广告
     * @param id 广告id
     * @return 
     */
    public static ServiceCode deleteAdv(final int id) {
    	//删除广告，则删除相关的推荐广告和相关的图片
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from advertisement where id = ?", id);
					Db.update("delete from recommend where type = ? and relate_id = ?", 4, id);
					Db.update("delete from resource where id = ?", id);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除广告失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 批量删除广告
     * @param ids 广告ids
     * @return 
     */
    public static ServiceCode batchDeleteAdv(final List<String> ids) {
    	//删除广告，则删除相关的推荐广告和相关的图片
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						deleteAdv(id);
					}
				} catch (Exception e) {
					Log.error(e.getMessage() + ",删除广告失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 获取广告
     * @param id
     * @return model
     */
    public static Record getAdv(int id) {
    	String sql = "select a.*, b.path from advertisement as a" +
    			" left join resource as b on a.mainPic = b.id where 1=1 ";
    	sql += " and a.id = " + id;
    	return Db.findFirst(sql);
    }
    
    /**
     * 获取广告
     * @param id
     * @return
     */
    public static Advertisement get(int id) {
    	return Advertisement.dao.findById(id);
    }
    
    /**
     * 批量查询广告记录
     * @param note
     * @param url
     * @param sort_number
     * @return
     */
    public static List<Record> findAdvItems(String note, String url, Integer sort_number) {
    	String sql = findAdvItemsSql(note, url, sort_number);
    	return Db.find(sql);
    }

    /**
     * 批量查询广告记录
     * @param offset
     * @param count
     * @param note
     * @param url
     * @param sort_number
     * @return
     */
    public static List<Record> findAdvItems(int offset, int count, String note, String url, 
    		Integer sort_number) {
    	
    	String sql = findAdvItemsSql(note, url, sort_number);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询广告记录的总数量
     * @param note
     * @param url
     * @param sort_number
     * @return
     */
    public static int countAdvItems(String note, String url, Integer sort_number) {
    	String sql = findAdvItemsSql(note, url, sort_number);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param note
     * @param url
     * @param sort_number
     * @return
     */
    private static String findAdvItemsSql(String note, String url, Integer sort_number) {
    	String sql = "select a.*, a.note as name, b.path from advertisement as a " +
    			" left join resource as b on a.mainPic = b.id where 1=1 ";
    	if (note != null && !note.equals("")) {
			sql += " and a.note like '%" + note + "%'";
		}
    	if (url != null && !url.equals("")) {
			sql += " and a.url like '%" + url + "%'";
		}
    	if (sort_number != null) {
			sql += " and a.sort_number = " + sort_number;
		}
    	return sql;
    }
    
}
