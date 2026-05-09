package edu.cit.batucan.StockEase.feature.product;

import edu.cit.batucan.StockEase.feature.auth.model.User;
import edu.cit.batucan.StockEase.feature.auth.model.UserRole;
import edu.cit.batucan.StockEase.feature.auth.repository.UserRepository;
import edu.cit.batucan.StockEase.feature.product.dto.ProductDto;
import edu.cit.batucan.StockEase.feature.product.dto.ProductRequest;
import edu.cit.batucan.StockEase.feature.product.model.Product;
import edu.cit.batucan.StockEase.feature.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private ProductService productService;

    private User ownerUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        ownerUser = User.builder()
                .id(1L)
                .email("owner@example.com")
                .firstname("John")
                .lastname("Doe")
                .role(UserRole.OWNER)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("A test product")
                .price(new BigDecimal("99.99"))
                .quantity(50)
                .category("Electronics")
                .isActive(true)
                .createdBy(ownerUser)
                .build();
    }

    @Test
    void testGetAllProducts_ReturnsPage() {
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productRepository.findByIsActiveTrue(any())).thenReturn(productPage);

        Page<ProductDto> result = productService.getAllProducts(PageRequest.of(0, 10), null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
    }

    @Test
    void testGetAllProducts_WithCategoryFilter() {
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productRepository.findByIsActiveTrueAndCategory(eq("Electronics"), any()))
                .thenReturn(productPage);

        Page<ProductDto> result = productService.getAllProducts(PageRequest.of(0, 10), "Electronics");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByIsActiveTrueAndCategory(eq("Electronics"), any());
    }

    @Test
    void testGetProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(new BigDecimal("99.99"), result.getPrice());
        assertEquals(50, result.getQuantity());
    }

    @Test
    void testGetProductById_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.getProductById(99L));
    }

    @Test
    void testCreateProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setDescription("Brand new product");
        request.setPrice(new BigDecimal("149.99"));
        request.setQuantity(100);
        request.setCategory("Clothing");

        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p = Product.builder().id(2L).name(p.getName()).description(p.getDescription())
                    .price(p.getPrice()).quantity(p.getQuantity()).category(p.getCategory())
                    .isActive(true).createdBy(ownerUser).build();
            return p;
        });

        ProductDto result = productService.createProduct(request, 1L);

        assertNotNull(result);
        assertEquals("New Product", result.getName());
        assertEquals(new BigDecimal("149.99"), result.getPrice());
    }

    @Test
    void testUpdateProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setDescription("Updated description");
        request.setPrice(new BigDecimal("79.99"));
        request.setQuantity(25);
        request.setCategory("Food");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDto result = productService.updateProduct(1L, request, 1L);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.deleteProduct(1L, 1L);

        verify(productRepository).save(argThat(p -> !p.getIsActive()));
    }

    @Test
    void testDeleteProduct_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(99L, 1L));
    }
}
