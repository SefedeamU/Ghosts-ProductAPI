package com.ghost.product_microservice.integration_test.category_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
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
public class CategoryControllerTest {

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

    @Order(1)
    @Test
    void createCategory_shouldPersistAndReturnCategory() {
        cleanDatabase();
        CategoryCreateDTO dto = buildCategoryDTO("TestCategory", "Test Category Description");
        CategoryDetailDTO createdCategory = webTestClient.post()
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

        Assertions.assertNotNull(createdCategory, "It was not possible to create the category");
        Assertions.assertEquals("TestCategory", createdCategory.getName());
        Assertions.assertEquals("Test Category Description", createdCategory.getDescription());
    }

    @Order(2)
    @Test
    void updateCategory_shouldUpdateAndReturnCategory() {
        cleanDatabase();
        CategoryCreateDTO dto = buildCategoryDTO("ToUpdate", "Desc");
        CategoryDetailDTO createdCategory = webTestClient.post()
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

        Assertions.assertNotNull(createdCategory, "The deleted category should not be null");

        CategoryCreateDTO updateDTO = buildCategoryDTO("UpdatedCategory", "Updated Description");
        webTestClient.put()
            .uri("/categories/{id}", createdCategory.getId())
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updateDTO)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("UpdatedCategory", response.getName());
                Assertions.assertEquals("Updated Description", response.getDescription());
            });
    }

    @Order(3)
    @Test
    void patchCategory_shouldPatchAndReturnCategory() {
        cleanDatabase();
        CategoryCreateDTO dto = buildCategoryDTO("PatchMe", "Desc");
        CategoryDetailDTO createdCategory = webTestClient.post()
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

        Assertions.assertNotNull(createdCategory, "The created category should not be null");

        CategoryPatchDTO patchDTO = new CategoryPatchDTO();
        patchDTO.setDescription("Patched Description");

        webTestClient.patch()
            .uri("/categories/{id}", createdCategory.getId())
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(patchDTO)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("PatchMe", response.getName());
                Assertions.assertEquals("Patched Description", response.getDescription());
            });
    }

    @Order(4)
    @Test
    void getCategoryById_shouldReturnCategory() {
        cleanDatabase();
        CategoryCreateDTO dto = buildCategoryDTO("FindMe", "Desc");
        CategoryDetailDTO createdCategory = webTestClient.post()
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
        
        Assertions.assertNotNull(createdCategory, "The created category should not be null");

        webTestClient.get()
            .uri("/categories/{id}", createdCategory.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals(createdCategory.getId(), response.getId());
                Assertions.assertEquals("FindMe", response.getName());
            });
    }

    @Order(5)
    @Test
    void getCategoryByName_shouldReturnCategory() {
        cleanDatabase();
        // Crear categoría
        CategoryCreateDTO dto = buildCategoryDTO("ByName", "Desc");
        webTestClient.post()
            .uri("/categories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/categories/by-name")
                .queryParam("name", "ByName")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("ByName", response.getName());
            });
    }

    @Order(6)
    @Test
    void listCategories_shouldReturnList() {
        cleanDatabase();
        // Crear varias categorías
        for (int i = 0; i < 3; i++) {
            CategoryCreateDTO dto = buildCategoryDTO("ListCat" + i, "Desc" + i);
            webTestClient.post()
                .uri("/categories")
                .header("X-User", "admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated();
        }

        webTestClient.get()
            .uri("/categories")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(CategoryDetailDTO.class)
            .value(list -> Assertions.assertEquals(3, list.size()));
    }

    @Order(7)
    @Test
    void deleteCategory_shouldDeleteAndReturnCategory() {
        cleanDatabase();
        // Crear categoría
        CategoryCreateDTO dto = buildCategoryDTO("DeleteMe", "Desc");
        CategoryDetailDTO createdCategory = webTestClient.post()
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

        Assertions.assertNotNull(createdCategory, "The created category should not be null");

        webTestClient.delete()
            .uri("/categories/{id}", createdCategory.getId())
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals(createdCategory.getId(), response.getId());
            });

        // Verificar que ya no existe
        webTestClient.get()
            .uri("/categories/{id}", createdCategory.getId())
            .exchange()
            .expectStatus().isNotFound();
    }
}