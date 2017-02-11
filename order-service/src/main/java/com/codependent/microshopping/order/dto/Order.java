package com.codependent.microshopping.order.dto;

import java.io.Serializable;

public class Order implements Serializable{

	private static final long serialVersionUID = -861134353502544185L;
	
	public enum State {
		PENDING_PAYMENT, PAYED, PENDING_SHIPPING, SHIPPED, COMPLETED, CANCELLED_PAYMENT_FAILED, CANCELLED_SERVICE_DOWN;
	}
	
	private Integer id;
	private String productId;
	private String uid; 
	private State state;
	
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
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	
}
