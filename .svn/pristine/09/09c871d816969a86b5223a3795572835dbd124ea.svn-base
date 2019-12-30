package com.eshop.controller.admin;

import java.util.List;

import com.eshop.groupcard.GroupProductService;
import com.eshop.interceptor.AdminAuthInterceptor;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Merchant;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 团购活动管理
 * @author TangYiFeng
 */
public class GroupProductController extends AdminBaseController {
	
	private GroupProductService service = new GroupProductService();
	
	@Before(AdminAuthInterceptor.class)
	public void create() {
		String[] params = {"name", "supplier_id", "suggestedRetailUnitPrice", "unitCost", "taxRate", 
				"invoiceType", "unitCostNoTax", "relate_id", "storeAmount"};
		if (!this.validate(params)) {
			return;
		}
		
		Product model = new Product();
		model.setName(this.getPara("name"));
		model.setSupplierId(this.getParaToInt("supplier_id"));
		model.setSuggestedRetailUnitPrice(this.getParaToDecimal("suggestedRetailUnitPrice"));
		model.setUnitCost(this.getParaToDecimal("unitCost"));
		model.setTaxRate(this.getParaToDecimal("taxRate"));
		model.setInvoiceType(this.getPara("invoiceType"));
		model.setRelateId(this.getParaToInt("relate_id"));
		model.setStoreAmount(this.getParaToInt("storeAmount"));
		model.setProdType(3);
		model.setShopId(1);
		model.setIsSale(0);
		model.setIsDelete(0);
		
		String mainPic = this.getPara("mainPic");
		mainPic = "[\"" + mainPic + "\"]";
		ServiceCode code = Merchant.publishProduct(model, "", "", mainPic);
		
		if (code != ServiceCode.Success) {
			this.returnError(ErrorCode.Exception, "创建失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void update() {
		String[] params = {"id", "name", "supplier_id", "suggestedRetailUnitPrice", "unitCost", "taxRate", 
				"invoiceType", "unitCostNoTax", "storeAmount"};
		if (!this.validate(params)) {
			return;
		}
		
		Product model = Merchant.getProduct(this.getParaToInt("id"));
		model.setName(this.getPara("name"));
		model.setSupplierId(this.getParaToInt("supplier_id"));
		model.setSuggestedRetailUnitPrice(this.getParaToDecimal("suggestedRetailUnitPrice"));
		model.setUnitCost(this.getParaToDecimal("unitCost"));
		model.setTaxRate(this.getParaToDecimal("taxRate"));
		model.setInvoiceType(this.getPara("invoiceType"));
		model.setStoreAmount(this.getParaToInt("storeAmount"));
		
		String mainPic = this.getPara("mainPic");
		mainPic = "[\"" + mainPic + "\"]";
		ServiceCode code = Merchant.publishProduct(model, "", "", mainPic);
		
		if (code != ServiceCode.Success) {
			this.returnError(ErrorCode.Exception, "修改失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		ServiceCode code = Merchant.deleteProduct(this.getParaToInt("id"));
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "删除失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		if (!this.validate(params)) {
			return;
		}
		
		Product model = Merchant.getProductDetail(this.getParaToInt("id"));
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	@Before(AdminAuthInterceptor.class)
	public void list() {
		String[] params = {"offset", "length", "relate_id"};
		if (!this.validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer prodType = 3;
		Integer relateId = getParaToIntegerDefault("relate_id");
		List<Record> list = service.list(offset, length, prodType, relateId);
		int total = service.count(prodType, relateId);
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("total", total);
		renderJson(jsonObject);
	}
	
}