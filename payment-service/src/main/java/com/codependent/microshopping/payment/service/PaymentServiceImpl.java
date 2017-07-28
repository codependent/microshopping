package com.codependent.microshopping.payment.service;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;
import org.springframework.stereotype.Service;

import com.codependent.microshopping.payment.stream.KStreamsConfig;

@Service
public class PaymentServiceImpl implements PaymentService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private KStreamBuilderFactoryBean kStreamBuilderFactoryBean;
	
	
	@Override
	public boolean alreadyPayed(Integer orderId){
		String payed = "false";
		KafkaStreams streams = kStreamBuilderFactoryBean.getKafkaStreams();
		try{
			ReadOnlyKeyValueStore<Integer, String> keyValueStore =
		    streams.store(KStreamsConfig.PAYMENTS_STORE, QueryableStoreTypes.keyValueStore());
			payed = keyValueStore.get(orderId);
		}catch(InvalidStateStoreException iese){}
		return payed == null ? Boolean.FALSE : Boolean.valueOf(payed);
	}
	
	@Override
	public boolean pay(Integer orderId){
		return (Math.random() < 0.8);
	}
	
}
