package com.codependent.microshopping.order.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.dto.Order.State;
import com.codependent.microshopping.order.service.OrderService;
import com.codependent.microshopping.order.stream.OrderSource;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersRestController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderSource orderSource;
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping
	public List<Order> getOrders(@RequestParam(required=false) State state){
		return orderService.getAll(state);
	}
	
	@PostMapping
	public Order createOrder(@RequestBody Order order){
		logger.debug("createOrder()");
		order = orderService.createOrder(order);
		try{
			orderSource.output().send(MessageBuilder.withPayload(order).build(), 500);
		}catch(Exception e){
			logger.error("{}", e);
		}
		return order;
	}
}
