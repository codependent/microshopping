package com.codependent.microshopping.shipping.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.shipping.dto.Order;
import com.codependent.microshopping.shipping.dto.Order.State;

@Component
public class OrderListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderProcessor orderProcessor;
	
	@SuppressWarnings("incomplete-switch")
	@StreamListener(OrderProcessor.INPUT)
	public void handle(Order order){
		switch(order.getState()){
		case REQUEST_SHIPPING:
			logger.info("received shipping request for order [{}]", order);
			requestShipping(order);
			try{
				order.setState(State.SHIPPING_REQUESTED);
				orderProcessor.output().send(MessageBuilder.withPayload(order).build(), 500);
			}catch(Exception e){
				logger.error("{}", e);
			}
			break;
		}
	}
	
	public void requestShipping(Order order){
		
	}
}
