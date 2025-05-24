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

    private String nombre;
    private String descripcion;
    private Integer stock;
    private String estado;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column("fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column("creado_por")
    private String creadoPor;

    @Column("modificado_por")
    private String modificadoPor;
}