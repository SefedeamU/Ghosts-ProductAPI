package com.ghost.product_microservice.controllers.dto.productdto.internal;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private Long categoryId;
    private Long subcategoryId;
    private String description;
    private Integer stock;
    private String status;
}