package com.codependent.microshopping.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.payment.dto.Order;

@Service
public class PaymentServiceImpl implements PaymentService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public boolean pay(Order order){
		return (Math.random() < 0.8);
	}
	
}
