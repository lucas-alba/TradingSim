package com.tradesim;

import java.util.List;

public class Backtester {
    private final TradingStrategy strategy;
    private final TradeEngine engine;
    private final Portfolio portfolio;

    public Backtester(TradingStrategy strategy, TradeEngine engine, Portfolio portfolio) {
        this.strategy = strategy;
        this.engine = engine;
        this.portfolio = portfolio;
    }

    public void runBacktest(List<Double> prices, String symbol) {
        for (double price : prices) {
            // Call the strategy with historical price data
            strategy.evaluate(symbol, price, engine);
        }

        System.out.println("\n--- Backtest Portfolio Snapshot ---");
        System.out.println(portfolio);

        System.out.println("\n--- Backtest Trade History ---");
        for (Order order : portfolio.getTradeHistory()) {
            System.out.println(order);
        }
    }
}
