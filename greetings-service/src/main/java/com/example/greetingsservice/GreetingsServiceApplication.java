package com.example.greetingsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Collections;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@SpringBootApplication
public class GreetingsServiceApplication {

	@Bean
	RouterFunction<ServerResponse> greetings() {
		return route()
			.GET("/hello", r -> ok().bodyValue(Collections.singletonMap("greetings", "Hello, Bootiful world!")))
			.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GreetingsServiceApplication.class, args);
	}

}
