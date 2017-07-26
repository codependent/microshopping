package com.codependent.microshopping.order.service;

import java.util.List;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.dto.Order.State;

public interface OrderService {

	Order createOrder(Order order);
	Order getOrder(int id);
	List<Order> getAll(State state);
	void requestPayment(Order order);
}
