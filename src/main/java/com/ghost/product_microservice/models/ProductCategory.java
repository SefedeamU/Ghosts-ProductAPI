package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Table("product_category")
public class ProductCategory {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("category_id")
    private Long categoryId;
}