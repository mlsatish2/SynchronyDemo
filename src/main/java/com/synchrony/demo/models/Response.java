package com.synchrony.demo.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response {
	private String status;
	private String errorMessage;
	private String dbErrorMessage;
	private String cacheErrorMessaage;
	private List<User> userDetails;
	private List<User> cacheResponse;
	private List<User> dbResponse;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<User> getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(List<User> userDetails) {
		this.userDetails = userDetails;
	}

	public List<User> getCacheResponse() {
		return cacheResponse;
	}

	public void setCacheResponse(List<User> cacheResponse) {
		this.cacheResponse = cacheResponse;
	}

	public List<User> getDbResponse() {
		return dbResponse;
	}

	public void setDbResponse(List<User> dbResponse) {
		this.dbResponse = dbResponse;
	}

	public String getDbErrorMessage() {
		return dbErrorMessage;
	}

	public void setDbErrorMessage(String dbErrorMessage) {
		this.dbErrorMessage = dbErrorMessage;
	}

	public String getCacheErrorMessaage() {
		return cacheErrorMessaage;
	}

	public void setCacheErrorMessaage(String cacheErrorMessaage) {
		this.cacheErrorMessaage = cacheErrorMessaage;
	}

}
