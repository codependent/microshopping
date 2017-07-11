package com.codependent.microshopping.order.processor.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.order.processor.dto.Order;
import com.codependent.microshopping.order.processor.entity.OrderEntity;
import com.codependent.microshopping.order.processor.repository.OrderDao;
import com.codependent.microshopping.order.processor.stream.OrderConsumer;
import com.codependent.microshopping.order.processor.utils.OrikaObjectMapper;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderConsumer orderProducer;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Override
	public Order createOrder(Order order) {
		OrderEntity orderEntity = mapper.map(order, OrderEntity.class);
		return mapper.map(orderEntity, Order.class);
	}

}
