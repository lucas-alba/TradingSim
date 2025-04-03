package com.tradesim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Portfolio {
    private double cashBalance;
    private Map<String, Integer> holdings;
    private List<Order> tradeHistory;

    public Portfolio(double initialCash){
        this.cashBalance = initialCash;
        this.holdings = new HashMap<>();
        this.tradeHistory = new ArrayList<>();
    }

    public boolean processOrder(Order order){
        double totalCash = order.getPrice() * order.getQuantity();

        if(order.getType() == Order.OrderType.BUY){ //BUY THE STOCK
            if(cashBalance < totalCash){
                System.out.println("Not enough cash to buy.");
                return false;
            }
            holdings.put(order.getSymbol(), holdings.getOrDefault(order.getSymbol(), 0) + order.getQuantity());
            cashBalance -= totalCash;
        }
        else { // SELL THE STOCK
            int currShares = holdings.getOrDefault(order.getSymbol(), 0);
            if(currShares < order.getQuantity()){
                System.out.println("Not enough shares to sell.");
                return false;
            }
            holdings.put(order.getSymbol(),holdings.get(order.getSymbol()) - order.getQuantity());
            cashBalance += totalCash;
        }
        tradeHistory.add(order);
        return true;
    }

    public double getCashBalance() {
        return cashBalance;
    }
    public Map<String, Integer> getHoldings() {
        return holdings;
    }
    public List<Order> getTradeHistory() {
        return tradeHistory;
    }

    @Override
    public String toString(){
        return "Cash: $" + String.format("%.2f", cashBalance) + "\nHoldings: " + holdings.toString();
    }
}
