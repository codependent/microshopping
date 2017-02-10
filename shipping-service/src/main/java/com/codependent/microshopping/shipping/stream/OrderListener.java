package com.codependent.microshopping.shipping.stream;

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
	private OrderProcessor orderProcessor;
	
	@StreamListener(OrderProcessor.INPUT)
	public void handle(Order order){
		logger.info("received shipping request for order [{}]", order);
		//TODO Do ship
		try{
			order.setState(State.SHIPPED);
			orderProcessor.output().send(MessageBuilder.withPayload(order).build(), 500);
		}catch(Exception e){
			logger.error("{}", e);
		}
	}
	
}
