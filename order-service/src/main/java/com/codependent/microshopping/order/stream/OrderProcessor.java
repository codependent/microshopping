package com.codependent.microshopping.order.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import com.codependent.microshopping.stream.Channel;

public interface OrderProcessor{
	
	String OUTPUT = Channel.ORDERS_OUT;
	String INPUT = Channel.ORDERS_IN;
	
	@Output(OrderProcessor.OUTPUT)
	MessageChannel output();
	
	@Input(OrderProcessor.INPUT)
	MessageChannel input();
	
}
