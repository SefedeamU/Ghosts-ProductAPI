package com.ghost.product_microservice.services.product;

import com.ghost.product_microservice.controllers.dto.productdto.ProductCreateDTO;
import com.ghost.product_microservice.controllers.dto.productdto.ProductDetailDTO;
import com.ghost.product_microservice.controllers.dto.productdto.ProductPartialDetailDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.create.ProductCreateImageDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.finddetails.ProductAttributeDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.finddetails.ProductDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.finddetails.ProductImageDTO;
import com.ghost.product_microservice.controllers.dto.productdto.internal.finddetails.ProductPriceDTO;
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

//Public methods to retrieve products with pagination and details

    public Flux<ProductPartialDetailDTO> findAllProducts(int page, int size) {
        return productRepository.findAll()
            .skip((long) page*size)
            .take(size)
            .flatMap(p -> {

                // Map base product
                ProductDTO product = new ProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                // Get attributes as reactive list
                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Get images as reactive list
                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
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

                // Map base product
                ProductDTO product = new ProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                // Get attributes as reactive list
                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Get images as reactive list
                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
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

                // Map base product
                ProductDTO product = new ProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                // Get attributes as reactive list
                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                // Get images as reactive list
                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                // Map product price
                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
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
                        ProductPartialDetailDTO dto = new ProductPartialDetailDTO();
                        dto.setProduct(tuple.getT1());
                        dto.setAttributes(tuple.getT2());
                        dto.setImages(tuple.getT3());
                        dto.setPrice(tuple.getT4());
                        return dto;
                    });
            });
        }

    public Mono<ProductPartialDetailDTO> findProductById(Long id) {
        return productRepository.findById(id)
            .flatMap(p -> {

                ProductDTO product = new ProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setPriceCurrency(price.getPriceCurrency());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        dto.setIsActive(price.getIsActive());
                        return dto;
                    });

                return Mono.zip(Mono.just(product), productAttributes, ProductImages, ProductPrice)
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
    
    public Mono<ProductPartialDetailDTO> findProductByName(String name) {
        return productRepository.findByName(name)
            .flatMap(p -> {

                ProductDTO product = new ProductDTO();
                    product.setId(p.getId());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    product.setStock(p.getStock());
                    product.setCategoryId(p.getCategoryId());
                    product.setSubcategoryId(p.getSubcategoryId());
                    product.setBrand(p.getBrand());
                    product.setStatus(p.getStatus());;

                Mono<List<ProductAttributeDTO>> productAttributes = productAttributeRepository.findAllByProductId(p.getId())
                    .map(attr -> {
                        ProductAttributeDTO dto = new ProductAttributeDTO();
                        dto.setAttributeName(attr.getAttributeName());
                        dto.setAttributeValue(attr.getAttributeValue());
                        return dto;
                    })
                    .collectList();

                Mono<List<ProductImageDTO>> ProductImages = productImageRepository.findAllByProductId(p.getId())
                    .map(image -> {
                        ProductImageDTO dto = new ProductImageDTO();
                        dto.setUrlImg(image.getUrlImg());
                        dto.setPriority(image.getPriority());
                        return dto;
                    })
                    .collectList();

                Mono<ProductPriceDTO> ProductPrice = productPriceRepository.findByProductIdAndIsActiveTrue(p.getId())
                    .map(price -> {
                        ProductPriceDTO dto = new ProductPriceDTO();
                        dto.setPrice(price.getPrice());
                        dto.setPriceCurrency(price.getPriceCurrency());
                        dto.setStartDate(price.getStartDate());
                        dto.setEndDate(price.getEndDate());
                        dto.setIsActive(price.getIsActive());
                        return dto;
                    });

                return Mono.zip(Mono.just(product), productAttributes, ProductImages, ProductPrice)
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

//Private methods for internal use, such as saving products, updating, etc. can be added here

    public Flux<ProductDetailDTO> findAllProductsWithAdminDetails(int page, int size){
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
                        ProductDetailDTO obj = new ProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Flux<ProductDetailDTO> findAllProductsWithAdminDetailsByCategory(Long categoryId, int page, int size){
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
                        ProductDetailDTO obj = new ProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Flux<ProductDetailDTO> findAllProductsWithAdminDetailsBySubCategory(Long subCategoryId, int page, int size){
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
                        ProductDetailDTO obj = new ProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Mono<ProductDetailDTO> findProductWithAdminDetailsById(Long productId){
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
                        ProductDetailDTO obj = new ProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Mono<ProductDetailDTO> findProductWithAdminDetailsByName(String name){
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
                        ProductDetailDTO obj = new ProductDetailDTO();
                        obj.setProduct(tuple.getT1());
                        obj.setImages(tuple.getT2());
                        obj.setPrice(tuple.getT3());
                        obj.setAudits(tuple.getT4());
                        obj.setAttributes(tuple.getT5());
                        return obj;
                    });
            });
    }

    public Mono<ProductDetailDTO> createProduct(ProductCreateDTO dto) {

        Product product = new Product();
            product.setName(dto.getPartialProductCreate().getName());
            product.setBrand(dto.getPartialProductCreate().getBrand());
            product.setCategoryId(dto.getPartialProductCreate().getCategoryId());
            product.setSubcategoryId(dto.getPartialProductCreate().getSubcategoryId());
            product.setDescription(dto.getPartialProductCreate().getDescription());
            product.setStock(dto.getPartialProductCreate().getStock());
            product.setStatus(dto.getPartialProductCreate().getStatus());
        
            product.setCreatedAt(LocalDateTime.now());
            product.setModificatedAt(null);
            product.setCreatedBy(dto.getPartialProductCreate().getUser());
            product.setModificatedBy(null);
        
        // Save the product and then create attributes, images, and price
        return productRepository.save(product)
            .flatMap(savedProduct -> {

                // Create attributes with the ID of the saved product
                List<ProductAttribute> attributes = new ArrayList<>();
                if (dto.getAttributes().isPresent()) {
                    for (ProductAttributeDTO createAttributeDTO : dto.getAttributes().get()) {
                        ProductAttribute attribute = new ProductAttribute();
                        attribute.setProductId(savedProduct.getId());
                        attribute.setAttributeName(createAttributeDTO.getAttributeName());
                        attribute.setAttributeValue(createAttributeDTO.getAttributeValue());
                        attributes.add(attribute);
                    }
                }

                // Create images with the ID of the saved product
                List<ProductImage> images = new ArrayList<>();
                if (dto.getImages().isPresent()) {
                    for (ProductCreateImageDTO createImageDTO : dto.getImages().get()) {
                        ProductImage image = new ProductImage();
                        image.setUrlImg(createImageDTO.getUrlImg());
                        image.setPriority(createImageDTO.getPriority());
                        image.setProductId(savedProduct.getId());
                        images.add(image);
                    }
                }

                // Create price with the ID of the saved product
                ProductPrice price = new ProductPrice();
                price.setPrice(dto.getPrice().getPrice());
                price.setPriceCurrency(dto.getPrice().getPriceCurrency());
                price.setStartDate(LocalDateTime.now());
                price.setCreationAt(LocalDateTime.now());
                price.setProductId(savedProduct.getId());
                price.setIsActive(true);
                price.setEndDate(null);

                // Save attributes, images, and price
                return Flux.fromIterable(attributes).flatMap(productAttributeRepository::save).collectList()
                    .thenMany(Flux.fromIterable(images).flatMap(productImageRepository::save).collectList())
                    .then(productPriceRepository.save(price))
                    .then(findProductWithAdminDetailsById(savedProduct.getId()));
            });
    }

    public Mono<ProductDetailDTO> updateProductById(Long id, ProductCreateDTO dto) {
        return productRepository.findById(id)
            .flatMap(toReplace -> productPriceRepository.findByProductIdAndIsActiveTrue(id)
                .flatMap(oldPrice -> {
                    oldPrice.setIsActive(false);
                    oldPrice.setEndDate(LocalDateTime.now());
                    return productPriceRepository.save(oldPrice);
                })
                .then(
                    productAttributeRepository.deleteAllByProductId(id)
                        .then(productImageRepository.deleteAllByProductId(id))
                )
                .then(Mono.defer(() -> {
                    List<ProductAttribute> attributes = new ArrayList<>();
                    if (dto.getAttributes().isPresent()) {
                        for (ProductAttributeDTO createAttributeDTO : dto.getAttributes().get()) {
                            ProductAttribute attribute = new ProductAttribute();
                            attribute.setProductId(id);
                            attribute.setAttributeName(createAttributeDTO.getAttributeName());
                            attribute.setAttributeValue(createAttributeDTO.getAttributeValue());
                            attributes.add(attribute);
                        }
                    }

                    List<ProductImage> images = new ArrayList<>();
                    if (dto.getImages().isPresent()) {
                        for (ProductCreateImageDTO createImageDTO : dto.getImages().get()) {
                            ProductImage image = new ProductImage();
                            image.setUrlImg(createImageDTO.getUrlImg());
                            image.setPriority(createImageDTO.getPriority());
                            image.setProductId(id);
                            images.add(image);
                        }
                    }

                    ProductPrice price = new ProductPrice();
                    price.setPrice(dto.getPrice().getPrice());
                    price.setPriceCurrency(dto.getPrice().getPriceCurrency());
                    price.setStartDate(LocalDateTime.now());
                    price.setCreationAt(LocalDateTime.now());
                    price.setProductId(id);
                    price.setIsActive(true);
                    price.setEndDate(null);

                    return Flux.fromIterable(attributes).flatMap(productAttributeRepository::save).collectList()
                        .thenMany(Flux.fromIterable(images).flatMap(productImageRepository::save).collectList())
                        .then(productPriceRepository.save(price))
                        .then(findProductWithAdminDetailsById(id));
                }))
            );
    }
}
