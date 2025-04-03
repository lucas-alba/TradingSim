package com.tradesim;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class App {
    public static void main(String[] args) {
        MarketDataService marketData = new MarketDataService();
        Portfolio portfolio = new Portfolio(1000.0);
        TradeEngine tradeEngine = new TradeEngine(marketData, portfolio);
        TradingStrategy strategy = new VolatilityHunterBot();

        List<String> watchlist = Arrays.asList("AAPL", "MSFT", "TSLA", "AMZN", "GOOG");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            for (String symbol : watchlist) {
                try {
                    double price = marketData.getCurrentPrice(symbol);
                    strategy.evaluate(symbol, price, tradeEngine);
                } catch (Exception e) {
                    System.err.println("⚠️ Error for " + symbol + ": " + e.getMessage());
                }
            }
        }, 0, 60, TimeUnit.SECONDS); // every minute

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n--- Final Portfolio ---");
            System.out.println(portfolio);
            for (Order o : portfolio.getTradeHistory()) {
                System.out.println(o);
            }
        }));
    }
}










/*public class App {
    public static void main(String[] args) {
        // Sample historical prices (can replace with real CSV later)
        List<Double> prices = Arrays.asList(
                150.0, 149.0, 148.0, 145.0, 142.0,     // big dip
                146.0, 150.0, 154.0, 157.0, 160.0,     // strong surge
                158.0, 155.0, 153.0, 150.0, 147.0,     // pullback
                149.0, 152.0, 154.0, 157.0, 159.0,     // another climb
                161.0, 164.0, 167.0, 170.0, 165.0,     // extended peak
                160.0, 155.0, 150.0, 145.0, 140.0,     // full drop
                138.0, 135.0, 133.0, 136.0, 139.0,     // recovery begins
                143.0, 146.0, 149.0, 152.0, 155.0,     // sustained rise
                153.0, 151.0, 148.0, 146.0, 144.0,     // short dip
                147.0, 150.0, 154.0, 158.0, 162.0      // breakout
        );



        MarketDataService marketData = new MarketDataService(); // Not used in backtest
        Portfolio portfolio = new Portfolio(1000.0);
        TradeEngine engine = new TradeEngine(marketData, portfolio);
        TradingStrategy strategy = new VolatilityHunterBot();

        Backtester backtester = new Backtester(strategy, engine, portfolio);
        backtester.runBacktest(prices, "AAPL");
    }
}
*/
