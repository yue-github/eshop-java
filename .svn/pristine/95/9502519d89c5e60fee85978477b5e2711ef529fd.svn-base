package com.eshop.controller.admin;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Category;
import com.eshop.model.Property;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 属性管理
 * 
 * @author TangYiFeng
 */
public class PropertyController extends AdminBaseController {

	/**
	 * Default constructor
	 */
	public PropertyController() {
	}

	/**
	 * 获取所有产品属性
	 * 
	 * @param token
	 *            帐户访问口令（必填）
	 * @param category_id
	 *            分类id
	 * @param name
	 *            关键字(选填)
	 * @param offset
	 *            页码
	 * @param length
	 *            每页显示条数
	 * @return 成功：{error: 0, offset: 页码, totalRow: 总数, recordsFiltered: 过滤后总数,
	 *         data: [{id:id, name:属性名称, categoryName:分类名称， category_id:分类id，
	 *         sortNumber:排序值,
	 *         is_sale_pro:是否销售属性，parent_id:父id，created_at：创建时间},
	 *         ...]}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void many() {
		String[] params = { "offset", "length" };

		if (!validate(params)) {
			return;
		}

		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer categoryId = getParaToIntegerDefault("category_id");
		String name = getPara("name");
		Integer sortNumber = getParaToIntegerDefault("sortNumber");
		Integer is_sale_pro = getParaToIntegerDefault("is_sale_pro");
		String categoryName = getPara("categoryName");
		
		Map<String, String> orderByMap = new HashMap<String, String>();
		orderByMap.put("a.created_at", "asc");

		List<Record> list = Manager.findPropertyItems(offset, length, categoryId, name, is_sale_pro, sortNumber, null, 
				orderByMap, categoryName);
		int total = Manager.countPropertyItems(categoryId, name, null, null, null);

		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		renderJson(jsonObject);
	}

	/**
	 * 创建产品属性
	 * 
	 * @param token
	 *            帐户访问口令（必填）
	 * @param name
	 *            名称(必填)
	 * @param categoryId
	 *            分类id(必填)
	 * @param sortNumber
	 *            排序(必填)
	 * @param isSalePro
	 *            是否销售属性(必填)
	 * @param parentId
	 *            父id(必填)，0表示顶级
	 * @return 成功：{error:0,} 失败：{error:>0, errmsg：错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = { "name", "categoryId", "sortNumber", "isSalePro", "parentId" };

		if (!validate(params)) {
			return;
		}

		String name = getPara("name");
		int categoryId = getParaToInt("categoryId");
		int sortNumber = getParaToInt("sortNumber");
		int isSalePro = getParaToInt("isSalePro");
		int parentId = getParaToInt("parentId");

		Property model = new Property();
		model.setName(name);
		model.setCategoryId(categoryId);
		model.setSortNumber(sortNumber);
		model.setIsSalePro(isSalePro);
		model.setParentId(parentId);

		ServiceCode code = Manager.createProperty(model);

		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "创建失败");
		}

		renderJson(jsonObject);
	}

	/**
	 * 修改产品属性
	 * 
	 * @param token
	 *            帐户访问口令（必填）
	 * @param id
	 *            产品属性id(必填)
	 * @param name
	 *            名称(必填)
	 * @param categoryId
	 *            分类id(必填)
	 * @param sortNumber
	 *            排序(必填)
	 * @param isSalePro
	 *            是否销售属性(必填)
	 * @param parentId
	 *            父id(必填)，0表示顶级
	 * @return 成功：{error:0,} 失败：{error:>0, errmsg：错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = { "id", "name", "categoryId", "sortNumber", "isSalePro", "parentId" };

		if (!validate(params)) {
			return;
		}

		int id = getParaToInt("id");
		String name = getPara("name");
		int categoryId = getParaToInt("categoryId");
		int sortNumber = getParaToInt("sortNumber");
		int isSalePro = getParaToInt("isSalePro");
		int parentId = getParaToInt("parentId");

		Property model = Manager.getProperty(id);
		model.setName(name);
		model.setCategoryId(categoryId);
		model.setSortNumber(sortNumber);
		model.setIsSalePro(isSalePro);
		model.setParentId(parentId);

		ServiceCode code = Manager.updateProperty(model);

		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "修改失败");
		}

		renderJson(jsonObject);
	}

	/**
	 * 获取属性详情
	 * 
	 * @param token
	 *            帐户访问口令（必填）
	 * @param id
	 *            属性id(必填)
	 * @return 成功：{error:0, data:{id:id, name:属性名称, category_id:分类id，
	 *         sortNumber:排序值,
	 *         is_sale_pro:是否销售属性，parent_id:父id，created_at：创建时间}} 失败：{error:>0,
	 *         errmsg：错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = { "id" };

		if (!validate(params)) {
			return;
		}

		int id = getParaToInt("id");

		Property model = Manager.getProperty(id);

		jsonObject.put("data", model);
		renderJson(jsonObject);
	}

	/**
	 * 批量删除产品属性
	 * 
	 * @param token
	 *            帐户访问口令（必填）
	 * @param ids
	 *            格式：[id,id,...] (必填)
	 * @return 成功：{error:0,} 失败：{error:>0, errmsg：错误信息}
	 */
	public void batchDelete() {
		String[] params = { "id" };

		if (!validate(params)) {
			return;
		}

		String idsStr = getPara("ids");
		List<String> ids = JSON.parseArray(idsStr, String.class);

		ServiceCode code = Manager.batchDeleteProperty(ids);

		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "批量删除失败");
		}

		renderJson(jsonObject);
	}

	/**
	 * 获取所有的分类
	 * @return 成功：{error: 0, data: [{id: 分类id, name: 名称, sortNumber: 序号, note:
	 *         备注}, ...]}；失败：{error: >0, errmsg: 错误信息}
	 */
	public void getAllCategories() {
		String name = getPara("name");
		Integer sortNumber = getParaToIntegerDefault("sortNumber");
		List<Record> list = Manager.findCategoryItems(null, name, null, sortNumber, 0, null);
		
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}

	/**
	 * 获取分类树
	 * @return 成功：[{"id": 1, "icon": "fa fa-folder icon-lg icon-state-success",
	 *         "text": "分类1", "state": ["opened": false,"disabled": false],
	 *         "children": false}]
	 */
	public void categoryTree() {
		String parent = getPara("parent");
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		if (parent.equals("#")) {
			Map<String, Object> m = createNode(0, -1, "全部", true);
			data.add(m);
		} else {
			int parent_id = Integer.parseInt(parent);
			List<Record> list = Db.find(
					"select a.id, a.name, a.parent_id, (select count(c.id) from category as c where c.parent_id = a.id and c.isDelete = 0 ) as children from category as a where parent_id = ? and a.isDelete = 0",
					parent_id);

			for (Record item : list) {
				Map<String, Object> m = createNode(item.getInt("id").intValue(), item.getInt("parent_id").intValue(),
						item.getStr("name"), item.getLong("children") > 0);
				data.add(m);
			}
		}

		renderJson(data);
	}

	public void getChildCategories() {
		String parent = getPara("parent");
		List<Category> list = Category.dao.find("select * from category where parent_id = ? and isDelete = 0", parent);
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}

	/**
	 * 创建树节点
	 * 
	 * @param id
	 * @param text
	 * @param children
	 * @param type
	 * @return
	 */
	private Map<String, Object> createNode(int id, int parent_id, String text, boolean children) {
		Map<String, Object> state = new HashMap<String, Object>();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", id + "");
		m.put("text", text);
		m.put("icon", "fa fa-folder icon-lg icon-state-success");
		m.put("children", children);

		state.put("opened", parent_id < 0);
		state.put("disabled", false);
		m.put("state", state);

		return m;
	}

}