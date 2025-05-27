package com.ghost.product_microservice.controllers.dto.productdto.internal.create;

import lombok.Data;

@Data
public class ProductCreateImageDTO {
    private String urlImg;
    private Integer priority;
    private Long productId;
}