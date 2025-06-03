package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String brand;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long subcategoryId;
    @NotBlank
    private String description;
    @NotNull
    private Integer stock;
    @NotBlank
    private String status;
    @NotBlank
    private String user;
}
