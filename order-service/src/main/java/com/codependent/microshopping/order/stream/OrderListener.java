package com.codependent.microshopping.order.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.order.service.OrderService;
import com.codependent.microshopping.order.utils.OrikaObjectMapper;
import com.codependent.microshopping.stream.dto.Order;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Autowired
	private OrderService orderService;
	
	@StreamListener(OrderProcessor.INPUT_PAYMENT)
	public void handlePayment(Order order){
		logger.info("received payment information [{}]", order);
		orderService.updateOrder(mapper.map(order, com.codependent.microshopping.order.dto.Order.class));
	}
	
	@StreamListener(OrderProcessor.INPUT_SHIPPING)
	public void handleShipping(Order order){
		logger.info("received shipping information [{}]", order);
		orderService.updateOrder(mapper.map(order, com.codependent.microshopping.order.dto.Order.class));
	}
}
