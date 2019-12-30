package com.eshop.routes;

import com.eshop.controller.webapp.*;
import com.jfinal.config.Routes;

public class WebAppRoutes extends Routes {

	@Override
	public void config() {
		add("/webapp/addCard", AddCardController.class);
		add("/webapp/addresses", AddressesController.class);
		add("/webapp/backGoods", BackGoodsController.class);
		add("/webapp/backs", BacksController.class);
		add("/webapp/bankCards", BankcardsController.class);
		add("/webapp/cash", CashController.class);
		add("/webapp/category", CategoryController.class);
		add("/webapp/comment", CommentController.class);
		add("/webapp/comfirm", ConfirmController.class);
		add("/webapp/coupons", CouponsController.class);
		add("/webapp/editAddress", EditAddressController.class);
		add("/webapp/gold", GoldController.class);
		add("/webapp/home", HomeController.class);
		add("/webapp/login", LoginController.class);
		add("/webapp/modify", ModifyController.class);
		add("/webapp/orderDetails", OrderDetailsController.class);
		add("/webapp/orders", OrdersController.class);
		add("/webapp/points", PointsController.class);
		add("/webapp/productCollections", ProductCollectionsController.class);
		add("/webapp/product", ProductController.class);
		add("/webapp/promotion", PromotionController.class);
		add("/webapp/recharge", RechargeController.class);
		add("/webapp/rechargeDetails", RechargeDetailsController.class);
		add("/webapp/refund", RefundController.class);
		add("/webapp/register", RegisterController.class);
		add("/webapp/shopCollections", ShopCollectionsController.class);
		add("/webapp/shopHome", ShopHomeController.class);
		add("/webapp/shoppingCart", ShoppingCartController.class);
		add("/webapp/surrounding", SurroundingController.class);
		add("/webapp/wallet", WalletController.class);
		add("/webapp/withdraw", WithdrawController.class);
		add("/webapp/withdrawDetails", WithdrawDetailsController.class);
		add("/webapp/center", CenterController.class);
		add("/webapp/invoice", InvoiceController.class);
		add("/webapp/forgetPassword", ForgetPasswordController.class);
		add("/webapp/app", AppVersionController.class);
		add("/webapp/group_activity", GroupActivityController.class);
		add("/webapp/Dating",DateController.class);
	}
}
