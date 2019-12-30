package com.eshop.coupon;

import java.util.*;

import com.eshop.helper.DateHelper;
import com.eshop.model.Coupon;
import com.eshop.model.CustomerCoupon;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 用户领取的优惠券
 */
public class CustomerCouponService {
	
	public static final int USED = 1;
	public static final int NO_USED = 0;

    /**
     * 批量查询用户优惠券
     * @param offset
     * @param count
     * @param customerId
     * @param couponId
     * @param startCreatedAt
     * @param endCreatedAt
     * @param isUsed
     * @param startUseTime
     * @param endUseTime
     * @param code
     * @param phone
     * @param title
     * @param baseOn
     * @param scope
     * @param type
     * @param shopId
     * @param shopName
     * @param isTimeOut
     * @return
     */
    public static List<Record> findCustomerCouponItems(Integer offset, Integer count, Integer customerId, Integer couponId, String startCreatedAt, 
    		String endCreatedAt, Integer isUsed, String startUseTime, String endUseTime, String code, String phone,
    		String title, Integer baseOn, Integer scope, Integer type, Integer shopId, String shopName, Integer isTimeOut) {
        
    	String sql = findCustomerCouponItemsSql(customerId, couponId, startCreatedAt, endCreatedAt, isUsed, 
    			startUseTime, endUseTime, code, phone, title, baseOn, scope, type, shopId, shopName, isTimeOut);
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	return Db.find(sql);
    }
    
    public static List<Record> findCustomerCouponItems(Integer offset, Integer count, Integer customerId, 
    		Integer isTimeOut) {
    	
    	return findCustomerCouponItems(offset, count, customerId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, isTimeOut);
    }
    
    public static List<Record> findCustomerCouponItems(Integer offset, Integer count, Integer customerId, 
    		Integer isTimeOut, Integer type) {
    	
    	return findCustomerCouponItems(offset, count, customerId, null, null, null, null, null, null, null, null, null, null, null, type, null, null, isTimeOut);
    }
    
    public static int countCustomerCouponItems(Integer customerId, Integer isTimeOut, Integer type) {
    	return countCustomerCouponItems(customerId, null, null, null, null, null, null, null, null, null, null, null, type, null, null, isTimeOut);
    }
    
    public static List<Record> findCustomerCouponItems(Integer offset, Integer count, Integer customerId, Integer couponId,
    		Integer isTimeOut, Integer type, String startCreatedAt, String endCreatedAt, Integer isUsed, String code,
    		String phone, String title, Integer baseOn, Integer scope) {
    	
    	return findCustomerCouponItems(offset, count, customerId, couponId, startCreatedAt, endCreatedAt, isUsed, null, null, code, phone, title, baseOn, scope, type, null, null, isTimeOut);
    }
    
    public static int countCustomerCouponItems(Integer customerId, Integer isTimeOut, Integer type, 
    		String startCreatedAt, String endCreatedAt, Integer isUsed, String code,
    		String phone, String title, Integer baseOn, Integer scope) {
    	
    	return countCustomerCouponItems(customerId, null, startCreatedAt, endCreatedAt, isUsed, null, null, code, phone, title, baseOn, scope, type, null, null, isTimeOut);
    }
    
    public static int countCustomerCouponItems(Integer customerId, Integer isTimeOut) {
    	return countCustomerCouponItems(customerId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, isTimeOut);
    }

    /**
     * 批量查询用户优惠券的总数量
     * @param customerId
     * @param couponId
     * @param startCreatedAt
     * @param endCreatedAt
     * @param isUsed
     * @param startUseTime
     * @param endUseTime
     * @param code
     * @param phone
     * @param title
     * @param baseOn
     * @param scope
     * @param type
     * @param shopId
     * @param shopName
     * @param isTimeOut
     * @return
     */
    public static int countCustomerCouponItems(Integer customerId, Integer couponId, String startCreatedAt, 
    		String endCreatedAt, Integer isUsed, String startUseTime, String endUseTime, String code, String phone,
    		String title, Integer baseOn, Integer scope, Integer type, Integer shopId, String shopName, Integer isTimeOut) {
        
    	String sql = findCustomerCouponItemsSql(customerId, couponId, startCreatedAt, endCreatedAt, isUsed, 
    			startUseTime, endUseTime, code, phone, title, baseOn, scope, type, shopId, shopName, isTimeOut);
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param couponId
     * @param startCreatedAt
     * @param endCreatedAt
     * @param isUsed
     * @param startUseTime
     * @param endUseTime
     * @param code
     * @param phone
     * @param title
     * @param baseOn
     * @param scope
     * @param type
     * @param shopId
     * @param shopName
     * @param isTimeOut
     * @return
     */
    public static String findCustomerCouponItemsSql(Integer customerId, Integer couponId, String startCreatedAt, 
    		String endCreatedAt, Integer isUsed, String startUseTime, String endUseTime, String code, String phone,
    		String title, Integer baseOn, Integer scope, Integer type, Integer shopId, String shopName, Integer isTimeOut) {
    	
    	String today = DateHelper.formatDateTime(new Date());
    	String sql = "select a.*, b.title, b.startDate, b.endDate, b.baseOn, b.scope, b.type, b.full, b.value, b.shopId, b.shopName, b.description" +
    			" from customer_coupon as a left join coupon as b on a.couponId = b.id where a.id != 0";
    	
   		if (customerId != null) {
			sql += " and a.customerId = " + customerId;
		}
    	if (couponId != null) {
			sql += " and a.couponId = " + couponId;
		}
    	if (startCreatedAt != null && !startCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startCreatedAt + "'";
		}
    	if (endCreatedAt != null && !endCreatedAt.equals("")) {
			sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') <= '" + endCreatedAt + "'";
		}
    	if (isUsed != null) {
			sql += " and a.isUsed = " + isUsed;
		}
    	if (startUseTime != null && !startUseTime.equals("")) {
			sql += " and DATE_FORMAT(a.useTime, '%Y-%m-%d') >= '" + startUseTime + "'";
		}
    	if (endUseTime != null && !endUseTime.equals("")) {
			sql += " and DATE_FORMAT(a.useTime, '%Y-%m-%d') <= '" + endUseTime + "'";
		}
    	if (code != null && !code.equals("")) {
			sql += " and a.code like '%" + code + "%'";
		}
    	if (phone != null && !code.equals("")) {
			sql += " and a.phone like '%" + phone + "%'";
		}
    	if (title != null && !title.equals("")) {
			sql += " and b.title like '%" + title + "%'";
		}
    	if (baseOn != null) {
			sql += " and b.baseOn = " + baseOn;
		}
    	if (scope != null) {
			sql += " and b.scope = " + scope;
		}
    	if (type != null) {
			sql += " and b.type = " + type;
		}
    	if (shopId != null) {
			sql += " and b.shopId = " + shopId;
		}
    	if (shopName != null && !shopName.equals("")) {
			sql += " and b.shopName like '%" + shopName + "%'";
		}
    	if (isTimeOut != null && isTimeOut == 0) {
			sql += " and DATE_FORMAT(b.endDate, '%Y-%m-%d') >= '" + today + "'";
		}
    	if (isTimeOut != null && isTimeOut == 1) {
			sql += " and DATE_FORMAT(b.endDate, '%Y-%m-%d') < '" + today + "'";
		}
    	
    	return sql;
    }

    /**
     * 删除用户优惠券
     * 算法：如果该优惠券已被使用，则不允许删除
     * @param id 
     * @return
     */
    public static ServiceCode deleteCustomerCoupon(int id) {
        CustomerCoupon customerCoupon = CustomerCoupon.dao.findById(id);
        
        if (customerCoupon.getIsUsed() == 1) {
			return ServiceCode.Validation;
		}
        
        if (!customerCoupon.delete()) {
			return ServiceCode.Failed;
		}
        
        return ServiceCode.Success;
    }
    
    /**
     * 生成优惠券编码
     * @param totalLength
     * @return
     */
    private static String generateCouponCode(int totalLength, String pre) {
    	Record record = Db.findFirst("select max(id) as maxId from customer_coupon");
    	
    	int maxId = record.getInt("maxId");
    	String maxIdStr = String.valueOf(maxId);
    	
    	int maxIdLength = maxIdStr.length();
    	int remainLength = totalLength - maxIdLength - 1;
    	
    	if (remainLength >= 0) {
    		for (int i = 0; i < remainLength; i++) {
				maxIdStr = "0" + maxIdStr;
			}
    	} else {
    		maxIdStr = maxIdStr.substring(maxIdLength - totalLength - 1, maxIdLength - 1);
    	}
    	
    	maxIdStr = pre + maxIdStr;
    	
    	return maxIdStr;
    }
    
    /**
     * 判断优惠券是否已过期
     * @param customerId
     * @param couponId
     * @return
     */
    public static boolean isExpired(int couponId) {
    	Coupon coupon = Coupon.dao.findById(couponId);
    	Date now = new Date();
        Date endDate = coupon.getEndDate();
        return now.after(endDate);
    }
    
    /**
     * 优惠券是否已被领完
     * @param couponId
     * @return
     */
    public static boolean isUseUp(int couponId) {
    	Coupon coupon = Coupon.dao.findById(couponId);
    	int receivedAmount = CustomerCoupon.dao.find("select * from customer_coupon where couponId = ?", couponId).size();
        int amount = coupon.getAmount();
        return receivedAmount >= amount;
    }
    
    /**
     * 用户是否已领取过该优惠券
     * @param customerId
     * @param couponId
     * @return
     */
    public static boolean isReceived(int customerId, int couponId) {
    	int count = CustomerCoupon.dao.find("select * from customer_coupon where customerId = ? and couponId = ?", customerId, couponId).size();
    	return count > 0;
    }
    
    /**
     * 用户是否已领取过该优惠券
     * @param phone
     * @param couponId
     * @return
     */
    public static boolean isReceived(String phone, int couponId) {
    	int count = CustomerCoupon.dao.find("select * from customer_coupon where phone = ? and couponId = ?", phone, couponId).size();
    	return count > 0;
    }

    /**
     * 点击领取优惠券
     * @param customerId
     * @return -1已过期，-2已被领完，-3已领取，0成功，-1失败
     */
    public static int receiveCoupon(int customerId, int couponId) {
        Coupon coupon = Coupon.dao.findById(couponId);
        
        if (coupon == null) {
			return 1;
		}
        
        if (isExpired(couponId)) {
			return -1;
		}
        
        if (isUseUp(couponId)) {
			return -2;
		}
        
        if (isReceived(customerId, couponId)) {
			return -3;
		}
        
        CustomerCoupon model = new CustomerCoupon();
        model.setCustomerId(customerId);
        model.setCouponId(couponId);
        model.setCreatedAt(new Date());
        model.setUpdatedAt(new Date());
        model.setIsUsed(0);
        model.setCode(generateCouponCode(9, "3"));
        model.save();
        
        return 0;
    }

    /**
     * 手机领取优惠券
     * @param phone 
     * @return -1已过期，-2已被领完，-3已领取，0成功，1失败
     */
    public static int receiveCoupon(String phone, int couponId) {
        Coupon coupon = Coupon.dao.findById(couponId);
        
        if (coupon == null) {
			return 1;
		}
        
        if (isExpired(couponId)) {
			return -1;
		}
        
        if (isUseUp(couponId)) {
			return -2;
		}
        
        if (isReceived(phone, couponId)) {
			return -3;
		}
        
        CustomerCoupon model = new CustomerCoupon();
        model.setCustomerId(0);
        model.setCouponId(couponId);
        model.setCreatedAt(new Date());
        model.setUpdatedAt(new Date());
        model.setIsUsed(0);
        model.setCode(generateCouponCode(9, "3"));
        model.setPhone(phone);
        model.save();
        
        return 0;
    }

}