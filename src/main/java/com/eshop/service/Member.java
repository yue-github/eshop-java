package com.eshop.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.SysexMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eshop.content.ResourceService;
import com.eshop.event.EventEnum;
import com.eshop.event.param.CouponParam;
import com.eshop.event.param.InvoiceParam;
import com.eshop.event.param.OrderParam;
import com.eshop.event.param.ShopCartParam;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.DateHelper;
import com.eshop.helper.MD5Helper;
import com.eshop.helper.MailHelper;
import com.eshop.helper.MathHelper;
import com.eshop.helper.SMSHelper;
import com.eshop.helper.WxPayHelper;
import com.eshop.log.Log;
import com.eshop.logistics.Logistics;
import com.eshop.membership.MemberShip;
import com.eshop.model.*;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.ManSong;
import com.eshop.promotion.MiaoSha;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.collections4.Put;

public class Member extends User {
	
	/**
	 * 获取会员姓名
	 * @param id
	 * @return
	 */
	public static String getCustomerName(Integer id) {
		String sql = "select name from customer where id=?";
		Customer customer = Customer.dao.findFirst(sql, id);
		return customer.getName() == null?"":customer.getName();
	}
	
	/**
     * 会员注册
     * @param model 会员信息
     * @return code
     */
    public static ServiceCode register(Customer model) {
    	if (model.getPassword() != null) {
    		model.set("password", MD5Helper.Encode(model.getPassword()));
    	}
    	
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	if (!model.save()) {
    		return ServiceCode.Failed;
    	}
    	
		return ServiceCode.Success;
    }
    
    /**
     * 手机注册
     * @param phone 
     * @param password 
     * @param repassword 
     * @return 1密码不能为空，2密码不一致，3该手机号码已被注册，4注册失败
     */
    public static int registerWithPhone(String phone, String password, String repassword) {
    	Customer customer = getCustomerByMobilePhone(phone);
    	
    	if (customer != null) {
			return 3;
		}
    	if (password == null || repassword == null) {
			return 1;
		}
    	if (!password.equals(repassword)) {
			return 2;
		}
    	
    	Customer model = new Customer();
    	model.setMobilePhone(phone);
    	model.setPassword(MD5Helper.Encode(password));
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	
    	if (!model.save()) {
			return 4;
		} else {
			return 0;
		}
    }

    /**
     * 邮箱注册
     * @param email 
     * @param password 
     * @param repassword 
     * @return
     */
    public static ServiceCode registerWithEmail(String email, String password, String repassword) {
    	if (password == null || repassword == null || !password.equals(repassword)) {
			return ServiceCode.Failed;
		}
    	Customer model = new Customer();
    	model.setEmail(email);
    	model.setPassword(MD5Helper.Encode(password));
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	model.setDisable(2);
    	model.save();
    	return ServiceCode.Success;
    }

    /**
     * 邮箱激活账号
     * @param email 
     * @param activityCode 
     * @return
     */
    public static ServiceCode activate(String email, String activityCode) {
    	Customer customer = getCustomerByEmail(email);
    	
    	if (customer == null) {
    		return ServiceCode.Failed;
    	}
    	
    	String code = MD5Helper.Encode(customer.getEmail() + customer.getPassword());
    	if (!code.equals(activityCode)) {
    		return ServiceCode.Failed;
    	}
    	
    	customer.setDisable(0);
    	customer.update();
    	return ServiceCode.Success;
    }
    
    /**
     * 根据邮箱获取会员信息
     * @param email
     * @return customer
     */
    public static Customer getCustomerByEmail(String email) {
    	return Customer.dao.findFirst("select * from customer where email = ?", email);
}
    
    /**
     * 用openId查询客户
     * @param openId
     * @return 客户信息
     */
    public static Customer getByOpenId(String openId) {
    	return Customer.dao.findFirst("select * from customer where weiXinOpenId = ?", openId);
    }
    
    /**
     * 根据手机获取会员信息
     * @param mobilePhone
     * @return
     */
    public static Customer getCustomerByMobilePhone(String mobilePhone) {
    	return Customer.dao.findFirst("select id, gender, headImg, nickName, created_at, name from customer where mobilePhone = ?", mobilePhone);
    }
    
    public static Customer getCustomer(int id) {
    	return Customer.dao.findFirst("select * from customer where id = ?", id);
    }
    
    /**
     * 微信union登录
     * @return customer
     */
    public static Customer loginByUnionid(String unionid, String nickName, String headImg) {
    	Customer customer = Customer.dao.findFirst("select * from customer where unionid = ? and unionid is not null", unionid);
    	
    	if (customer != null) {
    		return customer;
    	}
    	
    	Customer model = new Customer();
    	model.setNickName(nickName);
    	model.setUnionid(unionid);
    	model.setHeadImg(headImg);
    	model.setCreatedAt(new Date());
    	model.setUpdatedAt(new Date());
    	model.save();
    	
    	return model;
    }
    
    /**
     * 获取第一个用户
     * @return
     */
    public static Customer getFirstCustomer() {
    	return Customer.dao.findFirst("select * from cusotmer where disable = ?", 0);
    }

    /**
     * 登陆
     * @param name 
     * @param password 
     * @return
     */
    public static Customer login(String name, String password) {
    	int customerNum = Customer.dao.find("select * from customer where mobilePhone = ? OR email = ?", name, name).size();
    	if(customerNum > 0){
    		Customer customer = Customer.dao.findFirst("select * from customer where mobilePhone = ? or email = ?", name, name);
    		String password1 = customer.getPassword();
    		if (password1 == null) {
    			return null;
    		}
    		if(!password1.equals(MD5Helper.Encode(password))){
    			return null;
    		}
    		return customer;
    	}else{
    		return null;
    	}
    }
    
    /**
     * 判断验证码是否正确
     * @param codeToken token值
     * @param code 验证码
     * @return code
     */
    public static ServiceCode hasCode(String codeToken, String code) {
    	System.out.println("codeToken="+codeToken);
    	if (codeToken == null || codeToken == "" || codeToken.isEmpty()) {
    		return ServiceCode.Failed;
    	}
    	String cacheCode = (String) CacheHelper.get(codeToken);
    	if (cacheCode == null) {
    		return ServiceCode.Failed;
    	}
    	if (!cacheCode.equals(code)) {
    		return ServiceCode.Failed;
    	}
    	return ServiceCode.Success;
    }

    /**
     * 手机找回密码
     * @param phone 
     * @param code 
     * @param codeToken 
     * @return
     */
    public static Customer findPwdWithPhoneNext(String phone, String code, String codeToken) {
        ServiceCode scode = hasCode(codeToken, code);
        if (scode == ServiceCode.Failed) {
			return null;
		}
        return getCustomerByMobilePhone(phone);
    }

    /**
     * 重置密码
     * @param id 
     * @param password 
     * @param repassword 
     * @return
     */
    public static ServiceCode setPassword(int id, String password, String repassword) {
    	if (password == null || password.equals("")) {
			return ServiceCode.Validation;
		}
    	
    	if(repassword == null || repassword.equals("")) {
			return ServiceCode.Validation;
		}
    	
    	if (!password.equals(repassword)) {
			return ServiceCode.Validation;
		}
    	
    	Db.update("update customer set password = ? where id = ?", MD5Helper.Encode(password), id);
        
    	return ServiceCode.Success;
    }

    /**
     * 邮箱找回密码
     * @param email 
     * @return
     */
    public static ServiceCode findPwdWithEmailNext(String email, String url, String subject, String content) {
    	Customer customer = getCustomerByEmail(email);
    	if (customer == null) {
			return ServiceCode.Failed;
		}
    	try {
			MailHelper.sendEmail(email, subject, content);
			return ServiceCode.Success;
		} catch (MessagingException e) {
			Log.error(e.getMessage());
			return ServiceCode.Failed;
		}
    }

    /**
     * 获取个人信息
     * @param id 
     * @return
     */
    public static Customer info(int id) {
        return Customer.dao.findById(id);
    }

    /**
     * 更改头像
     * @param id 
     * @param photo 
     * @return
     */
    public static ServiceCode updatePhoto(int id, String photo) {
        Db.update("update customer set headImg = ? where id = ?", id);
        return ServiceCode.Success;
    }

    /**
     * 绑定手机号
     * @param id 
     * @param phone 
     * @return
     */
    public static ServiceCode bindPhone(int id, String phone) {
    	Customer customer = Customer.dao.findById(id);
    	if (customer == null) {
    		return ServiceCode.Failed;
    	}
    	
    	Customer customer2 = Customer.dao.findFirst("select * from customer where mobilePhone = ?", phone);
    	if (customer2 != null) {
    		return ServiceCode.Function;
    	}
    	
		customer.setMobilePhone(phone);
    	customer.setUpdatedAt(new Date());
    	
    	if (!customer.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 发送激活账号的邮件
     * @param email
     * @param customerId
     */
    public static void sendEmailWithActiveAccount(String email, String passwordMD5) {
    	String activityCode = MD5Helper.Encode(email + passwordMD5);
    	String url = PropKit.use("callBackUrl.txt").get("apiHostName") + PropKit.use("callBackUrl.txt").get("activeEmailUrl") + "?activityCode=" + activityCode + "&email=" + email;
    	
    	String subject = "【乐驿商城】亲爱的用户，您已经注册成功，请点击邮箱里面的链接激活账户";
    	String content = "<p>您好，" + email + "</p><p style='margin-top:30px;'>请点击以下链接激活你的账户</p>" + 
				"<p><a href=" + url + ">" + url + "</a></p>" + 
			 "<p style='margin-top:50px;'>乐驿商城</p>";
    	
    	try {
			MailHelper.sendEmail(email, subject, content);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 发送供应商合同到期的邮件
     * @param eamil
     */
    public static void sendEmailWithSupplierContract(String email) {
    	String subject = "【乐驿商城】亲爱的用户，你的合同已到期，请及时办理续签";
    	String content = "<p>您好，" + email + "</p><p style='margin-top:30px;'>你的合同已到期，为了不影响你的正常工作，请尽快联系我们续签合同</p>" + 
			 "<p style='margin-top:50px;'>乐驿商城</p>";
    	try {
			MailHelper.sendEmail(email, subject, content);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }

    /**
     * 修改个人信息
     * @param model 
     * @return
     */
    public static ServiceCode updateInfo(Customer model) {
        if (!model.update()) {
			return ServiceCode.Failed;
		}
        return ServiceCode.Success;
    }

    /**
     * 加入购物车
     * @param customerId 
     * @param productId 
     * @param priceId 
     * @param amount 
     * @return
     */
    public static ServiceCode addShoppingCart(int customerId, int productId, int priceId, int amount) {
    	ProductPrice price = getSelectPrice(priceId); //选择属性
		Product product = Product.dao.findById(productId);
		ShoppingCart model = ShoppingCart.dao.findFirst("select * from shopping_cart where price_id = ? and customer_id = ? and product_id = ?", priceId, customerId, productId);

		if (product == null) {
			return ServiceCode.Failed;
		}
		
		if (model != null) {
			int new_amount = model.getAmount() + amount;
			model.set("amount", new_amount);
			model.setShopId(product.getShopId());
			if (model.update()) {
				return ServiceCode.Success;
			} else {
				return ServiceCode.Failed;
			}
		}
		
    	ShoppingCart shoppingCart = new ShoppingCart();
    	shoppingCart.set("customer_id", customerId);
    	shoppingCart.set("product_id", productId);
    	shoppingCart.set("amount", amount);
    	shoppingCart.set("shop_id", product.getShopId());
    	if(price != null) {
    		shoppingCart.set("selectProterties", price.getStr("selectProterties"));
        	shoppingCart.set("type_attr", price.getTypeAttr());
    	}
    	
    	shoppingCart.set("price_id", priceId);
    	shoppingCart.set("created_at", new Date());
    	shoppingCart.set("updated_at", new Date());
    	if (shoppingCart.save()) {
    		return ServiceCode.Success;
    	} else {
    		return ServiceCode.Failed;
    	}
    }
    
    /**
     * 获取选中销售属性
     * @param id 价格id
     * @return
     */
    public static ProductPrice getSelectPrice(int id) {
    	if(id == 0) {
    		return null;
    	}
    	ProductPrice price = ProductPrice.dao.findById(id);
    	
    	String typeAttr = price.getTypeAttr();
    	String[] attrs = typeAttr.split(",");
    	String selectProterties = "";
    	
    	for(String s : attrs) {
    		int aid = Integer.parseInt(s);
    		
    	    List<Record> ps = Db.find("select property_value.*, property.name as property_name from property_value left join property on property.id = property_value.property_id where property_value.id = ?", aid);
    	    
    	    if(ps.size() > 0) {
    	    	Record p = ps.get(0);
    	    	selectProterties += p.getStr("property_name") + "：" + p.getStr("name") + " ";
    	    }
    	}
    	
    	price.put("selectProterties", selectProterties);
    	return price;
    }
    
    /**
     * 购物车产品列表
     * @param customerId
     * @param shopId
     * @param isSelected
     * @return [{id:4,price:22,product_id:5,productName:产品名称,mainPic:产品主图,amount:3,isSelected:1,totalPrice:33},...]
     */
    public static List<Record> getShoppingCartProducts(Integer customerId, Integer shopId, Integer isSelected) {
    	String sql = findShoppingCartItemsSql(customerId, shopId, isSelected);
        List<Record> shopCarts = Db.find(sql);
        
        List<Record> resources = Db.find("select * from resource");
        
        for (Record shopCart : shopCarts) {
        	//计算每个产品的销售价和总价
        	int productId = shopCart.getInt("product_id");
        	int priceId = shopCart.getInt("price_id");
        	double price = MiaoSha.calculatePromotionPrice(productId, priceId);
        	double totalPrice = price * shopCart.getInt("amount");
        	
        	Record resource = BaseDao.findItem(shopCart.getInt("mainPic"), resources, "id");
        	String mainPic = resource.getStr("path");
        	
        	shopCart.set("price", price);
        	shopCart.set("suggestedRetailUnitPrice", price);
        	shopCart.set("totalPrice", totalPrice);
        	shopCart.set("mainPic", mainPic);
        }
        
        return shopCarts;
    }

    /**
     * 购物车列表
     * @param offset
     * @param count 
     * @param customerId 
     * @param shopId 
     * @return [{id:购物车id,shop_id:2,name:"店铺名称",products:[{id:4,price:22,product_id:5,productName:产品名称,mainPic:产品主图,amount:3,isSelected:1,totalPrice:33},...]},...]
     */
    public static List<Record> findShoppingCartItems(Integer customerId, Integer shopId, Integer isSelected) {
        List<Record> shopCarts = getShoppingCartProducts(customerId, shopId, isSelected);
        List<Integer> shopIds = new ArrayList<Integer>();
        
        for (Record shopCart : shopCarts) {
        	// 该购物车有哪些店铺
        	int spId = shopCart.getInt("shop_id");
        	if (!shopIds.contains(spId)) {
				shopIds.add(spId);
			}
        }
        
        //找出所有店铺
        String whereIn = BaseDao.getWhereIn(shopIds);
        List<Record> shops = Db.find("select id, name, logoPic from shop where id in " + whereIn);
        
        //找出每个店铺下面的产品列表
        for (Record shop : shops) {
        	List<Record> products = BaseDao.findItems(shopCarts, "shop_id", shop.getInt("id"));
        	String logoPic = ResourceService.getPathByResId(shop.getInt("logoPic"));
        	shop.set("logoPic", logoPic);
        	shop.set("products", products);
        }
        
        // 购物车促销活动事件
        ShopCartParam shopCartParam = new ShopCartParam();
        shopCartParam.setShopCarts(shops);
        eventRole.dispatch(EventEnum.EVENT_SHOP_CART, shopCartParam); // 促销活动
        shops = shopCartParam.getShopCarts();
        
        // 满送
        Record zp = ManSong.gifts(shopCarts);
        shops.add(zp);
        
        return shops; 
    }

    /**
     * 查询购物车的总数量
     * @param customerId 
     * @param shopId 
     * @return
     */
    public static int getShoppingCartItemsCount(Integer customerId, Integer shopId, Integer isSelected) {
        String sql = findShoppingCartItemsSql(customerId, shopId, isSelected);
        return Db.find(sql).size();
    }
    
    /**
     * 查询购物车的sql语句
     * @param customerId
     * @param shopId
     * @return
     */
    public static String findShoppingCartItemsSql(Integer customerId, Integer shopId, Integer isSelected) {
    	String sql = "select a.*, b.name as productName,b.isSupportReturn, b.mainPic," +
    			" a.product_id as productId, b.logistics_template_id as logisticsTemplateId," + 
    			" b.logistics_template_id as template_id," + 
    			" b.is_sale, b.isDelete" +
    			" from shopping_cart as a" +
    			" left join product as b on a.product_id = b.id" +
    			" where a.id != 0";
    	if (customerId != null) {
			sql += " and a.customer_id = " + customerId;
		}
    	if (shopId != null) {
			sql += " and a.shop_id = " + shopId;
		}
    	if (isSelected != null) {
			sql += " and a.isSelected = " + isSelected;
		}
    	return sql;
    }

    /**
     * 删除购物车
     * @param id 
     * @return
     */
    public static ServiceCode clearShoppingCart(int id) {
    	if(ShoppingCart.dao.deleteById(id)){
    		return ServiceCode.Success;
    	}
    	return ServiceCode.Failed;
    }
    
    /**
     * 批量删除购物车
     * @param id
     * @return
     */
    public static ServiceCode batchClearShoppingCart(final List<Integer> ids) {
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (Integer id : ids) {
						ShoppingCart.dao.deleteById(id);
					}
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }


    /**
     * 修改购物车数量
     * @param id 
     * @param num 
     * @return
     */
    public static ServiceCode updateShoppingCartNum(int id, int amount) {
    	ShoppingCart shoppingCart = ShoppingCart.dao.findById(id);
        
		if(shoppingCart == null) {
			return ServiceCode.Failed;
		}
		
		int storeAmount = 0;
		if (shoppingCart.get("price_id") != null && shoppingCart.getPriceId() != 0) {
			ProductPrice productPrice = ProductPrice.dao.findById(shoppingCart.getPriceId());
			storeAmount = (productPrice != null) ? productPrice.getProductNumber() : 0;
			
		} else {
			Product product = Product.dao.findById(shoppingCart.getProductId());
			storeAmount = (product != null) ? product.getStoreAmount() : 0;
		}
		
		if (storeAmount < amount) {
			return ServiceCode.Function;
		}
		
		shoppingCart.setAmount(amount);
		shoppingCart.setUpdatedAt(new Date());
        
    	if (shoppingCart.update()) {
    		return ServiceCode.Success;
    	} else {
    		return ServiceCode.Failed;
    	}
    }

    /**
     * 选中购物车
     * @param customerId 
     * @param ids 
     * @return
     */
    public static ServiceCode selectedShoppingCart(String ids, int customerId) {
    	List<Integer> idsList = JSON.parseArray(ids, Integer.class);
    	Db.update("update shopping_cart set isSelected = 0 where customer_id = ?", customerId);
    	
    	for (Integer id : idsList) {
    		Db.update("update shopping_cart set isSelected = ? where id = ?", 1, id);
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 改变购物车选中状态
     * @param id
     * @param selected
     * @return
     */
    public static ServiceCode selectedShoppingCart(final int id, final int selected) {
    	boolean success = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("update shopping_cart set isSelected = ? where id = ?", selected, id);
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});

    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }


    /**
     * 批量改变购物车选中状态
     * @param ids [{id:1,selected:1},{id:2,selected:0},...]
     * @return
     */
    public static ServiceCode batchSelectedShoppingCart(String ids) {


    	final JSONArray jsarr = JSON.parseArray(ids);
    	boolean success = SelectedShoppingCart(jsarr);
//    	boolean success = Db.tx(new IAtom() {
//
//			@Override
//			public boolean run() throws SQLException {
//				try {
//					for (int i = 0; i < jsarr.size(); i++) {
//
//						int id = jsarr.getJSONObject(i).getIntValue("id");
//						int selected = jsarr.getJSONObject(i).getIntValue("selected");
//
//						Db.update("update shopping_cart set isSelected = ? where id = ?", selected, id);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					return false;
//				}
//				return true;
//			}
//		});

    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    public static boolean SelectedShoppingCart(final JSONArray jsarr){

    	try {

    		for(int i = 0;i<jsarr.size();i++){

    			Integer id = jsarr.getJSONObject(i).getIntValue("id");
    			Integer selected = jsarr.getJSONObject(i).getIntValue("selected");
				Db.update("update shopping_cart set isSelected = ? where id = ?", selected, id);
			}
			return true;
		}catch (Exception e){
    		e.printStackTrace();
    		return false;
		}

	}

	/**
	 *
	 *修改订单状态
	 * @param tradeNo
	 * @param customerId
	 * @return
	 */
	public static ServiceCode updateOrderStatus(final Integer customerId,final String tradeNo) {

		boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				try{
					String sql = "update `order` set `status` = ? where customer_id=? and theSameOrderNum = ?";
					Db.update(sql,2,customerId,tradeNo);
				}catch (Exception e){
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}

	/**
	 *
	 * 修改提交的订单增加TradeNo
	 * @param token
	 * @param TradeNo
	 */
	public static ServiceCode updateCustomerOder(final String tradeNo,final Integer customerId,final Integer TranNOderId){

		boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				try {

					String sql = "update `order` SET tradeNo = ? WHERE customer_id = ? and id = ?";
					Db.update(sql,tradeNo,customerId,TranNOderId);
				}catch (Exception e){
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});

		return success ? ServiceCode.Success : ServiceCode.Failed;
	}

    /**
     * 生成订单统一编号
     */
    private static String generateTheSameOrderNum() {
    	Record record = Db.findFirst("select MAX(id) as id from `order`");
    	String maxId = record != null ? String.valueOf(record.getInt("id")) : "0";
    	String result = maxId;
    	int maxLen = 8;
    	int maxIdLen = maxId.length();
    	int remainLen = maxLen - maxIdLen;
    	
    	if (remainLen > 0) {
    		for (int i = 0; i < remainLen; i++) {
    			result = "0" + result;
    		}
    	}
    	
    	String date = DateHelper.formatDate(new Date(), "yyyyMMdd");
    	int random = MathHelper.getRandom(0, 10);
    	
    	return "1" + date + result + random;
    }
    
    /**
     * 生成订单号
     */
    public static String generateOrderCode() {
    	Record record = Db.findFirst("select MAX(id) as id from `order`");
    	String maxId = record != null ? String.valueOf(record.getInt("id")) : "0";
    	String result = maxId;
    	int maxLen = 9;
    	int maxIdLen = maxId.length();
    	int remainLen = maxLen - maxIdLen;
    	
    	if (remainLen > 0) {
    		for (int i = 0; i < remainLen; i++) {
    			result = "0" + result;
    		}
    	}
    	
    	String date = DateHelper.formatDate(new Date(), "yyyyMMdd");
    	int random = MathHelper.getRandom(0, 10);
    	
    	return "2" + date + result + random;
    }
    
    /**
     * 对产品列表按店铺分组，再按供应商分组
     * @param prods [{shop_id:1,product_id:1,price_id:2,amount:3,totalPrice:产品金额小计,productName:产品名称,mainPic:产品主图,selectProterties:"票型：成人票 房型：双人房"},...]
     * @return [{"shop_id":1, "shopName":店铺名称, "supplierName":供应商名称, "supplier_id":3, "products":[{shop_id:1,product_id:1,productName:产品名称,mainPic:产品主图,price_id:2,amount:3,selectProterties:"票型：成人票 房型：双人房"},...]},...]
     */
    public static List<Record> splitOrder(List<Record> prods, int addressId) {
    	// 获取供应商id
    	List<Record> products = Db.find("select * from product");
    	for (Record item : prods) {
    		if(item.getInt("product_id") != null){
    			Record product = BaseDao.findItem(item.getInt("product_id"), products, "id");
    			item.set("supplier_id", product.getInt("supplier_id"));
    			item.set("shop_id", product.getInt("shop_id"));
    		}
    	}
    	
    	Address address = Address.dao.findById(addressId);
    	int provinceId = (address != null) ? address.getProvinceId() : 0;
    	int cityId = (address != null) ? address.getCityId() : 0;
    	
    	// 根据shopId和supplierId进行分组
    	List<Record> list = new ArrayList<Record>();
    	for (Record item : prods) {
    		Record record = new Record();
    		
    		if(item.getInt("shop_id") != null && item.getInt("supplier_id") != null){
    			int shopId = item.getInt("shop_id");
    			int supplierId = item.getInt("supplier_id");
    			if (list.size() == 0) {
    				record.set("shop_id", shopId);
    				record.set("supplier_id", supplierId);
    				list.add(record);
    				continue;
    			}
    			boolean isContain = false;
    			
    			for (Record one : list) {
    				if (one.getInt("shop_id") == shopId && one.getInt("supplier_id") == supplierId) {
    					isContain = true;
    				}
    			}
    			
    			if (!isContain) {
    				record.set("shop_id", shopId);
    				record.set("supplier_id", supplierId);
    				list.add(record);
    			}
    		}
    	}
    	
    	List<Record> shops = Db.find("select * from shop");
    	List<Record> suppliers = Db.find("select * from supplier");
    	Logistics logistics = new Logistics();
    	
    	// 把对应的产品放到对应的组里面
    	for (Record item : list) {
    		int shopId = item.getInt("shop_id");
    		int supplierId = item.getInt("supplier_id");
    		List<Record> lst = new ArrayList<Record>();
    		
    		for (Record prod : prods) {
    			if (shopId == prod.getInt("shop_id") && supplierId == prod.getInt("supplier_id")) {
					lst.add(prod);
				}
    		}
    		item.set("products", lst);
    		
    		// 计算运费
    		double deliveryPrice = logistics.getOrderFreight(lst, provinceId, cityId);
    		item.set("deliveryPrice", deliveryPrice);
    		
    		Record shop = BaseDao.findItem(shopId, shops, "id");
    		String shopName = shop.getStr("name");
    		Record supplier = BaseDao.findItem(supplierId, suppliers, "id");
    		String supplierName = (supplier != null) ? supplier.getStr("name") : "";
    		
    		item.set("shopName", shopName);
    		item.set("supplierName", supplierName);
    	}
    	
    	return list;
    }
    
    /**
     * 根据购物车生成配送单
     * @param customerId
     * @return
     */
    public static List<Record> deliveryOrdersByShopCart(int customerId, int addressId) {
    	List<Record> products = getShoppingCartProducts(customerId, null, 1);
    	List<Record> deliveryOrders = splitOrder(products, addressId);
    	return deliveryOrders;
    }
    
    /**
     * 根据直接购买信息生成配送单
     * @param productId
     * @param priceId
     * @param amount
     * @return 
     */
    public static List<Record> deliveryOrdersByDirect(int productId, int priceId, int amount, 
    		String startAt, String endAt, int addressId) {
    	
    	List<Record> products = getProductsByDirect(productId, priceId, amount, startAt, endAt);
    	return splitOrder(products, addressId);
    }
    
    /**
     * 直接购买产品列表
     * @param productId
     * @param priceId
     * @param amount
     * @return [{shop_id:1,product_id:1,productName:产品名称,mainPic:产品主图,price_id:2,amount:3,selectProterties:"票型：成人票 房型：双人房"},...]
     */
    public static List<Record> getProductsByDirect(int productId, int priceId, int amount, String startAt, String endAt) {
    	Record product = Db.findFirst("select id, prod_type, name as productName, mainPic, suggestedRetailUnitPrice, shop_id, logistics_template_id as template_id from product where id = ?", productId);
    	String mainPic = ResourceService.getPathByResId(product.getInt("mainPic"));
    	String selectProterties = BaseDao.getselectProterties(priceId);
    	double price = MiaoSha.calculatePromotionPrice(productId, priceId);
    	
    	int prodType = product.getInt("prod_type");
    	if (prodType == 1) {
    		selectProterties = "入住时间：从" + startAt + "至" + endAt;
    		Date start = DateHelper.strToDate(startAt);
    		Date end = DateHelper.strToDate(endAt);
    		Map<String, Double> map = Merchant.getHotelPrice(start, end, productId);
    		price = map.get("price");
    		product.set("suggestedRetailUnitPrice", price);
    		product.set("unitCost", map.get("unitCost"));
    	}
    	
    	double totalPrice = amount * price;
    	
    	product.set("price", price);
    	product.set("totalPrice", totalPrice);
    	product.set("mainPic", mainPic);
    	product.set("product_id", productId);
    	product.set("productId", productId);
    	product.set("price_id", priceId);
    	product.set("amount", amount);
    	product.set("selectProterties", selectProterties);
    	
    	List<Record> products = new ArrayList<Record>();
    	products.add(product);
    	
    	return products;
    }
    
    /**
     * 购物车提交订单并删除所选项
     * @param customerId
     * @return
     */
    public String submitOrderWithShoppingCart(int customerId, Record other) {
    	List<Record> list = findShoppingCartItems(customerId, null, 1);
    	List<Record> products = new ArrayList<Record>();
    	
    	for (Record item : list) {
    		List<Record> pds = item.get("products");
    		for (Record p : pds) {
    			products.add(p);
    		}
    	}
    	
    	String theSameOrderNum = submitOrder(customerId, products, other);
    	
    	if (theSameOrderNum != null) {
    		Db.update("delete from shopping_cart where customer_id = ? and isSelected = ?", customerId, 1);
    	}
    	
    	return theSameOrderNum;
    }
    
    /**
     * 直接购买提交订单
     * @param customerId
     * @param productId
     * @param priceId
     * @param amount
     * @param other
     * @return
     */
    public String submitOrderWithProduct(int customerId, int productId, int priceId, int amount, Record other, String startAt, String endAt) {
    	List<Record> products = getProductsByDirect(productId, priceId, amount, startAt, endAt);
    	String theSameOrderNum = submitOrder(customerId, products, other);
    	return theSameOrderNum;
    }
    
    public boolean checkPointAmount(int customerId, int pointProductId) {
    	PointProduct pointProduct = PointProduct.dao.findById(pointProductId);
    	Customer customer = Customer.dao.findById(customerId);
    	return customer.getPoints() >= pointProduct.getNeedPoint();
    }
    
    public Record submitOrderWithPoint(int pointProductId, final int customerId, int productId, int priceId, int amount, Record other, String startAt, String endAt) {
    	final PointProduct pointProduct = PointProduct.dao.findById(pointProductId);
    	final Customer customer = Customer.dao.findById(customerId);
    	Record result = new Record();
    	
    	if (customer.getPoints() < pointProduct.getNeedPoint()) {
    		result.set("error", -1);
    		return result;
    	}
    	
    	List<Record> products = getProductsByDirect(productId, priceId, amount, startAt, endAt);
    	final String theSameOrderNum = submitOrder(customerId, products, other);
    	
    	if (theSameOrderNum == null) {
    		result.set("error", 1);
    		return result;
    	}
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {

					Db.update("update `order` set status = ?, payTime = ?, points = ? where theSameOrderNum = ?", 2, new Date(), pointProduct.getNeedPoint(), theSameOrderNum);
					Db.update("update `customer` set points = points - ? where id = ?", pointProduct.getNeedPoint(), customer.getId());
					CustomerPoint customer = new CustomerPoint();
					Integer needPoint = pointProduct.getNeedPoint()-(pointProduct.getNeedPoint()+pointProduct.getNeedPoint());
					customer.setCustomerId(customerId);
					customer.setAmount(needPoint);
					customer.setType(2);
					customer.setNote("通过积分兑换商品扣除积分");
					customer.setCreatedAt(new Date());
					customer.setUpdatedAt(new Date());
					customer.setSource(42);
					customer.setRelateId(0);
					customer.setCycleNum(0);
					customer.save();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
    			
    	if (!success) {
    		result.set("error", 1);
    		return result;
    	}
    	
		result.set("error", 0);
		result.set("theSameOrderNum", theSameOrderNum);
    	return result;
    }


	/**
     * 计算运费
     * @param prods
     * @param receiveProvinceId
     * @param receiveCityId
     * @return
     */
    public double calcalateFreight(List<Record> prods, int receiveProvinceId, int receiveCityId) {
    	for (Record item : prods) {
    		Product product = Product.dao.findById(item.getInt("product_id"));
    		item.set("template_id", product.getLogisticsTemplateId());
    	}
    	Logistics logistics = new Logistics();
    	return logistics.getOrderFreight(prods, receiveProvinceId, receiveCityId);
    }
    
    /**
     * 计算未税成本
     * @param taxRate
     * @param unitCost
     * @param invoiceType
     * @return
     */
    private static BigDecimal calculateUnitCostNoTax(BigDecimal taxRate, BigDecimal unitCost, String invoiceType) {
    	BigDecimal rate = new BigDecimal(1);
		
		if (invoiceType.equals("value_add")) {
			rate = rate.add(taxRate.multiply(new BigDecimal(0.01)));
		}
		
		BigDecimal unitCostNoTax = unitCost.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
		
		return unitCostNoTax;
    }
    
    private Record getActualAddress(int addressType, int id, String contacts, String phone) {
    	Record result = new Record();
    	if (addressType == 1) {
    		Address address = Address.dao.findById(id);
    		result.set("province", address.getProvince());
    		result.set("city", address.getCity());
    		result.set("district", address.getDistrict());
    		result.set("detail_address", address.getDetailedAddress());
    		result.set("preferredContactPhone", address.getPhone());
    		result.set("receiveName", address.getContacts());
    	} else {
    		PickupAddress pkAddress = PickupAddress.dao.findById(id);
    		result.set("province", pkAddress.getProvince());
    		result.set("city", pkAddress.getCity());
    		result.set("district", pkAddress.getDistrict());
    		result.set("detail_address", pkAddress.getDetailAddress());
    		result.set("preferredContactPhone", phone);
    		result.set("receiveName", contacts);
    	}
    	return result;
    }
 
    /**
     * 提交订单 - 直接购物
     * @param customerId
     * @param prods 产品列表  [{shop_id:1,product_id:1,price_id:2,totalPrice:产品金额小计,amount:3,selectProterties:"票型：成人票 房型：双人房"},...]
     * @param other {address_id:1,source:1,payType:1,couponId:1,invoice:{type:发票类型(1普通发票,2电子发票,3增值税发票),invoiceHead:发票抬头,invoiceContent:发票内容}}
     * @return2
     */
    public String submitOrder(final int customerId, final List<Record> prods, final Record other) {
    	if (prods == null || prods.size() <= 0) {
			return null;
		}
    	
    	long start = new Date().getTime();
    	final String theSameOrderNum = generateTheSameOrderNum();
    	
    	boolean success = Db.tx(new IAtom() {

            @Override
			public boolean run() throws SQLException {
                try {
                	int addressId = other.getInt("address_id");
                	List<Record> list = splitOrder(prods, addressId);
                	int addressType = other.get("address_type") != null ? other.getInt("address_type") : 1;
                	Record address = getActualAddress(addressType, addressId, other.getStr("contacts"), other.getStr("phone"));
                	int gold = 0;

                	if(other.get("gold") != null) {
                		gold = other.getInt("gold");
                	}
                	Shop shop = null;
                	for (Record group : list) {
                		int shopId = group.getInt("shop_id");
                		int supplierId = group.getInt("supplier_id");
                		double orderTotalProductCost = 0;
                		double orderTotalActualProductPrice = 0;
                		double totalPayable = 0;
						double totalPrice = 0;
						shop = Shop.dao.findById(shopId);
                		String shopName = (shop != null) ? shop.getName() : "";
                		Supplier supplier = Supplier.dao.findById(supplierId);
                		String supplierName = (supplier != null) ? supplier.getName() : "";
                		Order orderModel = new Order();
                		orderModel.setCustomerId(customerId);
                		orderModel.setAddressId(addressId);
                		orderModel.setSource(other.getInt("source"));
                		orderModel.setStatus(BaseDao.UNPAY);
                		orderModel.setOrderCode(generateOrderCode());
                		orderModel.setPayType(other.getInt("payType"));
                		orderModel.setTheSameOrderNum(theSameOrderNum);
                		orderModel.setCodeType(1);
                		orderModel.setShopId(shopId);
                		orderModel.setShopName(shopName);
                		orderModel.setSupplierId(supplierId);
                		orderModel.setSupplierName(supplierName);
                		orderModel.setReceiveProvince(address.getStr("province"));
                		orderModel.setReceiveCity(address.getStr("city"));
                		orderModel.setReceiveDistrict(address.getStr("district"));
                		orderModel.setReceiveDetailAddress(address.getStr("detail_address"));
                		orderModel.setReceiveName(address.getStr("receiveName"));
                		orderModel.setPreferredContactPhone(address.getStr("preferredContactPhone"));
                		orderModel.setCreatedAt(new Date());
                		orderModel.setUpdatedAt(new Date());
                		orderModel.setOrderTime(new Date());
                		orderModel.setOrderType(other.getInt("order_type"));
                		orderModel.setNote(other.getStr("note"));
                		orderModel.setGold(gold);
                		orderModel.set("totalPayable", totalPayable);
                		orderModel.set("totalProductCost", orderTotalProductCost);
                		orderModel.set("totalActualProductPrice", orderTotalActualProductPrice);
                		orderModel.set("deliveryPrice", 0);
                		orderModel.save();
                		List<Record> lst = group.get("products");
                		Integer chars  = 0;
                		for (Record lstItem : lst) {
		                	int productId = lstItem.getInt("product_id");
	                		Product product = Product.dao.findById(productId);
	                		Category category = Category.dao.findById(product.getCategoryId());
	                		ProductOrder productOrder = new ProductOrder();
	                		Record sku = BaseDao.getSku(lstItem);
	                		int amount = lstItem.getInt("amount");
	                		double price = sku.getDouble("price");
	                		double originPrice = sku.getDouble("originPrice");
	                		double cost = sku.getDouble("cost");
	                		double totalProductCost = cost * amount;
	                		double totalDeliveryCost = 0;
	                		double totalCost = totalProductCost + totalDeliveryCost;
	                		double totalActualProductPrice = price * amount;
	                		double totalActualDeliveryCharge = 0;
	                		totalPrice = totalActualProductPrice + totalActualDeliveryCharge;
	                		String categoryName = (category != null) ? category.getName() : "";
	                		String invoiceType = product.getInvoiceType();
	                		BigDecimal taxRate = product.getTaxRate();
	                		BigDecimal unitCostNoTax = calculateUnitCostNoTax(taxRate, new BigDecimal(cost), invoiceType);
	            			BigDecimal taxRateSum = taxRate.multiply(new BigDecimal(price));
	                		productOrder.set("order_id", orderModel.getId());
	            		    productOrder.set("product_id", lstItem.get("product_id"));
	            		    productOrder.set("selectProterties", lstItem.getStr("selectProterties"));
	            		    productOrder.set("unitOrdered", amount);
	            			productOrder.set("unitCost", cost);
	            			productOrder.set("suggestedRetailUnitPrice", originPrice);
	            			productOrder.set("actualUnitPrice", price);
	            			productOrder.set("totalProductCost", totalProductCost);
	            			productOrder.set("totalDeliveryCost", totalDeliveryCost);
	            			productOrder.set("totalCost", totalCost);
	           				productOrder.set("totalActualProductPrice", totalActualProductPrice);
	           				productOrder.set("totalActualDeliveryCharge", totalActualProductPrice);
	           				productOrder.set("totalPrice", totalPrice);
	           				productOrder.set("product_name", product.get("name"));
	           				productOrder.set("supplier_name", supplierName);
	           				productOrder.set("supplier_id", product.getSupplierId());
	           				productOrder.set("upc", product.get("upc"));
	           				productOrder.set("category_name", categoryName);
	           				productOrder.set("product_summary", product.getSummary());
	           				productOrder.set("created_at", new Date());
	           				productOrder.set("updated_at", new Date());
	           				productOrder.set("promotion_id", 0);
            				productOrder.set("priceId", lstItem.get("price_id"));
            				productOrder.set("order_type", other.getInt("order_type"));
            				productOrder.set("taxRate", taxRate);
            				productOrder.set("taxRateSum", taxRateSum);
            				productOrder.set("invoiceType", invoiceType);
            				productOrder.set("unitCostNoTax", unitCostNoTax);
            				productOrder.set("commission", product.getCommission());
	            			productOrder.save();

//	            			CacheHelper.put("id",productOrder);
	            			String toString = CacheHelper.get("totalPayable").toString();
	            			totalPayable = Double.valueOf(toString);
	            			orderTotalActualProductPrice += totalActualProductPrice;
	            			orderTotalProductCost += totalProductCost;
	           				//判断商品是否临近界限
           					if(projectIsLimit(Integer.parseInt(lstItem.get("price_id").toString()), productId, amount)) {
	            				//获取提交购物车中商品的店铺信息
	                       		shop = Shop.dao.findById(shopId);
	                       		//通知商家单品剩余数量到达界限
	                       		String message = "【乐驿商城】"+ "”"+ product.getName()+ "“库存不足，请及时补充";
	                       		String subject = "【乐驿商城】库存临界提醒";
	                       		Member.informShop(shop, message, subject, message);
            				}
							productOrder.setTotalActualProductPrice(BigDecimal.valueOf(totalPayable));
							productOrder.set("totalActualDeliveryCharge", BigDecimal.valueOf(totalPayable));
							productOrder.update();
							chars = productOrder.getId();
						}
                		//通币抵扣
						if(gold<=0){
							totalPayable = totalPayable - ((gold * 1.0) / 10);
						}
						orderModel.set("totalActualProductPrice", BigDecimal.valueOf(totalPayable));
                		orderModel.set("totalProductCost", BigDecimal.valueOf(orderTotalProductCost));
                		orderModel.set("totalPayable", BigDecimal.valueOf(totalPayable));
                		orderModel.update();

                		if(gold<=0&&other.getInt("couponId")== 0){
                			if (addressType == 1) {
                			// 计算运费事件
//                    		OrderParam orderParam = new OrderParam();
//                    		orderParam.setOrder(orderModel);
//                    		orderParam.setProducts(prods);
//                    		eventRole.dispatch(EventEnum.EVENT_CALCULATE_DELIVERY, orderParam);
                			}
						}


                		// 提交订单事件(税率，支付手续费)
                		OrderParam orderParam = new OrderParam();
                		orderParam.setOrder(orderModel);
                		orderParam.setProducts(prods);
                		eventRole.dispatch(EventEnum.EVENT_SUBMIT_ORDER, orderParam);

                		//提交订单事件(发票)
                		if (other.get("invoice") != null) {
                        	InvoiceParam invoiceParam = new InvoiceParam();
                        	invoiceParam.setInvoice((Record) other.get("invoice"));
                        	invoiceParam.setOrderId(orderModel.getId());
                        	eventRole.dispatch(EventEnum.EVENT_SUBMIT_ORDER, invoiceParam);
                    	}
						if(other.getInt("couponId")!= 0){
							CacheHelper.put("couponId",other.getInt("couponId"));

						}
						double total=totalPrice/100*10;
                		Integer amount = (int)total;
						JSONObject jsonObject = new JSONObject();
						JSONArray jsonArray = new JSONArray();
						jsonObject.put("CustomerId",customerId);
						jsonObject.put("Amount",amount);
						jsonObject.put("Type",1);
						if(orderModel.getOrderType() == 2) {
							//购买旅游类产品获取通币
							//eventRole.dispatch(EventEnum.EVENT_BUY_TRAVEL, orderParam);
							jsonObject.put("note","购买旅游类商品第三方支付获取成长值");
						} else {
							//购买特产类商品获取通币
							//eventRole.dispatch(EventEnum.EVENT_BUY_SEPECILATY, orderParam);
							jsonObject.put("note","购买特产类商品第三方支付获取成长值");
						}
						jsonObject.put("RelateId",orderModel.getId());
						jsonObject.put("CreatedAt",new Date());
						jsonObject.put("UpdatedAt",new Date());
						jsonObject.put("Source",19);
						CacheHelper.put("TranNOderId",orderModel.getId());
						CacheHelper.put("customerId",customerId);
						CacheHelper.put("json",jsonObject);
						CacheHelper.put("gold",gold);

                	}

                	// 提交订单(促销活动)
                	/*PromotionParam promotionParam = new PromotionParam();
                	promotionParam.setTheSameOrderNum(theSameOrderNum);
                	promotionParam.setProducts(prods);
                	dispatch(EventEnum.EVENT_SUBMIT_ORDER, promotionParam);*/

                	// 提交订单事件(优惠券)
//                	CouponParam couponParam = new CouponParam();
//                	int couponId = (other.get("couponId") == null) ? 0 : other.getInt("couponId");
//                	couponParam.setTheSameOrderNum(theSameOrderNum);
//                	couponParam.setProducts(prods);
//                	couponParam.setCouponId(couponId);
//                	dispatch(EventEnum.EVENT_SUBMIT_ORDER, couponParam);
					CacheHelper.remove("toString");
					CacheHelper.remove("id");
                	return true;
                } catch (Exception e) {
                	e.printStackTrace();
                	Log.error(e.getMessage() + ",生成订单失败");
                    return false;
                }
            }
        });

    	long end = new Date().getTime();
    	Log.info("提交订单时间="+(end - start));
    	
//    	this.createdBigOrder(theSameOrderCode, address);
    	
		return success ? theSameOrderNum : null;
    }
    
    public void createdBigOrder(String theSameOrderCode, Record address) {
    	// 根据theSameOrderCode找出所有的送货单
    	// 创建总订单
    	// 更新送货单的big_order_id字段
    }
 
    
	/**
     * 改变订单状态
     * @param orderId
     * @param targetStatus
     * @return
     */
    public static ServiceCode changeOrderState(int orderId, int targetStatus) {
    	Order order = getOrder(orderId);
    	if (order == null) {
			return ServiceCode.Failed;
		}
    	int curStatus = order.getStatus();
    	switch (targetStatus) {
    	case BaseDao.PAYED:
    		if (curStatus != BaseDao.UNPAY) {
				return ServiceCode.Failed;
			}
    		order.setPayTime(new Date());
    		break;
    	case BaseDao.RECEIVED:
    		if (curStatus != BaseDao.PAYED && curStatus != BaseDao.DISPATCHED) {
				return ServiceCode.Failed;
			}
    		order.setReceiveTime(new Date());
    		break;
    	case BaseDao.CANCELED:
    		if (curStatus != BaseDao.UNPAY) {
				return ServiceCode.Failed;
			}
    		order.setCancelTime(new Date());
    		break;
		}
		order.setStatus(targetStatus);
		order.update();
		return ServiceCode.Success;
    }

    /**
     * 付款
     * @param orderId
     */
    public static ServiceCode pay(int orderId) {
    	return changeOrderState(orderId, BaseDao.PAYED);
    }

    /**
     * 确认收货
     * @param orderId
     */
    public static ServiceCode confirm(int orderId) {
    	return changeOrderState(orderId, BaseDao.RECEIVED);
    }

    /**
     * 评论商品
     * @param productId 
     * @param productOrder 
     * @param product 
     * @param model 
     * @return
     */
    public static ServiceCode submitComment(final ProductReview model, final Integer productOrderId,  final List<String> images) {
    	if (model == null) {
			return ServiceCode.Failed;
		}
    	
    	final Product product = Product.dao.findById(model.getProductId());
    	final ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    	if(product.getCommentNum() == null) {
    		product.setCommentNum(1);
    	}else {
    		product.setCommentNum(product.getCommentNum()+1);
    	}
    	
    	if (model.getCreatedAt() == null)
    		model.setCreatedAt(new Date());
    	if (model.getUpdatedAt() == null)
    		model.setUpdatedAt(new Date());
    	productOrder.setStatus(7);
    	
    	boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					model.save();
					productOrder.update();
					product.update();
					
					if(images!=null && !"".equals(images)){
						for (String item : images) {
							Resource resource = new Resource();
							resource.setCategory(ResourceService.COMMENT);
							resource.setType(ResourceService.PICTURE);
							resource.setPath(item);
							resource.setRelateId(model.getId());
							resource.setCreatedAt(new Date());
							resource.setUpdatedAt(new Date());
							resource.save();
						}
					}
					
					// 判断订单是否处于完成状态
					boolean flag = true;
					List<ProductOrder> list = ProductOrder.dao.find("select * from product_order where order_id = ?", productOrder.getOrderId());
					for (ProductOrder item : list) {
						if (item.getStatus() != 7) {
							flag = false;
							break;
						}
					}
					if (flag) {
						Order order = Order.dao.findById(productOrder.getOrderId());
						if (order != null) {
							order.setStatus(7);
							order.setCompleteTime(new Date());
							order.update();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					Log.error(e.getMessage() + ",发布评论失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 取消订单
     * @param orderId
     * @param cancelReason
     * @param cancelNote
     * @return
     */
    public static ServiceCode cancel(int orderId, String cancelReason, String cancelNote) {
        Order order = Order.dao.findById(orderId);
        if (order == null) {
			return ServiceCode.Failed;
		}
        if (order.getStatus() != BaseDao.UNPAY) {
			return ServiceCode.Failed;
		}
        
        order.setCancelReason(cancelReason);
        order.setCancelNote(cancelNote);
        order.setCancelTime(new Date());
        order.setStatus(BaseDao.CANCELED);
        order.update();
        
        return ServiceCode.Success;
    }
    
    /**
     * 批量取消订单
     * @param ids
     * @param cancelReason
     * @param cancelNote
     * @return
     */
    public static ServiceCode batchCancel(final List<String> ids, final String cancelReason, final String cancelNote) {
        boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						ServiceCode code = cancel(id, cancelReason, cancelNote);
						if (code != ServiceCode.Success) {
							return false;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 修改订单支付方式
     * @param orderId
     * @param targetPayType
     * @return
     */
    public static ServiceCode updatePayType(int orderId, int targetPayType) {
    	Order order = Order.dao.findById(orderId);
    	
    	if (order == null) {
    		return ServiceCode.Failed;
    	}
    	
    	order.setPayType(targetPayType);
    	order.update();
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 删除订单，只允许删除已取消的订单
     * @param id
     * @return
     */
    public static ServiceCode deleteOrder(final int id) {
    	Order order = Order.dao.findById(id);
        if (order == null) {
			return ServiceCode.Failed;
		}
        if (order.getStatus() != BaseDao.CANCELED) {
			return ServiceCode.Failed;
		}
        
        boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from `order` where id = ?", id);
					Db.update("delete from product_order where order_id = ?", id);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 批量删除订单
     * @param ids
     * @return
     */
    public static ServiceCode batchDeleteOrder(final List<String> ids) {
        boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						ServiceCode code = deleteOrder(id);
						if (ServiceCode.Success != code) {
							return false;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
        
        return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 批量查询商品评论
     * @param offset 
     * @param count 
     * @param customerId 
     * @param shopId 
     * @param rate 
     * @param startTime 
     * @param endTime 
     * @return
     */
    public static List<Record> findCommentItems(int offset, int count, Integer productId, Integer customerId, 
    		Integer shopId, Integer ratings, String startTime, String endTime, String comments, String name) {
        
    	String sql = findCommentItemsSql(productId, customerId, shopId, ratings, startTime, endTime, 
        		comments, name);
        sql = BaseDao.appendLimitSql(sql, offset, count);
        
        List<Record> list = Db.find(sql);
        //List<Record> resources = Db.find("select * from resource");
        List<Record> customers = Db.find("select * from customer");
        
        for (Record item : list) {
        	List<Record> res = Db.find("select * from resource where category = ? and relate_id = ?", 3, item.getInt("id"));
        	List<String> pics = new ArrayList<String>();
        	for (Record item2 : res) {
        		pics.add(item2.getStr("path"));
        	}
        	item.set("pics", pics);
        	
        	Record customer = BaseDao.findItem(item.getInt("customer_id"), customers, "id");
    		String nickName = "";
    		String headImg = "";
    		if (customer != null) {
    			headImg = customer.getStr("headImg");
    			nickName = customer.getStr("nickName");
    			if (nickName == null || nickName.equals("")) {
    				nickName = customer.getStr("name");
    				if (nickName == null || nickName.equals("")) {
    					nickName = customer.getStr("mobilePhone");
    					if (nickName == null || nickName.equals("")) {
        					nickName = customer.getStr("email");
        				}
    				}
    			}
    		}
    		item.set("nickName", nickName);
    		item.set("headImg", headImg);
        }
        
        return list;
     }

    /**
     * 批量查询评论的总数量
     * @param offset 
     * @param count 
     * @param customerId 
     * @param shopId 
     * @param rate 
     * @param startTime 
     * @param endTime 
     * @return
     */
    public static int countCommentItems(Integer productId, Integer customerId, Integer shopId, Integer ratings, String startTime, String endTime, String comments, String name) {
        String sql = findCommentItemsSql(productId, customerId, shopId, ratings, startTime, endTime, comments, name);
        return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param productId
     * @param customerId
     * @param shopId
     * @param ratings
     * @param startTime
     * @param endTime
     * @param comments
     * @param name
     * @return
     */
    public static String findCommentItemsSql(Integer productId, Integer customerId, Integer shopId, 
    		Integer ratings, String startTime, String endTime, String comments, String name) {
    	String sql = "select a.*, b.name as productName, c.nickName, c.name, c.mobilePhone, c.email, c.headImg" + 
    				" from product_review as a" +
    				" left join product as b on a.product_id = b.id" +
    				" left join customer as c on a.customer_id = c.id" +
    				" where a.id != 0";
    	
    	if (productId != null) {
			sql += " and a.product_id = " + productId;
		}
    	if (customerId != null) {
			sql += " and a.customer_id = " + customerId;
		}
    	if (shopId != null) {
			sql += " and b.shop_id = " + shopId;
		}
    	if (ratings != null) {
			sql += " and a.ratings = " + ratings;
		}
    	if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startTime + "'";
		}
    	if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endTime + "'";
		}
    	if (comments != null && !comments.equals("")) {
			sql += " and a.comments like '%" + comments + "%'";
		}
    	if (name != null && !name.equals("")) {
			sql += " and a.name like '%" + name + "%'";
		}
    	
    	sql += " order by a.created_at desc";
    	
    	return sql;
    }

    /**
     * 查看评论
     * @param id 
     * @return
     */
    public ProductReview getComment(int id) {
        return ProductReview.dao.findById(id);
    }

    /**
     * 删除评论
     * @param id 
     * @return
     */
    public static ServiceCode deleteComment(int id) {
        if (ProductReview.dao.deleteById(id)) {
			return ServiceCode.Failed;
		}
        return ServiceCode.Success;
    }
    
    /**
     * 查看产品评论等级
     * @param id 产品id
     * @return
     */
    public static Map<String, Integer> getProductRatings(int id) {
    	Map<String, Integer> ratings = new HashMap<String, Integer>();
    	int rn1 = ProductReview.dao.find("select * from product_review where product_id = ? and ratings in (5, 4)", id).size(); 
    	int rn2 = ProductReview.dao.find("select * from product_review where product_id = ? and ratings in (2, 3)", id).size(); 
    	int rn3 = ProductReview.dao.find("select * from product_review where product_id = ? and ratings in (1, 0)", id).size(); 
    	
    	int r1 = 1;
    	int r2 = 0;
    	int r3 = 0;
    	
    	int sum = rn1  + rn2 + rn3;
    	
    	if(sum != 0){
    		r1 = (int) MathHelper.cutDecimal(rn1 / sum);
    		r2 = (int) MathHelper.cutDecimal(rn2 / sum);
    		r3 = (int) MathHelper.cutDecimal(rn3 / sum);
    	}
    	
    	ratings.put("r1", r1 * 100);
    	ratings.put("r2", r2 * 100);
    	ratings.put("r3", r3 * 100);
    	
    	return ratings;
    }
    
    /**
     * 检测订单产品是否已经全部退款或退货完
     * @param customerId
     * @param productOrderId
     */
    private static boolean isRefundOrBack(int customerId, int productOrderId) {
    	ProductOrder productOrder =  ProductOrder.dao.findById(productOrderId);
    	int unitOrdered = productOrder.getUnitOrdered();
    	int totalAmount = 0;
    	
    	//计算该订单产品的退货退款数量
    	List<Refund> refunds = Refund.dao.find("select * from refund where customer_id = ? and product_order_id = ? and status != ?", customerId, productOrderId, 2);
    	for (Refund refund : refunds) {
    		totalAmount += refund.getRefundAmount();
    	}
    	
    	List<Back> backs = Back.dao.find("select * from back where product_order_id = ? and customer_id = ? and status != ?", productOrderId, customerId, 2);
    	for (Back back : backs) {
    		totalAmount += back.getRefundAmount();
    	}
    	
    	if (totalAmount >= unitOrdered) {
    		return true;
    	}
    	return false; 
    }
    
    /**
     * 根据产品订单获取店铺id
     * @param productOrderId
     * @return
     */
    private static int getShopIdByProductOrderId(int productOrderId) {
    	int shopId = 0;
    	ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    	
    	if (productOrder != null) {
    		int productId = productOrder.getProductId();
    		Product product = Product.dao.findById(productId);
    		
    		shopId = (product != null) ? product.getShopId() : 0;
    	}
    	return shopId;
    }
    
    /**
     * 生成退款单号，当前时间的毫秒数为退款单号
     * @return
     */
    private static String generateRefundCode() {
    	String refundCode = System.currentTimeMillis() + "";
    	return refundCode;
    }
    
    /**
     * 判断退款数量是否合理
     * @param refundAmount
     * @param productOrderId
     * @return
     */
    private static boolean checkRefundAmount(int refundAmount, int productOrderId) {
    	ProductOrder productOrder = getProductOrder(productOrderId);
    	int unitOrdered = productOrder.getUnitOrdered();
    	return unitOrdered >= refundAmount;
    }
    
    /**
     * 判断退款金额是否合理
     * @param refundCash
     * @param productOrderId
     * @return
     */
    private static boolean checkRefundCash(BigDecimal refundCash, int productOrderId) {
    	try {
    		ProductOrder productOrder = getProductOrder(productOrderId);
//    		注意，人玮并不是将totalActualProductPrice视为实际支付的金额，而是将totalActualDeliveryCharge视为实际支付的钱
//        	此属性变化解决pc端无法退款而提示“已发货，请选择退货操作”的问题
			BigDecimal totalActualProductPrice = productOrder.getTotalActualDeliveryCharge();
			//BigDecimal totalActualProductPrice = productOrder.getTotalActualDeliveryCharge();
        	String sql = "select id, applyCash, product_order_id from refund where status in (0, 1, 3)" +
        			" union" +
        			" select id, applyCash, product_order_id from back where status in (0, 1, 4)";
        	sql = "select * from " + "(" + sql + ") as t" + " where product_order_id = " + productOrderId;
        	List<Record> lst = Db.find(sql);
        	BigDecimal totalCash = new BigDecimal(0);
        	for (Record item : lst) {
        		totalCash = totalCash.add(item.getBigDecimal("applyCash"));
        	}
        	totalCash = totalCash.add(refundCash);
        	return totalCash.compareTo(totalActualProductPrice) <= 0;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    /**
     * 提交退款申请
     * @param model
     * @param picsStr ["path1", "path2",...]
     * @return
     */
    public static ServiceCode refund(final Refund model, final String picsStr) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	//判断订单的某个产品是否已经申请过退款或退货
    	int productOrderId = model.getProductOrderId();
    	int customerId = model.getCustomerId();
    	if (isRefundOrBack(customerId, productOrderId)) {
			return ServiceCode.Function;
		}
    	
    	//检查是否待发货状态，如果不是，则不能申请退款
    	Order order = BaseDao.getOrderByProductOrderId(productOrderId);
    	if (order.getStatus() != BaseDao.PAYED) {
			return ServiceCode.Validation;
		}
    	
    	//检测退货数量是否合理
	Boolean b = !checkRefundAmount(model.getRefundAmount(), productOrderId);
    	if (!checkRefundAmount(model.getRefundAmount(), productOrderId)) {
			return ServiceCode.Validation;
		}
    	Boolean c = !checkRefundCash(model.getRefundCash(), productOrderId);
    	//检测退款金额是否合理
		BigDecimal r = model.getRefundCash();
    	if (!checkRefundCash(model.getRefundCash(), productOrderId)) {
			return ServiceCode.Validation;
		}
    	
    	final int shopId = getShopIdByProductOrderId(productOrderId);
    	final String refundCode = generateRefundCode();
    	final ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    	
    	boolean success = Db.tx(new IAtom() {
            @Override
			public boolean run() throws SQLException {
                try {
                	model.setShopId(shopId);
                	model.setRefundCode(refundCode);
                	model.setCreatedAt(new Date());
                	model.setUpdatedAt(new Date());
                	model.setRefundAmount(productOrder.getUnitOrdered());
                	model.setApplyCash(model.getRefundCash());
                	model.setRefundCash(model.getRefundCash());
                	model.save();
            		List<String> pics = JSON.parseArray(picsStr, String.class);
            		for (String item : pics) {
            			if (!item.equals("") && picsStr != null) {
            				Resource res = new Resource();
            				res.setCategory(8); //退货
            				res.setType(1);
            				res.setPath(item);
            				res.setRelateId(model.getId());
            				res.setCreatedAt(new Date());
            				res.setUpdatedAt(new Date());
            				res.save();
            			}
            		}
            		productOrder.setStatus(2);
        			productOrder.update();
            		return true;
                } catch (Exception e) {
                	Log.error(e.getMessage() + ",生成退款单失败");
                    return false;
                }
            }
        });
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }

    /**
     * 提交退货申请
     * @param model
     * @param picsStr
     * @return -4退货金额不正确，-3退货数量不正确，-2还未发货不能申请退货，-1已申请过退货，0成功，1失败
     */
    public static int returned(final Back model, final String picsStr) {
    	if (model == null) {
    		return 1;
    	}
    	
    	//判断订单里的某个产品是否已经申请退货
    	int customerId = model.getCustomerId();
    	int productOrderId = model.getProductOrderId();
    	if (isRefundOrBack(customerId, productOrderId)) {
			return -1;
		}
    	
    	Order order = BaseDao.getOrderByProductOrderId(productOrderId);
    	//1: 待付款,2: 待发货,3: 待收货,4: 待评价,5: 已评价,6: 已取消, 7订单完成
    	int status = order.getStatus();
    	
    	//判断是否已发货，如果未发货，则不能申请退货
    	if (status != 3 && status != 4) {
			return -2;
		}
    	
    	//检测退货数量是否合理
    	if (!checkRefundAmount(model.getRefundAmount(), productOrderId)) {
			return -2;
		}
    	
    	//检测退款金额是否合理
    	if (!checkRefundCash(model.getRefundCash(), productOrderId)) {
			return -4;
		}
    	
    	final int shopId = getShopIdByProductOrderId(productOrderId);
    	final String refundCode = generateRefundCode();
    	final ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    	
    	boolean success = Db.tx(new IAtom() {
            @Override
			public boolean run() throws SQLException {
                try {
                	model.setShopId(shopId);
                	model.setRefundCode(refundCode);
                	model.setCreatedAt(new Date());
                	model.setUpdatedAt(new Date());
                	model.setRefundAmount(productOrder.getUnitOrdered());
                	model.setApplyCash(model.getRefundCash());
                	model.setRefundCash(model.getRefundCash());
                	model.save();
                	//保存退款图片
            		List<String> pics = JSON.parseArray(picsStr, String.class);
            	
            		if(pics != null && pics.size() > 0) {
	            		for (String item : pics) {
	            			if (!item.equals("") && item != null) {
	            				Resource res = new Resource();
	            				res.setCategory(7); //退货
	            				res.setType(1);
	            				res.setPath(item);
	            				res.setRelateId(model.getId());
	            				res.setCreatedAt(new Date());
	            				res.setUpdatedAt(new Date());
	            				res.save();
	            			}
	            		}
            		}
            		productOrder.setStatus(2);
        			productOrder.update();
        			return true;
                } catch (Exception e) {
                	e.printStackTrace();
                	Log.error(e.getMessage() + ",生成退货单失败");
                    return false;
                }
            }
        });
    	
    	return success ? 0 : 1;
    }

    /**
     * 选择产品
     * @param productId
     * @param typeAttr
     * @return
     */
    public static ProductPrice selectProduct(int productId, String typeAttr) {
    	return BaseDao.getProductPrice(productId, typeAttr);
    }
    
    /**
     * 计算商品总金额和总数量
     * @param [{price:3,amount:4,totalPrice:33},...]
     * @return {totalAmount:3,totalPayable:4.5}
     */
    private static Record calculate(List<Record> products) {
    	double totalPayable = 0;
    	double totalAmount = 0;
    	for (Record item: products) {
    		totalAmount += item.getInt("amount");
			totalPayable += item.getDouble("totalPrice");
    	}
    	Record result = new Record();
    	result.set("totalPayable", totalPayable);
    	result.set("totalAmount", totalAmount);
    	return result;
    }
    
    /**
     * 计算产品购买总金额
     * @param products [{price:3,amount:4,totalPrice:33},...]
     * @return
     */
    public static double calculateProductTotalPayable(List<Record> products) {
    	Record result = calculate(products);
    	return result.getDouble("totalPayable");
    }
    
    /**
     * 计算产品购买总数量
     * @param products [{price:3,amount:4,totalPrice:33},...]
     * @return
     */
    public static double calculateProductTotalAmount(List<Record> products) {
    	Record result = calculate(products);
    	return result.getDouble("totalAmount");
    }
    
    /**
     * 订单总金额
     * @param theSameOrderNum
     * @return
     */
    public static double calculateOrderPayable(String theSameOrderNum) {
    	List<Order> list = Order.dao.find("select * from `order` where theSameOrderNum = ?", theSameOrderNum);
    	
    	double totalPayable = 0;

    	for (Order item : list) {
    		totalPayable += item.getTotalPayable().doubleValue();
    	}
    	
    	return totalPayable;
    }

    /**
     * 计算订单商品总金额
     * @param orders
     * @return
     */
    public static double calculateTotalActualProductPrice(List<Order> orders) {
    	double totalActualProductPrice = 0;
    	
    	for (Order order : orders) {
    		totalActualProductPrice += order.getTotalActualProductPrice().doubleValue();
    	}
    	
    	return totalActualProductPrice;
    }
    
    /**
     * 获取订单
     * @param theSameOrderNum
     * @return
     */
    public static Order getOrder(String theSameOrderNum) {
    	return Order.dao.findFirst("select * from `order` where theSameOrderNum = ?", theSameOrderNum);
    }
    
    public static Order getOrderByOrderCode(String orderCode) {
    	return Order.dao.findFirst("select * from `order` where order_code = ?", orderCode);
    }
    
    /**
     * 获取订单
     * @param theSameOrderNum
     * @return
     */
    public static Order getOrder(String theSameOrderNum, String orderCode) {
    	return Order.dao.findFirst("select * from `order` where theSameOrderNum = ? or order_code=?", theSameOrderNum, orderCode);
    }
    
    /**
     * 获取产品订单明细
     * @param orderId
     * @return
     */
    public static List<ProductOrder> getProductOrder(Integer orderId) {
    	List<ProductOrder> list = ProductOrder.dao.find("select * from `product_order` where order_id = ?", orderId);
    	for (ProductOrder item : list) {
    		Product product = Product.dao.findById(item.getProductId());
    		int mp = (product != null) ? product.getMainPic() : 0;
    		String mainPic = ResourceService.getPath(mp);
    		item.put("mainPic", mainPic);
    	}
    	return list;
    }
    
    /**
     * 更新商品销售量和销售额
     * @param order_code
     */
    public static void updateProduct(String order_code) {
    	//更新商品销售量和销售额
		Order order = Member.getOrder(order_code,order_code);
		List<ProductOrder> list = Member.getProductOrder(order.getId());
		BigDecimal saleroom = null;
		for(ProductOrder productOrder: list) {
			Product product = Member.getProduct(productOrder.getProductId());
			if(product.getSaleroom() == null) {
				saleroom = productOrder.getTotalActualProductPrice();
			}else {
				saleroom = product.getSaleroom().add(productOrder.getTotalActualProductPrice());
			}
			product.setSaleroom(saleroom);
			if(product.getSalesVolume() == null) {
				product.setSalesVolume(productOrder.getUnitOrdered());
			}else {
				product.setSalesVolume(product.getSalesVolume()+productOrder.getUnitOrdered());
			}
			product.update();
		}
    }
    
    /**
     * 获取产品信息
     * @param orderId
     * @return
     */
    public static Product getProduct(Integer id) {
    	return Product.dao.findById(id);
    }
    
    
    /**
     * 批量查询用户订单
     * @param offset
     * @param count
     * @param customerId
     * @param orderCode
     * @param theSameOrderCode
     * @param status
     * @param startDate
     * @param endDate
     * @return [{theSameOrderNum:1233,created_at:创建时间,orders:[{id:订单id,order_code:订单号,shopName:店铺名称,shopLogo:店铺logo,shop_id:店铺id,productOrders:[{id:订单明细id,mainPic:产品图片,product_name:产品名称,unitOrdered:数量,actualUnitPrice:价格},...]},...]},...]
     */
    public static List<Record> findMermberOrderItems(int offset, int count, Integer customerId, 
    		String orderCode, String theSameOrderNum, Integer status, String startDate, String endDate) {
    	
    	String sql = findMermberOrderItemsSql(customerId, orderCode, theSameOrderNum, status, startDate, endDate);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	List<Record> list = Db.find(sql);
    	List<Record> shops = Db.find("select * from shop");
    	List<Record> resources = Db.find("select * from resource");
    	List<Record> allProductOrders = Db.find("select * from product_order");
    	List<Record> products = Db.find("select * from product");
    	
    	for (Record item : list) {
    		Record shop = BaseDao.findItem(item.getInt("shop_id"), shops, "id");
			String shopName = shop.getStr("name");
			Record resource = BaseDao.findItem(shop.getInt("logoPic"), resources, "id");
			String shopLogo = resource.getStr("path");
			item.set("shopName", shopName);
			item.set("shopLogo", shopLogo);
			
			List<Record> productOrders = BaseDao.findItems(allProductOrders, "order_id", item.getInt("id"));
			for (Record productOrder : productOrders) {
				Record product = BaseDao.findItem(productOrder.getInt("product_id"), products, "id");
				int mpId = (product != null) ? product.getInt("mainPic") : 0;
				Record res = BaseDao.findItem(mpId, resources, "id");
				String mainPic = (res != null) ? res.getStr("path") : "";
				productOrder.set("mainPic", mainPic);
			}
			
			item.set("productOrders", productOrders);
    	}
    	
    	return list;
    }
    
    /**
     * 批量查询用户订单的总数量
     * @param customerId
     * @param orderCode
     * @param theSameOrderCode
     * @param status
     * @param startDate
     * @param endDate
     * @return
     */
    public static int countMermberOrderItems(Integer customerId, String orderCode, String theSameOrderNum, 
    		Integer status, String startDate, String endDate) {
    	
    	String sql = findMermberOrderItemsSql(customerId, orderCode, theSameOrderNum, status, startDate, endDate);
    	return Db.find(sql).size();
    }
    
    /**
     * 获取店铺
     * @param productOrderId
     * @return
     */
    public static Shop getShopByproductOrderId(int productOrderId) {
    	String sql = "SELECT phone, email FROM shop WHERE id=(SELECT shop_id  FROM `order` WHERE id= (SELECT order_id FROM product_order WHERE id=?))";
    	Shop shop = Shop.dao.findFirst(sql, productOrderId);
    	return shop;
    }
    
    /**
     * 获取店铺
     * @param productOrderId
     * @return
     */
    public static Shop getShopByOrderCode(String orderCode) {
    	String sql = "SELECT phone, email FROM shop WHERE id=(SELECT shop_id  FROM `order` WHERE order_code=?)";
    	Shop shop = Shop.dao.findFirst(sql, orderCode);
    	return shop;
    }
    
   /**
    * 判断单品是否临近界限
    * @param priceId 单品id
    * @param productId 产品id
    * @param amount 数量
    * @return
    */
    private static Boolean projectIsLimit(Integer priceId, Integer productId, Integer amount) {
    	
    	ProductPrice price = ProductPrice.dao.findById(priceId);
    	Product product = Product.dao.findById(productId);
    	boolean flag = Boolean.FALSE;
    	if(price != null ) {
    		//更新产品、单品剩余数量
    		price.setProductNumber(price.getProductNumber() - amount);
    		price.update();
    		//判断单品是否临界
    		if(price.getProductNumber() <=product.getWarningValue()) {
    			flag =  Boolean.TRUE;
    		}
    	}
    	if(product != null) {
    		product.setStoreAmount(product.getStoreAmount() - amount);
    		product.update();
    		//判断单品是否临界
    		if(product.getStoreAmount() <=product.getWarningValue()) {
    			flag = Boolean.TRUE;
    		}
    	}
    	return flag;
    }
    

    /**
     * 向商家发送短信和邮件提醒
     * @param shop 商家信息
     * @param message 短信信息
     * @param subjet 邮件主题
     * @param content 邮件内容
     */
    public static void informShop(Shop shop, String message, String subjet, String content) {
    	if(shop != null) {
			try {
				String email = shop.getEmail();
				String phone = shop.getPhone();
				if (phone != null && !phone.equals("")) {
					SMSHelper.sendMessage(shop.getPhone(), message);
				}
				if (email != null && !email.equals("")) {
					MailHelper.sendEmail(shop.getEmail(), subjet, content);
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
	   	}
	 }
    
    /**
     * 每个待提交的订单添加 店铺订单总金额 和 运费
     * @param myShopCartList 待提交订单列表
     * @return [{deliveryPrice:店铺订单运费, totalPayable:店铺订单总金额 , shop_id:店铺id, shopName:店铺名称, products:[{product_id:产品id,productName:产品名称,mainPic:产品主图,selectProterties:所选属性,amount:购买数量,suggestedRetailUnitPrice:产品价格,totalPrice:总价, logisticsTemplateId:运费模板id},...}]},...}]
     */
    @SuppressWarnings("static-access")
	public static List<Record> addAllPriceAndFreight(List<Record> myShopCartList) {
    	Logistics logistics = new Logistics();
    	
    	for (Record record : myShopCartList) {
    		
    		double totalPayable = 0;
			List<Record> products = record.get("products");
			List<Record> details = new ArrayList<>();
			int templateId = 0;

			for (Record product : products) {
				
				double totalPrice = product.getDouble("totalPrice");
				//店铺订单总金额
				totalPayable += totalPrice;
				
				templateId = product.getInt("logisticsTemplateId");
				
				Record detailsRecord = new Record();
				detailsRecord.set("product_id", product.getInt("product_id"));
				detailsRecord.set("template_id", templateId);
				detailsRecord.set("amount", product.getInt("amount"));
				details.add(detailsRecord);
			}
			//获取运费模板省id，市id
			Record logisticsTemplate = Logistics.getLogisticsTemplate(templateId);
			int provinceId = logisticsTemplate.getInt("province_id");
			int cityId = logisticsTemplate.getInt("city_id");
			
			//店铺订单运费
			double deliveryPrice = logistics.getOrderFreight(details, provinceId, cityId);
			//店铺订单总金额(包含运费)
			record.set("totalPayable", totalPayable + deliveryPrice);
			
			record.set("deliveryPrice", deliveryPrice);
			
    	}
    	return myShopCartList;
    }
    
    /**
     * 组装sql
     * @param customerId
     * @param orderCode
     * @param theSameOrderCode
     * @param status
     * @param startDate
     * @param endDate
     * @return
     */
    public static String findMermberOrderItemsSql(Integer customerId, String orderCode, String theSameOrderNum,
    		Integer status, String startDate, String endDate) {
    	
    	String sql = "select * from `order` where id != 0";
    	
    	if (customerId != null) {
			sql += " and customer_id = " + customerId;
		}
    	if (orderCode != null && !orderCode.equals("")) {
			sql += " and order_code like '%" + orderCode + "%'";
		}
    	if (theSameOrderNum != null && !theSameOrderNum.equals("")) {
			sql += " and theSameOrderNum like '%" + theSameOrderNum + "%'";
		}
    	if (status != null && status != -1) {
			sql += " and status = " + status;
		}
    	if (startDate != null && !startDate.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d') >= '" + startDate + "'";
		}
    	if (endDate != null && !endDate.equals("")) {
			sql += " and DATE_FORMAT(created_at, '%Y-%m-%d') <= '" + endDate + "'";
		}
    	
    	sql += " order by created_at desc";
    	
    	return sql;
    }
    
    /**
     * 获取微信公众号支付参数
     * @param openId
     * @param tradeCode 商户订单号
     * @param ip
     * @return
     * @throws WxErrorException
     */
    public static Object getJsApiParameters(String openId, String tradeCode, String ip) throws WxErrorException {
    	String outTradeNo = tradeCode;
    	double amt = calculateOrderPayable(tradeCode);
    	String body = "乐驿商城";
    	String tradeType = "JSAPI";
    	String notifyUrl = PropKit.use("callBackUrl.txt").get("apiHostName") + PropKit.use("callBackUrl.txt").get("weixinPayNotifyUrl");
    	
    	Log.info("tradeCode"+tradeCode+",openid="+openId+",amt="+amt+",notifyUrl="+notifyUrl);
    	return WxPayHelper.getPrepayIdResult(openId, outTradeNo, amt, body, tradeType, notifyUrl, ip);
    }
    
    /**
     * 获取微信公众号支付参数
     * @param id 订单id
     * @return 支付参数
     * @throws WxErrorException 
     */
    public static Object getJsApiParameters(String openId, int orderId, String ip) throws WxErrorException {
    	Order order = Order.dao.findById(orderId);
    	String outTradeNo = order.getTheSameOrderNum() + "";
    	double amt = order.getTotalPayable().doubleValue();
    	String body = "乐驿商城";
    	String tradeType = "JSAPI";
    	String notifyUrl = PropKit.use("callBackUrl.txt").get("apiHostName") + PropKit.use("callBackUrl.txt").get("weixinPayNotifyUrl");
    	
    	return WxPayHelper.getPrepayIdResult(openId, outTradeNo, amt, body, tradeType, notifyUrl, ip);
    }
    
}