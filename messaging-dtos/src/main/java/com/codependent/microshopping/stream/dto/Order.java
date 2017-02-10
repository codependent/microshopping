package com.codependent.microshopping.stream.dto;

import java.io.Serializable;

public class Order implements Serializable{

	public enum State {
		PENDING, PAYED, SHIPPED, COMPLETED, CANCELLED_PAYMENT_FAILED, CANCELLED_SERVICE_DOWN;
	}
	
	private static final long serialVersionUID = -8611420025441863685L;
	private Integer id;
	private Product product;
	private User user;
	private State state;
	
	public Order() {}
	
	public Order(Integer id, Product product, User user, State state) {
		super();
		this.id = id;
		this.product = product;
		this.user = user;
		this.state = state;
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", product=" + product + ", user=" + user + ", state=" + state + "]";
	}
	
}
