package com.eshop.groupcard;

import java.util.List;

import com.eshop.model.GroupSetMealProducts;
import com.eshop.model.Supplier;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 套餐产品
 * @author lenovo
 *
 */
public class GroupSetMealProductService {

	/**
	 * 创建
	 * @param product_id
	 * @param price
	 * @param amount
	 * @param group_set_meal_id
	 * @return
	 */
	public ServiceCode create(int productId, int groupActivityId, int groupSetMealId, int amount) {
		GroupSetMealProducts model = new GroupSetMealProducts();
		model.setProductId(productId);
		model.setGroupActivityId(groupActivityId);
		model.setGroupSetMealId(groupSetMealId);
		model.setAmount(amount);
		boolean flag = model.save();
		return flag ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 修改
	 * @param id
	 * @param product_id
	 * @param price
	 * @param amount
	 * @param group_set_meal_id
	 * @return
	 */
	public ServiceCode update(int id, int productId, int amount) {
		GroupSetMealProducts model = GroupSetMealProducts.dao.findById(id);
		model.setProductId(productId);
		model.setAmount(amount);
		boolean flag = model.update();
		return flag ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public ServiceCode delete(int id) {
		boolean flag = GroupSetMealProducts.dao.deleteById(id);
		return flag ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 查询
	 * @param offset
	 * @param length
	 * @param groupActivityId
	 * @param groupSetMealId
	 * @return
	 */
	public List<Record> list(int offset, int length, Integer groupSetMealId) {
		String sql = sql(groupSetMealId);
		sql = BaseDao.appendLimitSql(sql, offset, length);
		List<Record> list = Db.find(sql);
		list = setItem(list);
		return list;
	}
	
	private List<Record> setItem(List<Record> list) {
		List<Record> suppliers = Db.find("select * from supplier");
		for (Record item : list) {
			Record supplier = BaseDao.findItem(item.getInt("supplier_id"), suppliers, "id");
			String supplier_name = supplier != null ? supplier.getStr("name") : "";
			item.set("supplier_name", supplier_name);
		}
		return list;
	}
	
	public int count(Integer groupSetMealId) {
		String sql = sql(groupSetMealId);
		return Db.find(sql).size();
	}
	
	private String sql(Integer groupSetMealId) {
		String sql = "select a.*, b.name, b.suggestedRetailUnitPrice, b.unitCost, b.unitCostNoTax, b.supplier_id" + 
				" from group_set_meal_products as a" +
				" left join product as b on a.product_id = b.id" +
				" where a.id != 0";
		if (groupSetMealId != null) {
			sql += " and group_set_meal_id = " + groupSetMealId;
		}
		return sql;
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Record get(int id) {
		String sql = "select gp.*, p.name, p.supplier_id, p.unitCost, p.suggestedRetailUnitPrice, p.mainPic, p.invoiceType, p.taxRate, p.unitCostNoTax" +
				" from group_set_meal_products as gp" +
				" left join product as p on gp.product_id = p.id" +
				" where gp.id = " + id;
		Record model = Db.findFirst(sql);
		
		Supplier supplier = Supplier.dao.findById(model.getInt("supplier_id"));
		model.set("supplier_name", supplier.getName());
		
		return model;
	}
	
}
