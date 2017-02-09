package com.codependent.microshopping.stream.dto;

import java.io.Serializable;

public class Product implements Serializable{

	private static final long serialVersionUID = 1563060971958742396L;
	private String name;
	
	public Product() {}

	public Product(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
