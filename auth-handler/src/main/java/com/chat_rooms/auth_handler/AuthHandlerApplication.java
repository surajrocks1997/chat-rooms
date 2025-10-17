package com.chat_rooms.auth_handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.chat_rooms.auth_handler.repository.jpa")
@EnableMongoRepositories(basePackages = "com.chat_rooms.auth_handler.repository.mongo")
public class AuthHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthHandlerApplication.class, args);
	}

}
