package com.codependent.microshopping.order.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderSource{
	
	String OUTPUT = "paymentRequests";
	
	@Output(OrderSource.OUTPUT)
	MessageChannel output();
}
