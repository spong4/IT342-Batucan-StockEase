package edu.cit.batucan.StockEase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    private String category;
}
