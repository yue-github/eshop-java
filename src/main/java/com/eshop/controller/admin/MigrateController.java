package com.eshop.controller.admin;

import com.eshop.helper.MigrateHelper;
import com.eshop.model.dao.BaseDao.ServiceCode;

public class MigrateController extends AdminBaseController {
	
	public void migrateOrder() {
		ServiceCode code2 = MigrateHelper.migrateOrderProductPayable();
		
		if (code2 != ServiceCode.Success) {
			setError(ErrorCode.Exception, "向订单填充产品金额失败");
		}
		
		renderJson(jsonObject);
	}
	
	public void migrateProductPrice() {
		ServiceCode code = MigrateHelper.migrateProductPrice();
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "失败");
		}
		
		renderJson(jsonObject);
	}
	
	public void migrate() {
		ServiceCode code2 = MigrateHelper.migrateOrderProductPayable();
		
		if (code2 != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "向订单填充产品金额失败");
			return;
		}
		
		ServiceCode code = MigrateHelper.migrateProductPrice();
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "更新单品未税成本失败");
			return;
		}
		
		renderJson(jsonObject);
	}
	
}
