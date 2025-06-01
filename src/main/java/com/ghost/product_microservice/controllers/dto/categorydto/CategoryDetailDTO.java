package com.ghost.product_microservice.controllers.dto.categorydto;

import lombok.Data;

@Data
public class CategoryDetailDTO {
    private Long id;
    private String name;
    private String description;
}
