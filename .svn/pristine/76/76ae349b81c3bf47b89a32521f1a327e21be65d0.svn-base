package com.eshop.controller.admin;

import java.math.BigDecimal;
import java.util.*;

import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.membership.CustomerGoldService;
import com.eshop.membership.CustomerGradeService;
import com.eshop.membership.CustomerGrowthService;
import com.eshop.membership.CustomerPointRuleService;
import com.eshop.membership.CustomerPointService;
import com.eshop.model.Customer;
import com.eshop.model.CustomerGrade;
import com.eshop.model.CustomerPointRule;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Manager;
import com.eshop.service.Member;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 会员管理-控制器
 * @author TangYiFeng
 */
public class CustomerController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public CustomerController() {
    }

    /**
     * 获取所有客户
     * @param token 帐户访问口令（必填）
     * @param offset
     * @param length
     * @param name
     * @param gender 0未知，1男，2女
     * @param phone
     * @param email
     * @param nickName
     * @param weiXinOpenId
     * @param mobilePhone
     * @return 成功：{error: 0, offset: 页码, totalRow: 总数, data: [{id: 客户id, headImg: 头像, nickName: 昵称, mobilePhone: 手机号, name: 姓名, gender: 姓别, dob: 出生日期, disable: 是否禁用}, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	String[] params = {"offset"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	Integer offset = getParaToInt("offset");      
    	Integer length = getParaToInt("length");
    	String name = getPara("name");
    	Integer gender = getParaToIntegerDefault("gender");
    	String phone = getPara("phone");
    	String email = getPara("email");
    	String nickName = getPara("nickName");
    	String weiXinOpenId = getPara("weiXinOpenId");
    	String mobilePhone = getPara("mobilePhone");
    	
    	List<Record> list = Manager.findCustomerItems(offset, length, name, gender, phone, email, nickName, null, weiXinOpenId, mobilePhone);
    	int total = Manager.countCustomerItems(name, gender, phone, email, nickName, weiXinOpenId, mobilePhone);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取成交用户
     * @param token 帐户访问口令（必填）
     * @param offset
     * @param length
     * @param phone
     * @param email
     */
    @Before(AdminAuthInterceptor.class)
    public void getTransactionCustomer(){
    	Integer offset = getParaToInt("offset");      
    	Integer length = getParaToInt("length");
    	String phone = getPara("phone");
    	String email = getPara("email");
    	List<Record> list = Manager.findCustomerItemsByOrder(offset, length, phone, email);
    	int total = Manager.countCustomerItemsByOrder(phone, email);
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    
    /**
     * 用id获取客户
     * @param token 帐户访问口令（必填）
     * @param id 客户id（必填）
     * @return 成功：{error: 0, customer: {id: 客户id, headImg: 头像, nickName: 昵称, mobilePhone: 手机号, name: 姓名, gender: 姓别, dob: 出生日期, disable: 是否禁用}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	Customer model = Member.getCustomer(id);
    	
    	jsonObject.put("customer", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改会员信息
     * @param token 帐户访问口令（必填）
     * @param id 会员id（必填）
     * @param name 姓名
     * @param gender 姓别
     * @param dob 出生日期
     * @param disable 是否禁用
     * @param grade 等级
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	String[] params = {"id", "disable"};
    	
    	if (!validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int disable = getParaToInt("disable");
    	String grade = getPara("grade");
    	Customer model = Member.getCustomer(id);
    	
    	if (model == null) {
    		setError(ErrorCode.Exception, "该客户不存在");
    		return;
    	}
    	
    	model.set("disable", disable);
    	model.set("grade", grade);
    	ServiceCode code = Member.updateInfo(model);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "修改客户信息失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取金币明细
     * @param token
     * @param offset
     * @param count
     * @param customerId
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void getGold() {
    	String[] params = {"offset", "count"};
    	
    	if (!validate(params)) {
			return;
		}
    	
    	int offset = getParaToInt("offset");      
    	int count = getParaToInt("count");
    	Integer customerId = getParaToIntegerDefault("customerId");
    	String name = getPara("userName");
    	if (customerId == null || customerId <= 0) {
    		customerId = null;
		}
    	
    	List<Record> list = CustomerGoldService.findCustomerGoldItems(offset, count, customerId, null, null, null, null, null, name);
    	int total = CustomerGoldService.countCustomerGoldItems(null, null, null, null, null, null, name);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("count", count);
    	jsonObject.put("data", list);
    	jsonObject.put("totalRow", total);
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取积分明细
     * @param token
     * @param offset
     * @param count
     * @param customerId
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void getPoint() {
    	String[] params = {"offset", "count"};
    	
    	if (!validate(params)) {
			return;
		}
    	
    	int offset = getParaToInt("offset");      
    	int count = getParaToInt("count");
    	Integer customerId = getParaToIntegerDefault("customerId");
    	String name = getPara("userName");
    	
    	if (customerId == null || customerId <= 0) {
    		customerId = null;
		}
    	
    	List<Record> list = CustomerPointService.findCustomerPointItems(offset, count, customerId, null, null, null, null, null, name);
    	int total = CustomerPointService.countCustomerPointItems(null, null, null, null, null, null, name);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("count", count);
    	jsonObject.put("data", list);
    	jsonObject.put("totalRow", total);
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取积分规则
     * @param token
     * @param offset
     * @param count
     * @param code
     * @param note
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void getPointRule() {
    	String[] params = {"offset", "count"};
    	
    	if (!validate(params)) {
			return;
		}
    	
    	int offset = getParaToInt("offset");      
    	int count = getParaToInt("count");
    	Integer code = getParaToIntegerDefault("code");
    	String note = getPara("note");
    	
    	if (code == null || code <= 0) {
			code = null;
		}
    	
    	List<Record> list = CustomerPointRuleService.fingPointRuleItems(offset, count, code, note);
    	int total = CustomerPointRuleService.countPointRuleItems(null, null);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("count", count);
    	jsonObject.put("data", list);
    	jsonObject.put("totalRow", total);
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个积分规则
     * @param id
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void getOneRule() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
			return;
		}
    	Integer id = getParaToInt("id");
    	
    	CustomerPointRule model = CustomerPointRuleService.get(id);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    	
    }
    
    /**
     * 更新积分规则
     * @param token 帐户访问口令（必填）
     * @param id
     * @param code
     * @param note
     * @param original
     * @param target
     * @retrun
     */
    @Before(AdminAuthInterceptor.class)
    public void updateRule() {
    	String[] params = {"id", "code", "note", "original", "target"};
    	
    	if (!validate(params)) {
			return;
		}
    	
    	Integer id = getParaToInt("id");
    	Integer code = getParaToInt("code");
    	String note = getPara("note");
    	BigDecimal original = null;
		BigDecimal target = null;
		try {
			original = getParaToDecimal("original");
			target = getParaToDecimal("target");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			returnError(ErrorCode.Exception, "数值异常");
			return;
		}
    	
    	CustomerPointRule model = new CustomerPointRule();
    	model.setId(id);
    	model.setCode(code);
    	model.setNote(note);
    	model.setOriginal(original);
    	model.setTarget(target);
    	
    	ServiceCode codeFlag = CustomerPointRuleService.update(model);
    	if (codeFlag != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "创建失败");
		}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 添加等级规则
     * @param name
     * @param start
     * @param end
     * @param vip
     */
    @Before(AdminAuthInterceptor.class)
    public void createGrade() {
    	String[] params = {"name", "start", "end"};
    	if (!validate(params)) {
			return;
		}
    	
    	String name = getPara("name");
    	Integer start = getParaToInt("start");
    	Integer end = getParaToInt("end");
    	Integer vip = getParaToIntegerDefault("vip") == null ? 0 : getParaToIntegerDefault("vip");
    	
    	CustomerGrade model = new CustomerGrade();
    	model.setName(name);
    	model.setStart(start);
    	model.setEnd(end);
    	model.setVip(vip);
    	
    	ServiceCode code = CustomerGradeService.create(model);
    	if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "添加等级规则失败");
		}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 获取等级规则
     * @param token
     * @param offset
     * @param count
     * @param name
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void getGrade() {
    	String[] params = {"offset", "count"};
    	if (!validate(params)) {
			return;
		}
    	
    	int offset = getParaToInt("offset");      
    	int count = getParaToInt("count");
    	String name = getPara("name");
    	
    	List<Record> list = CustomerGradeService.findCustomerGradeItems(offset, count, name, null);
    	int total = CustomerGradeService.countCustomerGradeItems(null, null);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("count", count);
    	jsonObject.put("data", list);
    	jsonObject.put("totalRow", total);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取某个等级规则
     * @param id
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void getOneGrade() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
			return;
		}
    	Integer id = getParaToInt("id");
    	
    	CustomerGrade model = CustomerGradeService.get(id);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 修改等级规则
     * @param id
     * @param name
     * @param start
     * @param end
     * @param vip
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void updateGrade() {
    	String[] params = {"id", "name", "start", "end"};
    	if (!validate(params)) {
			return;
		}
    	Integer id = getParaToInt("id");
    	String name = getPara("name");
    	Integer start = getParaToInt("start");
    	Integer end = getParaToInt("end");
    	Integer vip = getParaToIntegerDefault("vip") == null ? 0 : getParaToIntegerDefault("vip");
    	
    	CustomerGrade model = CustomerGradeService.get(id);
    	
    	if (model == null) {
    		returnError(ErrorCode.Exception, "修改等级规则失败");
    		return;
    	}
    	
    	// 检测字段是否合法
    	Map<Integer, String> check = CustomerGradeService.checkGrade(model.getStart(), model.getEnd(), start, end);
    	if (check.containsKey(1)) {
    		returnError(ErrorCode.Validation, check.get(1));
    		return;
    	} else if (check.containsKey(2)) {
    		returnError(ErrorCode.Validation, check.get(2));
    		return;
    	}
    	
    	model.setName(name);
    	model.setStart(start);
    	model.setEnd(end);
    	model.setVip(vip);
    	
    	ServiceCode code = CustomerGradeService.update(model);
    	if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "修改等级规则失败");
		}
    	renderJson(jsonObject);
    }
    
    /**
     * 删除一条等级规则
     * @param id
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void deleteOneGrade() {
    	String[] params = {"id"};
    	
    	if (!validate(params)) {
			return;
		}
    	Integer id = getParaToInt("id");
    	
    	//正在被客户使用的等级规则
    	List<Record> listApply = CustomerGradeService.findByApply();
    	for (Record r : listApply) {
    		int gradeIdApply = r.get("id");
    		//如果被使用不能删除
    		if (id == gradeIdApply) {
    			returnError(ErrorCode.Exception, "该等级规则正在被使用，不能删除");
    			return;
			}
		}
			
    	ServiceCode code = CustomerGradeService.delete(id);
    	
    	if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "删除等级规则失败");
		}
    			
    	renderJson(jsonObject);
    }
    
    /**
     * 获取成长值
     * @param token
     * @param offset
     * @param count
     * @param customerId
     * @return
     */
    @Before(AdminAuthInterceptor.class)
    public void getGrowth() {
    	String[] params = {"offset", "count"};
    	
    	if (!validate(params)) {
			return;
		}
    	
    	int offset = getParaToInt("offset");      
    	int count = getParaToInt("count");
    	Integer customerId = getParaToIntegerDefault("customerId");
    	String name = getPara("userName");
    	
    	if (customerId == null || customerId <= 0) {
			customerId = null;
		}
    	
    	List<Record> list = CustomerGrowthService.findCustomerGrowthItems(offset, count, customerId, null, null, null, null, null, name);
    	int total = CustomerGrowthService.countCustomerGrowthItems(null, null, null, null, null, null, name);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("count", count);
    	jsonObject.put("data", list);
    	jsonObject.put("totalRow", total);
    	
    	renderJson(jsonObject);
    }
    
}