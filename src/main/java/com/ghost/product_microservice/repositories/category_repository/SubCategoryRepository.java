package com.ghost.product_microservice.repositories.category_repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.ghost.product_microservice.models.SubCategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubCategoryRepository extends ReactiveCrudRepository<SubCategory, Long>{
    Mono<SubCategory> findByName(String name);

    Flux<SubCategory> findAllByCategoryId(Long categoryId);
}
