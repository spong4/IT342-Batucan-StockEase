package edu.cit.batucan.StockEase.feature.sales;

import edu.cit.batucan.StockEase.feature.sales.dto.SaleRequest;
import edu.cit.batucan.StockEase.feature.sales.dto.SaleResponse;
import edu.cit.batucan.StockEase.shared.dto.ApiResponse;
import edu.cit.batucan.StockEase.shared.exception.InsufficientStockException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> recordSale(@Valid @RequestBody SaleRequest request) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
            SaleResponse sale = saleService.recordSale(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(sale));
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("BUSINESS-001", "Insufficient stock", 
                            "Product: " + e.getProductName() + ", Available: " + e.getAvailable() + 
                            ", Requested: " + e.getRequested()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALID-001", "Validation failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Pageable pageable = PageRequest.of(page, size);
            Page<SaleResponse> sales = saleService.getSalesByUser(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(sales));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getSaleById(@PathVariable Long id) {
        try {
            SaleResponse sale = saleService.getSaleById(id);
            return ResponseEntity.ok(ApiResponse.success(sale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("VALID-001", "Sale not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }
}
