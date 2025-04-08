package com.tradesim.controller;

import com.tradesim.Order;
import com.tradesim.Portfolio;
import com.tradesim.PortfolioSnapshot;
import com.tradesim.TradingBot;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.io.IOException;


@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final TradingBot tradingBot;

    public PortfolioController(TradingBot tradingBot) {
        this.tradingBot = tradingBot;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Portfolio portfolio = tradingBot.getPortfolio();
        return Map.of(
                "cashBalance", portfolio.getCashBalance(),
                "holdings", portfolio.getHoldings()
        );
    }

    @GetMapping("/status")
    public String getStatus() {
        Portfolio portfolio = tradingBot.getPortfolio();
        double cash = portfolio.getCashBalance();
        double totalValue = cash;

        for (Map.Entry<String, Integer> entry : portfolio.getHoldings().entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            try {
                double price = tradingBot.getMarketDataService().getCurrentPrice(symbol);
                totalValue += price * quantity;
            } catch (IOException e) {
                return "Error fetching price for " + symbol;
            }
        }

        double profitLoss = totalValue - 1000.0;
        if (profitLoss > 0) return "Portfolio is up $" + String.format("%.2f", profitLoss);
        if (profitLoss < 0) return "Portfolio is down $" + String.format("%.2f", -profitLoss);
        return "Portfolio is even";
    }


    @GetMapping("/trades")
    public List<Order> getTradeHistory() {
        return tradingBot.getPortfolio().getTradeHistory();
    }

    @GetMapping("/performance")
    public List<PortfolioSnapshot> getPerformance() {
        return tradingBot.getPerformanceHistory().stream()
                .filter(snapshot -> {
                    ZonedDateTime timestamp = snapshot.getTimestamp().atZone(ZoneId.of("America/New_York"));
                    LocalTime time = timestamp.toLocalTime();
                    int dayOfWeek = timestamp.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

                    boolean isWeekday = dayOfWeek >= 1 && dayOfWeek <= 5;
                    boolean isDuringMarketHours = !time.isBefore(LocalTime.of(13, 30)) && !time.isAfter(LocalTime.of(20, 0));

                    return isWeekday && isDuringMarketHours;
                })
                .toList();
    }
}
