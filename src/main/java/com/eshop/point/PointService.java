package com.eshop.point;

import java.util.Date;
import java.util.List;

import com.eshop.content.ResourceService;
import com.eshop.helper.DateHelper;
import com.eshop.model.PointProduct;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 积分兑换
 *
 */
public class PointService {
	
	/**
	 * 创建
	 * @param model
	 * @return
	 */
	public ServiceCode create(PointProduct model) {
		try {
			model.save();
			return ServiceCode.Success;
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 修改
	 * @param model
	 * @return
	 */
	public ServiceCode update(PointProduct model) {
		try {
			model.update();
			return ServiceCode.Success;
		} catch (Exception e) {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public ServiceCode delete(int id) {
		try {
			PointProduct.dao.deleteById(id);
			return ServiceCode.Success;
		} catch (Exception e) {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public PointProduct get(int id) {
		return PointProduct.dao.findById(id);
	}
	
	/**
	 * 查询
	 * @param offset
	 * @param length
	 * @return
	 */
	public List<Record> list(int offset, int length, Integer productId, String isShow, Boolean isValid) {
		String sql = sql(productId, isShow, isValid);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		list = setItem(list);
		return list;
	}
	
	public List<Record> setItem(List<Record> list) {
		for (Record item : list) {
			String mainPic = ResourceService.getPath(item.getInt("mainPic"));
			item.set("mainPic", mainPic);
		}
		return list;
	}
	
	/**
	 * 记录总条数
	 * @param title
	 * @return
	 */
	public int count(Integer productId, String isShow, Boolean isValid) {
		String sql = sql(productId, isShow, isValid);
		return Db.find(sql).size();
	}
	
	private String sql(Integer productId, String isShow, Boolean isValid) {
		String sql = "select a.*, b.name as product_name, b.mainPic, b.suggestedRetailUnitPrice," +
				" b.id as productId" +
				" from point_product as a" +
				" left join product as b on a.product_id = b.id" +
				" where a.id != 0";
		if (productId != null) {
			sql += " and a.product_id = " + productId;
		}
		if (isShow != null && !isShow.equals("")) {
			sql += " and is_show = '" + isShow + "'";
		}
		if (isValid != null && isValid == true) {
			String now = DateHelper.formatDateTime(new Date());
			sql += " and start_time <= '" + now + "'";
			sql += " and end_time >= '" + now + "'";
		}
		return sql;
	}
	
}
