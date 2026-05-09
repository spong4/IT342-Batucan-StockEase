package edu.cit.batucan.StockEase.feature.product;

import edu.cit.batucan.StockEase.feature.product.dto.ProductDto;
import edu.cit.batucan.StockEase.feature.product.dto.ProductRequest;
import edu.cit.batucan.StockEase.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDto> products = productService.getAllProducts(pageable, category);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getProductById(@PathVariable Long id) {
        try {
            ProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("VALID-001", "Product not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
            ProductDto product = productService.createProduct(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALID-001", "Validation failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
            ProductDto product = productService.updateProduct(id, request, userId);
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALID-001", "Validation failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
            productService.deleteProduct(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALID-001", "Validation failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<ApiResponse<?>> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
            String imageUrl = fileUploadService.uploadProductImage(file);
            ProductDto product = productService.updateProductImage(id, imageUrl, userId);
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALID-001", "Invalid file", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }
}
