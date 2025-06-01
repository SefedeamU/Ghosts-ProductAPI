package com.ghost.product_microservice.controllers.dto.products_dto;

import java.util.List;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.PatchProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.PatchProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.PatchProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.PatchProductPriceDTO;
import lombok.Data;

@Data
public class FinalProductPatchDTO {
    private PatchProductDTO product;
    private List<PatchProductImageDTO> images;
    private List<PatchProductAttributeDTO> attributes;
    private PatchProductPriceDTO price;
}