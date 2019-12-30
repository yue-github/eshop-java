package com.eshop.controller.pc;

import com.alibaba.fastjson.JSON;
import com.eshop.content.ResourceService;
import com.eshop.helper.CacheHelper;
import com.eshop.helper.DateHelper;
import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.eshop.model.Customer;
import com.eshop.model.PayRate;
import com.eshop.model.Promotion;
import com.eshop.model.PromotionPingtuan;
import com.eshop.model.dao.BaseDao;
import com.eshop.promotion.BasePromotion;
import com.eshop.promotion.PinTuan;
import com.jfinal.aop.Before;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 促销活动控制器
 *   @author TangYiFeng
 */
public class PromotionTuangouController extends PcBaseController {

    /**
     * Default constructor
     */
    public PromotionTuangouController() {
    }
    
    /**
     * 创建促销活动
     * @param token 用户登录口令
     * @param promotionType 活动类型
     * @param PromotionTitle 促销活动名称
     * @param promotionDescription 活动描述(选填)
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @param amount 满多少人
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void create() {
        String[] params ={"title","type","scope","desc","startDate","endDate","baseOn","full"};
        if (!validate(params)) {
            return;
        }
        String token = getPara("token");
        Customer customer = (Customer) CacheHelper.get(token);

        String title = getPara("title");
        Integer type = getParaToIntegerDefault("type");
        Integer scope = getParaToIntDefault("scope");
        String desc = getPara("desc");
        Date startDate = getParaToDate("startDate");
        Date endDate = getParaToDate("endDate");
        Integer baseOn = getParaToIntDefault("baseOn");
        Integer full = getParaToIntDefault("full");
        String mainPic = getPara("mainPic");
        Integer shopId = customer.getShopId();

        //添加表数据 promotion
        Promotion promotion = new Promotion();
        promotion.setTitle(title);
        promotion.setDesc(desc);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setType(type);
        promotion.setScope(scope);
        promotion.setBaseOn(baseOn);
        promotion.setShopId(shopId);

        PromotionPingtuan promotionPingtuan = new PromotionPingtuan();
        ServiceCode code = PinTuan.create(promotion,promotionPingtuan,mainPic,full);

        if (code != ServiceCode.Success) {
            setError(ErrorCode.Exception, "创建失败");
        }

        renderJson(jsonObject);
    }

    /**
     * 修改促销活动
     * @param token 用户登录口令
     * @param promotionType 活动类型
     * @param id 促销活动id
     * @param PromotionTitle 促销活动名称
     * @param promotionDescription 活动描述(选填)
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @param amount 满多少人
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void update() {

        String[] params ={"title","type","scope","desc","startDate","endDate","baseOn","full"};
        if (!validate(params)) {
            return;
        }
        String token = getPara("token");
        Customer customer = (Customer) CacheHelper.get(token);
        Integer shopId = customer.getShopId();

        Integer id = getParaToInt("id");
        String title = getPara("title");
        Integer type = getParaToIntegerDefault("type");
        Integer scope = getParaToIntDefault("scope");
        String desc = getPara("desc");
        Date startDate = getParaToDate("startDate");
        Date endDate = getParaToDate("endDate");
        Integer baseOn = getParaToIntDefault("baseOn");
        Integer full = getParaToIntDefault("full");
        String mainPic = getPara("mainPic");

        Promotion promotion = BasePromotion.getPromotion(id);
        promotion.setType(type);
        promotion.setTitle(title);
        promotion.setDesc(desc);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setScope(scope);
        promotion.setBaseOn(baseOn);
        promotion.setShopId(shopId);

        PromotionPingtuan pingtuan = PinTuan.getPromotionPingtuan(id);
        pingtuan.setFull(full);

        ServiceCode code = PinTuan.udate(promotion,pingtuan,mainPic);
        if (code != ServiceCode.Success) {
            setError(ErrorCode.Exception, "修改失败");
        }

        renderJson(jsonObject);
    }
    
    /**
     * 获取促销活动
     * @param token 用户登录口令
     * @param promotionType 活动类型
     * @param pageNumber 页码
     * @param pageSize 每页显示条数
     * @return 成功：{error: 0, totalPage: 总页数, totalRow: 总行数, data:[{id,。。。},...}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void many() {
        String[] params = {"offset", "length"};

        if (!validate(params)) {
            return;
        }
        Integer offset = getParaToInt("offset");
        Integer length = getParaToInt("length");
        String title = getPara("title");
        String startDate = (getPara("startDate") != null) ? DateHelper.formatDateTime(getParaToDate("startDate")) : null;
        String endDate = (getPara("endDate") != null) ? DateHelper.formatDateTime(getParaToDate("endDate")) : null;
        String startCreatedAt = (getPara("startCreatedAt") != null) ? DateHelper.formatDateTime(getParaToDate("startCreatedAt")) : null;
        String endCreatedAt = (getPara("endCreatedAt") != null) ? DateHelper.formatDateTime(getParaToDate("endCreatedAt")) : null;
        Integer scope = (getPara("scope") != null) ? getParaToInt("scope") : null;
        Integer baseOn = (getPara("baseOn") != null) ? getParaToInt("baseOn") : null;

        String token = getPara("token");
        Customer customer = (Customer) CacheHelper.get(token);
        int shopId = customer.getShopId();

        Map<String, String> orderByMap = new HashMap<String, String>();
        orderByMap.put("a.created_at", "desc");

        List<Record> list = PinTuan.findPromotionItems(offset, length, title, null, startDate, endDate,
                                                        startCreatedAt, endCreatedAt, scope, baseOn, shopId,
                                                null, null, orderByMap);

        for(Record item: list) {
            if(null != item.get("mainPic")) {
                item.set("mainPic", ResourceService.getPath(item.getInt("mainPic")));
            }
        }

        Integer total = PinTuan.countPromotionItems(offset, length, title, null, startDate, endDate,
                                                    startCreatedAt, endCreatedAt, scope, baseOn, shopId,
                                                null, null, orderByMap);
        jsonObject.put("data", list);
        jsonObject.put("offset", offset);
        jsonObject.put("length", length);
        jsonObject.put("totalRow", total);
        renderJson(jsonObject);
    }

    /**
     * 查看促销活动
     * @param token 用户登录口令
     * @param promotionType
     * @param id 促销活动id
     * @return 成功：{error: 0 Promotion:{id,.....}}；失败：{error: >0, errmsg: 错误信息}
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void get() {

        String[] params = {"id"};

        if (!validate(params)) {
            return;
        }
        Integer id = getParaToInt("id");
        Promotion model = BasePromotion.getPromotion(id);
        PromotionPingtuan pingtuan =   PinTuan.getPromotionPingtuan(id);
        if(model != null) {

            if(null != model.getMainPic()) {
                model.put("mainPic", ResourceService.getPath(model.getMainPic()));
            }
        }
        jsonObject.put("full",pingtuan.getFull());
        jsonObject.put("data", model);
        renderJson(jsonObject);
    }
    
    /**
     * 删除促销活动
     * @param token
     * @param promotionType
     * @param ids 促销活动ids[1,2,...]
     */
    @Before(CustomerPcAuthInterceptor.class)
    public void delete() {
        String[] params = {"ids"};

        if (!validate(params)) {
            return;
        }

        String idsStr = getPara("ids");
        List<Integer> ids = JSON.parseArray(idsStr, Integer.class);

        ServiceCode code = PinTuan.delete(ids);
        if (code != ServiceCode.Success) {
            setError(ErrorCode.Exception, "删除失败");
        }

        renderJson(jsonObject);
    }
    
}