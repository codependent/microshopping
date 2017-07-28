package com.codependent.microshopping.payment.service;

import com.codependent.microshopping.payment.dto.Order;

public interface PaymentService {

	boolean pay(Order order);
	
}
