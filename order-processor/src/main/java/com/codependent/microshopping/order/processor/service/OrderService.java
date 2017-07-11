package com.codependent.microshopping.order.processor.service;

import java.util.List;

import com.codependent.microshopping.order.processor.dto.Order;
import com.codependent.microshopping.order.processor.dto.Order.State;

public interface OrderService {

	Order createOrder(Order order);
}
