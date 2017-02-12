package com.codependent.stream;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.codependent.stream.dao.MessageDao;
import com.codependent.stream.entity.Message;

@Configuration
@EnableScheduling
@EnableAsync
@EnableJpaRepositories(basePackageClasses=MessageDao.class)
@EntityScan(basePackageClasses=Message.class)
@ComponentScan(basePackageClasses=DelegatedMessagingConfig.class)
public class DelegatedMessagingConfig {

	@Bean
	public Map<String, Object> producerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
	    return props;
	}
	
	@Bean
	public ProducerFactory<Integer, byte[]> producerFactory() {
	    return new DefaultKafkaProducerFactory<>(producerConfigs());
	}
	
	@Bean
	public KafkaTemplate<Integer, byte[]> kafkaTemplate() {
	    return new KafkaTemplate<Integer, byte[]>(producerFactory());
	}
	
}
