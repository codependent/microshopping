package com.codependent.stream.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codependent.stream.entity.Message;
import com.codependent.stream.service.MessagingService;

@Component
public class MessagingServiceListener {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MessagingService messagingService;
	
	@Autowired
	private MessageSender messageSender;
	
	@Scheduled(fixedDelay=30000)
	public void checkMessages(){
		List<Message> pending = messagingService.getMessages("PENDING");
		logger.info("[{}] pending messages", pending.size());
		for (Message message : pending) {
			messageSender.sendMessage(message);
		}
	}
	
}
