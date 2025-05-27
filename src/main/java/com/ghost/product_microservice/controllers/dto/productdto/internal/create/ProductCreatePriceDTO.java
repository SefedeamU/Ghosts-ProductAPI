package com.ghost.product_microservice.controllers.dto.productdto.internal.create;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductCreatePriceDTO {
    private BigDecimal price;
    private String priceCurrency;
}
