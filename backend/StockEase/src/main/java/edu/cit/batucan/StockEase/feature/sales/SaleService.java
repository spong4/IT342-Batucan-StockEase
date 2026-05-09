package edu.cit.batucan.StockEase.feature.sales;

import edu.cit.batucan.StockEase.feature.auth.model.User;
import edu.cit.batucan.StockEase.feature.auth.repository.UserRepository;
import edu.cit.batucan.StockEase.feature.product.model.Product;
import edu.cit.batucan.StockEase.feature.product.repository.ProductRepository;
import edu.cit.batucan.StockEase.feature.sales.dto.SaleItemDto;
import edu.cit.batucan.StockEase.feature.sales.dto.SaleItemRequest;
import edu.cit.batucan.StockEase.feature.sales.dto.SaleRequest;
import edu.cit.batucan.StockEase.feature.sales.dto.SaleResponse;
import edu.cit.batucan.StockEase.feature.sales.model.Sale;
import edu.cit.batucan.StockEase.feature.sales.model.SaleItem;
import edu.cit.batucan.StockEase.feature.sales.repository.SaleItemRepository;
import edu.cit.batucan.StockEase.feature.sales.repository.SaleRepository;
import edu.cit.batucan.StockEase.shared.exception.InsufficientStockException;
import edu.cit.batucan.StockEase.shared.infrastructure.EmailService;
import edu.cit.batucan.StockEase.shared.infrastructure.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@SuppressWarnings("null")
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
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Sale must contain at least one item");
        }

        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(Objects.requireNonNull(itemRequest.getProductId()))
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));

            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(product.getName(), product.getQuantity(), itemRequest.getQuantity());
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();

        Sale sale = Sale.builder()
                .recordedBy(user)
                .totalAmount(BigDecimal.ZERO)
                .currencyCode("USD")
                .exchangeRate(exchangeRateService.getExchangeRate("USD", "USD"))
                .receiptEmailSent(false)
                .build();

        sale = saleRepository.save(sale);

        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(Objects.requireNonNull(itemRequest.getProductId())).get();

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

            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        sale.setTotalAmount(totalAmount);
        sale = saleRepository.save(sale);

        try {
            sendReceiptEmail(user.getEmail(), sale);
            sale.setReceiptEmailSent(true);
            sale = saleRepository.save(sale);
        } catch (Exception e) {
            System.err.println("Failed to send receipt email: " + e.getMessage());
        }

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
        Sale sale = saleRepository.findById(Objects.requireNonNull(saleId))
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
        body.append("Currency: ").append(sale.getCurrencyCode()).append("\n\n");
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
