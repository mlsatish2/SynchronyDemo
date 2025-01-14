package com.synchrony.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.demo.models.Request;
import com.synchrony.demo.models.Response;
import com.synchrony.demo.models.User;
import com.synchrony.demo.repository.UserRepository;

@SpringBootTest(classes = SynchronyDemoApplication.class, args = "--spring.config.use-legacy-processing=true",webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("Test")
class SynchronyDemoApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@MockBean
	@Autowired
	private UserRepository userRepository;
	@MockBean
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Mock
    private ValueOperations<String, Object> valueOperations;
	
	@ParameterizedTest
	@CsvSource({"valid-add-user-request.json,valid-add-user-response.json"})
	void testAddUserSuccess(String requestFile,String responseFile) throws JsonMappingException, JsonProcessingException {
		Request request = getJsonRequestAsObject(requestFile);
		when(userRepository.save(any())).thenReturn(getDBResponseAsObject(responseFile));
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		ResponseEntity<Response> response = testRestTemplate.postForEntity("/v1/create/user", request, Response.class);
		 assert response.getStatusCodeValue() == 200;
	     assert response.getBody().getDbResponse().equals(request.getUserDetails());
	     assert response.getBody().getCacheResponse().equals(request.getUserDetails());
	}
	
	Request getJsonRequestAsObject(String fileName) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue("./src/test/resource/json/request"+fileName, Request.class);
		
	}
	
	User getDBResponseAsObject(String fileName) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue("./src/test/resource/json/response"+fileName, User.class);
		
	}

}
