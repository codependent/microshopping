package com.codependent.microshopping.product.stream;

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
	
	@SuppressWarnings("incomplete-switch")
	@StreamListener(OrderProcessor.INPUT)
	public void handleOrder(Order order){
		switch(order.getState()){
		case PENDING_PRODUCT_RESERVATION:
			logger.info("received stock check request for order [{}]", order);
			productService.reserveProduct(order);
			break;
		case CANCELLED_PAYMENT_FAILED:
			logger.info("payment failed for order [{}] - cancelling product reservation", order);
			productService.cancelReservation(order);
			break;
		
		}
	}
}
