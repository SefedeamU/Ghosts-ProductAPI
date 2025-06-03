package com.ghost.product_microservice.controllers.category_controller;

import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryPatchDTO;

import com.ghost.product_microservice.services.category_service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Utilidad para extraer y validar IP
    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip)) {
            throw new IllegalArgumentException("Client IP address not found in X-Forwarded-For header");
        }
        return ip;
    }

    @GetMapping
    public Flux<CategoryDetailDTO> listCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/{id}")
    public Mono<CategoryDetailDTO> getCategoryById(@PathVariable Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        return categoryService.findCategoryById(id);
    }

    @GetMapping("/by-name")
    public Mono<CategoryDetailDTO> getCategoryByName(@RequestParam String name) {
        if (!StringUtils.hasText(name)) throw new IllegalArgumentException("name is required");
        return categoryService.findCategoryByName(name);
    }

    @PostMapping
    public Mono<CategoryDetailDTO> postCategory(
        @Valid @RequestBody CategoryDetailDTO categoryDTO,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = extractClientIp(request);
        if (categoryDTO == null) throw new IllegalArgumentException("Category data is required");
        if (!StringUtils.hasText(categoryDTO.getName()))
            throw new IllegalArgumentException("name is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return categoryService.createCategory(categoryDTO, user, ip);
    }

    @PutMapping("/{id}")
    public Mono<CategoryDetailDTO> updateCategory(
        @PathVariable Long id,
        @Valid @RequestBody CategoryCreateDTO categoryDTO,
        HttpServletRequest request,
        @RequestBody String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (categoryDTO == null) throw new IllegalArgumentException("Category data is required");
        if (!StringUtils.hasText(categoryDTO.getName()))
            throw new IllegalArgumentException("name is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return categoryService.updateCategoryById(id, categoryDTO, user, ip);
    }

    @PatchMapping("/{id}")
    public Mono<CategoryDetailDTO> patchCategory(
        @PathVariable Long id,
        @RequestBody CategoryPatchDTO categoryDTO,
        HttpServletRequest request,
        @RequestBody String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (categoryDTO == null) throw new IllegalArgumentException("Patch data is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return categoryService.patchCategoryById(id, categoryDTO, user, ip);
    }

    @DeleteMapping("/{id}")
    public Mono<CategoryDetailDTO> deleteCategory(
        @PathVariable Long id,
        HttpServletRequest request,
        @RequestBody String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return categoryService.deleteCategoryById(id, user, ip);
    }
}
