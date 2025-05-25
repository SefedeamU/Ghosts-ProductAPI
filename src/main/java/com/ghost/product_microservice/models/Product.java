package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Table("product")
public class Product {

    @Id
    private Long id;

    private String name;
    private String description;
    private Integer stock;
    private String status;

    @Column("category_id")
    private long categoryId;

    @Column("subcategory_id")
    private Long subcategoryId;

    @Column("fecha_creacion")
    private LocalDateTime createdAt;

    @Column("fecha_modificacion")
    private LocalDateTime modificatedAt;

    @Column("creado_por")
    private String createdBy;

    @Column("modificado_por")
    private String modificatedBy;
}