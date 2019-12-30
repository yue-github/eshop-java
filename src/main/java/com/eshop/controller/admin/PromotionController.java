package com.eshop.controller.admin;

/**
 * 活动管理-控制器
 * @author TangYiFeng
 */
public class PromotionController extends AdminBaseController {
	
    /**
     * 构造方法
     */
    public PromotionController() {
    }

    /**
     * 获取促销活动
     * @param token 用户登录口令
     * @param draw 页码
     * @param length 每页显示条数
     * @return 成功：{error: 0, draw: 页码, recordsTotal: 总数, recordsFiltered: 过滤后总数, data: [{id,。。。},...}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void many() {
    	int draw = getParaToInt("draw");      
    	int start = getParaToInt("start"); 
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");  //每页显示条数
//    	int pageNumber = start / length + 1;  //页数
    	
    	Page<Promotion> page = Promotion.dao.paginate(offset, length, "select * ", "from promotion");
    	List<Promotion> cList = page.getList();
    	
//    	jsonObject.put("draw", draw);
    	jsonObject.put("recordsTotal", page.getTotalRow());
    	jsonObject.put("recordsFiltered", page.getTotalRow());
    	jsonObject.put("data", cList);
    	
    	renderJson(jsonObject);
    }*/
    
    /**
     * 查看促销活动
     * @param id 促销活动id
     * @return 成功：{error: 0 Promotion:{id,.....}}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void get() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Promotion Promotion = promotionsService.get(id);
    	jsonObject.put("data", Promotion);
    	renderJson(jsonObject);
    }
    
    *//**
     * 修改促销活动
     * @param token 用户登录口令
     * @param id 促销活动id
     * @param PromotionTitle 促销活动名称
     * @param appliedTo 适用范围
     * @param conditions 条件
     * @param minPurchaseAmount 最低金额
     * @param percentageDiscount 折扣
     * @param groupAmount 团购数量
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void edit() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if(!this.validateRequiredString("promotionTitle")) {
    		return;
    	}
    	String promotionTitle = getPara("promotionTitle");
    	
    	if(!this.validateRequiredString("startTime")) {
    		return;
    	}
    	Date startTime = getParaToDate("startTime");
    	
    	if(!this.validateRequiredString("endTime")) {
    		return;
    	}
    	Date endTime = getParaToDate("endTime");
    	    	
    	Promotion model = new Promotion();
    	model.setId(id);
    	model.setPromotionTitle(promotionTitle);
    	model.setStartTime(startTime);
    	model.setEndTime(endTime);
    	
    	if (getPara("groupAmount") != null) {
    		int groupAmount = getParaToInt("groupAmount");
    		model.setGroupAmount(groupAmount);
    	}
    	
    	if (getPara("promotionDescription") != null) {
    		String promotionDescription = getPara("promotionDescription");
    		model.setPromotionDescription(promotionDescription);
    	}
    	
    	if (getPara("appliedTo") != null) {
    		int appliedTo = getParaToInt("appliedTo");
    		model.setAppliedTo(appliedTo);
    	}
    	
    	if (getPara("conditions") != null) {
    		int conditions = getParaToInt("conditions");
    		model.setConditions(conditions);
    	}
    	
    	if (getPara("minPurchaseAmount") != null) {
    		BigDecimal minPurchaseAmount = getParaToDecimal("minPurchaseAmount");
    		model.setMinPurchaseAmount(minPurchaseAmount);
    	}
    	
    	if (getPara("cashOrPercentageDiscount") != null) {
    		int cashOrPercentageDiscount = getParaToInt("cashOrPercentageDiscount");
    		model.setCashOrPercentageDiscount(cashOrPercentageDiscount);
    	}
    	
    	if (getPara("basedOn") != null) {
    		int basedOn = getParaToInt("basedOn");
    		model.setBasedOn(basedOn);
    	}
    	
    	if(getPara("percentageDiscount") != null) {
    		BigDecimal percentageDiscount = getParaToDecimal("percentageDiscount");
    		model.setPercentageDiscount(percentageDiscount);
    	}
    	
    	if(getPara("cashDiscount") != null) {
    		BigDecimal cashDiscount = getParaToDecimal("cashDiscount");
    		model.setCashDiscount(cashDiscount);
    	}
    	
    	if(promotionsService.update(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "update "+ id +" failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 创建促销活动
     * @param token
     * @param PromotionTitle 促销活动名称
     * @param appliedTo 适用范围
     * @param conditions 条件
     * @param minPurchaseAmount 最低金额
     * @param percentageDiscount 折扣
     * @param depositTime 折扣券存入时间
     * @param expirationTime 失效时间
     * @param groupAmount 团购数量
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void create() {
    	if(!this.validateRequiredString("promotionTitle")) {
    		return;
    	}
    	String promotionTitle = getPara("promotionTitle");
    	
    	if(!this.validateRequiredString("startTime")) {
    		return;
    	}
    	Date startTime = getParaToDate("startTime");
    	
    	if(!this.validateRequiredString("endTime")) {
    		return;
    	}
    	Date endTime = getParaToDate("endTime");
    	
    	int shopId = 0;
    	    	
    	Promotion model = new Promotion();
    	model.setPromotionTitle(promotionTitle);
    	model.setStartTime(startTime);
    	model.setEndTime(endTime);
    	model.setShopId(shopId);
    	
    	if (getPara("groupAmount") != null) {
    		int groupAmount = getParaToInt("groupAmount");
    		model.setGroupAmount(groupAmount);
    	}
    	
    	if (getPara("promotionDescription") != null) {
    		String promotionDescription = getPara("promotionDescription");
    		model.setPromotionDescription(promotionDescription);
    	}
    	
    	if (getPara("appliedTo") != null) {
    		int appliedTo = getParaToInt("appliedTo");
    		model.setAppliedTo(appliedTo);
    	}
    	
    	if (getPara("conditions") != null) {
    		int conditions = getParaToInt("conditions");
    		model.setConditions(conditions);
    	}
    	
    	if (getPara("minPurchaseAmount") != null) {
    		BigDecimal minPurchaseAmount = getParaToDecimal("minPurchaseAmount");
    		model.setMinPurchaseAmount(minPurchaseAmount);
    	}
    	
    	if (getPara("cashOrPercentageDiscount") != null) {
    		int cashOrPercentageDiscount = getParaToInt("cashOrPercentageDiscount");
    		model.setCashOrPercentageDiscount(cashOrPercentageDiscount);
    	}
    	
    	if (getPara("basedOn") != null) {
    		int basedOn = getParaToInt("basedOn");
    		model.setBasedOn(basedOn);
    	}
    	
    	if(getPara("percentageDiscount") != null) {
    		BigDecimal percentageDiscount = getParaToDecimal("percentageDiscount");
    		model.setPercentageDiscount(percentageDiscount);
    	}
    	
    	if(getPara("cashDiscount") != null) {
    		BigDecimal cashDiscount = getParaToDecimal("cashDiscount");
    		model.setCashDiscount(cashDiscount);
    	}
    	
    	if(promotionsService.create(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createPromotion failed");
    	}
    	
    	renderJson(jsonObject);
    }


    *//**
     * 删除促销活动
     * ids 促销活动ids[]
     *//*
    @Before(AdminAuthInterceptor.class)
    public void delete() {
    	if(!this.validateRequiredString("ids")) {
    		return;
    	}
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseObject(idsStr, List.class);
    	for(String item : ids){
    		int id = Integer.parseInt(item);//[String]待转换的字符串
    		if(promotionsService.delete(id) != ServiceCode.Success) {
        		returnError(ErrorCode.Exception, "delete failed");
        	}
        	
        	renderJson(jsonObject);
    	}
    }
    
    *//**
     * 添加促销活动产品
     * @param token
     * @param promotion_id 促销活动id
     * @param product_id 产品id
     * @param cashDiscount 优惠金额
     * @param storeAmount 库存
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void addProduct() {
    	if(!this.validateRequiredString("promotion_id")) {
    		return;
    	}
    	int promotionId = getParaToInt("promotion_id");
    	
    	if(!this.validateRequiredString("product_id")) {
    		return;
    	}
    	int productId = getParaToInt("product_id");
    	
    	PromotionProduct model = new PromotionProduct();
    	model.setPromotionId(promotionId);
    	model.setProductId(productId);
    	
    	if (getPara("cashDiscount") != null) {
    		BigDecimal cashDiscount = getParaToDecimal("cashDiscount");
    		model.setCashDiscount(cashDiscount);
    	}
    	
    	if (getPara("storeAmount") != null) {
    		int storeAmount = getParaToInt("storeAmount");
    		model.setStoreAmount(storeAmount);
    	}
    	
    	if(promotionsService.addProduct(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createPromotion failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 修改促销活动产品
     * @param token
     * @param id 促销产品id
     * @param promotion_id 促销活动id
     * @param product_id 产品id
     * @param cashDiscount 优惠金额
     * @param storeAmount 库存
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void updateProduct() {
    	if (!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	if(!this.validateRequiredString("promotion_id")) {
    		return;
    	}
    	int promotionId = getParaToInt("promotion_id");
    	
    	if(!this.validateRequiredString("product_id")) {
    		return;
    	}
    	int productId = getParaToInt("product_id");
    	
    	PromotionProduct model = (PromotionProduct) PromotionProduct.dao.findById(id);
    	model.setPromotionId(promotionId);
    	model.setProductId(productId);
    	
    	if (getPara("cashDiscount") != null) {
    		BigDecimal cashDiscount = getParaToDecimal("cashDiscount");
    		model.setCashDiscount(cashDiscount);
    	}
    	
    	if (getPara("storeAmount") != null) {
    		int storeAmount = getParaToInt("storeAmount");
    		model.setStoreAmount(storeAmount);
    	}
    	
    	if(promotionsService.updateProduct(model) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "createPromotion failed");
    	}
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 删除促销活动产品
     * @param ids
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void deleteProduct() {
    	if(!this.validateRequiredString("ids")) {
    		return;
    	}
    	
    	String idsStr = getPara("ids");
    	List<String> ids = JSON.parseObject(idsStr, List.class);
    	
    	if(promotionsService.deleteProduct(ids) != ServiceCode.Success) {
    		returnError(ErrorCode.Exception, "deletePromotion failed");
    	}
    	
    	;
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 获取促销活动产品列表
     * @param token 用户登录口令
     * @param promotion_id 活动id
     * @param draw 页码
     * @param length 每页显示条数
     * @return 成功：{error: 0, draw: 页码, recordsTotal: 总数, recordsFiltered: 过滤后总数, data: [{id,name:产品名称,cashDiscount:优惠金额,storeAmount:库存},...]}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void manyProduct() {
    	int draw = getParaToInt("draw");      
    	int start = getParaToInt("start"); 
    	int length = getParaToInt("length");  //每页显示条数
    	int pageNumber = start / length + 1;  //页数
    	
    	if(!this.validateRequiredString("promotion_id")) {
    		return;
    	}
    	int promotionId = getParaToInt("promotion_id");
    	
    	String select = "select promotion_product.*, product.name";
    	String sqlExceptSelect = "from promotion_product "
    			+ "left join product on promotion_product.product_id = product.id "
    			+ "where promotion_id = " + promotionId;
    	
    	Page<Record> page = promotionsService.paginateProduct(pageNumber, length, select, sqlExceptSelect);
    	
    	jsonObject.put("draw", draw);
    	jsonObject.put("recordsTotal", page.getTotalRow());
    	jsonObject.put("recordsFiltered", page.getTotalRow());
    	jsonObject.put("data", page.getList());
    	
    	renderJson(jsonObject);
    }
    
    *//**
     * 获取促销活动产品
     * @param token 用户登录口令
     * @param id promotionProductId
     * @return 成功：{error: 0, draw: 页码, recordsTotal: 总数, recordsFiltered: 过滤后总数, data: [{id,name:产品名称,cashDiscount:优惠金额,storeAmount:库存},...}；失败：{error: >0, errmsg: 错误信息}
     *//*
    @Before(AdminAuthInterceptor.class)
    public void getProduct() {
    	if(!this.validateRequiredString("id")) {
    		return;
    	}
    	int id = getParaToInt("id");
    	
    	Record model = promotionsService.getProductById(id);
    	
    	if(model == null) {
    		returnError(ErrorCode.Exception, "活动产品不存在");
    		return;
    	}
    	
    	jsonObject.put("data", model);
    	
    	renderJson(jsonObject);
    }*/
}