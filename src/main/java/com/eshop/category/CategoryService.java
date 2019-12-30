package com.eshop.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eshop.content.ResourceService;
import com.eshop.model.Category;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 分类管理
 * @author TangYiFeng
 */
public class CategoryService {

	private List<Category> allChildCategroy = new ArrayList<Category>();
	private List<Category> allParentCategory = new ArrayList<Category>();
	
    /**
     * 获取某个分类下面的所有子分类（不包括其自身）
     * @param cateId
     * @return
     */
    public List<Category> getAllChild(int cateId) {
    	this.addAllChildCategory(cateId);
    	return allChildCategroy;
    }
    
    /**
     * 获取某个分类下面的所有子分类（包括其自身）
     * @param cateId
     * @return
     */
    public List<Category> getAllChildAndInclude(int cateId) {
    	List<Category> list = getAllChild(cateId);
    	
    	Category category = Category.dao.findById(cateId);
    	list.add(category);
    	
    	return list;
    }
    
    /**
     * 根据分类名称获取其下面的所有子分类（不包括其自身）
     * @param cateName
     * @return
     */
    public List<Category> getAllChild(String cateName) {
    	String like = "%" + cateName + "%";
    	List<Category> list = Category.dao.find("select * from category where isDelete = ? and name like ?", 0, like);
    	
    	for (Category item : list) {
    		this.addAllChildCategory(item.getId());
    	}
    	
    	return allChildCategroy;
    }
    
    /**
     * 根据分类名称获取其下面的所有子分类（包括其自身）
     * @param cateName
     * @return
     */
    public List<Category> getAllChildAndInclude(String cateName) {
    	String sql = "select * from category where isDelete = 0 and name like concat('%'," + '"' + cateName + '"' + ",'%')";
    	System.out.println(sql);
    	
    	List<Category> list = Category.dao.find(sql);
    	
    	for (Category item : list) {
    		this.addAllChildCategory(item.getId());
    	}
    	
    	for (Category item : list) {
    		allChildCategroy.add(item);
    	}
    	
    	return allChildCategroy;
    }
    
    private void addAllChildCategory(int cateId) {
    	List<Category> list = Category.dao.find("select * from category where parent_id = ? and isDelete = ?", cateId, 0);
    	
    	for (Category item : list) {
    		allChildCategroy.add(item);
    		this.addAllChildCategory(item.getId());
    	}
    }
    
    private void addParentCategory(int id) {
    	Category model = Category.dao.findById(id);
    	
    	if (model != null) {
    		allParentCategory.add(model);
    		addParentCategory(model.getParentId());
    	}
    }
    
    /**
     * 构造whereIn子句
     * @param list
     * @return (2,3)
     */
    public static String getWhereInIds(List<Category> list) {
    	List<Integer> ids = new ArrayList<Integer>();
    	
    	for (Category item : list) {
    		if (item == null) continue;
    		ids.add(item.getId());
    	}
    	
    	return BaseDao.getWhereIn(ids);
    }
    
    /**
     * 把分类id放到集合中
     * @param cates
     * @return
     */
    public static List<Integer> getCateIds(List<Category> cates) {
    	List<Integer> list = new ArrayList<Integer>();
    	for (Category item : cates) {
    		list.add(item.getId());
    	}
    	return list;
    }
    
    /**
     * 判断该产品是否属于该分类
     * @param categoryId
     * @param productId
     * @return
     */
    public boolean isContain(int categoryId, int productId) {
    	Product product = Product.dao.findById(productId);
    	Category cate = Category.dao.findById(categoryId);
    	List<Category> categories = getAllChild(categoryId);
    	categories.add(cate);
    	
    	for (Category category : categories) {
    		if (product.getCategoryId() == category.getId()) {
				return true;
			}
    	}
    	
    	return false;
    }
    
    /**
     * 获取当前分类的所有子分类
     * @param id 当前分类id
     * @return 分类数组
     */
    public static List<Category> getNextCategories(int id) {
    	return Category.dao.find("select * from category where parent_id = ?", id);
    }
    
    /**
     * 根据分类名称获取其下面的所有父分类（包括其自身）
     * @param cateName
     * @return
     */
    public List<Category> getAllParentCategoryByIdAndInclude(int id) {
    	addParentCategory(id);
    	
    	Collections.reverse(allParentCategory);
    	
    	return allParentCategory;
    }
    
    /**
     * 获取所有一级分类及其二级分类
     * @return
     */
    public static List<Category> getAllCategory() {
    	List<Category> categories = Category.dao.find("select * from category where parent_id = 0 and isDelete = 0");
    	for(Category item : categories) {
    		//二级分类
    		List<Category> category1 = Category.dao.find("select * from category where parent_id = ? and isDelete = ?", item.getId(), 0);
    		int num = category1.size();
    		if(num > 0) {
    			item.put("subcategories",category1);
    		}
    	}
    	return categories;
    }
    
    /**
     * 根据分类名称查找产品
     * @param offset
     * @param count
     * @param cateName
     * @param sort 
     * @return
     */
    public List<Record> getProductsByCateName(int offset, int count, String cateName, String sort) {
    	String sql = getProductsByCateNameSql(cateName, sort);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	List<Record> list = Db.find(sql);
    	for(Record record : list){
    		record.set("mainPic", ResourceService.getPath(record.getNumber("mainPic").intValue()));
    	}
    	return list;
    }
    
    public int countProductsByCateName(String cateName) {
    	String sql = getProductsByCateNameSql(cateName, null);
    	return Db.find(sql).size();
    }
    
    private String getProductsByCateNameSql(String cateName, String sort) {
    	List<Category> categories = getAllChildAndInclude(cateName);
    	String whereIn = getWhereInIds(categories);
    	String sql = "select * from product where is_sale = 1 and isDelete =0 and category_id in " + whereIn;
    	if(sort != null && (sort.equals("salesVolume") || sort.equals("commentNum"))){
        	sql += " order by "+sort +" desc";
        }
        if(sort != null && (sort.equals("suggestedRetailUnitPrice") || sort.equals("suggestedRetailUnitPrice desc"))){
            sql += " order by "+sort;
        }
    	return sql;
    }
    
    /**
     * 根据分类ID查找产品
     * @param offset
     * @param count
     * @param sort 
     * @param cateName
     * @return
     */
    public List<Record> getProductsByCateId(int offset, int count, int cateId, String sort) {
    	String sql = getProductsByCateIdSql(cateId, sort);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	List<Record> list = Db.find(sql);
    	for(Record record : list){
    		record.set("mainPic", ResourceService.getPath(record.getNumber("mainPic").intValue()));
    	}
    	System.out.println(sql);
    	return list;
    }
    
    public int countProductsByCateId(int cateId) {
    	String sql = getProductsByCateIdSql(cateId, null);
    	return Db.find(sql).size();
    }
    
    /**
     * 根据品牌ID查找产品
     * @param offset
     * @param count
     * @param sort 
     * @param cateName
     * @return
     */
    public List<Record> getProductsBrandyId(int offset, int count, int brandId) {
    	String sql = getProductsByBrandIdSql(brandId);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	List<Record> list = Db.find(sql);
    	for(Record record : list){
    		record.set("mainPic", ResourceService.getPath(record.getNumber("mainPic").intValue()));
    	}
    	System.out.println(sql);
    	return list;
    }
    
    private String getProductsByBrandIdSql(int brandId) {
    	String sql = "select * from product where is_sale = 1 and  product_brand_id = " + brandId;
    	return sql;
    }
    
    private String getProductsByCateIdSql(int cateId, String sort) {
    	List<Category> categories = getAllChildAndInclude(cateId);
    	String whereIn = getWhereInIds(categories);
    	String sql = "select * from product where is_sale = 1 and  category_id in " + whereIn;
    	if(sort != null){
    		if(sort.equals("salesVolume") || sort.equals("commentNum")){
        		sql += " order by "+sort +" desc";
        	}
        	if(sort.equals("suggestedRetailUnitPrice") || sort.equals("suggestedRetailUnitPrice desc")){
            	sql += " order by "+sort;
        	}
    	}
    	System.out.println(sql);
    	return sql;
    }
    
    /**
     * 获取所有一级分类
     * @return
     */
    public static List<Category> getAllFirstCategories() {
    	return Category.dao.find("select * from category where parent_id = ? and isDelete = ?", 0, 0);
    }
    
    /**
     * 获取某分类的子分类
     * @param cateId
     * @return
     */
    public static List<Category> getChildCates(int cateId) {
    	List<Category> result = Category.dao.find("select * from category where parent_id = ? and isDelete = ?", cateId, 0);
    	
    	for (Category item : result) {
    		String pic = ResourceService.getPath(item.getMainPic());
    		item.set("mainPic", pic);
    		
    		List<Category> childCateList = Category.dao.find("select * from category where parent_id = ? and isDelete = ?", item.getId(), 0);
    		
    		for (Category childCate : childCateList) {
    			String mainPic = ResourceService.getPath(childCate.getMainPic());
    			childCate.set("mainPic", mainPic);
    		}
    		
    		item.put("childCateList", childCateList);
    	}
    	
    	return result;
    }
    
    /**
     * 获取所有产品
     * @param offset
     * @param length
     * @param sort 
     * @return
     */
	public List<Record> getProducts(int offset, int length, String sort) {
		String sql = "select * from product where  is_sale = 1 and isDelete = 0 limit ?, ?";
		if(sort != null){
			if(sort.equals("salesVolume") || sort.equals("commentNum")){
				sql = "select * from product where  is_sale = 1 and isDelete = 0 order by "+sort+ " DESC limit ?, ?";
			}
			if(sort.equals("suggestedRetailUnitPrice") || sort.equals("suggestedRetailUnitPrice desc")){
				sql = "select * from product where  is_sale = 1 and isDelete = 0 order by "+sort+ " limit ?, ?";
			}
		}
		List<Record> list = Db.find(sql, offset, length);
		for(Record record : list){
    		record.set("mainPic", ResourceService.getPath((int)record.get("mainPic")));
    	}
		return list;
	}

	public int countProducts() {
		return Db.find("select * from product").size();
	}
    
}