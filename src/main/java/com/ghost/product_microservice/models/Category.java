package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("category")
public class Category {

    @Id
    private Long id;

    private String nombre;
    private String descripcion;
}