package com.ghost.product_microservice.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Data;

@Data
@Table("category_subcategory")
public class SubCategory {
    @Id
    private Long id;
    private String name;
    private String description;
    
    @Column("category_id")
    private Long categoryId;
}
