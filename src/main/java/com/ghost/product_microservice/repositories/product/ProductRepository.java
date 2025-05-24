package com.ghost.product_microservice.repositories.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.Product;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}