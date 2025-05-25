package com.ghost.product_microservice.controllers.dto.productdto;

import java.util.List;

import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductImageDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductPriceDTO;

import lombok.Data;

@Data
public class ProductPartialDetailDTO {
    private ProductDTO product;
    private ProductPriceDTO price;
    private List<ProductImageDTO> images;
    private List<ProductAttributeDTO> attributes;
}