package com.tradesim;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TradingBot {

    private final Portfolio portfolio = new Portfolio(1000.0);
    private final MarketDataService marketDataService;
    private final TradeEngine tradeEngine;
    private final TradingStrategy strategy = new VolatilityHunterBot();
    private final List<String> watchlist = Arrays.asList("AAPL", "MSFT", "TSLA", "AMZN", "GOOG");

    public TradingBot(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
        this.tradeEngine = new TradeEngine(marketDataService, portfolio);
    }

    @PostConstruct
    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            for (String symbol : watchlist) {
                try {
                    double price = marketDataService.getCurrentPrice(symbol);
                    strategy.evaluate(symbol, price, tradeEngine);
                } catch (Exception e) {
                    System.err.println("⚠️ Error for " + symbol + ": " + e.getMessage());
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public MarketDataService getMarketDataService() {
        return this.marketDataService;
    }
}