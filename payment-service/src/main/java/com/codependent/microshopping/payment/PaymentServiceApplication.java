package com.codependent.microshopping.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.codependent.microshopping.payment.dao.PaymentDao;
import com.codependent.microshopping.payment.entity.PaymentEntity;
import com.codependent.microshopping.payment.stream.OrderProcessor;
import com.codependent.stream.DelegatedMessagingConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackageClasses=PaymentDao.class)
@EntityScan(basePackageClasses=PaymentEntity.class)
@Import(DelegatedMessagingConfig.class)
@EnableBinding(OrderProcessor.class)
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
}
