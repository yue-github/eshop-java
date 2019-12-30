package com.eshop.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.eshop.helper.StringHelper;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 分类管理-控制器
 * @author TangYiFeng
 */
public class DemoController extends AdminBaseController {
	
    /** 
     * 构造方法
     */
    public DemoController() {
    }

    public void demo() {
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
    		
    		sql = "select a.*, b.sendOutTime from product_order as a" + 
    				" left join `order` as b on a.order_id = b.id" +
    				" where b.status in (3, 4, 5, 7)" +
    				" and a.supplierBillStatus = 'normal'" +
    				" and a.supplier_id = " + supplierId;
    		
    		List<Record> productOrders = Db.find(sql);
    		
    		for (Record productOrder : productOrders) {
    			int id = productOrder.getInt("id");
    			Date sendOutTime = productOrder.getDate("sendOutTime");
    			if (isMeetAccountPeriod(sendOutTime, accountPeriod)) {
    				Db.update("update product_order set supplierBillStatus = ? where id = ?", "applying", id);
    			}
    		}
    		
    	}
    	
    	renderText("success");
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