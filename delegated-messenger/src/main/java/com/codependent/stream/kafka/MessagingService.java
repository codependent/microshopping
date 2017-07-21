package com.codependent.stream.kafka;

public interface MessagingService {

	void createMessage(String topic, Integer entityId, String state, Object message);
	
}
