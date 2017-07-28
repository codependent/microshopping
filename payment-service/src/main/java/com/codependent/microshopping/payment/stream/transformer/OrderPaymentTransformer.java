package com.codependent.microshopping.payment.stream.transformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codependent.microshopping.payment.dto.Order;
import com.codependent.microshopping.payment.service.PaymentService;
import com.codependent.microshopping.payment.stream.KStreamsConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderPaymentTransformer implements Transformer<Integer, JsonNode, KeyValue<Integer, JsonNode>>{

	protected ObjectMapper mapper = new ObjectMapper();
	
	protected ProcessorContext context;
	
	@Autowired
	private PaymentService paymentService;
	
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
		KeyValueStore<Integer, String> store = (KeyValueStore<Integer, String>) context.getStateStore(KStreamsConfig.PAYMENTS_STORE);
		responseEvent.put("productId", productId.toString());
		if (eventName.equals("PaymentRequested")) {
			responseEvent.put("id", event.get("id").asText());
			responseEvent.put("uid", event.get("uid").asText());
			if(!paymentService.alreadyPayed(key)){
				if(paymentService.pay(key)){
					store.put(key, Boolean.TRUE.toString());
					responseEvent.put("name", "PaymentSuccessful");
					responseEvent.put("state", Order.State.PAYMENT_SUCCESSFUL.name());
				}else{
					store.put(key, Boolean.FALSE.toString());
					responseEvent.put("name", "ProductReserved");
					responseEvent.put("state", Order.State.PAYMENT_FAILED.name());
				}
			}else{
				responseEvent.put("name", "PaymentSuccessful");
				responseEvent.put("state", Order.State.PAYMENT_SUCCESSFUL.name());
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
}