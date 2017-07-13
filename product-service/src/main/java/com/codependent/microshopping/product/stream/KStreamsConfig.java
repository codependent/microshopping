package com.codependent.microshopping.product.stream;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;

import com.fasterxml.jackson.databind.JsonNode;

@Configuration
public class KStreamsConfig {

	private static final String STREAMING_TOPIC1 = "orders";

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-service-streams");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        //props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(jsonSerializer, jsonDeserializer).getClass().getName());
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new StreamsConfig(props);
    }
	
	@Bean
	public FactoryBean<KStreamBuilder> myKStreamBuilder(StreamsConfig streamsConfig) {
	    return new KStreamBuilderFactoryBean(streamsConfig);
	}
	
	@Bean
	public KStream<?, ?> kStream(KStreamBuilder kStreamBuilder, KStreamBuilderFactoryBean kStreamBuilderFactoryBean) {

		Serde<Integer> integerSerde = Serdes.Integer();
		final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
		
	    KStream<Integer, JsonNode> stream = kStreamBuilder.stream(integerSerde, jsonSerde, STREAMING_TOPIC1);
	    
	    KGroupedStream<Integer, Integer> quantityStream = stream.map( (key, value) -> {
	    	return new KeyValue<>(value.get("productId").asInt(), value.get("quantity").asInt());
	    }).groupByKey();
	    quantityStream.count("ProductStock");
	    
	    stream.print();
	    return stream;
	}
	
}
