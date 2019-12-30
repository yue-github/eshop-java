package com.eshop.controller.pc;

import java.util.Date;

import com.eshop.helper.CacheHelper;
import com.eshop.model.Category;
import com.eshop.model.Customer;
import com.eshop.model.CustomerLookRecord;
import com.eshop.model.Supplier;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.visit.CustomerLookRecordService;

/**
 * 客户购买习惯类
 */
public class CustomerLookController extends PcBaseController {
	
	public CustomerLookController() {}
	
	/**
	* 会员查看商品习惯-- customer_look_record(表名)
	* 会员查看商品习惯添加
	* @param token
    * @param supplierId 供应商Id
    * @param supplierName 供应商名称
    * @param productId 商品Id
    * @param productName 商品名称
    * @param categoryId 商品类型Id
    * @param categoryName 商品类型名称 
    * @return  成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	*/
	public void createLook() {
		String[] params = { "productId", "productName", "supplierId", "categoryId"};
		if (!validate(params)) {
			return;
		}
		Integer supplierId = getParaToInt("supplierId");
		Integer productId = getParaToInt("productId");
		String productName = getPara("productName");
		Integer categoryId = getParaToInt("categoryId");
		
		Integer customerId = 0;
		String customerName = "";
		if (getPara("token") != null && !getPara("token").equals("")) {
			String token = getPara("token");
			Customer customer = (Customer) CacheHelper.get(token);
			if (customer != null) {
				customerId = customer.getId();
				customerName = CustomerLookRecordService.getCustomerName(customer);
			}
		} else {
			return;
		}
		
		if (!CustomerLookRecordService.isExistByProductId(customerId, productId)) {
			String supplierName = null;
			String categoryName = null;
			
			Supplier supplier = Supplier.dao.findById(supplierId);
			supplierName = supplier.getName();
			
			Category category = Category.dao.findById(categoryId);
			categoryName = category.getName();
			
			CustomerLookRecord model = new CustomerLookRecord();
			model.setSupplierId(supplierId);
			model.setSupplierName(supplierName);
			model.setProductId(productId);
			model.setProductName(productName);
			model.setCategoryId(categoryId);
			model.setCategoryName(categoryName);
			model.setCustomerId(customerId);
			model.setCustomerName(customerName);
			model.setCreateAt(new Date());
			
			ServiceCode code = CustomerLookRecordService.customerLook(model);
			
			if (code != ServiceCode.Success) {
				setError(ErrorCode.Exception, "失败");
			}
		}
		
		renderJson(jsonObject);
	}
	
}
