package com.ghost.product_microservice.repositories.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.ProductCategory;
import reactor.core.publisher.Flux;

public interface ProductCategoryRepository extends ReactiveCrudRepository<ProductCategory, Long> {
    Flux<ProductCategory> findAllByProductId(Long productId);
    Flux<ProductCategory> findAllByCategoryId(Long categoryId);
}