package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto;

import lombok.Data;

@Data
public class PatchProductImageDTO {
    private Integer priority;
    private String urlImg;
}