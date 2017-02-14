package com.codependent.microshopping.payment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codependent.microshopping.payment.entity.PaymentEntity;

public interface PaymentDao extends JpaRepository<PaymentEntity, Integer>{

	PaymentEntity findByOrderId(Integer id);

}
