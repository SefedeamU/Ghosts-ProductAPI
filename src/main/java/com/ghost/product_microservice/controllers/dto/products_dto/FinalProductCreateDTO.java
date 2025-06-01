package com.ghost.product_microservice.controllers.dto.products_dto;

import java.util.List;
import java.util.Optional;

import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.CreateProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.CreateProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.CreateProductPriceDTO;

import lombok.Data;

@Data
public class FinalProductCreateDTO {
    private CreateProductDTO product;
    private Optional<List<CreateProductImageDTO>> images;
    private Optional<List<CreateProductAttributeDTO>> attributes;
    private CreateProductPriceDTO price;
}
