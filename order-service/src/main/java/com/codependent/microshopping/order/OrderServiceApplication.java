package com.codependent.microshopping.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.codependent.microshopping.order.entity.OrderEntity;
import com.codependent.microshopping.order.repository.OrderDao;
import com.codependent.microshopping.order.stream.OrderProcessor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackageClasses=OrderDao.class)
@EntityScan(basePackageClasses=OrderEntity.class)
@EnableBinding(OrderProcessor.class)
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
}
