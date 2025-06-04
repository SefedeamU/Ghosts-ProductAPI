package com.ghost.product_microservice.integration_test.product_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.products_dto.*;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.CreateProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.CreateProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.CreateProductPriceDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerAdminTest {

    @Autowired
    private WebTestClient webTestClient;

    private static Long createdProductId;

    private static final String ADMIN_IP = "127.0.0.1";

    private FinalProductCreateDTO buildProductDTO(String name, String brand, Long categoryId, Long subcategoryId) {
        CreateProductDTO product = new CreateProductDTO();
        product.setName(name);
        product.setBrand(brand);
        product.setCategoryId(categoryId);
        product.setSubcategoryId(subcategoryId);
        product.setDescription("A test product");
        product.setStock(10);
        product.setStatus("ACTIVE");
        product.setUser("adminUser");

        CreateProductPriceDTO price = new CreateProductPriceDTO();
        price.setPrice(new BigDecimal("99.99"));
        price.setPriceCurrency("USD");

        CreateProductImageDTO image = new CreateProductImageDTO();
        image.setUrlImg("https://example.com/image.jpg");
        image.setPriority(1);

        CreateProductAttributeDTO attr = new CreateProductAttributeDTO();
        attr.setAttributeName("color");
        attr.setAttributeValue("red");

        FinalProductCreateDTO dto = new FinalProductCreateDTO();
        dto.setProduct(product);
        dto.setPrice(price);
        dto.setImages(Optional.of(List.of(image)));
        dto.setAttributes(Optional.of(List.of(attr)));
        return dto;
    }

    @Test
    @Order(1)
    void createProduct_shouldPersistAndReturnProduct() {
        FinalProductCreateDTO dto = buildProductDTO("TestProduct", "TestBrand", 1L, 1L);

        webTestClient.post()
                .uri("/products/admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getProduct());
                    Assertions.assertEquals("TestProduct", response.getProduct().getName());
                    Assertions.assertEquals("TestBrand", response.getProduct().getBrand());
                    Assertions.assertEquals(1L, response.getProduct().getCategoryId());
                    Assertions.assertEquals(1L, response.getProduct().getSubcategoryId());
                    Assertions.assertEquals("ACTIVE", response.getProduct().getStatus());
                    Assertions.assertNotNull(response.getPrice());
                    Assertions.assertEquals(new BigDecimal("99.99"), response.getPrice().getPrice());
                    createdProductId = Long.valueOf(response.getProduct().getId());
                });
    }

    @Test
    @Order(2)
    void updateProduct_shouldUpdateAndReturnProduct() {
        FinalProductCreateDTO dto = buildProductDTO("UpdatedProduct", "UpdatedBrand", 1L, 1L);

        webTestClient.put()
                .uri("/products/admin/{id}", createdProductId)
                .header("X-Forwarded-For", ADMIN_IP)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getProduct());
                    Assertions.assertEquals("UpdatedProduct", response.getProduct().getName());
                    Assertions.assertEquals("UpdatedBrand", response.getProduct().getBrand());
                });
    }

    @Test
    @Order(3)
    void patchProduct_shouldPatchAndReturnProduct() {
        FinalProductPatchDTO patchDTO = new FinalProductPatchDTO();
        var patchProduct = new com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.PatchProductDTO();
        patchProduct.setName("PatchedProduct");
        patchDTO.setProduct(patchProduct);

        var patchPrice = new CreateProductPriceDTO();
        patchPrice.setPrice(new BigDecimal("79.99"));
        patchPrice.setPriceCurrency("USD");
        patchDTO.setPrice(patchPrice);

        webTestClient.patch()
                .uri("/products/admin/{id}", createdProductId)
                .header("X-Forwarded-For", ADMIN_IP)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(patchDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getProduct());
                    Assertions.assertEquals("PatchedProduct", response.getProduct().getName());
                    Assertions.assertEquals(new BigDecimal("79.99"), response.getPrice().getPrice());
                });
    }

    @Test
    @Order(4)
    void getProductWithAdminDetailsById_shouldReturnProduct() {
        webTestClient.get()
                .uri("/products/admin/{id}", createdProductId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getProduct());
                    Assertions.assertEquals(createdProductId, Long.valueOf(response.getProduct().getId()));
                });
    }

    @Test
    @Order(5)
    void getProductWithAdminDetailsByName_shouldReturnProduct() {
        webTestClient.get()
                .uri("/products/admin/by-name/{name}", "PatchedProduct")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getProduct());
                    Assertions.assertEquals("PatchedProduct", response.getProduct().getName());
                });
    }

    @Test
    @Order(6)
    void listProductsWithAdminDetails_shouldReturnList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/admin")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(7)
    void listProductsWithAdminDetailsByCategory_shouldReturnList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/admin/by-category/{categoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(1L))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(8)
    void listProductsWithAdminDetailsBySubCategory_shouldReturnList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/admin/by-subcategory/{subCategoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(1L))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(9)
    void deleteProduct_shouldDeleteAndReturnNoContent() {
        webTestClient.delete()
                .uri("/products/admin/{id}", createdProductId)
                .header("X-Forwarded-For", ADMIN_IP)
                .exchange()
                .expectStatus().isNoContent();
    }
}