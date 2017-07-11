package com.codependent.microshopping.order.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.codependent.microshopping.order.processor.entity.OrderEntity;
import com.codependent.microshopping.order.processor.repository.OrderDao;
import com.codependent.microshopping.order.processor.stream.OrderConsumer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackageClasses=OrderDao.class)
@EntityScan(basePackageClasses=OrderEntity.class)
@EnableBinding(OrderConsumer.class)
public class OrderProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderProcessorApplication.class, args);
	}
}
