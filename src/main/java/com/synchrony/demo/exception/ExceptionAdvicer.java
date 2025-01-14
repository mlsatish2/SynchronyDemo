package com.synchrony.demo.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.synchrony.demo.models.Response;

@RestControllerAdvice
public class ExceptionAdvicer {
	private static final Logger logger = Logger.getLogger(ExceptionAdvicer.class.getName());
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Response> handleException(Exception ex){
		Response response = new Response();
		logger.log(Level.SEVERE, "Exception occured", ex);
		response.setErrorMessage(ex.getMessage());
		response.setStatus("Failed");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		
	}
}
