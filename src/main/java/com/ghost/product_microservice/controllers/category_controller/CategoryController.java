package com.ghost.product_microservice.controllers.category_controller;

import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryPatchDTO;

import com.ghost.product_microservice.services.category_service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @GetMapping
    public Flux<CategoryDetailDTO> listCategories(){

        return categoryService.findAllCategories();
    }

    @GetMapping("{id}")
    public Mono<CategoryDetailDTO> getCategoryById(@RequestParam Long id) {
        return categoryService.findCategoryById(id);
    }

    @GetMapping("/by-name")
    public Mono<CategoryDetailDTO> getCategoryByName(@RequestParam String name) {
        return categoryService.findCategoryByName(name);
    }

    @PostMapping()
    public Mono<CategoryDetailDTO> postCategory(
        @RequestBody CategoryDetailDTO categoryDTO,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return categoryService.createCategory(categoryDTO, user, ip);
    }

    @PutMapping("/{id}")
    public Mono<CategoryDetailDTO> updateCategory(
        @PathVariable Long id,
        @RequestBody CategoryCreateDTO categoryDTO,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return categoryService.updateCategoryById(id, categoryDTO, user, ip);
    }

    @PatchMapping("/{id}")
    public Mono<CategoryDetailDTO> patchCategory(
        @PathVariable Long id,
        @RequestBody CategoryPatchDTO categoryDTO,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return categoryService.patchCategoryById(id, categoryDTO, user, ip);
    }

    @DeleteMapping("/{id}")
    public Mono<CategoryDetailDTO> deleteCategory(
        @PathVariable Long id,
        HttpServletRequest request,
        @RequestBody String user) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return categoryService.deleteCategoryById(id, user, ip);
    }
}
