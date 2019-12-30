package com.eshop.task;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.eshop.config.ITask;
import com.eshop.helper.MathHelper;
import com.eshop.helper.StringHelper;
import com.eshop.model.SupplierBillItem;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SupplierBillTask implements ITask {
	
    @Override
	public void run() {
        // 根据供应商账期来生成对应的账单
    	// 算法：
    	// 1、查找出所有有账期并且在合同有效期内的供应商
    	// 2、便利每个供应商（判断账期格式是否正确，不正确不做处理）
    	// 3、找出每个供应商未参与结账并已发货的订单明细
    	// 4、遍历每个订单明细，判断是否符合账期条件，如符合把订单明细添加到供应商账单中
    	String sql = "select a.id, a.name, b.account_period from supplier as a" +
    			" left join supplier_contract as b on a.supplier_contract_id = b.id" +
    			" where a.supplier_contract_id != 0" + 
    			" and b.account_period != ''";
    	List<Record> suppliers = Db.find(sql);
    	
    	for (Record supplier : suppliers) {
    		int supplierId = supplier.getInt("id");
    		String accountPeriod = supplier.getStr("account_period");
    		if (!checkAccountPeriod(accountPeriod)) {
    			continue;
    		}
    		
    		// 筛选掉已退款成功并且已生成账单的订单明细
    		sql = "select a.*, b.sendOutTime, b.order_code from product_order as a" + 
    				" left join `order` as b on a.order_id = b.id" +
    				" where b.status in (3, 4, 5, 7)" +
    				" and a.supplier_id = " + supplierId +
    				" and a.id not in (" + "select product_order_id from supplier_bill_item where tradeType = 'order'" + ")" +
    				" and a.id not in (" + "select product_order_id from refund where status = 3" + ")";
    		
    		List<Record> productOrders = Db.find(sql);
    		String code = createCode();
    		
    		for (Record item : productOrders) {
    			Date sendOutTime = item.getDate("sendOutTime");
    			if (isMeetAccountPeriod(sendOutTime, accountPeriod)) {
    				SupplierBillItem model = new SupplierBillItem();
    				model.setProductOrderId(item.getInt("id"));
    				model.setSupplierId(item.getInt("supplier_id"));
    				model.setSupplierName(item.getStr("supplier_name"));
    				model.setProductId(item.getInt("product_id"));
    				model.setProductName(item.getStr("product_name"));
    				model.setPriceId(item.getInt("priceId"));
    				model.setSelectProterties(item.getStr("selectProterties"));
    				model.setTaxRate(item.getBigDecimal("taxRate"));
    				model.setUnitOrdered(item.getInt("unitOrdered"));
    				model.setUnitCost(item.getBigDecimal("unitCost"));
    				model.setSuggestedRetailUnitPrice(item.getBigDecimal("suggestedRetailUnitPrice"));
    				model.setActualUnitPrice(item.getBigDecimal("actualUnitPrice"));
    				model.setTotalProductCost(item.getBigDecimal("totalProductCost"));
    				model.setTotalActualProductPrice(item.getBigDecimal("totalActualProductPrice"));
    				model.setSendOutTime(sendOutTime);
    				model.setStatus("apply");
    				model.setCreatedAt(new Date());
    				model.setCode(code);
    				model.setTradeType("order");
    				model.setUnitCostNoTax(item.getBigDecimal("unitCostNoTax"));
    				model.setOrderCode(item.getStr("order_code"));
    				model.save();
    			}
    		}
    		
    		// 筛选出已退货并且还没生成账单的退货单
    		sql = "select * from back as a" +
    				" left join product_order as b on a.product_order_id = b.id" +
    				" left join `order` as c on b.order_id = c.id" +
    				" where a.status = 4" +
    				" and b.supplier_id = " + supplierId +
    				" and a.product_order_id not in (" + "select product_order_id from supplier_bill_item where tradeType = 'back'" + ")";
    		
    		List<Record> backs = Db.find(sql);
    		for (Record item : backs) {
    			Date sendOutTime = item.getDate("successTime");
    			if (isMeetAccountPeriod(sendOutTime, accountPeriod)) {
    				BigDecimal negative = new BigDecimal(-1);
    				SupplierBillItem model = new SupplierBillItem();
    				model.setProductOrderId(item.getInt("product_order_id"));
    				model.setSupplierId(item.getInt("supplier_id"));
    				model.setSupplierName(item.getStr("supplier_name"));
    				model.setProductId(item.getInt("product_id"));
    				model.setProductName(item.getStr("product_name"));
    				model.setPriceId(item.getInt("priceId"));
    				model.setSelectProterties(item.getStr("selectProterties"));
    				model.setTaxRate(item.getBigDecimal("taxRate").multiply(negative));
    				model.setUnitOrdered(item.getInt("unitOrdered") * -1);
    				model.setUnitCost(item.getBigDecimal("unitCost").multiply(negative));
    				model.setSuggestedRetailUnitPrice(item.getBigDecimal("suggestedRetailUnitPrice").multiply(negative));
    				model.setActualUnitPrice(item.getBigDecimal("actualUnitPrice").multiply(negative));
    				model.setTotalProductCost(item.getBigDecimal("totalProductCost").multiply(negative));
    				model.setTotalActualProductPrice(item.getBigDecimal("totalActualProductPrice").multiply(negative));
    				model.setSendOutTime(sendOutTime);
    				model.setStatus("apply");
    				model.setCreatedAt(new Date());
    				model.setCode(code);
    				model.setTradeType("back");
    				model.setUnitCostNoTax(item.getBigDecimal("unitCostNoTax").multiply(negative));
    				model.setOrderCode(item.getStr("order_code"));
    				model.save();
    			}
    		}
    		
    	}
    }
   
    @Override
	public void stop() {
        //这里的代码会在 task 被关闭前调用
    }
    
    /**
     * 生成供应商账单编号
     * 编号规则：'1' + 至少6位流水号 + 2位随机数
     * @return
     */
    private String createCode() {
    	int count = Db.find("select * from supplier_bill_item where code != '' group by code").size();
    	count += 1;
    	
    	String serialCode = count + "";
    	int length = 6;  // 流水号长度
    	int remainLength = length - serialCode.length();
    	
    	if (remainLength >= 0) {
    		for (int i = 0; i < remainLength; i++) {
    			serialCode = "0" + serialCode;
    		}
    	}
    	
    	String randomNum = MathHelper.getRandom(10, 100) + "";
    	
    	return "1" + serialCode + randomNum;
    }
    
    /**
     * 判断是否符合账期
     * @param sendOutTime
     * @param accountPeriod
     * @return
     */
    private boolean isMeetAccountPeriod(Date sendOutTime, String accountPeriod) {
    	if (sendOutTime == null) {
    		return false;
    	}
    	
    	Date now = new Date();
    	String[] arr = accountPeriod.split(",");
    	String type = arr[0];
    	int days = Integer.parseInt(arr[1]);
    	
    	long differDays = -1;
    	
    	if (type.equals("day")) {
    		differDays = differDays(now, sendOutTime);
    	} else if (type.equals("month")) {
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(sendOutTime);
    		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    		Date endMonth = calendar.getTime();
    		differDays = differDays(now, endMonth);
    	}
    	
    	if (differDays >= days) {
			return true;
		}
    	
    	return false;
    }
    
    private long differDays(Date now, Date other) {
    	long days = (now.getTime() - other.getTime()) / (1000*60*60*24);
    	return days;
    }
    
    /**
     * 判断账期格式是否正确
     * @param accountPeriod
     * @return
     */
    private boolean checkAccountPeriod(String accountPeriod) {
    	if (accountPeriod == null || accountPeriod.equals("")) {
			return false;
		}
    	
		String[] arr = accountPeriod.split(",");
		
		if (arr.length != 2) {
			return false;
		}
		if (!arr[0].equals("day") && !arr[0].equals("month")) {
			return false;
		}
		if (!StringHelper.isNumeric(arr[1])) {
			return false;
		}
		
		return true;
    }
    
}