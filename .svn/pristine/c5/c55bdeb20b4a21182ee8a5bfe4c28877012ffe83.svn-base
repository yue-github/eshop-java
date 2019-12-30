package com.eshop.wallet;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eshop.log.Log;
import com.eshop.model.BankCard;
import com.eshop.model.Wallet;
import com.eshop.model.WithdrawCash;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class WithDraw {

	/**
	 * 查看提现详情
	 * @param id
	 * @return
	 */
	public static Record get(int id) {
		String sql = "select a.* from withdraw_cash as a" +
				" left join customer as b on a.customer_id = b.id" +
				" where a.id = " + id;
		
		return Db.findFirst(sql);
	}
	
    /**
     * 批量查询提现记录
     * @param offset
     * @param count
     * @param customerId
     * @param customerName
     * @param mobilePhone
     * @param email
     * @param accountType
     * @param aplipayAccount
     * @param weixinAccount
     * @param status
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param startCreated
     * @param endCreated
     * @param startArriveTime
     * @param endArriveTime
     * @return
     */
	public static List<Record> findWithDrawItems(int offset, int count, Integer customerId, 
    		String customerName, String mobilePhone, String email, Integer accountType, 
    		String aplipayAccount, String weixinAccount, Integer status, String accountNumber, 
    		String accountName, String bankName, String bankBranch, String startCreated, 
    		String endCreated, String startArriveTime, String endArriveTime, 
    		Map<String, String> orderByMap){
		
		return findWithDrawItems(offset, count, customerId, customerName, mobilePhone, email, accountType, 
				aplipayAccount, weixinAccount, status, accountNumber, accountName, bankName, bankBranch, 
				startCreated, endCreated, startArriveTime, endArriveTime, orderByMap, null, null);
	}
	
	public static List<Record> findWithDrawItems(int offset, int count, Integer customerId, 
			Map<String, String> orderByMap){
		
		return findWithDrawItems(offset, count, customerId, null, null, null, null, null, null, 
				null, null, null, null, null, null, null, null, null, orderByMap, null, null);
	}
	
	public static int countWithDrawItems(Integer customerId) {
		
		return countWithDrawItems(customerId, null, null, null, null, null, null, null, null, 
				null, null, null, null, null, null, null, null, null);
	}
	
    public static List<Record> findWithDrawItems(int offset, int count, Integer customerId, 
    		String customerName, String mobilePhone, String email, Integer accountType, 
    		String aplipayAccount, String weixinAccount, Integer status, String accountNumber, 
    		String accountName, String bankName, String bankBranch, String startCreated, 
    		String endCreated, String startArriveTime, String endArriveTime, 
    		Map<String, String> orderByMap, BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = findWithDrawItemsSql(customerId, customerName, mobilePhone, email, accountType, 
    			aplipayAccount, weixinAccount, status, accountNumber, accountName, bankName, bankBranch, 
    			startCreated, endCreated, startArriveTime, endArriveTime, orderByMap, moneyMoreThan
    			, moneyLessThan);
    	
    	sql = BaseDao.appendLimitSql(sql, offset, count);
    	
    	return Db.find(sql);
    }
    
    /**
     * 批量查询提现记录
     * @param customerId
     * @param customerName
     * @param mobilePhone
     * @param email
     * @param accountType
     * @param aplipayAccount
     * @param weixinAccount
     * @param status
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param startCreated
     * @param endCreated
     * @param startArriveTime
     * @param endArriveTime
     * @param orderByMap
     * @return
     */
    public static List<Record> findWithDrawItems(Integer customerId, String customerName, 
    		String mobilePhone, String email, Integer accountType, String aplipayAccount, 
    		String weixinAccount, Integer status, String accountNumber, String accountName, 
    		String bankName, String bankBranch, String startCreated, String endCreated, 
    		String startArriveTime, String endArriveTime, Map<String, String> orderByMap,
    		BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = findWithDrawItemsSql(customerId, customerName, mobilePhone, email, accountType, 
    			aplipayAccount, weixinAccount, status, accountNumber, accountName, bankName, bankBranch, 
    			startCreated, endCreated, startArriveTime, endArriveTime, orderByMap, moneyMoreThan, moneyLessThan);
    	
    	return Db.find(sql);
    }
    
    /**
     * 批量查询提现记录的总数量
     * @param customerId
     * @param customerName
     * @param mobilePhone
     * @param email
     * @param accountType
     * @param aplipayAccount
     * @param weixinAccount
     * @param status
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param startCreated
     * @param endCreated
     * @param startArriveTime
     * @param endArriveTime
     * @return
     */
    public static int countWithDrawItems(Integer customerId, String customerName, String mobilePhone, 
    		String email, Integer accountType, String aplipayAccount, String weixinAccount, 
    		Integer status, String accountNumber, String accountName, String bankName, 
    		String bankBranch, String startCreated, String endCreated, String startArriveTime, 
    		String endArriveTime, BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = findWithDrawItemsSql(customerId, customerName, mobilePhone, email, accountType, 
    			aplipayAccount, weixinAccount, status, accountNumber, accountName, bankName, bankBranch, 
    			startCreated, endCreated, startArriveTime, endArriveTime, null, moneyMoreThan, moneyLessThan);
    	
    	return Db.find(sql).size();
    }
    
    /**
     * 组装sql语句
     * @param customerId
     * @param customerName
     * @param mobilePhone
     * @param email
     * @param accountType
     * @param aplipayAccount
     * @param weixinAccount
     * @param status
     * @param accountNumber
     * @param accountName
     * @param bankName
     * @param bankBranch
     * @param startCreated
     * @param endCreated
     * @param startArriveTime
     * @param endArriveTime
     * @return
     */
    private static String findWithDrawItemsSql(Integer customerId, String customerName, 
    		String mobilePhone, String email, Integer accountType, String aplipayAccount, 
    		String weixinAccount, Integer status, String accountNumber, String accountName, 
    		String bankName, String bankBranch, String startCreated, String endCreated, 
    		String startArriveTime, String endArriveTime, Map<String, String> orderByMap,
    		BigDecimal moneyMoreThan, BigDecimal moneyLessThan) {
    	
    	String sql = "select a.*, b.name, b.name as customerName, b.mobilePhone, b.email, b.nickname" + 
    			" from withdraw_cash as a" + 
    			" left join customer as b on a.customer_id = b.id" + 
    			" where a.id != 0";
    	
    	if (customerId != null)
    		sql += " and a.customer_id = " + customerId;
    	if (customerName != null && !customerName.equals(""))
    		sql += " and b.name like '%" + customerName + "%'";
    	if (mobilePhone != null && !mobilePhone.equals(""))
    		sql += " and b.mobilePhone like '%" + mobilePhone + "%'";
    	if (email != null && !email.equals(""))
    		sql += " and b.email like '%" + email + "%'";
    	if (accountType != null && !accountType.equals(""))
    		sql += " and a.accountType = " + accountType;
    	if (aplipayAccount != null && !aplipayAccount.equals(""))
    		sql += " b.aplipayAccount like '%" + aplipayAccount + "%'";
    	if (weixinAccount != null && !weixinAccount.equals(""))
    		sql += " and a.weixinAccount like '%" + weixinAccount + "%'";
    	if (status != null && !status.equals(""))
    		sql += " and a.status = " + status;
    	if (accountNumber != null && !accountNumber.equals(""))
    		sql += " b.accountNumber like '%" + accountNumber + "%'";
    	if (accountName != null && !accountName.equals(""))
    		sql += " b.accountNumber like '%" + accountNumber + "%'";
    	if (bankName != null && !bankName.equals(""))
    		sql += " b.bankName like '%" + bankName + "%'";
    	if (bankBranch != null && !bankBranch.equals(""))
    		sql += " b.bankBranch like '%" + bankBranch + "%'";
    	if (startCreated != null && !startCreated.equals(""))
    		sql += " and DATE_FORMAT(a.created_at, '%Y-%m-%d') >= '" + startCreated + "'";
    	if (endCreated != null && !endCreated.equals(""))
    		sql += " and DATE_FORMAT(a.arriveTime, '%Y-%m-%d') >= '" + endCreated + "'";
    	if (startArriveTime != null && !startArriveTime.equals(""))
    		sql += " and DATE_FORMAT(b.arriveTime, '%Y-%m-%d') >= '" + startArriveTime + "'";
    	if (endArriveTime != null && !endArriveTime.equals(""))
    		sql += " and DATE_FORMAT(b.arriveTime, '%Y-%m-%d') <= '" + endArriveTime + "'";
    	if (moneyMoreThan != null && !moneyMoreThan.equals("")){
    		sql += " and money >=" + moneyMoreThan ;
    	}
    	if (moneyLessThan != null && !moneyLessThan.equals("")){
    		sql += " and money <=" + moneyLessThan ;
    	}
    	
    	sql += BaseDao.getOrderSql(orderByMap);
    	
    	return sql;
    }
    
    /**
     * 计算提现金额
     * @param list
     * @return
     */
    public static Record calculateWithDraw(List<Record> list) {
    	BigDecimal totalMoney = new BigDecimal(0);
    	
    	for (Record item : list) {
    		totalMoney = totalMoney.add(item.getBigDecimal("money"));
    	}
    	
    	Record result = new Record();
    	result.set("totalMoney", totalMoney.doubleValue());
    	return result;
    }
    
    /**
     * 申请提现（如果提现到银行卡，需要通过银行卡id把银行卡信息查询出来放到提现记录里）
     * @param model 提现记录
     * @param bankCardId 银行卡id
     * @return 状态码
     */
    public ServiceCode applyCash(final WithdrawCash model) {
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	
    	int customerId = model.getCustomerId();
    	BigDecimal balance = new BigDecimal(Recharge.myBalance(customerId));
    	BigDecimal money = model.getMoney();
    	BigDecimal minCash = new BigDecimal(0.01);
    	
    	//如果余额小于提现金额或者提现金额小于0.01元则拒绝提现
    	if (balance.compareTo(money) < 0 || money.compareTo(minCash) < 0) {
    		return ServiceCode.Failed;
    	}
    	
    	boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					BankCard bankCard = Card.getCard(model.getBankcardId());
			    	if (bankCard != null && model.getAccountType() == 3) {
			    		model.set("accountNumber", bankCard.getAccoutNumber());
			    		model.set("accountName", bankCard.getAccountName());
			    		model.set("bankName", bankCard.getBankName());
			    		model.set("bankBranch", bankCard.getBankBranch());
			    	}
			    	model.set("created_at", new Date());
			    	model.set("updated_at", new Date());
			    	model.set("status", 0);
			    	model.save();
			    	
			    	//减掉相关余额
			    	Wallet wallet = new Wallet();
			    	wallet.setCustomerId(model.getCustomerId());
			    	wallet.setEvent(2);
			    	wallet.setMoney(model.getMoney().multiply(new BigDecimal(-1)));
			    	wallet.setCreatedAt(new Date());
			    	wallet.setUpdatedAt(new Date());
			    	wallet.setIsPaySuccess(0);
			    	wallet.setRelateId(model.getId());
			    	wallet.save();
				} catch (Exception e) {
					Log.error(e.getMessage() + ",申请提现失败");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 处理提现  0提交申请，1通过申请，2拒绝申请，3已转账
     * @param id
     * @param status
     * @param note
     * @return
     */
    public static ServiceCode auditWithDraw(int id, int status, String note) {
    	ServiceCode code;
    	if (status == 1) 
    		code = passWithDraw(id, note);
    	else if (status == 2)
    		code = refuseWithDraw(id, note);
    	else if (status == 3)
    		code = finishWithDraw(id, note);
    	else
    		code = ServiceCode.Failed;
    	return code;
    }
    
    /**
     * 拒绝提现申请
     * @param id 提现申请id
     * @param note 理由
     * @return
     */
    public static ServiceCode refuseWithDraw(final int id, final String note) {
    	final WithdrawCash model = WithdrawCash.dao.findById(id);
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	if (model.getStatus() != 0) {
    		return ServiceCode.Failed;
    	}
    	
    	boolean success = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
			    	model.setStatus(2);
			    	model.setNote(note);
			    	model.setAuditTime(new Date());
			    	model.update();
			    	Db.update("delete from wallet where relate_id = ? and event = ?", id, 2);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",拒绝提现错误");
					return false;
				}
				return true;
			}
		});
    	
    	return success ? ServiceCode.Success : ServiceCode.Failed;
    }
    
    /**
     * 同意提现申请
     * @param id 提现申请id
     * @param note 理由
     * @return 状态码
     */
    public static ServiceCode passWithDraw(int id, String note) {
    	WithdrawCash model = WithdrawCash.dao.findById(id);
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	if (model.getStatus() != 0) {
    		return ServiceCode.Failed;
    	}
    	model.setStatus(1);
    	model.setNote(note);
    	model.setAuditTime(new Date());
    	model.update();
    	return ServiceCode.Success;
    }
    
    /**
     * 确认已转帐
     * @param id
     * @return 状态码
     */
    public static ServiceCode finishWithDraw(int id, String note) {
    	WithdrawCash model = WithdrawCash.dao.findById(id);
    	if (model == null) {
    		return ServiceCode.Failed;
    	}
    	if (model.getStatus() != 1) {
    		return ServiceCode.Failed;
    	}
    	model.setStatus(3);
    	model.setNote(note);
    	model.setArriveTime(new Date());
    	model.update();
    	return ServiceCode.Success;
    }
	
}
