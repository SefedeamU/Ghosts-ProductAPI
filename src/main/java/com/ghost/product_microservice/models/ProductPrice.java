package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
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

    private BigDecimal precio;

    @Column("tipo_precio")
    private String tipoPrecio;

    @Column("fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column("fecha_fin")
    private LocalDateTime fechaFin;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
}