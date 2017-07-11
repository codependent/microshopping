package com.codependent.microshopping.order.processor.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface OrderConsumer{
	
	final String INPUT = "ordersIn";
	
	@Input(OrderConsumer.INPUT)
	MessageChannel input();
	
}
