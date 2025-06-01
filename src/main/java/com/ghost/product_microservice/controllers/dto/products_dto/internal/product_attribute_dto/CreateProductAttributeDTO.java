package com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto;

import lombok.Data;

@Data
public class CreateProductAttributeDTO {
    private String attributeName;
    private String attributeValue;
}
