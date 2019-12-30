package com.eshop.helper;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import com.eshop.model.Customer;
import com.eshop.model.ProductOrder;
import com.eshop.model.Wallet;


/**
 * 钱包退款辅助类
 * @author TangYiFeng
 */
public class WalletRefundHelper {
	
	private static Logger logger = Logger.getLogger(WalletRefundHelper.class);
	
	/**
	 * 余额退款
	 * @param productOrderId 产品明细id
	 * @param refundAmount 退款金额
	 * @return
	 */
	public boolean refund(int customerId, int productOrderId, double refundAmount) {
		ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
		Customer customer = Customer.dao.findById(customerId);
		
		if (productOrder == null || customer == null) {
			return false;
		}
		
		//把退款打到用户钱包
		Wallet model = new Wallet();
		model.setCustomerId(customerId);
		model.setEvent(1);
		model.setMoney(new BigDecimal(refundAmount));
		model.setCreatedAt(new Date());
		model.setUpdatedAt(new Date());
		model.setRelateId(productOrderId);
		model.setFinishTime(new Date());
		
		if (!model.save()) {
			return false;
		}
		
		return true;
	}
	
} 
