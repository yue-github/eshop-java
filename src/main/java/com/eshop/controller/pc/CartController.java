package com.eshop.controller.pc;

import java.util.*;

import com.eshop.helper.CacheHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.logistics.Logistics;
import com.eshop.model.Customer;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BasePromotion;
import com.eshop.service.Member;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

/**
 * 购物车控制器
 * @author TangYiFeng
 */
public class CartController extends PcBaseController {
	
	Logistics logistics;

    /**
     * 构造方法
     */
    public CartController() {
    	logistics = new Logistics();
    }

    /**
     * 我的购物车 - 下拉框
     * @param token 帐户访问口令（必填）
     * @return 成功：{error: 0,totalPrice:总价,maxSuggestedUnitDelivery:最大运费, products:[{id：产品id,name:名称,amount:数量,suggestedRetailUnitPrice:价格,shoppingCartId:购物车id},...]}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void cartProducts() {
    	String token = getPara("token");
    	Customer customer = (Customer)CacheHelper.get(token);
        int customerId = customer.getId();
    	
        List<Record> list = Member.getShoppingCartProducts(customerId, null, 1);
        
        double totalPrice = Member.calculateProductTotalPayable(list);
        double totalAmount = Member.calculateProductTotalAmount(list);
    	
    	jsonObject.put("error", 0);
    	jsonObject.put("data", list);
    	jsonObject.put("totalPrice", totalPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("maxSuggestedUnitDelivery", 0);
    	renderJson(jsonObject);
    }
    
    /**
     * 移除购物车
     *  @param token 帐户访问口令（必填）
     *  @param id 购物车ID（必填）
     *  @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void remove() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
        int id = getParaToInt("id");
        
        if(Member.clearShoppingCart(id) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "delete bankCard failed");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }

    /**
     * 修改购物车数量
     * @param token 帐户访问口令（必填）
     * @param id 购物车id（必填）
     * @param amount 数量（必填）
     * @return 成功：{error: 0,error:-1(库存不足)}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void update() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
        int id = getParaToInt("id");
        
        if(!this.validateRequiredString("amount")) {
    		return;
    	}
        int amount = getParaToInt("amount");
        
        ServiceCode code = Member.updateShoppingCartNum(id, amount);
        
        if (code == ServiceCode.Function) {
        	returnError(-1, "库存不足");
        	return;
        }
        
        if(code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "delete bankCard failed");
    		return;
    	}
    	
    	renderJson(jsonObject);
    }
    
    /**
     * 我的购物车列表
     *  @param token 帐户访问口令（必填）
     *  @return 成功：{error:0,totalPrice:总应付金额,totalAmount:总购买数量,promDiscount:促销优惠金额,totalProdPrice:商品总金额,shops:[{shop_id:店铺id,shopName:店铺名称,shopLogo:店铺logo,productList:[{productSelectPay:商品金额,remainPay:还差多少金额才可以优惠,promotionDiscount:优惠金额,isHasProm:是否有促销活动,products:[{id:购物车id,product_id:产品id,productName:产品名称,mainPic:产品主图,selectProterties:所选属性,amount:购买数量,suggestedRetailUnitPrice:产品单价,totalPrice:产品总价,isSelected:是否选中},...],promotion:{id:促销活动id,promotionTitle:促销活动标题}},...]}]}失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getMyShoppingCart() {
    	if (!this.validateRequiredString("token")) {
    		return;
    	}
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = Member.findShoppingCartItems(customerId, null, null);
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, 1);
        
        double totalProdPrice = Member.calculateProductTotalPayable(products);
        double totalAmount = Member.calculateProductTotalAmount(products);
        double promDiscount = BasePromotion.calculateDiscount(products);
        double totalPrice = totalProdPrice - promDiscount;
    	
    	jsonObject.put("totalPrice", totalPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("promDiscount", promDiscount);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	jsonObject.put("shops", list);
    	renderJson(jsonObject);
    }
    
    /**
     * 选中或取消购物车
     * @param token
     * @param id
     * @param selected 选择状态（0未选中，1选中）
     * @return 成功：{error:0, data:{totalPrice:总金额, totalAmount:总数量}}
     * 		       失败：{error:>0, errmsg:错误信息}	
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void selectShoppingCart() {
    	String[] params = {"id", "selected"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	int selected = getParaToInt("selected");
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = Member.selectedShoppingCart(id, selected);
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "选中购物车失败");
    		return;
    	}
    	
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, 1);
        
        double totalProdPrice = Member.calculateProductTotalPayable(products);
        double totalAmount = Member.calculateProductTotalAmount(products);
        double promDiscount = BasePromotion.calculateDiscount(products);
        double totalPrice = totalProdPrice - promDiscount;
    	
    	jsonObject.put("totalPrice", totalPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("promDiscount", promDiscount);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	renderJson(jsonObject);
    }
    
    /**
     * 批量选中或取消购物车
     * @param token
     * @param ids [{id:1,selected:1},{id:2,selected:0},...]
     * @param selected 选择状态（0未选中，1选中）
     * @return 成功：{error:0, data:{totalPrice:总金额, totalAmount:总数量}}
     * 		       失败：{error:>0, errmsg:错误信息}	
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void batchSelectShoppingCart() {
    	String[] params = {"ids"};
    	if (!this.validate(params)) {
    		return;
    	}
    	
    	String ids = getPara("ids");
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	ServiceCode code = Member.batchSelectedShoppingCart(ids);
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "选中购物车失败");
    		return;
    	}
    	
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, 1);
        
        double totalProdPrice = Member.calculateProductTotalPayable(products); //总产品价格
        double totalAmount = Member.calculateProductTotalAmount(products);//总数
        double promDiscount = BasePromotion.calculateDiscount(products);//折扣
        double totalPrice = totalProdPrice - promDiscount;//应付金额
    	
    	jsonObject.put("totalPrice", totalPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("promDiscount", promDiscount);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	renderJson(jsonObject);
    }
    
    /**
     * 获取待提交的订单列表
     * @param token
     * @return 成功：{error:0, data:{allPrice:总订单金额,shopList:[{deliveryPrice:店铺订单运费, totalPayable:店铺订单总金额 , shop_id:店铺id,shopName:店铺名称,shopLogo:店铺logo,totalPayable:店铺订单总金额(包含运费),deliveryPrice:店铺订单运费,couponList:[{id:优惠券id,type:优惠券类型(1折扣券,2现金quan),title:标题},...],productList:[{product_id:产品id,productName:产品名称,mainPic:产品主图,selectProterties:所选属性,amount:购买数量,suggestedRetailUnitPrice:产品价格,totalPrice:总价},...]},...]}}
     * 		       失败：{error:>0, errmsg:错误信息}	
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void getMyOrderListBeforeSubmit() {
    	int addressId = 0;
    	if ((getPara("address_id") != null && !getPara("address_id").equals(""))) {
    		addressId = getParaToInt("address_id");
    	}
    	Record other = new Record();
    	other.set("address_id", addressId);
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = Member.deliveryOrdersByShopCart(customerId, addressId);
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, 1);
    	double deliveryPrice = 0;
    	for (Record item : list) {
    		deliveryPrice += item.getDouble("deliveryPrice");
    	}
    	
    	double totalProdPrice = Member.calculateProductTotalPayable(products);
    	double totalAmount = Member.calculateProductTotalAmount(products);
    	double totalPromDiscount = BasePromotion.calculateDiscount(products);
    	double totalCouponDiscount = 0;
    	double totalPayable = totalProdPrice + deliveryPrice - totalPromDiscount - totalCouponDiscount;

    	jsonObject.put("data", list);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("totalPromDiscount", totalPromDiscount);
    	jsonObject.put("totalCouponDiscount", totalCouponDiscount);
    	jsonObject.put("deliveryPrice", deliveryPrice);
    	jsonObject.put("totalPayable", totalPayable);
    	renderJson(jsonObject);
    }
    
    /**
     * 根据收货地址获取待提交的订单列表
     * @param token
     * @param address_id
     * @return 成功：{error:0, data:{allPrice:总订单金额,shopList:[{shop_id:店铺id,shopName:店铺名称,shopLogo:店铺logo,totalPayable:店铺订单总金额(包含运费),deliveryPrice:店铺订单运费,productList:[{product_id:产品id,productName:产品名称,mainPic:产品主图,selectProterties:所选属性,amount:购买数量,suggestedRetailUnitPrice:产品价格,totalPrice:总价},...]},...]}}
     * 		       失败：{error:>0, errmsg:错误信息}	
     */
    /*@Before(CustomerPcAuthInterceptor.class)
    public void getMyOrderListByAddressId() {
    	int addressId = 0;
    	if ((getPara("address_id") != null && !getPara("address_id").equals(""))) {
    		addressId = getParaToInt("address_id");
    	}
    	Record other = new Record();
    	other.set("address_id", addressId);
    	
    	String token = getPara("token");
    	Customer customer = (Customer) CacheHelper.get(token);
    	int customerId = customer.getId();
    	
    	List<Record> list = Member.deliveryOrdersByShopCart(customerId, addressId);
    	List<Record> products = Member.getShoppingCartProducts(customerId, null, 1);
    	
    	double totalProdPrice = Member.calculateProductTotalPayable(products);
    	double totalAmount = Member.calculateProductTotalAmount(products);
    	double totalPromDiscount = BasePromotion.calculateDiscount(products);
    	double totalCouponDiscount = 0;
    	double deliveryPrice = logistics.calculateDeliveryPrice(list, other);
    	double totalPayable = totalProdPrice + deliveryPrice - totalPromDiscount - totalCouponDiscount;

    	jsonObject.put("data", list);
    	jsonObject.put("totalProdPrice", totalProdPrice);
    	jsonObject.put("totalAmount", totalAmount);
    	jsonObject.put("totalPromDiscount", totalPromDiscount);
    	jsonObject.put("totalCouponDiscount", totalCouponDiscount);
    	jsonObject.put("deliveryPrice", deliveryPrice);
    	jsonObject.put("totalPayable", totalPayable);
    	renderJson(jsonObject);
    }*/
    
}