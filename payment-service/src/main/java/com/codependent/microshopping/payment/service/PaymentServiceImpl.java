package com.codependent.microshopping.payment.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.payment.dao.PaymentDao;
import com.codependent.microshopping.payment.dto.Order;
import com.codependent.microshopping.payment.dto.Order.State;
import com.codependent.microshopping.payment.dto.Payment;
import com.codependent.microshopping.payment.entity.PaymentEntity;
import com.codependent.microshopping.payment.exception.PaymentGatewayException;
import com.codependent.microshopping.payment.utils.OrikaObjectMapper;
import com.codependent.stream.kafka.MessagingService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PaymentDao paymentDao;
	
	@Autowired
	private MessagingService messagingService;
	
	@Autowired
	private OrikaObjectMapper mapper;
	
	public Payment pay(Order order){
		PaymentEntity paymentEntity = paymentDao.findByOrderId(order.getId());
		if(paymentEntity == null){
			logger.info("processing payment for order [{}]", order);
			try {
				processPayment(order);
				paymentEntity = new PaymentEntity();
				paymentEntity.setOrderId(order.getId());
				paymentDao.save(paymentEntity);
				order.setState(State.PAYMENT_SUCCESSFUL);
				logger.info("Payment processed succesfully for order [{}] - Payment entity saved [{}]", order, paymentEntity);
			} catch (PaymentGatewayException e) {
				logger.info("Payment failed for order [{}]", order);
				order.setState(State.PAYMENT_FAILED);
			}
		}else{
			order.setState(State.PAYMENT_SUCCESSFUL);
		}
		messagingService.createPendingMessage("orders", order.getId(), order.getState().name(), order);
		return mapper.map(paymentEntity, Payment.class); 
	}
	
	private void processPayment(Order order) throws PaymentGatewayException{
		if( (Math.random() > 0.8)){
			throw new PaymentGatewayException();
		}
	}
	
}
