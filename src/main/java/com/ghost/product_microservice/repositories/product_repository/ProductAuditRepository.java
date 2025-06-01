package com.ghost.product_microservice.repositories.product_repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.ghost.product_microservice.models.ProductAudit;
import reactor.core.publisher.Flux;

public interface ProductAuditRepository extends ReactiveCrudRepository<ProductAudit, Long> {
    Flux<ProductAudit> findAllByProductsId(Long productsId);
    Flux<ProductAudit> findByProductId(Long productId);
}