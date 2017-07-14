package com.codependent.microshopping.product.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.product.dao.ProductDao;
import com.codependent.microshopping.product.dao.ReservationDao;
import com.codependent.microshopping.product.dto.Order;
import com.codependent.microshopping.product.dto.Order.State;
import com.codependent.microshopping.product.dto.Product;
import com.codependent.microshopping.product.dto.SearchCriteria;
import com.codependent.microshopping.product.entity.ProductEntity;
import com.codependent.microshopping.product.entity.ReservationEntity;
import com.codependent.microshopping.product.stream.OrderProcessor;
import com.codependent.microshopping.product.utils.OrikaObjectMapper;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ReservationDao reservationDao;
	
	@Autowired
	private OrderProcessor orderProcessor;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	@Autowired
	private KStreamBuilderFactoryBean kStreamBuilderFactoryBean;
	
	public Product getProduct(int id){
		return mapper.map(productDao.findOne(id), Product.class);
	}
	
	@Override
	public Product addProduct(Product product){
		Map<String, Object> event = new HashMap<>();
		event.put("name", "ProductCreated");
		event.put("productId", product.getId());
		event.put("quantity", product.getStock());
		event.put("dateAdded", new Date());
		orderProcessor.output().send(MessageBuilder.withPayload(event).build(), 500);
		return product;
	}
	
	@Override
	public List<Product> getProducts(){
		return mapper.map(productDao.findAll(), Product.class);
	}
	@Override
	public List<Product> searchProducts(SearchCriteria criteria) {
		return mapper.map(productDao.findAll(), Product.class);
	}
	
	@Override
	public void reserveProduct(Order order){
		if(getProductStock(order.getProductId()) <= 0) {
			Map<String, Object> event = new HashMap<>();
			event.put("name", "OrderCancelledNoStock");
			event.put("orderId", order.getId());
			event.put("productId", order.getProductId());
			orderProcessor.output().send(MessageBuilder.withPayload(event).build(), 500);
		}else{
			Map<String, Object> event = new HashMap<>();
			event.put("name", "ProductReserved");
			event.put("orderId", order.getId());
			event.put("productId", order.getProductId());
			orderProcessor.output().send(MessageBuilder.withPayload(event).build(), 500);
		}
	}
	
	@Deprecated
	@Override
	public void cancelReservation(Order order){
		Map<String, Object> event = new HashMap<>();
		event.put("name", "OrderCancelled");
		event.put("orderId", order.getId());
		event.put("productId", order.getProductId());
		orderProcessor.output().send(MessageBuilder.withPayload(event).build(), 500);
	}

	@Override
	public Integer getProductStock(Integer id) {
		KafkaStreams streams = kStreamBuilderFactoryBean.getKafkaStreams();
		ReadOnlyKeyValueStore<Integer, Integer> keyValueStore =
	    streams.store("ProductsStock", QueryableStoreTypes.keyValueStore());
		return keyValueStore.get(id);
	}
}
