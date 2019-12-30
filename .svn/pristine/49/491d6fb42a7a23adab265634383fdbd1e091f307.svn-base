package com.eshop.controller.admin;


/**
 * 折扣券管理
 *   @author TangYiFeng
 */
public class CouponController extends AdminBaseController {

    /**
     * 构造方法
     */
    public CouponController() {
    }
    
    /**
     * 店铺的优惠券列表（根据当前登录用户id查询）
     *  @param token 帐户访问口令（必填）
     *  @param type (1折扣券，2现金券)
     *  @param pageNumber 当前页，用于分页
     *  @param pageSize 每页条数
     *  @return 成功：{error: 0, totalPages:总页数， coupons: [优惠券信息, ...]}；失败：{error: >0, errmsg: 错误信息}
     */
    /*@Before(AdminAuthInterceptor.class)
    public void many() {
    	if (!this.validateRequiredString("pageNumber")) {
    		return;
    	}
    	
    	if (!this.validateRequiredString("pageSize")) {
    		return;
    	}
    	
    	int pageNumber = getParaToInt("pageNumber");
    	int pageSize = getParaToInt("pageSize");
    	
    	//获取优惠券
    	String select = "select *";
    	String sqlExceptSelect = "from coupon order by created_at desc";
    	
    	Page<Record> page = couponService.pageCoupon(pageNumber, pageSize, select, sqlExceptSelect);
    	
    	jsonObject.put("coupons", page.getList());
    	jsonObject.put("totalPage", page.getTotalPage());
    	jsonObject.put("totalRow", page.getTotalRow());
    	renderJson(jsonObject);
    }
    
    *//**
     * 创建优惠券
     * token 帐户访问口令（必填）
     * title 优惠券名称
     * baseOn 产品适用范围(1全订单，2特定产品)
     * full 最低金额
     * value 折扣
     * startDate 折扣券存入时间
     * endDate 失效时间
     * type 类型（1折扣券，2现金券）
     * description 描述 选填
     * amount 优惠券数量
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	if (!this.validateRequiredString("title")) {
    		return;
    	}
    	String title = getPara("title");
    	
    	if (!this.validateRequiredString("baseOn")) {
    		return;
    	}
    	int baseOn = getParaToInt("baseOn");
    	
    	if (!this.validateRequiredString("full")) {
    		return;
    	}
    	BigDecimal full = getParaToDecimal("full");
    	
    	if (!this.validateRequiredString("value")) {
    		return;
    	}
    	BigDecimal value = getParaToDecimal("value");
    	
    	if (!this.validateRequiredString("startDate")) {
    		return;
    	}
    	Date startDate = getParaToDate("startDate");
    	
    	if (!this.validateRequiredString("endDate")) {
    		return;
    	}
    	Date endDate = getParaToDate("endDate");
    	
    	if (!this.validateRequiredString("type")) {
    		return;
    	}
    	int type = getParaToInt("type");
    	
    	if (!this.validateRequiredString("amount")) {
    		return;
    	}
    	int amount = getParaToInt("amount");
    	
    	Coupon model = new Coupon();
    	model.setTitle(title);
    	model.setStartDate(startDate);
    	model.setEndDate(endDate);
    	model.setBaseOn(baseOn);
    	model.setScope(1);
    	model.setType(type);
    	model.setFull(full);
    	model.setValue(value);
    	model.setShopId(0);
    	model.setAmount(amount);
    	
    	if (getPara("description") != null) {
    		model.setDescription(getPara("description"));
    	}
    	
    	ServiceCode code = couponService.createCoupon(model);
    	
    	if (code == ServiceCode.Function) {
    		returnError(-1, "传入的值错误");
    		return;
    	}
    	
    	if(code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createCoupon failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 修改折扣券
     * token 帐户访问口令（必填）
     * id 优惠券id
     * title 优惠券名称
     * baseOn 产品适用范围(1全订单，2特定产品)
     * full 最低金额
     * value 折扣
     * startDate 折扣券存入时间
     * endDate 失效时间
     * type 类型（1折扣券，2现金券）
     * description 描述 选填
     * amount 优惠券数量
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void update() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if (!this.validateRequiredString("title")) {
    		return;
    	}
    	String title = getPara("title");
    	
    	if (!this.validateRequiredString("baseOn")) {
    		return;
    	}
    	int baseOn = getParaToInt("baseOn");
    	
    	if (!this.validateRequiredString("full")) {
    		return;
    	}
    	BigDecimal full = getParaToDecimal("full");
    	
    	if (!this.validateRequiredString("value")) {
    		return;
    	}
    	BigDecimal value = getParaToDecimal("value");
    	
    	if (!this.validateRequiredString("startDate")) {
    		return;
    	}
    	Date startDate = getParaToDate("startDate");
    	
    	if (!this.validateRequiredString("endDate")) {
    		return;
    	}
    	Date endDate = getParaToDate("endDate");
    	
    	if (!this.validateRequiredString("type")) {
    		return;
    	}
    	int type = getParaToInt("type");
    	
    	if (!this.validateRequiredString("amount")) {
    		return;
    	}
    	int amount = getParaToInt("amount");
    	
    	Coupon model = couponService.getCoupon(id);
    	
    	if (model == null) {
    		returnError(ErrorCode.Exception, "该优惠券不存在");
    		return;
    	}
    	
    	model.setTitle(title);
    	model.setStartDate(startDate);
    	model.setEndDate(endDate);
    	model.setBaseOn(baseOn);
    	model.setType(type);
    	model.setFull(full);
    	model.setValue(value);
    	model.setAmount(amount);
    	
    	if (getPara("description") != null) {
    		model.setDescription(getPara("description"));
    	}
    	
    	ServiceCode code = couponService.updateCoupon(model);
    	
    	if (code == ServiceCode.Function) {
    		returnError(-1, "传入的值错误");
    		return;
    	}
    	
    	if(code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createCoupon failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 查看折扣券
     * @param id 优惠券id
     * @return 成功：{error: 0 coupon:{id,.....}}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Coupon coupon = couponService.getCoupon(id);
    	
    	jsonObject.put("coupon", coupon);
    	renderJson(jsonObject);
    }
    
    *//**
     * 删除折扣券  -批量删除
     * ids 优惠券ids[]
     *//*
    @Before(AdminAuthInterceptor.class)
    public void batchDelete() {
    	if(!this.validateRequiredString("ids")) {
    		return;
    	}
    	String idsStr = getPara("ids");
    	
    	List<String> ids = JSON.parseObject(idsStr, List.class);
    	for(String item : ids){
    		int id = Integer.parseInt(item);//[String]待转换的字符串
    		if(couponService.deleteCoupon(id) != ServiceCode.Success) {
        		returnError(ErrorCode.Exception, "delete failed");
        		return;
        	}
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 删除折扣券  -单个删除
     * id 优惠券id
     *//*
    @Before(AdminAuthInterceptor.class)
    public void delete() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	
    	int id = getParaToInt("id");
    	ServiceCode code = couponService.deleteCoupon(id);
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "删除失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 创建会员优惠券
     * id 优惠券id
     * customerId 客户id
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void createCustomerCoupon() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if (!this.validateRequiredString("customerId")) {
    		return;
    	}
    	int customerId = getParaToInt("customerId");
    	
    	CustomerCoupon customerCoupon = new CustomerCoupon();
    	customerCoupon.setCustomerId(customerId);
    	customerCoupon.setCouponId(id);
    	
    	ServiceCode code = couponService.createCustomerCoupon(customerCoupon);
    	
    	if (code == ServiceCode.Function) {
    		returnError(-1, "优惠券已被领取完");
    		return;
    	}
    	
    	if (code != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "创建会员优惠券失败");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 获取会员优惠券列表
     * id 优惠券id
     * pageNumber 页码
     * pageSize 每页条数
     * @return 成功：{error: 0,recordsTotal：记录数 totalPage: 总页数, customerCoupons:[{id,...}...]}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void manyCustomerCoupon() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if (!this.validateRequiredString("pageNumber")) {
    		return;
    	}
    	int pageNumber = getParaToInt("pageNumber");
    	
    	if (!this.validateRequiredString("pageSize")) {
    		return;
    	}
    	int pageSize = getParaToInt("pageSize");
    	
    	String select = "select a.*, b.name";
    	String sqlExceptSelect = "from customer_coupon as a left join customer as b on a.customerId = b.id where couponId = " + id;
    	
    	Page<Record> page = couponService.pageCouponCustomer(pageNumber, pageSize, select, sqlExceptSelect);
    	
    	jsonObject.put("customerCoupons", page.getList());
        jsonObject.put("page", pageNumber);
    	jsonObject.put("recordsTotal", page.getTotalRow());
    	jsonObject.put("totalPage", page.getTotalPage());
    	renderJson(jsonObject);
    }
    
    *//**
     * 删除用户折扣券
     * id 用户折扣券id
     * @return 成功：{error: 0, error:-1(该用户优惠券已被使用，不能删除)}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void deleteCustomerCoupon() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	ServiceCode code = couponService.deleteCustomerCoupon(id);
    	
		if(code == ServiceCode.Function) {
    		returnError(-1, "该用户优惠券已被使用，不能删除");
    		return;
    	}
		
		if (code != ServiceCode.Success) {
			returnError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
    }
    
    *//**
     * 获取折扣券产品列表
     * @param id 优惠券id
     * @param pageNumber 页码
     * @param pageSize 每页条数
     * @return 成功：{error: 0, data:[{id:id,couponId:优惠券id,type:类型(1分类，2产品),ojectId:相关id,name:产品或分类名称,mainPic:图片},...]}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void manyCouponProduct() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if (!this.validateRequiredString("pageNumber")) {
    		return;
    	}
    	int pageNumber = getParaToInt("pageNumber");
    	
    	if (!this.validateRequiredString("pageSize")) {
    		return;
    	}
    	int pageSize = getParaToInt("pageSize");
    	
    	Record result = couponService.paginateCouponProduct(pageNumber, pageSize, id);
        
    	jsonObject.put("data", result.get("list"));
        jsonObject.put("pageSize", pageSize);
    	jsonObject.put("totalPage", result.getInt("totalPage"));
    	jsonObject.put("totalRow", result.getInt("totalRow"));
    	renderJson(jsonObject);
    }
    
    *//**
     * 添加优惠券产品
     * objectId 产品或分类id
     * couponId 折扣券id
     * type 类型(1分类，2产品)
     *//*
    @Before(AdminAuthInterceptor.class)
    public void createCouponProduct() {
    	if (!this.validateRequiredString("objectId")) {
    		return;
    	}
    	int objectId = getParaToInt("objectId");
    	
    	if (!this.validateRequiredString("couponId")) {
    		return;
    	}
    	int couponId = getParaToInt("couponId");
    	
    	if (!this.validateRequiredString("type")) {
    		return;
    	}
    	int type = getParaToInt("type");
    	
    	CouponProduct model = new CouponProduct();
    	model.setType(type);
    	model.setObjectId(objectId);
    	model.setCouponId(couponId);
    	
    	if(couponService.createCouponProduct(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createCouponProduct failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 删除折扣券产品
     * id 折扣券适用产品id
     *//*
    @Before(AdminAuthInterceptor.class)
    public void deleteCouponProduct() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if(couponService.deleteCouponProduct(id) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "deleteCouponProduct failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 根据id查看某个优惠券使用产品
     * @param id coupon_product的id
     * @return 成功：{error: 0 data:{id...}}；失败：{error: >0, errmsg: 错误信息}
     *//*
    public void getCouponProduct(){
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	CouponProduct data = couponService.getCouponProduct(id);
    	
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }
    
    *//**
     * 获取所有可用产品
     * @param pageNumber
     * @param pageSize
     * @return 成功：{error: 0,data:[id:产品id,name:产品名称,suggestedRetailUnitPrice:指导价格]}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void getProducts() {
    	if (!this.validateRequiredString("pageNumber")) {
    		return;
    	}
    	int pageNumber = getParaToInt("pageNumber");
    	
    	if (!this.validateRequiredString("pageSize")) {
    		return;
    	}
    	int pageSize = getParaToInt("pageSize");
    	
    	String select = "select *";
    	String sqlExceptSelect = "from product where is_sale = 1 and isDelete = 0";
    	
    	Page<Record> page = shopService.pageShopProduct(pageNumber, pageSize, select, sqlExceptSelect);
    	List<Record> list = page.getList();
    	
    	for (Record item : list) {
    		String mainPic = this.getResourcePathById(item.getInt("mainPic"));
    		item.set("mainPic", mainPic);
    	}
    	
    	jsonObject.put("totalPage", page.getTotalPage());
    	jsonObject.put("totalRow", page.getTotalRow());
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }
    
    *//**
     * 获取所有可用分类
     * @param pageNumber
     * @param pageSize
     * @return 成功：{error: 0,data:[id:分类id,name:分类名称,mainPic:分类主图]}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void getCategorys() {
    	if (!this.validateRequiredString("pageNumber")) {
    		return;
    	}
    	int pageNumber = getParaToInt("pageNumber");
    	
    	if (!this.validateRequiredString("pageSize")) {
    		return;
    	}
    	int pageSize = getParaToInt("pageSize");
    	
    	String select = "select *";
    	String sqlExceptSelect = "from category where isDelete = 0";
    	
    	Page<Record> page = categoryService.paginateCategory(pageNumber, pageSize, select, sqlExceptSelect);
    	List<Record> list = page.getList();
    	
    	for (Record item : list) {
    		String mainPic = this.getResourcePathById(item.getInt("mainPic"));
    		item.set("mainPic", mainPic);
    	}
    	
    	jsonObject.put("totalPage", page.getTotalPage());
    	jsonObject.put("totalRow", page.getTotalRow());
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
    }*/
}