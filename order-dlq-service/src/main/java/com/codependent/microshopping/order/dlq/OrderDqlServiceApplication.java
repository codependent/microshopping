package com.codependent.microshopping.order.dlq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

import com.codependent.microshopping.order.dlq.stream.OrderProcessor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding(OrderProcessor.class)
public class OrderDqlServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderDqlServiceApplication.class, args);
	}
}
