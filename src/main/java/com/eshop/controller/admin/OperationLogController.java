package com.eshop.controller.admin;

import java.util.List;

import com.eshop.interceptor.AdminAuthInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.eshop.model.OperationLog;

/**
 * 操作日志管理-控制器
 * @author TangYiFeng
 */
public class OperationLogController extends AdminBaseController {
	
    /** 
     * 构造方法
     */
    public OperationLogController() {
    }

    /**
     * 查找日志
     * @param token 帐户访问口令（必填）
     * @param id 分类id
     * @param parentId 父分类id
     * @param name 分类名称
     * @param sortNumber 排序
     * @param offset 页码
     * @param length 每页显示条数
     * @return 成功：{error: 0, offset: 页码, recordsTotal: 总数, recordsFiltered: 过滤后总数, data: [{id: 分类id, name: 名称, parentName: 上级分类名称, parent_id:上级分类id, sortNumber: 序号, note: 备注, mainPic:分类图片}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset", "length"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");      
    	int length = getParaToInt("length");
    	
    	Page<OperationLog> page = OperationLog.dao.paginate(offset/length + 1, length, "select *", "from operation_log order by created_at desc");
    	
    	List<OperationLog> list =page.getList();
    	int total = page.getTotalRow();
    	
    	jsonObject.put("data", list);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
}