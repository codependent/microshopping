package com.codependent.stream.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import com.codependent.stream.dao.MessageDao;
import com.codependent.stream.entity.Message;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@Transactional
public class MessagingServiceImpl implements MessagingService{

	@Autowired
	private MessageDao messageDao;
	
	@Autowired
	private MappingJackson2HttpMessageConverter jacksonConverter;
	
	@Override
	public void createPendingMessage(String topic, Object message){
		try {
			String text = jacksonConverter.getObjectMapper().writeValueAsString(message);
			Message messageEntity = new Message();
			messageEntity.setState("PENDING_PAYMENT");
			messageEntity.setTopic(topic);
			messageEntity.setMessage(text);
			messageDao.save(messageEntity);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Message> getMessages(String state) {
		return messageDao.findByState(state);
	}

	@Override
	public void removeMessage(Integer id) {
		messageDao.delete(id);
	}

}
