package com.tradesim;
import java.util.*;

public class VolatilityHunterBot implements TradingStrategy {

    private final Map<String, Queue<Double>> recentPricesMap = new HashMap<>();
    private final Map<String, Double> lastBuyPriceMap = new HashMap<>();
    private final Map<String, Double> peakSinceBuyMap = new HashMap<>();
    private final Map<String, Double> previousPriceMap = new HashMap<>();
    private final Map<String, Integer> momentumCounterMap = new HashMap<>();

    private final int windowSize = 5;

    @Override
    public void evaluate(String symbol, double price, TradeEngine engine) {
        // Track price history
        Queue<Double> recentPrices = recentPricesMap.computeIfAbsent(symbol, k -> new LinkedList<>());
        recentPrices.add(price);
        if (recentPrices.size() > windowSize) {
            recentPrices.poll();
        }

        double sum = 0.0;
        for (double p : recentPrices) sum += p;
        double avg = sum / recentPrices.size();

        double lastBuyPrice = lastBuyPriceMap.getOrDefault(symbol, -1.0);
        double peakSinceBuy = peakSinceBuyMap.getOrDefault(symbol, -1.0);
        double previousPrice = previousPriceMap.getOrDefault(symbol, price); // first tick fallback

        // Momentum tracking
        int momentum = momentumCounterMap.getOrDefault(symbol, 0);
        if (price > previousPrice) {
            momentum++;
        } else {
            momentum = 0;
        }
        momentumCounterMap.put(symbol, momentum);
        previousPriceMap.put(symbol, price);

        boolean bought = false;

        // Dip-buy logic
        if (lastBuyPrice == -1 && price < avg * 0.97) {
            bought = engine.execute(symbol, 1, Order.OrderType.BUY);
        }

        // Momentum-buy logic
        if (!bought && lastBuyPrice == -1 && price > avg && momentum >= 3) {
            bought = engine.execute(symbol, 1, Order.OrderType.BUY);
        }

        //TEST LOGIC FOR WHEN THE MARKET IS CLOSED
        /*if (!bought && lastBuyPrice == -1) {
            bought = engine.execute(symbol, 1, Order.OrderType.BUY);
        }*/

        if (bought) {
            lastBuyPriceMap.put(symbol, price);
            peakSinceBuyMap.put(symbol, price);
            System.out.println("Bought " + symbol + " at $" + price + " | Momentum: " + momentum);
        }

        // Sell conditions
        if (lastBuyPrice != -1) {
            if (price > peakSinceBuy) {
                peakSinceBuyMap.put(symbol, price);
            }

            if (price < peakSinceBuy * 0.95) {
                boolean sold = engine.execute(symbol, 1, Order.OrderType.SELL);
                if (sold) {
                    System.out.println("Sold " + symbol + " at $" + price);
                    lastBuyPriceMap.remove(symbol);
                    peakSinceBuyMap.remove(symbol);
                }
            }
        }
        if (lastBuyPrice != -1 && price > lastBuyPrice * 1.03 && price < previousPrice) {
            boolean sold = engine.execute(symbol, 1, Order.OrderType.SELL);
            if (sold) {
                System.out.println("🎯 Took profit on " + symbol + " at $" + price);
                lastBuyPriceMap.remove(symbol);
                peakSinceBuyMap.remove(symbol);
            }
        }


        System.out.println(symbol + " | Price: $" + price + " | Avg: $" + String.format("%.2f", avg) + " | Momentum: " + momentum);
    }
}
