package com.codependent.microshopping.payment.stream;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.payment.dto.Order;
import com.codependent.microshopping.payment.service.PaymentService;

@Component
public class OrderListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PaymentSource paymentSource;
	
	@Autowired
	private PaymentService paymentService;
	
	@SuppressWarnings("incomplete-switch")
	@StreamListener(OrderProcessor.INPUT)
	public void handle(Order order){
		switch(order.getState()){
		case REQUEST_PAYMENT:
			logger.info("received payment request for order [{}]", order);
			if(paymentService.pay(order)) {
				Map<String, Object> event = new HashMap<>();
				event.put("name", "PaymentRequest");
				event.put("productId", product.getId());
				event.put("quantity", product.getStock());
				event.put("dateAdded", new Date());
				paymentSource.output().send(MessageBuilder
						.withPayload(event)
						.setHeader(KafkaHeaders.MESSAGE_KEY, ByteBuffer.allocate(4).putInt(product.getId()).array())
						.build(), 500);
			}else{
				
			}
			break;
		}
	}
	
}
