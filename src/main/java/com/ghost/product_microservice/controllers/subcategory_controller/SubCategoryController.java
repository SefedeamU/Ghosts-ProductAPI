package com.ghost.product_microservice.controllers.subcategory_controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.subcategorydto.SubCategoryPatchDTO;
import com.ghost.product_microservice.services.subcategory_service.SubCategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/subcategories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip)) {
            throw new IllegalArgumentException("Client IP address not found in X-Forwarded-For header");
        }
        return ip;
    }

    @GetMapping
    public Flux<SubCategoryDetailDTO> listSubCategoriesByCategory(@RequestParam Long categoryId) {
        if (categoryId == null || categoryId <= 0) throw new IllegalArgumentException("categoryId must be positive");
        return subCategoryService.findAllSubCategoriesByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public Mono<SubCategoryDetailDTO> getSubCategoryById(@PathVariable Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        return subCategoryService.findSubCategoryById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub Category not found")));
    }

    @GetMapping("/by-name/{name}")
    public Mono<SubCategoryDetailDTO> getSubCategoryByName(@PathVariable String name) {
        if (!StringUtils.hasText(name)) throw new IllegalArgumentException("name is required");
        return subCategoryService.findSubCategoryByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SubCategoryDetailDTO> postSubCategory(
        @Valid @RequestBody SubCategoryCreateDTO subCategoryDTO,
        HttpServletRequest request,
        @RequestHeader("X-User") String user){

        String ip = extractClientIp(request);
        if (subCategoryDTO == null) throw new IllegalArgumentException("SubCategory data is required");
        if (subCategoryDTO.getCategoryId() == null || subCategoryDTO.getCategoryId() <= 0)
            throw new IllegalArgumentException("categoryId must be positive");
        if (!StringUtils.hasText(subCategoryDTO.getName()))
            throw new IllegalArgumentException("name is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return subCategoryService.createSubCategory(subCategoryDTO.getCategoryId(), subCategoryDTO, user, ip);
    }

    @PutMapping("/{id}")
    public Mono<SubCategoryDetailDTO> updateSubCategory(
        @PathVariable Long id,
        @Valid @RequestBody SubCategoryCreateDTO subCategoryDTO,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (subCategoryDTO == null) throw new IllegalArgumentException("SubCategory data is required");
        if (subCategoryDTO.getCategoryId() == null || subCategoryDTO.getCategoryId() <= 0)
            throw new IllegalArgumentException("categoryId must be positive");
        if (!StringUtils.hasText(subCategoryDTO.getName()))
            throw new IllegalArgumentException("name is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return subCategoryService.updateSubCategoryById(id, subCategoryDTO, user, ip)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }

    @PatchMapping("/{id}")
    public Mono<SubCategoryDetailDTO> patchSubCategory(
        @PathVariable Long id,
        @RequestBody SubCategoryPatchDTO subCategoryDTO,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (subCategoryDTO == null) throw new IllegalArgumentException("Patch data is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return subCategoryService.patchSubCategoryById(id, subCategoryDTO, user, ip)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }

    @DeleteMapping("/{id}")
    public Mono<SubCategoryDetailDTO> deleteSubCategory(
        @PathVariable Long id,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return subCategoryService.deleteSubCategoryById(id, user, ip)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }
}