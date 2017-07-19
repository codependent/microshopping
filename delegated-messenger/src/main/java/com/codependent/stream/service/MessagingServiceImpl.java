package com.codependent.stream.service;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import com.codependent.stream.dao.MessageDao;
import com.codependent.stream.entity.Message;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@Transactional
public class MessagingServiceImpl implements MessagingService{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MessageDao messageDao;
	
	@Autowired
	private MappingJackson2HttpMessageConverter jacksonConverter;
	
	@Override
	public void createPendingMessage(String topic, Integer entityId, String state, Object message){
		try {
			logger.info("creating pending message - topic [{}], entityId [{}], state [{}], message [{}]", 
				topic, entityId, state, message);
			String text = jacksonConverter.getObjectMapper().writeValueAsString(message);
			Message messageEntity = new Message();
			messageEntity.setState(state);
			messageEntity.setEntityId(entityId);
			messageEntity.setTopic(topic);
			messageEntity.setMessage(text);
			messageEntity.setProcessed(false);
			messageEntity = messageDao.save(messageEntity);
			logger.info("created pending message [{}]", messageEntity); 
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch( ConstraintViolationException e){
			
		}
	}
	
	public List<Message> getPendingMessages() {
		return messageDao.findByProcessed(false);
	}

	@Override
	public void markMessageAsProcessed(Integer id) {
		Message message = messageDao.findOne(id);
		logger.info("marking message [{}] as processed", message);
		if(message != null){
			message.setProcessed(true);;
			messageDao.save(message);
		}
		
	}

}
