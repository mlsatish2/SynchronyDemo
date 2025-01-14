package com.synchrony.demo.service;

import java.util.concurrent.ExecutionException;

import com.synchrony.demo.models.Request;
import com.synchrony.demo.models.Response;

public interface IService {

	Response createUser(Request request) throws InterruptedException, ExecutionException;

	Response getUserDetails(Request request) throws InterruptedException, ExecutionException;

	Response deleteAllUsers() throws InterruptedException, ExecutionException;

	Response updateUser(Request request) throws InterruptedException, ExecutionException;

	Response deleteUser(Request request) throws InterruptedException, ExecutionException;

	Response getAllUsers() throws InterruptedException, ExecutionException;

}
