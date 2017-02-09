package com.codependent.microshopping.stream.dto;

import java.io.Serializable;

public class Order implements Serializable{

	private static final long serialVersionUID = -8611420025441863685L;
	private Product product;
	private User user;
	
	public Order() {}
	
	public Order(Product product, User user) {
		super();
		this.product = product;
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
