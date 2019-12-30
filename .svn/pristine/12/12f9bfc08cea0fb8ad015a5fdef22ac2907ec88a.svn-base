package com.eshop.finance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.eshop.helper.DateHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.helper.MathHelper;
import com.eshop.model.AuditRecord;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class AutoBalanceService {

	/**
	 * 批量查询对账记录
	 * @param offset
	 * @param count
	 * @param tradeCode
	 * @param tradeNo
	 * @param tradeType
	 * @param startTime
	 * @param endTime
	 * @param payType
	 * @param differ
	 * @param status
	 * @return
	 */
	public static List<Record> findAuditRecordItems(int offset, int count, String tradeCode, String tradeNo, 
			Integer tradeType, String startTime, String endTime, Integer payType, Integer differ, 
			Integer status, Map<String, String> orderByMap) {
		
		String sql = findAuditRecordItemsSql(tradeCode, tradeNo, tradeType, startTime, endTime, payType, 
				differ, status, orderByMap);
		
		sql = BaseDao.appendLimitSql(sql, offset, count);
		
		return Db.find(sql);
	}
	
	public static List<Record> findAuditRecordItems(String tradeCode, String tradeNo, Integer tradeType, 
			String startTime, String endTime, Integer payType, Integer differ, Integer status, 
			Map<String, String> orderByMap) {
		
		String sql = findAuditRecordItemsSql(tradeCode, tradeNo, tradeType, startTime, endTime, payType, 
				differ, status, orderByMap);
		
		return Db.find(sql);
	}
	
	/**
	 * 批量查询对账记录的总数量
	 * @param tradeCode
	 * @param tradeNo
	 * @param tradeType
	 * @param startTime
	 * @param endTime
	 * @param payType
	 * @param differ
	 * @param status
	 * @return
	 */
	public static int countAuditRecordItems(String tradeCode, String tradeNo, Integer tradeType, 
			String startTime, String endTime, Integer payType, Integer differ, Integer status) {
		
		String sql = findAuditRecordItemsSql(tradeCode, tradeNo, tradeType, startTime, endTime, payType, 
				differ, status, null);
		
		return Db.find(sql).size();
	}
	
	/**
	 * 组装sql语句
	 * @param tradeCode
	 * @param tradeNo
	 * @param tradeType
	 * @param startTime
	 * @param endTime
	 * @param payType
	 * @param differ
	 * @param status
	 * @return
	 */
	public static String findAuditRecordItemsSql(String tradeCode, String tradeNo, Integer tradeType, 
			String startTime, String endTime, Integer payType, Integer differ, Integer status,
			Map<String, String> orderByMap) {
		
		String sql = "select * from audit_record where id != 0";
		
		if (tradeCode != null && !tradeCode.equals("")) {
			sql += " and tradeCode like '%" + tradeCode + "%'";
		}
		if (tradeNo != null && !tradeNo.equals("")) {
			sql += " and tradeNo like '%" + tradeNo + "%'";
		}
		if (tradeType != null) {
			sql += " and tradeType = " + tradeType;
		}
		if (startTime != null && !startTime.equals("")) {
			sql += " and DATE_FORMAT(tradeTime, '%Y-%m-%d') >= '" + startTime + "'";
		}
		if (endTime != null && !endTime.equals("")) {
			sql += " and DATE_FORMAT(tradeTime, '%Y-%m-%d') <= '" + endTime + "'";
		}
		if (payType != null) {
			sql += " and payType = " + payType;
		}
		if (differ != null) {
			switch (differ) {
			case 1:
				sql += " and differ = 0";
				break;
			case 2:
				sql += " and differ > 0";
				break;
			case 3:
				sql += " and differ < 0";
				break;
			}
		}
		if (status != null) {
			sql += " and status = " + status;
		}
		
		sql += BaseDao.getOrderSql(orderByMap);
		
		return sql;
	}
	
	/**
	 * 计算财务对账总金额
	 * @param tradeCode
	 * @param tradeNo
	 * @param tradeType
	 * @param startTime
	 * @param endTime
	 * @param payType
	 * @param differ
	 * @param status
	 * @return
	 */
    public static Record calculateAuditRecord(List<Record> list) {
    	BigDecimal totalPayable = new BigDecimal(0);
    	BigDecimal totalThirdPayable = new BigDecimal(0);
    	BigDecimal totalDiffer = new BigDecimal(0);
    	
    	for (Record item : list) {
    		if (item.getInt("tradeType") == 1) {
    			totalPayable = totalPayable.add(item.getBigDecimal("payable"));
        		totalThirdPayable = totalThirdPayable.add(item.getBigDecimal("thirdPayable"));
    		} else {
    			totalPayable = totalPayable.subtract(item.getBigDecimal("payable"));
        		totalThirdPayable = totalThirdPayable.subtract(item.getBigDecimal("thirdPayable"));
    		}
    		
    		totalDiffer = totalDiffer.add(item.getBigDecimal("differ"));
    	}
    	
    	Record result = new Record();
    	result.set("totalPayable", MathHelper.cutDecimal(totalPayable.doubleValue()));
    	result.set("totalThirdPayable", MathHelper.cutDecimal(totalThirdPayable.doubleValue()));
    	result.set("totalDiffer", MathHelper.cutDecimal(totalDiffer.doubleValue()));
    	return result;
    }
    
    /**
     * 财务对账
     * @param path 文件路径
     * @param payType 支付方式
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @throws IOException 
     */
    public List<Record> checkBalance(String path, int payType, Date startTime, Date endTime) 
    		throws IOException {
    	
    	List<Record> excelData = getFileData(path, payType);
    	List<Record> orderData = getOrderData(payType, startTime, endTime);
    	String start = DateHelper.formatDate(startTime, "yyyy-MM-dd", 0);
    	String end = DateHelper.formatDate(endTime, "yyyy-MM-dd", 1);
    	
    	String sql = "delete from audit_record where tradeTime >= '" + start + "' and tradeTime <= '" + end + "' and payType = " + payType;
    	Db.update(sql);
    	
    	for (Record item : orderData) {
    		double t1 = item.getBigDecimal("totalPayable").doubleValue();
    		BigDecimal d1 = new BigDecimal(MathHelper.cutDecimal(t1));
    		item.set("tradeNo", item.getStr("tradeNo").trim());
    		item.set("totalPayable", d1);
    		item.set("thirdPayable", 0.0);
			item.set("differ", 0.0);  //对比结果
			item.set("used", false);          //是否已对比
    	}
    	
    	for (Record item : excelData) {
    		double t2 = item.getBigDecimal("totalPayable").doubleValue();
    		BigDecimal d2 = new BigDecimal(MathHelper.cutDecimal(t2));
    		item.set("tradeNo", item.getStr("tradeNo").trim());
    		item.set("totalPayable", d2);
    		item.set("thirdPayable", 0.0);
			item.set("differ", 0.0);
    		item.set("used", false);
    	}
    	
    	List<Record> result = new ArrayList<Record>();
    	for (Record item : orderData) {
    		String tradeNo1 = item.getStr("tradeNo");
    		long tradeType1 = item.getLong("tradeType");
    		BigDecimal totalPayable1 = item.getBigDecimal("totalPayable");
    		for (Record item2 : excelData) {
    			String tradeNo2 = item2.getStr("tradeNo");
    			int tradeType2 = item2.getInt("tradeType");
    			BigDecimal totalPayable2 = item2.getBigDecimal("totalPayable");
    			if (!item2.getBoolean("used") && tradeType1 == tradeType2 && tradeNo1.indexOf(tradeNo2) != -1) {
    				if (tradeType1 == 2) {
    					if (totalPayable1.compareTo(totalPayable2) == 0) {
    						double thirdPayable = item2.getBigDecimal("totalPayable").doubleValue();
            				double differ = item.getBigDecimal("totalPayable").doubleValue() - thirdPayable;
            				item.set("thirdPayable", thirdPayable);
            				item.set("differ", differ);
            				item.set("status", 1);
            				if (item.get("tradeNo") == null) {
								item.set("tradeNo", item2.getStr("tradeNo"));
							}
            				if (item.get("tradeTime") == null) {
								item.set("tradeTime", item2.getDate("tradeTime"));
							}
            				item.set("used", true);
            				item2.set("used", true);
            				result.add(item);
            				break;
    					}
        				continue;
    				}
    				
    				double thirdPayable = item2.getBigDecimal("totalPayable").doubleValue();
    				double differ = item.getBigDecimal("totalPayable").doubleValue() - thirdPayable;
    				item.set("thirdPayable", thirdPayable);
    				item.set("differ", differ);
    				item.set("status", 1);
    				if (item.get("tradeNo") == null) {
						item.set("tradeNo", item2.getStr("tradeNo"));
					}
    				if (item.get("tradeTime") == null) {
						item.set("tradeTime", item2.getDate("tradeTime"));
					}
    				item.set("used", true);
    				item2.set("used", true);
    				result.add(item);
    				break;
    			}
    		}
    	}
    	
    	//加入数据库剩余的数据
    	for (Record item : orderData) {
    		if (!item.getBoolean("used")) {
    			item.set("status", 2);
    			item.set("totalPayable", item.getBigDecimal("totalPayable"));
        		item.set("thirdPayable", 0);
    			item.set("differ", MathHelper.cutDecimal(item.getBigDecimal("totalPayable").doubleValue()));
    			result.add(item);
    		}
    	}
    	
    	//加入excel表剩余的数据
    	for (Record item : excelData) {
    		if (!item.getBoolean("used")) {
    			item.set("status", 3);
    			item.set("thirdPayable", MathHelper.cutDecimal(item.getBigDecimal("totalPayable").doubleValue()));
    			item.set("differ", MathHelper.cutDecimal(item.getBigDecimal("totalPayable").doubleValue() * -1));
    			item.set("totalPayable", new BigDecimal(0));
    			result.add(item);
    		}
    	}
    	
    	for (Record item : result) {
    		item.set("totalPayable", MathHelper.cutDecimal(item.getBigDecimal("totalPayable").doubleValue()));
    		
    		AuditRecord model = new AuditRecord();
    		model.set("tradeCode", item.get("tradeCode"));
    		model.set("tradeNo", item.get("tradeNo"));
    		model.set("tradeType", item.get("tradeType"));
    		model.set("payType", item.get("payType"));
    		model.set("tradeTime", item.get("tradeTime"));
    		model.set("status", item.get("status"));
    		model.set("payable", item.get("totalPayable"));
    		model.set("thirdPayable", item.get("thirdPayable"));
    		model.set("differ", item.get("differ"));
			model.save();
    	}
    	
    	return result;
    }
	
    /**
     * 获取对账文件中的数据
     * @param path 文件路径
     * @param payType 支付方式 1: 微信支付, 2: 支付宝, 3银联支付
     * @return list = [{"tradeCode":商户号,"tradeNo":流水号,"totalPayable":交易金额,patType:支付方式}]
     * @throws IOException
     */
    private List<Record> getFileData(String path, int payType) throws IOException {
    	List<Record> list = new ArrayList<Record>();
    	if (payType == 1) {  //微信公众号
			list = getDataByWeixin(path, payType);
    	} else if (payType == 2) {  //支付宝
    		list = getDataByAlipay(path, payType);
    	} else if (payType == 3) { //银联支付
    		list = getDataByUnionPay(path, payType);
    	} else if (payType == 4) {  //微信APP
			list = getDataByWeixinApp(path, payType);
    	}
		return list;
    }
    
    private List<Record> getDataByWeixin(String path, int payType) throws FileNotFoundException, IOException {
    	List<Record> list = new ArrayList<Record>();
    	
    	ArrayList<String[]> wxList = ExcelHelper.readCsv(path, "gbk");
		int length = wxList.size();
		for (int row = 1; row < length; row++) {
			//倒数第二行起的数据无效
			if (row == length - 2) {
				break;
			}
			
			int tradeType = 1;
			double totalPayale = 0;
			String strTradeType = wxList.get(row)[9].replace("`", "");
			if (strTradeType.indexOf("REFUND") != -1) {
				tradeType = 2;
			}
			
			if (tradeType == 1) {
				totalPayale = Double.parseDouble(wxList.get(row)[12].replace("`", ""));
			} else {
				totalPayale = Double.parseDouble(wxList.get(row)[16].replace("`", ""));
			}
				
			totalPayale = Math.abs(totalPayale);
			BigDecimal payable = new BigDecimal(totalPayale);
			String tradeCode = wxList.get(row)[6].replace("`", "");
			String tradeNo = wxList.get(row)[5].replace("`", "");
			String strDate = wxList.get(row)[0].replace("`", "");
			Date tradeTime = DateHelper.strToDate(strDate, "yyyy-MM-dd HH:mm:ss");
			
			Record item = new Record();
			item.set("tradeCode", tradeCode);
			item.set("tradeNo", tradeNo);
			item.set("totalPayable", payable);
			item.set("payType", 1);
			item.set("tradeType", tradeType);
			item.set("tradeTime", tradeTime);
			list.add(item);
		}
		
		return list;
    }
    
    private List<Record> getDataByWeixinApp(String path, int payType) throws FileNotFoundException, IOException {
    	List<Record> list = new ArrayList<Record>();
    	
    	ArrayList<String[]> wxList = ExcelHelper.readCsv(path, "utf-8");
		int length = wxList.size();
		for (int row = 1; row < length; row++) {
			//倒数第二行起的数据无效
			if (row == length - 2) {
				break;
			}
			
			int tradeType = 1;
			double totalPayale = 0;
			
			String strTradeType = wxList.get(row)[9].replace("`", "");
			if (strTradeType.indexOf("REFUND") != -1) {
				tradeType = 2;
			}
			
			if (tradeType == 1) {
				totalPayale = Double.parseDouble(wxList.get(row)[12].replace("`", ""));
			} else {
				totalPayale = Double.parseDouble(wxList.get(row)[16].replace("`", ""));
			}
				
			totalPayale = Math.abs(totalPayale);
			BigDecimal payale = new BigDecimal(totalPayale);
			String tradeCode = wxList.get(row)[6].replace("`", "");
			String tradeNo = wxList.get(row)[5].replace("`", "");
			String strDate = wxList.get(row)[0].replace("`", "");
			Date tradeTime = DateHelper.strToDate(strDate, "yyyy-MM-dd HH:mm:ss");
			
			Record item = new Record();
			item.set("tradeCode", tradeCode);
			item.set("tradeNo", tradeNo);
			item.set("totalPayable", payale);
			item.set("payType", 4);
			item.set("tradeType", tradeType);
			item.set("tradeTime", tradeTime);
			list.add(item);
		}
		
		return list;
    }
    
    private List<Record> getDataByAlipay(String path, int payType) throws FileNotFoundException, IOException {
    	List<Record> list = new ArrayList<Record>();
    	
    	ArrayList<String[]> alpList = ExcelHelper.readCsv(path, "gbk");
		int length = alpList.size();
		for (int row = 5; row < length; row++) {
			//倒数第四行起的数据无效
			if (row == length - 4) {
				break;
			}
			
			//如果是手续费，则不做处理
			if (alpList.get(row)[10].equals("收费") || alpList.get(row)[10].equals("提现")) {
				continue;
			}
			
			int tradeType = 1;
			double totalPayable = 0;
			if (alpList.get(row)[10].equals("在线支付")) {
				totalPayable = Double.parseDouble(alpList.get(row)[6]);
			}
			if (alpList.get(row)[10].equals("交易退款")) {
				totalPayable = Double.parseDouble(alpList.get(row)[7]);
				tradeType = 2;
			}
			
			totalPayable = Math.abs(totalPayable);
			BigDecimal payable = new BigDecimal(totalPayable);
			String strDate = alpList.get(row)[4];
			Date tradeTime = DateHelper.strToDate(strDate, "yyyy-MM-dd HH:mm:ss");
			
			Record item = new Record();
			item.set("tradeCode", alpList.get(row)[2]);
			item.set("tradeNo", alpList.get(row)[1]);
			item.set("totalPayable", payable);
			item.set("payType", 2);
			item.set("tradeType", tradeType);
			item.set("tradeTime", tradeTime);
			list.add(item);
		}
		
		return list;
    }
    
    private List<Record> getDataByUnionPay(String path, int payType) throws IOException {
    	List<Record> list = new ArrayList<Record>();
    	
    	Sheet sheet = ExcelHelper.readerExcel(path, 0);
        for (Row r : sheet) {  
        	if (r.getCell(0) == null || r.getCell(0).getStringCellValue() == null || r.getCell(0).getStringCellValue().equals("")) {
        		break;
        	}
        	//如果当前行的行号（从0开始）未达到第5行则重新循环  
    		if(r.getRowNum() < 4){  
    			continue;  
    		}
    		
    		int tradeType = 1;
    		if (r.getCell(8).getStringCellValue().indexOf("退货") != -1) {
    			tradeType = 2;
    		}
    		
    		double totalPayable = r.getCell(4).getNumericCellValue();
    		totalPayable = Math.abs(totalPayable);
    		BigDecimal payable = new BigDecimal(totalPayable);
    		
    		Record item = new Record();
			item.set("tradeCode", r.getCell(7).getStringCellValue());
			item.set("tradeNo", r.getCell(9).getStringCellValue());
			item.set("totalPayable", payable);
			item.set("payType", 3);
			item.set("tradeType", tradeType);
			item.set("tradeTime", r.getCell(1).getDateCellValue());
			list.add(item);
        }
        
        return list;
    }
    
    /**
     * 获取数据库中的交易记录
     * @return list = [{"tradeCode":商户号,"tradeNo":流水号,"totalPayable":交易金额,patType:支付方式}]
     */
    private List<Record> getOrderData(int payType, Date startTime, Date endTime) {
    	String start = DateHelper.formatDate(startTime, "yyyy-MM-dd", 0);
    	String end = DateHelper.formatDate(endTime, "yyyy-MM-dd", 1);
    	
    	String orderSql = "select if(codeType=1, theSameOrderNum, order_code) as tradeCode, tradeNo, payType, totalPayable, 1 as tradeType, 1 as type, payTime as tradeTime" + 
    			" from `order`" + 
    			" where status not in(1, 6)" + 
    			" and payTime >= '" + start + "'" +
    			" and payTime < '" + end + "'";
    	
    	String backSql = "select if(c.codeType=1, c.theSameOrderNum, c.order_code) as tradeCode, c.tradeNo, c.payType, a.refundCash as totalPayable, 2 as tradeType, 2 as type, successTime as tradeTime" + 
    			" from back as a" +
    			" left join product_order as b on a.product_order_id = b.id" +
    			" left join `order` as c on b.order_id = c.id" +
    			" where a.status = 4" + 
    			" and successTime >= '" + start + "'" +
    			" and successTime < '" + end + "'";
    	
    	String refundSql = "select if(c.codeType=1, c.theSameOrderNum, c.order_code) as tradeCode, c.tradeNo, c.payType, a.refundCash as totalPayable, 2 as tradeType, 3 as type, successTime as tradeTime" + 
    			" from refund as a" +
    			" left join product_order as b on a.product_order_id = b.id" +
    			" left join `order` as c on b.order_id = c.id" +
    			" where a.status = 3" + 
    			" and successTime >= '" + start + "'" +
    			" and successTime < '" + end + "'";
    	
    	String walletSql = "select tradeNo as tradeCode, transactionId as tradeNo, money as totalPayable, case event when 3 then 2 when 4 then 1 when 6 then 3 end as payType, 1 as tradeType, 4 as type, created_at as tradeTime" +
    			" from wallet" +
    			" where isPaySuccess = 1" +
    			" and created_at >= '" + start + "'" +
    			" and created_at < '" + end  + "'";
    	
    	if (payType == 1) {
    		orderSql += " and payType = 1 and source in (1,2)";
    		backSql += " and payType = 1 and source in (1,2)";
    		refundSql += " and payType = 1 and source in (1,2)";
    		walletSql += " and event = 4 and source in (1,2)";
    	} else if (payType == 4) {
    		orderSql += " and payType = 1 and source in (3,4)";
    		backSql += " and payType = 1 and source in (3,4)";
    		refundSql += " and payType = 1 and source in (3,4)";
    		walletSql += " and event = 4 and source in (3,4)";
    	} else if (payType == 2) {
    		orderSql += " and payType = " + payType;
    		backSql += " and payType = " + payType;
    		refundSql += " and payType = " + payType;
    		walletSql += " and event = 3";
    	} else if (payType == 3) {
    		orderSql += " and payType = " + payType;
    		backSql += " and payType = " + payType;
    		refundSql += " and payType = " + payType;
    		walletSql += " and event = 6";
    	}
    	
    	List<Record> orders = Db.find(orderSql);
    	List<Record> backs = Db.find(backSql);
    	List<Record> refunds = Db.find(refundSql);
    	List<Record> wallets = Db.find(walletSql);
    	
    	for (Record item : backs) {
    		orders.add(item);
    	}
    	
    	for (Record item : refunds) {
    		orders.add(item);
    	}
    	
    	for (Record item : wallets) {
    		orders.add(item);
    	}
    	
    	for (Record item : orders) {
    		if (payType == 4) {
				item.set("payType", 4);
			}
    	}
    	
    	return orders;
    }
    
}
