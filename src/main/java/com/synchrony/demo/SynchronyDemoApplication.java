package com.synchrony.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.synchrony.demo.models")
public class SynchronyDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SynchronyDemoApplication.class, args);
	}

}
