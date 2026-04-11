package edu.cit.batucan.StockEase.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Column
    private String currencyCode;
    
    @Column
    private BigDecimal exchangeRate;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean receiptEmailSent;
    
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SaleItem> items;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (receiptEmailSent == null) {
            receiptEmailSent = false;
        }
        if (currencyCode == null) {
            currencyCode = "USD";
        }
    }
}
