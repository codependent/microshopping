package com.codependent.microshopping.payment.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import com.codependent.microshopping.stream.Channel;

public interface OrderProcessor{
	
	final String INPUT 		= Channel.ORDERS_IN;
	final String OUTPUT 	= Channel.ORDERS_OUT;
	
	@Input(INPUT)
	MessageChannel input();
	
	@Output(OUTPUT)
	MessageChannel output();
	
}
