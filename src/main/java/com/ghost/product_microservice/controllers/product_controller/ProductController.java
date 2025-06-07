package com.ghost.product_microservice.controllers.product_controller;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPatchDTO;
import com.ghost.product_microservice.services.product_service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip)) {
            throw new IllegalArgumentException("Client IP address not found in X-Forwarded-For header");
        }
        return ip;
    }

    // ----------- GETS -----------

    @GetMapping
    public Flux<FinalProductPartialDetailDTO> listProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return productService.findAllProducts(page, size);
    }

    @GetMapping("/by-category/{categoryId}")
    public Flux<FinalProductPartialDetailDTO> listProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        if (categoryId == null || categoryId <= 0) throw new IllegalArgumentException("categoryId must be positive");
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return productService.findAllProductsByCategory(categoryId, page, size);
    }

    @GetMapping("/by-subcategory/{subCategoryId}")
    public Flux<FinalProductPartialDetailDTO> listProductsBySubCategory(
        @PathVariable Long subCategoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        if (subCategoryId == null || subCategoryId <= 0) throw new IllegalArgumentException("subCategoryId must be positive");
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return productService.findAllProductsBySubcategory(subCategoryId, page, size);
    }

    @GetMapping("/{id}")
    public Mono<FinalProductPartialDetailDTO> getProductById(@PathVariable Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        return productService.findProductById(id);
    }

    @GetMapping("/by-name/{name}")
    public Mono<FinalProductPartialDetailDTO> getProductsByName(@PathVariable String name) {
        if (!StringUtils.hasText(name)) throw new IllegalArgumentException("name is required");
        return productService.findProductByName(name);
    }

    // ----------- ADMIN GETS -----------

    @GetMapping("/admin")
    public Flux<FinalProductDetailDTO> listProductsWithAdminDetails(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return productService.findAllProductsWithAdminDetails(page, size);
    }

    @GetMapping("/admin/by-category/{categoryId}")
    public Flux<FinalProductDetailDTO> listProductsWithAdminDetailsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        if (categoryId == null || categoryId <= 0) throw new IllegalArgumentException("categoryId must be positive");
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return productService.findAllProductsWithAdminDetailsByCategory(categoryId, page, size);
    }

    @GetMapping("/admin/by-subcategory/{subCategoryId}")
    public Flux<FinalProductDetailDTO> listProductsWithAdminDetailsBySubCategory(
        @PathVariable Long subCategoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        if (subCategoryId == null || subCategoryId <= 0) throw new IllegalArgumentException("subCategoryId must be positive");
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return productService.findAllProductsWithAdminDetailsBySubCategory(subCategoryId, page, size);
    }

    @GetMapping("/admin/{id}")
    public Mono<FinalProductDetailDTO> getProductWithAdminDetailsById(@PathVariable Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        return productService.findProductWithAdminDetailsById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }

    @GetMapping("/admin/by-name/{name}")
    public Mono<FinalProductDetailDTO> getProductWithAdminDetailsByName(@PathVariable String name) {
        if (!StringUtils.hasText(name)) throw new IllegalArgumentException("name is required");
        return productService.findProductWithAdminDetailsByName(name)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }

    // ----------- ADMIN POST/PUT/PATCH/DELETE -----------

    @PostMapping("/admin")
    public Mono<ResponseEntity<FinalProductDetailDTO>> postProduct(
        @Valid @RequestBody FinalProductCreateDTO product,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        String ip = extractClientIp(request);
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");
        return productService.createProduct(product, user, ip)
            .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PutMapping("/admin/{id}")
    public Mono<FinalProductDetailDTO> updateProduct(
        @PathVariable Long id,
        @Valid @RequestBody FinalProductCreateDTO dto,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        return productService.updateProductById(id, dto, user, extractClientIp(request))
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }

    @PatchMapping("/admin/{id}")
    public Mono<FinalProductDetailDTO> patchProduct(
        @PathVariable Long id,
        @RequestBody FinalProductPatchDTO dto,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        return productService.patchProductById(id, dto, user, extractClientIp(request))
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }

    @DeleteMapping("/admin/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(
        @PathVariable Long id,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        return productService.deleteProductById(id, user, extractClientIp(request))
            .map(deleted -> ResponseEntity.noContent().<Void>build())
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }
}