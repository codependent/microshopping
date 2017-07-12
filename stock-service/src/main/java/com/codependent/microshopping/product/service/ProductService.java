package com.codependent.microshopping.product.service;

import java.util.List;

import com.codependent.microshopping.product.dto.Order;
import com.codependent.microshopping.product.dto.Product;
import com.codependent.microshopping.product.dto.SearchCriteria;

public interface ProductService {

	Product addProduct(Product product);
	Product getProduct(int id);
	List<Product> getProducts();
	List<Product> searchProducts(SearchCriteria criteria);
	void reserveProduct(Order order);
	void cancelReservation(Order order);
}
