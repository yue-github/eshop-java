package com.eshop.routes;

import com.jfinal.config.Routes;
import com.eshop.controller.admin.*;

public class AdminRoutes extends Routes {

	@Override
	public void config() {
		add("/admin/migrate", MigrateController.class);
		add("/admin/file", FileController.class);
		add("/admin/product", ProductController.class);
		add("/admin/category", CategoryController.class);
		add("/admin/customer", CustomerController.class);
		add("/admin/coupon", CouponController.class);
		add("/admin/order", OrderController.class);
		add("/admin/login", LoginController.class);
		add("/admin/recommend", RecommendController.class);
		add("/admin/back", BackController.class);
		add("/admin/refund", RefundController.class);
		add("/admin/property", PropertyController.class);
		add("/admin/propertyValue", PropertyValueController.class);
		add("/admin/index", IndexController.class);
		add("/admin/shop", ShopController.class);
		add("/admin/advertisement", AdvertisementController.class);
		add("/admin/promotion", PromotionController.class);
		add("/admin/withdrawCash", WithdrawCashController.class);
		add("/admin/excel", ExcelController.class);
		add("/admin/supplier", SupplierController.class);
		add("/admin/wallet", WalletController.class);
		add("/admin/tax", TaxController.class);
		add("/admin/invoice", InvoiceController.class);
		add("/admin/payRate", PayRateController.class);
		add("/admin/user", UserController.class);
		add("/admin/navigation", NavigationController.class);
		add("/admin/role", RoleController.class);
		add("/admin/auditprice", AuditPriceController.class);
		add("/admin/visit", CustomerVisitController.class);
		add("/admin/lookRecord", CustomerLookRecordController.class);
		add("/admin/shouzhi", ShouZhiController.class);
		add("/admin/supplierContract", SupplierContractController.class);
		add("/admin/supplierPeriod", SupplierPeriodController.class);
		add("/admin/supplierBill", SupplierBillController.class);
		add("/admin/salesVolume", SalesVolumeController.class);
		add("/admin/notice", NoticeController.class);
		add("/admin/searchword", SearchWordController.class);
		add("/admin/helpLink", HelpLinkController.class);
		add("/admin/saleCost", SaleCostController.class);
		add("/admin/group_activity", GroupActivityController.class);
		add("/admin/group_product", GroupProductController.class);
		add("/admin/group_set_meal", GroupSetMealController.class);
		add("/admin/group_set_meal_product", GroupSetMealProductController.class);
		add("/admin/group_activity_card", GroupActivityCardController.class);
		add("/admin/point", PointController.class);
		add("/admin/point_product", PointProductController.class);
		add("/admin/trip", TripController.class);
		add("/admin/product_brand", ProductBrandController.class);
	}

}
