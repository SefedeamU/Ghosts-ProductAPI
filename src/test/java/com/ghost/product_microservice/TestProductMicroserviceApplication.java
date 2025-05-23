package com.ghost.product_microservice;

import org.springframework.boot.SpringApplication;

public class TestProductMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ProductMicroserviceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

