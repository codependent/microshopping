package com.codependent.microshopping.payment.dto;

import java.io.Serializable;

public class Order implements Serializable{

	private static final long serialVersionUID = -861134353502544185L;

	public enum State {
		PENDING_PRODUCT_RESERVATION,
		PRODUCT_RESERVED,
		REQUEST_PAYMENT, 
		PAYMENT_SUCCESSFUL,
		PAYMENT_FAILED,
		CANCEL_PRODUCT_RESERVATION,
		PRODUCT_RESERVATION_CANCELLED,
		REQUEST_SHIPPING, 
		SHIPPING_REQUESTED, 
		COMPLETED, 
		CANCELLED_NO_STOCK,
		CANCELLED_PAYMENT_FAILED, 
		CANCELLED_SERVICE_DOWN;
	}
	
	private Integer id;
	private Integer productId;
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
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	@Override
	public String toString() {
		return "Order [id=" + id + ", productId=" + productId + ", uid=" + uid + ", state=" + state + "]";
	}
	
}
