package com.codependent.microshopping.product.stream.transformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import com.codependent.microshopping.product.stream.KStreamsConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductStockCountTransformer implements Transformer<Integer, JsonNode, KeyValue<Integer, JsonNode>>{

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
		if (eventName.equals("ProductAdded") || 
			eventName.equals("ProductRemoved"))  {
			updateStockStore(event, store);
            return KeyValue.pair(null, null);
		} else {
			return null;
		}
	}
	
	private int updateStockStore(JsonNode event, KeyValueStore<Integer, Integer> store){
		Integer productId = event.get("productId").asInt();
		Integer quantity = event.get("quantity").asInt();
		Integer stock = store.get(productId);
		Integer updatedStock = stock == null ? 0 : stock + quantity;
		store.put(productId, updatedStock);
		return updatedStock;
	}
	
	@Override
	public KeyValue<Integer, JsonNode> punctuate(long timestamp) {
		return null;
	}

	@Override
	public void close() {
		
	}

}