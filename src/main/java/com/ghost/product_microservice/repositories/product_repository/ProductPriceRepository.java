package com.ghost.product_microservice.repositories.product_repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.ProductPrice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductPriceRepository extends ReactiveCrudRepository<ProductPrice, Long> {
    Flux<ProductPrice> findAllByProductId(Long productId);
    Mono <ProductPrice> findByProductId(Long productId);
    Mono<ProductPrice> findByProductIdAndIsActiveTrue(Long productId);
    Mono<Void> deleteAllByProductId(Long productId);
}