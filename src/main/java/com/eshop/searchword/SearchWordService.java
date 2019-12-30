package com.eshop.searchword;

import java.sql.SQLException;
import java.util.List;

import com.eshop.helper.QueryHelper;
import com.eshop.model.Searchword;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;

public class SearchWordService {

	/**
	 * 查询
	 * @param pageNumber
	 * @param pageSize
	 * @param name
	 * @return
	 */
	public static Page<Searchword> getList(Integer pageNumber, Integer pageSize, String name){
		QueryHelper helper = new QueryHelper("searchword", "s");
		if(!"".equals(name) && name != null) {
			helper.addCondition("s.name like ?", "%"+name+"%");
		}
		helper.addOrderByProperty("s.sort_num", "asc");
		System.out.println(helper.getQuerySql());
		Page<Searchword> page = Searchword.dao.paginate(pageNumber, pageSize, " select * ", helper.getQuerySql(), helper.getParams().toArray());
		return page;
	}
	
	/**
	 * 新增
	 * @param word
	 * @return
	 */
	public static ServiceCode add(Searchword word) {
		if(!word.save()) {
			return ServiceCode.Failed;
		}
		return ServiceCode.Failed;
	}
	
	/**
	 * 更新
	 * @param word
	 * @return
	 */
	public static ServiceCode update(Searchword word) {
		if(!word.update()) {
			return ServiceCode.Failed;
		}
		return ServiceCode.Failed;
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public static ServiceCode delete(Integer id) {
		if(!Searchword.dao.deleteById(id)) {
			return ServiceCode.Failed;
		}
		return ServiceCode.Success;
	}
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	public static ServiceCode batchDelete(final String[] ids) {
			
		  boolean success = Db.tx(new IAtom() {
				
				@Override
				public boolean run() throws SQLException {
					try {
						for (String item : ids) {
							int id = Integer.parseInt(item);
							ServiceCode code = delete(id);
							if (ServiceCode.Success != code) {
								return false;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					return true;
				}
			});
		  return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	/**
	 * 根据id查找
	 * @param id
	 * @return
	 */
	public static Searchword get(Integer id) {
		Searchword word = Searchword.dao.findById(id);
		return word;
	}
	/**
	 * 获取热门关键字
	 * @param id
	 * @return
	 */
	public static List<Searchword> searchword() {
		String sql = "select name from searchword order by sort_num asc limit 0,8";
		List<Searchword> words = Searchword.dao.find(sql);
		return words;
	}
}
