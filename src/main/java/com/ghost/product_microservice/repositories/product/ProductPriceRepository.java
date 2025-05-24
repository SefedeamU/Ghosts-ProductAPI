package com.ghost.product_microservice.repositories.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.ProductPrice;
import reactor.core.publisher.Flux;

public interface ProductPriceRepository extends ReactiveCrudRepository<ProductPrice, Long> {
    Flux<ProductPrice> findAllByProductId(Long productId);
}