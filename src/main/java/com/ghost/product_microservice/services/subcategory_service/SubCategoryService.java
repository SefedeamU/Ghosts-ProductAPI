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

    private Mono<Void> logAudit(Long productId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAction(action);
        audit.setUser(user);
        audit.setEntity("Sub Category");
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
            return logAudit(savedSubCategory.getId(), "PUT", user, "Put product", ip)
                        .thenReturn(response);
        });
    }

    public Mono<SubCategoryDetailDTO> updateSubCategoryById(Long id, SubCategoryCreateDTO dto, String user, String ip){
        return subCategoryRepository.findById(id)
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
            return logAudit(id, "PUT", user, "Put product", ip)
                        .thenReturn(response);
        });
    }

    public Mono<SubCategoryDetailDTO> patchSubCategoryById(Long id, SubCategoryPatchDTO dto, String user, String ip){
        return subCategoryRepository.findById(id)
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
            return logAudit(id, "PATCH", user, "Patch product", ip)
                        .thenReturn(response);
        });
    }

    public Mono<SubCategoryDetailDTO> deleteSubCategoryById(Long id, String user, String ip){
        return subCategoryRepository.findById(id)
        .flatMap(subCategory -> {
            SubCategoryDetailDTO response = new SubCategoryDetailDTO();
            response.setId(subCategory.getId());
            response.setName(subCategory.getName() + " has been succesfully deleted");
            response.setDescription("Sub Category deleted by" + user);
            response.setCategoryId(subCategory.getCategoryId());
            return subCategoryRepository.deleteById(id).then(
                    logAudit(id, "DELETE", user, "Delete product", ip)
                        .thenReturn(response)
                );
        });
    }
}
