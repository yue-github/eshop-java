package com.eshop.groupcard;

import java.util.List;

import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购产品
 * @author lenovo
 *
 */
public class GroupProductService {
	
	public List<Record> list(int offset, int length, Integer prodType, Integer relateId) {
		String sql = this.sql(prodType, relateId);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		list = this.setItem(list);
		return list;
	}
	
	public int count(Integer prodType, Integer relateId) {
		String sql = this.sql(prodType, relateId);
		return Db.find(sql).size();
	}
	
	private List<Record> setItem(List<Record> list) {
		List<Record> all = Db.find("select * from resource");
		for (Record item : list) {
			Record rsc = BaseDao.findItem(item.getInt("mainPic"), all, "id");
			String path = rsc != null ? rsc.getStr("path") : "";
			item.set("mainPic", path);
		}
		return list;
	}
	
	private String sql(Integer prodType, Integer relateId) {
		String sql = "select a.*, c.name as supplier_name from product as a" +
    			" left join supplier as c on c.id = a.supplier_id" +
    			" where a.isDelete = 0";
		if (prodType != null) {
			sql += " and a.prod_type = " + prodType;
		}
		if (relateId != null) {
			sql += " and a.relate_id = " + relateId;
		}
		return sql;
	}
	
}
