package com.eshop.tax;

import java.sql.SQLException;
import java.util.List;

import com.eshop.model.Tax;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class ProductTax {

	/**
     * 获取某一条税率
     * @param id
     * @return
     */
    public static Tax get(int id) {
    	return Tax.dao.findById(id);
    }
    
    /**
     * 批量查询税率
     * @param offset
     * @param count
     * @param name
     * @param enable
     * @return
     */
    public static List<Record> findTaxItems(int offset, int count, String name, Integer enable) {
    	String sql = findTaxItemsSql(name, enable);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    /**
     * 批量查询税率
     * @param name
     * @param enable
     * @return
     */
    public static List<Record> findTaxItems(String name, Integer enable) {
    	String sql = findTaxItemsSql(name, enable);
    	return Db.find(sql);
    }
    
    /**
     * 批量查找税率的总数量
     * @param name
     * @param enable
     * @return
     */
    public static int countTaxItems(String name, Integer enable) {
    	String sql = findTaxItemsSql(name, enable);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param name
     * @param enable
     * @return
     */
    private static String findTaxItemsSql(String name, Integer enable) {
    	String sql = "select * from tax where id != 0";
    	if (name != null && !name.equals(""))
    		sql += " and name like '%" + name + "%'";
    	if (enable != null)
    		sql += " and enable = " + enable;
    	return sql;
    }
    
    /**
     * 创建税率
     * @param model
     * @return
     */
    public static ServiceCode create(Tax model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	if (!model.save()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 修改税率
     * 算法：修改类别税率时，同时修改相关产品的税率
     * @param model
     * @return
     */
    public static ServiceCode update(final Tax model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					model.update();
					
//					List<Product> products = Product.dao.find("select * from product where taxId = ?", model.getId());
//					for (Product product : products) {
//						product.setTaxRate(model.getRate());
//						Merchant.calcualteUnitCostNoTax(product);
//					}
					
					return true;
				} catch (Exception e) {
					
					return false;
				}
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 删除
     * 算法：如果该税率已经被产品使用，则不能删除
     * @param id
     * @return
     */
    public static ServiceCode delete(int id) {
    	if (Tax.dao.deleteById(id))
    		return ServiceCode.Success;
    	else
    		return ServiceCode.Failed;
    }
    
    /**
     * 批量删除
     * @param ids
     * @return
     */
    public static ServiceCode batchDelete(final List<String> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						delete(id);
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
	
}
