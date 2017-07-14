package com.codependent.microshopping.order.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.codependent.microshopping.order.stream.OrderProducer;
import com.codependent.microshopping.order.utils.OrikaObjectMapper;

@Service
@Transactional
@SuppressWarnings("unused")
public class OrderServiceImpl implements OrderService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderProducer orderProducer;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Override
	public Order createOrder(Order order) {
		OrderEntity orderEntity = mapper.map(order, OrderEntity.class);
		orderEntity.setState(State.PROCESSING);
		Map<String, Object> event = new HashMap<>();
		event.put("name", "OrderPlaced");
		event.put("orderId", UUID.randomUUID().toString());
		event.put("productId", order.getProductId());
		event.put("uid", order.getUid());
		orderProducer.output().send(MessageBuilder.withPayload(event).build(), 500);
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

}
