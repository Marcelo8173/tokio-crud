package com.marcelo.tokiomarine.tokiomarine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class TokiomarineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokiomarineApplication.class, args);
	}

}
