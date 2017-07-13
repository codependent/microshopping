package com.codependent.microshopping.product.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Mapeador de objetos mediante Orika
 * 
 * @author JINGA4X
 * 
 */
@Component("orikaObjectMapper")
public class OrikaObjectMapper{

	@Autowired(required=false)
	private MapperFactory mapperFactory;
	private MapperFacade mapperFacade;
	
	@PostConstruct
	public void initialize(){
		if(mapperFactory == null){
			this.mapperFactory = new DefaultMapperFactory.Builder().build();
		}
		this.mapperFacade = mapperFactory.getMapperFacade();
	}
	
	public <D> D map(Object o, Class<D> clazz) {
		return mapperFacade.map(o, clazz);
	}

	public <D> Collection<D> map(Collection<?> oList, Class<D> clazzDestino) {
		if (oList == null) {
			return null;
		} else {
			List<D> convertedList = new ArrayList<D>();
			for (Object or : oList) {
				convertedList.add(map(or, clazzDestino));
			}
			return convertedList;
		}
	}

	public <D> List<D> map(List<?> oList, Class<D> clazzDestino) {
		if (oList == null) {
			return null;
		} else {
			List<D> convertedList = new ArrayList<D>();
			for (Object or : oList) {
				convertedList.add(map(or, clazzDestino));
			}
			return convertedList;
		}
	}

	public MapperFacade getMapperFacade() {
		return mapperFacade;
	}
	
}
