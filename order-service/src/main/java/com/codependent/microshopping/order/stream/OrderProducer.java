package com.codependent.microshopping.order.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderProducer{
	
	final String OUTPUT = "ordersOut";
	
	@Output(OrderProducer.OUTPUT)
	MessageChannel output();
	
}
