package com.tradesim.controller;

import com.tradesim.MarketDataService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/market")
public class MarketDataController {

    private final MarketDataService marketDataService = new MarketDataService();
    private final List<String> watchlist = Arrays.asList("AAPL", "MSFT", "TSLA", "AMZN", "GOOG");

    @GetMapping("/prices")
    public List<Map<String, Object>> getAllPrices() {
        List<Map<String, Object>> prices = new ArrayList<>();

        for (String symbol : watchlist) {
            Map<String, Object> stockInfo = new HashMap<>();
            try {
                double price = marketDataService.getCurrentPrice(symbol);
                stockInfo.put("symbol", symbol);
                stockInfo.put("price", price);
            } catch (IOException e) {
                stockInfo.put("symbol", symbol);
                stockInfo.put("error", e.getMessage());
            }
            prices.add(stockInfo);
        }

        return prices;
    }
}
