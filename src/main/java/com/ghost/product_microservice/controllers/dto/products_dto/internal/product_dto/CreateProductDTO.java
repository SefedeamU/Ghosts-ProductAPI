package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto;

import lombok.Data;

@Data
public class CreateProductDTO {
    private String name;
    private String brand;
    private Long categoryId;
    private Long subcategoryId;
    private String description;
    private Integer stock;
    private String status;
    private String user;
}
