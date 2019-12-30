package com.eshop.routes;

import com.eshop.controller.pc.*;
import com.jfinal.config.Routes;

public class PcRoutes extends Routes {

	@Override
	public void config() {
		add("/", IndexController.class);
		add("/pc/cart", CartController.class);
		add("/pc/test", TestController.class);
		add("/pc/home", HomeController.class);
		add("/pc/product", ProductController.class);
		add("/pc/products", ProductsController.class);
		add("/pc/promotion", PromotionController.class);
		add("/pc/address", AddressController.class);
		add("/pc/editAddress", EditAddressController.class);
		add("/pc/backManage", BackManageController.class);
		add("/pc/refundManage", RefundManageController.class);
		add("/pc/back", BackController.class);
		add("/pc/center", CenterController.class);
		add("/pc/order", OrderController.class);
		add("/pc/comfirmOrder", ComfirmOrderController.class);
		add("/pc/login", LoginController.class);
		add("/pc/register", RegisterController.class);
		add("/pc/myOrders", MyOrdersController.class);
		add("/pc/myPoints", CustomerPointController.class);
		add("/pc/myProducts", MyProductsController.class);
		add("/pc/shop", ShopController.class);
		add("/pc/shopProduct", ShopProductController.class);
		add("/pc/shopOrders", ShopOrdersController.class);
		add("/pc/wallet", WalletController.class);
		add("/pc/specialty", SpecialtyController.class);
		add("/pc/category", CategoryController.class);
		add("/pc/comment", CommentController.class);
		add("/pc/addProductInfo", AddProductInfoController.class);
		add("/pc/travel", TravelController.class);
		add("/pc/card", CardController.class);
		add("/pc/customerGold", CustomerGoldController.class);
		add("/pc/customerGrowth", CustomerGrowthController.class);
		add("/pc/customerGrade", CustomerGradeController.class);
		add("/pc/logisticsTemplate", LogisticsTemplateController.class);
		add("/pc/shopDecoration", ShopDecorationController.class);
		add("/pc/findPassword", FindPasswordController.class);
		add("/pc/promotionBaoyou", PromotionBaoyouController.class);
		add("/pc/promotionManjian", PromotionManjianController.class);
		add("/pc/promotionMansong", PromotionMansongController.class);
		add("/pc/promotionMiaosha", PromotionMiaoshaController.class);
		add("/pc/promotionTuangou", PromotionTuangouController.class);
		add("/pc/promotionZhekou", PromotionZhekouController.class);
		add("/pc/promotionSku", PromotionSkuController.class);
		add("/pc/promotionKanjia", PromotionKanJiaController.class);
		add("/pc/promotionCondition", PromotionConditionController.class);
		add("/pc/invoice", InvoiceController.class);
		add("/pc/coupon", CouponController.class);
		add("/pc/notice", NoticeController.class);
		add("/pc/helpLink", HelpLinkController.class);
		add("/pc/customerLook", CustomerLookController.class);
		add("/pc/logisticsDetails", LogisticsDetailsController.class);	
		add("/pc/validateCode", ValidateCodeController.class);	
		add("/pc/group_activity", GroupActivityController.class);	
		add("/pc/point", PointController.class);	
		add("/pc/pickup_address", PickUpAddressController.class);	
		add("/pc/product_brand", ProductBrandController.class);	
	}
}