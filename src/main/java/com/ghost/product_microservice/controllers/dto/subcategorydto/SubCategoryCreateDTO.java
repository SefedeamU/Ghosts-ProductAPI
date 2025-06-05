package com.ghost.product_microservice.controllers.dto.subcategorydto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubCategoryCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private Long categoryId;
}
