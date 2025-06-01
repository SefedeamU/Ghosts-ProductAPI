package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto;

import lombok.Data;

@Data
public class GetProductImageDTO {
    private Long id;
    private String urlImg;
    private Integer priority;
}
