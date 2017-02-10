package com.codependent.microshopping.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

import com.codependent.microshopping.shipping.stream.OrderProcessor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding(OrderProcessor.class)
public class ShippingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShippingServiceApplication.class, args);
	}
}
