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
	public KStream<?, ?> kStream(KStreamBuilder builder, KStreamBuilderFactoryBean kStreamBuilderFactoryBean) {
		KStream<Integer, JsonNode> unvalidatedOrdersStream = builder.stream(ORDERS_TOPIC);
		KStream<Integer, JsonNode> paymentStream = builder.stream(PAYMENTS_TOPIC);

		StateStoreSupplier<StateStore> paymentsStore = Stores.create(PAYMENTS_TOPIC)
			.withKeys(Serdes.Integer())
			.withValues(Serdes.String())
			.persistent()
			.build();
		
		builder.addStateStore(paymentsStore);

		KStream<Integer, JsonNode> orderOutputs = unvalidatedOrdersStream.outerJoin(paymentStream, 
			(JsonNode value1, JsonNode value2) -> { 
				if( value1 != null ){
					return value1;
				}else{
					return value2;
				}
			}, JoinWindows.of(1000));
		orderOutputs.<Integer, JsonNode>transform(() -> new StockCountTransformer(), PAYMENTS_STORE)
		.filter((key, value) -> {
			return value != null;
		}).to(ORDERS_TOPIC);
		
		return paymentStream;
    
	}
	
	public static class StockCountTransformer implements Transformer<Integer, JsonNode, KeyValue<Integer, JsonNode>>{

		private ObjectMapper mapper = new ObjectMapper();
		
		private ProcessorContext context;
		
		@Override
		public void init(ProcessorContext context) {
			this.context = context; 
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public KeyValue<Integer, JsonNode> transform(Integer key, JsonNode event) {
			String eventName = event.get("name").asText();
			Integer productId = event.get("productId").asInt();
			Map<String, String> responseEvent = new HashMap<>();
			KeyValueStore<Integer, Integer> store = (KeyValueStore<Integer, Integer>) context.getStateStore(PAYMENTS_STORE);
			responseEvent.put("productId", productId.toString());
			if (eventName.equals("ProductAdded") || 
				eventName.equals("ProductRemoved"))  {
				updateStockStore(event, store);
                return KeyValue.pair(null, null);
			} else if (eventName.equals("OrderPlaced")) {
				Integer orderId = event.get("id").asInt();
				responseEvent.put("id", event.get("id").asText());
				if(validateInventory(event, store)){
					responseEvent.put("name", "ProductReserved");
					responseEvent.put("state", Order.State.PRODUCT_RESERVED.name());
				}else{
					responseEvent.put("name", "ProductNoStock");
					responseEvent.put("state", Order.State.PRODUCT_RESERVATION_CANCELLED.name());
				}
				
				JsonNode jsonNode = mapper.convertValue(responseEvent, JsonNode.class);
				return KeyValue.pair(event.get("id").asInt(), jsonNode);
			} else {
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
		
		public int updateStockStore(JsonNode event, KeyValueStore<Integer, Integer> store){
			Integer productId = event.get("productId").asInt();
			Integer quantity = event.get("quantity").asInt();
			Integer stock = store.get(productId);
			Integer updatedStock = stock == null ? 0 : stock + quantity;
			store.put(productId, updatedStock);
			return updatedStock;
		}
		
		public boolean validateInventory(JsonNode event, KeyValueStore<Integer, Integer> store){
			Integer productId = event.get("productId").asInt();
			Integer quantity = 1;
			Integer stockCount = store.get(productId);
			if (stockCount - quantity >= 0) {
				//decrement the value in the store
				store.put(productId, stockCount - quantity);
				return true;
			} else {
		        return false;
			}
		}
	}

}
	

