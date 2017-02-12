package com.codependent.microshopping.order.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.dto.Order.State;
import com.codependent.microshopping.order.entity.OrderEntity;
import com.codependent.microshopping.order.repository.OrderDao;
import com.codependent.microshopping.order.stream.OrderProcessor;
import com.codependent.microshopping.order.utils.OrikaObjectMapper;
import com.codependent.microshopping.stream.Channel;
import com.codependent.stream.service.MessagingService;

@Service
@Transactional
@SuppressWarnings("unused")
public class OrderServiceImpl implements OrderService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderProcessor orderProcessor;
	
	@Autowired
	private MessagingService messagingService;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Override
	public Order createOrder(Order order) {
		OrderEntity orderEntity = mapper.map(order, OrderEntity.class);
		orderEntity.setState(State.PENDING_PAYMENT);
		orderEntity = orderDao.save(orderEntity);
		/*
		try{
			orderProcessor.output().send(MessageBuilder.withPayload(mapper.map(orderEntity, com.codependent.microshopping.stream.dto.Order.class)).build(), 500);
		}catch(Exception e){
			logger.error("{}", e);
		}*/
		messagingService.createPendingMessage("orders", mapper.map(orderEntity, com.codependent.microshopping.stream.dto.Order.class));
		return mapper.map(orderEntity, Order.class);
	}

	@Override
	public List<Order> getAll(State state) {
		List<OrderEntity> orders = orderDao.findByState(state);
		return mapper.map(orders, Order.class);
	}

	@Override
	public Order getOrder(int id) {
		return mapper.map(orderDao.findOne(id), Order.class);
	}

	@Override
	public Order updateOrder(Order order) {
		OrderEntity oe = orderDao.findOne(order.getId());
		oe.setState(order.getState());
		oe = orderDao.save(oe);
		return mapper.map(oe, Order.class);
	}

	
	
}
