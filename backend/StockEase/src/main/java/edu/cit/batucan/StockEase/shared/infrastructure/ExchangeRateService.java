package edu.cit.batucan.StockEase.shared.infrastructure;

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
    private static final long CACHE_DURATION = 30 * 60 * 1000;

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if ("USD".equals(fromCurrency) && "USD".equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            String cacheKey = fromCurrency + "_" + toCurrency;
            
            if (System.currentTimeMillis() - lastCacheTime < CACHE_DURATION && rateCache.containsKey(cacheKey)) {
                return rateCache.get(cacheKey);
            }

            BigDecimal rate = BigDecimal.ONE;
            
            rateCache.put(cacheKey, rate);
            lastCacheTime = System.currentTimeMillis();
            
            return rate;
        } catch (Exception e) {
            System.err.println("Error fetching exchange rate: " + e.getMessage());
            return BigDecimal.ONE;
        }
    }
}
