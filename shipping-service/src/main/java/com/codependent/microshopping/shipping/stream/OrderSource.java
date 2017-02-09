package com.codependent.microshopping.shipping.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface OrderSource{
	
	final String INPUT = "shippingRequests";
	
	@Input(OrderSource.INPUT)
	MessageChannel input();
	
}
