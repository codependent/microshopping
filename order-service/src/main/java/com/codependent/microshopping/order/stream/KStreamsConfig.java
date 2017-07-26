package com.codependent.microshopping.order.stream;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;

import com.codependent.microshopping.order.stream.utils.JsonSerde;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Configuration
public class KStreamsConfig {

	private static final String ORDERS_TOPIC = "orders";
	public static final String ORDERS_STORE = "OrdersStore";

	@Autowired
	private OrderProcessor orderProcessor;
	
	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-service-streams");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10);
        //props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        //props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(jsonSerializer, jsonDeserializer).getClass().getName());
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new StreamsConfig(props);
    }
	
	@Bean
	public KStreamBuilderFactoryBean myKStreamBuilder(StreamsConfig streamsConfig) {
	    return new KStreamBuilderFactoryBean(streamsConfig);
	}
	
	@Bean
	public KTable<?, ?> kTable(KStreamBuilder kStreamBuilder, KStreamBuilderFactoryBean kStreamBuilderFactoryBean) {
        KTable<Integer, JsonNode> table = kStreamBuilder.<Integer, JsonNode>table(ORDERS_TOPIC, ORDERS_STORE);
        table.filter( (key, value) ->{
        	System.out.println(key);
        	System.out.println(value);
        	return true;
        });
	    table.print();
	    return table;
	}
	
}
