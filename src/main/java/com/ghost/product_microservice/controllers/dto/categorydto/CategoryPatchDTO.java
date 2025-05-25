package com.ghost.product_microservice.controllers.dto.categorydto;

import java.util.Optional;

import lombok.Data;

@Data
public class CategoryPatchDTO {
    private Optional<String> name;
    private Optional<String> description;
}
