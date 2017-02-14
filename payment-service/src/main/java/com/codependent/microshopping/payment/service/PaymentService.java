package com.codependent.microshopping.payment.service;

import com.codependent.microshopping.payment.dto.Order;
import com.codependent.microshopping.payment.dto.Payment;

public interface PaymentService {

	Payment pay(Order order);
	
}
