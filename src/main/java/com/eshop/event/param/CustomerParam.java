package com.eshop.event.param;

import com.eshop.model.Customer;

public class CustomerParam extends EventParam {
	
	private Customer customer;
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
}
