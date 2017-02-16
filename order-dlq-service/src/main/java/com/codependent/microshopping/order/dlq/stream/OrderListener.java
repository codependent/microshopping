package com.codependent.microshopping.order.dlq.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.order.dlq.dto.Order;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderProcessor orderProcessor;
	
	@StreamListener(OrderProcessor.INPUT)
	public void handleOrder(Order order){
		logger.info("Received message in DLQ: {}", order);
	}
}
