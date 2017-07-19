package com.codependent.microshopping.product.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.codependent.microshopping.product.utils.OrikaObjectMapper;
import com.codependent.stream.kafka.MessagingService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ReservationDao reservationDao;
	
	@Autowired
	private MessagingService messagingService;

	@Autowired
	private OrikaObjectMapper mapper;
	
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
	
	@Override
	public void reserveProduct(Order order){
		ReservationEntity reservationEntity = reservationDao.findByOrderIdAndProductId(order.getId(), order.getProductId());
		if(reservationEntity == null){
			logger.info("reservation entity [{}]", reservationEntity);
			ProductEntity productEntity = productDao.findOne( order.getProductId() );
			if( productEntity.getStock() <= 0 ){
				order.setState(State.CANCELLED_NO_STOCK);
			}else{
				
				reservationEntity = new ReservationEntity();
				reservationEntity.setOrderId(order.getId());
				reservationEntity.setProduct(productEntity);
				reservationDao.save(reservationEntity);
				boolean updateProduct = true;
				while(updateProduct){
					try{
						if(productEntity.getStock()-1 >= 0){
							productEntity.setStock(productEntity.getStock()-1);
							productEntity.getReservations().add(reservationEntity);
							productDao.save(productEntity);
							order.setState(State.PRODUCT_RESERVED);
						}else{
							order.setState(State.CANCELLED_NO_STOCK);
						}
						updateProduct = false;
					}catch (HibernateOptimisticLockingFailureException e) {
						productEntity = productDao.findOne( order.getProductId() );
					}
				}
			}
		}else{
			order.setState(State.PRODUCT_RESERVED);
		}
		messagingService.createPendingMessage("orders", order.getId() , order.getState().name(), order);
	}
	
	@Override
	public void cancelReservation(Order order){
		ReservationEntity reservationEntity = reservationDao.findByOrderIdAndProductId(order.getId(), order.getProductId());
		if(reservationEntity != null){
			logger.info("cancelReservation () - reservation entity [{}]", reservationEntity);
			reservationDao.delete(reservationEntity);
			logger.info("cancelReservation () - reservation entity deleted");
			ProductEntity productEntity = productDao.findOne( order.getProductId() );
			boolean updateProduct = true;
			while(updateProduct){
				try{
					logger.info("trying to update stock of the product");
					productEntity.setStock(productEntity.getStock() +1 );
					productDao.save(productEntity);
					updateProduct = false;
				}catch (HibernateOptimisticLockingFailureException e) {
					productEntity = productDao.findOne( order.getProductId() );
				}
			}
		}
		order.setState(Order.State.PRODUCT_RESERVATION_CANCELLED);
		messagingService.createPendingMessage("orders", order.getId() , Order.State.PRODUCT_RESERVATION_CANCELLED.name(), order);
	}
}
