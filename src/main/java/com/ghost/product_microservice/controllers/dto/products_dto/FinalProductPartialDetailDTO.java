package com.ghost.product_microservice.controllers.dto.products_dto;

import java.util.List;

import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.GetProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.GetProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.GetProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.GetProductPriceDTO;

import lombok.Data;

@Data
public class FinalProductPartialDetailDTO {
    private GetProductDTO product;
    private GetProductPriceDTO price;
    private List<GetProductImageDTO> images;
    private List<GetProductAttributeDTO> attributes;
}
