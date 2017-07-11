package com.codependent.microshopping.order.processor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codependent.microshopping.order.processor.dto.Order.State;
import com.codependent.microshopping.order.processor.entity.OrderEntity;

public interface OrderDao extends JpaRepository<OrderEntity, Integer>{

	List<OrderEntity> findByState(State state);
	
}
