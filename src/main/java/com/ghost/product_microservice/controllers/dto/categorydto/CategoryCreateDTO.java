package com.ghost.product_microservice.controllers.dto.categorydto;

import lombok.Data;

@Data
public class CategoryCreateDTO {
    private String name;
    private String description;
}
