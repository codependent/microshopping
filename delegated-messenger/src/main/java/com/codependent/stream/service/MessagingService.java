package com.codependent.stream.service;

import java.util.List;

import com.codependent.stream.entity.Message;

public interface MessagingService {

	void createPendingMessage(String topic, Object message);
	List<Message> getMessages(String state);
	void removeMessage(Integer id);
	
}
