package com.ghost.product_microservice.services.product;

import com.ghost.product_microservice.controllers.dto.productdto.ProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductImageDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.ProductPriceDTO;

import com.ghost.product_microservice.repositories.product.ProductAttributeRepository;
import com.ghost.product_microservice.repositories.product.ProductAuditRepository;
import com.ghost.product_microservice.repositories.product.ProductPriceRepository;
import com.ghost.product_microservice.repositories.product.ProductRepository;
import com.ghost.product_microservice.repositories.product.ProductImageRepository;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductAuditRepository productAuditRepository;
    private final ProductAttributeRepository productAttributeRepository;

    public ProductService(ProductRepository productRepository,
                        ProductPriceRepository productPriceRepository,
                        ProductImageRepository prodcutImageRepository,
                        ProductAuditRepository productAuditRepository,
                        ProductAttributeRepository productAttributeRepository) 
    {
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.productImageRepository = prodcutImageRepository;
        this.productAuditRepository = productAuditRepository;
        this.productAttributeRepository = productAttributeRepository;

    }

    /*
    public class ProductPartialDetailDTO {
        private ProductDTO product;
        private ProductPriceDTO price;
        private List<ProductImageDTO> images;
        private List<ProductAttributeDTO> attributes;
    }*/

    public Flux<ProductPartialDetailDTO> findAllProducts(int page, int size) {
        return productRepository.findAll()
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {
                // Mapear producto base
                Mono<ProductDTO> product = productRepository.findById(p.getId())
                .map(prod -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(prod.getId());
                    dto.setName(prod.getName());
                    dto.setDescription(prod.getDescription());
                    dto.setStock(prod.getStock());
                    dto.setCategoryId(prod.getCategoryId());
                    dto.setSubcategoryId(prod.getSubcategoryId());
                    dto.setBrand(prod.getBrand());
                    dto.setStatus(prod.getStatus());
                    return dto;
                });

                // Obtener atributos como lista reactiva
                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Obtener imágenes como lista reactiva
                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductId(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setKindOfPrice(price.getKindOfPrice());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        return dto;
                    });

                // Combinar todo en el DTO final
                return Mono.zip(product, productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        ProductPartialDetailDTO dto = new ProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
            });
        }
    
    public Flux<ProductPartialDetailDTO> findAllProductsByCategory(Long categoryId, int page, int size) {
        return productRepository.findAllByCategoryId(categoryId)
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {
                // Mapear producto base
                Mono<ProductDTO> product = productRepository.findById(p.getId())
                .map(prod -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(prod.getId());
                    dto.setName(prod.getName());
                    dto.setDescription(prod.getDescription());
                    dto.setStock(prod.getStock());
                    dto.setCategoryId(prod.getCategoryId());
                    dto.setSubcategoryId(prod.getSubcategoryId());
                    dto.setBrand(prod.getBrand());
                    dto.setStatus(prod.getStatus());
                    return dto;
                });

                // Obtener atributos como lista reactiva
                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Obtener imágenes como lista reactiva
                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductId(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setKindOfPrice(price.getKindOfPrice());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        return dto;
                    });

                // Combinar todo en el DTO final
                return Mono.zip(product, productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        ProductPartialDetailDTO dto = new ProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
            });
        }

    public Flux<ProductPartialDetailDTO> findAllProductsBySubcategory(Long subCategoryId, int page, int size) {
        return productRepository.findAllBySubcategoryId(subCategoryId)
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {
                // Mapear producto base
                Mono<ProductDTO> product = productRepository.findById(p.getId())
                .map(prod -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(prod.getId());
                    dto.setName(prod.getName());
                    dto.setDescription(prod.getDescription());
                    dto.setStock(prod.getStock());
                    dto.setCategoryId(prod.getCategoryId());
                    dto.setSubcategoryId(prod.getSubcategoryId());
                    dto.setBrand(prod.getBrand());
                    dto.setStatus(prod.getStatus());
                    return dto;
                });

                // Obtener atributos como lista reactiva
                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Obtener imágenes como lista reactiva
                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductId(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setKindOfPrice(price.getKindOfPrice());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        return dto;
                    });

                // Combinar todo en el DTO final
                return Mono.zip(product, productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        ProductPartialDetailDTO dto = new ProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
            });
        }

}
