package com.codependent.microshopping.shipping.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.stream.dto.Order;

@Component
public class OrderProcessor {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@StreamListener(OrderSource.INPUT)
	public void handle(Order order){
		logger.info("received shipping request");
		//TODO Do ship
	}
	
}
