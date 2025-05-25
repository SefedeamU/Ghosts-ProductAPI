package com.ghost.product_microservice.controllers.dto.productdto;

import java.util.List;
import java.util.Optional;

import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductImageDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductPriceDTO;
import com.ghost.product_microservice.models.Product;

import lombok.Data;

@Data
public class ProductCreateDTO {
    private Product product;
    private Optional<List<ProductImageDTO>> images;
    private ProductPriceDTO price;
    private Optional<List<ProductAttributeDTO>> attributes;
}
