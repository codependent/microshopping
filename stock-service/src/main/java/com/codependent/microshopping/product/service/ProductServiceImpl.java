package com.codependent.microshopping.product.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.product.dao.ProductDao;
import com.codependent.microshopping.product.dto.Order;
import com.codependent.microshopping.product.dto.Order.State;
import com.codependent.microshopping.product.dto.Product;
import com.codependent.microshopping.product.dto.SearchCriteria;
import com.codependent.microshopping.product.entity.ProductEntity;
import com.codependent.microshopping.product.stream.OrderProcessor;
import com.codependent.microshopping.product.utils.OrikaObjectMapper;
import com.codependent.stream.service.MessagingService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private MessagingService messagingService;

	@Autowired
	private OrikaObjectMapper mapper;
	
	@Autowired
	private OrderProcessor orderProcessor;
	
	public Product getProduct(int id){
		return mapper.map(productDao.findOne(id), Product.class);
	}
	public List<Product> getProducts(){
		return mapper.map(productDao.findAll(), Product.class);
	}
	@Override
	public List<Product> searchProducts(SearchCriteria criteria) {
		return mapper.map(productDao.findAll(), Product.class);
	}
	
	/**
	 * TODO add optimistic locking
	 */
	@Override
	public void reserveProduct(Order order){
		ProductEntity productEntity = productDao.findOne( order.getProductId() );
		if( productEntity.getStock() > 0 ){
			productEntity.setStock(productEntity.getStock()-1);
			productDao.save(productEntity);
			order.setState(State.PRODUCT_RESERVED);
			messagingService.createPendingMessage("orders", order, order.getState().name());
		}else{
			order.setState(State.CANCELLED_NO_STOCK);
			orderProcessor.output().send(MessageBuilder.withPayload(order).build());
		}
	}
	
	/**
	 * TODO add optimistic locking
	 */
	@Override
	public void cancelReservation(Order order){
		ProductEntity productEntity = productDao.findOne( order.getProductId() );
		productEntity.setStock(productEntity.getStock() +1 );
		productDao.save(productEntity);
		order.setState(State.PRODUCT_RESERVED);
		messagingService.createPendingMessage("orders", order, order.getState().name());
	}
}
