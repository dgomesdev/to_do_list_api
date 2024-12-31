package com.dgomesdev.to_do_list_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication
public class ToDoListApiApplication {

	@Value("${spring.mail.host}")
	private static String mailHost;

	public static void main(String[] args) {
		System.out.println(mailHost);
		SpringApplication.run(ToDoListApiApplication.class, args);
	}

}
