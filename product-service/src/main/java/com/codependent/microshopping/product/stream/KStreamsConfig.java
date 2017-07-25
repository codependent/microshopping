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
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.ValueJoiner;
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

import com.codependent.microshopping.product.stream.utils.JsonSerde;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KStreamsConfig {

	private static final String ORDERS_TOPIC = "orders";
	private static final String PRODUCTS_TOPIC = "products";
	private static final String PRODUCTS_STORE = "ProductStore";

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
	/*
	@Bean
	public KStream<?, ?> kStream(KStreamBuilder kStreamBuilder, KStreamBuilderFactoryBean kStreamBuilderFactoryBean) {

		final Serde<Integer> integerSerde = Serdes.Integer();
		final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
		
	    KStream<Integer, JsonNode> stream = kStreamBuilder.stream(integerSerde, jsonSerde, STREAMING_TOPIC1);
		    stream.filter( (key, value) -> value != null && value.get("name").asText().equals("ProductAdded"))
		    .map( (key, value) -> {
		    	return new KeyValue<>(value.get("productId").asInt(), value.get("quantity").asInt());
		    }).groupByKey().reduce( (v1, v2) -> v1 + v2, "ProductsStock");
	    
	    stream.print();
	    return stream;
	}*/
	
	@Bean
	@SuppressWarnings("unchecked")
	public KStream<?, ?> kStream2(KStreamBuilder builder, KStreamBuilderFactoryBean kStreamBuilderFactoryBean) {
		final Serde<Integer> integerSerde = Serdes.Integer();
		final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
		
        
		KStream<Integer, JsonNode> unvalidatedOrdersStream = builder.stream(ORDERS_TOPIC);
		KStream<Integer, JsonNode> stockStream = builder.stream(PRODUCTS_TOPIC);
		stockStream.print();

		StateStoreSupplier<StateStore> productStore = Stores.create(PRODUCTS_STORE)
			.withKeys(integerSerde)
			.withValues(integerSerde)
			.persistent()
			.build();
		
		builder.addStateStore(productStore);

		ValueJoiner<JsonNode, JsonNode, JsonNode> valueJoiner = (JsonNode value1, JsonNode value2) -> { 
			if( value1 != null ){
				return value1;
			}else{
				return value2;
			}
		};
		
		KStream<Integer, JsonNode> orderOutputs = unvalidatedOrdersStream.outerJoin(stockStream, valueJoiner,  JoinWindows.of(1000));
		orderOutputs.<Integer, JsonNode>transform(() -> new StockCountTransformer(), PRODUCTS_STORE)
		.filter((key, value) -> {
			return value != null;
		}).to(ORDERS_TOPIC);
		
		return stockStream;
    
	}
	
	public static class StockCountTransformer implements Transformer<Integer, JsonNode, KeyValue<Integer, JsonNode>>{

		private ObjectMapper mapper = new ObjectMapper();
		
		private ProcessorContext context;
		
		@Override
		public void init(ProcessorContext context) {
			this.context = context; 
		}
		
		@Override
		public KeyValue<Integer, JsonNode> transform(Integer key, JsonNode event) {
			String eventName = event.get("name").asText();
			Integer productId = event.get("productId").asInt();
			
			Map<String, String> responseEvent = new HashMap<>();
			responseEvent.put("productId", productId.toString());
			if (eventName.equals("ProductAdded") || 
				eventName.equals("ProductRemoved") || 
				eventName.equals("ProductReserved"))  {
				Integer quantity = event.get("quantity").asInt();
				quantity = updateStockStore(productId, quantity, (KeyValueStore<Integer, Integer>)context.getStateStore(PRODUCTS_STORE));
				responseEvent.put("name", "Nothing");
				responseEvent.put("productId", productId.toString());
				JsonNode jsonNode = mapper.convertValue(responseEvent, JsonNode.class);
                return KeyValue.pair(key, jsonNode);
			}/* else if (event.isOrderRequest()) {
                return KeyValue.pair(key, validateInventory(parseOrderReq(event), productStore))
			} */else {
				return null;
			}
		}

		@Override
		public KeyValue<Integer, JsonNode> punctuate(long timestamp) {
			return null;
		}

		@Override
		public void close() {
			
		}
		
		public int updateStockStore(Integer productId, Integer quantity, KeyValueStore<Integer, Integer> store){
			Integer current = store.get(productId);
			Integer updatedStock = current == null ? 0 : current + quantity;
			store.put(productId, updatedStock);
			return updatedStock;
		}
		
		/*
		public Event validateInventory(OrderRequestEvent order, KeyValueStore<> store){
			Long stockCount = store.get(order.product);
			if (stockCount - order.quantity >= 0) {
				//decrement the value in the store
				store.put(order.product, stockCount - order.amount);
				return new OrderValidatedEvent(Validation.Passed);
			} else {
		           return new OrderValidatedEvent(Validation.Failed);
			}
		}*/

	}

}
	

