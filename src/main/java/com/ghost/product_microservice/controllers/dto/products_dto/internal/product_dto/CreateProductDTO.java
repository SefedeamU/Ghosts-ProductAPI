package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String brand;
    @NotNull
    @Positive
    private Long categoryId;
    @NotNull
    @Positive
    private Long subcategoryId;
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private Integer stock;
    @NotBlank
    private String status;
    @NotBlank
    private String user;
}
