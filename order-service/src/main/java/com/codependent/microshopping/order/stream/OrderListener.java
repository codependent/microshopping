package com.codependent.microshopping.order.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.dto.Order.State;
import com.codependent.microshopping.order.service.OrderService;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderService orderService;
	
	@SuppressWarnings("incomplete-switch")
	@StreamListener(OrderProcessor.INPUT)
	public void handleOrder(Order order){
		switch(order.getState()){
		case PRODUCT_RESERVED:
			logger.info("product reserved for order [{}] - asking for payment", order);
			order.setState(State.PENDING_PAYMENT);
			orderService.updateOrder(order);
			break;
		case PAYED:
			logger.info("received payment information [{}] - saving state and requesting shipping", order);
			order.setState(State.PENDING_SHIPPING);
			orderService.updateOrder(order);
			break;
		case CANCELLED_PAYMENT_FAILED:
			logger.info("payment failed for order [{}] - Cancelling order", order);
			orderService.updateOrder(order);
			break;
		case CANCELLED_NO_STOCK:
			logger.info("no product stock for order [{}] - Cancelling order", order);
			orderService.updateOrder(order);
			break;
		case SHIPPED:
			logger.info("received shipping information [{}]", order);
			order.setState(State.COMPLETED);
			orderService.updateOrder(order);
			break;
		}
	}
}
