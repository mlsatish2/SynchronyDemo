package com.synchrony.demo.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class Config {
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// Set String serialization for keys
		template.setKeySerializer(new StringRedisSerializer());

		// Set JSON serialization for values
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		return template;
	}

	@Bean
	public ExecutorService executorService() {
		// Creates a fixed thread pool with 10 threads (adjust as needed)
		return Executors.newFixedThreadPool(10);
	}
}
