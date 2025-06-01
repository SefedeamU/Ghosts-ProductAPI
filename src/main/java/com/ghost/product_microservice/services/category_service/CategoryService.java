package com.ghost.product_microservice.services.category_service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

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

    private Mono<Void> logAudit(Long productId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAction(action);
        audit.setUser(user);
        audit.setEntity("Category");
        audit.setDetails(details);
        audit.setDate(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        return productAuditRepository.save(audit).then();
    }

    public Flux<CategoryDetailDTO> findAllCategories() {
        return categoryRepository.findAll()
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
        .map(category -> {
            CategoryDetailDTO categoryDTO = new CategoryDetailDTO();
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        });
    }

    public Mono<CategoryDetailDTO> findCategoryByName(String name) {
        return categoryRepository.findCategoryByName(name)
        .map(category -> {
            CategoryDetailDTO categoryDTO = new CategoryDetailDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        });
    }

    public Mono<CategoryDetailDTO> createCategory(CategoryDetailDTO categoryDTO, String user, String ip) {
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
            .flatMap(category -> {
                CategoryDetailDTO dto = new CategoryDetailDTO();
                dto.setId(category.getId());
                dto.setName("Category " + category.getName() + " deleted.");
                dto.setDescription("Category with name "
                    + category.getName() + " and id " + category.getId()
                    + " has been deleted by the user " + user + ".");
                return categoryRepository.deleteById(id).thenReturn(dto);
            }).flatMap(deletedDetails ->
                logAudit(deletedDetails.getId(), "DELETE", user, "Patch product", ip)
                    .thenReturn(deletedDetails)
            );
    }
}
