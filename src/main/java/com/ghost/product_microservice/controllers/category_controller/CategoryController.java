package com.ghost.product_microservice.controllers.category_controller;

import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryPatchDTO;
import com.ghost.product_microservice.services.category_service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

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
        return categoryService.findCategoryById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }

    @GetMapping("/by-name")
    public Mono<CategoryDetailDTO> getCategoryByName(@RequestParam String name) {
        if (!StringUtils.hasText(name)) throw new IllegalArgumentException("name is required");
        return categoryService.findCategoryByName(name)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CategoryDetailDTO> postCategory(
        @Valid @RequestBody CategoryCreateDTO categoryDTO,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

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
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (categoryDTO == null) throw new IllegalArgumentException("Category data is required");
        if (!StringUtils.hasText(categoryDTO.getName()))
            throw new IllegalArgumentException("name is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return categoryService.updateCategoryById(id, categoryDTO, user, ip)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }

    @PatchMapping("/{id}")
    public Mono<CategoryDetailDTO> patchCategory(
        @PathVariable Long id,
        @RequestBody CategoryPatchDTO categoryDTO,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (categoryDTO == null) throw new IllegalArgumentException("Patch data is required");
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return categoryService.patchCategoryById(id, categoryDTO, user, ip)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }

    @DeleteMapping("/{id}")
    public Mono<CategoryDetailDTO> deleteCategory(
        @PathVariable Long id,
        HttpServletRequest request,
        @RequestHeader("X-User") String user) {

        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
        String ip = extractClientIp(request);
        if (!StringUtils.hasText(user)) throw new IllegalArgumentException("user is required");

        return categoryService.deleteCategoryById(id, user, ip)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")));
    }
}