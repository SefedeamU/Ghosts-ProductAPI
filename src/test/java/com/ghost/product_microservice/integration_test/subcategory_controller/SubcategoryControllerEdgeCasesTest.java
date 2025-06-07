package com.ghost.product_microservice.integration_test.subcategory_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryPatchDTO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.r2dbc.core.DatabaseClient;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubcategoryControllerEdgeCasesTest {

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

    private CategoryDetailDTO createCategory(String name, String description) {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName(name);
        dto.setDescription(description);
        return webTestClient.post()
            .uri("/categories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();
    }

    private SubCategoryCreateDTO buildSubCategoryDTO(String name, String description, Long categoryId) {
        SubCategoryCreateDTO dto = new SubCategoryCreateDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setCategoryId(categoryId);
        return dto;
    }

    @Test
    @Order(1)
    void createSubCategory_withoutRequiredHeader_shouldReturnBadRequest() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("EdgeCaseCat1", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("EdgeCaseSubCat1", "Desc", category.getId());
        webTestClient.post()
            .uri("/subcategories")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(2)
    void createSubCategory_withMissingFields_shouldReturnBadRequest() {
        cleanDatabase();
        SubCategoryCreateDTO dto = buildSubCategoryDTO("", "", null);
        webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void createSubCategory_withInvalidCategory_shouldReturnBadRequest() {
        cleanDatabase();
        SubCategoryCreateDTO dto = buildSubCategoryDTO("EdgeCaseSubCat2", "Desc", -1L);
        webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void getSubCategoryById_withInvalidId_shouldReturnBadRequest() {
        cleanDatabase();
        webTestClient.get()
            .uri("/subcategories/{id}", -1)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(5)
    void getSubCategoryByName_withMissingName_shouldReturnBadRequest() {
        cleanDatabase();
        webTestClient.get()
            .uri("/subcategories/by-name/")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    void updateSubCategory_withNonExistentId_shouldReturnNotFound() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("EdgeCaseCat2", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("EdgeCaseSubCat3", "Desc", category.getId());
        webTestClient.put()
            .uri("/subcategories/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void patchSubCategory_withNonExistentId_shouldReturnNotFound() {
        cleanDatabase();
        SubCategoryPatchDTO patchDTO = new SubCategoryPatchDTO();
        patchDTO.setDescription("Patched Desc");
        webTestClient.patch()
            .uri("/subcategories/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(patchDTO)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(8)
    void deleteSubCategory_withNonExistentId_shouldReturnNotFound() {
        cleanDatabase();
        webTestClient.delete()
            .uri("/subcategories/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(9)
    void deleteSubCategory_withoutRequiredHeader_shouldReturnBadRequest() {
        cleanDatabase();
        webTestClient.delete()
            .uri("/subcategories/{id}", 1)
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(10)
    void listSubCategoriesByCategory_withInvalidCategory_shouldReturnBadRequest() {
        cleanDatabase();
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/subcategories")
                .queryParam("categoryId", -1)
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }
}