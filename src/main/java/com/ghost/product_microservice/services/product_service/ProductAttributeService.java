package com.ghost.product_microservice.services.product_service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.CreateProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.PatchProductAttributeDTO;
import com.ghost.product_microservice.models.ProductAttribute;
import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.repositories.product_repository.ProductAttributeRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductAuditRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductAttributeService {
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAuditRepository productAuditRepository;

    public ProductAttributeService(ProductAttributeRepository productAttributeRepository,
                                    ProductAuditRepository productAuditRepository) {
        this.productAttributeRepository = productAttributeRepository;
        this.productAuditRepository = productAuditRepository;
    }

    private Mono<Void> logAudit(Long productId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAction(action);
        audit.setUsername(user);
        audit.setEntity("ProductAttribute");
        audit.setDetails(details);
        audit.setDate(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        return productAuditRepository.save(audit).then();
    }

    public Mono<Void> createAttributes(Long productId, List<CreateProductAttributeDTO> attributes, String user, String ip) {
        if (attributes == null || attributes.isEmpty()) {
            return Mono.empty();
        }
        List<ProductAttribute> attributeEntities = attributes.stream().map(dto -> {
            ProductAttribute attr = new ProductAttribute();
            attr.setProductId(productId);
            attr.setAttributeName(dto.getAttributeName());
            attr.setAttributeValue(dto.getAttributeValue());
            return attr;
        }).toList();
        return productAttributeRepository.deleteAllByProductId(productId)
            .thenMany(Flux.fromIterable(attributeEntities).flatMap(productAttributeRepository::save))
            .then(logAudit(productId, "CREATE", user, "Create attributes", ip));
    }

    public Mono<Void> replaceAttributes(Long productId, List<CreateProductAttributeDTO> attributes, String user, String ip) {
        return createAttributes(productId, attributes, user, ip)
            .then(logAudit(productId, "PUT", user, "Replace attributes", ip));
    }

    public Mono<Void> patchAttributes(Long productId, List<PatchProductAttributeDTO> attributes, String user, String ip) {
        if (attributes == null || attributes.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(attributes)
            .flatMap(dto ->
                productAttributeRepository.findByProductIdAndAttributeName(productId, dto.getAttributeName())
                    .flatMap(existingAttr -> {
                        if (dto.getAttributeValue() != null) {
                            existingAttr.setAttributeValue(dto.getAttributeValue());
                        }
                        return productAttributeRepository.save(existingAttr);
                    })
            )
            .then(logAudit(productId, "PATCH", user, "Patch attributes", ip));
    }

    public Mono<Void> deleteAttributes(Long productId, String user, String ip) {
        return logAudit(productId, "DELETE", user, "Delete all attributes", ip)
            .then(productAttributeRepository.deleteAllByProductId(productId));
    }
}