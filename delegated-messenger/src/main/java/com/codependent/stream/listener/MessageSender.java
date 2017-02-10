package com.codependent.stream.listener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.codependent.stream.entity.Message;
import com.codependent.stream.service.MessagingService;

@Component
public class MessageSender {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;
	
	@Autowired
	private MessagingService messagingService;
	
	@SuppressWarnings("unused")
	@Async
	public void send(Message message, int timeout){
		ListenableFuture<SendResult<Integer, String>> delivery = kafkaTemplate.send(message.getTopic(), message.getMessage());
		try {
			logger.info("Sending message to topic {} - id {} - content {}", message.getTopic(), message.getId(), message.getMessage());
			SendResult<Integer, String> result = delivery.get(timeout, TimeUnit.MILLISECONDS);
			messagingService.removeMessage(message.getId());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.error("Error sending message to topic {} - id {} - content {} - error: {}", message.getTopic(), message.getId(), message.getMessage(), e);
		}
	}
}
