package com.codependent.microshopping.payment.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.stream.dto.Order;

@Component
public class OrderProcessor {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderSource orderSource;
	
	@StreamListener(OrderSource.INPUT)
	public void handle(Order order){
		logger.info("received order, processing payment");
		processPayment(order);
		
	}
	
	private void processPayment(Order order){
		logger.info("Payment processed succesfully, requesting shipping");
		orderSource.output().send(MessageBuilder.withPayload(order).build());
	}
	
}
