package com.ghost.product_microservice.repositories.product_repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Mono<Product> findByName(String name);

    Flux<Product> findAllByCategoryId(Long categoryId);
    Flux<Product> findAllBySubcategoryId(Long subcategoryId);
}