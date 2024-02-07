package com.store.phonebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication
@EnableR2dbcAuditing
@ComponentScan({"com.store.phonebank.controller", "com.store.phonebank.services", "com.store.phonebank.repository"})
public class PhonebankApplication {
	public static void main(String[] args) {
		SpringApplication.run(PhonebankApplication.class, args);
	}
}
