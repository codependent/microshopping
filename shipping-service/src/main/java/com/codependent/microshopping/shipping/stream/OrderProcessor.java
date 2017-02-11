package com.codependent.microshopping.shipping.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import com.codependent.microshopping.stream.Channel;

public interface OrderProcessor{
	
	final String INPUT 	= Channel.ORDERS_IN;
	final String OUTPUT = Channel.ORDERS_OUT;
	
	@Input(OrderProcessor.INPUT)
	MessageChannel input();
	
	@Output(OrderProcessor.OUTPUT)
	MessageChannel output();
	
}
