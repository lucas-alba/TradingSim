package com.tradesim;


public class TradeEngine {
    private final MarketDataService marketData;
    private final Portfolio portfolio;

    public TradeEngine(MarketDataService marketData, Portfolio portfolio){
        this.marketData = marketData;
        this.portfolio = portfolio;
    }

    public boolean execute(String symbol, int quantity, Order.OrderType type){
        try {
            double curPrice = marketData.getCurrentPrice(symbol);
            Order order = new Order(symbol,quantity,curPrice,type);
            boolean success = portfolio.processOrder(order);
            if (success){
                System.out.println("Executed: " + order);
            }
            else{
                System.out.println("Order failed: " + order);
            }
            return success;


        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            return false;
        }
    }
    public boolean executeWithPrice(String symbol, int quantity, double price, Order.OrderType type) {
        Order order = new Order(symbol, quantity, price, type);
        boolean success = portfolio.processOrder(order);
        if (success) {
            System.out.println("Executed (Backtest): " + order);
        } else {
            System.out.println("Order failed (Backtest): " + order);
        }
        return success;
    }

}
