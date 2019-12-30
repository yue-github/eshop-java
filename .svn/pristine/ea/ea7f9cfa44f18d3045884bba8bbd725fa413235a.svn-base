package com.eshop.controller.webapp;

import java.util.*;

import com.eshop.content.ContentService;
import com.eshop.content.RecommendPositionService;
import com.jfinal.plugin.activerecord.Record;

/**
 *   周边游控制器
 *   @author TangYiFeng
 */
public class SurroundingController extends WebappBaseController {

    /**
     * 构造方法
     */
    public SurroundingController() {
    }

    /**
	 * 获取周边游页面位置1推荐内容
	 * @return 成功:{error:0, data:[{positionName:位置名称, productList:[{id:id,name:产品名称, suggestedRetailUnitPrice:价格, recommendPic:推荐图片, summary:摘要},...]},...]} 失败：{error:>0, errmsg:错误信息}
	 */
	public void getPositionOne() {
		int type = RecommendPositionService.TRAVEL_TOP;
    	List<Record> list = ContentService.getRecommendProductsByPositionType(type);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
	}
	
	/**
	 * 获取周边游页面位置2推荐内容
	 * @return 成功： {error:0, data:[{positionName:位置名称, productList:[{id:id, name: 产品名称, suggestedRetailUnitPrice: 价格, recommendPic: 图片, summary: 摘要}, ...]},...]} 失败: {error:>0, errmsg:错误信息}
	 */
	public void getPositionTwo() {
		int type = RecommendPositionService.TRAVEL_CENTER;
    	List<Record> list = ContentService.getRecommendProductsByPositionType(type);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
	}
	
	/**
	 * 获取周边游页面位置3推荐内容
	 * @return 成功： {error:0, data:[{positionName:位置名称, productList:[{id:id, name: 产品名称, suggestedRetailUnitPrice: 价格, recommendPic: 图片, summary: 摘要}, ...]},...]} 失败: {error:>0, errmsg:错误信息}
	 */
	public void getPositionThree() {
		int type = RecommendPositionService.TRAVEL_BOTTOM;
		List<Record> list = ContentService.getRecommendProductsByPositionType(type);
    	
    	jsonObject.put("data", list);
    	renderJson(jsonObject);
	}

}