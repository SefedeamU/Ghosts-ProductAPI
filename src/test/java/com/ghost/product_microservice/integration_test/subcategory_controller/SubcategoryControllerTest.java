package com.ghost.product_microservice.integration_test.subcategory_controller;

import com.ghost.product_microservice.TestcontainersConfiguration;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryDetailDTO;
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
public class SubcategoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    private static final String ADMIN_IP = "127.0.0.1";

    private void cleanDatabase() {
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
    void createSubCategory_shouldPersistAndReturnSubCategory() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("CatForSub", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("TestSubCat", "SubDesc", category.getId());

        SubCategoryDetailDTO created = webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SubCategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(created, "No se pudo crear la subcategoría");
        Assertions.assertEquals("TestSubCat", created.getName());
        Assertions.assertEquals("SubDesc", created.getDescription());
        Assertions.assertEquals(category.getId(), created.getCategoryId());
    }

    @Test
    @Order(2)
    void updateSubCategory_shouldUpdateAndReturnSubCategory() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("CatForUpdate", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("ToUpdate", "Desc", category.getId());

        SubCategoryDetailDTO created = webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SubCategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(created);

        SubCategoryCreateDTO updateDTO = buildSubCategoryDTO("UpdatedSubCat", "Updated Desc", category.getId());

        webTestClient.put()
            .uri("/subcategories/{id}", created.getId())
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updateDTO)
            .exchange()
            .expectStatus().isOk()
            .expectBody(SubCategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("UpdatedSubCat", response.getName());
                Assertions.assertEquals("Updated Desc", response.getDescription());
            });
    }

    @Test
    @Order(3)
    void patchSubCategory_shouldPatchAndReturnSubCategory() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("CatForPatch", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("PatchMe", "Desc", category.getId());

        SubCategoryDetailDTO created = webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SubCategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(created);

        SubCategoryPatchDTO patchDTO = new SubCategoryPatchDTO();
        patchDTO.setDescription("Patched Desc");

        webTestClient.patch()
            .uri("/subcategories/{id}", created.getId())
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(patchDTO)
            .exchange()
            .expectStatus().isOk()
            .expectBody(SubCategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("PatchMe", response.getName());
                Assertions.assertEquals("Patched Desc", response.getDescription());
            });
    }

    @Test
    @Order(4)
    void getSubCategoryById_shouldReturnSubCategory() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("CatForGet", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("FindMe", "Desc", category.getId());

        SubCategoryDetailDTO created = webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SubCategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(created);

        webTestClient.get()
            .uri("/subcategories/{id}", created.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(SubCategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals(created.getId(), response.getId());
                Assertions.assertEquals("FindMe", response.getName());
            });
    }

    @Test
    @Order(5)
    void getSubCategoryByName_shouldReturnSubCategory() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("CatForByName", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("ByName", "Desc", category.getId());

        webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated();

        webTestClient.get()
            .uri("/subcategories/by-name/{name}", "ByName")
            .exchange()
            .expectStatus().isOk()
            .expectBody(SubCategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals("ByName", response.getName());
            });
    }

    @Test
    @Order(6)
    void listSubCategoriesByCategory_shouldReturnList() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("CatForList", "Desc");
        for (int i = 0; i < 3; i++) {
            SubCategoryCreateDTO dto = buildSubCategoryDTO("ListSubCat" + i, "Desc" + i, category.getId());
            webTestClient.post()
                .uri("/subcategories")
                .header("X-User", "admin")
                .header("X-Forwarded-For", ADMIN_IP)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated();
        }

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/subcategories")
                .queryParam("categoryId", category.getId())
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(SubCategoryDetailDTO.class)
            .value(list -> Assertions.assertEquals(3, list.size()));
    }

    @Test
    @Order(7)
    void deleteSubCategory_shouldDeleteAndReturnSubCategory() {
        cleanDatabase();
        CategoryDetailDTO category = createCategory("CatForDelete", "Desc");
        SubCategoryCreateDTO dto = buildSubCategoryDTO("DeleteMe", "Desc", category.getId());

        SubCategoryDetailDTO created = webTestClient.post()
            .uri("/subcategories")
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SubCategoryDetailDTO.class)
            .returnResult()
            .getResponseBody();

        Assertions.assertNotNull(created);

        webTestClient.delete()
            .uri("/subcategories/{id}", created.getId())
            .header("X-User", "admin")
            .header("X-Forwarded-For", ADMIN_IP)
            .exchange()
            .expectStatus().isOk()
            .expectBody(SubCategoryDetailDTO.class)
            .value(response -> {
                Assertions.assertEquals(created.getId(), response.getId());
            });

        // Verificar que ya no existe
        webTestClient.get()
            .uri("/subcategories/{id}", created.getId())
            .exchange()
            .expectStatus().isNotFound();
    }
}