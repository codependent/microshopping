package com.codependent.microshopping.payment.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PaymentSource{
	
	final String OUTPUT = "paymentsOut";
	
	@Output(OUTPUT)
	MessageChannel output();
	
}
