package com.ghost.product_microservice.repositories.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.ProductAttribute;
import reactor.core.publisher.Flux;

public interface ProductAttributeRepository extends ReactiveCrudRepository<ProductAttribute, Long> {
    Flux<ProductAttribute> findAllByProductId(Long productId);
}