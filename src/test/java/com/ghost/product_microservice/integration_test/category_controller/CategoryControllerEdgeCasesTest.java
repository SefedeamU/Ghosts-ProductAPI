package com.ghost.product_microservice.integration_test.category_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryPatchDTO;

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
public class CategoryControllerEdgeCasesTest {

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

    private CategoryCreateDTO buildCategoryDTO(String name, String description) {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }

    @Test
    @Order(1)
    void createCategory_withoutRequiredHeader_shouldReturnBadRequest() {
        cleanDatabase();
        CategoryCreateDTO dto = buildCategoryDTO("EdgeCaseCategory1", "Desc");
        webTestClient.post()
            .uri("/categories")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(2)
    void createCategory_withMissingFields_shouldReturnBadRequest() {
        cleanDatabase();
        CategoryCreateDTO dto = buildCategoryDTO("", "");
        webTestClient.post()
            .uri("/categories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void getCategoryById_withInvalidId_shouldReturnBadRequest() {
        cleanDatabase();
        webTestClient.get()
            .uri("/categories/{id}", -1)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void getCategoryByName_withMissingName_shouldReturnBadRequest() {
        cleanDatabase();
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/categories/by-name")
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(5)
    void updateCategory_withNonExistentId_shouldReturnNotFound() {
        cleanDatabase();
        CategoryCreateDTO dto = buildCategoryDTO("EdgeCaseCategory2", "Desc");
        webTestClient.put()
            .uri("/categories/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    void patchCategory_withNonExistentId_shouldReturnNotFound() {
        cleanDatabase();
        CategoryPatchDTO patchDTO = new CategoryPatchDTO();
        patchDTO.setDescription("Patched Desc");
        webTestClient.patch()
            .uri("/categories/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(patchDTO)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void deleteCategory_withNonExistentId_shouldReturnNotFound() {
        cleanDatabase();
        webTestClient.delete()
            .uri("/categories/{id}", 999999)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(8)
    void deleteCategory_withoutRequiredHeader_shouldReturnBadRequest() {
        cleanDatabase();
        webTestClient.delete()
            .uri("/categories/{id}", 1)
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isBadRequest();
    }
}