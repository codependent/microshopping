package com.codependent.microshopping.order.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.service.OrderService;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderService orderService;
	
	@StreamListener(OrderProcessor.INPUT)
	public void handleOrder(Order order){
		
		/*
		switch(order.getState()){
		case PRODUCT_RESERVED:
			logger.info("product reserved for order [{}] - asking for payment", order);
			order.setState(State.REQUEST_PAYMENT);
			orderService.updateOrder(order);
			break;
		case PAYMENT_SUCCESSFUL:
			logger.info("received payment information [{}] - saving state and requesting shipping", order);
			order.setState(State.REQUEST_SHIPPING);
			orderService.updateOrder(order);
			break;
		case PAYMENT_FAILED:
			logger.info("payment failed for order [{}] - Cancelling product reservation", order);
			order.setState(State.CANCEL_PRODUCT_RESERVATION);
			orderService.updateOrder(order);
			break;
		case PRODUCT_RESERVATION_CANCELLED:
			logger.info("product reservation cancelled for order [{}]", order);
			order.setState(State.CANCELLED_PAYMENT_FAILED);
			orderService.updateOrder(order);
			break;
		case CANCELLED_NO_STOCK:
			logger.info("no product stock for order [{}] - Cancelling order", order);
			orderService.updateOrder(order);
			break;
		case SHIPPING_REQUESTED:
			logger.info("received shipping information [{}]", order);
			order.setState(State.COMPLETED);
			orderService.updateOrder(order);
			break;
		}*/
	}
}