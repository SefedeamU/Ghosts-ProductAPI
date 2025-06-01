package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GetProductPriceDTO {
    private Long id;
    private BigDecimal price;
    private String priceCurrency;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;

}
