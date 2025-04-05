package com.tradesim;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArrayList;
import java.time.LocalDateTime;

@Component
public class TradingBot {

    private final List<PortfolioSnapshot> performanceHistory = new CopyOnWriteArrayList<>();
    private final Portfolio portfolio = new Portfolio(1000.0);
    private final MarketDataService marketDataService;
    private final TradeEngine tradeEngine;
    private final TradingStrategy strategy = new VolatilityHunterBot();
    private final List<String> watchlist = Arrays.asList("AAPL", "MSFT", "TSLA", "AMZN", "GOOG");

    public TradingBot(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
        this.tradeEngine = new TradeEngine(marketDataService, portfolio);
        portfolio.processOrder(new Order("AAPL", 1, 200.00, Order.OrderType.BUY));
    }

    @PostConstruct
    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            // if (!isMarketOpen()) return;

            for (String symbol : watchlist) {
                try {
                    double price = marketDataService.getCurrentPrice(symbol);
                    strategy.evaluate(symbol, price, tradeEngine);
                } catch (Exception e) {
                    System.err.println("Error for " + symbol + ": " + e.getMessage());
                }
            }

            // ✅ Create + record performance snapshot
            double portfolioValue = portfolio.getCashBalance();
            for (var entry : portfolio.getHoldings().entrySet()) {
                String symbol = entry.getKey();
                int quantity = entry.getValue();
                try {
                    double price = marketDataService.getCurrentPrice(symbol);
                    portfolioValue += price * quantity;
                } catch (Exception e) {
                    // skip errors silently
                }
            }

            PortfolioSnapshot snapshot = new PortfolioSnapshot(LocalDateTime.now(), portfolioValue);
            performanceHistory.add(snapshot);
            saveSnapshotToFile(snapshot); // <-- ✅ This is the missing line!

        }, 0, 60, TimeUnit.SECONDS);
    }


    public Portfolio getPortfolio() {
        return portfolio;
    }

    public MarketDataService getMarketDataService() {
        return this.marketDataService;
    }

    private boolean isMarketOpen() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        LocalTime currentTime = now.toLocalTime();
        LocalTime marketOpen = LocalTime.of(9, 45); // 9:45 AM
        LocalTime marketClose = LocalTime.of(16, 0); // 4:00 PM
        return !currentTime.isBefore(marketOpen) && !currentTime.isAfter(marketClose);
    }

    public List<PortfolioSnapshot> getPerformanceHistory() {
        return performanceHistory;
    }

    private void saveSnapshotToFile(PortfolioSnapshot snapshot) {
        try (FileWriter writer = new FileWriter("performance.csv", true)) {
            writer.write(snapshot.getTimestamp() + "," + snapshot.getPortfolioValue() + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write performance snapshot: " + e.getMessage());
        }
    }
}