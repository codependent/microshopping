package com.codependent.microshopping.order.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import com.codependent.microshopping.stream.Topic;

public interface OrderProcessor{
	
	String OUTPUT = Topic.PAYMENT_REQUESTS;
	String INPUT_PAYMENT = Topic.PAYMENT_RESULTS;
	String INPUT_SHIPPING = Topic.SHIPPING_RESULTS;
	
	@Output(OrderProcessor.OUTPUT)
	MessageChannel output();
	
	@Input(OrderProcessor.INPUT_PAYMENT)
	MessageChannel inputPayment();
	
	@Input(OrderProcessor.INPUT_SHIPPING)
	MessageChannel inputShipping();
}
