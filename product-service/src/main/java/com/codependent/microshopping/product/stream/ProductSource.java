package com.codependent.microshopping.product.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


public interface ProductSource{
	
	final String OUTPUT = "productsOut";
	@Output(ProductSource.OUTPUT)
	MessageChannel output();
	
}
