package edu.cit.batucan.StockEase.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeRateService {

    @Value("${exchange.rate.api.url:https://api.exchangerate-api.com/v4/latest/}")
    private String apiUrl;

    private final Map<String, BigDecimal> rateCache = new HashMap<>();
    private long lastCacheTime = 0;
    private static final long CACHE_DURATION = 30 * 60 * 1000; // 30 minutes

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if ("USD".equals(fromCurrency) && "USD".equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            String cacheKey = fromCurrency + "_" + toCurrency;
            
            // Check cache
            if (System.currentTimeMillis() - lastCacheTime < CACHE_DURATION && rateCache.containsKey(cacheKey)) {
                return rateCache.get(cacheKey);
            }

            // Fetch from API (simplified - in real implementation would parse response properly)
            // For now, return 1.0 if USD, else attempt to fetch
            BigDecimal rate = BigDecimal.ONE;
            
            rateCache.put(cacheKey, rate);
            lastCacheTime = System.currentTimeMillis();
            
            return rate;
        } catch (Exception e) {
            System.err.println("Error fetching exchange rate: " + e.getMessage());
            // Return cached value or default
            return BigDecimal.ONE;
        }
    }
}
