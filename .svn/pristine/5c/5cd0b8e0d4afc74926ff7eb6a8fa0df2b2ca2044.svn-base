package com.eshop.controller.webapp;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerWebAppAuthInterceptor;
import com.eshop.invoice.EletronicInvoiceService;
import com.eshop.model.Customer;
import com.eshop.model.EletronicInvoice;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.aop.Before;

/**
 * 用户登录控制器
 * @author TangYiFeng
 */
public class InvoiceController extends WebappBaseController {

    /**
     * Default constructor
     */
    public InvoiceController() {
    }
    
    /**
     * 获取电子发票的邮箱和手机号
     * @param token
     * @param type
     * @return 成功：{error:0,data:{name:姓名,phone:电话,email:邮箱,provinceId:省id,cityId:市id,districtId:区id,addressDetail:详细地址}}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void getInvoicePerson() {
    	String[] params = {"type"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	int type = getParaToInt("type");
    	
    	EletronicInvoice model = EletronicInvoiceService.getEletronicInvoice(type, customerId);
    	
    	jsonObject.put("data", model);
    	renderJson(jsonObject);
    }
    
    /**
     * 保存电子发票
     * @param token
     * @param email
     * @param phone
     * @return 成功：{error:0}  失败：{error:>0,errmsg:错误信息}
     */
    @Before(CustomerWebAppAuthInterceptor.class)
    public void saveInvoiceEmail() {
    	String[] params = {"invoiceHead", "type", "companyCode", "email", "phone", "invoiceContent"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int type = getParaToInt("type");
    	int invoiceContent = getParaToInt("invoiceContent");
    	String invoiceHead = getPara("invoiceHead");
    	String companyCode = getPara("companyCode");
    	String email = getPara("email");
    	String phone = getPara("phone");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = EletronicInvoiceService.save(invoiceHead, customerId, type, companyCode, phone, email, invoiceContent);
    	
    	if (code != ServiceCode.Success) {
    		setError(ErrorCode.Exception, "保存失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
}