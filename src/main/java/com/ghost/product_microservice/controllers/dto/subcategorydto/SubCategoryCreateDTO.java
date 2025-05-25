package com.ghost.product_microservice.controllers.dto.subcategorydto;

import lombok.Data;

@Data
public class SubCategoryCreateDTO {
    private String name;
    private String description;
    private Long categoryId;
}
