package com.ghost.product_microservice.services.product_service;


import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPatchDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.GetProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.GetProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.PatchProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.GetProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.GetProductPriceDTO;
import com.ghost.product_microservice.models.Product;
import com.ghost.product_microservice.models.ProductAttribute;
import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.models.ProductImage;
import com.ghost.product_microservice.models.ProductPrice;
import com.ghost.product_microservice.repositories.product_repository.ProductAttributeRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductAuditRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductImageRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductPriceRepository;
import com.ghost.product_microservice.repositories.product_repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductAuditRepository productAuditRepository;
    private final ProductAttributeRepository productAttributeRepository;

    private final ProductAttributeService productAttributeService;
    private final ProductImageService productImageService;
    private final ProductPriceService productPriceService;

    public ProductService(ProductRepository productRepository,
                        ProductPriceRepository productPriceRepository,
                        ProductImageRepository prodcutImageRepository,
                        ProductAuditRepository productAuditRepository,
                        ProductAttributeRepository productAttributeRepository,

                        ProductAttributeService productAttributeService,
                        ProductImageService productImageService,
                        ProductPriceService productPriceService)
    {
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.productImageRepository = prodcutImageRepository;
        this.productAuditRepository = productAuditRepository;
        this.productAttributeRepository = productAttributeRepository;

        this.productAttributeService = productAttributeService;
        this.productImageService = productImageService;
        this.productPriceService = productPriceService;
    }

    private Mono<Void> logAudit(Long productId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAction(action);
        audit.setUsername(user);
        audit.setEntity("Product");
        audit.setDetails(details);
        audit.setDate(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        return productAuditRepository.save(audit).then();
    }

    //Public methods to retrieve products with pagination and details
    public Flux<FinalProductPartialDetailDTO> findAllProducts(int page, int size) {
        return productRepository.findAll()
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {

                // Map base product
                GetProductDTO product = new GetProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                // Get attributes as reactive list
                Mono<List<GetProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        GetProductAttributeDTO dto = new GetProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Get images as reactive list
                Mono<List<GetProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        GetProductImageDTO dto = new GetProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<GetProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        GetProductPriceDTO dto = new GetProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setPriceCurrency(price.getPriceCurrency());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        dto.setIsActive(price.getIsActive());
                        return dto;
                    });

                // Combine everything into the final DTO
                return Mono.zip(Mono.just(product), productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        FinalProductPartialDetailDTO dto = new FinalProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
            });
        }

    public Flux<FinalProductPartialDetailDTO> findAllProductsByCategory(Long categoryId, int page, int size) {
        return productRepository.findAllByCategoryId(categoryId)
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {

                // Map base product
                GetProductDTO product = new GetProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                // Get attributes as reactive list
                Mono<List<GetProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        GetProductAttributeDTO dto = new GetProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Get images as reactive list
                Mono<List<GetProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        GetProductImageDTO dto = new GetProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<GetProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        GetProductPriceDTO dto = new GetProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setPriceCurrency(price.getPriceCurrency());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        dto.setIsActive(price.getIsActive());

                        return dto;
                    });

                // Combine everything into the final DTO
                return Mono.zip(Mono.just(product), productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        FinalProductPartialDetailDTO dto = new FinalProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
            });
        }

    public Flux<FinalProductPartialDetailDTO> findAllProductsBySubcategory(Long subCategoryId, int page, int size) {
        return productRepository.findAllBySubcategoryId(subCategoryId)
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {

                // Map base product
                GetProductDTO product = new GetProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                // Get attributes as reactive list
                Mono<List<GetProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        GetProductAttributeDTO dto = new GetProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Get images as reactive list
                Mono<List<GetProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        GetProductImageDTO dto = new GetProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                // Map product price
                Mono<GetProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        GetProductPriceDTO dto = new GetProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setPriceCurrency(price.getPriceCurrency());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        dto.setIsActive(price.getIsActive());
                        return dto;
                    });

                // Combine everything into the final DTO
                return Mono.zip(Mono.just(product), productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        FinalProductPartialDetailDTO dto = new FinalProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
            });
        }

    public Mono<FinalProductPartialDetailDTO> findProductById(Long id) {
        return productRepository.findById(id)
            .flatMap(p -> {

                GetProductDTO product = new GetProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                Mono<List<GetProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        GetProductAttributeDTO dto = new GetProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                Mono<List<GetProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        GetProductImageDTO dto = new GetProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<GetProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        GetProductPriceDTO dto = new GetProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setPriceCurrency(price.getPriceCurrency());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        dto.setIsActive(price.getIsActive());
                        return dto;
                    });

                return Mono.zip(Mono.just(product), productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        FinalProductPartialDetailDTO dto = new FinalProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
                

            });
        }
    
    public Mono<FinalProductPartialDetailDTO> findProductByName(String name) {
        return productRepository.findByName(name)
            .flatMap(p -> {

                GetProductDTO product = new GetProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                Mono<List<GetProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        GetProductAttributeDTO dto = new GetProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                Mono<List<GetProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        GetProductImageDTO dto = new GetProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<GetProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        GetProductPriceDTO dto = new GetProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setPriceCurrency(price.getPriceCurrency());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        dto.setIsActive(price.getIsActive());
                        return dto;
                    });

                return Mono.zip(Mono.just(product), productAttributes, ProductImages, ProductPrice)
                    .map(tuple -> {
                        FinalProductPartialDetailDTO dto = new FinalProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
                });
        }

//Private methods for internal use, such as saving products, updating, etc. can be added here

    public Flux<FinalProductDetailDTO> findAllProductsWithAdminDetails(int page, int size){
        return productRepository.findAll()
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {

                // Base product
                Mono<Product> product = Mono.just(p);

                // Get images as reactive list
                Mono<List<ProductImage>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImage obj = new ProductImage();
                        obj.setId(image.getId());
                        obj.setProductId(image.getProductId());
                        obj.setUrlImg(image.getUrlImg());
                        obj.setPriority(image.getPriority());
                        return obj;
                    })
                    .collectList();

                Mono<ProductPrice> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPrice obj = new ProductPrice();
                        obj.setId(price.getId());
                        obj.setProductId(price.getProductId());
                        obj.setPrice(price.getPrice());
                        obj.setPriceCurrency(price.getPriceCurrency());
                        obj.setStartDate(price.getStartDate());
                        obj.setEndDate(price.getEndDate());
                        obj.setCreationAt(price.getCreationAt());
                        obj.setIsActive(price.getIsActive());
                        return obj;
                    });

                // Get Audits as reactive list
                Mono<List<ProductAudit>> productAudit = productAuditRepository.findByProductId(p.getId())
                    .map(audit -> {
                        ProductAudit obj = new ProductAudit();
                        obj.setId(audit.getId());
                        obj.setProductId(audit.getProductId());
                        obj.setAction(audit.getAction());
                        obj.setUsername(audit.getUsername());
                        obj.setDate(audit.getDate());
                        return obj;
                    })
                    .collectList();
                
                // Get attributes as reactive list
                Mono<List<ProductAttribute>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttribute obj = new ProductAttribute();
                        obj.setId(attr.getId());
                        obj.setProductId(attr.getProductId());
                        obj.setAttributeName(attr.getAttributeName());
                        obj.setAttributeValue(attr.getAttributeValue());
                        return obj;
                    })
                    .collectList();

                // Combine everything into the final DTO
                return Mono.zip(product, ProductImages, ProductPrice, productAudit, productAttributes)
                    .map(tuple -> {
                        FinalProductDetailDTO obj = new FinalProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Flux<FinalProductDetailDTO> findAllProductsWithAdminDetailsByCategory(Long categoryId, int page, int size){
        return productRepository.findAllByCategoryId(categoryId)
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {

                // Base product
                Mono<Product> product = Mono.just(p);

                // Get images as reactive list
                Mono<List<ProductImage>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImage obj = new ProductImage();
                        obj.setId(image.getId());
                        obj.setProductId(image.getProductId());
                        obj.setUrlImg(image.getUrlImg());
                        obj.setPriority(image.getPriority());
                        return obj;
                    })
                    .collectList();

                Mono<ProductPrice> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPrice obj = new ProductPrice();
                        obj.setId(price.getId());
                        obj.setProductId(price.getProductId());
                        obj.setPrice(price.getPrice());
                        obj.setPriceCurrency(price.getPriceCurrency());
                        obj.setStartDate(price.getStartDate());
                        obj.setEndDate(price.getEndDate());
                        obj.setCreationAt(price.getCreationAt());
                        obj.setIsActive(price.getIsActive());
                        return obj;
                    });

                // Get Audits as reactive list
                Mono<List<ProductAudit>> productAudit = productAuditRepository.findByProductId(p.getId())
                    .map(audit -> {
                        ProductAudit obj = new ProductAudit();
                        obj.setId(audit.getId());
                        obj.setProductId(audit.getProductId());
                        obj.setAction(audit.getAction());
                        obj.setUsername(audit.getUsername());
                        obj.setDate(audit.getDate());
                        return obj;
                    })
                    .collectList();
                
                // Get attributes as reactive list
                Mono<List<ProductAttribute>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttribute obj = new ProductAttribute();
                        obj.setId(attr.getId());
                        obj.setProductId(attr.getProductId());
                        obj.setAttributeName(attr.getAttributeName());
                        obj.setAttributeValue(attr.getAttributeValue());
                        return obj;
                    })
                    .collectList();

                // Combine everything into the final DTO
                return Mono.zip(product, ProductImages, ProductPrice, productAudit, productAttributes)
                    .map(tuple -> {
                        FinalProductDetailDTO obj = new FinalProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Flux<FinalProductDetailDTO> findAllProductsWithAdminDetailsBySubCategory(Long subCategoryId, int page, int size){
        return productRepository.findAllBySubcategoryId(subCategoryId)
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {

                // Base product
                Mono<Product> product = Mono.just(p);

                // Get images as reactive list
                Mono<List<ProductImage>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImage obj = new ProductImage();
                        obj.setId(image.getId());
                        obj.setProductId(image.getProductId());
                        obj.setUrlImg(image.getUrlImg());
                        obj.setPriority(image.getPriority());
                        return obj;
                    })
                    .collectList();

                Mono<ProductPrice> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPrice obj = new ProductPrice();
                        obj.setId(price.getId());
                        obj.setProductId(price.getProductId());
                        obj.setPrice(price.getPrice());
                        obj.setPriceCurrency(price.getPriceCurrency());
                        obj.setStartDate(price.getStartDate());
                        obj.setEndDate(price.getEndDate());
                        obj.setCreationAt(price.getCreationAt());
                        obj.setIsActive(price.getIsActive());
                        return obj;
                    });

                // Get Audits as reactive list
                Mono<List<ProductAudit>> productAudit = productAuditRepository.findByProductId(p.getId())
                    .map(audit -> {
                        ProductAudit obj = new ProductAudit();
                        obj.setId(audit.getId());
                        obj.setProductId(audit.getProductId());
                        obj.setAction(audit.getAction());
                        obj.setUsername(audit.getUsername());
                        obj.setDate(audit.getDate());
                        return obj;
                    })
                    .collectList();
                
                // Get attributes as reactive list
                Mono<List<ProductAttribute>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttribute obj = new ProductAttribute();
                        obj.setId(attr.getId());
                        obj.setProductId(attr.getProductId());
                        obj.setAttributeName(attr.getAttributeName());
                        obj.setAttributeValue(attr.getAttributeValue());
                        return obj;
                    })
                    .collectList();

                // Combine everything into the final DTO
                return Mono.zip(product, ProductImages, ProductPrice, productAudit, productAttributes)
                    .map(tuple -> {
                        FinalProductDetailDTO obj = new FinalProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Mono<FinalProductDetailDTO> findProductWithAdminDetailsById(Long productId){
        return productRepository.findById(productId)
            .flatMap(p -> {

                // Base product
                Mono<Product> product = Mono.just(p);

                // Get images as reactive list
                Mono<List<ProductImage>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImage obj = new ProductImage();
                        obj.setId(image.getId());
                        obj.setProductId(image.getProductId());
                        obj.setUrlImg(image.getUrlImg());
                        obj.setPriority(image.getPriority());
                        return obj;
                    })
                    .collectList();

                Mono<ProductPrice> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPrice obj = new ProductPrice();
                        obj.setId(price.getId());
                        obj.setProductId(price.getProductId());
                        obj.setPrice(price.getPrice());
                        obj.setPriceCurrency(price.getPriceCurrency());
                        obj.setStartDate(price.getStartDate());
                        obj.setEndDate(price.getEndDate());
                        obj.setCreationAt(price.getCreationAt());
                        obj.setIsActive(price.getIsActive());
                        return obj;
                    });

                // Get Audits as reactive list
                Mono<List<ProductAudit>> productAudit = productAuditRepository.findByProductId(p.getId())
                    .map(audit -> {
                        ProductAudit obj = new ProductAudit();
                        obj.setId(audit.getId());
                        obj.setProductId(audit.getProductId());
                        obj.setAction(audit.getAction());
                        obj.setUsername(audit.getUsername());
                        obj.setDate(audit.getDate());
                        return obj;
                    })
                    .collectList();
                
                // Get attributes as reactive list
                Mono<List<ProductAttribute>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttribute obj = new ProductAttribute();
                        obj.setId(attr.getId());
                        obj.setProductId(attr.getProductId());
                        obj.setAttributeName(attr.getAttributeName());
                        obj.setAttributeValue(attr.getAttributeValue());
                        return obj;
                    })
                    .collectList();

                // Combine everything into the final DTO
                return Mono.zip(product, ProductImages, ProductPrice, productAudit, productAttributes)
                    .map(tuple -> {
                        FinalProductDetailDTO obj = new FinalProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Mono<FinalProductDetailDTO> findProductWithAdminDetailsByName(String name){
        return productRepository.findByName(name)
            .flatMap(p -> {

                // Base product
                Mono<Product> product = Mono.just(p);

                // Get images as reactive list
                Mono<List<ProductImage>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImage obj = new ProductImage();
                        obj.setId(image.getId());
                        obj.setProductId(image.getProductId());
                        obj.setUrlImg(image.getUrlImg());
                        obj.setPriority(image.getPriority());
                        return obj;
                    })
                    .collectList();

                Mono<ProductPrice> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPrice obj = new ProductPrice();
                        obj.setId(price.getId());
                        obj.setProductId(price.getProductId());
                        obj.setPrice(price.getPrice());
                        obj.setPriceCurrency(price.getPriceCurrency());
                        obj.setStartDate(price.getStartDate());
                        obj.setEndDate(price.getEndDate());
                        obj.setCreationAt(price.getCreationAt());
                        obj.setIsActive(price.getIsActive());
                        return obj;
                    });

                // Get Audits as reactive list
                Mono<List<ProductAudit>> productAudit = productAuditRepository.findByProductId(p.getId())
                    .map(audit -> {
                        ProductAudit obj = new ProductAudit();
                        obj.setId(audit.getId());
                        obj.setProductId(audit.getProductId());
                        obj.setAction(audit.getAction());
                        obj.setUsername(audit.getUsername());
                        obj.setDate(audit.getDate());
                        return obj;
                    })
                    .collectList();
                
                // Get attributes as reactive list
                Mono<List<ProductAttribute>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttribute obj = new ProductAttribute();
                        obj.setId(attr.getId());
                        obj.setProductId(attr.getProductId());
                        obj.setAttributeName(attr.getAttributeName());
                        obj.setAttributeValue(attr.getAttributeValue());
                        return obj;
                    })
                    .collectList();

                // Combine everything into the final DTO
                return Mono.zip(product, ProductImages, ProductPrice, productAudit, productAttributes)
                    .map(tuple -> {
                        FinalProductDetailDTO obj = new FinalProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Mono<FinalProductDetailDTO> createProduct(FinalProductCreateDTO dto, String createdByUser, String ip) {
        Product product = new Product();
        product.setName(dto.getProduct().getName());
        product.setBrand(dto.getProduct().getBrand());
        product.setCategoryId(dto.getProduct().getCategoryId());
        product.setSubcategoryId(dto.getProduct().getSubcategoryId());
        product.setDescription(dto.getProduct().getDescription());
        product.setStock(dto.getProduct().getStock());
        product.setStatus(dto.getProduct().getStatus());
        product.setCreatedAt(LocalDateTime.now());
        product.setModificatedAt(null);
        product.setCreatedBy(dto.getProduct().getUser());
        product.setModificatedBy(null);

        return productRepository.save(product)
            .flatMap(savedProduct -> {
                Mono<Void> saveAttributes = productAttributeService.createAttributes(
                    savedProduct.getId(),
                    dto.getAttributes().orElse(Collections.emptyList()),
                    createdByUser,
                    ip
                );

                Mono<Void> saveImages = productImageService.createImages(
                    savedProduct.getId(),
                    dto.getImages().orElse(Collections.emptyList()),
                    createdByUser,
                    ip
                );

                Mono<Void> savePrice = productPriceService.createPrice(
                    savedProduct.getId(),
                    dto.getPrice(),
                    createdByUser,
                    ip
                ).then();

                return Mono.when(saveAttributes, saveImages, savePrice)
                    .then(logAudit(savedProduct.getId(), "POST", savedProduct.getCreatedBy(), "Product created", ip))
                    .then(findProductWithAdminDetailsById(savedProduct.getId()));
            });
    }

    public Mono<FinalProductDetailDTO> updateProductById(Long id, FinalProductCreateDTO dto, String updatedByUser, String ip) {
        return productRepository.findById(id)
            .flatMap(existingProduct -> {
                existingProduct.setName(dto.getProduct().getName());
                existingProduct.setBrand(dto.getProduct().getBrand());
                existingProduct.setCategoryId(dto.getProduct().getCategoryId());
                existingProduct.setSubcategoryId(dto.getProduct().getSubcategoryId());
                existingProduct.setDescription(dto.getProduct().getDescription());
                existingProduct.setStock(dto.getProduct().getStock());
                existingProduct.setStatus(dto.getProduct().getStatus());
                existingProduct.setModificatedAt(LocalDateTime.now());
                existingProduct.setModificatedBy(dto.getProduct().getUser());

                return productRepository.save(existingProduct)
                    .flatMap(savedProduct -> {
                        Mono<Void> replaceAttributes = productAttributeService.replaceAttributes(
                            id,
                            dto.getAttributes().orElse(Collections.emptyList()),
                            updatedByUser,
                            ip
                        );
                        Mono<Void> replaceImages = productImageService.replaceImages(
                            id,
                            dto.getImages().orElse(Collections.emptyList()),
                            updatedByUser,
                            ip
                        );
                        Mono<Void> replacePrice = productPriceService.updatePrice(
                            id,
                            dto.getPrice(),
                            updatedByUser,
                            ip
                        ).then();

                        return Mono.when(replaceAttributes, replaceImages, replacePrice)
                            .then(logAudit(savedProduct.getId(), "PUT", savedProduct.getModificatedBy(), "Update product", ip))
                            .then(findProductWithAdminDetailsById(id));
                    });
            });
    }

    public Mono<FinalProductDetailDTO> patchProductById(Long id, FinalProductPatchDTO dto, String updatedByUser, String ip) {
        return productRepository.findById(id)
            .flatMap(product -> {
                PatchProductDTO patch = dto.getProduct();
                if (patch != null) {
                    if (patch.getName() != null) product.setName(patch.getName());
                    if (patch.getBrand() != null) product.setBrand(patch.getBrand());
                    if (patch.getCategoryId() != null) product.setCategoryId(patch.getCategoryId());
                    if (patch.getSubcategoryId() != null) product.setSubcategoryId(patch.getSubcategoryId());
                    if (patch.getDescription() != null) product.setDescription(patch.getDescription());
                    if (patch.getStock() != null) product.setStock(patch.getStock());
                    if (patch.getStatus() != null) product.setStatus(patch.getStatus());
                    if (patch.getUser() != null) product.setModificatedBy(patch.getUser());
                }
                product.setModificatedAt(LocalDateTime.now());

                return productRepository.save(product)
                    .flatMap(savedProduct -> {
                        Mono<Void> patchAttributes = productAttributeService.patchAttributes(
                            savedProduct.getId(),
                            dto.getAttributes(),
                            updatedByUser,
                            ip
                        );
                        Mono<Void> patchImages = productImageService.patchImages(
                            savedProduct.getId(),
                            dto.getImages(),
                            updatedByUser,
                            ip
                        );
                        Mono<Void> patchPrice = Mono.empty();
                        if (dto.getPrice() != null) {
                            patchPrice = productPriceService.updatePrice(
                                savedProduct.getId(),
                                dto.getPrice(),
                                updatedByUser,
                                ip
                            ).then();
                        }

                        return Mono.when(patchAttributes, patchImages, patchPrice)
                            .then(logAudit(savedProduct.getId(), "PATCH", savedProduct.getModificatedBy(), "Partial update product", ip))
                            .then(findProductWithAdminDetailsById(savedProduct.getId()));
                    });
            });
    }

    public Mono<Boolean> deleteProductById(Long id, String deletedByUser, String ip) {
    return findProductWithAdminDetailsById(id)
        .flatMap(productDetail ->
            logAudit(productDetail.getProduct().getId(), "DELETE", deletedByUser, "Delete product", ip)
            .then(
                Mono.when(
                    productAttributeService.deleteAttributes(id, deletedByUser, ip),
                    productImageService.deleteImages(id, deletedByUser, ip),
                    productPriceService.deleteAllPrices(id, deletedByUser, ip)
                )
                .then(productRepository.deleteById(id))
                .thenReturn(true)
            )
        );
    }
}
