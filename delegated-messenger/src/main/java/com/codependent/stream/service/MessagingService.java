package com.codependent.stream.service;

import java.util.List;

import com.codependent.stream.entity.Message;

public interface MessagingService {

	void createPendingMessage(String topic, Integer entityId, String state, Object message);
	List<Message> getPendingMessages();
	void removeMessage(Integer id);
	
}
