package com.ghost.product_microservice.services.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPatchDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.GetProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.GetProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.GetProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.GetProductPriceDTO;
import com.ghost.product_microservice.models.Product;
import com.ghost.product_microservice.models.ProductAttribute;
import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.models.ProductImage;
import com.ghost.product_microservice.models.ProductPrice;
import com.ghost.product_microservice.repositories.product.ProductAttributeRepository;
import com.ghost.product_microservice.repositories.product.ProductAuditRepository;
import com.ghost.product_microservice.repositories.product.ProductImageRepository;
import com.ghost.product_microservice.repositories.product.ProductPriceRepository;
import com.ghost.product_microservice.repositories.product.ProductRepository;

import reactor.core.publisher.Flux;
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

    // Crear un nuevo precio para un producto (desactiva el anterior si existe)
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

    // Actualizar el precio activo de un producto (crea uno nuevo y desactiva el anterior)
    public Mono<ProductPrice> updatePrice(Long productId, ProductCreatePriceDTO dto, String user, String ip) {
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

    // Obtener el precio activo de un producto
    public Mono<ProductPrice> getActivePrice(Long productId) {
        return productPriceRepository.findByProductIdAndIsActiveTrue(productId);
    }

    // Obtener todos los precios (histórico) de un producto
    public Flux<ProductPrice> getAllPrices(Long productId) {
        return productPriceRepository.findAllByProductId(productId);
    }

    // Eliminar todos los precios de un producto
    public Mono<Void> deleteAllPrices(Long productId, String user, String ip) {
        return productPriceRepository.deleteAllByProductId(productId)
            .then(logAudit(productId, "DELETE", user, "Delete all prices", ip));
    }

    // Auditoría
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