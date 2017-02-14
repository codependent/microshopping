package com.codependent.microshopping.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codependent.microshopping.product.entity.ReservationEntity;

public interface ReservationDao extends JpaRepository<ReservationEntity, Integer>{

	public ReservationEntity findByOrderIdAndProductId(Integer orderId, Integer productId);
	
}
