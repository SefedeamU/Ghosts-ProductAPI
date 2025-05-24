package com.ghost.product_microservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Table("product_attribute")
public class ProductAttribute {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("nombre_atributo")
    private String nombreAtributo;

    @Column("valor_atributo")
    private String valorAtributo;
}