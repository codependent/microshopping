package com.codependent.microshopping.order.service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.order.dto.Order;
import com.codependent.microshopping.order.dto.Order.State;
import com.codependent.microshopping.order.entity.OrderEntity;
import com.codependent.microshopping.order.repository.OrderDao;
import com.codependent.microshopping.order.stream.OrderProcessor;
import com.codependent.microshopping.order.utils.OrikaObjectMapper;

@Service
@Transactional
@SuppressWarnings("unused")
public class OrderServiceImpl implements OrderService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderProcessor orderProducer;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Autowired
	private MappingJackson2HttpMessageConverter jackson2MessageConverter;
	
	@Autowired
	private KStreamBuilderFactoryBean kStreamBuilderFactoryBean;
	
	@Override
	public Order createOrder(Order order) {
		order.setState(State.PROCESSING);
		Map<String, Object> event = new HashMap<>();
		event.put("name", "OrderPlaced");
		event.put("orderId", UUID.randomUUID().toString());
		event.put("productId", order.getProductId());
		event.put("uid", order.getUid());
		orderProducer.output().send(MessageBuilder
				.withPayload(event)
				.setHeader(KafkaHeaders.MESSAGE_KEY, ByteBuffer.allocate(4).putInt(order.getProductId()).array())
				.build(), 500);
		return order;
	}

	@Override
	public List<Order> getAll(State state) {
		List<Order> orders = new ArrayList<>();
		
		KafkaStreams streams = kStreamBuilderFactoryBean.getKafkaStreams();
		ReadOnlyKeyValueStore<String, Map<String, String>> keyValueStore =
	    streams.store("OrdersStore", QueryableStoreTypes.keyValueStore());
		
		if(state != null){
			//TODO
		}else{
			KeyValueIterator<String, Map<String, String>> it = keyValueStore.all();
			while(it.hasNext()){
				KeyValue<String, Map<String, String>> next = it.next();
				Order order = jackson2MessageConverter.getObjectMapper().convertValue(next.value, Order.class);
				order.setId(next.key);
				orders.add(order);
			}
		}
		return orders;
	}

	@Override
	public Order getOrder(int id) {
		KafkaStreams streams = kStreamBuilderFactoryBean.getKafkaStreams();
		ReadOnlyKeyValueStore<Integer, Map<String, String>> keyValueStore =
	    streams.store("OrdersStore", QueryableStoreTypes.keyValueStore());
		Map<String, String> orderMap = keyValueStore.get(id);
		return mapper.map(orderMap, Order.class);
	}

}
