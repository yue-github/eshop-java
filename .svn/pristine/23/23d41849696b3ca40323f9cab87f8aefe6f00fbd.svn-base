package com.eshop.event.listenner;

import com.eshop.event.EventEnum;
import com.eshop.event.param.InvoiceParam;
import com.eshop.invoice.OrderInvoiceService;
import com.jfinal.plugin.activerecord.Record;

public class InvoiceListenner implements EventListenner {

	@Override
	public void Process(EventEnum event, Object param1) {
		switch (event) {
		case EVENT_SUBMIT_ORDER:
			if (param1 instanceof InvoiceParam) {
				int orderId = ((InvoiceParam) param1).getOrderId();
				Record invoice = ((InvoiceParam) param1).getInvoice();
				OrderInvoiceService.createOrderInvoice(invoice, orderId);
			}
			break;
		}
	}

}
