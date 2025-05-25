package com.ghost.product_microservice.controllers.dto.productdto;

import java.util.List;

import com.ghost.product_microservice.models.Product;
import com.ghost.product_microservice.models.ProductImage;
import com.ghost.product_microservice.models.ProductPrice;
import com.ghost.product_microservice.models.ProductAttribute;
import com.ghost.product_microservice.models.ProductAudit;

import lombok.Data;

@Data
public class ProductDetailDTO {
    private Product product;
    private List<ProductImage> images;
    private List<ProductPrice> prices;
    private List<ProductAudit> audits;
    private List<ProductAttribute> attributes;
}