package com.codependent.microshopping.product.stream.transformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import com.codependent.microshopping.product.dto.Order;
import com.codependent.microshopping.product.stream.KStreamsConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderStockCountTransformer implements Transformer<Integer, JsonNode, KeyValue<Integer, JsonNode>>{

	protected ObjectMapper mapper = new ObjectMapper();
	
	protected ProcessorContext context;
	
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
		KeyValueStore<Integer, Integer> store = (KeyValueStore<Integer, Integer>) context.getStateStore(KStreamsConfig.PRODUCTS_STORE);
		responseEvent.put("productId", productId.toString());
		if (eventName.equals("OrderPlaced")) {
			responseEvent.put("id", event.get("id").asText());
			responseEvent.put("uid", event.get("uid").asText());
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

	private boolean validateInventory(JsonNode event, KeyValueStore<Integer, Integer> store){
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
	
	@Override
	public KeyValue<Integer, JsonNode> punctuate(long timestamp) {
		return null;
	}

	@Override
	public void close() {
		
	}
}