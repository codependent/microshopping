package com.codependent.microshopping.payment.stream;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;

import com.codependent.microshopping.payment.dto.Order;
import com.codependent.microshopping.payment.stream.transformer.OrderPaymentTransformer;
import com.codependent.microshopping.payment.stream.utils.JsonSerde;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KStreamsConfig {

	private static final String ORDERS_TOPIC = "orders";
	private static final String PAYMENTS_TOPIC = "payments";
	public static final String PAYMENTS_STORE = "PaymentsStore";

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "payment-service-streams");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new StreamsConfig(props);
    }
	
	@Bean
	public KStreamBuilderFactoryBean myKStreamBuilder(StreamsConfig streamsConfig) {
	    return new KStreamBuilderFactoryBean(streamsConfig);
	}
	
	@Bean
	@SuppressWarnings("unchecked")
	public KStream<?, ?> kStream(KStreamBuilder builder, KStreamBuilderFactoryBean kStreamBuilderFactoryBean, OrderPaymentTransformer orderPaymentTransformer) {
		KStream<Integer, JsonNode> orderPayments = builder.stream(ORDERS_TOPIC);

		StateStoreSupplier<StateStore> paymentsStore = Stores.create(PAYMENTS_STORE)
			.withKeys(Serdes.Integer())
			.withValues(Serdes.String())
			.persistent()
			.build();
		
		builder.addStateStore(paymentsStore);

		orderPayments.<Integer, JsonNode>transform(() -> orderPaymentTransformer, PAYMENTS_STORE)
		.to(ORDERS_TOPIC);
		
		return orderPayments;
    
	}
	
	@Bean
	public OrderPaymentTransformer orderPaymentTransformer(){
		return new OrderPaymentTransformer();
	}

}
	

