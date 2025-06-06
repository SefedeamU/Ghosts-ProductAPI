package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Table("product_audit")
public class ProductAudit {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;
    @Column("category_id")
    private Long categoryId;
    @Column("subcategory_id")
    private Long subcategoryId;

    private String action;
    private String username;
    private String entity;
    private String details;
    private String ipAddress;
    private LocalDateTime date;
}