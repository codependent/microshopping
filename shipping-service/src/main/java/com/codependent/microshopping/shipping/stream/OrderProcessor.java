package com.codependent.microshopping.shipping.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import com.codependent.microshopping.stream.Topic;

public interface OrderProcessor{
	
	final String INPUT 	= Topic.SHIPPING_REQUESTS;
	final String OUTPUT = Topic.SHIPPING_RESULTS;
	
	@Input(OrderProcessor.INPUT)
	MessageChannel input();
	
	@Output(OrderProcessor.OUTPUT)
	MessageChannel output();
	
}
