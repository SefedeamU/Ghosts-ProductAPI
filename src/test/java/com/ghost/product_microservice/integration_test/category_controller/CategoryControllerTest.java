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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static Long categoryId;
    private Long createdCategoryId;
    private static final String ADMIN_IP = "127.0.0.1";

    @BeforeAll
    void setup() {
        // Crear categoría
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO();
        categoryDTO.setName("TestCategory");
        categoryDTO.setDescription("Test Category Description");

        CategoryDetailDTO createdCategory = webTestClient.post()
            .uri("/categories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(categoryDTO)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(createdCategory, "No se pudo crear la categoría");
        categoryId = createdCategory.getId();
        createdCategoryId = categoryId;
    }

    private CategoryCreateDTO buildCategoryDTO(String name, String description) {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }

    @Test
    @Order(1)
    void createCategory_shouldPersistAndReturnCategory() {
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

        Assertions.assertNotNull(createdCategory, "No se pudo crear la categoría");
        createdCategoryId = createdCategory.getId();
    }


    @Test
    @Order(2)
    void updateCategory_shouldUpdateAndReturnCategory() {
        CategoryCreateDTO dto = buildCategoryDTO("UpdatedCategory", "Updated Description");

        webTestClient.put()
            .uri("/categories/{id}", createdCategoryId)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("UpdatedCategory", response.getName());
                Assertions.assertEquals("Updated Description", response.getDescription());
            });
    }

    @Test
    @Order(3)
    void patchCategory_shouldPatchAndReturnCategory() {
        CategoryPatchDTO patchDTO = new CategoryPatchDTO();
        patchDTO.setDescription("Patched Description");

        webTestClient.patch()
            .uri("/categories/{id}", createdCategoryId)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(patchDTO)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("UpdatedCategory", response.getName());
                Assertions.assertEquals("Patched Description", response.getDescription());
            });
    }

    @Test
    @Order(4)
    void getCategoryById_shouldReturnCategory() {
        webTestClient.get()
            .uri("/categories/{id}", createdCategoryId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals(createdCategoryId, response.getId());
                Assertions.assertEquals("UpdatedCategory", response.getName());
            });
    }

    @Test
    @Order(5)
    void getCategoryByName_shouldReturnCategory() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/categories/by-name")
                .queryParam("name", "UpdatedCategory")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("UpdatedCategory", response.getName());
            });
    }

    @Test
    @Order(6)
    void listCategories_shouldReturnList() {
        webTestClient.get()
            .uri("/categories")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(CategoryDetailDTO.class)
            .value(list -> Assertions.assertFalse(list.isEmpty()));
    }

    @Test
    @Order(7)
    void deleteCategory_shouldDeleteAndReturnCategory() {
        webTestClient.delete()
            .uri("/categories/{id}", createdCategoryId)
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals(createdCategoryId, response.getId());
            });
    }

}