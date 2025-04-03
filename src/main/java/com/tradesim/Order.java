package com.tradesim;

public class Order {
    private String symbol;
    private int quantity;
    private double price;
    private OrderType type;
    private long timestamp;

    public enum OrderType{
        BUY,
        SELL
    }

    public Order(String symbol, int quantity, double price, OrderType type){
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public OrderType getType() { return type; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString(){
        return type + " " + quantity + " shares of " + symbol + " @ $" + price;
    }
}
