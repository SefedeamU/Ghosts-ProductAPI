package com.ghost.product_microservice.controllers.dto.productdto;


import java.util.List;
import java.util.Optional;

import com.ghost.product_microservice.controllers.dto.productdto.internal.create.PartialProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.create.ProductCreateImageDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.create.ProductCreatePriceDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.finddetails.ProductAttributeDTO;

import lombok.Data;

@Data
public class ProductCreateDTO {
    
    private PartialProductCreateDTO partialProductCreate;
    private Optional<List<ProductCreateImageDTO>> images;
    private ProductCreatePriceDTO price;
    private Optional<List<ProductAttributeDTO>> attributes;
}
