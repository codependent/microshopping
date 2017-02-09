package com.codependent.microshopping.stream.dto;

import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = -3967797569027402545L;
	private String uid;
	
	public User() {}

	public User(String uid) {
		super();
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
}
