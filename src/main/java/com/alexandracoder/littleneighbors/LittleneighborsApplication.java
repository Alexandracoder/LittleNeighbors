package com.alexandracoder.littleneighbors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication

public class LittleneighborsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LittleneighborsApplication.class, args);
	}

}

