package com.ghost.product_microservice.services.product_service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.CreateProductPriceDTO;

import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.models.ProductPrice;
import com.ghost.product_microservice.repositories.product_repository.ProductAuditRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductPriceRepository;

import reactor.core.publisher.Mono;

@Service
public class ProductPriceService {

    private final ProductPriceRepository productPriceRepository;
    private final ProductAuditRepository productAuditRepository;

    public ProductPriceService(ProductPriceRepository productPriceRepository,
                                ProductAuditRepository productAuditRepository) {
        this.productPriceRepository = productPriceRepository;
        this.productAuditRepository = productAuditRepository;
    }

    public Mono<ProductPrice> createPrice(Long productId, CreateProductPriceDTO dto, String user, String ip) {
        return productPriceRepository.findByProductIdAndIsActiveTrue(productId)
            .flatMap(oldPrice -> {
                oldPrice.setIsActive(false);
                oldPrice.setEndDate(LocalDateTime.now());
                return productPriceRepository.save(oldPrice);
            })
            .then(Mono.defer(() -> {
                ProductPrice price = new ProductPrice();
                price.setProductId(productId);
                price.setPrice(dto.getPrice());
                price.setPriceCurrency(dto.getPriceCurrency());
                price.setStartDate(LocalDateTime.now());
                price.setCreationAt(LocalDateTime.now());
                price.setIsActive(true);
                price.setEndDate(null);
                return productPriceRepository.save(price)
                    .flatMap(savedPrice -> logAudit(productId, "CREATE", user, "Create price", ip).thenReturn(savedPrice));
            }));
    }

    public Mono<ProductPrice> updatePrice(Long productId, CreateProductPriceDTO dto, String user, String ip) {
        return productPriceRepository.findByProductIdAndIsActiveTrue(productId)
            .flatMap(oldPrice -> {
                oldPrice.setIsActive(false);
                oldPrice.setEndDate(LocalDateTime.now());
                return productPriceRepository.save(oldPrice);
            })
            .then(Mono.defer(() -> {
                ProductPrice price = new ProductPrice();
                price.setProductId(productId);
                price.setPrice(dto.getPrice());
                price.setPriceCurrency(dto.getPriceCurrency());
                price.setStartDate(LocalDateTime.now());
                price.setCreationAt(LocalDateTime.now());
                price.setIsActive(true);
                price.setEndDate(null);
                return productPriceRepository.save(price)
                    .flatMap(savedPrice -> logAudit(productId, "UPDATE", user, "Update price", ip).thenReturn(savedPrice));
            }));
    }

    public Mono<Void> deleteAllPrices(Long productId, String user, String ip) {
        return productPriceRepository.deleteAllByProductId(productId)
            .then(logAudit(productId, "DELETE", user, "Delete all prices", ip));
    }

    private Mono<Void> logAudit(Long productId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAction(action);
        audit.setUser(user);
        audit.setEntity("ProductPrice");
        audit.setDetails(details);
        audit.setDate(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        return productAuditRepository.save(audit).then();
    }
}