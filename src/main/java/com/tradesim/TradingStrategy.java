package com.tradesim;

public interface TradingStrategy {
    void evaluate(String symbol, double price, TradeEngine engine);
}