package com.codependent.microshopping.payment.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.payment.dto.Order;
import com.codependent.microshopping.payment.service.PaymentService;

@Component
public class OrderListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PaymentService paymentService;
	
	@SuppressWarnings("incomplete-switch")
	@StreamListener(OrderProcessor.INPUT)
	public void handle(Order order){
		switch(order.getState()){
		case REQUEST_PAYMENT:
			logger.info("received payment request for order [{}]", order);
			paymentService.pay(order);
			break;
		}
	}
	
}
