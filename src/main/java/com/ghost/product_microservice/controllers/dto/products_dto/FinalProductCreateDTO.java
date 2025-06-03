package com.ghost.product_microservice.controllers.dto.products_dto;

import java.util.List;
import java.util.Optional;

import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.CreateProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.CreateProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.CreateProductPriceDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinalProductCreateDTO {
    @NotNull
    @Valid
    private CreateProductDTO product;

    @Valid
    private Optional<List<CreateProductImageDTO>> images = Optional.empty();

    @Valid
    private Optional<List<CreateProductAttributeDTO>> attributes = Optional.empty();

    @NotNull
    @Valid
    private CreateProductPriceDTO price;
}
