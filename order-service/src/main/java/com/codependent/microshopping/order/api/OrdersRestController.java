package com.codependent.microshopping.order.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codependent.microshopping.order.stream.OrderSource;
import com.codependent.microshopping.stream.dto.Order;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersRestController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<Order> orders = new ArrayList<>();
	
	@Autowired
	private OrderSource orderSource;
	
	@GetMapping
	public List<Order> getAllOrders(){
		return orders;
	}
	
	@PostMapping
	public Order createOrder(@RequestBody Order order){
		logger.debug("createOrder()");
		//TODO save to ORDERS DB
		try{
			orderSource.output().send(MessageBuilder.withPayload(order).build(), 500);
		}catch(Exception e){
			logger.error("{}", e);
		}
		return order;
	}
}
