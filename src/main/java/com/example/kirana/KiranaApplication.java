package com.example.kirana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KiranaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiranaApplication.class, args);
	}

}
