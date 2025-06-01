package com.ghost.product_microservice.repositories.category_repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.Category;

import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {
    Mono<Category> findCategoryByName(String name);
}