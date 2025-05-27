package com.ghost.product_microservice.controllers.dto.productdto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductPatchDTO {
    private String user;
    private String name;
    private String brand;
    private Long categoryId;
    private Long subcategoryId;
    private String description;
    private Integer stock;
    private String status;

    private String urlImg;
    private Integer priority;
    private Long productId;

    private BigDecimal price;
    private String priceCurrency;

    private String attributeName;
    private String attributeValue;
}
