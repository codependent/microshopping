package com.codependent.microshopping.product.stream;

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

import com.codependent.microshopping.product.dto.Order;
import com.codependent.microshopping.product.stream.transformer.OrderStockCountTransformer;
import com.codependent.microshopping.product.stream.transformer.ProductStockCountTransformer;
import com.codependent.microshopping.product.stream.utils.JsonSerde;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KStreamsConfig {

	private static final String ORDERS_TOPIC = "orders";
	private static final String PRODUCTS_TOPIC = "products";
	public static final String PRODUCTS_STORE = "ProductsStore";

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-service-streams");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        //props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(jsonSerializer, jsonDeserializer).getClass().getName());
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new StreamsConfig(props);
    }
	
	@Bean
	public KStreamBuilderFactoryBean myKStreamBuilder(StreamsConfig streamsConfig) {
	    return new KStreamBuilderFactoryBean(streamsConfig);
	}
	
	@Bean
	@SuppressWarnings("unchecked")
	public KStream<?, ?> kStream(KStreamBuilder builder, KStreamBuilderFactoryBean kStreamBuilderFactoryBean) {
		KStream<Integer, JsonNode> unvalidatedOrdersStream = builder.stream(ORDERS_TOPIC);
		KStream<Integer, JsonNode> stockStream = builder.stream(PRODUCTS_TOPIC);
		stockStream.print();

		StateStoreSupplier<StateStore> productStore = Stores.create(PRODUCTS_STORE)
			.withKeys(Serdes.Integer())
			.withValues(Serdes.Integer())
			.persistent()
			.build();
		
		builder.addStateStore(productStore);

		unvalidatedOrdersStream.<Integer, JsonNode>transform(() -> new OrderStockCountTransformer(), PRODUCTS_STORE).to(ORDERS_TOPIC);
		
		stockStream.<Integer, JsonNode>transform(() -> new ProductStockCountTransformer(), PRODUCTS_STORE);
		
		return stockStream;
    
	}

}
	

