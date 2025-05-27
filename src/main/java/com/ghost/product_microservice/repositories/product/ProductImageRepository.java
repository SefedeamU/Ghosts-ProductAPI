package com.ghost.product_microservice.repositories.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.ProductImage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductImageRepository extends ReactiveCrudRepository<ProductImage, Long> {
    Flux<ProductImage> findAllByProductId(Long productId);
    Mono<Void> deleteAllByProductId(Long productId);
}