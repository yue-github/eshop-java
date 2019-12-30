package com.eshop.controller.admin;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.HelpLink;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.HelpLinkService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 帮助链接控制器
 * 
 * @author
 *
 */
public class HelpLinkController extends AdminBaseController {

	/**
	 * 帮助链接列表
	 * 
	 * @param token
	 *            帐户访问口令（必填）
	 * @param offset
	 *            页码
	 * @param length
	 *            每页显示条数
	 * @param title
	 *            标题
	 * @return 成功：data:[{sortNumber:1, id:3, title:关于我, content:射手座},...]
	 */
	@Before(AdminAuthInterceptor.class)
	public void many() {
		String[] params = { "offset", "length" };

		if (!validate(params)) {
			return;
		}
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		String title = getPara("title");
		List<Record> list = HelpLinkService.findHelpLinkItems(offset, length, title);
		int total = HelpLinkService.countRoleItems(title);

		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}

	/**
	 *
	 * 获取某个链接
	 * 
	 * @param token
	 *            帐户访问口令（必填）
	 * @param id
	 * @return
	 */
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = { "id" };

		if (!validate(params)) {
			return;
		}

		int id = getParaToInt("id");
		Record result = HelpLinkService.get(id);

		jsonObject.put("data", result);
		renderJson(jsonObject);
	}

	/**
	 * 获取所有链接
	 * 
	 * @param token
	 */
	@Before(AdminAuthInterceptor.class)
	public void getAllHelpLinks() {
		List<HelpLink> lists = HelpLinkService.getAllHelpLinks();
		jsonObject.put("data", lists);
		renderJson(jsonObject);
	}

	/**
	 * 新建链接
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param sortNumber
	 *            排序
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = { "title", "content", "sortNumber" };

		if (!validate(params)) {
			return;
		}

		String title = getPara("title");
		String content = getPara("content");
		int sortNumber = getParaToInt("sortNumber");

		ServiceCode code = HelpLinkService.create(title, content, sortNumber);

		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "创建失败");
		}

		renderJson(jsonObject);
	}

	/**
	 * 修改链接
	 * 
	 * @param id
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param sortNumber
	 *            排序
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = { "id", "title", "content", "sortNumber" };

		if (!validate(params)) {
			return;
		}

		int id = getParaToInt("id");
		String title = getPara("title");
		String content = getPara("content");
		int sortNumber = getParaToInt("sortNumber");

		ServiceCode code = HelpLinkService.update(id, title, content, sortNumber);

		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "修改失败");
		}

		renderJson(jsonObject);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void delete() {
		String[] params = { "id" };

		if (!validate(params)) {
			return;
		}

		int id = getParaToInt("id");
		ServiceCode code = HelpLinkService.delete(id);

		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}

		renderJson(jsonObject);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 *            [1,2,3,...]
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void batchDelete() {
		String[] params = { "ids" };

		if (!validate(params)) {
			return;
		}

		String idsStr = getPara("ids");
		List<String> ids = JSON.parseArray(idsStr, String.class);

		ServiceCode code = HelpLinkService.batchDelete(ids);

		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "批量删除失败");
		}

		renderJson(jsonObject);
	}

}
