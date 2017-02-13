package com.codependent.microshopping.product.stream;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.annotation.IdempotentReceiver;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.product.dto.Order;
import com.codependent.microshopping.product.service.ProductService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class OrderListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProductService productService;
	
	@Autowired
	private MappingJackson2HttpMessageConverter converter;
	
	@SuppressWarnings("incomplete-switch")
	//@StreamListener(OrderProcessor.INPUT)
	@IdempotentReceiver("idempotentReceiverInterceptor")
	@ServiceActivator(inputChannel = OrderProcessor.INPUT)
	public void handleOrder(Message<String> message) throws JsonParseException, JsonMappingException, IOException{
		if(message.getHeaders().get("duplicateMessage") == null){
			Order order = converter.getObjectMapper().readValue(message.getPayload(), Order.class);
			switch(order.getState()){
			case PENDING_PRODUCT_RESERVATION:
				logger.info("received stock check request for order [{}]", order);
				productService.reserveProduct(order);
				break;
			case CANCEL_PRODUCT_RESERVATION:
				logger.info("payment failed for order [{}] - cancelling product reservation", order);
				productService.cancelReservation(order);
				break;
			}
		}else{
			logger.info("detected duplicated message [{}] - skipping its processing", message);
		}
	}
}
