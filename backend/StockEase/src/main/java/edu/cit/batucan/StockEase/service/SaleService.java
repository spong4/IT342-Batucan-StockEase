package edu.cit.batucan.StockEase.service;

import edu.cit.batucan.StockEase.dto.SaleItemDto;
import edu.cit.batucan.StockEase.dto.SaleItemRequest;
import edu.cit.batucan.StockEase.dto.SaleRequest;
import edu.cit.batucan.StockEase.dto.SaleResponse;
import edu.cit.batucan.StockEase.entity.Product;
import edu.cit.batucan.StockEase.entity.Sale;
import edu.cit.batucan.StockEase.entity.SaleItem;
import edu.cit.batucan.StockEase.entity.User;
import edu.cit.batucan.StockEase.exception.InsufficientStockException;
import edu.cit.batucan.StockEase.repository.ProductRepository;
import edu.cit.batucan.StockEase.repository.SaleItemRepository;
import edu.cit.batucan.StockEase.repository.SaleRepository;
import edu.cit.batucan.StockEase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Transactional
    public SaleResponse recordSale(SaleRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Sale must contain at least one item");
        }

        // Validate stock availability for all items first
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));

            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(product.getName(), product.getQuantity(), itemRequest.getQuantity());
            }
        }

        // Calculate total and create sale items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();

        Sale sale = Sale.builder()
                .recordedBy(user)
                .totalAmount(BigDecimal.ZERO) // Will be updated
                .currencyCode("USD")
                .exchangeRate(exchangeRateService.getExchangeRate("USD", "USD"))
                .receiptEmailSent(false)
                .build();

        sale = saleRepository.save(sale);

        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).get();

            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            SaleItem saleItem = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            saleItems.add(saleItemRepository.save(saleItem));

            // Deduct stock
            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        // Update sale with total amount
        sale.setTotalAmount(totalAmount);
        sale = saleRepository.save(sale);

        // Send receipt email
        try {
            sendReceiptEmail(user.getEmail(), sale);
            sale.setReceiptEmailSent(true);
            sale = saleRepository.save(sale);
        } catch (Exception e) {
            System.err.println("Failed to send receipt email: " + e.getMessage());
        }

        // Build response
        List<SaleItemDto> saleItemDtos = new ArrayList<>();
        for (SaleItem item : saleItems) {
            saleItemDtos.add(SaleItemDto.builder()
                    .id(item.getId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .build());
        }

        return SaleResponse.builder()
                .saleId(sale.getId())
                .totalAmount(sale.getTotalAmount())
                .currencyCode(sale.getCurrencyCode())
                .exchangeRate(sale.getExchangeRate())
                .items(saleItemDtos)
                .createdAt(sale.getCreatedAt())
                .receiptEmailSent(sale.getReceiptEmailSent())
                .build();
    }

    public Page<SaleResponse> getSalesByUser(Long userId, Pageable pageable) {
        return saleRepository.findByRecordedById(userId, pageable)
                .map(this::convertToResponse);
    }

    public SaleResponse getSaleById(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found"));
        return convertToResponse(sale);
    }

    private SaleResponse convertToResponse(Sale sale) {
        List<SaleItemDto> items = new ArrayList<>();
        for (SaleItem item : sale.getItems()) {
            items.add(SaleItemDto.builder()
                    .id(item.getId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .build());
        }

        return SaleResponse.builder()
                .saleId(sale.getId())
                .totalAmount(sale.getTotalAmount())
                .currencyCode(sale.getCurrencyCode())
                .exchangeRate(sale.getExchangeRate())
                .items(items)
                .createdAt(sale.getCreatedAt())
                .receiptEmailSent(sale.getReceiptEmailSent())
                .build();
    }

    private void sendReceiptEmail(String email, Sale sale) {
        StringBuilder body = new StringBuilder();
        body.append("Thank you for your purchase!\n\n");
        body.append("Sale ID: ").append(sale.getId()).append("\n");
        body.append("Date: ").append(sale.getCreatedAt()).append("\n");
        body.append("Total Amount: $").append(sale.getTotalAmount()).append("\n");
        body.append("Currency: ").append(sale.getCurrencyCode()).append("\n");
        body.append("Exchange Rate: ").append(sale.getExchangeRate()).append("\n\n");
        body.append("Items:\n");

        if (sale.getItems() != null) {
            for (SaleItem item : sale.getItems()) {
                body.append("- ").append(item.getProductName())
                        .append(" (Qty: ").append(item.getQuantity())
                        .append(", Price: $").append(item.getUnitPrice())
                        .append(")\n");
            }
        }

        body.append("\nBest regards,\nStockEase Team");

        emailService.sendReceiptEmail(email, "Sale Receipt - Order #" + sale.getId(), body.toString());
    }
}
