package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateProductPriceDTO {
    private BigDecimal price;
    private String priceCurrency;
}
