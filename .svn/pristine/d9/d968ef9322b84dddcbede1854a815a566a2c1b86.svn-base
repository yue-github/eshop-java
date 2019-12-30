package com.eshop.invoice;

import java.sql.SQLException;

import com.eshop.log.Log;
import com.eshop.model.EletronicInvoice;
import com.eshop.model.Order;
import com.eshop.model.OrderInvoice;
import com.eshop.model.PlainInvoice;
import com.eshop.model.VatInvoice;
import com.eshop.model.VatInvoiceItem;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class OrderInvoiceService {
	
	/**
	 * 处理发票
	 * @param invoiceHeadStr {type:发票类型(1普通发票,2电子发票,3增值税发票),invoiceHead:发票抬头,invoiceContent:发票内容}
	 * @param order
	 */
	public static void createOrderInvoice(final Record invoice, final int orderId) {
		if (invoice == null) {
    		return;
    	}
		
		Order order = Order.dao.findFirst("select * from `order` where id = ?", orderId);
		
    	int customerId = order.getCustomerId();
    	int type = invoice.getInt("type");
    	String invoiceHead = invoice.getStr("invoiceHead");
    	String invoiceContent = invoice.getInt("invoiceContent") == 1 ? "订单明细" : "商品类型";
    	
    	int count = OrderInvoice.dao.find("select * from order_invoice where orderId = ?", orderId).size();
    	if (count > 0) {
    		return;
    	}
    	
    	final OrderInvoice orderInvoice = new OrderInvoice();
    	orderInvoice.setOrderId(order.getId());
    	orderInvoice.setOrderCode(order.getOrderCode());
    	orderInvoice.setTotalPayable(order.getTotalPayable());
    	orderInvoice.setTheSameOrderNum(order.getTheSameOrderNum());
		orderInvoice.setType(type);
		orderInvoice.setInvoiceHead(invoiceHead);
		orderInvoice.setInvoiceContent(invoiceContent);
		orderInvoice.setCustomerId(customerId);
		
		if (type == 1) {
			PlainInvoice plainInvoice = PlainInvoice.dao.findFirst("select * from plain_invoice where customerId = ? and invoiceHead = ?", customerId, invoiceHead);
			if (plainInvoice != null) {
				orderInvoice.setCompanyCode(plainInvoice.getCompanyCode());
			}
		} else if (type == 2) {
			EletronicInvoice elec = EletronicInvoice.dao.findFirst("select * from eletronic_invoice where customerId = ? and invoiceHead = ?", customerId, invoiceHead);
			if (elec != null) {
				orderInvoice.setCompanyCode(elec.getCompanyCode());
				orderInvoice.setEmail(elec.getEmail());
	    		orderInvoice.setPhone(elec.getPhone());
			}
    	} else if (type == 3) {
    		VatInvoice vatInvoice = VatInvoice.dao.findFirst("select * from vat_invoice where customerId = ?", customerId);
    		VatInvoiceItem vatInvoiceItem = VatInvoiceItem.dao.findFirst("select * from vat_invoice_item where customerId = ?", customerId);
    		orderInvoice.setName(vatInvoiceItem.getName());
    		orderInvoice.setPhone(vatInvoiceItem.getPhone());
    		orderInvoice.setProvince(vatInvoiceItem.getProvince());
    		orderInvoice.setCity(vatInvoiceItem.getCity());
    		orderInvoice.setDistrict(vatInvoiceItem.getDistrict());
    		orderInvoice.setAddressDetail(vatInvoiceItem.getAddressDetail());
    		orderInvoice.setCompanyName(vatInvoice.getCompanyName());
    		orderInvoice.setCompanyCode(vatInvoice.getCompanyCode());
    		orderInvoice.setCompanyAddress(vatInvoice.getCompanyAddress());
    		orderInvoice.setCompanyPhone(vatInvoice.getCompanyPhone());
    		orderInvoice.setBankName(vatInvoice.getBankName());
    		orderInvoice.setBankAccount(vatInvoice.getBankAccount());
    	}
		
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					orderInvoice.save();
			    	Db.update("update `order` set invoiceHead = ? where id = ?", invoice.getStr("invoiceHead"), orderId);
				} catch (Exception e) {
					Log.error(e.getMessage() + ",生成发票失败");
					return false;
				}
				return true;
			}
		});
		
	}
	
	/**
     * 获取发票基本信息
     * @param id
     * @return
     */
    public static Record getOrderInvoice(int orderId) {
    	String sql = "select * from order_invoice where orderId = " + orderId;
    	Record record = Db.findFirst(sql);
    	return record;
    }
	
}
