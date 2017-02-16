package com.codependent.microshopping.order.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.dto.Order.State;
import com.codependent.microshopping.order.entity.OrderEntity;
import com.codependent.microshopping.order.repository.OrderDao;
import com.codependent.microshopping.order.stream.OrderProcessor;
import com.codependent.microshopping.order.utils.OrikaObjectMapper;
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
		orderEntity.setState(State.PENDING_PRODUCT_RESERVATION);
		orderEntity = orderDao.save(orderEntity);
		/*
		try{
			orderProcessor.output().send(MessageBuilder.withPayload(mapper.map(orderEntity, com.codependent.microshopping.stream.dto.Order.class)).build(), 500);
		}catch(Exception e){
			logger.error("{}", e);
		}*/
		messagingService.createPendingMessage("orders", orderEntity.getId(), orderEntity.getState().name(), mapper.map(orderEntity, Order.class));
		return mapper.map(orderEntity, Order.class);
	}

	@Override
	public List<Order> getAll(State state) {
		if(state != null){
			return mapper.map(orderDao.findByState(state), Order.class);
		}else{
			return mapper.map(orderDao.findAll(), Order.class);
		}
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
		if(order.getState() != State.COMPLETED &&
		   order.getState() != State.CANCELLED_NO_STOCK &&
		   order.getState() != State.CANCELLED_PAYMENT_FAILED){
			messagingService.createPendingMessage("orders", order.getId(), order.getState().name(), order);
		}
		return mapper.map(oe, Order.class);
	}

	
	
}
