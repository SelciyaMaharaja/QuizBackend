package com.example.quizonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class QuizonlineApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(QuizonlineApplication.class, args);
		Environment env = context.getBean(Environment.class);
		System.out.println("Application started " + env.getProperty("server.port"));
	}

}