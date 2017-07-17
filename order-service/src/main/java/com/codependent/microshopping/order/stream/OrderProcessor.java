package com.codependent.microshopping.order.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderProcessor{
	
	final String INPUT = "ordersIn";
	final String OUTPUT = "ordersOut";
	
	@Input(OrderProcessor.INPUT)
	MessageChannel input();
	
	@Output(OrderProcessor.OUTPUT)
	MessageChannel output();
	
}
