package com.codependent.microshopping.payment.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.stream.dto.Order;
import com.codependent.microshopping.stream.dto.Order.State;

@Component
public class OrderListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderProcessor orderSource;
	
	@StreamListener(OrderProcessor.INPUT)
	public void handle(Order order){
		logger.info("received order [{}], processing payment", order);
		processPayment(order);
	}
	
	private void processPayment(Order order){
		if(doPay(order)){
			logger.info("Payment processed succesfully for order [{}], notifying order service and proceeding with shipping", order);
			order.setState(State.PAYED);
			orderSource.outputPayment().send(MessageBuilder.withPayload(order).build());
			orderSource.outputShipping().send(MessageBuilder.withPayload(order).build());
		}else{
			logger.info("Payment failed for order [{}], cancelling order", order);
			order.setState(State.CANCELLED_PAYMENT_FAILED);
			orderSource.outputPayment().send(MessageBuilder.withPayload(order).build());
		}
	}
	
	private boolean doPay(Order order){
		return (Math.random() < 0.8);
	}
}
