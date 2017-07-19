package com.codependent.stream.kafka;

public interface MessagingService {

	void createPendingMessage(String topic, Integer entityId, String state, Object message);
	
}
