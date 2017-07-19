package com.codependent.stream.kafka;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codependent.stream.entity.Message;

@Component
public class MessagingServiceListener {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private KafkaMessagingService messagingService;
	
	@Autowired
	private MessageSender messageSender;
	
	@Scheduled(fixedDelay=5000)
	public void checkMessages(){
		List<Message> pending = messagingService.getPendingMessages();
		logger.info("Pending messages [{}]", pending);
		for (Message message : pending) {
			messageSender.send(message, 500);
		}
	}
	
}
