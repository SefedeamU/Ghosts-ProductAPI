package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import io.micrometer.common.lang.Nullable;

import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("product_price")
public class ProductPrice {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    private BigDecimal price;

    @Column("price_currency")
    private String priceCurrency;

    @Column("start_date")
    private LocalDateTime startDate;

    @Nullable
    @Column("end_date")
    private LocalDateTime endDate;

    @Column("is_active")
    private Boolean isActive;

    @Column("fecha_creacion")
    private LocalDateTime creationAt;
}