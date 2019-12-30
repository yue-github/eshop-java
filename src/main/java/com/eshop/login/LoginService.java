package com.eshop.login;

import java.util.Date;

import com.eshop.helper.MD5Helper;
import com.eshop.model.Customer;
import com.eshop.model.LoginRecord;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class LoginService {

	private final int TYPE_MEMBER = 1;
	private final int TYPE_ADMIN = 2;
	
	private int loginAmount;
	private double loginMinutes;
	private double loginHours;
	
	public LoginService() {
		Prop prop = PropKit.use("config.txt");
		loginAmount = prop.getInt("login_amount");
		loginMinutes = Double.parseDouble(prop.get("login_minutes"));
		loginHours = Double.parseDouble(prop.get("login_hours"));
	}
	
	/**
	 * 普通会员登录，0成功，1账号密码不正确，-1被禁用，-2未激活，-3被锁定
	 * @param account
	 * @param password
	 * @return
	 */
	public Record login(String account, String password) {
		Record map = new Record();
		
		// 判断账号是否已被锁定
		if (isLock(account, TYPE_MEMBER)) {
			map.set("error", -3);
			map.set("errmsg", "账号已被锁定");
			return map;
		}
		
		String sql = "select * from customer where (mobilePhone = ? or email = ? ) and password = ?";
		Customer customer = Customer.dao.findFirst(sql, account, account, MD5Helper.Encode(password));
	
		// 判断账号密码是否正确
		if (customer == null) {
			handleLoginFail(account, TYPE_MEMBER);
			
			int remainAmount = getRemainAmount(account, TYPE_MEMBER);
			
			if (remainAmount == (loginAmount - 1)) {
				map.set("errmsg", "账号密码错误");
			} else if (remainAmount > 0) {
				map.set("errmsg", "账号密码错误，还有"+remainAmount+"次，账号将被锁定");
			} else {
				map.set("errmsg", "该账号被锁定");
			}
			
			map.set("error", 1);
			return map;
		}
		
		// 判断账号是否被禁用
		if (customer.getDisable() == 1) {
			map.set("error", -1);
			map.set("errmsg", "该账号已被禁用");
			return map;
		}
		
		// 判断账号是否未激活
		if (customer.getDisable() == 2) {
			map.set("error", -2);
			map.set("errmsg", "该账号未激活");
			return map;
		}
		
		// 登录成功清除登录失败记录
		deleteLoginRecord(account, TYPE_MEMBER);
		
		map.set("error", 0);
		map.set("errmsg", "登录成功");
		map.set("customer", customer);
		
		return map;
	}
	
	/**
	 * 普通会员登录，0成功，1账号密码不正确，-1被禁用，-2未激活，-3被锁定
	 * @param account
	 * @return
	 */
	public Record login(String account) {
		Record map = new Record();
		
		// 判断账号是否已被锁定
		if (isLock(account, TYPE_MEMBER)) {
			map.set("error", -3);
			map.set("errmsg", "账号已被锁定");
			return map;
		}
		
		String sql = "select * from customer where mobilePhone = ?";
		Customer customer = Customer.dao.findFirst(sql, account);
	
		// 判断账号密码是否正确
		if (customer == null) {
			handleLoginFail(account, TYPE_MEMBER);
			
			int remainAmount = getRemainAmount(account, TYPE_MEMBER);
			
			if (remainAmount == (loginAmount - 1)) {
				map.set("errmsg", "账号密码错误");
			} else if (remainAmount > 0) {
				map.set("errmsg", "账号密码错误，还有"+remainAmount+"次，账号将被锁定");
			} else {
				map.set("errmsg", "该账号被锁定");
			}
			
			map.set("error", 1);
			return map;
		}
		
		// 判断账号是否被禁用
		if (customer.getDisable() == 1) {
			map.set("error", -1);
			map.set("errmsg", "该账号已被禁用");
			return map;
		}
		
		// 判断账号是否未激活
		if (customer.getDisable() == 2) {
			map.set("error", -2);
			map.set("errmsg", "该账号未激活");
			return map;
		}
		
		// 登录成功清除登录失败记录
		deleteLoginRecord(account, TYPE_MEMBER);
		
		map.set("error", 0);
		map.set("errmsg", "登录成功");
		map.set("customer", customer);
		
		return map;
	}
	
	private int getRemainAmount(String account, int accountType) {
		int remainAmount = loginAmount;
		
		String sql = "select * from login_record where account = ? and account_type = ?";
		LoginRecord model = LoginRecord.dao.findFirst(sql, account, accountType);
		
		if (model != null) {
			int lgAmt = model.getLoginAmount();
			remainAmount = loginAmount - lgAmt;
		}
		
		return remainAmount;
	} 
	
	/**
	 * 账号是否已锁定
	 * @param account
	 * @param accountType
	 * @return
	 */
	private boolean isLock(String account, int accountType) {
		// 没有登录记录
		LoginRecord model = LoginRecord.dao.findFirst("select * from login_record where account = ? and account_type = ?", account, accountType);
		if (model == null) {
			return false;
		}
		
		Date lastTime = model.getUpdatedAt();
		Date currentTime = new Date();
		
		long intervalHourMsc = getIntervalMsc(lastTime, currentTime);
		long loginHourMsc = (long) (loginHours * 60 * 60 * 1000);
		
		int currLoginAmount = model.getLoginAmount();
		
		if (intervalHourMsc <= loginHourMsc && currLoginAmount >= loginAmount) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 处理登录失败
	 * @param account
	 * @param accountType
	 * @return
	 */
	private void handleLoginFail(String account, int accountType) {
		// 没有登录记录
		LoginRecord model = LoginRecord.dao.findFirst("select * from login_record where account = ? and account_type = ?", account, accountType);
		if (model == null) {
			insertLoginRecord(account, accountType);
			return;
		}
		
		Date firstTime = model.getCreatedAt();
		Date lastTime = model.getUpdatedAt();
		Date currentTime = new Date();
		
		long intervalMinuteMsc = getIntervalMsc(firstTime, currentTime);
		long intervalHourMsc = getIntervalMsc(lastTime, currentTime);
		
		long loginMinuteMsc = (long) (loginMinutes * 60 * 1000);
		long loginHourMsc = (long) (loginHours * 60 * 60 * 1000);
		
		int currLoginAmount = model.getLoginAmount();
		
		// 未锁定
		if (intervalHourMsc > loginHourMsc || currLoginAmount < loginAmount) {	 
			// 如果在登录时间范围内容，登录次数加一，并且修改登录时间；否则清空登录记录，然后重新增加一条记录
			if (intervalMinuteMsc <= loginMinuteMsc) {  // 在时间范围内
				int newLoginAmount = currLoginAmount + 1;
				model.setLoginAmount(newLoginAmount);
				model.setUpdatedAt(new Date());
				model.update();
			} else {
				deleteLoginRecord(account, accountType);
				insertLoginRecord(account, accountType);
			}
		}
	}
	
	private long getIntervalMsc(Date beforeDate, Date afterDate) {
		return afterDate.getTime() - beforeDate.getTime();
	}
	
	private void deleteLoginRecord(String account, int accountType) {
		Db.update("delete from login_record where account = ? and account_type = ?", account, accountType);
	}
	
	private void insertLoginRecord(String account, int accountType) {
		LoginRecord model = new LoginRecord();
		model.setAccount(account);
		model.setAccountType(accountType);
		model.setLoginAmount(1);
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		model.save();
	}
	
}
