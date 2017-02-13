package com.codependent.microshopping.product;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor;
import org.springframework.integration.selector.MetadataStoreSelector;

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
	
	@Bean
	IdempotentReceiverInterceptor idempotentReceiverInterceptor(){
		return new IdempotentReceiverInterceptor(new MetadataStoreSelector(m -> {
			String idempotencyKey = (String)m.getHeaders().get("idempotencyKey");
			return idempotencyKey != null ? idempotencyKey : ((UUID)m.getHeaders().get("id")).toString();
		}));
	}
}
