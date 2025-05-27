package com.ghost.product_microservice.controllers.dto.productdto.internal.create;

import lombok.Data;

@Data
public class PartialProductCreateDTO {
    private String user;
    private String name;
    private String brand;
    private Long categoryId;
    private Long subcategoryId;
    private String description;
    private Integer stock;
    private String status;
}
