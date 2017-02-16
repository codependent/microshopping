package com.codependent.microshopping.order.dlq.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


public interface OrderProcessor{
	
	final String OUTPUT = "ordersOut";
	final String INPUT = "ordersIn";
	
	@Output(OrderProcessor.OUTPUT)
	MessageChannel output();
	
	@Input(OrderProcessor.INPUT)
	MessageChannel input();
	
}
