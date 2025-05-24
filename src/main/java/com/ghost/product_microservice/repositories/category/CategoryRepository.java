package com.ghost.product_microservice.repositories.category;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.ghost.product_microservice.models.Category;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

}