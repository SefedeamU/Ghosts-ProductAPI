package com.ghost.product_microservice.controllers.category;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghost.product_microservice.controllers.dto.categorydto.CategoryCreateDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryDetailDTO;
import com.ghost.product_microservice.controllers.dto.categorydto.CategoryPatchDTO;
import com.ghost.product_microservice.services.category.CategoryService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<CategoryDetailDTO> listCategories() {
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
    public Mono<CategoryCreateDTO> postCategory(@RequestBody CategoryDetailDTO categoryDTO) {
        return categoryService.createCategory(categoryDTO);
    }

    @PutMapping("/{id}")
    public Mono<CategoryCreateDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryCreateDTO categoryDTO) {
        return categoryService.updateCategoryById(id, categoryDTO);
    }

    @PatchMapping("/{id}")
    public Mono<CategoryPatchDTO> patchCategory(@PathVariable Long id, @RequestBody CategoryPatchDTO categoryDTO) {
        return categoryService.patchCategoryById(id, categoryDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
    }
}
