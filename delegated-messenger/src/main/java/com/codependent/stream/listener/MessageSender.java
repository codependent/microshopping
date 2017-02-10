package com.codependent.stream.listener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;
	
	@Autowired
	private MessagingService messagingService;
	
	@SuppressWarnings("unused")
	@Async
	public void sendMessage(Message message){
		ListenableFuture<SendResult<Integer, String>> delivery = kafkaTemplate.send(message.getTopic(), message.getMessage());
		try {
			SendResult<Integer, String> result = delivery.get(500, TimeUnit.MILLISECONDS);
			messagingService.removeMessage(message.getId());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}
