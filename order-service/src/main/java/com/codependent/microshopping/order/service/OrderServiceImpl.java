package com.codependent.microshopping.order.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.dto.Order.State;
import com.codependent.microshopping.order.entity.OrderEntity;
import com.codependent.microshopping.order.repository.OrderDao;
import com.codependent.microshopping.order.utils.OrikaObjectMapper;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Override
	public Order createOrder(Order order) {
		OrderEntity orderEntity = mapper.map(order, OrderEntity.class);
		orderEntity = orderDao.save(orderEntity);
		return mapper.map(orderEntity, Order.class);
	}

	@Override
	public List<Order> getAll(State state) {
		List<OrderEntity> orders = orderDao.findByState(state);
		return mapper.map(orders, Order.class);
	}

	
	
}
