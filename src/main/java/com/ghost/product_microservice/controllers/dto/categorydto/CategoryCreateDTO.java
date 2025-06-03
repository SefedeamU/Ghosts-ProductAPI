package com.ghost.product_microservice.controllers.dto.categorydto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryCreateDTO {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
}
