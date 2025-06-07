package com.ghost.product_microservice.integration_test.product_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.CreateProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.CreateProductPriceDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.CreateProductAttributeDTO;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerEdgeCasesTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    private Long categoryId;
    private Long subCategoryId;

    private static final String ADMIN_IP = "127.0.0.1";

    @BeforeEach
    void cleanAndSetupData(TestInfo testInfo) {
        databaseClient.sql("TRUNCATE TABLE product_audit RESTART IDENTITY CASCADE").then().block();
        databaseClient.sql("TRUNCATE TABLE product RESTART IDENTITY CASCADE").then().block();
        databaseClient.sql("TRUNCATE TABLE subcategory RESTART IDENTITY CASCADE").then().block();
        databaseClient.sql("TRUNCATE TABLE category RESTART IDENTITY CASCADE").then().block();

        CategoryCreateDTO categoryDTO = new CategoryCreateDTO();
        categoryDTO.setName("EdgeCaseCategory-" + testInfo.getDisplayName() + "-" + System.nanoTime());
        categoryDTO.setDescription("Edge case category");

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
        categoryId = createdCategory.getId();

        SubCategoryCreateDTO subCategoryDTO = new SubCategoryCreateDTO();
        subCategoryDTO.setName("EdgeCaseSubCategory-" + testInfo.getDisplayName() + "-" + System.nanoTime());
        subCategoryDTO.setDescription("Edge case subcategory");
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

        Assertions.assertNotNull(createdSubCategory, "It was not possible to create the subcategory");
        subCategoryId = createdSubCategory.getId();
    }

    private FinalProductCreateDTO buildProductDTO(String name, String brand, Long categoryId, Long subcategoryId) {
        CreateProductDTO product = new CreateProductDTO();
        product.setName(name);
        product.setBrand(brand);
        product.setCategoryId(categoryId);
        product.setSubcategoryId(subcategoryId);
        product.setDescription("Edge case product");
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
    void createProduct_withoutRequiredHeader_shouldReturnBadRequest() {
        FinalProductCreateDTO dto = buildProductDTO("EdgeCaseProduct1", "EdgeBrand", categoryId, subCategoryId);

        webTestClient.post()
            .uri("/products/admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(2)
    void createProduct_withInvalidCategory_shouldReturnBadRequest() {
        FinalProductCreateDTO dto = buildProductDTO("EdgeCaseProduct2", "EdgeBrand", -1L, subCategoryId);

        webTestClient.post()
            .uri("/products/admin")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void createProduct_withMissingFields_shouldReturnBadRequest() {
        FinalProductCreateDTO dto = buildProductDTO("", "", null, null);

        webTestClient.post()
            .uri("/products/admin")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void getProductById_withInvalidId_shouldReturnBadRequest() {
        webTestClient.get()
            .uri("/products/{id}", -1)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(5)
    void updateProduct_withNonExistentId_shouldReturnNotFound() {
        FinalProductCreateDTO dto = buildProductDTO("EdgeCaseProduct3", "EdgeBrand", categoryId, subCategoryId);

        webTestClient.put()
            .uri("/products/admin/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    void patchProduct_withNonExistentId_shouldReturnNotFound() {
        com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPatchDTO patchDTO =
            new com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPatchDTO();

        var patchProduct = new com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.PatchProductDTO();
        patchProduct.setName("EdgePatchedProduct");
        patchDTO.setProduct(patchProduct);

        webTestClient.patch()
            .uri("/products/admin/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(patchDTO)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void deleteProduct_withNonExistentId_shouldReturnNotFound() {
        webTestClient.delete()
            .uri("/products/admin/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(8)
    void listProducts_withInvalidPagination_shouldReturnBadRequest() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/products")
                .queryParam("page", -1)
                .queryParam("size", 0)
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }
}