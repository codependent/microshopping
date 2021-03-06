package com.codependent.microshopping.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.codependent.microshopping.product.dao.ProductDao;
import com.codependent.microshopping.product.entity.ProductEntity;
import com.codependent.microshopping.product.stream.OrderProcessor;
import com.codependent.stream.DelegatedMessagingConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackageClasses=ProductDao.class)
@EntityScan(basePackageClasses=ProductEntity.class)
@EnableBinding(OrderProcessor.class)
@Import(DelegatedMessagingConfig.class)
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
}
