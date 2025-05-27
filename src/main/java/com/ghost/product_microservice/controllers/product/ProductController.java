package com.ghost.product_microservice.controllers.product;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

import com.ghost.product_microservice.controllers.dto.productdto.ProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.productdto.ProductDetailDTO;
import com.ghost.product_microservice.controllers.dto.productdto.ProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.productdto.ProductPatchDTO;
import com.ghost.product_microservice.services.product.ProductService;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    

    @GetMapping
    public Flux<ProductPartialDetailDTO> listProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        return productService.findAllProducts(page, size);
    }

    @GetMapping("/by-category/{categoryId}")
    public Flux<ProductPartialDetailDTO> listProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        return productService.findAllProductsByCategory(categoryId, page, size);
    }

    @GetMapping("/by-subcategory/{subCategoryId}")
    public Flux<ProductPartialDetailDTO> listProductsBySubCategory(
        @PathVariable Long subCategoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        return productService.findAllProductsBySubcategory(subCategoryId, page, size);
    }
    

    @GetMapping("/{id}")
    public Mono<ProductPartialDetailDTO> getProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }

    @GetMapping("/by-name/{name}")
    public Mono<ProductPartialDetailDTO> getProductsByName(@PathVariable String name) {
        return productService.findProductByName(name);
    }


    // This endpoint is for admin use only
    @GetMapping("/admin")
    public Flux<ProductDetailDTO> listProductsWithAdminDetails(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        return productService.findAllProductsWithAdminDetails(page, size);
    }

    @GetMapping("/admin/by-category/{categoryId}")
    public Flux<ProductDetailDTO> listProductsWithAdminDetailsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        return productService.findAllProductsWithAdminDetailsByCategory(categoryId, page, size);
    }

    @GetMapping("/admin/by-subcategory/{subCategoryId}")
    public Flux<ProductDetailDTO> listProductsWithAdminDetailsBySubCategory(
        @PathVariable Long subCategoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice)
    {
        return productService.findAllProductsWithAdminDetailsBySubCategory(subCategoryId, page, size);
    }
    

    @GetMapping("/admin/{id}")
    public Mono<ProductDetailDTO> getProductWithAdminDetailsById(@PathVariable Long id) {
        return productService.findProductWithAdminDetailsById(id);
    }

    @GetMapping("/admin/by-name/{name}")
    public Mono<ProductDetailDTO> WithAdminDetailsByName(@PathVariable String name) {
        return productService.findProductWithAdminDetailsByName(name);
    }


    @PostMapping("/admin")
    public Mono<ProductDetailDTO> postProduct(@RequestBody ProductCreateDTO dto) {
        return productService.createProduct(dto);
    }

    @PutMapping("/admin/{id}")
    public Mono<ProductDetailDTO> updateProduct(@PathVariable Long id, @RequestBody ProductCreateDTO product) {
        return productService.updateProductById(id, product);
    }

    @PatchMapping("/admin/{id}")
    public Mono<ProductDetailDTO> patchProduct(@PathVariable Long id, @RequestBody ProductCreateDTO product) {
        return productService.patchProductById(id, product);
    }

    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }
}
