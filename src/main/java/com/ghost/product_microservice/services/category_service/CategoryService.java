package com.ghost.product_microservice.services.category_service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryPatchDTO;
import com.ghost.product_microservice.models.Category;
import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.repositories.category_repository.CategoryRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductAuditRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryService {
    
    final private CategoryRepository categoryRepository;
    final private ProductAuditRepository productAuditRepository;

    public CategoryService(CategoryRepository categoryRepository,
                            ProductAuditRepository productAuditRepository) {
        this.productAuditRepository = productAuditRepository;
        this.categoryRepository = categoryRepository;
    }

    private Mono<Void> logAudit(Long categoryId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setCategoryId(categoryId);
        audit.setProductId(null);
        audit.setSubcategoryId(null);
        audit.setAction(action);
        audit.setUsername(user);
        audit.setEntity("Category");
        audit.setDetails(details);
        audit.setDate(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        return productAuditRepository.save(audit).then();
    }

    public Flux<CategoryDetailDTO> findAllCategories() {
        return categoryRepository.findAll()
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no categories available")))
            .map(category -> {
                CategoryDetailDTO categoryDTO = new CategoryDetailDTO();
                categoryDTO.setId(category.getId());
                categoryDTO.setName(category.getName());
                categoryDTO.setDescription(category.getDescription());
                return categoryDTO;
            });
    }

    public Mono<CategoryDetailDTO> findCategoryById(Long id) {
        return categoryRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "The category does not exist")))
            .map(category -> {
                CategoryDetailDTO categoryDTO = new CategoryDetailDTO();
                categoryDTO.setId(category.getId());
                categoryDTO.setName(category.getName());
                categoryDTO.setDescription(category.getDescription());
                return categoryDTO;
            });
    }

    public Mono<CategoryDetailDTO> findCategoryByName(String name) {
        return categoryRepository.findCategoryByName(name)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "The category does not exist")))
            .map(category -> {
                CategoryDetailDTO categoryDTO = new CategoryDetailDTO();
                categoryDTO.setId(category.getId());
                categoryDTO.setName(category.getName());
                categoryDTO.setDescription(category.getDescription());
                return categoryDTO;
            });
    }

    public Mono<CategoryDetailDTO> createCategory(CategoryCreateDTO categoryDTO, String user, String ip) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        return categoryRepository.save(category)
            .flatMap(savedCategory ->
                logAudit(savedCategory.getId(), "POST", user, "Create product", ip)
                    .thenReturn(savedCategory)
            )
            .map(savedCategory -> {
                CategoryDetailDTO savedCategoryDTO = new CategoryDetailDTO();
                savedCategoryDTO.setId(savedCategory.getId());
                savedCategoryDTO.setName(savedCategory.getName());
                savedCategoryDTO.setDescription(savedCategory.getDescription());
                return savedCategoryDTO;
            });
    }

    public Mono<CategoryDetailDTO> updateCategoryById(Long id, CategoryCreateDTO categoryDTO, String user, String ip) {
        return categoryRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "The category does not exist")))
            .flatMap(category -> {
                category.setName(categoryDTO.getName());
                category.setDescription(categoryDTO.getDescription());
                return categoryRepository.save(category);
            })
            .flatMap(updatedCategory ->
                logAudit(updatedCategory.getId(), "PUT", user, "Update product", ip)
                    .thenReturn(updatedCategory)
            )
            .map(updatedCategory -> {
                CategoryDetailDTO updatedCategoryDTO = new CategoryDetailDTO();
                updatedCategoryDTO.setId(updatedCategory.getId());
                updatedCategoryDTO.setName(updatedCategory.getName());
                updatedCategoryDTO.setDescription(updatedCategory.getDescription());
                return updatedCategoryDTO;
            });
    }

    public Mono<CategoryDetailDTO> patchCategoryById(Long id, CategoryPatchDTO categoryDTO, String user, String ip) {
        return categoryRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "The category does not exist")))
            .flatMap(category -> {
                if (categoryDTO.getName() != null) {
                    category.setName(categoryDTO.getName());
                }
                if (categoryDTO.getDescription() != null) {
                    category.setDescription(categoryDTO.getDescription());
                }
                return categoryRepository.save(category);
            }).flatMap(updatedCategory ->
                logAudit(updatedCategory.getId(), "PATCH", user, "Patch product", ip)
                    .thenReturn(updatedCategory)
            )
            .map(updatedCategory -> {
                CategoryDetailDTO updatedCategoryDTO = new CategoryDetailDTO();
                updatedCategoryDTO.setId(updatedCategory.getId());
                updatedCategoryDTO.setName(updatedCategory.getName());
                updatedCategoryDTO.setDescription(updatedCategory.getDescription());
                return updatedCategoryDTO;
            });
    }

    public Mono<CategoryDetailDTO> deleteCategoryById(Long id, String user, String ip) {
        return categoryRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "The category does not exist")))
            .flatMap(category -> 
                logAudit(category.getId(), "DELETE", user, "Delete product", ip)
                    .then(categoryRepository.deleteById(id))
                    .then(Mono.fromCallable(() -> {
                        CategoryDetailDTO dto = new CategoryDetailDTO();
                        dto.setId(category.getId());
                        dto.setName("Category " + category.getName() + " deleted.");
                        dto.setDescription("Category with name "
                            + category.getName() + " and id " + category.getId()
                            + " has been deleted by the user " + user + ".");
                        return dto;
                    }))
            );
    }
}
