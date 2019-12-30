package com.eshop.controller.pc;

import java.util.*;

import com.eshop.content.ContentService;
import com.jfinal.plugin.activerecord.Record;

/**
 * 
 * @author TangYiFeng
 */
public class TravelController extends PcBaseController {
	
	public TravelController() {
	}
	
	/**
	 * 获取周边游页面位置1推荐内容
	 * @return 成功:{error:0, data:[{positionName:位置名称, productList:[{id:id,name:产品名称, suggestedRetailUnitPrice:价格, recommendPic:推荐图片, summary:摘要},...]},...]} 失败：{error:>0, errmsg:错误信息}
	 */
	public void getPositionOne() {
		int type = 7;
    	List<Record> list = ContentService.getRecommendProductsByPositionType(type);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
	}
	
	/**
	 * 获取周边游页面位置2推荐内容
	 * @return 成功： {error:0, data:[{positionName:位置名称, productList:[{id:id, name: 产品名称, suggestedRetailUnitPrice: 价格, recommendPic: 图片, summary: 摘要}, ...]},...]} 失败: {error:>0, errmsg:错误信息}
	 */
	public void getPositionTwo() {
		int type = 8;
		List<Record> list = ContentService.getRecommendProductsByPositionType(type);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
	}
	
	/**
	 * 获取周边游页面位置3推荐内容
	 * @return 成功： {error:0, data:[{positionName:位置名称, productList:[{id:id, name: 产品名称, suggestedRetailUnitPrice: 价格, recommendPic: 图片, summary: 摘要}, ...]},...]} 失败: {error:>0, errmsg:错误信息}
	 */
	public void getPositionThree() {
		int type = 9;
		List<Record> list = ContentService.getRecommendProductsByPositionType(type);
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
	}
	
}