package com.ghost.product_microservice.integration_test.product_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPartialDetailDTO;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.r2dbc.core.DatabaseClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerPublicTest {

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

    private static class TestData {
        Long categoryId;
        Long subCategoryId;
        Long productId;
        String productName;
    }

    private TestData setupTestData(String productName) {
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO();
        categoryDTO.setName("PublicCategory-" + System.nanoTime());
        categoryDTO.setDescription("Categoría pública");

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
        subCategoryDTO.setName("PublicSubCategory-" + System.nanoTime());
        subCategoryDTO.setDescription("Subcategoría pública");
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

        FinalProductPartialDetailDTO createdProduct = webTestClient.post()
            .uri("/products/admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .header("X-User", "admin")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(FinalProductPartialDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(createdProduct, "No se pudo crear el producto");

        TestData data = new TestData();
        data.categoryId = createdCategory.getId();
        data.subCategoryId = createdSubCategory.getId();
        data.productId = createdProduct.getProduct().getId();
        data.productName = productName;
        return data;
    }

    @Test
    @Order(1)
    void listProducts_shouldReturnList() {
        cleanDatabase();
        setupTestData("TestProduct-Public-List");
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductPartialDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(2)
    void listProductsByCategory_shouldReturnList() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-Public-ByCategory");
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/by-category/{categoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(data.categoryId))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductPartialDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(3)
    void listProductsBySubCategory_shouldReturnList() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-Public-BySubCategory");
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/by-subcategory/{subCategoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(data.subCategoryId))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductPartialDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(4)
    void getProductById_shouldReturnProduct() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-Public-ById");
        webTestClient.get()
            .uri("/products/{id}", data.productId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(FinalProductPartialDetailDTO.class)
            .value(response -> Assertions.assertNotNull(response.getProduct()));
    }

    @Test
    @Order(5)
    void getProductsByName_shouldReturnProduct() {
        cleanDatabase();
        TestData data = setupTestData("TestProduct-Public-ByName");
        webTestClient.get()
                .uri("/products/by-name/{name}", data.productName)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductPartialDetailDTO.class)
                .value(response -> Assertions.assertNotNull(response.getProduct()));
    }
}