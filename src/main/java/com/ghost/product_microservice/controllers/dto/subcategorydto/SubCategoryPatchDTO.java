package com.ghost.product_microservice.controllers.dto.subcategorydto;

import lombok.Data;

@Data
public class SubCategoryPatchDTO {
    private String name;
    private String description;
    private Long categoryId;
}
