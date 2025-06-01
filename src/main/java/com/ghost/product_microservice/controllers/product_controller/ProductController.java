package com.ghost.product_microservice.controllers.product_controller;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPatchDTO;
import com.ghost.product_microservice.services.product_service.ProductService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    

    @GetMapping
    public Flux<FinalProductPartialDetailDTO> listProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
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
        return productService.findAllProductsBySubcategory(subCategoryId, page, size);
    }
    

    @GetMapping("/{id}")
    public Mono<FinalProductPartialDetailDTO> getProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }

    @GetMapping("/by-name/{name}")
    public Mono<FinalProductPartialDetailDTO> getProductsByName(@PathVariable String name) {
        return productService.findProductByName(name);
    }


    // This endpoint is for admin use only
    @GetMapping("/admin")
    public Flux<FinalProductDetailDTO> listProductsWithAdminDetails(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
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
        return productService.findAllProductsWithAdminDetailsBySubCategory(subCategoryId, page, size);
    }
    

    @GetMapping("/admin/{id}")
    public Mono<FinalProductDetailDTO> getProductWithAdminDetailsById(@PathVariable Long id) {
        return productService.findProductWithAdminDetailsById(id);
    }

    @GetMapping("/admin/by-name/{name}")
    public Mono<FinalProductDetailDTO> WithAdminDetailsByName(@PathVariable String name) {
        return productService.findProductWithAdminDetailsByName(name);
    }


    @PostMapping("/admin")
    public Mono<FinalProductDetailDTO> postProduct(
        @RequestBody FinalProductCreateDTO dto,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return productService.createProduct(dto, user, ip);
    }

    @PutMapping("/admin/{id}")
    public Mono<FinalProductDetailDTO> updateProduct(
        @PathVariable Long id, 
        @RequestBody FinalProductCreateDTO product,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return productService.updateProductById(id, product, user, ip);
    }

    @PatchMapping("/admin/{id}")
    public Mono<FinalProductDetailDTO> patchProduct(
        @PathVariable Long id, 
        @RequestBody FinalProductPatchDTO product,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return productService.patchProductById(id, product, user, ip);
    }

    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<FinalProductDetailDTO> deleteProduct(
        @PathVariable Long id,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return productService.deleteProductById(id, user, ip);
    }
}
