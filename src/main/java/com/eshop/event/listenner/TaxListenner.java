package com.eshop.event.listenner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.eshop.event.EventEnum;
import com.eshop.event.param.OrderParam;
import com.eshop.model.Order;
import com.eshop.model.PayRate;
import com.eshop.model.Product;
import com.eshop.model.ProductOrder;
import com.eshop.model.Tax;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

public class TaxListenner implements EventListenner {

	@Override
	public void Process(EventEnum event, Object param1) {
		switch (event) {
		case EVENT_SUBMIT_ORDER:
			if (param1 instanceof OrderParam) {
				
				Order order = ((OrderParam) param1).getOrder();
				
				//计算订单明细的税率
				calculateProductTax(order);
				
				//计算支付手续费
				calculatePayTax(order);
			}
			break;
		}
	}
	
	/**
	 * 计算订单明细的税率
	 * @param order
	 */
	private void calculateProductTax(final Order order) {
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					BigDecimal totalTaxRateSum = new BigDecimal(0);
					List<ProductOrder> productOrders = ProductOrder.dao.find("select * from product_order where order_id = ?", order.getId());
					for (ProductOrder productOrder : productOrders) {
						Product product = Product.dao.findById(productOrder.getProductId());
						
						Tax tax = Tax.dao.findById(product.getTaxId());
						String taxName = (tax != null) ? tax.getName() : "";
						BigDecimal taxRate = product.getTaxRate();
						BigDecimal totalActualProductPrice = productOrder.getTotalActualProductPrice();
						BigDecimal taxRateSum = totalActualProductPrice.multiply(taxRate).multiply(new BigDecimal(0.01));
						totalTaxRateSum = totalTaxRateSum.add(taxRateSum);
						
						productOrder.setTaxName(taxName);
						productOrder.setTaxRate(taxRate);
						productOrder.setTaxRateSum(taxRateSum);
						productOrder.update();
					}
					
					order.setTaxRateSum(totalTaxRateSum);
					order.update();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		
	}
	
	/**
	 * 计算支付手续费
	 * @param order
	 */
	private void calculatePayTax(Order order) {
		int payType = order.getPayType();
		
		PayRate payRate = PayRate.dao.findFirst("select * from pay_rate where payType = ?", payType);
		BigDecimal rate = (payRate != null) ? payRate.getRate() : new BigDecimal(0);
		BigDecimal totalPayable = order.getTotalPayable();
		BigDecimal payRateSum = totalPayable.multiply(rate);
		payRateSum = payRateSum.multiply(new BigDecimal(0.01));
		
		order.setPayRate(rate);
		order.setPayRateSum(payRateSum);
		order.update();
	}

}
