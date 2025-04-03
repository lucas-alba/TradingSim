package com.tradesim.controller;

import com.tradesim.Order;
import com.tradesim.Portfolio;
import com.tradesim.TradingBot;
import org.springframework.web.bind.annotation.*;

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
        if (profitLoss > 0) return "📈 Portfolio is up $" + String.format("%.2f", profitLoss);
        if (profitLoss < 0) return "📉 Portfolio is down $" + String.format("%.2f", -profitLoss);
        return "Portfolio is even";
    }


    @GetMapping("/trades")
    public List<Order> getTradeHistory() {
        return tradingBot.getPortfolio().getTradeHistory();
    }
}
