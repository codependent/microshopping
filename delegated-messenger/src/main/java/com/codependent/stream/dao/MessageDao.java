package com.codependent.stream.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codependent.stream.entity.Message;

public interface MessageDao extends JpaRepository<Message, Integer>{

	List<Message> findByState(String state);
	
}
