package com.ghost.product_microservice.services.product;


import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.FinalProductPatchDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_attribute_dto.GetProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.GetProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_dto.PatchProductDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.CreateProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_image_dto.GetProductImageDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.GetProductPriceDTO;
import com.ghost.product_microservice.controllers.dto.products_dto.internal.product_price_dto.PatchProductPriceDTO;
import com.ghost.product_microservice.models.Product;
import com.ghost.product_microservice.models.ProductAttribute;
import com.ghost.product_microservice.models.ProductAudit;
import com.ghost.product_microservice.models.ProductImage;
import com.ghost.product_microservice.models.ProductPrice;
import com.ghost.product_microservice.repositories.product.ProductAttributeRepository;
import com.ghost.product_microservice.repositories.product.ProductAuditRepository;
import com.ghost.product_microservice.repositories.product.ProductPriceRepository;
import com.ghost.product_microservice.repositories.product.ProductRepository;
import com.ghost.product_microservice.repositories.product.ProductImageRepository;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Mono<Void> logAudit(Long productId, String action, String user, String details, String ipAddress) {
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAction(action);
        audit.setUser(user);
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
                        obj.setUser(audit.getUser());
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
                        obj.setUser(audit.getUser());
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
                        obj.setUser(audit.getUser());
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
                        obj.setUser(audit.getUser());
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
                        obj.setUser(audit.getUser());
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

    public Mono<FinalProductDetailDTO> createProduct(FinalProductCreateDTO dto, String deletedByUser, String ip) {
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
                Mono<Void> saveAttributes = Mono.empty();
                if (dto.getAttributes().isPresent()) {
                    saveAttributes = Flux.fromIterable(dto.getAttributes().get())
                        .map(attrDto -> {
                            ProductAttribute attribute = new ProductAttribute();
                            attribute.setProductId(savedProduct.getId());
                            attribute.setAttributeName(attrDto.getAttributeName());
                            attribute.setAttributeValue(attrDto.getAttributeValue());
                            return attribute;
                        })
                        .flatMap(productAttributeRepository::save)
                        .then();
                }

                Mono<Void> saveImages = Mono.fromRunnable(() -> {})
                    .then(Mono.defer(() -> {
                        List<ProductImage> images = new ArrayList<>();
                        List<Integer> usedPriorities = new ArrayList<>();
                        if (dto.getImages().isPresent()) {
                            List<CreateProductImageDTO> imageDTOs = dto.getImages().get();
                            if (imageDTOs.size() > 5) {
                                throw new IllegalArgumentException("Images cannot exceed 5.");
                            }
                            for (CreateProductImageDTO createImageDTO : imageDTOs) {
                                ProductImage image = new ProductImage();
                                image.setUrlImg(createImageDTO.getUrlImg());
                                image.setPriority(createImageDTO.getPriority());
                                image.setProductId(savedProduct.getId());
                                images.add(image);
                                usedPriorities.add(createImageDTO.getPriority());
                            }
                        }
                        String genericUrl = "https://example.com/generic-image.jpg";
                        for (int i = 1; i <= 5; i++) {
                            if (!usedPriorities.contains(i)) {
                                ProductImage image = new ProductImage();
                                image.setUrlImg(genericUrl);
                                image.setPriority(i);
                                image.setProductId(savedProduct.getId());
                                images.add(image);
                            }
                        }
                        return Flux.fromIterable(images)
                            .flatMap(productImageRepository::save)
                            .then();
                    }));

                Mono<Void> savePrice = Mono.defer(() -> {
                    ProductPrice price = new ProductPrice();
                    price.setPrice(dto.getPrice().getPrice());
                    price.setPriceCurrency(dto.getPrice().getPriceCurrency());
                    price.setStartDate(LocalDateTime.now());
                    price.setCreationAt(LocalDateTime.now());
                    price.setProductId(savedProduct.getId());
                    price.setIsActive(true);
                    price.setEndDate(null);
                    return productPriceRepository.save(price).then();
                });

                return Mono.when(saveAttributes, saveImages, savePrice)
                .then(logAudit(savedProduct.getId(), "CREATE", savedProduct.getCreatedBy(), "Product created", ip))
                .then(findProductWithAdminDetailsById(savedProduct.getId()));
            });
    }

    public Mono<FinalProductDetailDTO> updateProductById(Long id, FinalProductCreateDTO dto, String deletedByUser, String ip) {
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
                Mono<Void> replaceAttributes = productAttributeRepository.deleteAllByProductId(id)
                    .thenMany(
                        dto.getAttributes().isPresent() ? Flux.fromIterable(dto.getAttributes().get())
                        .map(attrDto -> {
                            ProductAttribute attr = new ProductAttribute();
                            attr.setProductId(id);
                            attr.setAttributeName(attrDto.getAttributeName());
                            attr.setAttributeValue(attrDto.getAttributeValue());
                            return attr;
                        })
                            .collectList()
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(productAttributeRepository::save): Flux.empty()
                            )
                .then();

                Mono<Void> replaceImages = productImageRepository.deleteAllByProductId(id)
                    .thenMany(
                        dto.getImages().isPresent() ?
                            Mono.fromCallable(() -> {
                                List<ProductImage> images = new ArrayList<>();
                                List<Integer> usedPriorities = new ArrayList<>();
                                List<CreateProductImageDTO> imageDTOs = dto.getImages().get();
                                if (imageDTOs.size() > 5) {
                                    throw new IllegalArgumentException("Images cannot exceed 5.");
                                }
                                for (CreateProductImageDTO imgDto : imageDTOs) {
                                    ProductImage img = new ProductImage();
                                    img.setProductId(id);
                                    img.setUrlImg(imgDto.getUrlImg());
                                    img.setPriority(imgDto.getPriority());
                                    images.add(img);
                                    usedPriorities.add(imgDto.getPriority());
                                }
                                if (imageDTOs.size() < 5) {
                                    String genericUrl = "https://example.com/generic-image.jpg";
                                    for (int i = 1; i <= 5; i++) {
                                        if (!usedPriorities.contains(i)) {
                                            ProductImage img = new ProductImage();
                                            img.setProductId(id);
                                            img.setUrlImg(genericUrl);
                                            img.setPriority(i);
                                            images.add(img);
                                        }
                                    }
                                }
                            return images;
                        })
                .flatMapMany(Flux::fromIterable)
                .flatMap(productImageRepository::save): Flux.empty()
                )
                .then();

                Mono<Void> replacePrice = productPriceRepository.findByProductIdAndIsActiveTrue(id)
                    .flatMap(oldPrice -> {
                            oldPrice.setIsActive(false);
                            oldPrice.setEndDate(LocalDateTime.now());
                        return productPriceRepository.save(oldPrice);
                    })
                    .then(Mono.defer(() -> {
                            ProductPrice price = new ProductPrice();
                            price.setProductId(id);
                            price.setPrice(dto.getPrice().getPrice());
                            price.setPriceCurrency(dto.getPrice().getPriceCurrency());
                            price.setStartDate(LocalDateTime.now());
                            price.setCreationAt(LocalDateTime.now());
                            price.setIsActive(true);
                            price.setEndDate(null);
                        return productPriceRepository.save(price);
                    }))
                .then();

                return Mono.when(replaceAttributes, replaceImages, replacePrice)
                .then(logAudit(savedProduct.getId(), "PUT", savedProduct.getCreatedBy(), "Update product", ip))
                .then(findProductWithAdminDetailsById(id));
            });
        });
    }

    public Mono<FinalProductDetailDTO> patchProductById(Long id, FinalProductPatchDTO dto, String deletedByUser, String ip) {
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
                product.setModificatedAt(java.time.LocalDateTime.now());

                return productRepository.save(product)
                    .flatMap(savedProduct -> {

                        Mono<Void> patchImages = Mono.empty();
                        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                            patchImages = Flux.fromIterable(dto.getImages())
                                .flatMap(imgDto ->
                                    productImageRepository.findByProductIdAndPriority(savedProduct.getId(), imgDto.getPriority())
                                        .flatMap(existingImage -> {
                                            existingImage.setUrlImg(imgDto.getUrlImg());
                                            return productImageRepository.save(existingImage);
                                        })
                                )
                                .then();
                        }

                        Mono<Void> patchAttributes = Mono.empty();
                        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
                            patchAttributes = Flux.fromIterable(dto.getAttributes())
                                .flatMap(attrDto ->
                                    productAttributeRepository.findByProductIdAndAttributeName(savedProduct.getId(), attrDto.getAttributeName())
                                        .flatMap(existingAttr -> {
                                            existingAttr.setAttributeValue(attrDto.getAttributeValue());
                                            return productAttributeRepository.save(existingAttr);
                                        })
                                )
                                .then();
                        }

                        Mono<Void> patchPrice = Mono.empty();
                        PatchProductPriceDTO patchPriceDTO = dto.getPrice();
                        if (patchPriceDTO != null && patchPriceDTO.getPrice() != null && patchPriceDTO.getPriceCurrency() != null) {
                            patchPrice = productPriceRepository.findByProductIdAndIsActiveTrue(savedProduct.getId())
                                .flatMap(oldPrice -> {
                                    oldPrice.setIsActive(false);
                                    oldPrice.setEndDate(java.time.LocalDateTime.now());
                                    return productPriceRepository.save(oldPrice);
                                })
                                .then(Mono.defer(() -> {
                                    ProductPrice price = new ProductPrice();
                                    price.setProductId(savedProduct.getId());
                                    price.setPrice(patchPriceDTO.getPrice());
                                    price.setPriceCurrency(patchPriceDTO.getPriceCurrency());
                                    price.setStartDate(java.time.LocalDateTime.now());
                                    price.setCreationAt(java.time.LocalDateTime.now());
                                    price.setIsActive(true);
                                    price.setEndDate(null);
                                    return productPriceRepository.save(price);
                                }))
                                .then();
                        }

                        return Mono.when(patchImages, patchAttributes, patchPrice)
                            .then(logAudit(savedProduct.getId(), "PATCH", savedProduct.getCreatedBy(), "Partial update product", ip))
                            .then(findProductWithAdminDetailsById(savedProduct.getId()));
                    });
            });
    }

    public Mono<FinalProductDetailDTO> deleteProductById(Long id, String deletedByUser, String ip) {
        return findProductWithAdminDetailsById(id)
            .flatMap(productDetail ->
                Mono.when(
                    productAttributeRepository.deleteAllByProductId(id),
                    productImageRepository.deleteAllByProductId(id),
                    productPriceRepository.deleteAllByProductId(id)
                )
                .then(productRepository.deleteById(id))
                .then(logAudit(productDetail.getProduct().getId(), "DELETE", deletedByUser, "Delete product", ip))
                .thenReturn(productDetail)
            );
    }
}
