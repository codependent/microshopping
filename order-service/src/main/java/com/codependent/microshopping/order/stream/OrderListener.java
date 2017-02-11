package com.codependent.microshopping.order.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.order.service.OrderService;
import com.codependent.microshopping.order.utils.OrikaObjectMapper;
import com.codependent.microshopping.stream.dto.Order;
import com.codependent.microshopping.stream.dto.Order.State;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderProcessor orderProcessor;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Autowired
	private OrderService orderService;
	
	@SuppressWarnings("incomplete-switch")
	@StreamListener(OrderProcessor.INPUT)
	public void handleOrder(Order order){
		switch(order.getState()){
		case PAYED:
			logger.info("received payment information [{}] - saving state and requesting shipping", order);
			order.setState(State.PENDING_SHIPPING);
			orderService.updateOrder(mapper.map(order, com.codependent.microshopping.order.dto.Order.class));
			orderProcessor.output().send(MessageBuilder.withPayload(mapper.map(order, com.codependent.microshopping.stream.dto.Order.class)).build(), 500);
			break;
		case SHIPPED:
			logger.info("received shipping information [{}]", order);
			order.setState(State.COMPLETED);
			orderService.updateOrder(mapper.map(order, com.codependent.microshopping.order.dto.Order.class));
			break;
		}
	}
}
