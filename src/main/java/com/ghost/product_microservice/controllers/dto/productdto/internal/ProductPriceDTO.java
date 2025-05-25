package com.ghost.product_microservice.controllers.dto.productdto.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductPriceDTO {
    private BigDecimal price;
    private String kindOfPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}