package com.ghost.product_microservice.controllers.dto.categorydto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
