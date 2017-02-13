package com.codependent.stream.listener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.EmbeddedHeadersMessageConverter;
import org.springframework.cloud.stream.binder.MessageValues;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.codependent.stream.entity.Message;
import com.codependent.stream.service.MessagingService;

@Component
public class MessageSender {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private KafkaTemplate<Integer, byte[]> kafkaTemplate;
	
	@Autowired
	private MessagingService messagingService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonConverter;
	
	private EmbeddedHeadersMessageConverter converter = new EmbeddedHeadersMessageConverter();
	
	@SuppressWarnings("unused")
	@Async
	public void send(Message msg, int timeout){
		try {
			ListenableFuture<SendResult<Integer, byte[]>> delivery = kafkaTemplate.send(msg.getTopic(), prepareMessage(msg));
			logger.info("Sending message to topic {} - id {} - content {}", msg.getTopic(), msg.getId(), msg.getMessage());
			SendResult<Integer, byte[]> result = delivery.get(timeout, TimeUnit.MILLISECONDS);
			messagingService.removeMessage(msg.getId());
		} catch (Exception e) {
			logger.error("Error sending message to topic {} - id {} - content {} - error: {}", msg.getTopic(), msg.getId(), msg.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private byte[] prepareMessage(Message msg) throws Exception{
		Map<String, Object> map = jacksonConverter.getObjectMapper().readValue(msg.getMessage(), Map.class);
		org.springframework.messaging.Message<byte[]> message = MessageBuilder.withPayload(msg.getMessage().getBytes())
				.setHeader(MessageHeaders.CONTENT_TYPE, "application/json")
				.setHeader("idempotencyKey", map.get("id")+"-"+msg.getState()).build();
		MessageValues messageValues = new MessageValues(message);
		byte[] fullPayload = converter.embedHeaders(messageValues, new String[]{MessageHeaders.CONTENT_TYPE, "idempotencyKey"});
		return fullPayload;
	}

}
