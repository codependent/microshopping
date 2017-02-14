package com.codependent.stream.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Message implements Serializable{
	
	private static final long serialVersionUID = -6647914838219800211L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String topic;
	
	@Column(unique=true)
	private Integer entityId;

	private String state;
	
	private String message;
	
	private boolean removeAfterSending;
	
	public boolean isRemoveAfterSending() {
		return removeAfterSending;
	}

	public void setRemoveAfterSending(boolean removeAfterSending) {
		this.removeAfterSending = removeAfterSending;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", topic=" + topic + ", entityId=" + entityId + ", state=" + state + ", message="
				+ message + ", removeAfterSending=" + removeAfterSending + "]";
	}
	
}
