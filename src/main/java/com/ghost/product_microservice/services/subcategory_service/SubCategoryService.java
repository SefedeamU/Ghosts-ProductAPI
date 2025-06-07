package com.ghost.product_microservice.services.subcategory_service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryPatchDTO;
import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.models.SubCategory;
import com.ghost.product_microservice.repositories.category_repository.SubCategoryRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductAuditRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubCategoryService {

    final private SubCategoryRepository subCategoryRepository;
    final private ProductAuditRepository productAuditRepository;

    public SubCategoryService(SubCategoryRepository subCategoryRepository,
                                ProductAuditRepository productAuditRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.productAuditRepository = productAuditRepository;
    }

    private Mono<Void> logAudit(Long categoryId, Long subcategoryId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setCategoryId(categoryId);
        audit.setProductId(null);
        audit.setSubcategoryId(subcategoryId);
        audit.setAction(action);
        audit.setUsername(user);
        audit.setEntity("SubCategory");
        audit.setDetails(details);
        audit.setDate(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        return productAuditRepository.save(audit).then();
    }

    public Flux<SubCategoryDetailDTO> findAllSubCategoriesByCategory(Long categoryId) {
        return subCategoryRepository.findAllByCategoryId(categoryId)
            .map(subCategory -> {
                SubCategoryDetailDTO subCategoryDTO = new SubCategoryDetailDTO();
                subCategoryDTO.setId(subCategory.getId());
                subCategoryDTO.setName(subCategory.getName());
                subCategoryDTO.setDescription(subCategory.getDescription());
                subCategoryDTO.setCategoryId(subCategory.getCategoryId());
                return subCategoryDTO;
            });
    }

    public Mono<SubCategoryDetailDTO> findSubCategoryById(Long id) {
        return subCategoryRepository.findById(id)
            .map(subCategory -> {
                SubCategoryDetailDTO subCategoryDTO = new SubCategoryDetailDTO();
                subCategoryDTO.setId(subCategory.getId());
                subCategoryDTO.setName(subCategory.getName());
                subCategoryDTO.setDescription(subCategory.getDescription());
                subCategoryDTO.setCategoryId(subCategory.getCategoryId());
                return subCategoryDTO;
            });
    }

    public Mono<SubCategoryDetailDTO> findSubCategoryByName(String name){
        return subCategoryRepository.findByName(name)
        .map(subCategory -> {
            SubCategoryDetailDTO subCategoryDTO = new SubCategoryDetailDTO();
            subCategoryDTO.setId(subCategory.getId());
            subCategoryDTO.setName(subCategory.getName());
            subCategoryDTO.setDescription(subCategory.getDescription());
            subCategoryDTO.setCategoryId(subCategory.getCategoryId());
            return subCategoryDTO;
        });
    }

    public Mono<SubCategoryDetailDTO> createSubCategory(Long categoryId, SubCategoryCreateDTO dto, String user, String ip){
        SubCategory newSubCategory = new SubCategory();
        newSubCategory.setCategoryId(categoryId);
        newSubCategory.setDescription(dto.getDescription());
        newSubCategory.setName(dto.getName());

        return subCategoryRepository.save(newSubCategory)
        .flatMap(savedSubCategory -> {
            SubCategoryDetailDTO response = new SubCategoryDetailDTO();
            response.setId(savedSubCategory.getId());
            response.setName(savedSubCategory.getName());
            response.setDescription(savedSubCategory.getDescription());
            response.setCategoryId(savedSubCategory.getCategoryId());
            return logAudit(savedSubCategory.getCategoryId(), savedSubCategory.getId(), "CREATE", user, "Create subcategory", ip)
                        .thenReturn(response);
        });
    }

    public Mono<SubCategoryDetailDTO> updateSubCategoryById(Long subcategoryId, SubCategoryCreateDTO dto, String user, String ip){
        return subCategoryRepository.findById(subcategoryId)
        .flatMap(subCategory -> {
            subCategory.setCategoryId(dto.getCategoryId());
            subCategory.setDescription(dto.getDescription());
            subCategory.setName(dto.getName());
            return subCategoryRepository.save(subCategory);
        })
        .flatMap(savedSubCategory -> {
            SubCategoryDetailDTO response = new SubCategoryDetailDTO();
            response.setId(savedSubCategory.getId());
            response.setName(savedSubCategory.getName());
            response.setDescription(savedSubCategory.getDescription());
            response.setCategoryId(savedSubCategory.getCategoryId());
            return logAudit(savedSubCategory.getCategoryId(), savedSubCategory.getId(), "UPDATE", user, "Update subcategory", ip)
                        .thenReturn(response);
        });
    }

    public Mono<SubCategoryDetailDTO> patchSubCategoryById(Long subcategoryId, SubCategoryPatchDTO dto, String user, String ip){
        return subCategoryRepository.findById(subcategoryId)
        .flatMap(subCategory -> {
            if(dto.getCategoryId() != null){
                subCategory.setCategoryId(dto.getCategoryId());
            }
            if(dto.getDescription() != null){
                subCategory.setDescription(dto.getDescription());
            }
            if(dto.getName() != null){
                subCategory.setName(dto.getName());
            }
            return subCategoryRepository.save(subCategory);
        })
        .flatMap(savedSubCategory -> {
            SubCategoryDetailDTO response = new SubCategoryDetailDTO();
            response.setId(savedSubCategory.getId());
            response.setName(savedSubCategory.getName());
            response.setDescription(savedSubCategory.getDescription());
            response.setCategoryId(savedSubCategory.getCategoryId());
            return logAudit(savedSubCategory.getCategoryId(), savedSubCategory.getId(), "PATCH", user, "Patch subcategory", ip)
                        .thenReturn(response);
        });
    }

    public Mono<SubCategoryDetailDTO> deleteSubCategoryById(Long subcategoryId, String user, String ip){
        return subCategoryRepository.findById(subcategoryId)
            .flatMap(subCategory -> 
                logAudit(subCategory.getCategoryId(), subCategory.getId(), "DELETE", user, "Delete subcategory", ip)
                    .then(subCategoryRepository.deleteById(subcategoryId))
                    .then(Mono.fromCallable(() -> {
                        SubCategoryDetailDTO response = new SubCategoryDetailDTO();
                        response.setId(subCategory.getId());
                        response.setName(subCategory.getName() + " has been succesfully deleted");
                        response.setDescription("Sub Category deleted by" + user);
                        response.setCategoryId(subCategory.getCategoryId());
                        return response;
                    }))
            );
    }
}