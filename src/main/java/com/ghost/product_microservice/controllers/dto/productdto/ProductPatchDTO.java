package com.ghost.product_microservice.controllers.dto.productdto;

import java.util.List;
import java.util.Optional;

import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductImageDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductPriceDTO;

import lombok.Data;

@Data
public class ProductPatchDTO {
    private Optional<ProductDTO> product;
    private Optional<List<ProductImageDTO>> images;
    private Optional<ProductPriceDTO> price;
    private Optional<List<ProductAttributeDTO>> attributes;
    private Optional<List<Long>> categoryIds;
}
