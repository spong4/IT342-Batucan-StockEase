package edu.cit.batucan.StockEase.exception;

public class InsufficientStockException extends RuntimeException {
    private String productName;
    private Integer available;
    private Integer requested;

    public InsufficientStockException(String productName, Integer available, Integer requested) {
        super(String.format("Product '%s': only %d available, but %d requested", 
            productName, available, requested));
        this.productName = productName;
        this.available = available;
        this.requested = requested;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getAvailable() {
        return available;
    }

    public Integer getRequested() {
        return requested;
    }
}
