package com.codependent.microshopping.payment.dto;

public class Payment {

	private Integer id;
	
	private Integer orderId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", orderId=" + orderId + "]";
	}
	
}
