package com.ghost.product_microservice.services.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.PatchProductImageDTO;

import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.models.ProductImage;

import com.ghost.product_microservice.repositories.product.ProductAuditRepository;
import com.ghost.product_microservice.repositories.product.ProductImageRepository;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductAuditRepository productAuditRepository;

    public ProductImageService(ProductImageRepository productImageRepository,
                                ProductAuditRepository productAuditRepository) {
        this.productImageRepository = productImageRepository;
        this.productAuditRepository = productAuditRepository;
    }

    private Mono<Void> logAudit(Long productId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAction(action);
        audit.setUser(user);
        audit.setEntity("ProductImage");
        audit.setDetails(details);
        audit.setDate(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        return productAuditRepository.save(audit).then();
    }

    public Mono<Void> createImages(Long productId, List<CreateProductImageDTO> images, String user, String ip) {
        List<ProductImage> imageEntities = new ArrayList<>();
        List<Integer> usedPriorities = new ArrayList<>();
        if (images != null) {
            if (images.size() > 5) {
                return Mono.error(new IllegalArgumentException("Images cannot exceed 5."));
            }
            for (CreateProductImageDTO dto : images) {
                ProductImage img = new ProductImage();
                img.setProductId(productId);
                img.setUrlImg(dto.getUrlImg());
                img.setPriority(dto.getPriority());
                imageEntities.add(img);
                usedPriorities.add(dto.getPriority());
            }
        }
        String genericUrl = "https://example.com/generic-image.jpg";
        for (int i = 1; i <= 5; i++) {
            if (!usedPriorities.contains(i)) {
                ProductImage img = new ProductImage();
                img.setProductId(productId);
                img.setUrlImg(genericUrl);
                img.setPriority(i);
                imageEntities.add(img);
            }
        }
        return productImageRepository.deleteAllByProductId(productId)
            .thenMany(Flux.fromIterable(imageEntities).flatMap(productImageRepository::save))
            .then(logAudit(productId, "CREATE", user, "Create images", ip));
    }

    public Mono<Void> replaceImages(Long productId, List<CreateProductImageDTO> images, String user, String ip) {
        return createImages(productId, images, user, ip)
            .then(logAudit(productId, "PUT", user, "Replace images", ip));
    }

    public Mono<Void> patchImages(Long productId, List<PatchProductImageDTO> images, String user, String ip) {
        if (images == null || images.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(images)
            .flatMap(dto ->
                productImageRepository.findByProductIdAndPriority(productId, dto.getPriority())
                    .flatMap(existingImage -> {
                        if (dto.getUrlImg() != null) {
                            existingImage.setUrlImg(dto.getUrlImg());
                        }
                        return productImageRepository.save(existingImage);
                    })
            )
            .then(logAudit(productId, "PATCH", user, "Patch images", ip));
    }

    public Mono<Void> deleteImages(Long productId, String user, String ip) {
        return productImageRepository.deleteAllByProductId(productId)
            .then(logAudit(productId, "DELETE", user, "Delete all images", ip));
    }
}
