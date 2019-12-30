package com.eshop.controller.pc;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.PromotionSku;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.promotion.PromotionSkuService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;

public class PromotionSkuController extends PcBaseController {
	
	/**
	 * 创建赠品
	 * @param token
	 * @param productId
	 * @param priceId
	 * @param limitStock
	 * @param promotionId
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void createGift() {
		String[] params = {"productId", "priceId", "limitStock", "promotionId"};
		
		if (!validate(params)) {
			return;
		}
		
		int productId = getParaToInt("productId");
		int priceId = getParaToInt("priceId");
		int limitStock = getParaToInt("limitStock");
		int promotionId = getParaToInt("promotionId");
		BigDecimal promotionPrice = new BigDecimal(0);
		
		PromotionSku model = new PromotionSku();
		model.setProductId(productId);
		model.setPriceId(priceId);
		model.setLimitStock(limitStock);
		model.setPromotionId(promotionId);
		model.setPromotionPrice(promotionPrice);
		
		ServiceCode code = PromotionSkuService.create(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "创建失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 创建秒杀产品
	 * @param token
	 * @param productId
	 * @param priceId
	 * @param limitStock
	 * @param promotionId
	 * @param promtionPrice
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void createMiaoshaProduct() {
		String[] params = {"productId", "priceId", "limitStock", "promotionId", "promtionPrice"};
		
		if (!validate(params)) {
			return;
		}
		
		int productId = getParaToInt("productId");
		int priceId = getParaToInt("priceId");
		int limitStock = getParaToInt("limitStock");
		int promotionId = getParaToInt("promotionId");
		BigDecimal promotionPrice = getParaToDecimal("promtionPrice");
		
		PromotionSku model = new PromotionSku();
		model.setProductId(productId);
		model.setPriceId(priceId);
		model.setLimitStock(limitStock);
		model.setPromotionId(promotionId);
		model.setPromotionPrice(promotionPrice);
		
		ServiceCode code = PromotionSkuService.create(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "创建失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 修改赠品
	 * @param token
	 * @param id
	 * @param productId
	 * @param priceId
	 * @param limitStock
	 * @param promotionId
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void updateGift() {
		String[] params = {"id", "productId", "priceId", "limitStock", "promotionId"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		int productId = getParaToInt("productId");
		int priceId = getParaToInt("priceId");
		int limitStock = getParaToInt("limitStock");
		int promotionId = getParaToInt("promotionId");
		BigDecimal promotionPrice = new BigDecimal(0);
		
		PromotionSku model = PromotionSkuService.get(id);
		model.setProductId(productId);
		model.setPriceId(priceId);
		model.setLimitStock(limitStock);
		model.setPromotionId(promotionId);
		model.setPromotionPrice(promotionPrice);
		
		ServiceCode code = PromotionSkuService.update(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "修改失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 修改秒杀产品
	 * @param token
	 * @param id
	 * @param productId
	 * @param priceId
	 * @param limitStock
	 * @param promotionId
	 * @param promtionPrice
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void updateMiaoshaProduct() {
		String[] params = {"id", "productId", "priceId", "limitStock", "promotionId", "promtionPrice"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		int productId = getParaToInt("productId");
		int priceId = getParaToInt("priceId");
		int limitStock = getParaToInt("limitStock");
		int promotionId = getParaToInt("promotionId");
		BigDecimal promotionPrice = getParaToDecimal("promtionPrice");
		
		PromotionSku model = PromotionSkuService.get(id);
		model.setProductId(productId);
		model.setPriceId(priceId);
		model.setLimitStock(limitStock);
		model.setPromotionId(promotionId);
		model.setPromotionPrice(promotionPrice);
		
		ServiceCode code = PromotionSkuService.update(model);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "修改失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 获取赠品或参与秒杀的产品
	 * @param token
	 * @param id
	 * @return 成功：{error:0, data:{}} 失败：{error:>0, errmsg:错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void get() {
		String[] params = {"id"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		PromotionSku model = PromotionSkuService.get(id);
		
		jsonObject.put("data", model);
		renderJson(jsonObject);
	}
	
	/**
	 * 批量查询赠品或参与秒杀的产品
	 * @param token
	 * @param offset
	 * @param length
	 * @param promotionId
	 * @param productName
	 * @return 成功：{error:0, data:[]} 失败：{error:>0, errmsg:错误信息}
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void many() {
		String[] params = {"offset", "length"};
		
		if (!validate(params)) {
			return;
		}
		
		int offset = getParaToInt("offset");
		int length = getParaToInt("length");
		Integer promotionId = (getPara("promotionId") != null) ? getParaToInt("promotionId") : null;
		String productName = getPara("productName");
		
		List<Record> list = PromotionSkuService.findPromotionSku(offset, length, null, null, null, null, null, promotionId, productName, null, null);
		int total = PromotionSkuService.countPromotionSku(null, null, null, null, null, promotionId, productName, null);
		
		jsonObject.put("data", list);
		jsonObject.put("offset", offset);
		jsonObject.put("length", length);
		jsonObject.put("totalRow", total);
		renderJson(jsonObject);
	}
	
	/**
	 * 删除赠品或参与秒杀的产品
	 * @param id
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void delete() {
		String[] params = {"id"};
		
		if (!validate(params)) {
			return;
		}
		
		int id = getParaToInt("id");
		
		ServiceCode code = PromotionSkuService.delete(id);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
	}
	
	/**
	 * 批量删除赠品或参与秒杀的产品
	 * @param ids
	 */
	@Before(CustomerPcAuthInterceptor.class)
	public void batchDelete() {
		String[] params = {"ids"};
		
		if (!validate(params)) {
			return;
		}
		
		String idsStr = getPara("ids");
		List<Integer> ids = JSON.parseArray(idsStr, Integer.class);
		
		ServiceCode code = PromotionSkuService.delete(ids);
		
		if (code != ServiceCode.Success) {
			setError(ErrorCode.Exception, "删除失败");
		}
		
		renderJson(jsonObject);
	}
	
}
