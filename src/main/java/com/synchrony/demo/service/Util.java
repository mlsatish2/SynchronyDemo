package com.synchrony.demo.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
	private static final Logger logger = Logger.getLogger(Util.class.getName());
	private static ObjectMapper  mapper = new ObjectMapper();
	
	public static String objectToJSON(Object obj) {
		String json = null;
		try {
			json = mapper.writeValueAsString(obj);
		}
		catch(JsonProcessingException e){
			logger.log(Level.WARNING, "Warning :"+ e);
		}
		return json;
		
	}
}
