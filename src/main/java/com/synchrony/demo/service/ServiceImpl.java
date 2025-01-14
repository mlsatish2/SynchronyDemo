package com.synchrony.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.synchrony.demo.models.Request;
import com.synchrony.demo.models.Response;
import com.synchrony.demo.models.User;
import com.synchrony.demo.repository.UserRepository;

@Service
public class ServiceImpl implements IService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ExecutorService executorService;
	@Autowired
	private RedisCacheService cacheService;
	private static final String USER_CACHE_ID_PREFIX = "users::id::";
	private static final String USER_CACHE_EMAIL_PREFIX = "users::email::";
	private static final Logger logger = Logger.getLogger(ServiceImpl.class.getName());

	@Override
	public Response createUser(Request request) throws InterruptedException, ExecutionException {
		Response response = new Response();
		addUser(request.getUserDetails(), response);
		return response;
	}

	@Override
	public Response getUserDetails(Request request) throws InterruptedException, ExecutionException {
		Response response = new Response();
		if (request.getUserDetails().getId() != null) {
			response = fetchUserById(response, request.getUserDetails().getId()).get();
		} else {
			response = fetchUserByEmail(response, request.getUserDetails().getEmail()).get();
		}
		return response;
	}

	@Override
	public Response deleteAllUsers() throws InterruptedException, ExecutionException {
		Response response = new Response();
		deleteAllUsers(response);
		return response;
	}

	public Response deleteAllUsers(Response response) {

		CompletableFuture<Void> dbTask = CompletableFuture.runAsync(() -> {
			userRepository.deleteAll(); // Save to database
			userRepository.resetAutoIncrement();
		}, executorService);
		CompletableFuture<Void> cacheTask = CompletableFuture.runAsync(() -> {
			Set<String> cacheKeys = cacheService.getAllKeys();
			if (cacheKeys != null && !cacheKeys.isEmpty()) {
				cacheService.deleteAllFromCache(cacheKeys);
			}
		}, executorService);
		// Wait for both tasks to complete
		CompletableFuture.allOf(dbTask, cacheTask).join();

		if (userRepository.count() == 0 && redisTemplate.keys("*").isEmpty()) {
			response.setStatus("Cleared all user details in both DB and Redis Cache");
		} else {
			response.setCacheErrorMessaage(
					!redisTemplate.keys("*").isEmpty() ? "Failed to clear all user data in cache" : null);
			response.setDbErrorMessage(
					userRepository.count() != 0 ? "Failed to clear all user data in database" : null);
		}

		return response;
	}

	public CompletableFuture<Response> fetchUserById(Response response, Long userId) {
		return CompletableFuture.supplyAsync(() -> {
			String cacheKey = USER_CACHE_ID_PREFIX + userId;
			User cachedUser = (User) cacheService.getUserDetailsFromCache(cacheKey);
			if (cachedUser != null) {
				List<User> cacheUserList = new ArrayList<>();
				cacheUserList.add(cachedUser);
				response.setCacheResponse(cacheUserList);
				return response;
			}
			User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
			cacheService.saveToCache(cacheKey, user);
			List<User> dbUserList = new ArrayList<>();
			dbUserList.add(user);
			response.setDbResponse(dbUserList);
			return response;
		}, executorService);
	}

	public CompletableFuture<Response> fetchUserByEmail(Response response, String email) {
		return CompletableFuture.supplyAsync(() -> {
			String cacheKey = USER_CACHE_EMAIL_PREFIX + email;
			User cachedUser = (User) cacheService.getUserDetailsFromCache(cacheKey);
			if (cachedUser != null) {
				List<User> cacheUserList = new ArrayList<>();
				cacheUserList.add(cachedUser);
				response.setCacheResponse(cacheUserList);
				return response;
			}
			User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
			cacheService.saveToCache(cacheKey, user);
			List<User> dbUserList = new ArrayList<>();
			dbUserList.add(user);
			response.setDbResponse(dbUserList);
			return response;
		}, executorService);
	}

	/**
	 * This method adds user details in both database and Redis Cache in parallel
	 * and returns saved data from both database and cache.
	 * 
	 * @param user
	 * @param response
	 * @return
	 */
	public Response addUser(User user, Response response) {
		List<User> dbResponse = new ArrayList<>();
		List<User> cacheResponse = new ArrayList<>();
		response.setStatus("Success");
		CompletableFuture<Void> dbTask = CompletableFuture.runAsync(() -> {
			User savedUser = userRepository.save(user); // Save to database
			if (userRepository.existsById(savedUser.getId())) {
				logger.log(Level.INFO, "User saved to database : " + savedUser.toString());
				dbResponse.add(savedUser);
				response.setDbResponse(dbResponse);
			} else {
				response.setDbErrorMessage("User addition to databse failed");
				response.setStatus("failed");
			}
		}, executorService);

		CompletableFuture<Void> cacheTask = CompletableFuture.runAsync(() -> {
			if (user.getId() != null) {
				saveUserToCache(user, response, cacheResponse);

			} else{
				CompletableFuture.allOf(dbTask).join();
				user.setId(response.getDbResponse().get(0).getId());
				saveUserToCache(user, response, cacheResponse);
			}
		}, executorService);
		// Wait for both tasks to complete
		CompletableFuture.allOf(dbTask, cacheTask).join();
		return response;
	}
	
	public Response saveUserToCache(User user, Response response, List<User> cacheResponse) {
		cacheService.saveToCache(USER_CACHE_ID_PREFIX + user.getId(), user); // Add to Redis
		if (redisTemplate.hasKey(USER_CACHE_ID_PREFIX + user.getId())) {
			User cacheUser = cacheService.getUserDetailsFromCache(USER_CACHE_ID_PREFIX + user.getId());
			cacheResponse.add(cacheUser);
			logger.log(Level.INFO, "User saved to Cache : " + cacheUser.toString());
			response.setCacheResponse(cacheResponse);
		} else {
			response.setCacheErrorMessaage("user addition to cache failed");
			response.setStatus("failed");
		}
		return response;
	}

	@Override
	public Response updateUser(Request request) throws InterruptedException, ExecutionException {
		Response response = new Response();
		response = updateUserDetails(response, request.getUserDetails()).get();
		return response;
	}

	private CompletableFuture<Response> updateUserDetails(Response response, User userDetails) {
		List<User> dbResponse = new ArrayList<>();
		List<User> cacheResponse = new ArrayList<>();
		return CompletableFuture.supplyAsync(() -> {
			User savedUser = userRepository.findById(userDetails.getId())
					.orElseThrow(() -> new RuntimeException("User not found"));
			savedUser.setName(userDetails.getName());
			savedUser.setEmail(userDetails.getEmail());
			savedUser.setAge(userDetails.getAge());
			savedUser = userRepository.save(savedUser);
			dbResponse.add(savedUser);
			response.setDbResponse(dbResponse);
			logger.log(Level.INFO, "User saved to database : " + Util.objectToJSON(savedUser));
			cacheService.saveToCache(USER_CACHE_ID_PREFIX + savedUser.getId(), savedUser); // update to Redis
			if (redisTemplate.hasKey(USER_CACHE_ID_PREFIX + savedUser.getId())) {
				User cacheUser = cacheService.getUserDetailsFromCache(USER_CACHE_ID_PREFIX + savedUser.getId());
				cacheResponse.add(cacheUser);
				logger.log(Level.INFO, "User saved to Cache : " + Util.objectToJSON(cacheUser));
				response.setCacheResponse(cacheResponse);
			} else {
				response.setCacheErrorMessaage("user update to cache failed");
				response.setStatus("failed");
			}
			return response;
		}, executorService);

	}

	@Override
	public Response deleteUser(Request request) throws InterruptedException, ExecutionException {
		Response response = new Response();
		deleteUserById(response, request.getUserDetails().getId());
		return response;
	}

	public Response deleteUserById(Response response, Long Id) {
		CompletableFuture<Void> dbTask = CompletableFuture.runAsync(() -> {
			if (userRepository.existsById(Id)) {
				userRepository.deleteById(Id); // Save to database
				logger.log(Level.INFO, "User deleted from DB : "+!userRepository.existsById(Id));
			} else {
				throw new RuntimeException("User not found in DB");
			}
		}, executorService);
		CompletableFuture<Void> cacheTask = CompletableFuture.runAsync(() -> {
			if (redisTemplate.hasKey(USER_CACHE_ID_PREFIX + Id)) {
				cacheService.deleteFromCache(USER_CACHE_ID_PREFIX + Id);
				logger.log(Level.INFO, "User deleted from Cache : "+ !userRepository.existsById(Id));
			} else {
				throw new RuntimeException("User not found in Cache");
			}
		}, executorService);
		// Wait for both tasks to complete
		CompletableFuture.allOf(dbTask, cacheTask).join();
		response.setStatus("Deleted user successfully.");
		return response;

	}

	@Override
	public Response getAllUsers() throws InterruptedException, ExecutionException {
		Response response = new Response();
		getAllUserDetails(response);
		return response;
	}

	private Response getAllUserDetails(Response response) {
		CompletableFuture<Void> cacheTask  = CompletableFuture.runAsync(() -> {
			Set<String> cacheKeys = cacheService.getAllKeys();
			if (!cacheKeys.isEmpty()) {
				logger.log(Level.INFO, "Cache keys found. count is  :" + Util.objectToJSON(cacheKeys));
				List<User>cacheResponse = new ArrayList<>();
				for(String cacheKey : cacheKeys) {
					cacheResponse.add((User) cacheService.getUserDetailsFromCache(cacheKey));
				}
				logger.log(Level.INFO, "User fetched from Cache : "+ Util.objectToJSON(cacheResponse));
				response.setCacheResponse(cacheResponse);
				response.setStatus("SUCCESS");
			}
			else if(userRepository.count() > 0){
				response.setCacheErrorMessaage("No users found!");
				response.setDbResponse(userRepository.findAll());
			}else {
				throw new RuntimeException("Users not found in both Cache and DB");
			}
		}, executorService);
		CompletableFuture.allOf(cacheTask).join();
		return response;
		
	}

}
