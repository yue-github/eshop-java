package com.eshop.finance;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;

public class StatisticsService {

	/**
	 * 统计订单
	 * @param orders
	 * @return
	 */
	public static Record calculateOrders(List<Record> orders) {
		double totalPayable = 0;
		double totalProductCost = 0;
		
		Record result = new Record();
		
		for (Record item : orders) {
			totalPayable += item.getDouble("totalPayable");
			totalProductCost += item.getDouble("totalProductCost");
		}
		
		result.set("totalPayable", totalPayable);
		result.set("totalProductCost", totalProductCost);
		return result;
	}
	
}
