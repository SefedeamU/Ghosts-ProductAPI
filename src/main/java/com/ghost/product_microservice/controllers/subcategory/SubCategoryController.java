package com.ghost.product_microservice.controllers.subcategory;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryPatchDTO;
import com.ghost.product_microservice.services.subcategory.SubCategoryService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/subcategories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @GetMapping
    public Flux<SubCategoryDetailDTO> listSubCategoriesByCategory(@RequestParam Long categoryId) {
        return subCategoryService.findAllSubCategoriesByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public Mono<SubCategoryDetailDTO> getSubCategoryById(@PathVariable Long id) {
        return subCategoryService.findSubCategoryById(id);
    }

    @GetMapping("/by-name/{name}")
    public Mono<SubCategoryDetailDTO> getSubCategoryByName(@PathVariable String name) {
        return subCategoryService.findSubCategoryByName(name);
    }

    @PostMapping
    public Mono<SubCategoryDetailDTO> postSubCategory(@RequestBody SubCategoryCreateDTO subCategoryDTO) {
        return subCategoryService.createSubCategory(subCategoryDTO.getCategoryId(), subCategoryDTO);
    }
    
    @PutMapping("/{id}")
    public Mono<SubCategoryCreateDTO> updateSubCategory(@PathVariable Long id, @RequestBody SubCategoryCreateDTO subCategoryDTO) {
        return subCategoryService.updateSubCategoryById(id, subCategoryDTO);
    }

    @PatchMapping("/{id}")
    public Mono<SubCategoryPatchDTO> patchSubCategory(@PathVariable Long id, @RequestBody SubCategoryPatchDTO subCategoryDTO) {
        return subCategoryService.patchSubCategoryById(id, subCategoryDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategoryById(id);
    }
}
