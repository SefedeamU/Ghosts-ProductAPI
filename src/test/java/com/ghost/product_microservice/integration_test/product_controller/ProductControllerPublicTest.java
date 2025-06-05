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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerPublicTest {

    @Autowired
    private WebTestClient webTestClient;

    private Long createdProductId;
    private Long categoryId;
    private Long subCategoryId;

    @BeforeAll
    void setup() {
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO();
        categoryDTO.setName("TestCategory-Public");
        categoryDTO.setDescription("Test Category Description");

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

        Assertions.assertNotNull(createdCategory, "It was not possible to create the category");
        categoryId = createdCategory.getId();

        SubCategoryCreateDTO subCategoryDTO = new SubCategoryCreateDTO();
        subCategoryDTO.setName("TestSubCategory-Public");
        subCategoryDTO.setDescription("Test SubCategory Description");
        subCategoryDTO.setCategoryId(categoryId);

        SubCategoryDetailDTO createdSubCategory = webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", "127.0.0.1")
            .bodyValue(subCategoryDTO)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SubCategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(createdSubCategory, "It was not possible to create the subcategory");
        subCategoryId = createdSubCategory.getId();


        FinalProductCreateDTO dto = buildProductDTO(
            "TestProduct-Public", "TestBrand-Public", categoryId, subCategoryId
        );
        webTestClient.post()
            .uri("/products/admin")
            .header("X-User", "admin")
            .header("X-Forwarded-For", "127.0.0.1")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated();

        FinalProductPartialDetailDTO response = webTestClient.get()
            .uri("/products/by-name/{name}", "TestProduct-Public")
            .exchange()
            .expectStatus().isOk()
            .expectBody(FinalProductPartialDetailDTO.class)
            .returnResult()
            .getResponseBody();

        if (response == null || response.getProduct() == null) {
            throw new AssertionError("It was not possible to retrieve the created product");
        }
        createdProductId = response.getProduct().getId();
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
    void listProducts_shouldReturnList() {
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
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/by-category/{categoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(categoryId))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductPartialDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(3)
    void listProductsBySubCategory_shouldReturnList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/by-subcategory/{subCategoryId}")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build(subCategoryId))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FinalProductPartialDetailDTO.class)
                .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(4)
    void getProductById_shouldReturnProduct() {
        webTestClient.get()
            .uri("/products/{id}", createdProductId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(FinalProductPartialDetailDTO.class)
            .value(response -> Assertions.assertNotNull(response.getProduct()));
    }

    @Test
    @Order(5)
    void getProductsByName_shouldReturnProduct() {
        webTestClient.get()
                .uri("/products/by-name/{name}", "TestProduct-Public")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinalProductPartialDetailDTO.class)
                .value(response -> Assertions.assertNotNull(response.getProduct()));
    }
}