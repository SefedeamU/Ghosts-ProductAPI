package com.ghost.product_microservice.repositories.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.ProductAttribute;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductAttributeRepository extends ReactiveCrudRepository<ProductAttribute, Long> {
    Flux<ProductAttribute> findAllByProductId(Long productId);
    Mono<Void> deleteAllByProductId(Long productId);
    Mono<ProductAttribute> findByProductIdAndAttributeName(Long productId, String attributeName);
}