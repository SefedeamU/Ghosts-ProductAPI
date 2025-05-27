package com.ghost.product_microservice.controllers.dto.productdto.internal.finddetails;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String brand;
    private Long categoryId;
    private Long subcategoryId;
    private String description;
    private Integer stock;
    private String status;
}