package com.eshop.controller.admin;

import java.util.List;

import com.eshop.controller.pc.PcBaseController;
import com.eshop.volume.ProductSalesVolume;
import com.jfinal.plugin.activerecord.Record;

/**
 * 销量统计控制器
 */
public class SalesVolumeController extends PcBaseController {
	
	 /**
     * 商品销量
     * @param offset
     * @param length
     * @param name 产品名称
     * @param startDatetime 订单开始时间
     * @param endDatetime 订单结束时间
     * @return {data:[{pid:商品id, pname:商品名称, salesVolume:销量}]}
     */
    public void productSalesVolume() {
    	String[] params = {"offset", "length"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String name = getPara("name");
    	String startDate = getPara("startDatetime");
    	String endDate = getPara("endDatetime");
    	
    	List<Record> data = ProductSalesVolume.productSalesNum(offset, length, name, startDate, endDate);
    	List<Record> list = ProductSalesVolume.productSalesNum(name, startDate, endDate);
    	Record statistics = ProductSalesVolume.statisticProductSale(list);
    	int total = ProductSalesVolume.countProductSalesNum(name, startDate, endDate);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalSalesVolume", statistics.getInt("totalSalesVolume"));
    	jsonObject.put("totalPrice", statistics.getBigDecimal("totalSalesPrice"));
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }
    
    /**
     * 购买商品种类统计
     * @param offset
     * @param length
     * @param cname 分类名称
     * @param startDatetime 开始时间
     * @param endDatetime 结束时间
     * @return {data:[{cid:分类id, cname:分类名称, cvolume:客户购买商品的种类统计数}]}
     */
    public void categoryVolume() {
    	String[] params = {"offset", "length"};
    	if (!validate(params)) {
    		return;
    	}
    	
    	int offset = getParaToInt("offset");
    	int length = getParaToInt("length");
    	String name = getPara("cname");
    	String startDatetime = getPara("startDatetime");
    	String endDatetime = getPara("endDatetime");
    	
    	List<Record> data = ProductSalesVolume.cetegoryByCustomerBuy(offset, length, name, startDatetime, endDatetime);
    	List<Record> list = ProductSalesVolume.cetegoryByCustomerBuy(name, startDatetime, endDatetime);
    	Record statistics = ProductSalesVolume.statisticsCategoryByCustomerBuy(list);
    	int total = ProductSalesVolume.countCetegoryByCustomerBuy(name, startDatetime, endDatetime);
    	
    	jsonObject.put("offset", offset);
    	jsonObject.put("length", length);
    	jsonObject.put("totalSalesVolume", statistics.getInt("totalSalesVolume"));
    	jsonObject.put("totalPrice", statistics.getBigDecimal("totalSalesPrice"));
    	jsonObject.put("totalRow", total);
    	jsonObject.put("data", data);
    	renderJson(jsonObject);
    }
}
