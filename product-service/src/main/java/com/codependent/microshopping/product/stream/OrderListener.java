package com.codependent.microshopping.product.stream;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.product.dto.Order;
import com.codependent.microshopping.product.service.ProductService;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProductService productService;
	
	//@StreamListener(OrderProcessor.INPUT)
	public void handleOrder(Map<String, Object> event){
		logger.info("Event {}", event);
		if("OrderPlaced".equals(event.get("name"))){
			Order order = new Order();
			order.setId((String)event.get("orderId"));
			order.setProductId((Integer)(event.get("productId")));
			order.setUid(event.get("uid").toString());
			productService.reserveProduct(order);
		}else{
			
		}
		/*
		switch(order.getState()){
		case PENDING_PRODUCT_RESERVATION:
			logger.info("received stock check request for order [{}]", order);
			productService.reserveProduct(order);
			break;
		case CANCEL_PRODUCT_RESERVATION:
			logger.info("payment failed for order [{}] - cancelling product reservation", order);
			productService.cancelReservation(order);
			break;
		
		}*/
	}
}
