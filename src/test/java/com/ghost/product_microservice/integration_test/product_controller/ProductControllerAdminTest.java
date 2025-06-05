package com.ghost.product_microservice.integration_test.product_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.*;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.CreateProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.CreateProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.CreateProductPriceDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryDetailDTO;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerAdminTest {

    @Autowired
    private WebTestClient webTestClient;

    private static Long createdProductId;
    private static Long categoryId;
    private static Long subCategoryId;

    private static final String ADMIN_IP = "127.0.0.1";

    @BeforeAll
    void setup() {
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO();
        categoryDTO.setName("TestCategory-Admin");
        categoryDTO.setDescription("Categoría de prueba");

        CategoryDetailDTO createdCategory = webTestClient.post()
            .uri("/categories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", "127.0.0.1")
            .bodyValue(categoryDTO)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(createdCategory, "No se pudo crear la categoría");
        categoryId = createdCategory.getId();

        SubCategoryCreateDTO subCategoryDTO = new SubCategoryCreateDTO();
        subCategoryDTO.setName("TestSubCategory");
        subCategoryDTO.setDescription("Subcategoría de prueba");
        subCategoryDTO.setCategoryId(categoryId);

        SubCategoryDetailDTO createdSubCategory = webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .bodyValue(subCategoryDTO)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SubCategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(createdSubCategory, "No se pudo crear la subcategoría");
        subCategoryId = createdSubCategory.getId();
    }

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
        FinalProductCreateDTO dto = buildProductDTO("TestProduct", "TestBrand", categoryId, subCategoryId);

        webTestClient.post()
                .uri("/products/admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .header("X-User", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getProduct());
                    Assertions.assertEquals("TestProduct", response.getProduct().getName());
                    Assertions.assertEquals("TestBrand", response.getProduct().getBrand());
                    Assertions.assertEquals(categoryId, response.getProduct().getCategoryId());
                    Assertions.assertEquals(subCategoryId, response.getProduct().getSubcategoryId());
                    Assertions.assertEquals("ACTIVE", response.getProduct().getStatus());
                    Assertions.assertNotNull(response.getPrice());
                    Assertions.assertEquals(new BigDecimal("99.99"), response.getPrice().getPrice());
                    createdProductId = Long.valueOf(response.getProduct().getId());
                });
    }

    @Test
    @Order(2)
    void updateProduct_shouldUpdateAndReturnProduct() {
        FinalProductCreateDTO dto = buildProductDTO("UpdatedProduct", "UpdatedBrand", categoryId, subCategoryId);

        webTestClient.put()
                .uri("/products/admin/{id}", createdProductId)
                .header("X-Forwarded-For", ADMIN_IP)
                .header("X-User", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> {
                    Assertions.assertNotNull(response.getProduct());
                    Assertions.assertEquals("UpdatedProduct", response.getProduct().getName());
                    Assertions.assertEquals("UpdatedBrand", response.getProduct().getBrand());
                    Assertions.assertEquals(categoryId, response.getProduct().getCategoryId());
                    Assertions.assertEquals(subCategoryId, response.getProduct().getSubcategoryId());
                });
    }

    @Test
    @Order(7)
    void listProductsWithAdminDetailsByCategory_shouldReturnList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/admin/by-category/{categoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(categoryId))
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
                        .build(subCategoryId))
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
                .header("X-User", "admin")
                .exchange()
                .expectStatus().isNoContent();
    }
}