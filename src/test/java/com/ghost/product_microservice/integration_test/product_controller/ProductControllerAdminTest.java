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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.r2dbc.core.DatabaseClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerAdminTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    private static final String ADMIN_IP = "127.0.0.1";

    private void cleanDatabase() {
        databaseClient.sql("TRUNCATE TABLE product_audit RESTART IDENTITY CASCADE").then().block();
        databaseClient.sql("TRUNCATE TABLE product RESTART IDENTITY CASCADE").then().block();
        databaseClient.sql("TRUNCATE TABLE subcategory RESTART IDENTITY CASCADE").then().block();
        databaseClient.sql("TRUNCATE TABLE category RESTART IDENTITY CASCADE").then().block();
    }

    private static class TestData {
        Long categoryId;
        Long subCategoryId;
        Long productId;
    }

    private TestData setupTestData(String productName) {
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO();
        categoryDTO.setName("AdminCategory-" + System.nanoTime());
        categoryDTO.setDescription("Categoría admin");

        CategoryDetailDTO createdCategory = webTestClient.post()
            .uri("/categories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .bodyValue(categoryDTO)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(createdCategory, "It was not possible to create the category");

        SubCategoryCreateDTO subCategoryDTO = new SubCategoryCreateDTO();
        subCategoryDTO.setName("AdminSubCategory-" + System.nanoTime());
        subCategoryDTO.setDescription("Subcategoría admin");
        subCategoryDTO.setCategoryId(createdCategory.getId());

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

        Assertions.assertNotNull(createdSubCategory, "It was not possible to create the subcategory");

        FinalProductCreateDTO dto = buildProductDTO(productName, "TestBrand", createdCategory.getId(), createdSubCategory.getId());

        FinalProductDetailDTO createdProduct = webTestClient.post()
            .uri("/products/admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .header("X-User", "admin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(FinalProductDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(createdProduct, "No se pudo crear el producto");

        TestData data = new TestData();
        data.categoryId = createdCategory.getId();
        data.subCategoryId = createdSubCategory.getId();
        data.productId = Long.valueOf(createdProduct.getProduct().getId());
        return data;
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

    @Order(1)
    @Test
    void createProduct_shouldPersistAndReturnProduct() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-Create");
        webTestClient.get()
            .uri("/products/admin/{id}", data.productId)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isOk()
            .expectBody(FinalProductDetailDTO.class)
            .value(response -> Assertions.assertNotNull(response.getProduct()));
    }

    @Order(2)
    @Test
    void updateProduct_shouldUpdateAndReturnProduct() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-Update");
        FinalProductCreateDTO dto = buildProductDTO("UpdatedProduct", "UpdatedBrand", data.categoryId, data.subCategoryId);

        webTestClient.put()
                .uri("/products/admin/{id}", data.productId)
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
                    Assertions.assertEquals(data.categoryId, response.getProduct().getCategoryId());
                    Assertions.assertEquals(data.subCategoryId, response.getProduct().getSubcategoryId());
                });
    }

    @Order(3)
    @Test
    void getProductById_shouldReturnProduct() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-GetById");
        webTestClient.get()
                .uri("/products/admin/{id}", data.productId)
                .header("X-User", "admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductDetailDTO.class)
                .value(response -> Assertions.assertNotNull(response.getProduct()));
    }

    @Order(4)
    @Test
    void listProductsWithAdminDetails_shouldReturnList() {
        cleanDatabase();
        setupTestData("TestProduct-List");
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/admin")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .header("X-User", "admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Order(5)
    @Test
    void listProductsWithAdminDetailsByCategory_shouldReturnList() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-ListByCategory");
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/admin/by-category/{categoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(data.categoryId))
                .header("X-User", "admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Order(6)
    @Test
    void listProductsWithAdminDetailsBySubCategory_shouldReturnList() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-ListBySubCategory");
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/admin/by-subcategory/{subCategoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(data.subCategoryId))
                .header("X-User", "admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Order(7)
    @Test
    void deleteProduct_shouldDeleteAndReturnNoContent() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-Delete");
        webTestClient.delete()
                .uri("/products/admin/{id}", data.productId)
                .header("X-Forwarded-For", ADMIN_IP)
                .header("X-User", "admin")
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/products/admin/{id}", data.productId)
                .header("X-User", "admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .exchange()
                .expectStatus().isNotFound();
    }
}