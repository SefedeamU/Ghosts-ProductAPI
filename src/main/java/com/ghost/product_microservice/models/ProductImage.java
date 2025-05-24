package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Table("product_image")
public class ProductImage {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("url_imagen")
    private String urlImagen;

    private Integer orden;
}