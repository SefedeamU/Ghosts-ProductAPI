package com.ghost.product_microservice.controllers.dto.productdto.internal.create;

import lombok.Data;

@Data
public class ProductCreateAttributeDTO {
    private String attributeName;
    private String attributeValue;
}
