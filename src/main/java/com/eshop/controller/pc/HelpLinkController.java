package com.eshop.controller.pc;

import java.util.List;

import com.eshop.model.HelpLink;
import com.eshop.service.HelpLinkService;
import com.jfinal.plugin.activerecord.Record;

/**
 * 帮助链接控制器
 * @author 
 *
 */
public class HelpLinkController extends PcBaseController{

	
	/**
	 * 获取所有帮助链接
	 * @return
	 */
	public void many() {
		List<HelpLink> list = HelpLinkService.getAllHelpLinks();
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
	//@Before(CustomerPcAuthInterceptor.class)
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

}
