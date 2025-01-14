package com.synchrony.demo.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synchrony.demo.models.Request;
import com.synchrony.demo.models.Response;
import com.synchrony.demo.service.IService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1")
@Tag(name = "SynchronyDemo")
public class Controller {

	@Autowired
	private IService service;

	@PostMapping("/create/user")
	@Operation(summary = "This endpoint is used to create a new user in DB and add the user in Redis Cache.")
	public ResponseEntity<Response> processCreateRequest(@RequestBody Request request)
			throws InterruptedException, ExecutionException {
		Response registrationResponse = service.createUser(request);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(registrationResponse);
	}

	@PostMapping("/get/user/details")
	@Operation(summary = "This endpoint is used to fetch userdetails from Redis Cache and DB.")
	public ResponseEntity<Response> processGetUserRequest(@RequestBody Request request)
			throws InterruptedException, ExecutionException {
		Response registrationResponse = service.getUserDetails(request);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(registrationResponse);
	}
	
	@GetMapping("/get/all/users")
	@Operation(summary = "This endpoint is used to fetch userdetails from Redis Cache and DB.")
	public ResponseEntity<Response> processGetAllUsersRequest()
			throws InterruptedException, ExecutionException {
		Response registrationResponse = service.getAllUsers();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(registrationResponse);
	}

	@DeleteMapping("/delete/all/users")
	@Operation(summary = "This endpoint is used to delete all userdetails from Redis Cache and DB.")
	public ResponseEntity<Response> processDelteAllRequest() throws InterruptedException, ExecutionException {
		Response registrationResponse = service.deleteAllUsers();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(registrationResponse);
	}

	@DeleteMapping("/delete/user")
	@Operation(summary = "This endpoint is used to delete userdetails from Redis Cache and DB based pn given id/cache key.")
	public ResponseEntity<Response> processDelteUserRequest(@RequestBody Request request)
			throws InterruptedException, ExecutionException {
		Response registrationResponse = service.deleteUser(request);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(registrationResponse);
	}

	@PutMapping("/update/user")
	@Operation(summary = "This endpoint is used to update a userdetail in both DB and Redis Cache.")
	public ResponseEntity<Response> processUpdateRequest(@RequestBody Request request)
			throws InterruptedException, ExecutionException {
		Response registrationResponse = service.updateUser(request);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(registrationResponse);
	}
}
