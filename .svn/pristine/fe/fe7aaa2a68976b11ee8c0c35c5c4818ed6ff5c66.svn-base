package com.eshop.controller.admin;

import java.util.Date;
import com.eshop.model.Searchword;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.searchword.SearchWordService;
import com.jfinal.plugin.activerecord.Page;

public class SearchWordController extends AdminBaseController {

	public void getList() {
		String name = getPara("name");
		Integer pageIndex = getParaToInt("pageIndex");
		Integer pageSize = getParaToInt("length");
		Page<Searchword> list = SearchWordService.getList(pageIndex, pageSize, name);
		jsonObject.put("data", list.getList());
		jsonObject.put("pageIndex", list.getPageNumber());
		jsonObject.put("total", list.getTotalRow());
		renderJson(jsonObject);
	}
	public void add() {
		String name = getPara("name");
		Integer num = getParaToInt("sort_num");
		Searchword searchword = new Searchword();
		searchword.setUpdatedAt(new Date());
		searchword.setCreatedAt(new Date());
		searchword.setName(name);
		searchword.setSortNum(num);
		ServiceCode code = SearchWordService.add(searchword);
		if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "新增失败");
    	}
		renderJson(jsonObject);
	}
	public void update() {
		String name = getPara("name");
		Integer num = getParaToInt("sort_num");
		Integer id = getParaToInt("id");
		Searchword searchword = SearchWordService.get(id);
		searchword.setUpdatedAt(new Date());
		searchword.setName(name);
		searchword.setSortNum(num);
		ServiceCode code = SearchWordService.update(searchword);
		if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改失败");
    	}
		renderJson(jsonObject);
	}
	public void delete() {
		Integer id = getParaToInt("id");
		ServiceCode code = SearchWordService.delete(id);
		if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
		renderJson(jsonObject);
	}
	public void batchDelete() {
		String idstr = getPara("ids");
		String[] ids = idstr.split(",");
		ServiceCode code = SearchWordService.batchDelete(ids);
		if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "删除失败");
    	}
		renderJson(jsonObject);
	}
	public void get() {
		Integer id = getParaToInt("id");
		Searchword word = SearchWordService.get(id);
		jsonObject.put("data", word);
		renderJson(jsonObject);
	}
}
