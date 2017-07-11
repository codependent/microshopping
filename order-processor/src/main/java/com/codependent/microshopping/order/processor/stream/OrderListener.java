package com.codependent.microshopping.order.processor.stream;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.order.processor.dto.Order;
import com.codependent.microshopping.order.processor.dto.Order.State;
import com.codependent.microshopping.order.processor.service.OrderService;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderService orderService;
	
	@StreamListener(OrderConsumer.INPUT)
	public void handleOrder(Map<String, Object> event){
		logger.info("Received event {}", event);
		if("OrderPlaced".equals(event.get("name"))){
			logger.info("Order created");
			Order order = new Order();
			order.setProductId((Integer)(event.get("productId")));
			order.setUid(event.get("uid").toString());
			order.setState(State.PENDING_PRODUCT_RESERVATION);
			orderService.createOrder(order);
		}else{
			
		}
	}	
}
