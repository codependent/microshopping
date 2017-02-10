package com.codependent.microshopping.payment.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import com.codependent.microshopping.stream.Topic;

public interface OrderProcessor{
	
	final String INPUT 				= Topic.PAYMENT_REQUESTS;
	final String OUTPUT_PAYMENT 	= Topic.PAYMENT_RESULTS;
	final String OUTPUT_SHIPPING 	= Topic.SHIPPING_REQUESTS;
	
	@Input(INPUT)
	MessageChannel input();
	
	@Output(OUTPUT_PAYMENT)
	MessageChannel outputPayment();
	
	@Output(OUTPUT_SHIPPING)
	MessageChannel outputShipping();
}
