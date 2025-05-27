package com.ghost.product_microservice.repositories.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.Product;

import org.springframework.lang.NonNull;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Flux<Product> findAllByCategoryId(Long categoryId);
    Flux<Product> findAllBySubcategoryId(Long subcategoryId);
    
    @Override
    @NonNull
    Mono<Product> findById(@NonNull Long id);

}