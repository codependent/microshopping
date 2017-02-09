package com.codependent.microshopping.payment.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderSource{
	
	final String INPUT = "paymentRequests";
	final String OUTPUT = "shippingRequests";
	
	@Input(OrderSource.INPUT)
	MessageChannel input();
	
	@Output(OrderSource.OUTPUT)
	MessageChannel output();
}
