package com.ghost.product_microservice.controllers.dto.subcategorydto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubCategoryCreateDTO {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @NotBlank
    private Long categoryId;
}
