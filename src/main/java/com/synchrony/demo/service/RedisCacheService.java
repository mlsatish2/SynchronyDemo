package com.synchrony.demo.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.synchrony.demo.models.User;

@Service
public class RedisCacheService {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	 public void saveToCache(String key, Object value) {
	        redisTemplate.opsForValue().set(key, value);
	    }

	    public User getUserDetailsFromCache(String key) {
	        return (User)redisTemplate.opsForValue().get(key);
	    }

	    public void deleteFromCache(String key) {
	        redisTemplate.delete(key);
	    }
	    
	    public void deleteAllFromCache(Set<String> key) {
	    	redisTemplate.delete(key);
	    }
	    
	    public Set<String> getAllKeys(){
	    	return redisTemplate.keys("*");
	    }
}
