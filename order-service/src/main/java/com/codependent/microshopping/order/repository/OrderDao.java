package com.codependent.microshopping.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codependent.microshopping.order.dto.Order.State;
import com.codependent.microshopping.order.entity.OrderEntity;

public interface OrderDao extends JpaRepository<OrderEntity, Integer>{

	List<OrderEntity> findByState(State state);
	
}
