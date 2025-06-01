package com.ghost.product_microservice.controllers.dto.products_dto;

import java.util.List;

import com.ghost.product_microservice.models.Product;
import com.ghost.product_microservice.models.ProductAttribute;
import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.models.ProductImage;
import com.ghost.product_microservice.models.ProductPrice;

import lombok.Data;

@Data
public class FinalProductDetailDTO {
    private Product product;
    private List<ProductImage> images;
    private List<ProductAttribute> attributes;
    private List<ProductAudit> audits;
    private ProductPrice price;
}
