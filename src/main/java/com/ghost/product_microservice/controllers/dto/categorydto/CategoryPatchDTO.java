package com.ghost.product_microservice.controllers.dto.categorydto;

import lombok.Data;

@Data
public class CategoryPatchDTO {
    private String name;
    private String description;
}
