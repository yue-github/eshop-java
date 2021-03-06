package com.eshop.logistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eshop.helper.MathHelper;
import com.eshop.model.Address;
import com.eshop.model.LogisticsTemplate;
import com.eshop.model.LogisticsTemplateDetail;
import com.eshop.model.Product;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.BaoYou;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class Logistics {
	
	/**
     * 添加模板
     * @param template {shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{provinceItem:[{province_id:省id,city_id:市id,isAllProvince:是否省下面所有}],firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费},...]},...
     *                              ]
     *                 }
     * @return code                
     */
    public static ServiceCode createLogisticsTemplate(String template) {
    	if (template == null || template.equals("")) {
    		return ServiceCode.Failed;
    	}
    	JSONObject tp = JSON.parseObject(template);
    	JSONArray expressTypeArr = tp.getJSONArray("expressType");
    	
    	LogisticsTemplate logisticsTemplate = new LogisticsTemplate();
    	logisticsTemplate.setName(tp.getString("name"));
    	logisticsTemplate.setShopId(tp.getInteger("shopId"));
    	logisticsTemplate.setProvinceId(tp.getInteger("province_id"));
    	logisticsTemplate.setCityId(tp.getInteger("city_id"));
    	logisticsTemplate.setDistrictId(tp.getInteger("district_id"));
    	logisticsTemplate.setIsFree(tp.getInteger("isFree"));
    	logisticsTemplate.setPayType(tp.getInteger("payType"));
    	logisticsTemplate.setCreatedAt(new Date());
    	logisticsTemplate.setUpdatedAt(new Date());
    	if (!logisticsTemplate.save()) {
    		return ServiceCode.Failed;
    	}
    	int templateId = logisticsTemplate.getId();

    	for (int i = 0; i < expressTypeArr.size(); i++) {
    		JSONObject expressTypeItem = expressTypeArr.getJSONObject(i);
    		
    		Integer expressType = expressTypeItem.getInteger("expressType");
//    		if (expressType == null || expressType < 0) {
//    			continue;
//    		}
			if (expressType ==  0) {
    			continue;
    		}
    		LogisticsTemplateDetail logisticsTemplateDetailDefault = new LogisticsTemplateDetail();
    		logisticsTemplateDetailDefault.setTemplateId(templateId);
    		logisticsTemplateDetailDefault.setExpressType(expressTypeItem.getInteger("expressType"));
    		logisticsTemplateDetailDefault.setFirstPay(expressTypeItem.getBigDecimal("firstPay"));
    		logisticsTemplateDetailDefault.setFirstUnit(expressTypeItem.getBigDecimal("firstUnit"));
    		logisticsTemplateDetailDefault.setAddPay(expressTypeItem.getBigDecimal("addPay"));
    		logisticsTemplateDetailDefault.setAddUnit(expressTypeItem.getBigDecimal("addUnit"));
    		logisticsTemplateDetailDefault.setIsAllProvince(0);
    		logisticsTemplateDetailDefault.setProvinceId(0);
    		logisticsTemplateDetailDefault.setCityId(0);
    		logisticsTemplateDetailDefault.setIsDefaultFreight(1);
    		logisticsTemplateDetailDefault.setCreatedAt(new Date());
    		logisticsTemplateDetailDefault.setUpdatedAt(new Date());
    		logisticsTemplateDetailDefault.setTheSameItemNum(0);
    		logisticsTemplateDetailDefault.save();
    		JSONArray details = expressTypeItem.getJSONArray("details");
    		for (int j = 0; j < details.size(); j++) {
    			JSONObject detailItem = details.getJSONObject(j);
				JSONArray provinceItem = detailItem.getJSONArray("provinceItem");
    			BigDecimal firstPay = detailItem.getBigDecimal("firstPay");
    			BigDecimal firstUnit = detailItem.getBigDecimal("firstUnit");
    			BigDecimal addPay = BigDecimal.valueOf(detailItem.getDouble("addPay"));
    			BigDecimal addUnit = detailItem.getBigDecimal("addUnit");
    			
    			for (int k = 0; k < provinceItem.size(); k++) {
    				JSONObject cityItem = provinceItem.getJSONObject(k);
    				
    				LogisticsTemplateDetail logisticsTemplateDetail = new LogisticsTemplateDetail();
	    			logisticsTemplateDetail.setTemplateId(templateId);
	    			logisticsTemplateDetail.setExpressType(expressTypeItem.getInteger("expressType"));
	    			logisticsTemplateDetail.setProvinceId(cityItem.getInteger("province_id"));
	    			logisticsTemplateDetail.setCityId(cityItem.getInteger("city_id"));
	    			logisticsTemplateDetail.setIsAllProvince(cityItem.getInteger("isAllProvince"));
	    			logisticsTemplateDetail.setFirstPay(firstPay);
	    			logisticsTemplateDetail.setFirstUnit(firstUnit);
	    			logisticsTemplateDetail.setAddPay(addPay);
	    			logisticsTemplateDetail.setAddUnit(addUnit);
	    			logisticsTemplateDetail.setIsDefaultFreight(0);
	    			logisticsTemplateDetail.setTheSameItemNum(j+1);
	    			logisticsTemplateDetail.setCreatedAt(new Date());
	    			logisticsTemplateDetail.setUpdatedAt(new Date());
	    			logisticsTemplateDetail.save();
    			}
    		}
    	}
    	
    	return ServiceCode.Success;
    }
	
    /**
     * 修改模板
     * @param template {id:模板id,shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{province_id:省id,city_id:市id,isAllProvince:是否省下面所有市,firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费}]},...
     *                              ]
     *                 }
     * @return code                
     */
    public static ServiceCode updateLogisticsTemplate(String template) {
    	JSONObject tp = JSON.parseObject(template);
    	JSONArray expressTypeArr = tp.getJSONArray("expressType");
    	int templateId = tp.getIntValue("id");
    	
    	Db.update("delete from logistics_template_detail where template_id = ?", templateId);
    	
    	LogisticsTemplate logisticsTemplate = LogisticsTemplate.dao.findById(templateId);
    	logisticsTemplate.setName(tp.getString("name"));
    	logisticsTemplate.setShopId(tp.getInteger("shopId"));
    	logisticsTemplate.setProvinceId(tp.getInteger("province_id"));
    	logisticsTemplate.setCityId(tp.getInteger("city_id"));
    	logisticsTemplate.setDistrictId(tp.getInteger("district_id"));
    	logisticsTemplate.setIsFree(tp.getInteger("isFree"));
    	logisticsTemplate.setPayType(tp.getInteger("payType"));
    	logisticsTemplate.setUpdatedAt(new Date());
    	
    	if (!logisticsTemplate.update()) {
    		return ServiceCode.Failed;
    	}
    	
    	for (int i = 0; i < expressTypeArr.size(); i++) {
    		JSONObject expressTypeItem = expressTypeArr.getJSONObject(i);
    		String expressTypeStr = expressTypeItem.getString("expressType");
    		
    		if (expressTypeStr.equals("") || expressTypeStr.equals("0")) {
    			continue;
    		}
    		
    		LogisticsTemplateDetail logisticsTemplateDetailDefault = new LogisticsTemplateDetail();
    		logisticsTemplateDetailDefault.setTemplateId(templateId);
    		logisticsTemplateDetailDefault.setExpressType(expressTypeItem.getInteger("expressType"));
    		logisticsTemplateDetailDefault.setFirstPay(expressTypeItem.getBigDecimal("firstPay"));
    		logisticsTemplateDetailDefault.setFirstUnit(expressTypeItem.getBigDecimal("firstUnit"));
    		logisticsTemplateDetailDefault.setAddPay(expressTypeItem.getBigDecimal("addPay"));
    		logisticsTemplateDetailDefault.setAddUnit(expressTypeItem.getBigDecimal("addUnit"));
    		logisticsTemplateDetailDefault.setIsAllProvince(0);
    		logisticsTemplateDetailDefault.setProvinceId(0);
    		logisticsTemplateDetailDefault.setCityId(0);
    		logisticsTemplateDetailDefault.setIsDefaultFreight(1);
    		logisticsTemplateDetailDefault.setCreatedAt(new Date());
    		logisticsTemplateDetailDefault.setUpdatedAt(new Date());
    		logisticsTemplateDetailDefault.setTheSameItemNum(0);
    		logisticsTemplateDetailDefault.save();
    		
    		JSONArray details = expressTypeItem.getJSONArray("details");
    		for (int j = 0; j < details.size(); j++) {
    			JSONObject detailItem = details.getJSONObject(j);
    			JSONArray provinceItem = detailItem.getJSONArray("provinceItem");
    			BigDecimal firstPay = detailItem.getBigDecimal("firstPay");
    			BigDecimal firstUnit = detailItem.getBigDecimal("firstUnit");
    			BigDecimal addPay = detailItem.getBigDecimal("addPay");
    			BigDecimal addUnit = detailItem.getBigDecimal("addUnit");
    			
    			for (int k = 0; k < provinceItem.size(); k++) {
    				JSONObject cityItem = provinceItem.getJSONObject(k);
    				
    				LogisticsTemplateDetail logisticsTemplateDetail = new LogisticsTemplateDetail();
	    			logisticsTemplateDetail.setTemplateId(templateId);
	    			logisticsTemplateDetail.setExpressType(expressTypeItem.getInteger("expressType"));
	    			logisticsTemplateDetail.setProvinceId(cityItem.getInteger("province_id"));
	    			logisticsTemplateDetail.setCityId(cityItem.getInteger("city_id"));
	    			logisticsTemplateDetail.setIsAllProvince(cityItem.getInteger("isAllProvince"));
	    			logisticsTemplateDetail.setFirstPay(firstPay);
	    			logisticsTemplateDetail.setFirstUnit(firstUnit);
	    			logisticsTemplateDetail.setAddPay(addPay);
	    			logisticsTemplateDetail.setAddUnit(addUnit);
	    			logisticsTemplateDetail.setIsDefaultFreight(0);
	    			logisticsTemplateDetail.setTheSameItemNum(j+1);
	    			logisticsTemplateDetail.setCreatedAt(new Date());
	    			logisticsTemplateDetail.setUpdatedAt(new Date());
	    			logisticsTemplateDetail.save();
    			}
    		}
    	}
    	
    	return ServiceCode.Success;
    }
    
    /**
     * 删除模板
     * @param id 模板id
     * @return code
     */
    public static ServiceCode deleteLogisticsTemplate(int id) {
    	int count = Product.dao.find("select * from product where logistics_template_id = ?", id).size();
    	if (count > 0) {
    		return ServiceCode.Function;
    	}
    	
    	Db.update("delete from logistics_template_detail where template_id = ?", id);
    	Db.update("delete from logistics_template where id = ?", id);
    	return ServiceCode.Success;
    }
    
    /**
     * 获取某个模板信息
     * @param id 模板id
     * @return template {id:模板id,shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费
     * 											provinceItem:[{province_id:省id,city_id:市id,provinceName:省名称,cityName:市名称,isAllProvince:是否省下面所有市},...]
     * 										  }],...
     *                              }]
     *                  }
     */
    public static Record getLogisticsTemplate(int id) {
    	Record result = Db.findById("logistics_template", id);
    	
    	List<Record> expressTypeArr = Db.find("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ?", id, 1);
    	for (Record expressTypeItem : expressTypeArr) {
    		List<Record> details = new ArrayList<Record>();
    		
    		if(expressTypeItem.getInt("expressType") != null){
    			int expressType = expressTypeItem.getInt("expressType");
    			List<Record> provinceItemList = Db.find("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ? and expressType = ? group by theSameItemNum", id, 0, expressType);
        		for (Record provinceItem : provinceItemList) {
        			List<Record> cityArr = Db.find("select * from logistics_template_detail where template_id = ? and expressType = ? and theSameItemNum = ?", id, expressType, provinceItem.getInt("theSameItemNum"));
        			for (Record cityItem : cityArr) {
        				String provinceName = CustomerAddress.getProvinceName(cityItem.getInt("province_id"));
        	    		String cityName = CustomerAddress.getCityName(cityItem.getInt("city_id"));
        	    		int isAllProvince = cityItem.getInt("isAllProvince");
        	    		if (isAllProvince == 1) {
        	    			cityItem.set("name", provinceName);
        	    		} else {
        	    			cityItem.set("name", cityName);
        	    		}
        	    		cityItem.set("provinceName", provinceName);
        	    		cityItem.set("cityName", cityName);
        			}
        			
        			provinceItem.set("provinceItem", cityArr);
        			details.add(provinceItem);
        		}
        		
        		expressTypeItem.set("details", details);
    		}
    		
    	}
    	
    	result.set("expressType", expressTypeArr);
    	return result;
    }
    
    /**
     * 获取某个店铺下的所有模板 -前台
     * @param shopId 店铺id
     * @return list [{id:模板id,shopId:店铺id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 					expressType:[{expressType:运送方式,firstUnit:默认首重,firstPay:默认首费,addUnit:默认续重,addPay:默认续费,
     * 								  details:[{province_id:省id,city_id:市id,isAllProvince:是否省下面所有市,firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费},...],
     *                              },...]
     *              },...]
     */
    public static List<Record> findLogisticsTemplateByShopId(int shopId) {
    	List<Record> list = Db.find("select * from logistics_template where shop_id = ?", shopId);
    	
    	for (Record item : list) {
    		List<Record> expressTypeList = Db.find("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ?", item.getInt("id"), 1);
    		
    		for (Record expressType : expressTypeList) {
    			List<Record> details = Db.find("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ? and expressType = ?", item.getInt("id"), 0, expressType.getInt("expressType"));
    			expressType.set("details", details);
    		}
    		
    		item.set("expressType", expressTypeList);
    	}
    	
    	return list;
    }
    
    /**
     * 批量查询快递模板 -后台
     * @param offset
     * @param count
     * @param shopId
     * @param isFree
     * @param payType
     * @param name
     * @return [{id:模板id,name:模板名称,province_id:宝贝省id,city_id:宝贝市id,district_id:宝贝区id,isFree:是否包邮,payType:计价方式,
     * 				  details:[{expressType:运送方式,firstUnit:首重,firstPay:首费,addUnit:续重,addPay:续费,isDefaultFreight:是否全国默认运费,provinceItem:[{provinceName:省名称,cityName:市名称,province_id:省id,city_id:市id,isAllProvince:是否省下面所有市},...]},...]
     *              }]
     */
    public static List<Record> findLogisticsTemplateItems(int offset, int count, Integer shopId, Integer isFree, Integer payType, String name) {
    	String sql = findLogisticsTemplateItemsSql(shopId, isFree, payType, name);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	List<Record> list = Db.find(sql);
    	
    	for (Record item : list) {
    		int id = item.getInt("id");
    		List<Record> details = new ArrayList<Record>();
    		List<Record> expressTypeArr = Db.find("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ?", id, 1);
        	
    		for (Record expressTypeItem : expressTypeArr) {
    			if(expressTypeItem.getInt("expressType") != null){
    				int expressType = expressTypeItem.getInt("expressType");
    				List<Record> provinceItemList = Db.find("select * from logistics_template_detail where template_id = ? and expressType = ? group by theSameItemNum", id, expressType);
            		for (Record provinceItem : provinceItemList) {
            			List<Record> cityArr = Db.find("select * from logistics_template_detail where template_id = ? and expressType = ? and theSameItemNum = ?", id, expressType, provinceItem.getInt("theSameItemNum"));
            			for (Record cityItem : cityArr) {
            				String provinceName = CustomerAddress.getProvinceName(cityItem.getInt("province_id"));
            	    		String cityName = CustomerAddress.getCityName(cityItem.getInt("city_id"));
            	    		cityItem.set("provinceName", provinceName);
            	    		cityItem.set("cityName", cityName);
            			}
            			
            			provinceItem.set("provinceItem", cityArr);
            			details.add(provinceItem);
            		}
    			}
        	}
    		
    		item.set("details", details);
    	}
    	
    	return list;
    }
    
    /**
     * 批量查询快递模板的总数量
     * @return
     */
    public static int getLogisticsTemplateItemsCount(Integer shopId, Integer isFree, Integer payType, String name) {
    	String sql = findLogisticsTemplateItemsSql(shopId, isFree, payType, name);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql
     * @param shopId
     * @param isFree
     * @param payType
     * @param name
     * @return
     */
    public static String findLogisticsTemplateItemsSql(Integer shopId, Integer isFree, Integer payType, String name) {
    	String sql = "select * from logistics_template where id != 0";
    	if (shopId != null) {
			sql += " and shop_id = " + shopId;
		}
    	if (isFree != null) {
			sql += " and isFree = " + isFree;
		}
    	if (payType != null) {
			sql += " and payType = " + payType;
		}
    	if (name != null && !name.equals("")) {
			sql += " and name like '%" + name + "%'";
		}
    	return sql;
    }
    
    /**
     * 计算某个订单的运费
     * @param details [{product_id:产品id, amount:数量, template_id:运费模板id},...]
     * @param receiveProvinceId 收货省id
     * @param receiveCityId 收货市id
     * @return double
     */
    public double getOrderFreight(List<Record> details, int receiveProvinceId, int receiveCityId) {
    	double result = 0;
    	
//    	if (receiveProvinceId == 0 || receiveCityId == 0) {
//    		return result;
//    	}
    	
    	//根据运费模板对产品进行分组，返回的数据格式：[{template_id:运费模板id, productList:[{id:产品id,unitAmount:单位数量},...]},...]
    	List<Record> templateList = getProductListGroupByTemplateId(details);
    	
    	for (Record template : templateList) {
    		//计算每个运费模板的运费
    		double freight = getMinTemplateFreight(template, receiveProvinceId, receiveCityId);
    		result += freight;
    	}
    	
    	result = MathHelper.cutDecimal(result);
    	return result;
    }
    
    /**
     * 计算某个订单的运费以及每个模板的运费
     * @param details [{product_id:产品id, amount:数量, template_id:运费模板id},...]
     * @param receiveProvinceId 收货省id
     * @param receiveCityId 收货市id
     * @return [{freight:运费, template_id:运费模板id},...]
     */
    public List<Record> getTemplatesFreight(List<Record> details, int receiveProvinceId, int receiveCityId) {
    	//根据运费模板对产品进行分组，返回的数据格式：[{template_id:运费模板id, productList:[{id:产品id,unitAmount:单位数量},...]},...]
    	List<Record> templateList = getProductListGroupByTemplateId(details);
    	List<Record> templatesFreight = new ArrayList<Record>();
    	
    	for (Record template : templateList) {
    		//计算每个运费模板的运费
    		double freight = getMinTemplateFreight(template, receiveProvinceId, receiveCityId);
    		Record item = new Record();
    		item.set("template_id", template.getInt("template_id"));
    		item.set("freight", MathHelper.cutDecimal(freight));
    		templatesFreight.add(item);
    	}
    	
    	return templatesFreight;
    }
    
    /**
     * 获取所有运费模板的运费总和
     * @param templatesFreight
     * @return
     */
    public double getTotalTemplateFreight(List<Record> templatesFreight) {
    	double deliveryPrice = 0;
    	for (Record item : templatesFreight) {
    		deliveryPrice += item.getDouble("freight");
    	}
    	return MathHelper.cutDecimal(deliveryPrice);
    }
    
    /**
     * 计算大订单总运费
     * @param deliveryOrders
     * @param other
     * @return
     */
    public double calculateDeliveryPrice(List<Record> deliveryOrders, Record other) {
    	List<Record> list = new ArrayList<Record>();
    	
    	for (Record item : deliveryOrders) {
    		List<Record> products = item.get("products");
    		list.addAll(products);
    	}
    	
    	boolean isAllFree = BaoYou.isMeetAllFree(list);
    	double deliveryPrice = 0;
    	
    	if (isAllFree)	{
    		return deliveryPrice;
    	}
    	
    	int addressId = other.getInt("address_id");
    	Address address = Address.dao.findById(addressId);
    	int provinceId = address != null ? address.getProvinceId() : 0;
    	int cityId = address != null ? address.getCityId() : 0;
    	
//    	if (provinceId == 0 || cityId == 0) {
//    		return deliveryPrice;
//    	}
    	
    	for (Record item : deliveryOrders) {
    		List<Record> promotions = BaoYou.meetShopFree(list);
			for (Record promotion : promotions) {
				if (promotion.getInt("shop_id") == item.getInt("shop_id")) {
					deliveryPrice += 0;
					break;
				}
			}
			
			List<Record> templatesFreight = getTemplatesFreight(list, provinceId, cityId);
			deliveryPrice += getTotalTemplateFreight(templatesFreight);
    	}
    	
    	return deliveryPrice;
    }
    
    /**
     * 根据运费模板id对产品列表分组
     * @param details [{prouduct_id:产品id, amount:数量, template_id:运费模板id},...]
     * @return [{template_id:运费模板id, productList:[{id:产品id,unitAmount:单位数量},...]},...]
     */
    private List<Record> getProductListGroupByTemplateId(List<Record> details) {
    	List<Record> result = new ArrayList<Record>();
    	
    	List<Record> templateIds = this.getListGroupByTemplateId(details);
    	
    	for (Record templateIdItem : templateIds) {
    		int templateId = templateIdItem.getInt("template_id");
    		Record item = new Record();
    		item.set("template_id", templateId);
    		
    		List<Record> productList = new ArrayList<Record>();
    		for (Record detailsItem : details) {
    			int newTemplateId = detailsItem.get("template_id");
    			int productId = detailsItem.get("product_id");
    			int amount = detailsItem.get("amount");
    			
    			Record productItem = new Record();
    			if (templateId == newTemplateId) {
    				LogisticsTemplate logisticsTemplate = LogisticsTemplate.dao.findById(templateId);
    				if (logisticsTemplate == null) {
    					continue;
    				}
    				
    				Product product = Product.dao.findById(productId);
    				
    				double unit = this.getAmountByProduct(product, logisticsTemplate.getPayType());
    				double unitAmount = amount * unit;
    				
    				productItem.set("id", productId);
    				productItem.set("unitAmount", unitAmount);
    				productList.add(productItem);
    			}
    		}
    		
    		item.set("productList", productList);
    		result.add(item);
    	}
    	
    	return result;
    }
    
    /**
     * 计算某个模板的最小运费
     * @param template {template_id:运费模板id, productList:[{id:产品id,unitAmount:单位数量},...]}
     * @return double
     */
    private double getMinTemplateFreight(Record template, int receiveProvinceId, int receiveCityId) {
    	int templateId = template.getInt("template_id");
    	LogisticsTemplate logisticsTemplate = LogisticsTemplate.dao.findById(templateId);
    	
    	if (logisticsTemplate == null) {
    		return 0;
    	}
    	
    	if (logisticsTemplate.getIsFree() == 1) {
    		return 0;
    	}
    	
    	//计算每个运送方式的运费
    	//该模板有多少运送方式
    	List<Record> expressTypeList = Db.find("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ?", templateId, 1);
    	for (Record expressTypeItem : expressTypeList) {
    		int expressType = expressTypeItem.getInt("expressType");
    		List<Record> productList = template.get("productList");
    		
    		//最大首费
    		double firstPay = this.getFirstFreightByAddress(receiveProvinceId, receiveCityId, templateId, expressType);
    		double totalUnitAmount = 0;
    		for (Record productItem : productList) {
    			totalUnitAmount += productItem.getDouble("unitAmount");
    		}
    		//计算续费
    		double addFreight = this.getAddFreightByAddress(totalUnitAmount, receiveProvinceId, receiveCityId, templateId, expressType);
    		firstPay += addFreight;
    		
    		expressTypeItem.set("freight", firstPay);
    	}
    	
    	Record firstExpressType = expressTypeList.get(0);
    	if (firstExpressType == null) {
    		return 0;
    	}
    	
    	//找出多个运送方式中的最小运费
    	double temp = firstExpressType.getDouble("freight");
    	for (Record expressTypeItem : expressTypeList) {
    		double newFreight = expressTypeItem.getDouble("freight");
    		if (newFreight < temp) {
    			temp = newFreight;
    		}
    	}
    	
    	return temp;
    }
    
    /**
     * 根据运费模板id对运费模板分组
     * @param details [{product_id:产品id, amount:数量, template_id:运费模板id},...]
     * @return [{template_id:运费模板id},...]
     */
    private List<Record> getListGroupByTemplateId(List<Record> details) {
    	List<Record> result = new ArrayList<Record>();
    	
    	for (Record item : details) {
    		System.out.println(item);
    		int newTemplateId = item.getInt("template_id");
    		if (!contain(result, "template_id", newTemplateId)) {
    			Record record = new Record();
    			record.set("template_id", newTemplateId);
    			result.add(record);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * 根据运费计价方式获取数量
     * @param product
     * @param payType
     * @return double
     */
    private double getAmountByProduct(Product product, int payType) {
    	double amount = 0;
    	switch (payType) {
	    	case 1: //计件
	    		amount = 1;
	    		break;
	    	case 2: //重量
	    		amount = product.getWeight() != null ? product.getWeight().doubleValue() : 0;
	    		break;
	    	case 3: //体积
	    		amount = product.getVolume() != null ? product.getVolume().doubleValue() : 0;
	    		break;
    	}
    	
    	return amount;
    }
    
    /**
     * 获取首费
     * @return double
     */
    private double getFirstFreightByAddress(int receiveProvinceId, int receiveCityId, int templateId, int expressType) {
    	double firstFreight = 0;
    	
    	LogisticsTemplateDetail lgtd = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and expressType= ? and province_id = ? and city_id = ?", templateId, expressType, receiveProvinceId, receiveCityId);
        if (lgtd != null) {
        	firstFreight = lgtd.getFirstPay().doubleValue();
        	return firstFreight;
        }
        
        lgtd = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and expressType= ? and province_id = ? and isAllProvince = ?", templateId, expressType, receiveProvinceId, 1);
        if (lgtd != null) {
        	firstFreight = lgtd.getFirstPay().doubleValue();
        	return firstFreight;
        }
        
        lgtd = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and expressType= ? and isDefaultFreight = ?", templateId, expressType, 1);
        if (lgtd != null) {
        	firstFreight = lgtd.getFirstPay().doubleValue();
        	return firstFreight;
        }
        
        return firstFreight;
    }
    
    /**
     * 根据地址获取续重运费
     * @param receiveProvinceId 省id
     * @param receiveCityId 市id
     * @return double
     */
    private double getAddFreightByAddress(double amount, int receiveProvinceId, int receiveCityId, int templateId, int expressType) {
    	double addFreight = 0;
    	
    	LogisticsTemplateDetail lgtd = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and expressType= ? and province_id = ? and city_id = ?", templateId, expressType, receiveProvinceId, receiveCityId);
        if (lgtd != null) {
        	addFreight = this.getAddFreightByAmount(amount, lgtd);
        	return addFreight;
        }
        
        lgtd = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and expressType= ? and province_id = ? and isAllProvince = ?", templateId, expressType, receiveProvinceId, 1);
        if (lgtd != null) {
        	addFreight = this.getAddFreightByAmount(amount, lgtd);
        	return addFreight;
        }
        
        lgtd = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and expressType= ? and isDefaultFreight = ?", templateId, expressType, 1);
        if (lgtd != null) {
        	addFreight = this.getAddFreightByAmount(amount, lgtd);
        	return addFreight;
        }
        
        return addFreight;
    }
    
    /**
     * 根据数量计算续重运费
     * @param amount
     * @param logisticsTemplateDetail
     * @return double
     */
    private double getAddFreightByAmount(double amount, LogisticsTemplateDetail logisticsTemplateDetail) {
    	if (logisticsTemplateDetail == null) {
    		return 0;
    	}
    	
    	double firstUnit = logisticsTemplateDetail.getFirstUnit().doubleValue();
    	double addPay = logisticsTemplateDetail.getAddPay().doubleValue();
    	double addUnit = logisticsTemplateDetail.getAddUnit().doubleValue();
    	double overUnit = amount - firstUnit;
    	double overPay = 0;
    	
    	if (overUnit > 0) {
    		overPay = overUnit / addUnit * addPay;
    	}
    	
    	return MathHelper.cutDecimal(overPay);
    }
    
    private boolean contain(List<Record> list, String key, int value) {
    	for (Record item : list) {
    		if (item.getInt(key) == value) {
    			return true;
    		}
    	}
    	return false;
    }
    
    
    /**
     * 计算某个产品的运费
     * @param productId
     * @param receiveProvinceId 收货省id
     * @param receiveCityId 收货市id
     * @return [{template_id:模板id, expressType:运送方式(1快递，2EMS，3平邮), freight:运费},...]
     */
    public List<Record> getFreightByProduct(int productId, int receiveProvinceId, int receiveCityId) {
    	Product product = Product.dao.findById(productId);
    	if (product == null) {
    		return null;
    	}
    	
    	LogisticsTemplate logisticsTemplate = LogisticsTemplate.dao.findById(product.getLogisticsTemplateId());
    	if (logisticsTemplate == null) {
    		return null;
    	}
    	
    	// 获取数量
    	double amount = this.getAmountByProduct(product, logisticsTemplate.getPayType());
    	int logistics_template_id = logisticsTemplate.getId();
    	List<Record> list = Db.find("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ?", logistics_template_id, 1);
    	
    	for (Record item : list) {
    		double freight = 0;
    		
    		LogisticsTemplateDetail lgtdCity = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ? and city_id = ? and expressType = ?", logistics_template_id, 0, receiveCityId, item.getInt("expressType"));
    		if (lgtdCity != null) {
    			freight = getFreightByAmount(amount, lgtdCity);
    			item.set("freight", freight);
    			continue;
    		}
    		
    		LogisticsTemplateDetail lgtdProvince = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ? and province_id = ? and isAllProvince = ? and expressType = ?", logistics_template_id, 0, receiveProvinceId, 1, item.getInt("expressType"));
    		if (lgtdProvince != null) {
    			freight = getFreightByAmount(amount, lgtdProvince);
    			item.set("freight", freight);
    			continue;
    		}
    		
    		LogisticsTemplateDetail lgtdDefault = LogisticsTemplateDetail.dao.findFirst("select * from logistics_template_detail where template_id = ? and isDefaultFreight = ? and expressType = ?", logistics_template_id, 1, item.getInt("expressType"));
    		if (lgtdDefault != null) {
    			freight = getFreightByAmount(amount, lgtdDefault);
    			item.set("freight", freight);
    			continue;
    		}
    	}
    	
    	return list;
    }
    
    /**
     * 根据数量计算运费
     * @param amount
     * @param logisticsTemplateDetail
     * @return double
     */
    private double getFreightByAmount(double amount, LogisticsTemplateDetail logisticsTemplateDetail) {
    	if (logisticsTemplateDetail == null) {
    		return 0;
    	}
    	
    	double firstPay = logisticsTemplateDetail.getFirstPay().doubleValue();
    	double firstUnit = logisticsTemplateDetail.getFirstUnit().doubleValue();
    	double addPay = logisticsTemplateDetail.getAddPay().doubleValue();
    	double addUnit = logisticsTemplateDetail.getAddUnit().doubleValue();
    	double overUnit = amount - firstUnit;
    	double overPay = 0;
    	
    	if (overUnit > 0) {
    		overPay = overUnit / addUnit * addPay;
    	}
    	
    	double result = firstPay + overPay;
    	return MathHelper.cutDecimal(result);
    }
    
}
