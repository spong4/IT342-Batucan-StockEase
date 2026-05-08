package edu.cit.batucan.StockEase.service;

import edu.cit.batucan.StockEase.dto.ProductDto;
import edu.cit.batucan.StockEase.dto.ProductRequest;
import edu.cit.batucan.StockEase.entity.Product;
import edu.cit.batucan.StockEase.entity.User;
import edu.cit.batucan.StockEase.repository.ProductRepository;
import edu.cit.batucan.StockEase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@SuppressWarnings("null")
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<ProductDto> getAllProducts(Pageable pageable, String category) {
        Page<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productRepository.findByIsActiveTrueAndCategory(category, pageable);
        } else {
            products = productRepository.findByIsActiveTrue(pageable);
        }
        return products.map(this::convertToDto);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return convertToDto(product);
    }

    @Transactional
    public ProductDto createProduct(ProductRequest request, Long userId) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .createdBy(user)
                .isActive(true)
                .build();

        product = productRepository.save(product);
        return convertToDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductRequest request, Long userId) {
        Product product = productRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own products");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());

        product = productRepository.save(product);
        return convertToDto(product);
    }

    @Transactional
    public void deleteProduct(Long id, Long userId) {
        Product product = productRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own products");
        }

        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional
    public ProductDto updateProductImage(Long id, String imageUrl, Long userId) {
        Product product = productRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own products");
        }

        product.setImageUrl(imageUrl);
        product = productRepository.save(product);
        return convertToDto(product);
    }

    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .createdById(product.getCreatedBy().getId())
                .build();
    }
}
