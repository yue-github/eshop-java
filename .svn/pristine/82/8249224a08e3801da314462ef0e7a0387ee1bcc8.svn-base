package com.eshop.controller.admin;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.notice.NoticeService;
import com.jfinal.aop.Before;

/**
 * 消息通知类
 */
public class NoticeController extends AdminBaseController {
	
	public NoticeController() {}
	
	/**
	 * 创建消息
	 * @param token 账户访问口令（必填）
	 * @param customer_ids [1,2,3,....] 客户id（必填）
	 * @param msg_content 消息内容（必填）
	 * @param msg_grade 消息等级（必填）
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"customer_ids", "msg_content", "msg_grade"};
		if (!validate(params)) {
			return;
		}

		String msgContent = getPara("msg_content");
		Integer msgGrade = getParaToInt("msg_grade");
		String idsStr = getPara("customer_ids");
		
		List<String> customerIds = JSON.parseArray(idsStr, String.class);
		
		
		ServiceCode code = NoticeService.createNotice(msgContent, msgGrade, customerIds);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "发送失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 短信发送通知
	 * @param token 账户访问口令（必填）
	 * @param customer_ids [1,2,3,....] 客户id（必填）
	 * @param msg_content 消息内容（必填）
	 * @param msg_grade 消息等级（必填）
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void sendSMS() {
		String[] params = {"customer_ids", "msg_content", "msg_grade"};
		
		if (!validate(params)) {
			return;
		}
		
		String msgContent = getPara("msg_content");
		Integer msgGrade = getParaToInt("msg_grade");
		String phoneStr = getPara("customer_ids");
		List<String> phones = JSON.parseArray(phoneStr, String.class);
		
		ServiceCode code = NoticeService.sendNoticeBySMS(phones, msgGrade, msgContent);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "发送失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 删除一条消息
	 * @param token 账户访问口令（必填）
	 * @param id 消息id
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		
		if (!validate(params)) {
			return;
		}
		
		Integer id = getParaToInt("id");
		ServiceCode code = NoticeService.delete(id);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 批量删除消息
	 * @param token 帐户访问口令（必填）
     * @param ids [1,2,...] 
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	@Before(AdminAuthInterceptor.class)
	public void batchDelete() {
		String[] params = {"ids"};
		
		if (!validate(params)) {
			return;
		}
		
		String idsStr = getPara("ids");
		List<String> ids = JSON.parseArray(idsStr, String.class);
		
		ServiceCode code = NoticeService.batchNoticedelete(ids);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "失败");
		}
		
		renderJson(jsonObject);
	}
}
