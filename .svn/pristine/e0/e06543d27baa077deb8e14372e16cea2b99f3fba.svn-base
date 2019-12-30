package com.eshop.controller.pc;

import java.util.List;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.notice.NoticeService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;


/**
 * 消息通知类
 */
public class NoticeController extends PcBaseController {
	
	public NoticeController() {}
	
	/**
	 * 获取消息
	 * @param token 账户访问口令（必填）
	 * @param offset
	 * @param length
	 * @param id 消息id
	 * @param isRead 1已读，0未读
	 * @return 
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void many() {
		String[] params = {"offset", "length"};
		if (!this.validateRequiredString("token") || !validate(params)) {
    		return;
    	}
		
		String token = getPara("token");
		Integer offset = getParaToInt("offset");
		Integer length = getParaToInt("length");
		Integer noticeId = getParaToInt("id");
		Integer isRead = getParaToInt("isRead");
		Customer customer = (Customer) CacheHelper.get(token);
		int customerId = customer.getId();
		
		List<Record> list = NoticeService.findNotice(offset, length, customerId, noticeId, isRead);
		int total = NoticeService.count(customerId, isRead);
		
		jsonObject.put("totalRow", total);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	/**
	 * 获取某条消息
	 * @param token 账户访问口令
	 * @param id 消息id
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		if (!this.validateRequiredString("token") || !validate(params) ) {
    		return;
    	}
		
		Integer noticeId = getParaToInt("id");
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		Integer customerId = customer.getId();
		
		Record notice = NoticeService.getNoice(customerId, noticeId);
		
		jsonObject.put("data", notice);
		renderJson(jsonObject);
	}
	
	/**
	 * 未读消息总数
	 * @return
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void noReadCount() {
		if (!this.validateRequiredString("token")) {
    		return;
    	}
		
		String token = getPara("token");
		Customer customer = (Customer) CacheHelper.get(token);
		
		int count = NoticeService.count(customer.getId(), 0);
		jsonObject.put("noReadCount", count);
		renderJson(jsonObject);
	}
	
	/**
	 * 消息已读
	 * @param id 消息id
	 * @return
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void update() {
		String[] params = {"id"};
		if (!this.validateRequiredString("token") || !validate(params)) {
    		return;
    	}
		
		String token = getPara("token");
		Integer noticeId = getParaToInt("id");
		Customer customer = (Customer) CacheHelper.get(token);
		
		ServiceCode code = NoticeService.updateRead(customer.getId(), noticeId);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "修改失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 删除一条消息
	 * @param token 帐户访问口令（必填）
     * @param id 消息id（必填）
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		
		if (!validateRequiredString("token") || !validate(params)) {
			return;
		}
		
		String token = getPara("token");
		Integer id = getParaToInt("id");
		
		Customer customer = (Customer) CacheHelper.get(token);
		Integer customerId = customer.getId();
		
		ServiceCode code = NoticeService.delete(id, customerId);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
	}
}
