package com.forever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ForeverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForeverApplication.class, args);
	}

}
