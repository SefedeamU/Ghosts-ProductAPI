package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductPriceDTO {
    @NotNull
    private BigDecimal price;
    @NotBlank
    private String priceCurrency;
}