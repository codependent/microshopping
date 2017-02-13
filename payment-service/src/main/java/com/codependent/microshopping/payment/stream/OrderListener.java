package com.codependent.microshopping.payment.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.payment.dto.Order;
import com.codependent.microshopping.payment.dto.Order.State;

@Component
public class OrderListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderProcessor orderSource;
	
	@SuppressWarnings("incomplete-switch")
	@StreamListener(OrderProcessor.INPUT)
	public void handle(Order order){
		switch(order.getState()){
		case PENDING_PAYMENT:
			logger.info("received order [{}], processing payment", order);
			processPayment(order);
			break;
		}
	}
	
	private void processPayment(Order order){
		if(doPay(order)){
			logger.info("Payment processed succesfully for order [{}], notifying order service and proceeding with shipping", order);
			order.setState(State.PAYED);
			orderSource.output().send(MessageBuilder.withPayload(order).build());
		}else{
			logger.info("Payment failed for order [{}], cancelling order", order);
			order.setState(State.PAYMENT_FAILED);
			orderSource.output().send(MessageBuilder.withPayload(order).build());
		}
	}
	
	private boolean doPay(Order order){
		return (Math.random() < 0.01);
	}
}
