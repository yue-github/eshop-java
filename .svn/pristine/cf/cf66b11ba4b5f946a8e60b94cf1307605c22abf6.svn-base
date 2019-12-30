package com.eshop.controller.admin;

import java.util.Date;
import java.util.Map;

import com.eshop.helper.HelgaHelper;
import com.eshop.model.CustomerVisit;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.visit.CustomerVisitService;
import com.jfinal.plugin.activerecord.Page;

/**
 * 
 * @author Helga
 *
 */
public class CustomerVisitController  extends AdminBaseController {
	/**
	*会员访问 -- customer_visit(表名)
	*会员页面访问记录添加
    *@param customerId 用户id
    *@param customerName 会员姓名
    *@param page 页面名称
    *@param source 访问来源（1pc、2微信端、3android、4苹果）
    *@return  成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	*/
	public void visitPage() {
		ServiceCode code = null;
		try {
			String ip = HelgaHelper.getIPAddr(getRequest());
			Integer customerId = getParaToInt("customer_id");
			String customerName = getPara("customer_name");
			String page = getPara("page");
			Integer source = getParaToInt("source");
			Date createAt = new Date();
			CustomerVisit model = new CustomerVisit();
			model.setIp(ip);
			model.setCustomerId(customerId);
			model.setCustomerName(customerName);
			model.setPage(page);
			model.setSource(source);
			model.setCreateAt(createAt);
			code = CustomerVisitService.visitPage(model);
		} catch (Exception e) {
			e.printStackTrace();
			setError(ErrorCode.Exception, "失败");
		}
		if(code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "失败");
    	}
    	
    	renderJson(jsonObject);
		//
	}
	
/**
	*会员访问 -- customer_visit(表名)
	*会员页面访问记录
	*@param pageIndex 开始页数 
	*@param length
    *@param startTime开始时间
    *@param endTime 结束时间
    *@param page 页面名称
    *@return  成功：{"error":0,"data":[{"id":id值,"source":"访问来源（1pc、2微信端、3android、4苹果"）,"page":"访问的页面","create_at":"访问时间","customer_name":"会员名称","customer_id":会员id,"ip":"会员ip"}],"totalRow":总页数,"offset":当前页数}；失败：{error: >0, errmsg: 错误信息}
	*/
	public void customerVisitList() {
		String[] params = {"pageIndex", "length"};
		if (!validate(params)) {
			return;
		}
		int pageIndex = getParaToInt("pageIndex");
		int length = getParaToInt("length");
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		String page = getPara("page");
		Page<CustomerVisit> list = CustomerVisitService.findCustomerVisits(pageIndex, length, startTime, endTime, page);
		//将数据封装
		jsonObject.put("pageIndex", pageIndex);
		jsonObject.put("totalRow", list.getTotalRow());
		jsonObject.put("data", list.getList());
		//返回
		renderJson(jsonObject);
	}
	
	/**
	 * 统计页面访问的IP/UV/PV
	 *@param startTime开始时间
     *@param endTime 结束时间
	 */
	public void count() {
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		System.out.println(startTime);
		System.out.println(endTime);
		Map<String, Integer> count = CustomerVisitService.count(startTime, endTime);
		jsonObject.putAll(count);
		renderJson(jsonObject);
	}
	
	/**
	 * 统计页面访问数量和来源数量
	 * @param offset
	 * @param length
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @return
	 */
	public void countPageAndSource() {
		/*String[] params = {"pageIndex", "length"};
		if (!validate(params)) {
			return;
		}
		int pageIndex = getParaToInt("pageIndex");
		int length = getParaToInt("length");*/
		String startTime = getParaToDateTimeDefault("startTime");
		String endTime = getParaToDateTimeDefault("endTime");
		Map<String, Object> result = CustomerVisitService.countPageAndSource(startTime, endTime);
		jsonObject.put("data", result);
		renderJson(jsonObject);
	}

	
}
