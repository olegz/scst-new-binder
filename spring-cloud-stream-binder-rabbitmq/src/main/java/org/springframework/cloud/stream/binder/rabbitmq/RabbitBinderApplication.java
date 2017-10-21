package org.springframework.cloud.stream.binder.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitBinderApplication {

	public static void main(String[] args) {
		System.out.println("Executing parent binder");
		SpringApplication.run(RabbitBinderApplication.class, args);
	}
}
