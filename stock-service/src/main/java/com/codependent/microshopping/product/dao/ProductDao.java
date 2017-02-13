package com.codependent.microshopping.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codependent.microshopping.product.entity.ProductEntity;

public interface ProductDao extends JpaRepository<ProductEntity, Integer>{

}
