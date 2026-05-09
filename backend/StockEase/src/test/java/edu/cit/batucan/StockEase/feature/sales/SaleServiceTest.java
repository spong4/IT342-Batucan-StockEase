package edu.cit.batucan.StockEase.feature.sales;

import edu.cit.batucan.StockEase.feature.auth.model.User;
import edu.cit.batucan.StockEase.feature.auth.model.UserRole;
import edu.cit.batucan.StockEase.feature.auth.repository.UserRepository;
import edu.cit.batucan.StockEase.feature.product.model.Product;
import edu.cit.batucan.StockEase.feature.product.repository.ProductRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @Mock private SaleItemRepository saleItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;
    @Mock private ExchangeRateService exchangeRateService;

    @InjectMocks
    private SaleService saleService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).email("user@example.com")
                .firstname("John").lastname("Doe")
                .role(UserRole.OWNER).build();

        testProduct = Product.builder()
                .id(1L).name("Test Product")
                .price(new BigDecimal("50.00"))
                .quantity(100).isActive(true)
                .build();
    }

    @Test
    void testRecordSale_Success() {
        SaleItemRequest itemRequest = new SaleItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        SaleRequest request = new SaleRequest();
        request.setItems(List.of(itemRequest));

        Sale savedSale = Sale.builder()
                .id(1L).recordedBy(testUser)
                .totalAmount(new BigDecimal("100.00"))
                .currencyCode("USD").exchangeRate(new BigDecimal("1.0"))
                .receiptEmailSent(false)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now()).build();

        SaleItem savedItem = SaleItem.builder()
                .id(1L).sale(savedSale).product(testProduct)
                .productName("Test Product").quantity(2)
                .unitPrice(new BigDecimal("50.00"))
                .subtotal(new BigDecimal("100.00")).build();

        savedSale.getItems().add(savedItem);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(exchangeRateService.getExchangeRate(any(), any())).thenReturn(new BigDecimal("1.0"));
        when(saleRepository.save(any(Sale.class))).thenReturn(savedSale);
        when(saleItemRepository.save(any(SaleItem.class))).thenReturn(savedItem);
        doNothing().when(emailService).sendReceiptEmail(anyString(), anyString(), anyString());

        SaleResponse response = saleService.recordSale(request, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getSaleId());
        verify(productRepository, atLeast(1)).save(any(Product.class));
    }

    @Test
    void testRecordSale_InsufficientStock_ThrowsException() {
        testProduct.setQuantity(1); // Only 1 in stock

        SaleItemRequest itemRequest = new SaleItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(5); // Requesting 5

        SaleRequest request = new SaleRequest();
        request.setItems(List.of(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientStockException.class, () -> saleService.recordSale(request, 1L));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void testRecordSale_EmptyItems_ThrowsException() {
        SaleRequest request = new SaleRequest();
        request.setItems(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> saleService.recordSale(request, 1L));
    }

    @Test
    void testRecordSale_UserNotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        SaleRequest request = new SaleRequest();
        request.setItems(List.of(new SaleItemRequest()));

        assertThrows(Exception.class, () -> saleService.recordSale(request, 99L));
    }

    @Test
    void testGetSaleById_Found() {
        Sale sale = Sale.builder()
                .id(1L).recordedBy(testUser)
                .totalAmount(new BigDecimal("100.00"))
                .currencyCode("USD").exchangeRate(new BigDecimal("1.0"))
                .receiptEmailSent(true).items(new ArrayList<>())
                .createdAt(LocalDateTime.now()).build();

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        SaleResponse response = saleService.getSaleById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getSaleId());
        assertEquals(new BigDecimal("100.00"), response.getTotalAmount());
    }

    @Test
    void testGetSaleById_NotFound_ThrowsException() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> saleService.getSaleById(99L));
    }
}
